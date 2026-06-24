package cn.iocoder.yudao.module.linbang.service.map;

import cn.hutool.core.util.StrUtil;
import cn.iocoder.yudao.framework.common.util.http.HttpUtils;
import cn.iocoder.yudao.framework.common.util.json.JsonUtils;
import cn.iocoder.yudao.framework.common.util.number.NumberUtils;
import cn.iocoder.yudao.module.linbang.map.config.AmapProperties;
import com.fasterxml.jackson.databind.JsonNode;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.web.util.UriComponentsBuilder;

import javax.annotation.Resource;
import java.math.BigDecimal;
import java.math.RoundingMode;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Objects;

import static cn.iocoder.yudao.framework.common.exception.util.ServiceExceptionUtil.exception;
import static cn.iocoder.yudao.module.linbang.enums.ErrorCodeConstants.MAP_ADDRESS_RESOLVE_FAILED;
import static cn.iocoder.yudao.module.linbang.enums.ErrorCodeConstants.MAP_KEY_NOT_CONFIGURED;
import static cn.iocoder.yudao.module.linbang.enums.ErrorCodeConstants.MAP_LOCATION_INVALID;
import static cn.iocoder.yudao.module.linbang.enums.ErrorCodeConstants.MAP_REQUEST_FAILED;

@Service
@Slf4j
public class AmapLocationService {

    private static final String GEOCODE_PATH = "/v3/geocode/geo";
    private static final String REVERSE_GEOCODE_PATH = "/v3/geocode/regeo";
    private static final String DISTANCE_PATH = "/v3/distance";
    private static final String DISTRICT_PATH = "/v3/config/district";
    private static final String NEARBY_SEARCH_PATH = "/v3/place/around";

    @Resource
    private AmapProperties amapProperties;

    public ResolvedAddress resolveAddress(ResolveAddressRequest request) {
        if (request == null) {
            throw exception(MAP_LOCATION_INVALID);
        }
        requireConfigured();
        ResolvedAddress resolved = ResolvedAddress.builder()
                .province(trimToNull(request.getProvince()))
                .city(trimToNull(request.getCity()))
                .district(trimToNull(request.getDistrict()))
                .street(trimToNull(request.getStreet()))
                .detailAddress(trimToNull(request.getDetailAddress()))
                .longitude(request.getLongitude())
                .latitude(request.getLatitude())
                .adcode(trimToNull(request.getAdcode()))
                .build();

        if (hasCoordinates(resolved.getLongitude(), resolved.getLatitude())) {
            fillByReverseGeocode(resolved, resolved.getLongitude(), resolved.getLatitude());
        } else {
            String fullAddress = buildFullAddress(resolved);
            if (StrUtil.isBlank(fullAddress)) {
                throw exception(MAP_LOCATION_INVALID);
            }
            GeocodeResult geocode = geocode(fullAddress, StrUtil.blankToDefault(resolved.getCity(), resolved.getProvince()));
            if (geocode == null || !hasCoordinates(geocode.getLongitude(), geocode.getLatitude())) {
                throw exception(MAP_ADDRESS_RESOLVE_FAILED, fullAddress);
            }
            resolved.setLongitude(geocode.getLongitude());
            resolved.setLatitude(geocode.getLatitude());
            fillByReverseGeocode(resolved, geocode.getLongitude(), geocode.getLatitude());
        }

        if (!hasCoordinates(resolved.getLongitude(), resolved.getLatitude())) {
            throw exception(MAP_ADDRESS_RESOLVE_FAILED, buildFullAddress(resolved));
        }
        normalizeMunicipality(resolved);
        return resolved;
    }

    public BigDecimal calculateDistanceKm(BigDecimal originLongitude, BigDecimal originLatitude,
                                          BigDecimal destinationLongitude, BigDecimal destinationLatitude) {
        if (!hasCoordinates(originLongitude, originLatitude) || !hasCoordinates(destinationLongitude, destinationLatitude)) {
            return null;
        }
        if (amapProperties.isEnabled() && amapProperties.hasKey()) {
            try {
                BigDecimal amapDistance = requestDistance(originLongitude, originLatitude, destinationLongitude, destinationLatitude);
                if (amapDistance != null) {
                    return amapDistance;
                }
            } catch (Exception ex) {
                log.warn("高德距离计算失败，回退本地距离算法: {}", ex.getMessage());
            }
        }
        double distance = NumberUtils.getDistance(originLatitude.doubleValue(), originLongitude.doubleValue(),
                destinationLatitude.doubleValue(), destinationLongitude.doubleValue());
        return BigDecimal.valueOf(distance).setScale(2, RoundingMode.HALF_UP);
    }

    public DistrictInfo resolveDistrict(String keywords) {
        if (StrUtil.isBlank(keywords)) {
            return null;
        }
        requireConfigured();
        JsonNode root = request(DISTRICT_PATH, UriComponentsBuilder.newInstance()
                .queryParam("keywords", keywords)
                .queryParam("subdistrict", 0)
                .queryParam("extensions", "base"));
        JsonNode districtNode = root.path("districts").isArray() && root.path("districts").size() > 0
                ? root.path("districts").get(0) : null;
        if (districtNode == null || districtNode.isMissingNode()) {
            return null;
        }
        return DistrictInfo.builder()
                .name(trimToNull(JsonUtils.getText(districtNode, "name")))
                .adcode(trimToNull(JsonUtils.getText(districtNode, "adcode")))
                .cityCode(trimToNull(JsonUtils.getText(districtNode, "citycode")))
                .level(trimToNull(JsonUtils.getText(districtNode, "level")))
                .center(trimToNull(JsonUtils.getText(districtNode, "center")))
                .build();
    }

    public List<NearbyPlace> searchNearby(BigDecimal longitude, BigDecimal latitude, String keywords, Integer radiusMeters) {
        if (!hasCoordinates(longitude, latitude)) {
            return Collections.emptyList();
        }
        requireConfigured();
        JsonNode root = request(NEARBY_SEARCH_PATH, UriComponentsBuilder.newInstance()
                .queryParam("location", buildLocation(longitude, latitude))
                .queryParam("keywords", trimToNull(keywords))
                .queryParam("radius", radiusMeters != null ? radiusMeters : 3000)
                .queryParam("sortrule", "distance")
                .queryParam("offset", 20)
                .queryParam("page", 1));
        JsonNode poisNode = root.path("pois");
        if (!poisNode.isArray() || poisNode.isEmpty()) {
            return Collections.emptyList();
        }
        List<NearbyPlace> results = new ArrayList<>(poisNode.size());
        for (JsonNode poiNode : poisNode) {
            JsonNode locationNode = poiNode.path("location");
            BigDecimal poiLongitude = null;
            BigDecimal poiLatitude = null;
            if (locationNode.isTextual() && locationNode.asText().contains(",")) {
                String[] location = locationNode.asText().split(",");
                if (location.length == 2) {
                    poiLongitude = parseDecimal(location[0]);
                    poiLatitude = parseDecimal(location[1]);
                }
            }
            results.add(NearbyPlace.builder()
                    .id(trimToNull(JsonUtils.getText(poiNode, "id")))
                    .name(trimToNull(JsonUtils.getText(poiNode, "name")))
                    .address(trimToNull(JsonUtils.getText(poiNode, "address")))
                    .distanceMeters(parseInteger(JsonUtils.getText(poiNode, "distance")))
                    .longitude(poiLongitude)
                    .latitude(poiLatitude)
                    .build());
        }
        return results;
    }

    private void fillByReverseGeocode(ResolvedAddress resolved, BigDecimal longitude, BigDecimal latitude) {
        ReverseGeocodeResult reverseGeocode = reverseGeocode(longitude, latitude);
        if (reverseGeocode == null) {
            return;
        }
        resolved.setProvince(firstNonBlank(reverseGeocode.getProvince(), resolved.getProvince()));
        resolved.setCity(firstNonBlank(reverseGeocode.getCity(), resolved.getCity()));
        resolved.setDistrict(firstNonBlank(reverseGeocode.getDistrict(), resolved.getDistrict()));
        resolved.setStreet(firstNonBlank(reverseGeocode.getStreet(), resolved.getStreet()));
        resolved.setAdcode(firstNonBlank(reverseGeocode.getAdcode(), resolved.getAdcode()));
    }

    private GeocodeResult geocode(String address, String city) {
        JsonNode root = request(GEOCODE_PATH, UriComponentsBuilder.newInstance()
                .queryParam("address", address)
                .queryParam("city", trimToNull(city)));
        JsonNode geocodeNode = root.path("geocodes").isArray() && root.path("geocodes").size() > 0
                ? root.path("geocodes").get(0) : null;
        if (geocodeNode == null || geocodeNode.isMissingNode()) {
            return null;
        }
        String location = JsonUtils.getText(geocodeNode, "location");
        if (StrUtil.isBlank(location) || !location.contains(",")) {
            return null;
        }
        String[] coordinates = location.split(",");
        if (coordinates.length != 2) {
            return null;
        }
        return GeocodeResult.builder()
                .longitude(parseDecimal(coordinates[0]))
                .latitude(parseDecimal(coordinates[1]))
                .adcode(trimToNull(JsonUtils.getText(geocodeNode, "adcode")))
                .province(trimToNull(JsonUtils.getText(geocodeNode, "province")))
                .city(readTextOrFirst(JsonUtils.parseTree(geocodeNode.path("city").toString())))
                .district(trimToNull(JsonUtils.getText(geocodeNode, "district")))
                .build();
    }

    private ReverseGeocodeResult reverseGeocode(BigDecimal longitude, BigDecimal latitude) {
        JsonNode root = request(REVERSE_GEOCODE_PATH, UriComponentsBuilder.newInstance()
                .queryParam("location", buildLocation(longitude, latitude))
                .queryParam("extensions", "base")
                .queryParam("batch", "false")
                .queryParam("roadlevel", 0));
        JsonNode addressComponent = root.path("regeocode").path("addressComponent");
        if (addressComponent.isMissingNode() || addressComponent.isNull()) {
            return null;
        }
        String street = trimToNull(JsonUtils.getText(addressComponent.path("streetNumber"), "street"));
        if (StrUtil.isBlank(street)) {
            street = trimToNull(JsonUtils.getText(addressComponent, "township"));
        }
        return ReverseGeocodeResult.builder()
                .province(trimToNull(JsonUtils.getText(addressComponent, "province")))
                .city(readTextOrFirst(addressComponent.path("city")))
                .district(trimToNull(JsonUtils.getText(addressComponent, "district")))
                .street(street)
                .adcode(trimToNull(JsonUtils.getText(addressComponent, "adcode")))
                .formattedAddress(trimToNull(JsonUtils.getText(root.path("regeocode"), "formatted_address")))
                .build();
    }

    private BigDecimal requestDistance(BigDecimal originLongitude, BigDecimal originLatitude,
                                       BigDecimal destinationLongitude, BigDecimal destinationLatitude) {
        JsonNode root = request(DISTANCE_PATH, UriComponentsBuilder.newInstance()
                .queryParam("origins", buildLocation(originLongitude, originLatitude))
                .queryParam("destination", buildLocation(destinationLongitude, destinationLatitude))
                .queryParam("type", 0));
        JsonNode resultsNode = root.path("results");
        if (!resultsNode.isArray() || resultsNode.isEmpty()) {
            return null;
        }
        String distanceMeters = JsonUtils.getText(resultsNode.get(0), "distance");
        if (StrUtil.isBlank(distanceMeters)) {
            return null;
        }
        return new BigDecimal(distanceMeters)
                .divide(new BigDecimal("1000"), 2, RoundingMode.HALF_UP);
    }

    private JsonNode request(String path, UriComponentsBuilder builder) {
        if (!amapProperties.isEnabled()) {
            throw exception(MAP_REQUEST_FAILED, "高德地图功能已禁用");
        }
        requireConfigured();
        String url = builder.scheme("https")
                .host(extractHost(amapProperties.getBaseUrl()))
                .path(path)
                .queryParam("key", amapProperties.getKey())
                .build()
                .encode()
                .toUriString();
        String body = HttpUtils.get(url, Collections.emptyMap());
        JsonNode root = JsonUtils.parseTree(body);
        if (!Objects.equals("1", JsonUtils.getText(root, "status"))) {
            throw exception(MAP_REQUEST_FAILED, firstNonBlank(JsonUtils.getText(root, "info"), "未知错误"));
        }
        return root;
    }

    private void normalizeMunicipality(ResolvedAddress resolved) {
        if (StrUtil.isBlank(resolved.getCity())) {
            resolved.setCity(resolved.getProvince());
        }
    }

    private void requireConfigured() {
        if (!amapProperties.isEnabled() || !amapProperties.hasKey()) {
            throw exception(MAP_KEY_NOT_CONFIGURED);
        }
    }

    private boolean hasCoordinates(BigDecimal longitude, BigDecimal latitude) {
        return longitude != null && latitude != null;
    }

    private String buildFullAddress(ResolvedAddress address) {
        StringBuilder builder = new StringBuilder();
        appendIfPresent(builder, address.getProvince());
        appendIfPresent(builder, address.getCity());
        appendIfPresent(builder, address.getDistrict());
        appendIfPresent(builder, address.getStreet());
        appendIfPresent(builder, address.getDetailAddress());
        return builder.toString();
    }

    private String buildLocation(BigDecimal longitude, BigDecimal latitude) {
        return longitude.stripTrailingZeros().toPlainString() + "," + latitude.stripTrailingZeros().toPlainString();
    }

    private String extractHost(String baseUrl) {
        String trimmed = StrUtil.removeSuffix(StrUtil.removePrefixIgnoreCase(StrUtil.trim(baseUrl), "https://"), "/");
        return StrUtil.removePrefixIgnoreCase(trimmed, "http://");
    }

    private String readTextOrFirst(JsonNode node) {
        if (node == null || node.isNull() || node.isMissingNode()) {
            return null;
        }
        if (node.isTextual()) {
            return trimToNull(node.asText());
        }
        if (node.isArray() && node.size() > 0) {
            for (JsonNode item : node) {
                String text = trimToNull(item.asText());
                if (text != null) {
                    return text;
                }
            }
        }
        return null;
    }

    private String firstNonBlank(String... values) {
        for (String value : values) {
            String trimmed = trimToNull(value);
            if (trimmed != null) {
                return trimmed;
            }
        }
        return null;
    }

    private String trimToNull(String value) {
        return StrUtil.blankToDefault(StrUtil.trim(value), null);
    }

    private void appendIfPresent(StringBuilder builder, String value) {
        String trimmed = trimToNull(value);
        if (trimmed != null) {
            builder.append(trimmed);
        }
    }

    private BigDecimal parseDecimal(String value) {
        if (StrUtil.isBlank(value)) {
            return null;
        }
        return new BigDecimal(value).setScale(6, RoundingMode.HALF_UP);
    }

    private Integer parseInteger(String value) {
        if (StrUtil.isBlank(value)) {
            return null;
        }
        return Integer.valueOf(value);
    }

    @Data
    @Builder
    @NoArgsConstructor
    @AllArgsConstructor
    public static class ResolveAddressRequest {
        private String province;
        private String city;
        private String district;
        private String street;
        private String detailAddress;
        private BigDecimal longitude;
        private BigDecimal latitude;
        private String adcode;
    }

    @Data
    @Builder
    @NoArgsConstructor
    @AllArgsConstructor
    public static class ResolvedAddress {
        private String province;
        private String city;
        private String district;
        private String street;
        private String detailAddress;
        private BigDecimal longitude;
        private BigDecimal latitude;
        private String adcode;
    }

    @Data
    @Builder
    @NoArgsConstructor
    @AllArgsConstructor
    public static class DistrictInfo {
        private String name;
        private String adcode;
        private String cityCode;
        private String level;
        private String center;
    }

    @Data
    @Builder
    @NoArgsConstructor
    @AllArgsConstructor
    public static class NearbyPlace {
        private String id;
        private String name;
        private String address;
        private Integer distanceMeters;
        private BigDecimal longitude;
        private BigDecimal latitude;
    }

    @Data
    @Builder
    @NoArgsConstructor
    @AllArgsConstructor
    private static class GeocodeResult {
        private BigDecimal longitude;
        private BigDecimal latitude;
        private String province;
        private String city;
        private String district;
        private String adcode;
    }

    @Data
    @Builder
    @NoArgsConstructor
    @AllArgsConstructor
    private static class ReverseGeocodeResult {
        private String province;
        private String city;
        private String district;
        private String street;
        private String adcode;
        private String formattedAddress;
    }

}

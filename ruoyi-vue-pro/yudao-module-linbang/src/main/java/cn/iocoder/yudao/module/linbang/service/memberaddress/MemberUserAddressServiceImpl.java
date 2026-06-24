package cn.iocoder.yudao.module.linbang.service.memberaddress;

import cn.hutool.core.collection.CollUtil;
import cn.hutool.core.util.StrUtil;
import com.baomidou.mybatisplus.core.conditions.update.LambdaUpdateWrapper;
import org.springframework.stereotype.Service;
import javax.annotation.Resource;
import org.springframework.validation.annotation.Validated;
import org.springframework.transaction.annotation.Transactional;

import java.util.*;
import cn.iocoder.yudao.module.linbang.controller.admin.memberaddress.vo.*;
import cn.iocoder.yudao.module.linbang.dal.dataobject.memberaddress.MemberUserAddressDO;
import cn.iocoder.yudao.module.linbang.dal.dataobject.memberuser.MemberUserDO;
import cn.iocoder.yudao.framework.common.pojo.PageResult;
import cn.iocoder.yudao.framework.common.util.object.BeanUtils;

import cn.iocoder.yudao.module.linbang.dal.mysql.memberaddress.MemberUserAddressMapper;
import cn.iocoder.yudao.module.linbang.dal.mysql.memberuser.MemberUserMapper;
import cn.iocoder.yudao.module.linbang.service.map.AmapLocationService;

import static cn.iocoder.yudao.framework.common.exception.util.ServiceExceptionUtil.exception;
import static cn.iocoder.yudao.framework.common.util.collection.CollectionUtils.convertList;
import static cn.iocoder.yudao.framework.common.util.collection.CollectionUtils.convertMap;
import static cn.iocoder.yudao.framework.common.util.collection.CollectionUtils.convertSet;
import static cn.iocoder.yudao.module.linbang.enums.ErrorCodeConstants.*;

/**
 * 用户地址表 Service 实现类
 *
 * @author dawn
 */
@Service
@Validated
public class MemberUserAddressServiceImpl implements MemberUserAddressService {

    @Resource
    private MemberUserAddressMapper memberUserAddressMapper;
    @Resource
    private MemberUserMapper memberUserMapper;
    @Resource
    private AmapLocationService amapLocationService;

    @Override
    @Transactional(rollbackFor = Exception.class)
    public Long createMemberUserAddress(MemberUserAddressSaveReqVO createReqVO) {
        if (Boolean.TRUE.equals(createReqVO.getIsDefault())) {
            clearDefaultAddress(createReqVO.getUserId(), null);
        }
        AmapLocationService.ResolvedAddress resolvedAddress = resolveAddress(createReqVO);
        MemberUserAddressDO memberUserAddress = BeanUtils.toBean(createReqVO, MemberUserAddressDO.class)
                .setProvince(resolvedAddress.getProvince())
                .setCity(resolvedAddress.getCity())
                .setDistrict(resolvedAddress.getDistrict())
                .setStreet(resolvedAddress.getStreet())
                .setDetailAddress(resolvedAddress.getDetailAddress())
                .setLongitude(resolvedAddress.getLongitude())
                .setLatitude(resolvedAddress.getLatitude())
                .setAdcode(resolvedAddress.getAdcode());
        memberUserAddressMapper.insert(memberUserAddress);
        return memberUserAddress.getId();
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public void updateMemberUserAddress(MemberUserAddressSaveReqVO updateReqVO) {
        MemberUserAddressDO existed = validateMemberUserAddressExists(updateReqVO.getId());
        AmapLocationService.ResolvedAddress resolvedAddress = resolveAddress(updateReqVO);
        if (Boolean.TRUE.equals(updateReqVO.getIsDefault())) {
            clearDefaultAddress(existed.getUserId(), existed.getId());
        }
        MemberUserAddressDO updateObj = BeanUtils.toBean(updateReqVO, MemberUserAddressDO.class)
                .setProvince(resolvedAddress.getProvince())
                .setCity(resolvedAddress.getCity())
                .setDistrict(resolvedAddress.getDistrict())
                .setStreet(resolvedAddress.getStreet())
                .setDetailAddress(resolvedAddress.getDetailAddress())
                .setLongitude(resolvedAddress.getLongitude())
                .setLatitude(resolvedAddress.getLatitude())
                .setAdcode(resolvedAddress.getAdcode());
        memberUserAddressMapper.updateById(updateObj);
    }

    @Override
    public void deleteMemberUserAddress(Long id) {
        // 校验存在
        validateMemberUserAddressExists(id);
        // 删除
        memberUserAddressMapper.deleteById(id);
    }

    @Override
    public void deleteMemberUserAddressListByIds(List<Long> ids) {
        // 删除
        memberUserAddressMapper.deleteByIds(ids);
    }


    private MemberUserAddressDO validateMemberUserAddressExists(Long id) {
        MemberUserAddressDO memberUserAddress = memberUserAddressMapper.selectById(id);
        if (memberUserAddress == null) {
            throw exception(MEMBER_USER_ADDRESS_NOT_EXISTS);
        }
        return memberUserAddress;
    }

    private void clearDefaultAddress(Long userId, Long excludeId) {
        LambdaUpdateWrapper<MemberUserAddressDO> updateWrapper = new LambdaUpdateWrapper<MemberUserAddressDO>()
                .eq(MemberUserAddressDO::getUserId, userId)
                .set(MemberUserAddressDO::getIsDefault, Boolean.FALSE);
        if (excludeId != null) {
            updateWrapper.ne(MemberUserAddressDO::getId, excludeId);
        }
        memberUserAddressMapper.update(null, updateWrapper);
    }

    @Override
    public MemberUserAddressDO getMemberUserAddress(Long id) {
        return memberUserAddressMapper.selectById(id);
    }

    @Override
    public PageResult<MemberUserAddressRespVO> getMemberUserAddressPage(MemberUserAddressPageReqVO pageReqVO) {
        List<Long> matchedUserIds = resolveMatchedUserIds(pageReqVO.getUserKeyword());
        if (StrUtil.isNotBlank(pageReqVO.getUserKeyword()) && CollUtil.isEmpty(matchedUserIds)) {
            return PageResult.empty();
        }
        PageResult<MemberUserAddressDO> pageResult = memberUserAddressMapper.selectPage(pageReqVO, matchedUserIds);
        List<MemberUserAddressRespVO> list = BeanUtils.toBean(pageResult.getList(), MemberUserAddressRespVO.class);
        fillUserDisplayInfo(list);
        return new PageResult<>(list, pageResult.getTotal());
    }

    private List<Long> resolveMatchedUserIds(String userKeyword) {
        if (StrUtil.isBlank(userKeyword)) {
            return null;
        }
        return convertList(memberUserMapper.selectListByKeyword(userKeyword), MemberUserDO::getId);
    }

    private void fillUserDisplayInfo(List<MemberUserAddressRespVO> list) {
        Set<Long> userIds = convertSet(list, MemberUserAddressRespVO::getUserId,
                item -> item.getUserId() != null);
        Map<Long, MemberUserDO> userMap = userIds.isEmpty() ? Collections.emptyMap()
                : convertMap(memberUserMapper.selectListByIds(userIds), MemberUserDO::getId);
        list.forEach(item -> {
            MemberUserDO user = userMap.get(item.getUserId());
            if (user == null) {
                return;
            }
            item.setUserNo(user.getUserNo());
            item.setUserNickname(user.getNickname());
            item.setUserMobile(user.getMobile());
        });
    }

    private AmapLocationService.ResolvedAddress resolveAddress(MemberUserAddressSaveReqVO reqVO) {
        return amapLocationService.resolveAddress(AmapLocationService.ResolveAddressRequest.builder()
                .province(reqVO.getProvince())
                .city(reqVO.getCity())
                .district(reqVO.getDistrict())
                .street(reqVO.getStreet())
                .detailAddress(reqVO.getDetailAddress())
                .longitude(reqVO.getLongitude())
                .latitude(reqVO.getLatitude())
                .adcode(reqVO.getAdcode())
                .build());
    }

}

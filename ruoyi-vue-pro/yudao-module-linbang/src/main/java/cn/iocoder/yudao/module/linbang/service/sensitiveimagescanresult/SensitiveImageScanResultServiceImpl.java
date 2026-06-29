package cn.iocoder.yudao.module.linbang.service.sensitiveimagescanresult;

import cn.iocoder.yudao.framework.common.pojo.PageResult;
import cn.iocoder.yudao.framework.common.util.object.BeanUtils;
import cn.iocoder.yudao.module.linbang.controller.admin.sensitiveimagescanresult.vo.SensitiveImageScanResultPageReqVO;
import cn.iocoder.yudao.module.linbang.controller.admin.sensitiveimagescanresult.vo.SensitiveImageScanResultRespVO;
import cn.iocoder.yudao.module.linbang.dal.dataobject.memberuser.MemberUserDO;
import cn.iocoder.yudao.module.linbang.dal.dataobject.sensitiveimagescanresult.SensitiveImageScanResultDO;
import cn.iocoder.yudao.module.linbang.dal.mysql.memberuser.MemberUserMapper;
import cn.iocoder.yudao.module.linbang.dal.mysql.sensitiveimagescanresult.SensitiveImageScanResultMapper;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;
import java.util.Collections;
import java.util.List;
import java.util.Map;

import static cn.iocoder.yudao.framework.common.util.collection.CollectionUtils.convertMap;
import static cn.iocoder.yudao.framework.common.util.collection.CollectionUtils.convertSet;

@Service
public class SensitiveImageScanResultServiceImpl implements SensitiveImageScanResultService {

    @Resource
    private SensitiveImageScanResultMapper sensitiveImageScanResultMapper;
    @Resource
    private MemberUserMapper memberUserMapper;

    @Override
    public PageResult<SensitiveImageScanResultRespVO> getPage(SensitiveImageScanResultPageReqVO reqVO) {
        PageResult<SensitiveImageScanResultDO> page = sensitiveImageScanResultMapper.selectPage(reqVO);
        List<SensitiveImageScanResultRespVO> list = BeanUtils.toBean(page.getList(), SensitiveImageScanResultRespVO.class);
        Map<Long, MemberUserDO> userMap = convertSet(list, SensitiveImageScanResultRespVO::getUserId, item -> item.getUserId() != null).isEmpty()
                ? Collections.emptyMap()
                : convertMap(memberUserMapper.selectListByIds(convertSet(list, SensitiveImageScanResultRespVO::getUserId,
                item -> item.getUserId() != null)), MemberUserDO::getId);
        list.forEach(item -> {
            MemberUserDO user = userMap.get(item.getUserId());
            if (user != null) {
                item.setUserNo(user.getUserNo());
                item.setUserNickname(user.getNickname());
                item.setUserMobile(user.getMobile());
            }
        });
        return new PageResult<>(list, page.getTotal());
    }
}

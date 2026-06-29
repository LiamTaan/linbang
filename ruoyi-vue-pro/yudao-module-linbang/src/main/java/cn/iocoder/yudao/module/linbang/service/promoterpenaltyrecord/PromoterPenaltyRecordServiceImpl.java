package cn.iocoder.yudao.module.linbang.service.promoterpenaltyrecord;

import cn.iocoder.yudao.framework.common.pojo.PageResult;
import cn.iocoder.yudao.framework.common.util.object.BeanUtils;
import cn.iocoder.yudao.module.linbang.controller.admin.promoterpenaltyrecord.vo.PromoterPenaltyRecordPageReqVO;
import cn.iocoder.yudao.module.linbang.controller.admin.promoterpenaltyrecord.vo.PromoterPenaltyRecordDetailRespVO;
import cn.iocoder.yudao.module.linbang.controller.admin.promoterpenaltyrecord.vo.PromoterPenaltyRecordRespVO;
import cn.iocoder.yudao.module.linbang.dal.dataobject.memberuser.MemberUserDO;
import cn.iocoder.yudao.module.linbang.dal.dataobject.promotecontent.PromoteContentDO;
import cn.iocoder.yudao.module.linbang.dal.dataobject.promoterpenaltyrecord.PromoterPenaltyRecordDO;
import cn.iocoder.yudao.module.linbang.dal.mysql.memberuser.MemberUserMapper;
import cn.iocoder.yudao.module.linbang.dal.mysql.promotecontent.PromoteContentMapper;
import cn.iocoder.yudao.module.linbang.dal.mysql.promoterpenaltyrecord.PromoterPenaltyRecordMapper;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;
import java.util.Collections;
import java.util.List;
import java.util.Map;

import static cn.iocoder.yudao.framework.common.util.collection.CollectionUtils.convertMap;
import static cn.iocoder.yudao.framework.common.util.collection.CollectionUtils.convertSet;
import static cn.iocoder.yudao.framework.common.exception.util.ServiceExceptionUtil.exception;
import static cn.iocoder.yudao.module.linbang.enums.ErrorCodeConstants.PROMOTER_PENALTY_RECORD_NOT_EXISTS;

@Service
public class PromoterPenaltyRecordServiceImpl implements PromoterPenaltyRecordService {

    @Resource
    private PromoterPenaltyRecordMapper promoterPenaltyRecordMapper;
    @Resource
    private MemberUserMapper memberUserMapper;
    @Resource
    private PromoteContentMapper promoteContentMapper;

    @Override
    public PageResult<PromoterPenaltyRecordRespVO> getAdminPage(PromoterPenaltyRecordPageReqVO reqVO) {
        PageResult<PromoterPenaltyRecordDO> page = promoterPenaltyRecordMapper.selectPageByPromoterIds(reqVO, Collections.<Long>emptySet());
        List<PromoterPenaltyRecordRespVO> list = BeanUtils.toBean(page.getList(), PromoterPenaltyRecordRespVO.class);
        Map<Long, MemberUserDO> userMap = convertSet(list, PromoterPenaltyRecordRespVO::getUserId, item -> item.getUserId() != null).isEmpty()
                ? Collections.emptyMap()
                : convertMap(memberUserMapper.selectListByIds(convertSet(list, PromoterPenaltyRecordRespVO::getUserId, item -> item.getUserId() != null)),
                MemberUserDO::getId);
        Map<Long, PromoteContentDO> contentMap = convertSet(list, PromoterPenaltyRecordRespVO::getContentId, item -> item.getContentId() != null).isEmpty()
                ? Collections.emptyMap()
                : convertMap(promoteContentMapper.selectBatchIds(convertSet(list, PromoterPenaltyRecordRespVO::getContentId, item -> item.getContentId() != null)),
                PromoteContentDO::getId);
        list.forEach(item -> {
            MemberUserDO user = userMap.get(item.getUserId());
            if (user != null) {
                item.setUserNo(user.getUserNo());
                item.setUserNickname(user.getNickname());
                item.setUserMobile(user.getMobile());
            }
            PromoteContentDO content = contentMap.get(item.getContentId());
            if (content != null) {
                item.setContentTitle(content.getTitle());
            }
        });
        return new PageResult<>(list, page.getTotal());
    }

    @Override
    public PromoterPenaltyRecordDetailRespVO getDetail(Long id) {
        PromoterPenaltyRecordDO record = promoterPenaltyRecordMapper.selectById(id);
        if (record == null) {
            throw exception(PROMOTER_PENALTY_RECORD_NOT_EXISTS);
        }
        PromoterPenaltyRecordDetailRespVO respVO = BeanUtils.toBean(record, PromoterPenaltyRecordDetailRespVO.class);
        fillDisplay(Collections.singletonList(respVO));
        return respVO;
    }

    private void fillDisplay(List<? extends PromoterPenaltyRecordRespVO> list) {
        Map<Long, MemberUserDO> userMap = convertSet(list, PromoterPenaltyRecordRespVO::getUserId, item -> item.getUserId() != null).isEmpty()
                ? Collections.emptyMap()
                : convertMap(memberUserMapper.selectListByIds(convertSet(list, PromoterPenaltyRecordRespVO::getUserId, item -> item.getUserId() != null)),
                MemberUserDO::getId);
        Map<Long, PromoteContentDO> contentMap = convertSet(list, PromoterPenaltyRecordRespVO::getContentId, item -> item.getContentId() != null).isEmpty()
                ? Collections.emptyMap()
                : convertMap(promoteContentMapper.selectBatchIds(convertSet(list, PromoterPenaltyRecordRespVO::getContentId, item -> item.getContentId() != null)),
                PromoteContentDO::getId);
        list.forEach(item -> {
            MemberUserDO user = userMap.get(item.getUserId());
            if (user != null) {
                item.setUserNo(user.getUserNo());
                item.setUserNickname(user.getNickname());
                item.setUserMobile(user.getMobile());
            }
            PromoteContentDO content = contentMap.get(item.getContentId());
            if (content != null) {
                item.setContentTitle(content.getTitle());
            }
        });
    }
}

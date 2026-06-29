package cn.iocoder.yudao.module.linbang.service.appeal;

import cn.hutool.core.collection.CollUtil;
import cn.hutool.core.util.StrUtil;
import cn.iocoder.yudao.framework.common.pojo.PageResult;
import cn.iocoder.yudao.framework.common.util.object.BeanUtils;
import cn.iocoder.yudao.framework.mybatis.core.query.LambdaQueryWrapperX;
import cn.iocoder.yudao.framework.security.core.util.SecurityFrameworkUtils;
import org.springframework.stereotype.Service;
import org.springframework.validation.annotation.Validated;
import org.springframework.transaction.annotation.Transactional;

import javax.annotation.Resource;
import java.util.*;
import java.time.LocalDateTime;
import cn.iocoder.yudao.module.linbang.controller.admin.appeal.vo.AppealAuditReqVO;
import cn.iocoder.yudao.module.linbang.controller.admin.appeal.vo.AppealDetailRespVO;
import cn.iocoder.yudao.module.linbang.controller.admin.appeal.vo.AppealPageReqVO;
import cn.iocoder.yudao.module.linbang.controller.admin.appeal.vo.AppealRespVO;
import cn.iocoder.yudao.module.linbang.controller.admin.appeal.vo.AppealSaveReqVO;
import cn.iocoder.yudao.module.linbang.dal.dataobject.appeal.AppealDO;
import cn.iocoder.yudao.module.linbang.dal.dataobject.appealfilerel.AppealFileRelDO;
import cn.iocoder.yudao.module.linbang.dal.dataobject.memberuser.MemberUserDO;
import cn.iocoder.yudao.module.linbang.dal.mysql.appeal.AppealMapper;
import cn.iocoder.yudao.module.linbang.dal.mysql.appealfilerel.AppealFileRelMapper;
import cn.iocoder.yudao.module.linbang.dal.mysql.memberuser.MemberUserMapper;
import cn.iocoder.yudao.module.linbang.dal.dataobject.merchantinfo.MerchantInfoDO;
import cn.iocoder.yudao.module.linbang.dal.mysql.merchantinfo.MerchantInfoMapper;
import cn.iocoder.yudao.module.linbang.dal.dataobject.orderinfo.OrderInfoDO;
import cn.iocoder.yudao.module.linbang.dal.dataobject.orderoperatelog.OrderOperateLogDO;
import cn.iocoder.yudao.module.linbang.dal.dataobject.orderunit.OrderUnitDO;
import cn.iocoder.yudao.module.linbang.dal.dataobject.partnercoordination.PartnerCoordinationDO;
import cn.iocoder.yudao.module.linbang.dal.mysql.orderinfo.OrderInfoMapper;
import cn.iocoder.yudao.module.linbang.dal.mysql.orderoperatelog.OrderOperateLogMapper;
import cn.iocoder.yudao.module.linbang.dal.mysql.orderunit.OrderUnitMapper;
import cn.iocoder.yudao.module.linbang.dal.mysql.partnercoordination.PartnerCoordinationMapper;
import cn.iocoder.yudao.module.linbang.service.creditrecord.CreditRecordService;
import cn.iocoder.yudao.module.linbang.service.messagepushtask.MessagePushDispatchService;

import static cn.iocoder.yudao.framework.common.exception.util.ServiceExceptionUtil.exception;
import static cn.iocoder.yudao.framework.common.util.collection.CollectionUtils.convertList;
import static cn.iocoder.yudao.framework.common.util.collection.CollectionUtils.convertMap;
import static cn.iocoder.yudao.framework.common.util.collection.CollectionUtils.convertSet;
import static cn.iocoder.yudao.module.linbang.enums.ErrorCodeConstants.*;

/**
 * 申诉 Service 实现类
 *
 * @author dawn
 */
@Service
@Validated
public class AppealServiceImpl implements AppealService {

    @Resource
    private AppealMapper appealMapper;
    @Resource
    private OrderInfoMapper orderInfoMapper;
    @Resource
    private OrderUnitMapper orderUnitMapper;
    @Resource
    private MemberUserMapper memberUserMapper;
    @Resource
    private MerchantInfoMapper merchantInfoMapper;
    @Resource
    private AppealFileRelMapper appealFileRelMapper;
    @Resource
    private OrderOperateLogMapper orderOperateLogMapper;
    @Resource
    private PartnerCoordinationMapper partnerCoordinationMapper;
    @Resource
    private CreditRecordService creditRecordService;
    @Resource
    private MessagePushDispatchService messagePushDispatchService;

    @Override
    public Long createAppeal(AppealSaveReqVO createReqVO) {
        // 插入
        AppealDO appeal = BeanUtils.toBean(createReqVO, AppealDO.class);
        appealMapper.insert(appeal);

        // 返回
        return appeal.getId();
    }

    @Override
    public void updateAppeal(AppealSaveReqVO updateReqVO) {
        // 校验存在
        validateAppealExists(updateReqVO.getId());
        // 更新
        AppealDO updateObj = BeanUtils.toBean(updateReqVO, AppealDO.class);
        appealMapper.updateById(updateObj);
    }

    @Override
    public void deleteAppeal(Long id) {
        // 校验存在
        validateAppealExists(id);
        // 删除
        appealMapper.deleteById(id);
    }

    @Override
        public void deleteAppealListByIds(List<Long> ids) {
        // 删除
        appealMapper.deleteByIds(ids);
        }


    private void validateAppealExists(Long id) {
        if (appealMapper.selectById(id) == null) {
            throw exception(APPEAL_NOT_EXISTS);
        }
    }

    @Override
    public AppealDO getAppeal(Long id) {
        return appealMapper.selectById(id);
    }

    @Override
    public AppealDetailRespVO getAppealDetail(Long id) {
        AppealDO appeal = appealMapper.selectById(id);
        if (appeal == null) {
            throw exception(APPEAL_NOT_EXISTS);
        }
        OrderInfoDO order = appeal.getOrderId() == null ? null : orderInfoMapper.selectById(appeal.getOrderId());
        OrderUnitDO unit = appeal.getUnitId() == null ? null : orderUnitMapper.selectById(appeal.getUnitId());
        MemberUserDO orderUser = order == null || order.getUserId() == null ? null : memberUserMapper.selectById(order.getUserId());
        MemberUserDO user = appeal.getUserId() == null ? null : memberUserMapper.selectById(appeal.getUserId());
        MerchantInfoDO merchant = appeal.getUserId() == null ? null : merchantInfoMapper.selectOne(
                new LambdaQueryWrapperX<MerchantInfoDO>()
                        .eq(MerchantInfoDO::getUserId, appeal.getUserId())
                        .last("LIMIT 1"));
        List<AppealFileRelDO> fileRels = appealFileRelMapper.selectListByAppealId(id);
        List<AppealDO> relatedAppeals = appeal.getOrderId() == null ? Collections.emptyList()
                : appealMapper.selectList(new LambdaQueryWrapperX<AppealDO>()
                .eq(AppealDO::getOrderId, appeal.getOrderId())
                .orderByDesc(AppealDO::getCreateTime, AppealDO::getId));
        List<PartnerCoordinationDO> coordinationRecords = partnerCoordinationMapper.selectListByDispute("APPEAL", id);
        List<OrderOperateLogDO> operateLogs = appeal.getOrderId() == null ? Collections.emptyList()
                : orderOperateLogMapper.selectList(new LambdaQueryWrapperX<OrderOperateLogDO>()
                .eq(OrderOperateLogDO::getOrderId, appeal.getOrderId())
                .eq(appeal.getUnitId() != null, OrderOperateLogDO::getUnitId, appeal.getUnitId())
                .orderByDesc(OrderOperateLogDO::getOperateTime, OrderOperateLogDO::getId));
        return AppealDetailAssembler.build(appeal, order, unit, user, orderUser, merchant, fileRels,
                relatedAppeals, coordinationRecords, operateLogs);
    }

    @Override
    public void auditAppeal(AppealAuditReqVO reqVO) {
        AppealDO appeal = appealMapper.selectById(reqVO.getId());
        if (appeal == null) {
            throw exception(APPEAL_NOT_EXISTS);
        }
        AppealDO updateObj = new AppealDO();
        updateObj.setId(reqVO.getId());
        updateObj.setAuditStatus(reqVO.getAuditStatus());
        updateObj.setAuditRemark(reqVO.getAuditRemark());
        updateObj.setRejectReason(reqVO.getRejectReason());
        updateObj.setAuditBy(SecurityFrameworkUtils.getLoginUserId());
        updateObj.setAuditTime(LocalDateTime.now());
        if ("APPROVED".equals(reqVO.getAuditStatus())) {
            updateObj.setStatus("PROCESSING");
        } else if ("REJECTED".equals(reqVO.getAuditStatus())) {
            updateObj.setStatus("REJECTED");
        }
        appealMapper.updateById(updateObj);
        if ("APPROVED".equals(reqVO.getAuditStatus())) {
            MerchantInfoDO merchant = merchantInfoMapper.selectOne(new LambdaQueryWrapperX<MerchantInfoDO>()
                    .eq(MerchantInfoDO::getUserId, appeal.getUserId())
                    .last("LIMIT 1"));
            creditRecordService.applyCreditRule(appeal.getUserId(),
                    merchant != null ? merchant.getId() : null,
                    "APPEAL_APPROVED", "APPEAL", appeal.getId(),
                    reqVO.getAuditRemark() != null ? reqVO.getAuditRemark() : "申诉审核通过");
        }
        orderOperateLogMapper.insert(OrderOperateLogDO.builder()
                .orderId(appeal.getOrderId())
                .unitId(appeal.getUnitId())
                .operateType("AUDIT_APPEAL")
                .operateRole("ADMIN")
                .operateBy(SecurityFrameworkUtils.getLoginUserId())
                .beforeStatus(appeal.getStatus())
                .afterStatus(updateObj.getStatus())
                .remark(StrUtil.blankToDefault(reqVO.getAuditRemark(),
                        "申诉审核结果：" + reqVO.getAuditStatus()))
                .operateTime(LocalDateTime.now())
                .build());
        dispatchAppealAuditResult(appeal, reqVO);
    }

    @Override
    public PageResult<AppealRespVO> getAppealPage(AppealPageReqVO pageReqVO) {
        List<Long> matchedOrderIds = resolveMatchedOrderIds(pageReqVO.getOrderNo());
        if (StrUtil.isNotBlank(pageReqVO.getOrderNo()) && CollUtil.isEmpty(matchedOrderIds)) {
            return PageResult.empty();
        }
        List<Long> matchedUnitIds = resolveMatchedUnitIds(pageReqVO.getUnitNo());
        if (StrUtil.isNotBlank(pageReqVO.getUnitNo()) && CollUtil.isEmpty(matchedUnitIds)) {
            return PageResult.empty();
        }
        List<Long> matchedUserIds = resolveMatchedUserIds(pageReqVO.getUserKeyword());
        if (StrUtil.isNotBlank(pageReqVO.getUserKeyword()) && CollUtil.isEmpty(matchedUserIds)) {
            return PageResult.empty();
        }
        PageResult<AppealDO> pageResult = appealMapper.selectPage(pageReqVO, matchedOrderIds, matchedUnitIds, matchedUserIds);
        List<AppealRespVO> list = BeanUtils.toBean(pageResult.getList(), AppealRespVO.class);
        fillDisplayInfo(list);
        return new PageResult<>(list, pageResult.getTotal());
    }

    private List<Long> resolveMatchedUserIds(String userKeyword) {
        if (StrUtil.isBlank(userKeyword)) {
            return null;
        }
        return convertList(memberUserMapper.selectListByKeyword(userKeyword), MemberUserDO::getId);
    }

    private List<Long> resolveMatchedOrderIds(String orderNo) {
        if (StrUtil.isBlank(orderNo)) {
            return null;
        }
        return convertList(orderInfoMapper.selectListByOrderNo(orderNo), OrderInfoDO::getId);
    }

    private List<Long> resolveMatchedUnitIds(String unitNo) {
        if (StrUtil.isBlank(unitNo)) {
            return null;
        }
        return convertList(orderUnitMapper.selectListByUnitNo(unitNo), OrderUnitDO::getId);
    }

    private void fillDisplayInfo(List<AppealRespVO> list) {
        Set<Long> orderIds = convertSet(list, AppealRespVO::getOrderId);
        Map<Long, OrderInfoDO> orderMap = orderIds.isEmpty() ? Collections.emptyMap()
                : convertMap(orderInfoMapper.selectBatchIds(orderIds), OrderInfoDO::getId);
        Set<Long> unitIds = convertSet(list, AppealRespVO::getUnitId);
        Map<Long, OrderUnitDO> unitMap = unitIds.isEmpty() ? Collections.emptyMap()
                : convertMap(orderUnitMapper.selectBatchIds(unitIds), OrderUnitDO::getId);
        Set<Long> userIds = convertSet(list, AppealRespVO::getUserId);
        Map<Long, MemberUserDO> userMap = userIds.isEmpty() ? Collections.emptyMap()
                : convertMap(memberUserMapper.selectListByIds(userIds), MemberUserDO::getId);
        list.forEach(item -> {
            OrderInfoDO order = orderMap.get(item.getOrderId());
            if (order != null) {
                item.setOrderNo(order.getOrderNo());
            }
            OrderUnitDO unit = unitMap.get(item.getUnitId());
            if (unit != null) {
                item.setUnitNo(unit.getUnitNo());
            }
            MemberUserDO user = userMap.get(item.getUserId());
            if (user == null) {
                return;
            }
            item.setUserNo(user.getUserNo());
            item.setUserNickname(user.getNickname());
            item.setUserMobile(user.getMobile());
        });
    }

    private void dispatchAppealAuditResult(AppealDO appeal, AppealAuditReqVO reqVO) {
        if (appeal == null || appeal.getUserId() == null) {
            return;
        }
        String remark = StrUtil.blankToDefault(reqVO.getAuditRemark(),
                "申诉审核结果：" + StrUtil.blankToDefault(reqVO.getAuditStatus(), "UNKNOWN"));
        if (StrUtil.isNotBlank(reqVO.getRejectReason())) {
            remark = reqVO.getRejectReason();
        }
        messagePushDispatchService.dispatchSingle("DISPUTE_RESULT", "申诉结果通知", "APPEAL",
                appeal.getId(), appeal.getUserId(), remark);
    }

}

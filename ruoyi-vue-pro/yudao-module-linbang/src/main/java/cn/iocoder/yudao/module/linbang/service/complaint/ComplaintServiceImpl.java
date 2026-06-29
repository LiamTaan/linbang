package cn.iocoder.yudao.module.linbang.service.complaint;

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
import cn.iocoder.yudao.module.linbang.controller.admin.complaint.vo.ComplaintDetailRespVO;
import cn.iocoder.yudao.module.linbang.controller.admin.complaint.vo.ComplaintPageReqVO;
import cn.iocoder.yudao.module.linbang.controller.admin.complaint.vo.ComplaintProcessReqVO;
import cn.iocoder.yudao.module.linbang.controller.admin.complaint.vo.ComplaintRespVO;
import cn.iocoder.yudao.module.linbang.controller.admin.complaint.vo.ComplaintSaveReqVO;
import cn.iocoder.yudao.module.linbang.dal.dataobject.complaint.ComplaintDO;
import cn.iocoder.yudao.module.linbang.dal.dataobject.complaintfilerel.ComplaintFileRelDO;
import cn.iocoder.yudao.module.linbang.dal.dataobject.memberuser.MemberUserDO;
import cn.iocoder.yudao.module.linbang.dal.dataobject.orderinfo.OrderInfoDO;
import cn.iocoder.yudao.module.linbang.dal.dataobject.orderoperatelog.OrderOperateLogDO;
import cn.iocoder.yudao.module.linbang.dal.dataobject.orderunit.OrderUnitDO;
import cn.iocoder.yudao.module.linbang.dal.dataobject.partnercoordination.PartnerCoordinationDO;
import cn.iocoder.yudao.module.linbang.dal.mysql.complaint.ComplaintMapper;
import cn.iocoder.yudao.module.linbang.dal.mysql.complaintfilerel.ComplaintFileRelMapper;
import cn.iocoder.yudao.module.linbang.dal.mysql.memberuser.MemberUserMapper;
import cn.iocoder.yudao.module.linbang.dal.dataobject.merchantinfo.MerchantInfoDO;
import cn.iocoder.yudao.module.linbang.dal.mysql.merchantinfo.MerchantInfoMapper;
import cn.iocoder.yudao.module.linbang.dal.mysql.orderinfo.OrderInfoMapper;
import cn.iocoder.yudao.module.linbang.dal.mysql.orderoperatelog.OrderOperateLogMapper;
import cn.iocoder.yudao.module.linbang.dal.mysql.orderunit.OrderUnitMapper;
import cn.iocoder.yudao.module.linbang.dal.mysql.partnercoordination.PartnerCoordinationMapper;
import cn.iocoder.yudao.module.linbang.service.creditrecord.CreditRecordService;
import cn.iocoder.yudao.module.linbang.service.messagepushtask.MessagePushDispatchService;

import static cn.iocoder.yudao.framework.common.exception.util.ServiceExceptionUtil.exception;
import static cn.iocoder.yudao.framework.common.util.collection.CollectionUtils.convertList;
import static cn.iocoder.yudao.framework.common.util.collection.CollectionUtils.convertMap;
import static cn.iocoder.yudao.module.linbang.enums.ErrorCodeConstants.*;

/**
 * 投诉 Service 实现类
 *
 * @author dawn
 */
@Service
@Validated
public class ComplaintServiceImpl implements ComplaintService {

    @Resource
    private ComplaintMapper complaintMapper;
    @Resource
    private OrderInfoMapper orderInfoMapper;
    @Resource
    private OrderUnitMapper orderUnitMapper;
    @Resource
    private MemberUserMapper memberUserMapper;
    @Resource
    private MerchantInfoMapper merchantInfoMapper;
    @Resource
    private ComplaintFileRelMapper complaintFileRelMapper;
    @Resource
    private OrderOperateLogMapper orderOperateLogMapper;
    @Resource
    private PartnerCoordinationMapper partnerCoordinationMapper;
    @Resource
    private CreditRecordService creditRecordService;
    @Resource
    private MessagePushDispatchService messagePushDispatchService;

    @Override
    public Long createComplaint(ComplaintSaveReqVO createReqVO) {
        // 插入
        ComplaintDO complaint = BeanUtils.toBean(createReqVO, ComplaintDO.class);
        complaintMapper.insert(complaint);
        dispatchComplaintCreated(complaint);

        // 返回
        return complaint.getId();
    }

    @Override
    public void updateComplaint(ComplaintSaveReqVO updateReqVO) {
        // 校验存在
        validateComplaintExists(updateReqVO.getId());
        // 更新
        ComplaintDO updateObj = BeanUtils.toBean(updateReqVO, ComplaintDO.class);
        complaintMapper.updateById(updateObj);
    }

    @Override
    public void deleteComplaint(Long id) {
        // 校验存在
        validateComplaintExists(id);
        // 删除
        complaintMapper.deleteById(id);
    }

    @Override
        public void deleteComplaintListByIds(List<Long> ids) {
        // 删除
        complaintMapper.deleteByIds(ids);
        }


    private void validateComplaintExists(Long id) {
        if (complaintMapper.selectById(id) == null) {
            throw exception(COMPLAINT_NOT_EXISTS);
        }
    }

    @Override
    public ComplaintDO getComplaint(Long id) {
        return complaintMapper.selectById(id);
    }

    @Override
    public ComplaintDetailRespVO getComplaintDetail(Long id) {
        ComplaintDO complaint = complaintMapper.selectById(id);
        if (complaint == null) {
            throw exception(COMPLAINT_NOT_EXISTS);
        }
        OrderInfoDO order = complaint.getOrderId() == null ? null : orderInfoMapper.selectById(complaint.getOrderId());
        OrderUnitDO unit = complaint.getUnitId() == null ? null : orderUnitMapper.selectById(complaint.getUnitId());
        MemberUserDO orderUser = order == null || order.getUserId() == null ? null : memberUserMapper.selectById(order.getUserId());
        MemberUserDO complainantUser = complaint.getComplainantUserId() == null ? null : memberUserMapper.selectById(complaint.getComplainantUserId());
        MemberUserDO respondentUser = complaint.getRespondentUserId() == null ? null : memberUserMapper.selectById(complaint.getRespondentUserId());
        MerchantInfoDO respondentMerchant = complaint.getRespondentUserId() == null ? null : merchantInfoMapper.selectOne(
                new LambdaQueryWrapperX<MerchantInfoDO>()
                        .eq(MerchantInfoDO::getUserId, complaint.getRespondentUserId())
                        .last("LIMIT 1"));
        List<ComplaintFileRelDO> fileRels = complaintFileRelMapper.selectListByComplaintId(id);
        List<ComplaintDO> relatedComplaints = complaint.getOrderId() == null ? Collections.emptyList()
                : complaintMapper.selectList(new LambdaQueryWrapperX<ComplaintDO>()
                .eq(ComplaintDO::getOrderId, complaint.getOrderId())
                .orderByDesc(ComplaintDO::getCreateTime, ComplaintDO::getId));
        List<PartnerCoordinationDO> coordinationRecords = partnerCoordinationMapper.selectListByDispute("COMPLAINT", id);
        List<OrderOperateLogDO> operateLogs = complaint.getOrderId() == null ? Collections.emptyList()
                : orderOperateLogMapper.selectList(new LambdaQueryWrapperX<OrderOperateLogDO>()
                .eq(OrderOperateLogDO::getOrderId, complaint.getOrderId())
                .eq(complaint.getUnitId() != null, OrderOperateLogDO::getUnitId, complaint.getUnitId())
                .orderByDesc(OrderOperateLogDO::getOperateTime, OrderOperateLogDO::getId));
        return ComplaintDetailAssembler.build(complaint, order, unit, complainantUser, respondentUser,
                orderUser, respondentMerchant, fileRels, relatedComplaints, coordinationRecords, operateLogs);
    }

    @Override
    public void processComplaint(ComplaintProcessReqVO reqVO) {
        ComplaintDO complaint = complaintMapper.selectById(reqVO.getId());
        if (complaint == null) {
            throw exception(COMPLAINT_NOT_EXISTS);
        }
        ComplaintDO updateObj = new ComplaintDO();
        updateObj.setId(reqVO.getId());
        updateObj.setStatus(reqVO.getStatus());
        updateObj.setResultDesc(reqVO.getResultDesc());
        updateObj.setHandleBy(SecurityFrameworkUtils.getLoginUserId());
        updateObj.setHandleTime(LocalDateTime.now());
        complaintMapper.updateById(updateObj);
        if ("FINISHED".equals(reqVO.getStatus())) {
            MerchantInfoDO merchant = merchantInfoMapper.selectOne(new LambdaQueryWrapperX<MerchantInfoDO>()
                    .eq(MerchantInfoDO::getUserId, complaint.getRespondentUserId())
                    .last("LIMIT 1"));
            creditRecordService.applyCreditRule(complaint.getRespondentUserId(),
                    merchant != null ? merchant.getId() : null,
                    "COMPLAINT_CONFIRMED", "COMPLAINT", complaint.getId(),
                    reqVO.getResultDesc() != null ? reqVO.getResultDesc() : "投诉核实成立");
        }
        dispatchComplaintResult(complaint, reqVO.getResultDesc());
    }

    @Override
    public PageResult<ComplaintRespVO> getComplaintPage(ComplaintPageReqVO pageReqVO) {
        List<Long> complainantUserIds = resolveMatchedUserIds(pageReqVO.getComplainantUserKeyword());
        if (StrUtil.isNotBlank(pageReqVO.getComplainantUserKeyword()) && CollUtil.isEmpty(complainantUserIds)) {
            return PageResult.empty();
        }
        List<Long> respondentUserIds = resolveMatchedUserIds(pageReqVO.getRespondentUserKeyword());
        if (StrUtil.isNotBlank(pageReqVO.getRespondentUserKeyword()) && CollUtil.isEmpty(respondentUserIds)) {
            return PageResult.empty();
        }
        List<Long> matchedOrderIds = resolveMatchedOrderIds(pageReqVO.getOrderNo());
        if (StrUtil.isNotBlank(pageReqVO.getOrderNo()) && CollUtil.isEmpty(matchedOrderIds)) {
            return PageResult.empty();
        }
        List<Long> matchedUnitIds = resolveMatchedUnitIds(pageReqVO.getUnitNo());
        if (StrUtil.isNotBlank(pageReqVO.getUnitNo()) && CollUtil.isEmpty(matchedUnitIds)) {
            return PageResult.empty();
        }
        PageResult<ComplaintDO> pageResult = complaintMapper.selectPage(pageReqVO, matchedOrderIds, matchedUnitIds,
                complainantUserIds, respondentUserIds);
        List<ComplaintRespVO> list = BeanUtils.toBean(pageResult.getList(), ComplaintRespVO.class);
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

    private void fillDisplayInfo(List<ComplaintRespVO> list) {
        Set<Long> userIds = new HashSet<>();
        Set<Long> orderIds = new HashSet<>();
        Set<Long> unitIds = new HashSet<>();
        list.forEach(item -> {
            if (item.getOrderId() != null) {
                orderIds.add(item.getOrderId());
            }
            if (item.getUnitId() != null) {
                unitIds.add(item.getUnitId());
            }
            if (item.getComplainantUserId() != null) {
                userIds.add(item.getComplainantUserId());
            }
            if (item.getRespondentUserId() != null) {
                userIds.add(item.getRespondentUserId());
            }
        });
        Map<Long, OrderInfoDO> orderMap = orderIds.isEmpty() ? Collections.emptyMap()
                : convertMap(orderInfoMapper.selectBatchIds(orderIds), OrderInfoDO::getId);
        Map<Long, OrderUnitDO> unitMap = unitIds.isEmpty() ? Collections.emptyMap()
                : convertMap(orderUnitMapper.selectBatchIds(unitIds), OrderUnitDO::getId);
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
            MemberUserDO complainantUser = userMap.get(item.getComplainantUserId());
            if (complainantUser != null) {
                item.setComplainantUserNo(complainantUser.getUserNo());
                item.setComplainantUserNickname(complainantUser.getNickname());
                item.setComplainantUserMobile(complainantUser.getMobile());
            }
            MemberUserDO respondentUser = userMap.get(item.getRespondentUserId());
            if (respondentUser != null) {
                item.setRespondentUserNo(respondentUser.getUserNo());
                item.setRespondentUserNickname(respondentUser.getNickname());
                item.setRespondentUserMobile(respondentUser.getMobile());
            }
        });
    }

    private void dispatchComplaintCreated(ComplaintDO complaint) {
        if (complaint == null) {
            return;
        }
        if (complaint.getComplainantUserId() != null) {
            messagePushDispatchService.dispatchSingle("DISPUTE_CREATED", "纠纷发起通知", "COMPLAINT",
                    complaint.getId(), complaint.getComplainantUserId(), "投诉已提交，请留意处理进度");
        }
        if (complaint.getRespondentUserId() != null) {
            messagePushDispatchService.dispatchSingle("DISPUTE_CREATED", "纠纷发起通知", "COMPLAINT",
                    complaint.getId(), complaint.getRespondentUserId(), "您收到一条新的投诉，请及时处理");
        }
    }

    private void dispatchComplaintResult(ComplaintDO complaint, String resultDesc) {
        if (complaint == null) {
            return;
        }
        String remark = StrUtil.blankToDefault(resultDesc, "投诉处理结果已更新");
        if (complaint.getComplainantUserId() != null) {
            messagePushDispatchService.dispatchSingle("DISPUTE_RESULT", "纠纷结果通知", "COMPLAINT",
                    complaint.getId(), complaint.getComplainantUserId(), remark);
        }
        if (complaint.getRespondentUserId() != null) {
            messagePushDispatchService.dispatchSingle("DISPUTE_RESULT", "纠纷结果通知", "COMPLAINT",
                    complaint.getId(), complaint.getRespondentUserId(), remark);
        }
    }

}

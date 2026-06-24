package cn.iocoder.yudao.module.linbang.service.dashboard;

import cn.iocoder.yudao.framework.mybatis.core.query.LambdaQueryWrapperX;
import cn.iocoder.yudao.module.linbang.controller.admin.dashboard.vo.DashboardOverviewRespVO;
import cn.iocoder.yudao.module.linbang.controller.admin.dashboard.vo.DashboardTrendPointRespVO;
import cn.iocoder.yudao.module.linbang.dal.dataobject.appeal.AppealDO;
import cn.iocoder.yudao.module.linbang.dal.dataobject.complaint.ComplaintDO;
import cn.iocoder.yudao.module.linbang.dal.dataobject.memberqualification.MemberUserQualificationDO;
import cn.iocoder.yudao.module.linbang.dal.dataobject.memberrealname.MemberUserRealNameDO;
import cn.iocoder.yudao.module.linbang.dal.dataobject.memberroleapply.MemberRoleApplyDO;
import cn.iocoder.yudao.module.linbang.dal.dataobject.memberuser.MemberUserDO;
import cn.iocoder.yudao.module.linbang.dal.dataobject.merchantentry.MerchantEntryDO;
import cn.iocoder.yudao.module.linbang.dal.dataobject.merchantpricereport.MerchantPriceReportDO;
import cn.iocoder.yudao.module.linbang.dal.dataobject.messagepushtask.MessagePushTaskDO;
import cn.iocoder.yudao.module.linbang.dal.dataobject.orderabnormal.OrderAbnormalDO;
import cn.iocoder.yudao.module.linbang.dal.dataobject.orderinfo.OrderInfoDO;
import cn.iocoder.yudao.module.linbang.dal.dataobject.riskevent.RiskEventDO;
import cn.iocoder.yudao.module.linbang.dal.dataobject.walletwithdraw.WalletWithdrawDO;
import cn.iocoder.yudao.module.linbang.dal.mysql.appeal.AppealMapper;
import cn.iocoder.yudao.module.linbang.dal.mysql.complaint.ComplaintMapper;
import cn.iocoder.yudao.module.linbang.dal.mysql.memberqualification.MemberUserQualificationMapper;
import cn.iocoder.yudao.module.linbang.dal.mysql.memberrealname.MemberUserRealNameMapper;
import cn.iocoder.yudao.module.linbang.dal.mysql.memberroleapply.MemberRoleApplyMapper;
import cn.iocoder.yudao.module.linbang.dal.mysql.memberuser.MemberUserMapper;
import cn.iocoder.yudao.module.linbang.dal.mysql.merchantentry.MerchantEntryMapper;
import cn.iocoder.yudao.module.linbang.dal.mysql.merchantpricereport.MerchantPriceReportMapper;
import cn.iocoder.yudao.module.linbang.dal.mysql.messagepushtask.MessagePushTaskMapper;
import cn.iocoder.yudao.module.linbang.dal.mysql.orderabnormal.OrderAbnormalMapper;
import cn.iocoder.yudao.module.linbang.dal.mysql.orderinfo.OrderInfoMapper;
import cn.iocoder.yudao.module.linbang.dal.mysql.riskevent.RiskEventMapper;
import cn.iocoder.yudao.module.linbang.dal.mysql.walletwithdraw.WalletWithdrawMapper;
import org.springframework.stereotype.Service;
import org.springframework.validation.annotation.Validated;

import javax.annotation.Resource;
import java.math.BigDecimal;
import java.math.RoundingMode;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

@Service
@Validated
public class DashboardServiceImpl implements DashboardService {

    @Resource
    private OrderInfoMapper orderInfoMapper;
    @Resource
    private MemberUserMapper memberUserMapper;
    @Resource
    private MemberUserRealNameMapper memberUserRealNameMapper;
    @Resource
    private MemberUserQualificationMapper memberQualificationMapper;
    @Resource
    private MemberRoleApplyMapper memberRoleApplyMapper;
    @Resource
    private MerchantEntryMapper merchantEntryMapper;
    @Resource
    private MerchantPriceReportMapper merchantPriceReportMapper;
    @Resource
    private MessagePushTaskMapper messagePushTaskMapper;
    @Resource
    private WalletWithdrawMapper walletWithdrawMapper;
    @Resource
    private OrderAbnormalMapper orderAbnormalMapper;
    @Resource
    private RiskEventMapper riskEventMapper;
    @Resource
    private ComplaintMapper complaintMapper;
    @Resource
    private AppealMapper appealMapper;

    @Override
    public DashboardOverviewRespVO getOverview() {
        LocalDateTime start = LocalDate.now().atStartOfDay();
        LocalDateTime end = start.plusDays(1);

        long todayOrderCount = orderInfoMapper.selectCount(new LambdaQueryWrapperX<OrderInfoDO>()
                .ge(OrderInfoDO::getCreateTime, start)
                .lt(OrderInfoDO::getCreateTime, end));
        long todayNewUserCount = memberUserMapper.selectCount(new LambdaQueryWrapperX<MemberUserDO>()
                .ge(MemberUserDO::getCreateTime, start)
                .lt(MemberUserDO::getCreateTime, end));
        BigDecimal todayTradeAmount = sumOrderAmount(start, end);
        long totalOrderCount = orderInfoMapper.selectCount();
        long finishedOrderCount = orderInfoMapper.selectCount(new LambdaQueryWrapperX<OrderInfoDO>()
                .eq(OrderInfoDO::getStatus, "FINISHED"));
        BigDecimal completionRate = totalOrderCount == 0 ? BigDecimal.ZERO
                : BigDecimal.valueOf(finishedOrderCount)
                .multiply(BigDecimal.valueOf(100))
                .divide(BigDecimal.valueOf(totalOrderCount), 2, RoundingMode.HALF_UP);

        long pendingAuditCount = memberUserRealNameMapper.selectCount(new LambdaQueryWrapperX<MemberUserRealNameDO>()
                .eq(MemberUserRealNameDO::getAuditStatus, "PENDING"))
                + memberQualificationMapper.selectCount(new LambdaQueryWrapperX<MemberUserQualificationDO>()
                .eq(MemberUserQualificationDO::getAuditStatus, "PENDING"))
                + memberRoleApplyMapper.selectCount(new LambdaQueryWrapperX<MemberRoleApplyDO>()
                .eq(MemberRoleApplyDO::getAuditStatus, "PENDING"))
                + merchantEntryMapper.selectCount(new LambdaQueryWrapperX<MerchantEntryDO>()
                .eq(MerchantEntryDO::getStatus, "PENDING"))
                + merchantPriceReportMapper.selectCount(new LambdaQueryWrapperX<MerchantPriceReportDO>()
                .eq(MerchantPriceReportDO::getAuditStatus, "PENDING"))
                + walletWithdrawMapper.selectCount(new LambdaQueryWrapperX<WalletWithdrawDO>()
                .eq(WalletWithdrawDO::getAuditStatus, "PENDING"));
        long pendingRoleApplyCount = memberRoleApplyMapper.selectCount(new LambdaQueryWrapperX<MemberRoleApplyDO>()
                .eq(MemberRoleApplyDO::getAuditStatus, "PENDING"));
        long expiringQualificationCount = memberQualificationMapper.selectCount(new LambdaQueryWrapperX<MemberUserQualificationDO>()
                .eq(MemberUserQualificationDO::getAuditStatus, "APPROVED")
                .between(MemberUserQualificationDO::getValidEndDate, LocalDate.now(), LocalDate.now().plusDays(7)));
        long pendingPriceReportCount = merchantPriceReportMapper.selectCount(new LambdaQueryWrapperX<MerchantPriceReportDO>()
                .eq(MerchantPriceReportDO::getAuditStatus, "PENDING"));
        long pendingPushTaskCount = messagePushTaskMapper.selectCount(new LambdaQueryWrapperX<MessagePushTaskDO>()
                .in(MessagePushTaskDO::getStatus, "PENDING", "PROCESSING"));
        long failedPushTaskCount = messagePushTaskMapper.selectCount(new LambdaQueryWrapperX<MessagePushTaskDO>()
                .in(MessagePushTaskDO::getStatus, "FAILED", "PARTIAL_FAILED"));

        long abnormalOrderCount = orderAbnormalMapper.selectCount(new LambdaQueryWrapperX<OrderAbnormalDO>()
                .ne(OrderAbnormalDO::getHandleStatus, "FINISHED"));
        long riskAlertCount = riskEventMapper.selectCount(new LambdaQueryWrapperX<RiskEventDO>()
                .ne(RiskEventDO::getStatus, "FINISHED"));
        long refundPendingCount = complaintMapper.selectCount(new LambdaQueryWrapperX<ComplaintDO>()
                .eq(ComplaintDO::getStatus, "PENDING"))
                + appealMapper.selectCount(new LambdaQueryWrapperX<AppealDO>()
                .eq(AppealDO::getAuditStatus, "PENDING"));

        DashboardOverviewRespVO respVO = new DashboardOverviewRespVO();
        respVO.setTodayOrderCount(todayOrderCount);
        respVO.setTodayTradeAmount(todayTradeAmount);
        respVO.setTodayNewUserCount(todayNewUserCount);
        respVO.setCompletionRate(completionRate);
        respVO.setPendingAuditCount(pendingAuditCount);
        respVO.setPendingRoleApplyCount(pendingRoleApplyCount);
        respVO.setExpiringQualificationCount(expiringQualificationCount);
        respVO.setPendingPriceReportCount(pendingPriceReportCount);
        respVO.setPendingPushTaskCount(pendingPushTaskCount);
        respVO.setFailedPushTaskCount(failedPushTaskCount);
        respVO.setAbnormalOrderCount(abnormalOrderCount);
        respVO.setRiskAlertCount(riskAlertCount);
        respVO.setRefundPendingCount(refundPendingCount);
        return respVO;
    }

    @Override
    public List<DashboardTrendPointRespVO> getOrderStat() {
        return buildTrendPoints();
    }

    @Override
    public List<DashboardTrendPointRespVO> getFinanceStat() {
        return buildTrendPoints();
    }

    @Override
    public List<DashboardTrendPointRespVO> getUserStat() {
        return buildTrendPoints();
    }

    private List<DashboardTrendPointRespVO> buildTrendPoints() {
        List<DashboardTrendPointRespVO> list = new ArrayList<>();
        for (int i = 6; i >= 0; i--) {
            LocalDate date = LocalDate.now().minusDays(i);
            LocalDateTime start = date.atStartOfDay();
            LocalDateTime end = start.plusDays(1);
            long orderCount = orderInfoMapper.selectCount(new LambdaQueryWrapperX<OrderInfoDO>()
                    .ge(OrderInfoDO::getCreateTime, start)
                    .lt(OrderInfoDO::getCreateTime, end));
            long newUserCount = memberUserMapper.selectCount(new LambdaQueryWrapperX<MemberUserDO>()
                    .ge(MemberUserDO::getCreateTime, start)
                    .lt(MemberUserDO::getCreateTime, end));
            BigDecimal tradeAmount = sumOrderAmount(start, end);
            BigDecimal withdrawAmount = sumWithdrawAmount(start, end);
            list.add(DashboardTrendPointRespVO.builder()
                    .statDate(date.toString())
                    .orderCount(orderCount)
                    .tradeAmount(tradeAmount)
                    .newUserCount(newUserCount)
                    .withdrawAmount(withdrawAmount)
                    .build());
        }
        return list;
    }

    private BigDecimal sumOrderAmount(LocalDateTime start, LocalDateTime end) {
        return orderInfoMapper.selectList(new LambdaQueryWrapperX<OrderInfoDO>()
                        .ge(OrderInfoDO::getCreateTime, start)
                        .lt(OrderInfoDO::getCreateTime, end))
                .stream()
                .map(OrderInfoDO::getOrderAmount)
                .filter(item -> item != null)
                .reduce(BigDecimal.ZERO, BigDecimal::add);
    }

    private BigDecimal sumWithdrawAmount(LocalDateTime start, LocalDateTime end) {
        return walletWithdrawMapper.selectList(new LambdaQueryWrapperX<WalletWithdrawDO>()
                        .ge(WalletWithdrawDO::getCreateTime, start)
                        .lt(WalletWithdrawDO::getCreateTime, end))
                .stream()
                .map(WalletWithdrawDO::getApplyAmount)
                .filter(item -> item != null)
                .reduce(BigDecimal.ZERO, BigDecimal::add);
    }
}

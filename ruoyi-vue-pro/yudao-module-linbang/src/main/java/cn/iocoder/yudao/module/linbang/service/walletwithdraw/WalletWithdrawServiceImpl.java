package cn.iocoder.yudao.module.linbang.service.walletwithdraw;

import cn.hutool.core.collection.CollUtil;
import cn.hutool.core.util.StrUtil;
import cn.iocoder.yudao.framework.common.util.object.BeanUtils;
import cn.iocoder.yudao.framework.mybatis.core.query.LambdaQueryWrapperX;
import cn.iocoder.yudao.framework.security.core.util.SecurityFrameworkUtils;
import cn.iocoder.yudao.module.linbang.controller.admin.walletwithdraw.vo.WalletWithdrawDetailRespVO;
import cn.iocoder.yudao.module.linbang.dal.dataobject.memberuser.MemberUserDO;
import cn.iocoder.yudao.module.linbang.dal.dataobject.walletaccount.WalletAccountDO;
import cn.iocoder.yudao.module.linbang.dal.dataobject.walletbankcard.WalletBankCardDO;
import cn.iocoder.yudao.module.linbang.dal.dataobject.walletflow.WalletFlowDO;
import org.springframework.stereotype.Service;
import javax.annotation.Resource;
import org.springframework.validation.annotation.Validated;

import java.util.*;
import java.time.LocalDateTime;
import cn.iocoder.yudao.module.linbang.controller.admin.walletwithdraw.vo.*;
import cn.iocoder.yudao.module.linbang.dal.dataobject.walletwithdraw.WalletWithdrawDO;
import cn.iocoder.yudao.framework.common.pojo.PageResult;
import cn.iocoder.yudao.framework.common.pojo.PageParam;

import cn.iocoder.yudao.module.linbang.dal.mysql.walletwithdraw.WalletWithdrawMapper;
import cn.iocoder.yudao.module.linbang.dal.mysql.walletaccount.WalletAccountMapper;
import cn.iocoder.yudao.module.linbang.dal.mysql.walletbankcard.WalletBankCardMapper;
import cn.iocoder.yudao.module.linbang.dal.mysql.walletflow.WalletFlowMapper;
import cn.iocoder.yudao.module.linbang.dal.mysql.memberuser.MemberUserMapper;
import cn.iocoder.yudao.module.linbang.service.messagepushtask.MessagePushDispatchService;
import org.springframework.transaction.annotation.Transactional;

import static cn.iocoder.yudao.framework.common.exception.util.ServiceExceptionUtil.exception;
import static cn.iocoder.yudao.framework.common.util.collection.CollectionUtils.convertList;
import static cn.iocoder.yudao.framework.common.util.collection.CollectionUtils.convertMap;
import static cn.iocoder.yudao.framework.common.util.collection.CollectionUtils.convertSet;
import static cn.iocoder.yudao.module.linbang.enums.ErrorCodeConstants.*;

/**
 * 提现申请 Service 实现类
 *
 * @author dawn
 */
@Service
@Validated
public class WalletWithdrawServiceImpl implements WalletWithdrawService {

    @Resource
    private WalletWithdrawMapper walletWithdrawMapper;
    @Resource
    private WalletAccountMapper walletAccountMapper;
    @Resource
    private WalletBankCardMapper walletBankCardMapper;
    @Resource
    private WalletFlowMapper walletFlowMapper;
    @Resource
    private MemberUserMapper memberUserMapper;
    @Resource
    private MessagePushDispatchService messagePushDispatchService;

    @Override
    public Long createWalletWithdraw(WalletWithdrawSaveReqVO createReqVO) {
        // 插入
        WalletWithdrawDO walletWithdraw = BeanUtils.toBean(createReqVO, WalletWithdrawDO.class);
        walletWithdrawMapper.insert(walletWithdraw);

        // 返回
        return walletWithdraw.getId();
    }

    @Override
    public void updateWalletWithdraw(WalletWithdrawSaveReqVO updateReqVO) {
        // 校验存在
        validateWalletWithdrawExists(updateReqVO.getId());
        // 更新
        WalletWithdrawDO updateObj = BeanUtils.toBean(updateReqVO, WalletWithdrawDO.class);
        walletWithdrawMapper.updateById(updateObj);
    }

    @Override
    public void deleteWalletWithdraw(Long id) {
        // 校验存在
        validateWalletWithdrawExists(id);
        // 删除
        walletWithdrawMapper.deleteById(id);
    }

    @Override
        public void deleteWalletWithdrawListByIds(List<Long> ids) {
        // 删除
        walletWithdrawMapper.deleteByIds(ids);
        }


    private void validateWalletWithdrawExists(Long id) {
        if (walletWithdrawMapper.selectById(id) == null) {
            throw exception(WALLET_WITHDRAW_NOT_EXISTS);
        }
    }

    @Override
    public WalletWithdrawDO getWalletWithdraw(Long id) {
        return walletWithdrawMapper.selectById(id);
    }

    @Override
    public WalletWithdrawDetailRespVO getWalletWithdrawDetail(Long id) {
        WalletWithdrawDO withdraw = walletWithdrawMapper.selectById(id);
        if (withdraw == null) {
            throw exception(WALLET_WITHDRAW_NOT_EXISTS);
        }
        MemberUserDO user = withdraw.getUserId() == null ? null : memberUserMapper.selectById(withdraw.getUserId());
        WalletAccountDO walletAccount = withdraw.getWalletAccountId() == null ? null : walletAccountMapper.selectById(withdraw.getWalletAccountId());
        WalletBankCardDO bankCard = withdraw.getBankCardId() == null ? null : walletBankCardMapper.selectById(withdraw.getBankCardId());
        List<WalletFlowDO> flows = walletFlowMapper.selectList(new LambdaQueryWrapperX<WalletFlowDO>()
                .eq(WalletFlowDO::getWalletAccountId, withdraw.getWalletAccountId())
                .eq(WalletFlowDO::getUserId, withdraw.getUserId())
                .or(wrapper -> wrapper.like(WalletFlowDO::getRemark, withdraw.getWithdrawNo()))
                .orderByDesc(WalletFlowDO::getCreateTime, WalletFlowDO::getId)
                .last("LIMIT 10"));

        WalletWithdrawDetailRespVO respVO = BeanUtils.toBean(withdraw, WalletWithdrawDetailRespVO.class);
        respVO.setUser(WalletWithdrawDetailAssembler.buildUser(user));
        respVO.setWalletAccount(WalletWithdrawDetailAssembler.buildWalletAccount(walletAccount));
        respVO.setBankCard(WalletWithdrawDetailAssembler.buildBankCard(bankCard));
        respVO.setRelatedFlows(WalletWithdrawDetailAssembler.buildFlows(flows));
        return respVO;
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public void auditWalletWithdraw(WithdrawAuditReqVO reqVO) {
        WalletWithdrawDO walletWithdraw = walletWithdrawMapper.selectById(reqVO.getId());
        if (walletWithdraw == null) {
            throw exception(WALLET_WITHDRAW_NOT_EXISTS);
        }
        WalletWithdrawDO updateObj = new WalletWithdrawDO();
        updateObj.setId(reqVO.getId());
        updateObj.setAuditStatus(reqVO.getAuditStatus());
        updateObj.setAuditRemark(reqVO.getAuditRemark());
        updateObj.setRejectReason(reqVO.getRejectReason());
        updateObj.setAuditBy(SecurityFrameworkUtils.getLoginUserId());
        updateObj.setAuditTime(LocalDateTime.now());
        if ("APPROVED".equals(reqVO.getAuditStatus())) {
            updateObj.setStatus("APPROVED");
        } else if ("REJECTED".equals(reqVO.getAuditStatus())) {
            updateObj.setStatus("REJECTED");
        }
        walletWithdrawMapper.updateById(updateObj);
        messagePushDispatchService.dispatchSingle("lb_withdraw_audited", "提现审核结果通知", "WITHDRAW",
                walletWithdraw.getId(), walletWithdraw.getUserId(), "管理员审核提现后自动通知申请人");
    }

    @Override
    public PageResult<WalletWithdrawRespVO> getWalletWithdrawPage(WalletWithdrawPageReqVO pageReqVO) {
        List<Long> matchedUserIds = resolveMatchedUserIds(pageReqVO.getUserKeyword());
        if (StrUtil.isNotBlank(pageReqVO.getUserKeyword()) && CollUtil.isEmpty(matchedUserIds)) {
            return PageResult.empty();
        }
        PageResult<WalletWithdrawDO> pageResult = walletWithdrawMapper.selectPage(pageReqVO, matchedUserIds);
        List<WalletWithdrawRespVO> list = BeanUtils.toBean(pageResult.getList(), WalletWithdrawRespVO.class);
        fillUserDisplayInfo(list);
        return new PageResult<>(list, pageResult.getTotal());
    }

    private List<Long> resolveMatchedUserIds(String userKeyword) {
        if (StrUtil.isBlank(userKeyword)) {
            return null;
        }
        return convertList(memberUserMapper.selectListByKeyword(userKeyword), MemberUserDO::getId);
    }

    private void fillUserDisplayInfo(List<WalletWithdrawRespVO> list) {
        Set<Long> userIds = convertSet(list, WalletWithdrawRespVO::getUserId,
                item -> item.getUserId() != null);
        Map<Long, MemberUserDO> userMap = userIds.isEmpty() ? Collections.emptyMap()
                : convertMap(memberUserMapper.selectListByIds(userIds), MemberUserDO::getId);
        Set<Long> walletAccountIds = convertSet(list, WalletWithdrawRespVO::getWalletAccountId,
                item -> item.getWalletAccountId() != null);
        Map<Long, WalletAccountDO> walletAccountMap = walletAccountIds.isEmpty() ? Collections.emptyMap()
                : convertMap(walletAccountMapper.selectBatchIds(walletAccountIds), WalletAccountDO::getId);
        Set<Long> bankCardIds = convertSet(list, WalletWithdrawRespVO::getBankCardId,
                item -> item.getBankCardId() != null);
        Map<Long, WalletBankCardDO> bankCardMap = bankCardIds.isEmpty() ? Collections.emptyMap()
                : convertMap(walletBankCardMapper.selectBatchIds(bankCardIds), WalletBankCardDO::getId);
        list.forEach(item -> {
            MemberUserDO user = userMap.get(item.getUserId());
            if (user != null) {
                item.setUserNo(user.getUserNo());
                item.setUserNickname(user.getNickname());
                item.setUserMobile(user.getMobile());
            }
            WalletAccountDO walletAccount = walletAccountMap.get(item.getWalletAccountId());
            if (walletAccount != null) {
                item.setWalletRoleCode(walletAccount.getRoleCode());
                item.setWalletStatus(walletAccount.getStatus());
            }
            WalletBankCardDO bankCard = bankCardMap.get(item.getBankCardId());
            if (bankCard != null) {
                item.setBankName(bankCard.getBankName());
                item.setCardNoMask(bankCard.getCardNoMask());
                item.setBankAccountName(bankCard.getAccountName());
            }
        });
    }

}

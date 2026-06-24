package cn.iocoder.yudao.module.linbang.service.walletaccount;

import cn.hutool.core.collection.CollUtil;
import cn.hutool.core.util.StrUtil;
import cn.iocoder.yudao.framework.common.util.object.BeanUtils;
import cn.iocoder.yudao.framework.mybatis.core.query.LambdaQueryWrapperX;
import cn.iocoder.yudao.module.linbang.dal.dataobject.memberuser.MemberUserDO;
import cn.iocoder.yudao.module.linbang.dal.dataobject.walletbankcard.WalletBankCardDO;
import cn.iocoder.yudao.module.linbang.dal.dataobject.walletflow.WalletFlowDO;
import cn.iocoder.yudao.module.linbang.dal.dataobject.walletwithdraw.WalletWithdrawDO;
import cn.iocoder.yudao.module.linbang.dal.mysql.memberuser.MemberUserMapper;
import cn.iocoder.yudao.module.linbang.dal.mysql.walletbankcard.WalletBankCardMapper;
import cn.iocoder.yudao.module.linbang.dal.mysql.walletflow.WalletFlowMapper;
import cn.iocoder.yudao.module.linbang.dal.mysql.walletwithdraw.WalletWithdrawMapper;
import org.springframework.stereotype.Service;
import javax.annotation.Resource;
import org.springframework.validation.annotation.Validated;

import java.util.*;
import cn.iocoder.yudao.module.linbang.controller.admin.walletaccount.vo.*;
import cn.iocoder.yudao.module.linbang.dal.dataobject.walletaccount.WalletAccountDO;
import cn.iocoder.yudao.framework.common.pojo.PageResult;

import cn.iocoder.yudao.module.linbang.dal.mysql.walletaccount.WalletAccountMapper;

import static cn.iocoder.yudao.framework.common.exception.util.ServiceExceptionUtil.exception;
import static cn.iocoder.yudao.framework.common.util.collection.CollectionUtils.convertList;
import static cn.iocoder.yudao.framework.common.util.collection.CollectionUtils.convertMap;
import static cn.iocoder.yudao.framework.common.util.collection.CollectionUtils.convertSet;
import static cn.iocoder.yudao.module.linbang.enums.ErrorCodeConstants.*;

/**
 * 钱包账户 Service 实现类
 *
 * @author dawn
 */
@Service
@Validated
public class WalletAccountServiceImpl implements WalletAccountService {

    @Resource
    private WalletAccountMapper walletAccountMapper;
    @Resource
    private WalletBankCardMapper walletBankCardMapper;
    @Resource
    private WalletFlowMapper walletFlowMapper;
    @Resource
    private WalletWithdrawMapper walletWithdrawMapper;
    @Resource
    private MemberUserMapper memberUserMapper;

    @Override
    public Long createWalletAccount(WalletAccountSaveReqVO createReqVO) {
        // 插入
        WalletAccountDO walletAccount = BeanUtils.toBean(createReqVO, WalletAccountDO.class);
        walletAccountMapper.insert(walletAccount);

        // 返回
        return walletAccount.getId();
    }

    @Override
    public void updateWalletAccount(WalletAccountSaveReqVO updateReqVO) {
        // 校验存在
        validateWalletAccountExists(updateReqVO.getId());
        // 更新
        WalletAccountDO updateObj = BeanUtils.toBean(updateReqVO, WalletAccountDO.class);
        walletAccountMapper.updateById(updateObj);
    }

    @Override
    public void deleteWalletAccount(Long id) {
        // 校验存在
        validateWalletAccountExists(id);
        // 删除
        walletAccountMapper.deleteById(id);
    }

    @Override
        public void deleteWalletAccountListByIds(List<Long> ids) {
        // 删除
        walletAccountMapper.deleteByIds(ids);
        }


    private void validateWalletAccountExists(Long id) {
        if (walletAccountMapper.selectById(id) == null) {
            throw exception(WALLET_ACCOUNT_NOT_EXISTS);
        }
    }

    @Override
    public WalletAccountDO getWalletAccount(Long id) {
        return walletAccountMapper.selectById(id);
    }

    @Override
    public WalletAccountDetailRespVO getWalletAccountDetail(Long id) {
        WalletAccountDO walletAccount = walletAccountMapper.selectById(id);
        if (walletAccount == null) {
            throw exception(WALLET_ACCOUNT_NOT_EXISTS);
        }
        MemberUserDO user = walletAccount.getUserId() == null ? null : memberUserMapper.selectById(walletAccount.getUserId());
        WalletBankCardDO defaultBankCard = walletBankCardMapper.selectOne(new LambdaQueryWrapperX<WalletBankCardDO>()
                .eq(WalletBankCardDO::getUserId, walletAccount.getUserId())
                .eq(WalletBankCardDO::getIsDefault, true)
                .orderByDesc(WalletBankCardDO::getId)
                .last("LIMIT 1"));
        if (defaultBankCard == null) {
            defaultBankCard = walletBankCardMapper.selectOne(new LambdaQueryWrapperX<WalletBankCardDO>()
                    .eq(WalletBankCardDO::getUserId, walletAccount.getUserId())
                    .orderByDesc(WalletBankCardDO::getIsDefault, WalletBankCardDO::getId)
                    .last("LIMIT 1"));
        }
        List<WalletFlowDO> recentFlows = walletFlowMapper.selectList(new LambdaQueryWrapperX<WalletFlowDO>()
                .eq(WalletFlowDO::getWalletAccountId, walletAccount.getId())
                .orderByDesc(WalletFlowDO::getCreateTime, WalletFlowDO::getId)
                .last("LIMIT 10"));
        List<WalletWithdrawDO> recentWithdraws = walletWithdrawMapper.selectList(new LambdaQueryWrapperX<WalletWithdrawDO>()
                .eq(WalletWithdrawDO::getWalletAccountId, walletAccount.getId())
                .orderByDesc(WalletWithdrawDO::getCreateTime, WalletWithdrawDO::getId)
                .last("LIMIT 10"));

        WalletAccountDetailRespVO respVO = WalletAccountDetailAssembler.buildDetail(walletAccount);
        respVO.setUser(WalletAccountDetailAssembler.buildUser(user));
        respVO.setDefaultBankCard(WalletAccountDetailAssembler.buildBankCard(defaultBankCard));
        respVO.setRecentFlows(WalletAccountDetailAssembler.buildFlows(recentFlows));
        respVO.setRecentWithdraws(WalletAccountDetailAssembler.buildWithdraws(recentWithdraws));
        respVO.setWithdrawStats(WalletAccountDetailAssembler.buildWithdrawStats(recentWithdraws));
        return respVO;
    }

    @Override
    public PageResult<WalletAccountRespVO> getWalletAccountPage(WalletAccountPageReqVO pageReqVO) {
        List<Long> matchedUserIds = resolveMatchedUserIds(pageReqVO.getUserKeyword());
        if (StrUtil.isNotBlank(pageReqVO.getUserKeyword()) && CollUtil.isEmpty(matchedUserIds)) {
            return PageResult.empty();
        }
        PageResult<WalletAccountDO> pageResult = walletAccountMapper.selectPage(pageReqVO, matchedUserIds);
        List<WalletAccountRespVO> list = BeanUtils.toBean(pageResult.getList(), WalletAccountRespVO.class);
        fillUserDisplayInfo(list);
        return new PageResult<>(list, pageResult.getTotal());
    }

    private List<Long> resolveMatchedUserIds(String userKeyword) {
        if (StrUtil.isBlank(userKeyword)) {
            return null;
        }
        return convertList(memberUserMapper.selectListByKeyword(userKeyword), MemberUserDO::getId);
    }

    private void fillUserDisplayInfo(List<WalletAccountRespVO> list) {
        Set<Long> userIds = convertSet(list, WalletAccountRespVO::getUserId,
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

}

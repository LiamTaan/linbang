package cn.iocoder.yudao.module.linbang.service.walletbankcard;

import cn.hutool.core.collection.CollUtil;
import cn.hutool.core.util.StrUtil;
import cn.iocoder.yudao.framework.common.pojo.PageResult;
import cn.iocoder.yudao.framework.common.util.object.BeanUtils;
import cn.iocoder.yudao.framework.mybatis.core.query.LambdaQueryWrapperX;
import cn.iocoder.yudao.module.linbang.controller.admin.walletbankcard.vo.WalletBankCardDetailRespVO;
import cn.iocoder.yudao.module.linbang.controller.admin.walletbankcard.vo.WalletBankCardPageReqVO;
import cn.iocoder.yudao.module.linbang.controller.admin.walletbankcard.vo.WalletBankCardRespVO;
import cn.iocoder.yudao.module.linbang.controller.admin.walletbankcard.vo.WalletBankCardSaveReqVO;
import cn.iocoder.yudao.module.linbang.dal.dataobject.memberuser.MemberUserDO;
import cn.iocoder.yudao.module.linbang.dal.dataobject.walletaccount.WalletAccountDO;
import cn.iocoder.yudao.module.linbang.dal.dataobject.walletbankcard.WalletBankCardDO;
import cn.iocoder.yudao.module.linbang.dal.dataobject.walletwithdraw.WalletWithdrawDO;
import cn.iocoder.yudao.module.linbang.dal.mysql.memberuser.MemberUserMapper;
import cn.iocoder.yudao.module.linbang.dal.mysql.walletaccount.WalletAccountMapper;
import cn.iocoder.yudao.module.linbang.dal.mysql.walletbankcard.WalletBankCardMapper;
import cn.iocoder.yudao.module.linbang.dal.mysql.walletwithdraw.WalletWithdrawMapper;
import org.springframework.stereotype.Service;
import org.springframework.validation.annotation.Validated;

import javax.annotation.Resource;
import java.util.*;

import static cn.iocoder.yudao.framework.common.exception.util.ServiceExceptionUtil.exception;
import static cn.iocoder.yudao.framework.common.util.collection.CollectionUtils.convertList;
import static cn.iocoder.yudao.framework.common.util.collection.CollectionUtils.convertMap;
import static cn.iocoder.yudao.framework.common.util.collection.CollectionUtils.convertSet;
import static cn.iocoder.yudao.module.linbang.enums.ErrorCodeConstants.*;

/**
 * 用户银行卡 Service 实现类
 *
 * @author dawn
 */
@Service
@Validated
public class WalletBankCardServiceImpl implements WalletBankCardService {

    @Resource
    private WalletBankCardMapper walletBankCardMapper;
    @Resource
    private WalletAccountMapper walletAccountMapper;
    @Resource
    private WalletWithdrawMapper walletWithdrawMapper;
    @Resource
    private MemberUserMapper memberUserMapper;

    @Override
    public Long createWalletBankCard(WalletBankCardSaveReqVO createReqVO) {
        // 插入
        WalletBankCardDO walletBankCard = BeanUtils.toBean(createReqVO, WalletBankCardDO.class);
        walletBankCardMapper.insert(walletBankCard);

        // 返回
        return walletBankCard.getId();
    }

    @Override
    public void updateWalletBankCard(WalletBankCardSaveReqVO updateReqVO) {
        // 校验存在
        validateWalletBankCardExists(updateReqVO.getId());
        // 更新
        WalletBankCardDO updateObj = BeanUtils.toBean(updateReqVO, WalletBankCardDO.class);
        walletBankCardMapper.updateById(updateObj);
    }

    @Override
    public void deleteWalletBankCard(Long id) {
        // 校验存在
        validateWalletBankCardExists(id);
        // 删除
        walletBankCardMapper.deleteById(id);
    }

    @Override
        public void deleteWalletBankCardListByIds(List<Long> ids) {
        // 删除
        walletBankCardMapper.deleteByIds(ids);
        }


    private void validateWalletBankCardExists(Long id) {
        if (walletBankCardMapper.selectById(id) == null) {
            throw exception(WALLET_BANK_CARD_NOT_EXISTS);
        }
    }

    @Override
    public WalletBankCardDO getWalletBankCard(Long id) {
        return walletBankCardMapper.selectById(id);
    }

    @Override
    public WalletBankCardDetailRespVO getWalletBankCardDetail(Long id) {
        WalletBankCardDO walletBankCard = walletBankCardMapper.selectById(id);
        if (walletBankCard == null) {
            throw exception(WALLET_BANK_CARD_NOT_EXISTS);
        }
        MemberUserDO user = walletBankCard.getUserId() == null ? null : memberUserMapper.selectById(walletBankCard.getUserId());
        List<WalletAccountDO> walletAccounts = walletAccountMapper.selectList(new LambdaQueryWrapperX<WalletAccountDO>()
                .eq(WalletAccountDO::getUserId, walletBankCard.getUserId())
                .orderByDesc(WalletAccountDO::getId));
        List<WalletWithdrawDO> recentWithdraws = walletWithdrawMapper.selectList(new LambdaQueryWrapperX<WalletWithdrawDO>()
                .eq(WalletWithdrawDO::getBankCardId, walletBankCard.getId())
                .orderByDesc(WalletWithdrawDO::getCreateTime, WalletWithdrawDO::getId)
                .last("LIMIT 10"));

        WalletBankCardDetailRespVO respVO = WalletBankCardDetailAssembler.buildDetail(walletBankCard, user);
        respVO.setWalletAccounts(WalletBankCardDetailAssembler.buildWalletAccounts(walletAccounts));
        respVO.setRecentWithdraws(WalletBankCardDetailAssembler.buildWithdraws(recentWithdraws));
        respVO.setWithdrawStats(WalletBankCardDetailAssembler.buildWithdrawStats(recentWithdraws));
        return respVO;
    }

    @Override
    public PageResult<WalletBankCardRespVO> getWalletBankCardPage(WalletBankCardPageReqVO pageReqVO) {
        List<Long> matchedUserIds = resolveMatchedUserIds(pageReqVO.getUserKeyword());
        if (StrUtil.isNotBlank(pageReqVO.getUserKeyword()) && CollUtil.isEmpty(matchedUserIds)) {
            return PageResult.empty();
        }
        PageResult<WalletBankCardDO> pageResult = walletBankCardMapper.selectPage(pageReqVO, matchedUserIds);
        List<WalletBankCardRespVO> list = BeanUtils.toBean(pageResult.getList(), WalletBankCardRespVO.class);
        fillUserDisplayInfo(list);
        return new PageResult<>(list, pageResult.getTotal());
    }

    private List<Long> resolveMatchedUserIds(String userKeyword) {
        if (StrUtil.isBlank(userKeyword)) {
            return null;
        }
        return convertList(memberUserMapper.selectListByKeyword(userKeyword), MemberUserDO::getId);
    }

    private void fillUserDisplayInfo(List<WalletBankCardRespVO> list) {
        Set<Long> userIds = convertSet(list, WalletBankCardRespVO::getUserId,
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

package cn.iocoder.yudao.module.linbang.service.creditrecord;

import cn.iocoder.yudao.framework.common.pojo.PageResult;
import cn.iocoder.yudao.module.linbang.controller.app.review.vo.AppCreditRecordDetailRespVO;
import cn.iocoder.yudao.module.linbang.controller.app.review.vo.AppCreditRecordPageReqVO;
import cn.iocoder.yudao.module.linbang.controller.app.review.vo.AppCreditRecordRespVO;
import cn.iocoder.yudao.module.linbang.controller.admin.creditrecord.vo.CreditRecordDetailRespVO;
import cn.iocoder.yudao.module.linbang.controller.admin.creditrecord.vo.CreditRecordPageReqVO;
import cn.iocoder.yudao.module.linbang.controller.admin.creditrecord.vo.CreditRecordRespVO;

public interface CreditRecordService {

    /**
     * 应用信用规则并记录变动
     *
     * @param userId 用户ID
     * @param merchantId 服务商ID
     * @param ruleCode 规则编码
     * @param bizType 业务类型
     * @param bizId 业务ID
     * @param remark 备注
     * @return 记录ID，无命中规则时返回 null
     */
    Long applyCreditRule(Long userId, Long merchantId, String ruleCode, String bizType, Long bizId, String remark);

    /**
     * 删除指定业务对应的信用记录，并按剩余记录重建该用户的信用分链路。
     *
     * @param userId 用户ID
     * @param bizType 业务类型
     * @param bizId 业务ID
     */
    void rollbackBizCreditRecords(Long userId, String bizType, Long bizId);

    /**
     * 获得信用记录分页
     *
     * @param pageReqVO 分页查询
     * @return 信用记录分页
     */
    PageResult<CreditRecordRespVO> getCreditRecordPage(CreditRecordPageReqVO pageReqVO);

    PageResult<AppCreditRecordRespVO> getAppCreditRecordPage(Long userId, AppCreditRecordPageReqVO pageReqVO);

    /**
     * 获得信用记录详情
     *
     * @param id 主键
     * @return 信用记录详情
     */
    CreditRecordDetailRespVO getCreditRecordDetail(Long id);

    AppCreditRecordDetailRespVO getAppCreditRecordDetail(Long userId, Long id);
}

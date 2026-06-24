package cn.iocoder.yudao.module.linbang.service.merchantentry;

import java.util.*;
import javax.validation.*;
import cn.iocoder.yudao.module.linbang.controller.admin.merchantentry.vo.*;
import cn.iocoder.yudao.module.linbang.dal.dataobject.merchantentry.MerchantEntryDO;
import cn.iocoder.yudao.framework.common.pojo.PageResult;
import cn.iocoder.yudao.framework.common.pojo.PageParam;

/**
 * 服务商入驻申请表 Service 接口
 *
 * @author dawn
 */
public interface MerchantEntryService {

    /**
     * 创建服务商入驻申请表
     *
     * @param createReqVO 创建信息
     * @return 编号
     */
    Long createMerchantEntry(@Valid MerchantEntrySaveReqVO createReqVO);

    /**
     * 更新服务商入驻申请表
     *
     * @param updateReqVO 更新信息
     */
    void updateMerchantEntry(@Valid MerchantEntrySaveReqVO updateReqVO);

    /**
     * 删除服务商入驻申请表
     *
     * @param id 编号
     */
    void deleteMerchantEntry(Long id);

    /**
    * 批量删除服务商入驻申请表
    *
    * @param ids 编号
    */
    void deleteMerchantEntryListByIds(List<Long> ids);

    /**
     * 获得服务商入驻申请表
     *
     * @param id 编号
     * @return 服务商入驻申请表
     */
    MerchantEntryDO getMerchantEntry(Long id);

    /**
     * 获得服务商入驻详情
     *
     * @param id 编号
     * @return 服务商入驻详情
     */
    MerchantEntryDetailRespVO getMerchantEntryDetail(Long id);

    /**
     * 审核服务商入驻申请表
     *
     * @param reqVO 审核信息
     */
    void auditMerchantEntry(@Valid MerchantEntryAuditReqVO reqVO);

    /**
     * 获得服务商入驻申请表分页
     *
     * @param pageReqVO 分页查询
     * @return 服务商入驻申请表分页
     */
    PageResult<MerchantEntryRespVO> getMerchantEntryPage(MerchantEntryPageReqVO pageReqVO);

}

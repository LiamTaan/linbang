package cn.iocoder.yudao.module.linbang.service.merchantinfo;

import java.util.*;
import javax.validation.*;
import cn.iocoder.yudao.module.linbang.controller.admin.merchantinfo.vo.*;
import cn.iocoder.yudao.module.linbang.dal.dataobject.merchantinfo.MerchantInfoDO;
import cn.iocoder.yudao.framework.common.pojo.PageResult;
import cn.iocoder.yudao.framework.common.pojo.PageParam;

/**
 * 服务商信息表 Service 接口
 *
 * @author dawn
 */
public interface MerchantInfoService {

    /**
     * 创建服务商信息表
     *
     * @param createReqVO 创建信息
     * @return 编号
     */
    Long createMerchantInfo(@Valid MerchantInfoSaveReqVO createReqVO);

    /**
     * 更新服务商信息表
     *
     * @param updateReqVO 更新信息
     */
    void updateMerchantInfo(@Valid MerchantInfoSaveReqVO updateReqVO);

    /**
     * 删除服务商信息表
     *
     * @param id 编号
     */
    void deleteMerchantInfo(Long id);

    /**
    * 批量删除服务商信息表
    *
    * @param ids 编号
    */
    void deleteMerchantInfoListByIds(List<Long> ids);

    /**
     * 获得服务商信息表
     *
     * @param id 编号
     * @return 服务商信息表
     */
    MerchantInfoDO getMerchantInfo(Long id);

    /**
     * 获得服务商详情
     *
     * @param id 编号
     * @return 服务商详情
     */
    MerchantInfoDetailRespVO getMerchantInfoDetail(Long id);

    /**
     * 获得服务商信息表分页
     *
     * @param pageReqVO 分页查询
     * @return 服务商信息表分页
     */
    PageResult<MerchantInfoRespVO> getMerchantInfoPage(MerchantInfoPageReqVO pageReqVO);

}

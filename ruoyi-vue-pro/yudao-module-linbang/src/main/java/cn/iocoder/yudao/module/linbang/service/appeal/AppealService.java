package cn.iocoder.yudao.module.linbang.service.appeal;

import java.util.*;
import javax.validation.*;
import cn.iocoder.yudao.module.linbang.controller.admin.appeal.vo.*;
import cn.iocoder.yudao.framework.common.pojo.PageResult;
import cn.iocoder.yudao.module.linbang.dal.dataobject.appeal.AppealDO;
import cn.iocoder.yudao.framework.common.pojo.PageParam;

/**
 * 申诉 Service 接口
 *
 * @author dawn
 */
public interface AppealService {

    /**
     * 创建申诉
     *
     * @param createReqVO 创建信息
     * @return 编号
     */
    Long createAppeal(@Valid AppealSaveReqVO createReqVO);

    /**
     * 更新申诉
     *
     * @param updateReqVO 更新信息
     */
    void updateAppeal(@Valid AppealSaveReqVO updateReqVO);

    /**
     * 删除申诉
     *
     * @param id 编号
     */
    void deleteAppeal(Long id);

    /**
    * 批量删除申诉
    *
    * @param ids 编号
    */
    void deleteAppealListByIds(List<Long> ids);

    /**
     * 获得申诉
     *
     * @param id 编号
     * @return 申诉
     */
    AppealDO getAppeal(Long id);

    /**
     * 获得申诉详情
     *
     * @param id 编号
     * @return 申诉详情
     */
    AppealDetailRespVO getAppealDetail(Long id);

    /**
     * 审核申诉
     *
     * @param reqVO 审核请求
     */
    void auditAppeal(@Valid AppealAuditReqVO reqVO);

    /**
     * 获得申诉分页
     *
     * @param pageReqVO 分页查询
     * @return 申诉分页
     */
    PageResult<AppealRespVO> getAppealPage(AppealPageReqVO pageReqVO);

}

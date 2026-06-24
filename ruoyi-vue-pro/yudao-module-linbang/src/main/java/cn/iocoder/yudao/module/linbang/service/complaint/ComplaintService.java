package cn.iocoder.yudao.module.linbang.service.complaint;

import java.util.*;
import javax.validation.*;
import cn.iocoder.yudao.module.linbang.controller.admin.complaint.vo.*;
import cn.iocoder.yudao.framework.common.pojo.PageResult;
import cn.iocoder.yudao.module.linbang.dal.dataobject.complaint.ComplaintDO;

/**
 * 投诉 Service 接口
 *
 * @author dawn
 */
public interface ComplaintService {

    /**
     * 创建投诉
     *
     * @param createReqVO 创建信息
     * @return 编号
     */
    Long createComplaint(@Valid ComplaintSaveReqVO createReqVO);

    /**
     * 更新投诉
     *
     * @param updateReqVO 更新信息
     */
    void updateComplaint(@Valid ComplaintSaveReqVO updateReqVO);

    /**
     * 删除投诉
     *
     * @param id 编号
     */
    void deleteComplaint(Long id);

    /**
    * 批量删除投诉
    *
    * @param ids 编号
    */
    void deleteComplaintListByIds(List<Long> ids);

    /**
     * 获得投诉
     *
     * @param id 编号
     * @return 投诉
     */
    ComplaintDO getComplaint(Long id);

    /**
     * 获得投诉详情
     *
     * @param id 编号
     * @return 投诉详情
     */
    ComplaintDetailRespVO getComplaintDetail(Long id);

    /**
     * 处理投诉
     *
     * @param reqVO 处理请求
     */
    void processComplaint(@Valid ComplaintProcessReqVO reqVO);

    /**
     * 获得投诉分页
     *
     * @param pageReqVO 分页查询
     * @return 投诉分页
     */
    PageResult<ComplaintRespVO> getComplaintPage(ComplaintPageReqVO pageReqVO);

}

package cn.iocoder.yudao.module.linbang.service.orderacceptrecord;

import java.util.*;
import javax.validation.*;
import cn.iocoder.yudao.module.linbang.controller.admin.orderacceptrecord.vo.*;
import cn.iocoder.yudao.module.linbang.dal.dataobject.orderacceptrecord.OrderAcceptRecordDO;
import cn.iocoder.yudao.framework.common.pojo.PageResult;

/**
 * 抢单记录 Service 接口
 *
 * @author dawn
 */
public interface OrderAcceptRecordService {

    /**
     * 创建抢单记录
     *
     * @param createReqVO 创建信息
     * @return 编号
     */
    Long createOrderAcceptRecord(@Valid OrderAcceptRecordSaveReqVO createReqVO);

    /**
     * 更新抢单记录
     *
     * @param updateReqVO 更新信息
     */
    void updateOrderAcceptRecord(@Valid OrderAcceptRecordSaveReqVO updateReqVO);

    /**
     * 删除抢单记录
     *
     * @param id 编号
     */
    void deleteOrderAcceptRecord(Long id);

    /**
    * 批量删除抢单记录
    *
    * @param ids 编号
    */
    void deleteOrderAcceptRecordListByIds(List<Long> ids);

    /**
     * 获得抢单记录
     *
     * @param id 编号
     * @return 抢单记录
     */
    OrderAcceptRecordDO getOrderAcceptRecord(Long id);

    /**
     * 获得抢单记录详情
     *
     * @param id 编号
     * @return 抢单记录详情
     */
    OrderAcceptRecordDetailRespVO getOrderAcceptRecordDetail(Long id);

    /**
     * 获得抢单记录分页
     *
     * @param pageReqVO 分页查询
     * @return 抢单记录分页
     */
    PageResult<OrderAcceptRecordRespVO> getOrderAcceptRecordPage(OrderAcceptRecordPageReqVO pageReqVO);

}

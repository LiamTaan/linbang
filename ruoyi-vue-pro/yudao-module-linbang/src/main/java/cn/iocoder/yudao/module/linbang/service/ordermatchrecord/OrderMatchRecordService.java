package cn.iocoder.yudao.module.linbang.service.ordermatchrecord;

import java.util.*;
import javax.validation.*;
import cn.iocoder.yudao.module.linbang.controller.admin.ordermatchrecord.vo.*;
import cn.iocoder.yudao.module.linbang.dal.dataobject.ordermatchrecord.OrderMatchRecordDO;
import cn.iocoder.yudao.framework.common.pojo.PageResult;

/**
 * 订单匹配记录 Service 接口
 *
 * @author dawn
 */
public interface OrderMatchRecordService {

    /**
     * 创建订单匹配记录
     *
     * @param createReqVO 创建信息
     * @return 编号
     */
    Long createOrderMatchRecord(@Valid OrderMatchRecordSaveReqVO createReqVO);

    /**
     * 更新订单匹配记录
     *
     * @param updateReqVO 更新信息
     */
    void updateOrderMatchRecord(@Valid OrderMatchRecordSaveReqVO updateReqVO);

    /**
     * 删除订单匹配记录
     *
     * @param id 编号
     */
    void deleteOrderMatchRecord(Long id);

    /**
    * 批量删除订单匹配记录
    *
    * @param ids 编号
    */
    void deleteOrderMatchRecordListByIds(List<Long> ids);

    /**
     * 获得订单匹配记录
     *
     * @param id 编号
     * @return 订单匹配记录
     */
    OrderMatchRecordDO getOrderMatchRecord(Long id);

    /**
     * 获得订单匹配记录详情
     *
     * @param id 编号
     * @return 订单匹配记录详情
     */
    OrderMatchRecordDetailRespVO getOrderMatchRecordDetail(Long id);

    /**
     * 获得订单匹配记录分页
     *
     * @param pageReqVO 分页查询
     * @return 订单匹配记录分页
     */
    PageResult<OrderMatchRecordRespVO> getOrderMatchRecordPage(OrderMatchRecordPageReqVO pageReqVO);

}

package cn.iocoder.yudao.module.linbang.service.orderinfo;

import java.util.*;
import javax.validation.*;
import cn.iocoder.yudao.module.linbang.controller.admin.orderinfo.vo.*;
import cn.iocoder.yudao.module.linbang.dal.dataobject.orderinfo.OrderInfoDO;
import cn.iocoder.yudao.framework.common.pojo.PageResult;
import cn.iocoder.yudao.framework.common.pojo.PageParam;

/**
 * 订单主 Service 接口
 *
 * @author dawn
 */
public interface OrderInfoService {

    /**
     * 创建订单主
     *
     * @param createReqVO 创建信息
     * @return 编号
     */
    Long createOrderInfo(@Valid OrderInfoSaveReqVO createReqVO);

    /**
     * 更新订单主
     *
     * @param updateReqVO 更新信息
     */
    void updateOrderInfo(@Valid OrderInfoSaveReqVO updateReqVO);

    /**
     * 删除订单主
     *
     * @param id 编号
     */
    void deleteOrderInfo(Long id);

    /**
    * 批量删除订单主
    *
    * @param ids 编号
    */
    void deleteOrderInfoListByIds(List<Long> ids);

    /**
     * 获得订单主
     *
     * @param id 编号
     * @return 订单主
     */
    OrderInfoDO getOrderInfo(Long id);

    /**
     * 获得订单详情
     *
     * @param id 编号
     * @return 订单详情
     */
    OrderInfoDetailRespVO getOrderInfoDetail(Long id);

    /**
     * 获得订单主分页
     *
     * @param pageReqVO 分页查询
     * @return 订单主分页
     */
    PageResult<OrderInfoRespVO> getOrderInfoPage(OrderInfoPageReqVO pageReqVO);

}

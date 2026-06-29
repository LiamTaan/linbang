package cn.iocoder.yudao.module.linbang.service.orderabnormal;

import java.util.*;
import javax.validation.*;
import cn.iocoder.yudao.module.linbang.controller.admin.orderinfo.vo.OrderMarkAbnormalReqVO;
import cn.iocoder.yudao.module.linbang.controller.admin.orderabnormal.vo.*;
import cn.iocoder.yudao.framework.common.pojo.PageResult;
import cn.iocoder.yudao.module.linbang.dal.dataobject.orderabnormal.OrderAbnormalDO;

/**
 * 异常订单 Service 接口
 *
 * @author dawn
 */
public interface OrderAbnormalService {

    /**
     * 创建异常订单
     *
     * @param createReqVO 创建信息
     * @return 编号
     */
    Long createOrderAbnormal(@Valid OrderAbnormalSaveReqVO createReqVO);

    /**
     * 更新异常订单
     *
     * @param updateReqVO 更新信息
     */
    void updateOrderAbnormal(@Valid OrderAbnormalSaveReqVO updateReqVO);

    /**
     * 删除异常订单
     *
     * @param id 编号
     */
    void deleteOrderAbnormal(Long id);

    /**
    * 批量删除异常订单
    *
    * @param ids 编号
    */
    void deleteOrderAbnormalListByIds(List<Long> ids);

    /**
     * 获得异常订单
     *
     * @param id 编号
     * @return 异常订单
     */
    OrderAbnormalDO getOrderAbnormal(Long id);

    /**
     * 获得异常订单详情
     *
     * @param id 编号
     * @return 异常订单详情
     */
    OrderAbnormalDetailRespVO getOrderAbnormalDetail(Long id);

    /**
     * 订单标记异常
     *
     * @param reqVO 标记请求
     * @return 异常单编号
     */
    Long markOrderAbnormal(@Valid OrderMarkAbnormalReqVO reqVO);

    /**
     * 异常单终审
     *
     * @param reqVO 终审请求
     */
    void finalAuditOrderAbnormal(@Valid OrderAbnormalFinalAuditReqVO reqVO);

    /**
     * 获得异常订单分页
     *
     * @param pageReqVO 分页查询
     * @return 异常订单分页
     */
    PageResult<OrderAbnormalRespVO> getOrderAbnormalPage(OrderAbnormalPageReqVO pageReqVO);

}

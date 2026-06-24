package cn.iocoder.yudao.module.linbang.service.orderunit;

import java.util.*;
import javax.validation.*;
import cn.iocoder.yudao.module.linbang.controller.admin.orderunit.vo.*;
import cn.iocoder.yudao.module.linbang.dal.dataobject.orderunit.OrderUnitDO;
import cn.iocoder.yudao.framework.common.pojo.PageResult;
import cn.iocoder.yudao.framework.common.pojo.PageParam;

/**
 * 拆分单元 Service 接口
 *
 * @author dawn
 */
public interface OrderUnitService {

    /**
     * 创建拆分单元
     *
     * @param createReqVO 创建信息
     * @return 编号
     */
    Long createOrderUnit(@Valid OrderUnitSaveReqVO createReqVO);

    /**
     * 更新拆分单元
     *
     * @param updateReqVO 更新信息
     */
    void updateOrderUnit(@Valid OrderUnitSaveReqVO updateReqVO);

    /**
     * 删除拆分单元
     *
     * @param id 编号
     */
    void deleteOrderUnit(Long id);

    /**
    * 批量删除拆分单元
    *
    * @param ids 编号
    */
    void deleteOrderUnitListByIds(List<Long> ids);

    /**
     * 获得拆分单元
     *
     * @param id 编号
     * @return 拆分单元
     */
    OrderUnitDO getOrderUnit(Long id);

    /**
     * 获得拆分单元详情
     *
     * @param id 编号
     * @return 拆分单元详情
     */
    OrderUnitDetailRespVO getOrderUnitDetail(Long id);

    /**
     * 人工解锁单元
     *
     * @param reqVO 解锁请求
     */
    void unlockOrderUnit(@Valid OrderUnitUnlockReqVO reqVO);

    /**
     * 获得拆分单元分页
     *
     * @param pageReqVO 分页查询
     * @return 拆分单元分页
     */
    PageResult<OrderUnitRespVO> getOrderUnitPage(OrderUnitPageReqVO pageReqVO);

}

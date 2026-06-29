package cn.iocoder.yudao.module.linbang.service.orderdividerecord;

import cn.iocoder.yudao.framework.common.pojo.PageResult;
import cn.iocoder.yudao.module.linbang.controller.admin.orderdividerecord.vo.OrderDivideRecordPageReqVO;
import cn.iocoder.yudao.module.linbang.controller.admin.orderdividerecord.vo.OrderDivideRecordRespVO;

public interface OrderDivideRecordService {

    PageResult<OrderDivideRecordRespVO> getOrderDivideRecordPage(OrderDivideRecordPageReqVO reqVO);

    OrderDivideRecordRespVO getOrderDivideRecord(Long id);

}

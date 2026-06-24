package cn.iocoder.yudao.module.linbang.service.memberaddress;

import java.util.*;
import javax.validation.*;
import cn.iocoder.yudao.module.linbang.controller.admin.memberaddress.vo.*;
import cn.iocoder.yudao.module.linbang.dal.dataobject.memberaddress.MemberUserAddressDO;
import cn.iocoder.yudao.framework.common.pojo.PageResult;
import cn.iocoder.yudao.framework.common.pojo.PageParam;

/**
 * 用户地址表 Service 接口
 *
 * @author dawn
 */
public interface MemberUserAddressService {

    /**
     * 创建用户地址表
     *
     * @param createReqVO 创建信息
     * @return 编号
     */
    Long createMemberUserAddress(@Valid MemberUserAddressSaveReqVO createReqVO);

    /**
     * 更新用户地址表
     *
     * @param updateReqVO 更新信息
     */
    void updateMemberUserAddress(@Valid MemberUserAddressSaveReqVO updateReqVO);

    /**
     * 删除用户地址表
     *
     * @param id 编号
     */
    void deleteMemberUserAddress(Long id);

    /**
    * 批量删除用户地址表
    *
    * @param ids 编号
    */
    void deleteMemberUserAddressListByIds(List<Long> ids);

    /**
     * 获得用户地址表
     *
     * @param id 编号
     * @return 用户地址表
     */
    MemberUserAddressDO getMemberUserAddress(Long id);

    /**
     * 获得用户地址表分页
     *
     * @param pageReqVO 分页查询
     * @return 用户地址表分页
     */
    PageResult<MemberUserAddressRespVO> getMemberUserAddressPage(MemberUserAddressPageReqVO pageReqVO);

}

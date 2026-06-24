package cn.iocoder.yudao.module.linbang.service.memberrealname;

import java.util.*;
import javax.validation.*;
import cn.iocoder.yudao.module.linbang.controller.admin.memberrealname.vo.*;
import cn.iocoder.yudao.module.linbang.dal.dataobject.memberrealname.MemberUserRealNameDO;
import cn.iocoder.yudao.framework.common.pojo.PageResult;
import cn.iocoder.yudao.framework.common.pojo.PageParam;

/**
 * 实名认证表 Service 接口
 *
 * @author dawn
 */
public interface MemberUserRealNameService {

    /**
     * 创建实名认证表
     *
     * @param createReqVO 创建信息
     * @return 编号
     */
    Long createMemberUserRealName(@Valid MemberUserRealNameSaveReqVO createReqVO);

    /**
     * 更新实名认证表
     *
     * @param updateReqVO 更新信息
     */
    void updateMemberUserRealName(@Valid MemberUserRealNameSaveReqVO updateReqVO);

    /**
     * 删除实名认证表
     *
     * @param id 编号
     */
    void deleteMemberUserRealName(Long id);

    /**
    * 批量删除实名认证表
    *
    * @param ids 编号
    */
    void deleteMemberUserRealNameListByIds(List<Long> ids);

    /**
     * 获得实名认证表
     *
     * @param id 编号
     * @return 实名认证表
     */
    MemberUserRealNameDO getMemberUserRealName(Long id);

    /**
     * 获得实名认证详情
     *
     * @param id 编号
     * @return 实名认证详情
     */
    MemberUserRealNameDetailRespVO getMemberUserRealNameDetail(Long id);

    /**
     * 审核实名认证表
     *
     * @param reqVO 审核信息
     */
    void auditMemberUserRealName(@Valid MemberUserRealNameAuditReqVO reqVO);

    /**
     * 获得实名认证表分页
     *
     * @param pageReqVO 分页查询
     * @return 实名认证表分页
     */
    PageResult<MemberUserRealNameRespVO> getMemberUserRealNamePage(MemberUserRealNamePageReqVO pageReqVO);

}

package cn.iocoder.yudao.module.linbang.service.sensitiveword;

import java.util.*;
import javax.validation.*;
import cn.iocoder.yudao.module.linbang.controller.admin.sensitiveword.vo.*;
import cn.iocoder.yudao.module.linbang.dal.dataobject.sensitiveword.SensitiveWordDO;
import cn.iocoder.yudao.framework.common.pojo.PageResult;

/**
 * 敏感词表 Service 接口
 *
 * @author dawn
 */
public interface SensitiveWordService {

    /**
     * 创建敏感词表
     *
     * @param createReqVO 创建信息
     * @return 编号
     */
    Long createSensitiveWord(@Valid SensitiveWordSaveReqVO createReqVO);

    /**
     * 更新敏感词表
     *
     * @param updateReqVO 更新信息
     */
    void updateSensitiveWord(@Valid SensitiveWordSaveReqVO updateReqVO);

    /**
     * 删除敏感词表
     *
     * @param id 编号
     */
    void deleteSensitiveWord(Long id);

    /**
    * 批量删除敏感词表
    *
    * @param ids 编号
    */
    void deleteSensitiveWordListByIds(List<Long> ids);

    /**
     * 获得敏感词表
     *
     * @param id 编号
     * @return 敏感词表
     */
    SensitiveWordDO getSensitiveWord(Long id);

    /**
     * 获得敏感词详情
     *
     * @param id 编号
     * @return 敏感词详情
     */
    SensitiveWordDetailRespVO getSensitiveWordDetail(Long id);

    /**
     * 获得敏感词表分页
     *
     * @param pageReqVO 分页查询
     * @return 敏感词表分页
     */
    PageResult<SensitiveWordDO> getSensitiveWordPage(SensitiveWordPageReqVO pageReqVO);

}

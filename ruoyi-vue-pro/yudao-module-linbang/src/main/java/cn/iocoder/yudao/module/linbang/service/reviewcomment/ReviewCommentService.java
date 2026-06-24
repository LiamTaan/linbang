package cn.iocoder.yudao.module.linbang.service.reviewcomment;

import java.util.*;
import javax.validation.*;
import cn.iocoder.yudao.module.linbang.controller.admin.reviewcomment.vo.*;
import cn.iocoder.yudao.framework.common.pojo.PageResult;
import cn.iocoder.yudao.module.linbang.dal.dataobject.reviewcomment.ReviewCommentDO;
import cn.iocoder.yudao.framework.common.pojo.PageParam;

/**
 * 评价 Service 接口
 *
 * @author dawn
 */
public interface ReviewCommentService {

    /**
     * 创建评价
     *
     * @param createReqVO 创建信息
     * @return 编号
     */
    Long createReviewComment(@Valid ReviewCommentSaveReqVO createReqVO);

    /**
     * 更新评价
     *
     * @param updateReqVO 更新信息
     */
    void updateReviewComment(@Valid ReviewCommentSaveReqVO updateReqVO);

    /**
     * 删除评价
     *
     * @param id 编号
     */
    void deleteReviewComment(Long id);

    /**
    * 批量删除评价
    *
    * @param ids 编号
    */
    void deleteReviewCommentListByIds(List<Long> ids);

    /**
     * 获得评价
     *
     * @param id 编号
     * @return 评价
     */
    ReviewCommentDO getReviewComment(Long id);

    ReviewCommentDetailRespVO getReviewCommentDetail(Long id);

    /**
     * 获得评价分页
     *
     * @param pageReqVO 分页查询
     * @return 评价分页
     */
    PageResult<ReviewCommentRespVO> getReviewCommentPage(ReviewCommentPageReqVO pageReqVO);

}

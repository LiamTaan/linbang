package cn.iocoder.yudao.module.linbang.dal.mysql.helpfaq;

import cn.iocoder.yudao.framework.common.pojo.PageResult;
import cn.iocoder.yudao.framework.mybatis.core.mapper.BaseMapperX;
import cn.iocoder.yudao.framework.mybatis.core.query.LambdaQueryWrapperX;
import cn.iocoder.yudao.module.linbang.controller.admin.helpfaq.vo.HelpFaqPageReqVO;
import cn.iocoder.yudao.module.linbang.controller.app.help.faq.vo.AppHelpFaqPageReqVO;
import cn.iocoder.yudao.module.linbang.dal.dataobject.helpfaq.HelpFaqDO;
import org.apache.ibatis.annotations.Mapper;
import org.springframework.util.StringUtils;

import java.util.List;

@Mapper
public interface HelpFaqMapper extends BaseMapperX<HelpFaqDO> {

    default PageResult<HelpFaqDO> selectPage(HelpFaqPageReqVO reqVO) {
        return selectPage(reqVO, new LambdaQueryWrapperX<HelpFaqDO>()
                .likeIfPresent(HelpFaqDO::getTitle, reqVO.getTitle())
                .eqIfPresent(HelpFaqDO::getCategoryCode, reqVO.getCategoryCode())
                .eqIfPresent(HelpFaqDO::getStatus, reqVO.getStatus())
                .betweenIfPresent(HelpFaqDO::getCreateTime, reqVO.getCreateTime())
                .orderByAsc(HelpFaqDO::getSortNo)
                .orderByDesc(HelpFaqDO::getId));
    }

    default List<HelpFaqDO> selectAppList(AppHelpFaqPageReqVO reqVO) {
        LambdaQueryWrapperX<HelpFaqDO> wrapper = new LambdaQueryWrapperX<HelpFaqDO>()
                .eq(HelpFaqDO::getStatus, "ENABLE")
                .eqIfPresent(HelpFaqDO::getCategoryCode, reqVO.getCategoryCode());
        if (StringUtils.hasText(reqVO.getKeyword())) {
            wrapper.and(item -> item
                        .like(HelpFaqDO::getTitle, reqVO.getKeyword())
                        .or()
                        .like(HelpFaqDO::getContent, reqVO.getKeyword()));
        }
        return selectList(wrapper
                .orderByAsc(HelpFaqDO::getSortNo)
                .orderByDesc(HelpFaqDO::getId));
    }
}

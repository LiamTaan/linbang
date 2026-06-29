package cn.iocoder.yudao.module.linbang.controller.app.promote;

import cn.iocoder.yudao.framework.common.pojo.CommonResult;
import cn.iocoder.yudao.framework.common.pojo.PageResult;
import cn.iocoder.yudao.module.linbang.controller.app.promote.vo.AppPromoteContentCreateReqVO;
import cn.iocoder.yudao.module.linbang.controller.app.promote.vo.AppPromoteContentPageReqVO;
import cn.iocoder.yudao.module.linbang.controller.app.promote.vo.AppPromoteContentRespVO;
import cn.iocoder.yudao.module.linbang.service.promotecontent.PromoteContentService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import javax.annotation.Resource;
import javax.validation.Valid;

import static cn.iocoder.yudao.framework.common.pojo.CommonResult.success;
import static cn.iocoder.yudao.framework.security.core.util.SecurityFrameworkUtils.getLoginUserId;

@Tag(name = "用户 App - 推广内容")
@RestController
@RequestMapping("/promote/content")
@Validated
public class AppPromoteContentController {

    @Resource
    private PromoteContentService promoteContentService;

    @PostMapping("/create")
    @Operation(summary = "提交推广内容")
    public CommonResult<Long> create(@Valid @RequestBody AppPromoteContentCreateReqVO reqVO) {
        return success(promoteContentService.createAppContent(getLoginUserId(), reqVO));
    }

    @GetMapping("/page")
    @Operation(summary = "分页查询我的推广内容")
    public CommonResult<PageResult<AppPromoteContentRespVO>> getPage(@Valid AppPromoteContentPageReqVO reqVO) {
        return success(promoteContentService.getAppContentPage(getLoginUserId(), reqVO));
    }

    @GetMapping("/get")
    @Operation(summary = "获取推广内容详情")
    @Parameter(name = "id", required = true)
    public CommonResult<AppPromoteContentRespVO> get(@RequestParam("id") Long id) {
        return success(promoteContentService.getAppContent(getLoginUserId(), id));
    }
}

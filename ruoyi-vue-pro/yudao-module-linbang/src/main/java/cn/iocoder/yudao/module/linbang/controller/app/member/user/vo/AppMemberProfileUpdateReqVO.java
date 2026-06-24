package cn.iocoder.yudao.module.linbang.controller.app.member.user.vo;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;
import org.springframework.format.annotation.DateTimeFormat;

import javax.validation.constraints.NotEmpty;
import java.time.LocalDate;

import static cn.iocoder.yudao.framework.common.util.date.DateUtils.FORMAT_YEAR_MONTH_DAY;

@Schema(description = "用户 App - 更新个人资料 Request VO")
@Data
public class AppMemberProfileUpdateReqVO {

    @Schema(description = "昵称", requiredMode = Schema.RequiredMode.REQUIRED, example = "小区热心人")
    @NotEmpty(message = "昵称不能为空")
    private String nickname;

    @Schema(description = "头像", example = "https://example.com/avatar.png")
    private String avatar;

    @Schema(description = "性别", example = "1")
    private Integer gender;

    @Schema(description = "生日", example = "1995-05-20")
    @DateTimeFormat(pattern = FORMAT_YEAR_MONTH_DAY)
    private LocalDate birthday;
}

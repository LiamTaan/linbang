package cn.iocoder.yudao.module.linbang.framework.swagger;

import cn.iocoder.yudao.framework.swagger.config.YudaoSwaggerAutoConfiguration;
import org.springdoc.core.GroupedOpenApi;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import java.util.Arrays;
import java.util.List;

@Configuration
public class LinbangSwaggerConfiguration {

    @Bean
    public GroupedOpenApi linbangAppGroupedOpenApi() {
        return YudaoSwaggerAutoConfiguration.buildGroupedOpenApi(
                "linbang-app",
                Arrays.asList("/app-api/**"),
                Arrays.asList(
                        "cn.iocoder.yudao.module.linbang.controller.app",
                        "cn.iocoder.yudao.module.infra.controller.app.file",
                        "cn.iocoder.yudao.module.system.controller.app.dict"));
    }

    @Bean
    public GroupedOpenApi linbangAdminGroupedOpenApi() {
        return YudaoSwaggerAutoConfiguration.buildGroupedOpenApi(
                "linbang-admin",
                Arrays.asList("/admin-api/**"),
                Arrays.asList("cn.iocoder.yudao.module.linbang.controller.admin"));
    }

}

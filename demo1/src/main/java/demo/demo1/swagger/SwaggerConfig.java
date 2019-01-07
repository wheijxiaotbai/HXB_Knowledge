package demo.demo1.swagger;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.bind.annotation.RequestMethod;
import springfox.documentation.builders.*;
import springfox.documentation.service.ApiInfo;
import springfox.documentation.service.Parameter;
import springfox.documentation.service.ResponseMessage;
import springfox.documentation.spi.DocumentationType;
import springfox.documentation.spring.web.plugins.Docket;
import springfox.documentation.swagger2.annotations.EnableSwagger2;

import java.util.List;

import static com.google.common.collect.Lists.newArrayList;


/**
 * Created by work on 2017/7/3.
 * Modified by Alex Yang on 2017/7/18.
 * <p>
 * Swagger UI Configuration.
 */
@Configuration
@EnableSwagger2
public class SwaggerConfig {

    @Bean
    public Docket api() {
        return new Docket(DocumentationType.SWAGGER_2)
                .groupName("demo")
                .useDefaultResponseMessages(false)
                .globalResponseMessage(RequestMethod.POST, globalResponseMessages())
                .globalResponseMessage(RequestMethod.DELETE, globalResponseMessages())
                .globalResponseMessage(RequestMethod.PUT, globalResponseMessages())
                .globalResponseMessage(RequestMethod.GET, globalResponseMessages())
                .globalResponseMessage(RequestMethod.PATCH, globalResponseMessages())
                .globalOperationParameters(globalParams())
                .apiInfo(apiInfo())
                .select()
                .apis(RequestHandlerSelectors.basePackage("demo.demo1"))
                .paths(PathSelectors.any())
                .build();
    }

    private ApiInfo apiInfo() {
        return new ApiInfoBuilder()
                .title("spring boot jwt security API Documentation")
                .build();
    }

    private List<ResponseMessage> globalResponseMessages() {
        return newArrayList(
                new ResponseMessageBuilder()
                        .code(400)
                        .message("请求路径或参数不合法")
                        .build(),
                new ResponseMessageBuilder()
                        .code(401)
                        .message("认证失败")
                        .build(),
                new ResponseMessageBuilder()
                        .code(403)
                        .message("禁止访问")
                        .build(),
                new ResponseMessageBuilder()
                        .code(500)
                        .message("内部错误")
                        .build()
        );
    }

    private List<Parameter> globalParams() {
        return newArrayList(
                new ParameterBuilder()
                        .name("Authorization")
                        .parameterType("header")
                        .required(false)
                        .allowMultiple(false)
                        .modelRef(new springfox.documentation.schema.ModelRef("string"))
                        .description("认证用Token, 非Auth接口必填")
                        .build()
        );
    }

}

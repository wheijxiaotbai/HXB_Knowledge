package demo.demo1.auth.api.rest;

import demo.demo1.User.model.User;
import demo.demo1.User.service.UserService;
import demo.demo1.auth.jwt.JwtUtil;
import demo.demo1.auth.model.LoginUser;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import io.swagger.annotations.ApiParam;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.AuthenticationException;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import javax.servlet.http.HttpServletResponse;

@RestController
@RequestMapping("/auth")
@CrossOrigin(origins = "*")
@Api(
        value = "/auth",
        description = "用户登录认证"
)
public class AuthController {

    @Autowired
    private UserService userService;

    @Autowired
    private JwtUtil jwtUtil;

    @Autowired
    private AuthenticationManager authenticationManager;

    @RequestMapping(value = "", method = RequestMethod.POST)
    @ApiOperation(
            value = "登录",
            produces = "application/json"
    )
    public void login(
            @ApiParam(value = "登录用户名/密码", name = "LoginUser", required = true)
            @Validated
            @RequestBody LoginUser loginUser,
            HttpServletResponse response) throws Exception {

        try {
            /** 通过security验证登录账号是否正确,这里直接将用户和密码传入security就好,
             * 不需要在这里进行验证,你的验证会在userDetailService中由security帮你进行
             */
            authenticationManager.authenticate(
                    new UsernamePasswordAuthenticationToken(
                            loginUser.getUsername(),
                            loginUser.getPassword()
                    )
            );
        } catch (AuthenticationException e) {
            throw new Exception("Username or Password error.");
        }

        // 验证通过后返回一个token值在http head中
        User user = userService.getUserByUserName(loginUser.getUsername());
        String token = jwtUtil.generateToken(user);
        // set token to header
        response.setHeader(JwtUtil.HEADER_STRING, token);
    }
}

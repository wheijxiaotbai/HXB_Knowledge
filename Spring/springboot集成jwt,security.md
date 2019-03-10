# spring boot 集成jwt security

### 文章结构

> * 开门见山
>
> * 数据流程

### 开门见山

这一部分直接展示代码,及将哪些代码进行修改就可以直接移值到自己的项目进行安全验证

##### 代码目录结构

> Auth
>
> > AuthController				
> >
> > LoginUser
>
> JWT
>
> >JwtUtil	
>
> Security
>
> >AuthFilter
> >
> >SecurityConfig
> >
> >UserDetailsImpl
> >
> >UserDetailsServiceImpl
>
> User
>
> > UserController
> >
> > UserService
> >
> > User
> >
> > UserRepository

##### AuthController

说明:该类是自定义的用户进行登录验证获取token 的接口,是所有人都能访问的

```java
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
```



##### LoginUser

说明: 该类是你进行登录时的bean,这里主要就是为了和user进行区分,登录的时候用这个bean

```java
package demo.demo1.auth.model;

import io.swagger.annotations.ApiModel;

import javax.validation.constraints.NotNull;

@ApiModel(value = "Login User", description = "登录用户信息")
public class LoginUser {

    @NotNull
    private String username;

    @NotNull
    private String password;

    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }
}
```



##### JwtUtil

说明:这个类就是依据你的登录用户生成jwt token,验证/解析请求时的token

```java
package demo.demo1.auth.jwt;

import demo.demo1.User.model.User;
import demo.demo1.User.service.UserService;
import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.SignatureAlgorithm;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import java.util.Date;
import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

@Component
public class JwtUtil {

    /**
    token前缀
     */
    public static final String TOKEN_PREFIX = "Bearer ";

    /**
     * 设置http head中Authorization字段为token
     */
    public static final String HEADER_STRING = "Authorization";

    @Value("${jwt.secret}")
    private String secret;

    @Value("${jwt.expiration}")
    private Long expiration;

    @Autowired
    private UserService userService;

    /**
     * 依据登录的账号生成token
     */
    public String generateToken(User user) throws Exception {

        if (user == null || user.getId() == null) {
            throw new Exception(String.format("user %s not valid", user));
        }

        //设置token参数
        Map<String, Object> claims = new HashMap<>();
        claims.put("sub", user.getId());
        claims.put("aud", "web");
        claims.put("iss", "demo");
        claims.put("iat", new Date());

        return JwtUtil.TOKEN_PREFIX + Jwts.builder()
                .setClaims(claims)
                .setExpiration(new Date(System.currentTimeMillis() + expiration * 1000))
                .signWith(SignatureAlgorithm.HS512, secret)
                .compact();

    }

    /**
     * 解析token
     */
    public Claims parseTokenClaims(String token) throws Exception {

        try {
            String pure = token.replace(JwtUtil.TOKEN_PREFIX, "");
            return Jwts.parser().setSigningKey(secret).parseClaimsJws(pure).getBody();
        } catch (Exception e) {
            throw new Exception(e.getMessage());
        }

    }

    /**
     * 验证token
     */
    public Boolean validateToken(String token) {
        
        try {
            String pure = token.replace(JwtUtil.TOKEN_PREFIX, "");
            Claims claims = parseTokenClaims(pure);
            String subject = claims.getSubject();
            User user = userService.getUserById(UUID.fromString(subject));
            if (user == null) {
                return false;
            } else if (claims.getExpiration().after(new Date())) {
                return true;
            }
            return false;
        } catch (Exception e) {

        }
        return false;
    }
    
}
```



##### AuthFilter

说明

这个类继承了OncePerRequestFilter

当发送一个携带token的http请求访问某个接口的时候,这个过滤器就进行验证其用户权限

该类重写了doFilterInternal方法,在该方法中通过token进行权限验证

```java
package demo.demo1.auth.security;

import demo.demo1.User.service.UserService;
import demo.demo1.auth.jwt.JwtUtil;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.env.Environment;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.web.authentication.WebAuthenticationDetailsSource;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;

import javax.servlet.FilterChain;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.util.UUID;

@Component
public class AuthFilter extends OncePerRequestFilter {

    private static final Logger logger = LoggerFactory.getLogger(AuthFilter.class);

    @Autowired
    private JwtUtil jwtUtil;

    @Autowired
    private UserDetailsService userDetailsService;

    @Autowired
    private UserService userService;

    @Autowired
    private Environment env;

    @Override
    protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response, FilterChain chain)
            throws IOException, ServletException {

        String token = request.getHeader(JwtUtil.HEADER_STRING);
        if (token != null && token.startsWith(JwtUtil.TOKEN_PREFIX)) {
            token = token.replace(JwtUtil.TOKEN_PREFIX, "");
            try {
                String id = jwtUtil.parseTokenClaims(token).getSubject();
                String username = userService.getUserById(UUID.fromString(id)).getUsername();
                if (null != id && SecurityContextHolder.getContext().getAuthentication() == null) {
                    logger.debug("Checking token for user {}", id);
                    // In security, use uuid as username.
                    UserDetails userDetails = userDetailsService.loadUserByUsername(username);
                    if (jwtUtil.validateToken(token) && userDetails != null) {
                        // create authentication
                        UsernamePasswordAuthenticationToken authenticationToken = new UsernamePasswordAuthenticationToken(
                                userDetails, null, userDetails.getAuthorities()
                        );
                        // set authentication
                        authenticationToken.setDetails(new WebAuthenticationDetailsSource().buildDetails(request));
                        // put authentication into context holder
                        SecurityContextHolder.getContext().setAuthentication(authenticationToken);
                    }
                }
            } catch (Exception e) {
                logger.debug("Check token failed {}", e.getMessage());
            }
        }

        chain.doFilter(request, response);

    }

}
```



##### SecurityConfig

说明:该类是security的配置类,通过该类可以控制资源访问权限,通过什么方式进行验证用户权限

```java
package demo.demo1.auth.security;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.config.annotation.authentication.builders.AuthenticationManagerBuilder;
import org.springframework.security.config.annotation.method.configuration.EnableGlobalMethodSecurity;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configuration.WebSecurityConfigurerAdapter;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;

@Configuration
@EnableWebSecurity
@EnableGlobalMethodSecurity(prePostEnabled = true)
public class SecurityConfig extends WebSecurityConfigurerAdapter {

    @Autowired
    private UserDetailsService userDetailsService;

    @Bean
    public AuthFilter authorizationFilterBean() throws Exception {
        return new AuthFilter();
    }

    @Bean
    @Override
    public AuthenticationManager authenticationManagerBean() throws Exception {
        return super.authenticationManagerBean();
    }

    @Override
    protected void configure(HttpSecurity http) throws Exception {
        http
                // 禁用csrf
                .csrf().disable()
                // 因为是用的jwt所以不需要session        				  .sessionManagement().sessionCreationPolicy(SessionCreationPolicy.STATELESS)
                .and()
                .authorizeRequests()
                // 运行auth路径访问
                .antMatchers("/auth").permitAll()
                // 设置允许访问的资源
                .antMatchers("/webjars/**").permitAll()
                .antMatchers(
                        "/swagger-resources/configuration/ui",
                        "/swagger-resources",
                        "/swagger-resources/configuration/security",
                        "/swagger-ui.html",
                        "/swagger-ui.html",
                        "/v2/*",
                        "/user"
                ).permitAll()
                .anyRequest().authenticated();

        // 设置security过滤器
        http
                .addFilterBefore(authorizationFilterBean(), UsernamePasswordAuthenticationFilter.class);

        http.headers().cacheControl();
    }

    /**
     * 设置用户权限验证方式
     */
    @Override
    protected void configure(AuthenticationManagerBuilder amb) throws Exception {
        amb.userDetailsService(userDetailsService).passwordEncoder(passwordEncoder());
    }

    // 装载BCrypt密码编码器
    @Bean
    public PasswordEncoder passwordEncoder() {
        return new BCryptPasswordEncoder();
    }

}
```



##### UserDetailsImpl

说明:该类实现了security的UserDetails,进行自定义验证用户验证用户

值得注意的是,在转换的时候user role前缀必须为ROLE_,否则security会返回403状态码(我在这炸了一天)

```java
package demo.demo1.auth.security;

import demo.demo1.User.model.User;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.stream.Collectors;

public class UserDetailsImpl implements UserDetails {

    private User user;

    public UserDetailsImpl(User user) {
        this.user = user;
    }

    //将你自定义的用户角色转换为security的user role
    //值得注意的是,在转换的时候user role前缀必须为ROLE_,否则security会返回403状态码
    private static List<GrantedAuthority> mapToGrantedAuthorities(List<String> roles) {

        return roles.stream()
                .map(role -> new SimpleGrantedAuthority(role))
                .collect(Collectors.toList());

    }

    @Override
    public boolean isAccountNonLocked() {
        return true;
    }


    @Override
    public Collection<GrantedAuthority> getAuthorities() {
        List<String> roles = new ArrayList<String>() {{ add(user.getRole());}};
        if (roles == null) {
            roles = new ArrayList<String>();
        }
        return mapToGrantedAuthorities(roles);
    }

    //必须要有,可以自定义,判断用户是否被禁用
    @Override
    public boolean isEnabled() { return true; };

    @Override
    public String getPassword() {
        return user.getPassword();
    }

    @Override
    public boolean isAccountNonExpired() {
        return true;
    }

    @Override
    public boolean isCredentialsNonExpired() {
        return true;
    }

    @Override
    public String getUsername() {
        return user.getUsername();
    }
}
```



##### UserDetailsServiceImpl

说明:该类继承了UserDetailsService,自定义security用户验证,只需要实现loadUserByUsername方法

该方法正常写法就如下所示,一般不需要修改

```java
package demo.demo1.auth.security;

import demo.demo1.User.model.User;
import demo.demo1.User.service.UserService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;

@Service
public class UserDetailsServiceImpl implements UserDetailsService {

    private final static Logger logger = LoggerFactory.getLogger(UserDetailsServiceImpl.class);

    @Autowired
    private UserService userService;

    @Override
    public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {
        
        try {
            User user = userService.getUserByUserName(username);
            if (user != null) {
                return new UserDetailsImpl(user);
            } else {
                throw new UsernameNotFoundException("username not found.");
            }
        } catch (UsernameNotFoundException e) {
            throw e;
        } catch (Exception e) {
            logger.error(e.getMessage());
            throw new UsernameNotFoundException(e.getMessage());
        }
        
    }
}
```



##### UserController

说明:用来验证security的一个例子

使用@PreAuthorize("hasRole('ROLE_SENIOR')")注解限制只能ROLE_SENIOR权限的用户访问该接口

这里post接口没做限制方便实验的时候可以自定义用户

```java
package demo.demo1.User.controller;


import demo.demo1.User.model.User;
import demo.demo1.User.service.UserService;
import demo.demo1.auth.security.SecurityConfig;
import io.swagger.annotations.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.UUID;

@RestController
@RequestMapping("/user")
@Api(
        value = "/user",
        description = "用户API"
)
public class UserController {

    @Autowired
    UserService userService;

    @Autowired
    SecurityConfig securityConfig;

    @RequestMapping(value = "" , method = RequestMethod.GET)
    @PreAuthorize("hasRole('ROLE_SENIOR')")
    @ApiOperation(
            value = "get all User",
            code = 201,
            consumes = "application/json",
            produces = "application/json"
    )
    public List<User> getAllUser() {
        return userService.getAllUser();
    }

    @RequestMapping(value = "/{id}" , method = RequestMethod.GET)
    @PreAuthorize("hasRole('ROLE_SENIOR')")
    @ApiOperation(
            value = "get one user by user id",
            code = 201,
            consumes = "application/json",
            produces = "application/json"
    )
    public User getOneUser(
            @ApiParam(value = "用户UUID") @PathVariable UUID id
    ) {
        return userService.getUserById(id);
    }

    @RequestMapping(value = "" , method = RequestMethod.POST)
    //@PreAuthorize("hasRole('ROLE_SENIOR')")
    @ApiOperation(
            value = "create user",
            code = 201,
            consumes = "application/json",
            produces = "application/json"
    )
    public void create(
            @RequestBody User user
    ) throws Exception{

        user.setUsername(user.getUsername().trim());
        user.setPassword(user.getPassword().trim());
        user.setId(UUID.randomUUID());
        user.setEmail(user.getEmail().trim());

        // encode password
        BCryptPasswordEncoder passwordEncoder = (BCryptPasswordEncoder) securityConfig.passwordEncoder();
        user.setPassword(passwordEncoder.encode(user.getPassword()));

        userService.create(user);
    }

}
```



##### UserService

说明:user的service层,不需要多说

```java
package demo.demo1.User.service;

import demo.demo1.User.model.User;
import demo.demo1.User.model.UserRole;
import demo.demo1.User.repository.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.UUID;

@Service
public class UserService {

    @Autowired
    private UserRepository userRepository;

    public User create(User user) throws Exception{

        //check that the username already exists
        if(user.getUsername() == null) {
            throw new Exception("User name can not null");
        }
        if(userRepository.findByUsername(user.getUsername()) != null) {
            throw new Exception(String.format("User %S alrady exist" , user.getUsername()));
        }

        //check userRole
        if(user.getRole() != null) {
            if (!user.getRole().equals(UserRole.ROLE_LOWER.getValue()) && !user.getRole().equals(UserRole.ROLE_SENIOR.getValue()) && !user.getRole().equals(UserRole.ROLE_INTERMEDIATE.getValue())) {
                throw new Exception(String.format("User Role %s is invalid", user.getRole()));
            }
        } else {
            throw new Exception("User role can not null");
        }

        return userRepository.save(user);
    }

    public User getUserById(UUID id) {

        return userRepository.findById(id).get();

    }

    public User getUserByUserName(String name) {

        return userRepository.findByUsername(name);

    }

    public List<User> getAllUser() {

        return userRepository.findAll();

    }

}
```



##### User

说明:user bean

这里的set,get可用@Date注解,但是这里我用的builder模式,在写代码的时候service层会报红,没有安全感,所以都写上了

```java
package demo.demo1.User.model;

import com.fasterxml.jackson.annotation.JsonProperty;
import io.swagger.annotations.ApiModelProperty;
import org.hibernate.validator.constraints.NotBlank;

import javax.persistence.*;
import javax.persistence.Column;
import javax.validation.constraints.NotNull;
import javax.validation.constraints.Size;
import java.util.Set;
import java.util.UUID;

@Entity
@Table(name = "DEMO_USER" , indexes = {
        @Index(name = "IDX_USER" , columnList = "ID,USERNAME")
})
public class User {

    public User(){};

    public User(Builder builder) {
        setId(builder.id);
        setUsername(builder.username);
        setPassword(builder.password);
        setRole(builder.role);
        setEmail(builder.email);
    }

    @Id
    @Column(name = "ID")
    @org.hibernate.annotations.Type(type = "org.hibernate.type.PostgresUUIDType")
    @ApiModelProperty(value = "用户ID", required = false, example = "876C2203-7472-44E8-9EB6-13CF372D326C")
    private UUID id;

    @Column(name = "USERNAME" , length = 60)
    @NotBlank(message = "error.not_blank")
    @Size(min = 1 , max = 50 , message = "error.size")
    @ApiModelProperty(value = "用户名,长度1~50", required = true, example = "username")
    private String username;

    @Column(name = "PASSWORD", length = 60)
    @NotBlank( message = "error.not_blank")
    @Size(min = 1, max = 60 , message = "error.size")
    @ApiModelProperty(value = "密码,长度1~25", required = true, example = "r00tme")
    private String password;

    @Column(name = "ROLE" , length = 20)
    @NotNull
    private String role;

    @Column(name = "EMAIL" , length = 60)
    @Size(min = 1, max = 320, message = "error.size")
    @ApiModelProperty(value = "邮箱,长度1~60", required = true, example = "xxx@xxx.com")
    private String email;


    public UUID getId() {
        return id;
    }

    public void setId(UUID id) {
        this.id = id;
    }

    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    public String getPassword() {
        return password;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public static final class Builder {
        private UUID id;
        private String username;
        private String password;
         private String role;
        private String email;

        public Builder setId(UUID val) {
            this.id = val;
            return this;
        }

        public Builder setUsername(String val) {
            this.username = val;
            return this;
        }

        public Builder setPassword(String val) {
            this.password = val;
            return this;
        }

        public Builder setRole(String val) {
            this.role = val;
            return this;
        }


        public Builder setEmail(String val) {
            this.email = val;
            return this;
        }

        public User build() { return new User(this); }

    }

 }
```



##### UserRepository

```java
package demo.demo1.User.repository;

import demo.demo1.User.model.User;
import demo.demo1.User.model.UserRole;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.stereotype.Repository;

import java.util.UUID;

@Repository
public interface UserRepository extends JpaRepository<User, UUID>, JpaSpecificationExecutor {

    User findByUsername(String username);

}
```



### 数据流程

spring boot 集成jwt security认证大概流程（转自https://www.jianshu.com/p/ca4cebefd1cc）

![img](https://upload-images.jianshu.io/upload_images/7434356-2952ac89facd4525.jpg?imageMogr2/auto-orient/strip%7CimageView2/2/w/1000/format/webp)

首先是左边一张图，通过登陆接口获取token，该接口是任何权限都能个访问的

> http请求中携带username，password参数
>
> 经过security过滤器或者自定义的过滤器（AuthFilter），验证是否有权限访问该接口
>
> 在authController中检查用户
>
> 根据登录的用户生成相应的token，将token放在response的head中返回

然后是右边一张图，说的是如何通过携带token访问接口

> 在请求的http resquest中加入在登录是获取的token参数
>
> http request经过jwtfile验证，判断是否是一个合法的token
>
> 将token解析出来获取用户信息
>
> http request经过自定义security authfile过滤
>
> 进入资源认证器，判断是否有权限访问请求的接口



代码github

https://github.com/wheijxiaotbai/HXB_Knowledge/tree/springboot_jwt_security_demo






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

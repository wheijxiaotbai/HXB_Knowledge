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

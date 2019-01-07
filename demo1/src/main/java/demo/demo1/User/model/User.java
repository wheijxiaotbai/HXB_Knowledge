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

    public void setPassword(String password) {
        this.password = password;
    }

    public String getRole() {
        return role;
    }

    public void setRole(String role) {
        this.role = role;
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

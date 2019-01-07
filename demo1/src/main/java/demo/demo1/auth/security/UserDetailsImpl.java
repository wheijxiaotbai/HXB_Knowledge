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

    //
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


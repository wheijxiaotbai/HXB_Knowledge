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

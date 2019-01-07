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

package mapper;

import model.User;

public interface UserMapper {

    public User findUserById(int id);

    public void addUser(User user);

    public void updateUserById(User user);

    public void deleteUserByID(int id);

}

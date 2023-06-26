package ru.practicum.shareit.user;

import ru.practicum.shareit.user.model.User;

import java.util.HashMap;
import java.util.List;

public interface UserStorage {
    public User createUser(User user);

    public User updateUser(int id, User user);

    public User getUserById(int id);

    public void deleteUserById(int id);

    public HashMap<Integer, User> getUsers();

    public List<User> getAllUser();
}

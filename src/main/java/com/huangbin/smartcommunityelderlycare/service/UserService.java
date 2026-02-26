package com.huangbin.smartcommunityelderlycare.service;

import com.huangbin.smartcommunityelderlycare.entity.User;
import java.util.List;

public interface UserService {
    User register(User user);
    User login(String phone, String password);
    List<User> getAllUsers();
    List<User> getUsersByRole(String role);
    User getUserById(Long id);
    void updateAvatar(Long userId, String avatarPath);
}
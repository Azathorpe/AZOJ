package org.example.azoi.service;

import org.example.azoi.dto.userdto.UserDTO;
import org.example.azoi.model.User;

public interface UserService {
    String getUserById(Long id);

    String registerUser(UserDTO user);

    String registerUser(User user);
}

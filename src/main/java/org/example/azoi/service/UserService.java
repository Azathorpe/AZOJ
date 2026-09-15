package org.example.azoi.service;

import org.example.azoi.dto.usertransmit.UserDTO;
import org.example.azoi.model.User;

public interface UserService {
    /**
     * 通过Id获取用户，与{@link #getUserInfoById(Long id)}不同的是 这个会返回User
     * @param id
     * @return
     */
    String getUserById(Long id);

    String registerUser(UserDTO user);

    String registerUser(User user);

    /**
     * 通过Id获取某位用户的公开信息，与{@link #getUserById(Long id)}不同的是 这个会返回UserInfoVO
     * @param id
     * @return
     */
    String getUserInfoById(Long id);

    /**
     * 重设密码
     * @param id
     * @return
     */
    String resetPassword(Long id);

    /**
     * 通过Id获取当前用户详细信息，与{@link #getUserById(Long id)}不同的是 这个会返回UserCurrentVO
     * @param id
     * @return
     */
    String getCurrentUser(Long id);

    /**
     * 删除用户
     * @param id
     * @return
     */
    String deleteUser(Long id);

    String loginUser(UserDTO user);
}

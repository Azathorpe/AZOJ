package org.example.azoi.service;

import jakarta.servlet.http.HttpServletRequest;
import org.example.azoi.dto.Result;
import org.example.azoi.dto.usertransmit.UserCurrentVO;
import org.example.azoi.dto.usertransmit.UserDTO;
import org.example.azoi.dto.usertransmit.UserInfoVO;
import org.example.azoi.model.user_model.User;

import java.util.List;

public interface UserService {
    /**
     * 通过Id获取用户，与{@link #getUserInfoById(Long id)}不同的是 这个会返回User
     *
     * @param id
     * @return
     */
    Result<User> getUserById(Long id);

    Result<List<UserInfoVO>> registerUsers(List<UserDTO> user);

    Result<UserInfoVO> registerUser(UserDTO user);

    Result<UserInfoVO> registerUser(User user);

    /**
     * 通过Id获取某位用户的公开信息，与{@link #getUserById(Long id)}不同的是 这个会返回UserInfoVO
     *
     * @param id
     * @return
     */
    Result<UserInfoVO> getUserInfoById(Long id);

    /**
     * 重设密码
     *
     * @param id
     * @return
     */
    Result<Boolean> resetPassword(Long id);

    /**
     * 通过Id获取当前用户详细信息，与{@link #getUserById(Long id)}不同的是 这个会返回UserCurrentVO
     *
     * @param id
     * @return
     */
    Result<UserCurrentVO> getCurrentUser(Long id);

    /**
     * 删除用户
     *
     * @param id
     * @return
     */
    Result<String> deleteUser(Long id);

    Result<Boolean> loginUser(UserDTO user, HttpServletRequest HR);
}

package com.guyuqi.moreagent.service;

import com.guyuqi.moreagent.dto.UserAddRequest;
import com.guyuqi.moreagent.model.entity.User;
import com.guyuqi.moreagent.vo.LoginUserVO;
import com.guyuqi.moreagent.vo.UserVO;
import com.mybatisflex.core.paginate.Page;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import org.springframework.stereotype.Service;

/**
 * 用户服务接口
 *
 * @author GuYuqi
 * @version 1.0
 */
public interface UserService {

    /**
     * 用户注册
     *
     * @param userAccount   用户账号
     * @param userPassword  用户密码
     * @param checkPassword 确认密码
     * @return 新用户 ID
     */
    public long userRegister(String userAccount, String userPassword, String checkPassword);

    /**
     * 用户登录
     *
     * @param userAccount  用户账号
     * @param userPassword 用户密码
     * @return 用户 ID
     */
    public LoginUserVO userLogin(String userAccount, String userPassword, HttpServletRequest request);

    /**
     * 获取登录用户信息
     *
     * @param request HTTP 请求对象
     * @return 登录用户信息
     */
    public User getLoginUser(HttpServletRequest request);

    /**
     * 用户注销
     *
     * @param request HTTP 请求对象
     * @return 是否注销成功
     */
    public boolean userLogout(HttpServletRequest request);

    /**
     * 密码加密
     *
     * @param userPassword 用户密码
     * @return 加密后的密码
     */
    public String getEncryptPassword(String userPassword);

    /**
     * 获取登录用户信息 VO
     *
     * @param user 用户实体
     * @return 登录用户信息 VO
     */
    public LoginUserVO getLoginUserVO(User user);

    /**
     * 获取用户角色
     *
     * @param request HTTP 请求对象
     * @return 用户角色
     */
    public String getUserRole(HttpServletRequest request);

    /**
     * 添加用户
     *
     * @param userAccount  用户账号
     * @param userPassword 用户密码
     * @param userName     用户姓名
     * @param userRole     用户角色
     * @return 新用户 ID
     */
    public Long addUser(String userAccount, String userPassword, String userName, String userRole);

    /**
     * 删除用户
     *
     * @param id 用户 ID
     * @return 是否删除成功
     */
    public Boolean deleteUser(Long id);

    /**
     * 更新用户信息（仅管理员可用）
     *
     * @param id             用户 ID
     * @param userName       用户姓名
     * @param userAvatarUrl  用户头像 URL
     * @param userProfile    用户简介
     * @param userRole       用户角色
     * @return 是否更新成功
     */
    public Boolean updateUser(Long id, String userName, String userAvatarUrl, String userProfile, String userRole);

    /**
     * 更新用户信息（仅用户本人可用）
     *
     * @param request        HTTP 请求对象
     * @param userName       用户姓名
     * @param userAvatarUrl  用户头像 URL
     * @param userProfile    用户简介
     * @return 是否更新成功
     */
    public Boolean updateUserMy(HttpServletRequest request, String userName, String userAvatarUrl, String userProfile);

    /**
     * 获取用户信息
     *
     * @param id 用户 ID
     * @return 用户信息
     */
    public UserVO getUserById(Long id);

    /**
     * 分页获取用户列表
     *
     * @param current   当前页码
     * @param pageSize  每页记录数
     * @param userName  用户姓名（可选）
     * @param userRole  用户角色（可选）
     * @return 包含用户信息 VO 的分页对象
     */
    public Page<UserVO> listUsersByPage(Integer current, Integer pageSize, String userName, String userRole);
}


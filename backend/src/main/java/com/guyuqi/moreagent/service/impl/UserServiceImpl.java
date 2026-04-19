package com.guyuqi.moreagent.service.impl;

import cn.hutool.core.bean.BeanUtil;
import cn.hutool.core.util.StrUtil;
import cn.hutool.crypto.digest.DigestUtil;
import cn.hutool.log.Log;
import com.guyuqi.moreagent.annotation.AuthCheck;
import com.guyuqi.moreagent.common.ResultUtils;
import com.guyuqi.moreagent.dto.UserAddRequest;
import com.guyuqi.moreagent.model.entity.User;
import com.guyuqi.moreagent.exception.ErrorCode;
import com.guyuqi.moreagent.exception.ThrowUtils;
import com.guyuqi.moreagent.mapper.UserMapper;
import com.guyuqi.moreagent.model.enums.UserRoleEnum;
import com.guyuqi.moreagent.service.UserService;
import com.guyuqi.moreagent.vo.LoginUserVO;
import com.guyuqi.moreagent.vo.UserVO;
import com.mybatisflex.core.paginate.Page;
import com.mybatisflex.core.query.QueryChain;
import jakarta.servlet.http.HttpServletRequest;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.Date;
import java.util.List;
import java.util.stream.Collectors;

import static com.guyuqi.moreagent.constant.UserConstant.USER_LOGIN_STATE;

/**
 * @author GuYuqi
 * @version 1.0
 */
@Service
public class UserServiceImpl implements UserService {

    @Autowired
    private UserMapper userMapper;

    /**
     * 用户注册
     *
     * @param userAccount   用户账号
     * @param userPassword  用户密码
     * @param checkPassword 确认密码
     * @return 新用户 ID
     */
    @Override
    public long userRegister(String userAccount, String userPassword, String checkPassword) {
        // 1. 校验参数
        // 是否为空
        ThrowUtils.throwIf(StrUtil.isBlank(userAccount), ErrorCode.PARAMS_ERROR, "用户账号不能为空");
        ThrowUtils.throwIf(StrUtil.isBlank(userPassword), ErrorCode.PARAMS_ERROR, "用户密码不能为空");
        ThrowUtils.throwIf(StrUtil.isBlank(checkPassword), ErrorCode.PARAMS_ERROR, "确认密码不能为空");
        // 账号大于 4 位，密码大于 8 位，且两次输入的密码相同
        ThrowUtils.throwIf(userAccount.length() < 4, ErrorCode.PARAMS_ERROR, "用户账号长度不能小于 4 位");
        ThrowUtils.throwIf(userPassword.length() < 8, ErrorCode.PARAMS_ERROR, "用户密码长度不能小于 8 位");
        ThrowUtils.throwIf(!userPassword.equals(checkPassword), ErrorCode.PARAMS_ERROR, "两次输入的密码不一致");
        // 2. 校验账户存在
        long count = QueryChain.of(userMapper).eq(User::getUseraccount, userAccount).count();
        ThrowUtils.throwIf(count != 0, ErrorCode.PARAMS_ERROR, "用户账号已存在");
        // 3. 密码加密
        String encryptPassword = getEncryptPassword(userPassword);
        // 4. 插入数据
        User user = User.builder()
                .useraccount(userAccount)
                .userpassword(encryptPassword)
                .username("user_" + userAccount)
                .userrole(UserRoleEnum.USER.getValue())
                .build();
        int insert = userMapper.insert(user);
        ThrowUtils.throwIf(insert != 1, ErrorCode.SYSTEM_ERROR, "用户注册失败，数据库异常");
        // 5. 返回新用户 ID
        return user.getId();
    }

    /**
     * 用户登录
     *
     * @param userAccount  用户账号
     * @param userPassword 用户密码
     * @return 用户 ID
     */
    @Override
    public LoginUserVO userLogin(String userAccount, String userPassword, HttpServletRequest request) {
        // 1. 校验参数
        ThrowUtils.throwIf(StrUtil.isBlank(userAccount), ErrorCode.PARAMS_ERROR, "用户账号不能为空");
        ThrowUtils.throwIf(StrUtil.isBlank(userPassword), ErrorCode.PARAMS_ERROR, "用户密码不能为空");
        // 2. 密码加密
        String encryptPassword = getEncryptPassword(userPassword);
        // 3. 查询用户是否存在
        User user = QueryChain.of(userMapper)
                .eq(User::getUseraccount, userAccount)
                .eq(User::getUserpassword, encryptPassword)
                .one();
        ThrowUtils.throwIf(user == null, ErrorCode.PARAMS_ERROR, "用户账号不存在或密码错误");
        // 4. 记录用户登录态
        request.getSession().setAttribute(USER_LOGIN_STATE, user);
        // 5. 返回用户信息
        return this.getLoginUserVO(user);
    }

    /**
     * 获取登录用户信息
     *
     * @param request HTTP 请求对象
     * @return 登录用户信息
     */
    @Override
    public User getLoginUser(HttpServletRequest request) {
        // 1. 判断用户是否登录
        Object userObj = request.getSession().getAttribute(USER_LOGIN_STATE);
        User currentUser = (User) userObj;
        ThrowUtils.throwIf(currentUser == null, ErrorCode.NOT_LOGIN_ERROR, "用户未登录");
        // 2. 获取登录用户信息
        long userId = currentUser.getId();
        currentUser = userMapper.selectOneById(userId);
        //从数据库重新查用户，有可能用户在 Session 有效期内被删除了（比如管理员封号），所以再判断一次确保用户在数据库里真实存在
        ThrowUtils.throwIf(currentUser == null, ErrorCode.NOT_LOGIN_ERROR, "用户未登录");
        return currentUser;
    }

    /**
     * 用户注销
     *
     * @param request HTTP 请求对象
     * @return 是否注销成功
     */
    @Override
    public boolean userLogout(HttpServletRequest request) {
        // 1. 判断用户是否登录
        Object userObj = request.getSession().getAttribute(USER_LOGIN_STATE);
        ThrowUtils.throwIf(userObj == null, ErrorCode.NOT_LOGIN_ERROR, "用户未登录");
        // 2. 移除用户登录态
        request.getSession().removeAttribute(USER_LOGIN_STATE);
        return true;
    }

    /**
     * 密码加密
     *
     * @param userPassword 用户密码
     * @return 加密后的密码
     */
    @Override
    public String getEncryptPassword(String userPassword) {
        String salt = "ThisIsASpringAiProjectByRichardGu";
        return DigestUtil.md5Hex(salt + userPassword);
    }

    /**
     * 获取登录用户信息 VO
     *
     * @param user 用户实体
     * @return 登录用户信息 VO
     */
    @Override
    public LoginUserVO getLoginUserVO(User user) {
        LoginUserVO loginUserVO = new LoginUserVO();
        BeanUtil.copyProperties(user, loginUserVO);
        return loginUserVO;
    }

    /**
     * 获取用户角色
     *
     * @param request HTTP 请求对象
     * @return 用户角色
     */
    @Override
    public String getUserRole(HttpServletRequest request) {
        // 1. 判断用户是否登录
        Object userObj = request.getSession().getAttribute(USER_LOGIN_STATE);
        User currentUser = (User) userObj;
        ThrowUtils.throwIf(currentUser == null, ErrorCode.NOT_LOGIN_ERROR, "用户未登录");
        // 2. 获取登录用户信息并返回
        return currentUser.getUserrole();
    }

    /**
     * 添加用户（管理员接口）
     *
     * @param userAccount  用户账号
     * @param userPassword 用户密码
     * @param userName     用户姓名
     * @param userRole     用户角色
     * @return 新用户 ID
     */
    @Override
    public Long addUser(String userAccount, String userPassword, String userName, String userRole) {
        // 1，校验参数
        ThrowUtils.throwIf(StrUtil.isBlank(userAccount), ErrorCode.PARAMS_ERROR, "用户账号不能为空");
        ThrowUtils.throwIf(StrUtil.isBlank(userPassword), ErrorCode.PARAMS_ERROR, "用户密码不能为空");
        ThrowUtils.throwIf(StrUtil.isBlank(userName), ErrorCode.PARAMS_ERROR, "用户姓名不能为空");
        ThrowUtils.throwIf(StrUtil.isBlank(userRole), ErrorCode.PARAMS_ERROR, "用户角色不能为空");
        // 账号大于 4 位，密码大于 8 位
        ThrowUtils.throwIf(userAccount.length() < 4, ErrorCode.PARAMS_ERROR, "用户账号长度不能小于 4 位");
        ThrowUtils.throwIf(userPassword.length() < 8, ErrorCode.PARAMS_ERROR, "用户密码长度不能小于 8 位");
        // 角色必须是 user 或 admin
        UserRoleEnum userRoleEnum = UserRoleEnum.getEnumByValue(userRole);
        ThrowUtils.throwIf(userRoleEnum == null, ErrorCode.PARAMS_ERROR, "用户角色不合法");
        // 2. 校验账户存在
        long count = QueryChain.of(userMapper).eq(User::getUseraccount, userAccount).count();
        ThrowUtils.throwIf(count != 0, ErrorCode.PARAMS_ERROR, "用户账号已存在");
        // 3. 密码加密
        String encryptPassword = getEncryptPassword(userPassword);
        // 4. 插入数据
        User user = User.builder()
                .useraccount(userAccount)
                .userpassword(encryptPassword)
                .username("user_" + userAccount)
                .userrole(userRole)
                .build();
        int insert = userMapper.insert(user);
        ThrowUtils.throwIf(insert != 1, ErrorCode.SYSTEM_ERROR, "添加用户失败，数据库异常");
        // 5. 返回新用户 ID
        return user.getId();
    }

    /**
     * 删除用户（管理员接口）
     *
     * @param id 用户 ID
     * @return 是否删除成功
     */
    @Override
    @AuthCheck(mustRole = "admin")
    public Boolean deleteUser(Long id) {
        // 1. 校验参数
        ThrowUtils.throwIf(id == null || id <= 0, ErrorCode.PARAMS_ERROR, "用户 ID 不合法");
        // 2. 判断用户是否存在
        User user = userMapper.selectOneById(id);
        ThrowUtils.throwIf(user == null, ErrorCode.PARAMS_ERROR, "用户不存在");
        // 3. 删除用户
        int i = userMapper.deleteById(user.getId());
        ThrowUtils.throwIf(i != 1, ErrorCode.SYSTEM_ERROR, "删除用户失败，数据库异常");
        return true;
    }

    /**
     * 更新用户信息（管理员接口）
     *
     * @param id            用户 ID
     * @param userName      用户姓名
     * @param userAvatarUrl 用户头像 URL
     * @param userProfile   用户简介
     * @param userRole      用户角色
     * @return 是否更新成功
     */
    @Override
    public Boolean updateUser(Long id, String userName, String userAvatarUrl, String userProfile, String userRole) {
        // 1. 校验参数
        ThrowUtils.throwIf(id == null || id <= 0, ErrorCode.PARAMS_ERROR, "用户 ID 不合法");
        // 2. 判断用户是否存在
        User user = userMapper.selectOneById(id);
        ThrowUtils.throwIf(user == null, ErrorCode.PARAMS_ERROR, "用户不存在");
        // 3. 更新用户信息
        user.setUsername(userName);
        user.setUseravatar(userAvatarUrl);
        user.setUserprofile(userProfile);
        user.setUserrole(userRole);
        user.setEdittime(new Date());
        int update = userMapper.update(user, true);
        ThrowUtils.throwIf(update != 1, ErrorCode.SYSTEM_ERROR, "更新用户失败，数据库异常");
        return true;
    }

    /**
     * 更新用户信息（用户本人接口）
     *
     * @param request       HTTP 请求对象
     * @param userName      用户姓名
     * @param userAvatarUrl 用户头像 URL
     * @param userProfile   用户简介
     * @return 是否更新成功
     */
    @Override
    public Boolean updateUserMy(HttpServletRequest request, String userName, String userAvatarUrl, String userProfile) {
        // 1. 判断用户是否登录
        // 不能直接传 id，因为用户可能会伪造请求来修改别人的信息，所以只能从登录态里获取用户 ID，确保只能修改自己的信息
        // 也不能只在前端限制，因为用户可能通过 ApiFox 等工具绕过前端限制来修改别人的信息，所以后端也要做校验
        User loginUser = getLoginUser(request);
        // 2. 判断用户是否存在
        User user = userMapper.selectOneById(loginUser.getId());
        ThrowUtils.throwIf(user == null, ErrorCode.PARAMS_ERROR, "用户不存在");
        // 3. 更新用户信息
        user.setUsername(userName);
        user.setUseravatar(userAvatarUrl);
        user.setUserprofile(userProfile);
        user.setEdittime(new Date());
        int update = userMapper.update(user, true);
        ThrowUtils.throwIf(update != 1, ErrorCode.SYSTEM_ERROR, "更新用户失败，数据库异常");
        return true;
    }

    /**
     * 获取用户信息
     *
     * @param id 用户 ID
     * @return 用户信息
     */
    @Override
    public UserVO getUserById(Long id) {
        // 1. 校验参数
        ThrowUtils.throwIf(id == null || id <= 0, ErrorCode.PARAMS_ERROR, "用户 ID 不合法");
        // 2. 判断用户是否存在
        User user = userMapper.selectOneById(id);
        ThrowUtils.throwIf(user == null, ErrorCode.PARAMS_ERROR, "用户不存在");
        // 3. 返回用户信息
        UserVO userVO = new UserVO();
        BeanUtil.copyProperties(user, userVO);
        return userVO;
    }

    /**
     * 分页获取用户列表
     *
     * @param current    当前页码
     * @param pageSize   每页条数
     * @param userName   用户姓名（可选）
     * @param userRole   用户角色（可选）
     * @return 包含用户信息的分页对象
     */
    @Override
    public Page<UserVO> listUsersByPage(Integer current, Integer pageSize, String userName, String userRole) {
        // 1. 校验参数
        ThrowUtils.throwIf(current == null || current <= 0, ErrorCode.PARAMS_ERROR, "当前页码不合法");
        ThrowUtils.throwIf(pageSize == null || pageSize <= 0 || pageSize > 100, ErrorCode.PARAMS_ERROR, "每页条数不合法");
        // 2. 构造查询条件
        QueryChain<User> queryChain = QueryChain.of(userMapper);
        if (StrUtil.isNotBlank(userName)) {
            queryChain.like(User::getUsername, userName);
        }
        if (StrUtil.isNotBlank(userRole)) {
            queryChain.eq(User::getUserrole, userRole);
        }
        Page<User> page = userMapper.paginate(current, pageSize, queryChain);
        // 3. 执行分页查询
        Page<UserVO> userVOPage = new Page<>();
        userVOPage.setTotalRow(page.getTotalRow());
        List<UserVO> userVOList = page.getRecords().stream()
                .map(user -> {
                    UserVO userVO = new UserVO();
                    BeanUtil.copyProperties(user, userVO);
                    return userVO;
                }).collect(Collectors.toList());
        userVOPage.setRecords(userVOList);
        return userVOPage;
    }


}

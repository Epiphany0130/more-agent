package com.guyuqi.moreagent.controller;

import com.guyuqi.moreagent.annotation.AuthCheck;
import com.guyuqi.moreagent.common.BaseResponse;
import com.guyuqi.moreagent.common.ResultUtils;
import com.guyuqi.moreagent.dto.*;
import com.guyuqi.moreagent.exception.ErrorCode;
import com.guyuqi.moreagent.exception.ThrowUtils;
import com.guyuqi.moreagent.model.entity.User;
import com.guyuqi.moreagent.service.UserService;
import com.guyuqi.moreagent.service.impl.UserServiceImpl;
import com.guyuqi.moreagent.vo.LoginUserVO;
import com.guyuqi.moreagent.vo.UserVO;
import com.mybatisflex.core.paginate.Page;
import jakarta.servlet.http.HttpServletRequest;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

/**
 * 用户接口
 * @author GuYuqi
 * @version 1.0
 */
@RestController
@RequestMapping("/user")
public class UserController {

    @Autowired
    private UserService userService;

    /**
     * 用户注册接口
     *
     * @param userRegisterRequest 用户注册请求对象
     * @return 包含新用户 ID 的响应对象
     */
    @PostMapping("/register")
    public BaseResponse<Long> userRegister(@RequestBody UserRegisterRequest userRegisterRequest) {
        ThrowUtils.throwIf(userRegisterRequest == null, ErrorCode.PARAMS_ERROR, "请求参数不能为空");
        long userId = userService.userRegister(userRegisterRequest.getUserAccount(),
                userRegisterRequest.getUserPassword(),
                userRegisterRequest.getCheckPassword());
        return ResultUtils.success(userId);
    }

    /**
     * 用户登录接口
     *
     * @param userLoginRequest 用户登录请求对象
     * @param request HTTP 请求对象
     * @return 包含登录用户信息 VO 的响应对象
     */
    @PostMapping("/login")
    public BaseResponse<LoginUserVO> userLogin(@RequestBody UserLoginRequest userLoginRequest, HttpServletRequest request) {
        ThrowUtils.throwIf(userLoginRequest == null, ErrorCode.PARAMS_ERROR, "请求参数不能为空");
        LoginUserVO loginUserVO = userService.userLogin(userLoginRequest.getUserAccount(),
                userLoginRequest.getUserPassword(), request);
        return ResultUtils.success(loginUserVO);
    }

    /**
     * 用户注销接口
     *
     * @param request HTTP 请求对象
     * @return 是否注销成功
     */
    @PostMapping("/logout")
    public BaseResponse<Boolean> userLogout(HttpServletRequest request) {
        boolean result = userService.userLogout(request);
        return ResultUtils.success(result);
    }

    /**
     * 获取当前登录用户信息接口
     *
     * @param request HTTP 请求对象
     * @return 包含当前登录用户信息的响应对象
     */
    @GetMapping("/get/login")
    public BaseResponse<LoginUserVO> getLoginUser(HttpServletRequest request) {
        User user = userService.getLoginUser(request);
        return ResultUtils.success(userService.getLoginUserVO(user));
    }

    /**
     * 添加用户接口（仅管理员可用）
     *
     * @param userAddRequest 用户添加请求对象
     * @return 包含新用户 ID 的响应对象
     */
    @PostMapping("/add")
    @AuthCheck(mustRole = "admin")
    public BaseResponse<Long> addUser(@RequestBody UserAddRequest userAddRequest) {
        ThrowUtils.throwIf(userAddRequest == null, ErrorCode.PARAMS_ERROR, "请求参数不能为空");
        Long userId = userService.addUser(userAddRequest.getUserAccount(),
                userAddRequest.getUserPassword(),
                userAddRequest.getUsername(),
                userAddRequest.getUserRole());
        return ResultUtils.success(userId);
    }

    /**
     * 删除用户接口（仅管理员可用）
     *
     * @param userDeleteRequest 用户删除请求对象
     * @return 是否删除成功
     */
    @PostMapping("/delete")
    @AuthCheck(mustRole = "admin")
    public BaseResponse<Boolean> deleteUser(@RequestBody UserDeleteRequest userDeleteRequest) {
        ThrowUtils.throwIf(userDeleteRequest == null, ErrorCode.PARAMS_ERROR, "请求参数不能为空");
        Boolean result = userService.deleteUser(userDeleteRequest.getId());
        return ResultUtils.success(result);
    }

    /**
     * 更新用户接口（仅管理员可用）
     *
     * @param userUpdateRequest 用户更新请求对象
     * @return 是否更新成功
     */
    @PostMapping("/update")
    @AuthCheck(mustRole = "admin")
    public BaseResponse<Boolean> updateUser(@RequestBody UserUpdateRequest userUpdateRequest) {
        ThrowUtils.throwIf(userUpdateRequest == null, ErrorCode.PARAMS_ERROR, "请求参数不能为空");
        Boolean result = userService.updateUser(userUpdateRequest.getId(),
                userUpdateRequest.getUserName(),
                userUpdateRequest.getUserAvatarUrl(),
                userUpdateRequest.getUserProfile(),
                userUpdateRequest.getUserRole());
        return ResultUtils.success(result);
    }

    /**
     * 更新用户接口（仅用户本人可用）
     *
     * @param userUpdateMyRequest 用户更新请求对象
     * @return 是否更新成功
     */
    @PostMapping("/update/my")
    public BaseResponse<Boolean> updateUserMy(@RequestBody UserUpdateMyRequest userUpdateMyRequest, HttpServletRequest request) {
        ThrowUtils.throwIf(userUpdateMyRequest == null, ErrorCode.PARAMS_ERROR, "请求参数不能为空");
        Boolean result = userService.updateUserMy(request,
                userUpdateMyRequest.getUserName(),
                userUpdateMyRequest.getUserAvatarUrl(),
                userUpdateMyRequest.getUserProfile());
        return ResultUtils.success(result);
    }

    /**
     * 获取用户信息接口
     *
     * @param id 用户 ID
     * @return 包含用户信息 VO 的响应对象
     */
    @GetMapping("/get")
    // @RequestBody → 接收 JSON 格式的请求体，POST 用
    // @RequestParam → 接收 URL 参数，如 /get?id=1，GET 用
    // @PathVariable → 接收路径参数，如 /get/1，GET 用
    public BaseResponse<UserVO> getUserById(@RequestParam Long id) {
        ThrowUtils.throwIf(id == null, ErrorCode.PARAMS_ERROR, "请求参数不能为空");
        UserVO userVO = userService.getUserById(id);
        return ResultUtils.success(userVO);
    }

    /**
     * 分页获取用户列表接口
     *
     * @param userPageRequest 用户分页请求对象
     * @return 包含用户信息 VO 的分页对象的响应对象
     */
    @PostMapping("/list/page")
    public BaseResponse<Page<UserVO>> getUsersByPage(@RequestBody UserPageRequest userPageRequest) {
        ThrowUtils.throwIf(userPageRequest == null, ErrorCode.PARAMS_ERROR, "请求参数不能为空");
        Page<UserVO> userVOPage = userService.listUsersByPage(userPageRequest.getCurrent(),
                userPageRequest.getPageSize(),
                userPageRequest.getUserName(),
                userPageRequest.getUserRole());
        return ResultUtils.success(userVOPage);
    }
}

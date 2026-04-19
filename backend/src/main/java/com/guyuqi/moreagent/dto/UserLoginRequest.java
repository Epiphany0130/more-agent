package com.guyuqi.moreagent.dto;

import lombok.Data;

import java.io.Serializable;

/**
 * 用户登录请求 DTO
 *
 * @author GuYuqi
 * @version 1.0
 */
@Data
public class UserLoginRequest implements Serializable {

    private String userAccount;

    private String userPassword;
}

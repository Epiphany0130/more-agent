package com.guyuqi.moreagent.dto;

import lombok.Data;

import java.io.Serializable;

/**
 * 用户添加请求 DTO
 *
 * @author GuYuqi
 * @version 1.0
 */
@Data
public class UserAddRequest implements Serializable {

    private String userAccount;

    private String userPassword;

    private String username;

    private String userRole;

}

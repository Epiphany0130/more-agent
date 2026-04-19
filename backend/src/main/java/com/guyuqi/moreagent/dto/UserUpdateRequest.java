package com.guyuqi.moreagent.dto;

import lombok.Data;

import java.io.Serializable;

/**
 * 用户更新请求 DTO（管理员可用）
 *
 * @author GuYuqi
 * @version 1.0
 */
@Data
public class UserUpdateRequest implements Serializable {

    private Long id;

    private String userName;

    private String userAvatarUrl;

    private String userProfile;

    private String userRole;

}

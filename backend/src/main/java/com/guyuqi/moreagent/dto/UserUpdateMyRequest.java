package com.guyuqi.moreagent.dto;

import lombok.Data;

import java.io.Serializable;

/**
 * 用户更新请求 DTO（用户可用）
 *
 * @author GuYuqi
 * @version 1.0
 */
@Data
public class UserUpdateMyRequest implements Serializable {

    private Long id;

    private String userName;

    private String userAvatarUrl;

    private String userProfile;

}

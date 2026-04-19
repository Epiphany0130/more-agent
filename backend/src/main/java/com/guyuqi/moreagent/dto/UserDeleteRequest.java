package com.guyuqi.moreagent.dto;

import lombok.Data;

import java.io.Serializable;

/**
 * 用户删除请求 DTO
 *
 * @author GuYuqi
 * @version 1.0
 */
@Data
public class UserDeleteRequest implements Serializable {

    /**
     * id
     */
    private Long id;
}
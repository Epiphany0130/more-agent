package com.guyuqi.moreagent.vo;

import lombok.Data;

import java.io.Serializable;
import java.util.Date;

/**
 * 用户登录信息 VO 脱敏后返回给前端
 *
 * @author GuYuqi
 * @version 1.0
 */
@Data
public class UserVO implements Serializable {

    private Long id;
    private String username;
    private String useravatar;
    private String userprofile;
    private Date createtime;
    private Date updatetime;

}

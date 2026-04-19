package com.guyuqi.moreagent.vo;

import com.mybatisflex.annotation.Column;
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
public class LoginUserVO implements Serializable {

    private Long id;
    private String useraccount;
    private String username;
    private String useravatar;
    private String userprofile;
    private String userrole;
    private Date createtime;
    private Date updatetime;

}

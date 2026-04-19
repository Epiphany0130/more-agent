package com.guyuqi.moreagent.controller;

import com.guyuqi.moreagent.common.BaseResponse;
import com.guyuqi.moreagent.common.ResultUtils;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.*;

/**
 * 健康检查接口
 * @author GuYuqi
 * @version 1.0
 */
@RestController
@RequestMapping("/health")
public class HealthController {

    @GetMapping("/")
    public BaseResponse<String> healthCheck() {
        return ResultUtils.success("ok");
    }
}


package com.lihainuo.imgify.server.controller;

import com.lihainuo.imgify.server.common.BaseResponse;
import com.lihainuo.imgify.server.common.ResultUtils;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@Api(tags = "健康模块")
@RestController
@RequestMapping("/")
public class HealthController {


    /**
     * 健康检查
     */
    @ApiOperation(value = "健康检查")
    @GetMapping("health")
    public BaseResponse<String> health() {
        return ResultUtils.success("ok");
    }

}

package com.lihainuo.imgify.server.controller;

import com.lihainuo.imgify.server.annotation.AuthCheck;
import com.lihainuo.imgify.server.common.BaseResponse;
import com.lihainuo.imgify.server.common.ResultUtils;
import com.lihainuo.imgify.server.exception.BusinessException;
import com.lihainuo.imgify.server.exception.ErrorCode;
import com.lihainuo.imgify.server.manager.CosManager;
import com.lihainuo.imgify.server.model.constant.UserConstant;
import com.qcloud.cos.model.COSObject;
import com.qcloud.cos.model.COSObjectInputStream;
import com.qcloud.cos.utils.IOUtils;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import javax.servlet.http.HttpServletResponse;
import java.io.File;
import java.io.IOException;

@Api(tags = "文件模块")
@Slf4j
@RestController
@RequestMapping("/file")
public class FileController {

    private final CosManager cosManager;

    public FileController(CosManager cosManager) {
        this.cosManager = cosManager;
    }

    @ApiOperation(value = "测试上传文件")
    @PostMapping("/test/upload")
    @AuthCheck(mustRole = UserConstant.ADMIN_ROLE)
    public BaseResponse<String> testUploadFile(@RequestPart("file") MultipartFile multipartFile) {
        // 文件目录
        String filename = multipartFile.getOriginalFilename(); // 获取原始文件名 包含扩展名
        String filepath = String.format("/test/%s", filename); // 构建文件存储路径
        File file = null;
        try {
            // 上传文件
            file = File.createTempFile(filepath, null); // 创建临时文件 用于临时存储上传的文件内容
            multipartFile.transferTo(file); // 将上传内容转移到临时文件
            cosManager.putObject(filepath, file); // 上传文件到云存储
            // 返回可访问地址
            return ResultUtils.success(filepath);
        } catch (IOException e) {
            log.error("file upload error, filepath = {}", filepath, e);
            throw new BusinessException(ErrorCode.SYSTEM_ERROR, "上传文件失败");
        } finally {
            if (file != null) {
                // 删除临时文件
                boolean delete = file.delete();
                if (!delete) {
                    log.error("file delete error, filepath = {}", filepath);
                }
            }
        }
    }

    @ApiOperation("测试下载文件")
    @GetMapping("/test/download")
    @AuthCheck(mustRole = UserConstant.ADMIN_ROLE)
    public void testDownloadFile(String filepath, HttpServletResponse response) {
        COSObjectInputStream cosObjectInputStream = null;
        try {
            COSObject cosObject = cosManager.getObject(filepath);
            cosObjectInputStream = cosObject.getObjectContent();
            // 处理下载到的流
            byte[] bytes = IOUtils.toByteArray(cosObjectInputStream);
            // 设置响应头
            response.setContentType("application/octet-stream;charset=UTF-8");
            response.setHeader("Content-Disposition", "attachment;filename=" + filepath);
            // 写入响应
            response.getOutputStream().write(bytes);
            response.getOutputStream().flush();
        } catch (Exception e) {
            log.error("testDownloadFile error, filepath = {}, e = ", filepath, e);
            throw new BusinessException(ErrorCode.SYSTEM_ERROR, "下载文件失败");
        } finally {
            if (cosObjectInputStream != null) {
                cosObjectInputStream.release();
            }
        }
    }
}

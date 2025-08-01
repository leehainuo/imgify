package com.lihainuo.imgify.server.service;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.lihainuo.imgify.server.model.dto.picture.PictureQueryRequest;
import com.lihainuo.imgify.server.model.dto.picture.PictureReviewRequest;
import com.lihainuo.imgify.server.model.dto.picture.PictureUploadByBatchRequest;
import com.lihainuo.imgify.server.model.dto.picture.PictureUploadRequest;
import com.lihainuo.imgify.server.model.entity.Picture;
import com.baomidou.mybatisplus.extension.service.IService;
import com.lihainuo.imgify.server.model.entity.User;
import com.lihainuo.imgify.server.model.vo.PictureVO;
import org.springframework.web.multipart.MultipartFile;

import javax.servlet.http.HttpServletRequest;
import java.util.List;

/**
* @author leehainuo
* @description 针对表【picture(图片)】的数据库操作Service
* @createDate 2025-07-26 16:45:17
*/
public interface PictureService extends IService<Picture> {

    /**
     * 上传图片
     * @param inputSource - 输入源
     * @param pictureUploadRequest - 上传图片请求
     * @param loginUser - 登录用户
     * @return 封装后的图片数据
     */
    PictureVO uploadPicture(
            Object inputSource,
            PictureUploadRequest pictureUploadRequest,
            User loginUser
    );

    /**
     * 获取图片分页查询条件
     * @param pictureQueryRequest - 图片查询请求
     * @return 图片查询条件
     */
    QueryWrapper<Picture> getQueryWrapper(PictureQueryRequest pictureQueryRequest);

    /**
     * 封装单个图片
     * @param picture - 图片
     * @return 封装的图片数据
     */
    PictureVO getPictureVO(Picture picture);

    /**
     * 封装图片列表
     * @param picturePage - 图片列表数据
     * @param request - 请求
     * @return 封装的图片列表数据
     */
    Page<PictureVO> getPictureVOPage(Page<Picture> picturePage, HttpServletRequest request);

    /**
     * 图片数据校验
     * @param picture - 图片数据
     */
    void validPicture(Picture picture);

    /**
     * 图片数据审核
     * @param pictureReviewRequest - 图片数据审核请求
     * @param loginUser - 登录用户
     */
    void doPictureReview(PictureReviewRequest pictureReviewRequest, User loginUser);

    /**
     * 填充图片数据审核参数
     * @param picture - 图片数据
     * @param loginUser - 登录用户
     */
    void fillReviewParam(Picture picture, User loginUser);

    /**
     * 批量爬取和上传图片
     * @param pictureUploadByBatchRequest - 图片批量上传请求
     * @param loginUser - 登录用户
     * @return 成功批量上传图片数量
     */
    Integer uploadPictureByBatch(
            PictureUploadByBatchRequest pictureUploadByBatchRequest,
            User loginUser
    );

    void clearPictureFile(Picture oldPicture);
}

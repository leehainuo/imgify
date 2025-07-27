package com.lihainuo.imgify.server.service.impl;

import cn.hutool.core.collection.CollUtil;
import cn.hutool.core.util.ObjUtil;
import cn.hutool.core.util.StrUtil;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.lihainuo.imgify.server.exception.ErrorCode;
import com.lihainuo.imgify.server.exception.ThrowUtils;
import com.lihainuo.imgify.server.manager.FileManager;
import com.lihainuo.imgify.server.model.dto.picture.PictureQueryRequest;
import com.lihainuo.imgify.server.model.dto.picture.PictureUploadRequest;
import com.lihainuo.imgify.server.model.dto.picture.PictureUploadResult;
import com.lihainuo.imgify.server.model.entity.Picture;
import com.lihainuo.imgify.server.model.entity.User;
import com.lihainuo.imgify.server.model.vo.PictureVO;
import com.lihainuo.imgify.server.model.vo.UserVO;
import com.lihainuo.imgify.server.service.PictureService;
import com.lihainuo.imgify.server.mapper.PictureMapper;
import com.lihainuo.imgify.server.service.UserService;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import javax.servlet.http.HttpServletRequest;
import java.util.*;
import java.util.stream.Collectors;

/**
* @author leehainuo
* @description 针对表【picture(图片)】的数据库操作Service实现
* @createDate 2025-07-26 16:45:17
*/
@Service
public class PictureServiceImpl extends ServiceImpl<PictureMapper, Picture> implements PictureService{

    private final FileManager fileManager;
    private final UserService userService;

    public PictureServiceImpl(FileManager fileManager, UserService userService) {
        this.fileManager = fileManager;
        this.userService = userService;
    }

    /**
     * 上传图片
     * @param multipartFile - 图片文件
     * @param pictureUploadRequest - 上传图片请求
     * @param loginUser - 登录用户
     * @return 封装后的图片数据
     */
    @Override
    public PictureVO uploadPicture(MultipartFile multipartFile, PictureUploadRequest pictureUploadRequest, User loginUser) {
        ThrowUtils.throwIf(loginUser == null, ErrorCode.NOT_LOGIN_ERROR);
        // 用于判断是新增还是更新图片
        Long pictureId = null;
        if (pictureUploadRequest != null) {
            pictureId = pictureUploadRequest.getId();
        }
        // 如果是更新，需要校验图片是否存在
        if (pictureId != null) {
            boolean existed = this.lambdaQuery()
                    .eq(Picture::getId, pictureId)
                    .exists();
            ThrowUtils.throwIf(!existed, ErrorCode.NOT_FOUND_ERROR);
        }
        // 上传图片，得到信息
        // 按照用户 id 划分目录
        String uploadPathPrefix = String.format("public/%s", loginUser.getId());
        PictureUploadResult pictureUploadResult = fileManager.uploadPicture(multipartFile, uploadPathPrefix);
        // 构造要入库的图片信息
        Picture picture = new Picture();
        picture.setUrl(pictureUploadResult.getUrl());
        picture.setName(pictureUploadResult.getPicName());
        picture.setPicSize(pictureUploadResult.getPicSize());
        picture.setPicWidth(pictureUploadResult.getPicWidth());
        picture.setPicHeight(pictureUploadResult.getPicHeight());
        picture.setPicScale(pictureUploadResult.getPicScale());
        picture.setPicFormat(pictureUploadResult.getPicFormat());
        picture.setUserId(loginUser.getId());
        // 如果 pictureId 不为空，表示更新，否则是新增
        if (pictureId != null) {
            picture.setId(pictureId);
            picture.setEditTime(new Date());
        }

        boolean result = this.saveOrUpdate(picture);
        ThrowUtils.throwIf(!result, ErrorCode.OPERATION_ERROR, "图片上传失败");
        return PictureVO.objToVo(picture);
    }

    /**
     * 获取查询条件
     * @param pictureQueryRequest - 图片查询请求
     * @return 查询条件
     */
    @Override
    public QueryWrapper<Picture> getQueryWrapper(PictureQueryRequest pictureQueryRequest) {
        QueryWrapper<Picture> queryWrapper = new QueryWrapper<>();
        if (pictureQueryRequest == null) {
            return queryWrapper;
        }
        // 从对象中取值
        Long id = pictureQueryRequest.getId();
        String name = pictureQueryRequest.getName();
        String introduction = pictureQueryRequest.getIntroduction();
        String category = pictureQueryRequest.getCategory();
        List<String> tags = pictureQueryRequest.getTags();
        Long picSize = pictureQueryRequest.getPicSize();
        Integer picWidth = pictureQueryRequest.getPicWidth();
        Integer picHeight = pictureQueryRequest.getPicHeight();
        Double picScale = pictureQueryRequest.getPicScale();
        String picFormat = pictureQueryRequest.getPicFormat();
        String searchText = pictureQueryRequest.getSearchText();
        Long userId = pictureQueryRequest.getUserId();
        String sortField = pictureQueryRequest.getSortField();
        String sortOrder = pictureQueryRequest.getSortOrder();
        // 从字段中搜索
        if (StrUtil.isNotBlank(searchText)) {
            // 需要拼接查询条件
            queryWrapper.and(qw -> qw.like("name", searchText)
                    .or()
                    .like("introduction", searchText));
        }
        queryWrapper.eq(ObjUtil.isNotEmpty(id), "id", id);
        queryWrapper.eq(ObjUtil.isNotEmpty(userId), "userId", userId);
        queryWrapper.like(StrUtil.isNotBlank(name), "name", name);
        queryWrapper.like(StrUtil.isNotBlank(introduction), "introduction", introduction);
        queryWrapper.like(StrUtil.isNotBlank(category), "category", category);
        queryWrapper.like(StrUtil.isNotBlank(picFormat), "picFormat", picFormat);
        queryWrapper.eq(ObjUtil.isNotEmpty(picWidth), "picWidth", picWidth);
        queryWrapper.eq(ObjUtil.isNotEmpty(picHeight), "picHeight", picHeight);
        queryWrapper.eq(ObjUtil.isNotEmpty(picSize), "picSize", picSize);
        queryWrapper.eq(ObjUtil.isNotEmpty(picScale), "picScale", picScale);
        // JSON 数组查询
        if (CollUtil.isNotEmpty(tags)) {
            for (String tag : tags) {
                queryWrapper.like("tags", "\"" + tag + "\"");
            }
        }
        // 排序
        queryWrapper.orderBy(StrUtil.isNotEmpty(sortField), sortOrder.equals("ascend"), sortField);
        return queryWrapper;
    }

    /**
     * 封装单个图片
     * @param picture - 图片
     * @return 封装的图片数据
     */
    @Override
    public PictureVO getPictureVO(Picture picture) {
        // 对象转封装类
        PictureVO pictureVO = PictureVO.objToVo(picture);
        // 关联查询用户信息
        Long userId = picture.getUserId();
        if (userId != null && userId > 0) {
            User user = userService.getById(userId);
            UserVO userVO = userService.getUserVO(user);
            pictureVO.setUser(userVO);
        }
        return pictureVO;
    }

    /**
     * 封装图片列表
     * @param picturePage - 图片列表
     * @return 封装的图片列表
     */
    @Override
    public Page<PictureVO> getPictureVOPage(Page<Picture> picturePage, HttpServletRequest request) {
        List<Picture> pictureList = picturePage.getRecords();
        Page<PictureVO> pictureVOPage = new Page<>(picturePage.getCurrent(), picturePage.getSize(), picturePage.getTotal());
        if (CollUtil.isEmpty(pictureList)) {return pictureVOPage;}
        // 对象列表 -> 封装对象列表
        List<PictureVO> pictureVOList = pictureList.stream()
                .map(PictureVO::objToVo)
                .collect(Collectors.toList());
        // 关联查询用户信息
        Set<Long> userIdSet = pictureVOList.stream().map(PictureVO::getUserId).collect(Collectors.toSet());
        Map<Long, List<User>> userIdUserListMap = userService.listByIds(userIdSet).stream().collect(Collectors.groupingBy(User::getId));
        // 填充信息
        pictureVOList.forEach(pictureVO -> {
            Long userId = pictureVO.getUserId();
            User user = null;
            if (userIdUserListMap.containsKey(userId)) {
                user = userIdUserListMap.get(userId).get(0);
            }
            pictureVO.setUser(userService.getUserVO(user));
        });
        pictureVOPage.setRecords(pictureVOList);
        return pictureVOPage;
    }

    @Override
    public void validPicture(Picture picture) {
        ThrowUtils.throwIf(picture == null, ErrorCode.PARAMS_ERROR);
        // 从对象中取值
        Long id = picture.getId();
        String url = picture.getUrl();
        String introduction = picture.getIntroduction();
        // 修改数据时，id 不能为空，有参数则校验
        ThrowUtils.throwIf(ObjUtil.isNull(id), ErrorCode.PARAMS_ERROR);
        if (StrUtil.isNotBlank(url)) {
            ThrowUtils.throwIf(url.length() > 1024, ErrorCode.PARAMS_ERROR, "url 不得超过 1024 个字符");
        }
        if (StrUtil.isNotBlank(introduction)) {
            ThrowUtils.throwIf(introduction.length() > 800, ErrorCode.PARAMS_ERROR, "简介不得超过 800 字");
        }
    }

}





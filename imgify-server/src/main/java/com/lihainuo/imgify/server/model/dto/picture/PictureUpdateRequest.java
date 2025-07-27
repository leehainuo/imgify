package com.lihainuo.imgify.server.model.dto.picture;

import lombok.Data;

import java.io.Serializable;
import java.util.List;

@Data
public class PictureUpdateRequest implements Serializable {

    /**
     * 图片 id
     */
    private Long id;

    /**
     * 图片名称
     */
    private String name;

    /**
     * 图片简介
     */
    private String introduction;

    /**
     * 分类
     */
    private String category;

    /**
     * 标签
     */
    private List<String> tags;


    private static final long serialVersionUID = 1L;
}

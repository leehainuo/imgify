package com.lihainuo.imgify.server.model.dto.picture;

import lombok.Data;

import java.io.Serializable;

@Data
public class PictureReviewRequest implements Serializable {

    /**
     * 图片 id
     */
    private Long id;

    /**
     * 审核状态：0-待审核，1-审核通过，2-审核未通过
     */
    private Integer reviewStatus;

    /**
     * 审核信息
     */
    private String reviewMessage;

    private static final long serialVersionUID = 1L;
}

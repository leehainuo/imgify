package com.lihainuo.imgify.server.model.dto.user;

import com.lihainuo.imgify.server.common.PageRequest;
import lombok.Data;

import java.io.Serializable;

/**
 * 用户查询请求，需要继承公共包的 PageRequest 来支持分页查询
 */
@Data
public class UserQueryRequest extends PageRequest implements Serializable {

    /**
     * 用户id
     */
    private Long id;

    /**
     * 用户昵称
     */
    private String userName;

    /**
     * 账号
     */
    private String userAccount;

    /**
     * 简介
     */
    private String userProfile;

    /**
     * 用户角色
     */
    private String userRole;

    private static final long serialVersionUID = 1L;
}

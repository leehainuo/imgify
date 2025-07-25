package com.lihainuo.imgify.server.enums;

import lombok.Getter;
import org.apache.commons.lang3.StringUtils;

import java.util.Arrays;
import java.util.Map;
import java.util.stream.Collectors;

/**
 * 用户角色枚举
 */
@Getter
public enum UserRoleEnum {
    USER("用户", "user"),
    ADMIN("管理员", "admin");

    private final String text;

    private final String value;

    UserRoleEnum(String text, String value) {
        this.text = text;
        this.value = value;
    }

    public static UserRoleEnum getEnumByValue(String value) {
        // 处理空情况
        if (StringUtils.isBlank(value)) {
            return null;
        }
        Map<String, UserRoleEnum> userRoleEnumMap = Arrays.stream(UserRoleEnum.values())
                .collect(Collectors.toMap(UserRoleEnum::getValue, userRoleEnum -> userRoleEnum));
        return userRoleEnumMap.get(value);
    }
}

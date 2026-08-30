package com.aliax.qlky.bean.baseenum;

import java.util.Arrays;
import java.util.Optional;

public enum DbFieldType {
    TEXT(1, "TEXT", "String", "text", null),
    LONGTEXT(2, "LONGTEXT", "String", "text", null),
    VARCHAR(3, "VARCHAR", "String", "text", 255),
    INT(4, "INT", "Integer", "number", 11),
    DATETIME(6, "DATETIME", "LocalDateTime", "datetime", null),
    IMAGE(7, "IMAGE", "String", "image", null);
    private final int id;
    private final String dbType;
    private final String javaType;

    private final String vueType;
    private final Integer length;

    DbFieldType(int id, String dbType, String javaType, String vueType, Integer length) {
        this.id = id;
        this.dbType = dbType;
        this.javaType = javaType;
        this.vueType = vueType;
        this.length = length;
    }

    public String getVueType() {
        return vueType;
    }

    public int getId() {
        return id;
    }

    public String getDbType() {
        return dbType;
    }

    public String getJavaType() {
        return javaType;
    }

    public Integer getLength() {
        return length;
    }

    // 根据ID获取对应的DbType
    public static Optional<String> getDbTypeById(int id) {
        return Arrays.stream(values())
                .filter(type -> type.id == id)
                .findFirst()
                .map(DbFieldType::getDbType);
    }

    // 根据ID获取整个枚举实例
    public static Optional<DbFieldType> getById(int id) {
        return Arrays.stream(values())
                .filter(type -> type.id == id)
                .findFirst();
    }
}
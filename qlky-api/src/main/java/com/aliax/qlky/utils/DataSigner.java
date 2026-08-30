package com.aliax.qlky.utils;

import org.apache.commons.codec.digest.DigestUtils;

import java.util.Arrays;
import java.util.Map;
import java.util.TreeMap;

public class DataSigner {
    /**
     * 生成数据签名（SHA-256）
     * @param data 数据键值对
     * @param excludeKeys 需要排除的字段（如group_id等系统字段）
     */
    public static String generateSignature(Map<String, Object> data, String... excludeKeys) {
        // 使用TreeMap自动排序键值
        Map<String, Object> sortedData = new TreeMap<>(data);
        
        // 拼接有效数据
        StringBuilder sb = new StringBuilder();
        sortedData.entrySet().stream()
            .filter(entry -> !Arrays.asList(excludeKeys).contains(entry.getKey()))
            .forEach(entry -> 
                sb.append(entry.getKey())
                  .append("=")
                  .append(entry.getValue() != null ? entry.getValue().toString() : "NULL")
                  .append("|")
            );
        
        // SHA-256哈希
        return DigestUtils.sha256Hex(sb.toString());
    }
}
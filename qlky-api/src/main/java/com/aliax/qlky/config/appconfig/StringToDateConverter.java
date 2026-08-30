package com.aliax.qlky.config.appconfig;

import org.springframework.core.convert.converter.Converter;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.*;

public class StringToDateConverter implements Converter<String, Date> {
    private static final List<String> SUPPORTED_FORMATS = Arrays.asList(
            "EEE MMM dd yyyy HH:mm:ss 'GMT'Z", // 你的格式
            "yyyy-MM-dd'T'HH:mm:ss.SSSXXX",      // ISO 8601
            "yyyy-MM-dd HH:mm:ss"                // 简单格式
    );

    private final List<SimpleDateFormat> formatters = new ArrayList<>();

    public StringToDateConverter() {
        for (String format : SUPPORTED_FORMATS) {
            SimpleDateFormat sdf = new SimpleDateFormat(format, Locale.US);
            sdf.setTimeZone(TimeZone.getTimeZone("GMT+8"));
            formatters.add(sdf);
        }
    }

    @Override
    public Date convert(String source) {
        String cleanedDate = source.replace(" (中国标准时间)", "").trim();
        for (SimpleDateFormat formatter : formatters) {
            try {
                return formatter.parse(cleanedDate);
            } catch (ParseException e) {
                // 尝试下一个格式
            }
        }
        throw new IllegalArgumentException("无法解析的日期格式: " + source);
    }
}
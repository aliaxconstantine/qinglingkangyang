package com.aliax.qlky.bean.basebean;

import com.aliax.qlky.bean.baseenum.ErrorCodeEnum;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.HashMap;
import java.util.Map;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class HttpResult {
    private Integer code; //响应码
    private String msg; //消息
    private Object data; //数据
    //常见地返回格式
    public static HttpResult success(Object data) {
        return HttpResult.builder()
                .code(ErrorCodeEnum.SUCCESS.code)
                .data(data)
                .msg(ErrorCodeEnum.SUCCESS.data)
                .build();
    }
    public static HttpResult success(Object data, Long total) {
        Map<String,Object> retMap = new HashMap<>();
        retMap.put("total",total);
        retMap.put("list",data);
        return HttpResult.builder()
                .code(ErrorCodeEnum.SUCCESS.code)
                .data(retMap)
                .msg(ErrorCodeEnum.SUCCESS.data)
                .build();
    }
    public static HttpResult success(String data) {
        return HttpResult.builder()
                .code(ErrorCodeEnum.SUCCESS.code)
                .msg(data)
                .build();
    }
    public static HttpResult fail(String message) {
        return HttpResult.builder()
                .code(ErrorCodeEnum.FAIL.code)
                .msg(message)
                .build();
    }

}

package com.aliax.qlky.bean.basebean;

import lombok.Builder;
import lombok.Data;

@Builder
@Data
public class SocketMessageBean {
    private Long sendId;
    private Long publishId;
    private String message;
}

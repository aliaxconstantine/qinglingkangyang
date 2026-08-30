package com.aliax.qlky.bean.basebean;

import lombok.Builder;
import lombok.Data;

@Builder
@Data
public class SystemMessageBean {
    private String userId;
    private String message;
}

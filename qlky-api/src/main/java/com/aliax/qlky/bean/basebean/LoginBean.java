package com.aliax.qlky.bean.basebean;

import lombok.Builder;
import lombok.Data;

@Data
@Builder
public class LoginBean {
    private String phone;
    private String code;
    private String password;
}

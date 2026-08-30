package com.aliax.qlky.bean.basebean;

import com.baomidou.mybatisplus.annotation.TableField;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;

@EqualsAndHashCode(callSuper = true)
@Data
@NoArgsConstructor
public class ParamBaseBean extends PageBean<PageBean<PageBean>> {
    @TableField(exist = false)
    private String appId;
    @TableField(exist = false)
    private Long userId;
    @TableField(exist = false)
    private String retCode;
    @TableField(exist = false)
    private String retDesc;

}

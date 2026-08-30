package com.aliax.qlky.bean.basebean;

import com.baomidou.mybatisplus.annotation.TableField;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
public class PageBean<T> {
    @TableField(exist = false)
    private Integer currentPage = 1;
    @TableField(exist = false)
    private Integer pageSize = 20;
    @TableField(exist = false)
    private Integer total;

    @TableField(exist = false)
    private Integer offset;

    @TableField(exist = false)
    private Integer len;
    @TableField(exist = false)
    private Integer index;

}

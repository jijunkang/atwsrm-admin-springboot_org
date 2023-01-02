package org.springblade.modules.pr.entity;

import com.baomidou.mybatisplus.annotation.TableName;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import java.io.Serializable;

@Data
@TableName("atw_item_info_part_bl_rx")
@ApiModel(value = "小零件 板料 - 基础表", description = "")
public class ItemInfoEntityBasOfXLJRX implements Serializable {
    private static final long serialVersionUID = 1L;

    @ApiModelProperty(value = "id")
    private Long id;

    @ApiModelProperty(value = "料号")
    private String itemCode;

    @ApiModelProperty(value = "R")
    private String r;

    @ApiModelProperty(value = "X")
    private String x;
}

package org.springblade.modules.pr.entity;

import com.baomidou.mybatisplus.annotation.TableName;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import java.io.Serializable;

@Data
@TableName("atw_item_info_dj_basic_zfl")
@ApiModel(value = "锻件 - 中法兰基础表", description = "")
public class ItemInfoEntityBasZFLOfDJ implements Serializable {
    private static final long serialVersionUID = 1L;

    @ApiModelProperty(value = "id")
    private Long id;

    @ApiModelProperty(value = "料号")
    private String itemCode;

    @ApiModelProperty(value = "物料描述")
    private String itemName;

    @ApiModelProperty(value = "外径")
    private String outerSize;

    @ApiModelProperty(value = "内径")
    private String innerSize;

    @ApiModelProperty(value = "高度")
    private String heightSize;
}

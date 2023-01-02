package org.springblade.modules.pr.entity;

import com.baomidou.mybatisplus.annotation.TableName;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import java.io.Serializable;

@Data
@TableName("atw_item_info_dj_basic_fm")
@ApiModel(value = "锻件 - 阀帽基础表", description = "")
public class ItemInfoEntityBasFMOfDJ implements Serializable {
    private static final long serialVersionUID = 1L;

    @ApiModelProperty(value = "id")
    private Long id;

    @ApiModelProperty(value = "料号")
    private String itemCode;

    @ApiModelProperty(value = "物料描述")
    private String itemName;

    @ApiModelProperty(value = "大头外径")
    private String bigOuterSize;

    @ApiModelProperty(value = "内径")
    private String innerSize;

    @ApiModelProperty(value = "大头高度")
    private String bigHeightSize;

    @ApiModelProperty(value = "小头外径")
    private String smallOuterSize;

    @ApiModelProperty(value = "总高度")
    private String totalHeightSize;
}

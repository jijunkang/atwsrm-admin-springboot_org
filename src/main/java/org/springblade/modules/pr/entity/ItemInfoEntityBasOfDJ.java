package org.springblade.modules.pr.entity;

import com.baomidou.mybatisplus.annotation.TableName;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import java.io.Serializable;

@Data
@TableName("atw_item_info_dj_basic")
@ApiModel(value = "锻件 - 基础表", description = "")
public class ItemInfoEntityBasOfDJ implements Serializable {
    private static final long serialVersionUID = 1L;

    @ApiModelProperty(value = "id")
    private Long id;

    @ApiModelProperty(value = "料号")
    private String itemCode;

    @ApiModelProperty(value = "物料描述")
    private String itemName;

    @ApiModelProperty(value = "外径-B")
    private String outerSize;

    @ApiModelProperty(value = "内径-AB")
    private String innerSize;

    @ApiModelProperty(value = "高度-B")
    private String heightSize;

    @ApiModelProperty(value = "大头外径-A")
    private String bigOuterSize;

    @ApiModelProperty(value = "大头高度-A")
    private String bigHeightSize;

    @ApiModelProperty(value = "小头外径-A")
    private String smallOuterSize;

    @ApiModelProperty(value = "总高度-A")
    private String totalHeightSize;

    @ApiModelProperty(value = "公式类型 - A：大小头 B：常规")
    private String type;

    @ApiModelProperty(value = "公式类型 - fm：阀帽 zfl：中法兰 zyd：自由锻")
    private String res;
}

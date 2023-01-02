package org.springblade.modules.pr.entity;

import com.baomidou.mybatisplus.annotation.TableName;
import com.fasterxml.jackson.annotation.JsonProperty;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import java.io.Serializable;

@Data
@TableName("atw_item_info_dz_not_split")
@ApiModel(value = "底轴-手动输入", description = "")
public class ItemInfoEntityBasOfDZ implements Serializable {
    private static final long serialVersionUID = 1L;

    @ApiModelProperty(value = "id")
    private Long id;

    @ApiModelProperty(value = "料号")
    private String itemCode;

    @ApiModelProperty(value = "物料名称")
    private String itemName;

    @ApiModelProperty(value = "外径")
    private String outerSize;

    @ApiModelProperty(value = "内径")
    private String innerSize;

    @ApiModelProperty(value = "喷涂外径")
    @JsonProperty("pOuterSize")
    private String pOuterSize;

    @ApiModelProperty(value = "喷涂内径")
    @JsonProperty("pInnerSize")
    private String pInnerSize;
}

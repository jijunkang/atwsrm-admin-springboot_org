package org.springblade.modules.pr.entity;

import com.baomidou.mybatisplus.annotation.TableName;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import java.io.Serializable;

@Data
@TableName("atw_item_info_part_not_split")
@ApiModel(value = "小零件 - 基础表", description = "")
public class ItemInfoEntityBasOfXLJ implements Serializable {
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

    @ApiModelProperty(value = "线切割长度")
    private String xqgSize;

    @ApiModelProperty(value = "槽边宽")
    private String cbkSize;

    @ApiModelProperty(value = "槽边中心距")
    private String cbzxjSize;

    @ApiModelProperty(value = "线切割宽")
    private String xqgkSize;
}

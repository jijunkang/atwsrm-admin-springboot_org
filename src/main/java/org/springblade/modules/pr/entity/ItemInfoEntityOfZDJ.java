package org.springblade.modules.pr.entity;

import com.baomidou.mybatisplus.annotation.TableName;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;
import lombok.EqualsAndHashCode;
import org.springblade.core.mp.base.BaseEntity;

@Data
@TableName("atw_item_info_zdj")
@EqualsAndHashCode(callSuper = true)
@ApiModel(value = "铸件物料信息对象", description = "")
public class ItemInfoEntityOfZDJ extends BaseEntity {
    private static final long serialVersionUID = 1L;

    @ApiModelProperty(value = "物料分类")
    private String itemize;

    @ApiModelProperty(value = "尺寸")
    private String size;

    @ApiModelProperty(value = "形式")
    private String form;

    @ApiModelProperty(value = "磅级")
    private String pound;

    @ApiModelProperty(value = "法兰")
    private String flange;

    @ApiModelProperty(value = "系列")
    private String series;

    @ApiModelProperty(value = "材质-价格")
    private String material;

    @ApiModelProperty(value = "供应商名称")
    private String supName;

    @ApiModelProperty(value = "供应商编码")
    private String supCode;

    @ApiModelProperty(value = "单重")
    private String weight;

    @ApiModelProperty(value = "铸造工艺")
    private String technology;

    @ApiModelProperty(value = "优先级")
    private String priority;

    @ApiModelProperty(value = "加工费")
    private String charge;

    @ApiModelProperty(value = "材质-单重")
    private String materialOfWeight;
}

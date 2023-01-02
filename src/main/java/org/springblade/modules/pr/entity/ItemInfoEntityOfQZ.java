package org.springblade.modules.pr.entity;

import com.baomidou.mybatisplus.annotation.TableName;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;
import lombok.EqualsAndHashCode;
import org.springblade.core.mp.base.BaseEntity;

@Data
@TableName("atw_item_info_qz")
@EqualsAndHashCode(callSuper = true)
@ApiModel(value = "球座物料信息对象", description = "")
public class ItemInfoEntityOfQZ extends BaseEntity {
    private static final long serialVersionUID = 1L;

    @ApiModelProperty(value = "物料分类")
    private String itemize;

    @ApiModelProperty(value = "寸级")
    private String size;

    @ApiModelProperty(value = "形式")
    private String form;

    @ApiModelProperty(value = "磅级")
    private String pound;

    @ApiModelProperty(value = "特殊规则")
    private String specialRule;

    @ApiModelProperty(value = "等级")
    private String grade;

    @ApiModelProperty(value = "球直径")
    private String ballDiam;

    @ApiModelProperty(value = "球高")
    private String ballHeight;

    @ApiModelProperty(value = "球通道直径")
    private String ballPassDiam;

    @ApiModelProperty(value = "材质")
    private String material;

    @ApiModelProperty(value = "涂层")
    private String coat;

    private String fzCoat;

    @ApiModelProperty(value = "供应商编码")
    private String supCode;

    @ApiModelProperty(value = "供应商名称")
    private String supName;

    @ApiModelProperty(value = "优先级")
    private String priority;

    @ApiModelProperty(value = "球座单重")
    private String qzWeight;

    @ApiModelProperty(value = "球座喷涂面积")
    private String qzSprayArea;

    @ApiModelProperty(value = "球座加工费")
    private String qzCharge;

    @ApiModelProperty(value = "阀座单重")
    private String fzWeight;

    @ApiModelProperty(value = "阀座喷涂费")
    private String fzSprayCharge;

    @ApiModelProperty(value = "阀座加工费")
    private String fzCharge;
}

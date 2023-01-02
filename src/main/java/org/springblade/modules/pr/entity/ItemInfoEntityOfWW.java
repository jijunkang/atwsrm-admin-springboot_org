package org.springblade.modules.pr.entity;

import com.baomidou.mybatisplus.annotation.TableName;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;
import lombok.EqualsAndHashCode;
import org.springblade.core.mp.base.BaseEntity;

import java.math.BigDecimal;

@Data
@TableName("atw_item_info_ww")
@EqualsAndHashCode(callSuper = true)
@ApiModel(value = "全称委外信息对象", description = "")
public class ItemInfoEntityOfWW extends BaseEntity {
    private static final long serialVersionUID = 1L;

    @ApiModelProperty(value = "物料分类")
    private String itemize;

    @ApiModelProperty(value = "尺寸")
    private String size;

    @ApiModelProperty(value = "磅级")
    private String pound;

    @ApiModelProperty(value = "固定形式")
    private String form;

    @ApiModelProperty(value = "系列")
    private String series;

    @ApiModelProperty(value = "材质")
    private String material;

    @ApiModelProperty(value = "供应商名称")
    private String supName;

    @ApiModelProperty(value = "供应商编码")
    private String supCode;

    @ApiModelProperty(value = "优先级")
    private String priority;

    @ApiModelProperty(value = "单价")
    private BigDecimal price;
}

package org.springblade.modules.pr.entity;

import cn.afterturn.easypoi.excel.annotation.Excel;
import com.baomidou.mybatisplus.annotation.TableName;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;
import lombok.EqualsAndHashCode;
import org.springblade.core.mp.base.BaseEntity;

@Data
@TableName("atw_qz_report")
@EqualsAndHashCode(callSuper = true)
@ApiModel(value = "球座物料信息对象", description = "")
public class ItemInfoEntityOfQZNew extends BaseEntity {
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
    @Excel(name = "球直径", orderNum = "6")
    private String ballDiam;

    @ApiModelProperty(value = "球高")
    @Excel(name = "球高", orderNum = "7")
    private String ballHeight;

    @ApiModelProperty(value = "球通道直径")
    private String ballPassDiam;

    @ApiModelProperty(value = "材质")
    private String material;

    @ApiModelProperty(value = "球涂层")
    private String coat;

    @ApiModelProperty(value = "座涂层")
    private String fzCoat;

    @ApiModelProperty(value = "供应商编码")
    @Excel(name = "供应商编码", orderNum = "4")
    private String supCode;

    @ApiModelProperty(value = "供应商名称")
    @Excel(name = "供应商名称", orderNum = "3")
    private String supName;

    @ApiModelProperty(value = "优先级")
    private String priority;

    @ApiModelProperty(value = "球体单重")
    @Excel(name = "球体单重", orderNum = "8")
    private String qzWeight;

    @ApiModelProperty(value = "球体喷涂面积")
    @Excel(name = "球体喷涂面积", orderNum = "11")
    private String qzSprayArea;

    @Excel(name = "球体喷涂单价", orderNum = "11")
    private String qzSprayPrice;

    @Excel(name = "球体喷涂费", orderNum = "12")
    private String qzSprayCost;

    @ApiModelProperty(value = "球体加工费")
    @Excel(name = "球体加工费", orderNum = "13")
    private String qzCharge;

    @Excel(name = "球体单价", orderNum = "14")
    private String qzCost;

    @ApiModelProperty(value = "阀座单重")
    @Excel(name = "阀座单重", orderNum = "15")
    private String fzWeight;

    @ApiModelProperty(value = "阀座单价")
    @Excel(name = "阀座单价", orderNum = "15")
    private String fzPrice;

    @Excel(name = "阀座材料费", orderNum = "16")
    private String fzMaterialCost;

    @Excel(name = "阀座喷涂费", orderNum = "17")
    @ApiModelProperty(value = "阀座喷涂费")
    private String fzSprayCharge;

    @Excel(name = "阀座加工费", orderNum = "18")
    @ApiModelProperty(value = "阀座加工费")
    private String fzCharge;

    @Excel(name = "配磨费", orderNum = "19")
    @ApiModelProperty(value = "配磨费")
    private String deliverCost;

    @Excel(name = "系数", orderNum = "20")
    @ApiModelProperty(value = "系数")
    private String k;

    @Excel(name = "请购单号", orderNum = "1")
    private String prCode;
    @Excel(name = "请购行", orderNum = "2")
    private String prLn;
    @Excel(name = "数量", orderNum = "5")
    private String quantity;
    @Excel(name = "总价", orderNum = "21")
    private String totalCost;

    @Excel(name = "球体材料单价", orderNum = "9")
    private String qtMaterialPrice;

    @Excel(name = "球体材料费", orderNum = "10")
    private String qzMaterialCost;

    @Excel(name ="料号",orderNum = "4")
    @ApiModelProperty(value = "料号")
    private String itemCode;

    @Excel(name ="物料描述",orderNum = "5")
    @ApiModelProperty(value = "物料描述")
    private String itemName;
}

package org.springblade.modules.report.entity;


import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;
import lombok.EqualsAndHashCode;
import org.springblade.core.mp.base.BaseEntity;

import java.util.Date;


/**
 * 供应商产能分析 实体类供应商产能分析 实体类
 */
@Data
@EqualsAndHashCode(callSuper = true)
@ApiModel(value = "Oracle供应商产能数据", description = "Oracle供应商产能数据")
public class SupplierOutputFromOracle extends BaseEntity {

    private static final long serialVersionUID = 1L;

    @ApiModelProperty(value = "需求数量")
    private String num;

    @ApiModelProperty(value = "供应商编号")
    private String supCode;

    @ApiModelProperty(value = "物料编码")
    private String itemCode;

    @ApiModelProperty(value = "物料名称")
    private String itemName;

    @ApiModelProperty(value = "项目号")
    private String proNo;

    @ApiModelProperty(value = "单位产能")
    private String outputCent;

    @ApiModelProperty(value = "单位工时")
    private String weightCent;

    @ApiModelProperty(value = "工艺")
    private String gy;

    @ApiModelProperty(value = "wwpo_date")
    private Date wwpoDate;

    @ApiModelProperty(value = "plan_date")
    private Date planDate;

    @ApiModelProperty(value = "总工时")
    private String zgs;

    @ApiModelProperty(value = "总产能")
    private String zcz;

    @ApiModelProperty(value = "需求数量")
    private String reqNum;


    @ApiModelProperty(value = "未交货数量")
    private String notRcvNum;



}

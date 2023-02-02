package org.springblade.modules.report.vo;


import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;
import lombok.EqualsAndHashCode;
import org.springblade.core.mp.base.BaseEntity;


/**
 * 供应商产能分析 实体类供应商产能分析  (球座)
 */
@Data
@EqualsAndHashCode(callSuper = true)
@ApiModel(value = "供应商产能分析（球座）", description = "供应商产能分析（球座）")
public class SupplierOutputQZVo extends BaseEntity {

    private static final long serialVersionUID = 1L;

    @ApiModelProperty(value = "工艺")
    private String gy;

    @ApiModelProperty(value = "总工时(H/月)")
    private String zgs;

    @ApiModelProperty(value = "总金额(W/月)")
    private String zje;

    @ApiModelProperty(value = "占用工时(H/月)")
    private String zygs;

    @ApiModelProperty(value = "溢出值(H/月)")
    private String ycgs;

    @ApiModelProperty(value = "占用金额(W/月)")
    private String zyje;

    @ApiModelProperty(value = "溢出值(W/月)")
    private String ycje;

    @ApiModelProperty(value = "供应商简称")
    private String supName;

    @ApiModelProperty(value = "供应商溢出比例")
    private String gysycbl;

}

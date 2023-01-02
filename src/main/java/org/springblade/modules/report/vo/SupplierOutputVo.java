package org.springblade.modules.report.vo;


import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;
import lombok.EqualsAndHashCode;
import org.springblade.core.mp.base.BaseEntity;


/**
 * 供应商产能分析 实体类供应商产能分析 实体类
 */
@Data
@EqualsAndHashCode(callSuper = true)
@ApiModel(value = "供应商产能分析", description = "供应商产能分析")
public class SupplierOutputVo extends BaseEntity {

    private static final long serialVersionUID = 1L;

    @ApiModelProperty(value = "类别")
    private String lb;

    @ApiModelProperty(value = "工艺")
    private String gy;

    @ApiModelProperty(value = "总工时(H/月)")
    private String zgs;

    @ApiModelProperty(value = "总产值(T/月)")
    private String zcz;

    @ApiModelProperty(value = "占用工时(H/月)")
    private String zygs;

    @ApiModelProperty(value = "溢出值(H/月)")
    private String ycgs;

    @ApiModelProperty(value = "占用产能(T/月)")
    private String zycn;

    @ApiModelProperty(value = "溢出值(T)")
    private String yccn;

    @ApiModelProperty(value = "溢出比例")
    private String ycbl;

    @ApiModelProperty(value = "供应商简称")
    private String supName;

    @ApiModelProperty(value = "供应商溢出比例")
    private String gysycbl;

    @ApiModelProperty(value = "对策")
    private String dc;

}

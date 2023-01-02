package org.springblade.modules.supitem.dto;

import cn.afterturn.easypoi.excel.annotation.Excel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import java.io.Serializable;
import java.math.BigDecimal;
import java.util.Date;

/**
 **/
@Data
public class SupItemExcelDTO implements Serializable {
    private static final long serialVersionUID = 1L;

    @ApiModelProperty(value = "id")
    private Long id;

    @Excel(name = "物料编码")
    private String itemCode;

    @Excel(name = "物料名称")
    @ApiModelProperty(value = "物料名称")
    private String itemName;

    @Excel(name = "供应商编号")
    @ApiModelProperty(value = "供应商编号")
    private String supCode;

    @Excel(name = "供应商名称")
    @ApiModelProperty(value = "供应商名称")
    private String supName;

    @Excel(name = "标准交期")
    @ApiModelProperty(value = "标准交期")
    private Integer normalPeriod;

}

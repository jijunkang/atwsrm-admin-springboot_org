package org.springblade.modules.priceframe.dto;

import cn.afterturn.easypoi.excel.annotation.Excel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;
import org.springblade.core.tool.utils.DateUtil;

import java.io.Serializable;
import java.math.BigDecimal;
import java.util.Date;

/**
 **/
@Data
public class PriceFrameExcelDTO implements Serializable {
    private static final long serialVersionUID = 1L;

    @ApiModelProperty(value = "id")
    private Long id;

    @Excel(name = "物料编号")
    private String itemCode;

    @Excel(name = "物料描述")
    @ApiModelProperty(value = "物料描述")
    private String itemName;

    @Excel(name = "供应商编码")
    @ApiModelProperty(value = "供应商编码")
    private String supCode;

    @Excel(name = "供应商名称")
    @ApiModelProperty(value = "供应商名称")
    private String supName;

    @Excel(name = "最小采购量")
    @ApiModelProperty(value = "最小采购量")
    private BigDecimal limitMin;

    @Excel(name = "最大采购量")
    @ApiModelProperty(value = "最大采购量")
    private BigDecimal limitMax;

    @Excel(name = "单价")
    @ApiModelProperty(value = "单价")
    private BigDecimal price;

    @Excel(name = "计价单位")
    @ApiModelProperty(value = "计价单位")
    private String     uom;

    @Excel(name = "生效日期" ,format = DateUtil.PATTERN_DATETIME)
    @ApiModelProperty(value = "生效日期")
    private Date effectiveDate;

    @Excel(name = "失效日期",format = DateUtil.PATTERN_DATETIME)
    @ApiModelProperty(value = "失效日期")
    private Date expirationDate;
}

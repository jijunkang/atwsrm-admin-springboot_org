package org.springblade.modules.priceframe.dto;

import com.fasterxml.jackson.databind.annotation.JsonSerialize;
import com.fasterxml.jackson.databind.ser.std.ToStringSerializer;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import java.io.Serializable;
import java.math.BigDecimal;

/**
 * @author libin
 * @date 16:34 2020/6/28
 **/
@Data
public class CenterPriceFrame implements Serializable {

    private static final long serialVersionUID = 1L;

    @JsonSerialize(using = ToStringSerializer.class)
    private Long id;
    @JsonSerialize(using = ToStringSerializer.class)
    private Long prId;
    @JsonSerialize(using = ToStringSerializer.class)
    private Long pfId;
    private String prCode;
    private String itemCode;
    private String itemName;
    private BigDecimal priceNum;
    private String priceUom;
    private Long reqDate;
    private String supCode;
    private String supName;
    private BigDecimal price;
    private Long promiseDate;
    private Integer status;
    private String remark;
    private String flowType;
    private BigDecimal referencePrice;

    @ApiModelProperty(value = "可用量")
    private BigDecimal availableQuantity;

    @ApiModelProperty(value = "项目占用量")
    private BigDecimal projectOccupancyNum;

    @ApiModelProperty(value = "请购单备注")
    private String requisitionRemark;

    @ApiModelProperty(value = "最小起订量")
    private BigDecimal purchMix;

    @ApiModelProperty(value = "安全库存量")
    private BigDecimal stockLowerLimit;

}

package org.springblade.modules.finance.vo;

import io.swagger.annotations.ApiModelProperty;
import lombok.Data;
import lombok.EqualsAndHashCode;
import org.springblade.modules.finance.entity.PrepayOrderItemEntity;

import java.math.BigDecimal;

/**
 *  模型VO
 *
 * @author Will
 */
@Data
@EqualsAndHashCode(callSuper = true)
public class PrepayOrderItemVO extends PrepayOrderItemEntity {

    private static final long serialVersionUID = 1L;

    @ApiModelProperty(value = "支付方式")
    private String payWay;

    @ApiModelProperty(value = "订单总金额")
    private BigDecimal docAmount;

    @ApiModelProperty(value = "累计预付金额")
    private BigDecimal accumPrepay;

    @ApiModelProperty(value = "实际累计预付金额")
    private BigDecimal prepaidTotal;

    @ApiModelProperty(value = "实际累计应付付金额")
    private BigDecimal accpaidTotal;

    @ApiModelProperty(value = "下单日期")
    private Integer    docDate;




}

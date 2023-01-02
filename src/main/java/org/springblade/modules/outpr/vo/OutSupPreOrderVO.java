package org.springblade.modules.outpr.vo;

import io.swagger.annotations.ApiModelProperty;
import lombok.Data;
import lombok.EqualsAndHashCode;
import org.springblade.modules.outpr.entity.OutSupItemProcessPriceEntity;
import org.springblade.modules.outpr.entity.OutSupPreOrderEntity;

import java.math.BigDecimal;
import java.util.List;

/**
 * 模型VO
 * @author Will
 */
@Data
@EqualsAndHashCode(callSuper = true)
public
class OutSupPreOrderVO extends OutSupPreOrderEntity{

    private static final long serialVersionUID = 1L;

    List<OutSupItemProcessPriceEntity> processPriceList;

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

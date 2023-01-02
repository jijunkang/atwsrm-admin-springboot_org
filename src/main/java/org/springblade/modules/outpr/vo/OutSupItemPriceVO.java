package org.springblade.modules.outpr.vo;

import io.swagger.annotations.ApiModelProperty;
import lombok.Data;
import lombok.EqualsAndHashCode;
import org.springblade.modules.outpr.entity.OutSupItemPriceEntity;
import org.springblade.modules.outpr.entity.OutSupItemProcessPriceEntity;

import java.math.BigDecimal;
import java.util.List;

/**
 * @author Will
 */
@Data
@EqualsAndHashCode(callSuper = true)
public
class OutSupItemPriceVO extends OutSupItemPriceEntity{

    private static final long serialVersionUID = 1L;

    @ApiModelProperty(value = "安特威内部估价")
    private BigDecimal atwPrice;

    @ApiModelProperty(value = "材料费")
    private BigDecimal materialCost;

    @ApiModelProperty(value = "供应商承诺交期")
    private Long supDeliveryTime;

    @ApiModelProperty(value = "供应商状态")
    private String supStatus;

    @ApiModelProperty(value = "供应商备注")
    private String supRemark;

    @ApiModelProperty(value = "数学模型参考价")
    private BigDecimal referencePrice;

    List<OutSupItemProcessPriceVO> processPriceList;

}

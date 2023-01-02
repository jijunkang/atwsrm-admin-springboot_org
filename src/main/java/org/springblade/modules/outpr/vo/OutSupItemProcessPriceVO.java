package org.springblade.modules.outpr.vo;

import io.swagger.annotations.ApiModelProperty;
import lombok.Data;
import lombok.EqualsAndHashCode;
import org.springblade.modules.outpr.entity.OutSupItemProcessPriceEntity;

import java.math.BigDecimal;

/**
 * 模型VO
 * @author Will
 */
@Data
@EqualsAndHashCode(callSuper = true)
public
class OutSupItemProcessPriceVO extends OutSupItemProcessPriceEntity{

    private static final long serialVersionUID = 1L;

    @ApiModelProperty(value = "安特威价格")
    private BigDecimal atwPrice;

}

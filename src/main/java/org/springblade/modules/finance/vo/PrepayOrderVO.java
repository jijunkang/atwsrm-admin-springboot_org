package org.springblade.modules.finance.vo;

import io.swagger.annotations.ApiModelProperty;
import lombok.Data;
import lombok.EqualsAndHashCode;
import org.springblade.modules.finance.entity.PrepayOrderEntity;

import java.math.BigDecimal;

/**
 *  模型VO
 *
 * @author Will
 */
@Data
@EqualsAndHashCode(callSuper = true)
public class PrepayOrderVO extends PrepayOrderEntity {

	private static final long serialVersionUID = 1L;

	@ApiModelProperty(name = "预付比例")
	private Double prepayRate;
}

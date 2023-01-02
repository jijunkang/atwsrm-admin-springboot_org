package org.springblade.modules.po.vo;

import io.swagger.annotations.ApiModelProperty;
import lombok.Data;
import lombok.EqualsAndHashCode;
import org.springblade.modules.po.entity.IoEntity;

import java.math.BigDecimal;

/**
 *  模型VO
 *
 * @author Will
 */
@Data
@EqualsAndHashCode(callSuper = true)
public class IoVO extends IoEntity {

	private static final long serialVersionUID = 1L;

	@ApiModelProperty(value = "标准价格")
	private BigDecimal standardPrice;

    @ApiModelProperty(value = "数学模型参考价")
    private BigDecimal referencePrice;
}

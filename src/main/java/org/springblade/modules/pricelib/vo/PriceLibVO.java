package org.springblade.modules.pricelib.vo;

import io.swagger.annotations.ApiModel;
import lombok.Data;
import lombok.EqualsAndHashCode;
import org.springblade.modules.pricelib.entity.PriceLibEntity;

/**
 *  模型VO
 *
 * @author Will
 */
@Data
@EqualsAndHashCode(callSuper = true)
@ApiModel(value = "PriceLibVO对象", description = "价格库")
public class PriceLibVO extends PriceLibEntity {

	private static final long serialVersionUID = 1L;

}

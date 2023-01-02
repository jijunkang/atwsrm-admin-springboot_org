package org.springblade.modules.priceframe.dto;

import lombok.Data;
import lombok.EqualsAndHashCode;
import org.springblade.modules.priceframe.entity.PriceFrameEntity;

/**
 *  模型DTO
 *
 * @author Will
 */
@Data
@EqualsAndHashCode(callSuper = true)
public class PriceFrameDTO extends PriceFrameEntity {

	private static final long serialVersionUID = 1L;

}

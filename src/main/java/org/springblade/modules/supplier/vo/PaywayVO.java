package org.springblade.modules.supplier.vo;

import lombok.Data;
import lombok.EqualsAndHashCode;
import org.springblade.modules.supplier.entity.PaywayEntity;

/**
 *  模型VO
 *
 * @author Will
 */
@Data
@EqualsAndHashCode(callSuper = true)
public class PaywayVO extends PaywayEntity {

	private static final long serialVersionUID = 1L;

	private String templateType;

}

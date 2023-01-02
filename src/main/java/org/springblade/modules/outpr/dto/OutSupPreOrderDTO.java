package org.springblade.modules.outpr.dto;

import lombok.Data;
import lombok.EqualsAndHashCode;
import org.springblade.modules.outpr.entity.OutSupPreOrderEntity;

import java.util.Date;

/**
 *  模型DTO
 *
 * @author Will
 */
@Data
@EqualsAndHashCode(callSuper = true)
public class OutSupPreOrderDTO extends OutSupPreOrderEntity {

	private static final long serialVersionUID = 1L;

	Long reqDateStart;
	Long reqDateEnd;
	Long prDateStart;
	Long prDateEnd;
}

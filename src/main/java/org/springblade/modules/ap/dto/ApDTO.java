package org.springblade.modules.ap.dto;

import lombok.Data;
import lombok.EqualsAndHashCode;
import org.springblade.modules.ap.entity.ApEntity;

/**
 *  模型DTO
 *
 * @author Will
 */
@Data
@EqualsAndHashCode(callSuper = true)
public class ApDTO extends ApEntity {

	private static final long serialVersionUID = 1L;

}

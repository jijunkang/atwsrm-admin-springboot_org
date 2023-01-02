package org.springblade.modules.system.dto;

import lombok.Data;
import lombok.EqualsAndHashCode;
import org.springblade.modules.system.entity.AuditRecordEntity;

/**
 *  模型DTO
 *
 * @author Will
 */
@Data
@EqualsAndHashCode(callSuper = true)
public class AuditRecordDTO extends AuditRecordEntity {

	private static final long serialVersionUID = 1L;

}

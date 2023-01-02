package org.springblade.modules.system.vo;

import lombok.Data;
import lombok.EqualsAndHashCode;
import org.springblade.modules.system.entity.AuditRecordEntity;

/**
 *  模型VO
 *
 * @author Will
 */
@Data
@EqualsAndHashCode(callSuper = true)
public class AuditRecordVO extends AuditRecordEntity {

	private static final long serialVersionUID = 1L;

}

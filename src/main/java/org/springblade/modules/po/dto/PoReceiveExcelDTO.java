package org.springblade.modules.po.dto;

import lombok.Data;
import lombok.EqualsAndHashCode;
import org.springblade.modules.po.entity.PoReceiveEntity;

/**
 *  模型DTO
 *
 * @author Will
 */
@Data
@EqualsAndHashCode(callSuper = true)
public class PoReceiveExcelDTO extends PoReceiveEntity {

	private static final long serialVersionUID = 1L;

	private String poCode;
	private Integer poLn;
    private String itemCode;
    private String itemName;
}

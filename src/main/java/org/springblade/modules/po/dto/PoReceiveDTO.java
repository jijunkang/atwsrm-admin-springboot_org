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
public class PoReceiveDTO extends PoReceiveEntity {

	private static final long serialVersionUID = 1L;

    private String itemCode;

	private String itemName;

    private String outSupCode;

    private String outSupName;

    private String process;

    private String rcvCodes;

    private String statuss;

    private String templateType;

    private String orderCode;

    private String poCode;

    private String poLn;

    private String orgCode;//组织代码

    private String isNew;//组织代码

}

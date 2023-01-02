package org.springblade.modules.po.dto;

import lombok.Data;
import lombok.EqualsAndHashCode;
import org.springblade.modules.po.entity.PoEntity;

/**
 * 采购订单表头 模型DTO
 * @author Will
 */
@Data
@EqualsAndHashCode(callSuper = true)
public
class PoDTO extends PoEntity{

    private static final long serialVersionUID = 1L;

    private String type;

    private String itemCode;

    private String purchCode;

    private String createTimeStart;
    private String createTimeEnd;
    private Long docDateStart;
    private Long docDateEnd;
}

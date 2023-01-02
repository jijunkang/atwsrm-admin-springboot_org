package org.springblade.modules.finance.dto;

import lombok.Data;
import lombok.EqualsAndHashCode;
import org.springblade.modules.finance.entity.PrepayOrderEntity;

import java.util.List;

/**
 *  模型DTO
 *
 * @author Will
 */
@Data
@EqualsAndHashCode(callSuper = true)
public class PrepayOrderUpdateDto extends PrepayOrderEntity {

    private static final long serialVersionUID = 1L;

    List<PrepayOrderItemUpdateDto> ppoItems;
}

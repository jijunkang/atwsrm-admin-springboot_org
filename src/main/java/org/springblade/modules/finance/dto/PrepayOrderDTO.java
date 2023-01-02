package org.springblade.modules.finance.dto;

import lombok.Data;
import lombok.EqualsAndHashCode;
import org.springblade.modules.finance.entity.PrepayOrderEntity;

/**
 *  模型DTO
 *
 * @author Will
 */
@Data
@EqualsAndHashCode(callSuper = true)
public class PrepayOrderDTO extends PrepayOrderEntity {

    private static final long serialVersionUID = 1L;

    String poCode;

    String statuss;

    String createTimeStart;

    String createTimeEnd;

}

package org.springblade.modules.material.dto;

import lombok.Data;
import lombok.EqualsAndHashCode;
import org.springblade.modules.material.entity.MaterialPriceEntity;
import org.springblade.modules.material.entity.MaterialPriceNewEntity;

import java.util.Date;

/**
 *  模型DTO
 *
 * @author Will
 */
@Data
@EqualsAndHashCode(callSuper = true)
public class MaterialPriceNewDTO extends MaterialPriceNewEntity {

    private static final long serialVersionUID = 1L;

    private Date createTimeStart;
    private Date createTimeEnd;

}

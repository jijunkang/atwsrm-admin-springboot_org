package org.springblade.modules.mathmodel.vo;

import lombok.Data;
import lombok.EqualsAndHashCode;
import org.springblade.modules.mathmodel.entity.MmOutMarginEntity;

import java.util.List;

/**
 *  模型VO
 *
 * @author Will
 */
@Data
@EqualsAndHashCode(callSuper = true)
public class MmOutMarginVO extends MmOutMarginEntity {

	private static final long serialVersionUID = 1L;

    List<MmOutMarginEntity> histories;
}

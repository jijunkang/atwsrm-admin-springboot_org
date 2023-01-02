package org.springblade.modules.mathmodel.vo;

import lombok.Data;
import lombok.EqualsAndHashCode;
import org.springblade.modules.mathmodel.entity.MmCoefEntity;

import java.util.List;

/**
 *  模型VO
 *
 * @author Will
 */
@Data
@EqualsAndHashCode(callSuper = true)
public class MmCoefVO extends MmCoefEntity {

	private static final long serialVersionUID = 1L;

    List<MmCoefEntity> histories;
}

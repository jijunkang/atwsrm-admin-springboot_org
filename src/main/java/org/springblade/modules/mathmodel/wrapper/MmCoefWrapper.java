package org.springblade.modules.mathmodel.wrapper;

import org.springblade.core.mp.support.BaseEntityWrapper;
import org.springblade.core.tool.utils.BeanUtil;
import org.springblade.modules.mathmodel.entity.MmCoefEntity;
import org.springblade.modules.mathmodel.vo.MmCoefVO;

/**
 *  包装类,返回视图层所需的字段
 *
 * @author Will
 */
public class MmCoefWrapper extends BaseEntityWrapper<MmCoefEntity, MmCoefVO>  {

	public static MmCoefWrapper build() {
		return new MmCoefWrapper();
 	}

	@Override
	public MmCoefVO entityVO(MmCoefEntity mmCoef) {
		MmCoefVO mmCoefVO = BeanUtil.copy(mmCoef, MmCoefVO.class);

		return mmCoefVO;
	}

}

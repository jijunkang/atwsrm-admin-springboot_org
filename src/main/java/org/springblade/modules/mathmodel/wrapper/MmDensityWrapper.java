package org.springblade.modules.mathmodel.wrapper;

import org.springblade.core.mp.support.BaseEntityWrapper;
import org.springblade.core.tool.utils.BeanUtil;
import org.springblade.modules.mathmodel.entity.MmDensityEntity;
import org.springblade.modules.mathmodel.vo.MmDensityVO;

/**
 *  包装类,返回视图层所需的字段
 *
 * @author Will
 */
public class MmDensityWrapper extends BaseEntityWrapper<MmDensityEntity, MmDensityVO>  {

	public static MmDensityWrapper build() {
		return new MmDensityWrapper();
 	}

	@Override
	public MmDensityVO entityVO(MmDensityEntity mmDensity) {
		MmDensityVO mmDensityVO = BeanUtil.copy(mmDensity, MmDensityVO.class);

		return mmDensityVO;
	}

}

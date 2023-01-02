package org.springblade.modules.mathmodel.wrapper;

import org.springblade.core.mp.support.BaseEntityWrapper;
import org.springblade.core.tool.utils.BeanUtil;
import org.springblade.modules.mathmodel.entity.MmOutMarginEntity;
import org.springblade.modules.mathmodel.vo.MmOutMarginVO;

/**
 *  包装类,返回视图层所需的字段
 *
 * @author Will
 */
public class MmOutMarginWrapper extends BaseEntityWrapper<MmOutMarginEntity, MmOutMarginVO>  {

	public static MmOutMarginWrapper build() {
		return new MmOutMarginWrapper();
 	}

	@Override
	public MmOutMarginVO entityVO(MmOutMarginEntity mmOutMargin) {
		MmOutMarginVO mmOutMarginVO = BeanUtil.copy(mmOutMargin, MmOutMarginVO.class);

		return mmOutMarginVO;
	}

}

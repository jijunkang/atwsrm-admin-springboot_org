package org.springblade.modules.mathmodel.wrapper;

import org.springblade.core.mp.support.BaseEntityWrapper;
import org.springblade.core.tool.utils.BeanUtil;
import org.springblade.modules.mathmodel.entity.MmInMarginEntity;
import org.springblade.modules.mathmodel.vo.MmInMarginVO;

/**
 *  包装类,返回视图层所需的字段
 *
 * @author Will
 */
public class MmInMarginWrapper extends BaseEntityWrapper<MmInMarginEntity, MmInMarginVO>  {

	public static MmInMarginWrapper build() {
		return new MmInMarginWrapper();
 	}

	@Override
	public MmInMarginVO entityVO(MmInMarginEntity mmInMargin) {
		MmInMarginVO mmInMarginVO = BeanUtil.copy(mmInMargin, MmInMarginVO.class);

		return mmInMarginVO;
	}

}

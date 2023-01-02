package org.springblade.modules.mathmodel.wrapper;

import org.springblade.core.mp.support.BaseEntityWrapper;
import org.springblade.core.tool.utils.BeanUtil;
import org.springblade.modules.mathmodel.entity.MmHotPriceEntity;
import org.springblade.modules.mathmodel.vo.MmHotPriceVO;

/**
 *  包装类,返回视图层所需的字段
 *
 * @author Will
 */
public class MmHotPriceWrapper extends BaseEntityWrapper<MmHotPriceEntity, MmHotPriceVO>  {

	public static MmHotPriceWrapper build() {
		return new MmHotPriceWrapper();
 	}

	@Override
	public MmHotPriceVO entityVO(MmHotPriceEntity mmHotPrice) {
		MmHotPriceVO mmHotPriceVO = BeanUtil.copy(mmHotPrice, MmHotPriceVO.class);

		return mmHotPriceVO;
	}

}

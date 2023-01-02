package org.springblade.modules.priceframe.wrapper;

import org.springblade.core.mp.support.BaseEntityWrapper;
import org.springblade.core.tool.utils.BeanUtil;
import org.springblade.modules.priceframe.entity.PriceFrameEntity;
import org.springblade.modules.priceframe.vo.PriceFrameVO;

/**
 *  包装类,返回视图层所需的字段
 *
 * @author Will
 */
public class PriceFrameWrapper extends BaseEntityWrapper<PriceFrameEntity, PriceFrameVO>  {

	public static PriceFrameWrapper build() {
		return new PriceFrameWrapper();
 	}

	@Override
	public PriceFrameVO entityVO(PriceFrameEntity price_lib) {
		PriceFrameVO price_libVO = BeanUtil.copy(price_lib, PriceFrameVO.class);

		return price_libVO;
	}

}

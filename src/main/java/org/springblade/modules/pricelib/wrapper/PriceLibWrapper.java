package org.springblade.modules.pricelib.wrapper;

import org.springblade.core.mp.support.BaseEntityWrapper;
import org.springblade.core.tool.utils.BeanUtil;
import org.springblade.modules.pricelib.entity.PriceLibEntity;
import org.springblade.modules.pricelib.vo.PriceLibVO;

/**
 *  包装类,返回视图层所需的字段
 *
 * @author Will
 */
public class PriceLibWrapper extends BaseEntityWrapper<PriceLibEntity, PriceLibVO>  {

	public static PriceLibWrapper build() {
		return new PriceLibWrapper();
 	}

	@Override
	public PriceLibVO entityVO(PriceLibEntity price_lib) {
		PriceLibVO price_libVO = BeanUtil.copy(price_lib, PriceLibVO.class);

		return price_libVO;
	}

}

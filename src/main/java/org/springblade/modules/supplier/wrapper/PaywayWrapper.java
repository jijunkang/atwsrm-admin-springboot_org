package org.springblade.modules.supplier.wrapper;

import org.springblade.core.mp.support.BaseEntityWrapper;
import org.springblade.core.tool.utils.BeanUtil;
import org.springblade.modules.supplier.entity.PaywayEntity;
import org.springblade.modules.supplier.vo.PaywayVO;

/**
 *  包装类,返回视图层所需的字段
 *
 * @author Will
 */
public class PaywayWrapper extends BaseEntityWrapper<PaywayEntity, PaywayVO>  {

	public static PaywayWrapper build() {
		return new PaywayWrapper();
 	}

	@Override
	public PaywayVO entityVO(PaywayEntity payway) {
		PaywayVO paywayVO = BeanUtil.copy(payway, PaywayVO.class);

		return paywayVO;
	}

}

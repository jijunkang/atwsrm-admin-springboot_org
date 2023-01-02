package org.springblade.modules.outpr.wrapper;

import org.springblade.core.mp.support.BaseEntityWrapper;
import org.springblade.core.tool.utils.BeanUtil;
import org.springblade.modules.outpr.entity.OutSupItemPriceEntity;
import org.springblade.modules.outpr.vo.OutSupItemPriceVO;

/**
 *  包装类,返回视图层所需的字段
 *
 * @author Will
 */
public class OutSupItemPriceWrapper extends BaseEntityWrapper<OutSupItemPriceEntity, OutSupItemPriceVO>  {

	public static OutSupItemPriceWrapper build() {
		return new OutSupItemPriceWrapper();
 	}

	@Override
	public OutSupItemPriceVO entityVO(OutSupItemPriceEntity outsupitemprice) {
		OutSupItemPriceVO outsupitempriceVO = BeanUtil.copy(outsupitemprice, OutSupItemPriceVO.class);

		return outsupitempriceVO;
	}

}

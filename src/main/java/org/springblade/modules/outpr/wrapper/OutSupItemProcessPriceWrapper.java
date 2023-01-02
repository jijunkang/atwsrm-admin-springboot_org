package org.springblade.modules.outpr.wrapper;

import org.springblade.core.mp.support.BaseEntityWrapper;
import org.springblade.core.tool.utils.BeanUtil;
import org.springblade.modules.outpr.entity.OutSupItemProcessPriceEntity;
import org.springblade.modules.outpr.vo.OutSupItemProcessPriceVO;

/**
 *  包装类,返回视图层所需的字段
 *
 * @author Will
 */
public class OutSupItemProcessPriceWrapper extends BaseEntityWrapper<OutSupItemProcessPriceEntity, OutSupItemProcessPriceVO>  {

	public static OutSupItemProcessPriceWrapper build() {
		return new OutSupItemProcessPriceWrapper();
 	}

	@Override
	public OutSupItemProcessPriceVO entityVO(OutSupItemProcessPriceEntity outsupitemprocessprice) {
		OutSupItemProcessPriceVO outsupitemprocesspriceVO = BeanUtil.copy(outsupitemprocessprice, OutSupItemProcessPriceVO.class);

		return outsupitemprocesspriceVO;
	}

}

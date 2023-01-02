package org.springblade.modules.outpr.wrapper;

import org.springblade.core.mp.support.BaseEntityWrapper;
import org.springblade.core.tool.utils.BeanUtil;
import org.springblade.modules.outpr.entity.OutSupPreOrderEntity;
import org.springblade.modules.outpr.vo.OutSupPreOrderVO;

/**
 *  包装类,返回视图层所需的字段
 *
 * @author Will
 */
public class OutSupPreOrderWrapper extends BaseEntityWrapper<OutSupPreOrderEntity, OutSupPreOrderVO>  {

	public static OutSupPreOrderWrapper build() {
		return new OutSupPreOrderWrapper();
 	}

	@Override
	public OutSupPreOrderVO entityVO(OutSupPreOrderEntity outsuppreorder) {
		OutSupPreOrderVO outsuppreorderVO = BeanUtil.copy(outsuppreorder, OutSupPreOrderVO.class);

		return outsuppreorderVO;
	}

}

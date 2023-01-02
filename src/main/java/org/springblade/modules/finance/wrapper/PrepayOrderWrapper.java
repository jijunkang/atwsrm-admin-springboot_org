package org.springblade.modules.finance.wrapper;

import org.springblade.core.mp.support.BaseEntityWrapper;
import org.springblade.core.tool.utils.BeanUtil;
import org.springblade.modules.finance.entity.PrepayOrderEntity;
import org.springblade.modules.finance.vo.PrepayOrderVO;

/**
 *  包装类,返回视图层所需的字段
 *
 * @author Will
 */
public class PrepayOrderWrapper extends BaseEntityWrapper<PrepayOrderEntity, PrepayOrderVO>  {

	public static PrepayOrderWrapper build() {
		return new PrepayOrderWrapper();
 	}

	@Override
	public PrepayOrderVO entityVO(PrepayOrderEntity prepayOrder) {
		PrepayOrderVO prepayOrderVO = BeanUtil.copy(prepayOrder, PrepayOrderVO.class);

		return prepayOrderVO;
	}

}

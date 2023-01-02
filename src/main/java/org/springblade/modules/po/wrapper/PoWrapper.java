package org.springblade.modules.po.wrapper;

import org.springblade.core.mp.support.BaseEntityWrapper;
import org.springblade.core.tool.utils.BeanUtil;
import org.springblade.modules.po.entity.PoEntity;
import org.springblade.modules.po.vo.PoVO;

/**
 * 采购订单表头 包装类,返回视图层所需的字段
 *
 * @author Will
 */
public class PoWrapper extends BaseEntityWrapper<PoEntity, PoVO>  {

	public static PoWrapper build() {
		return new PoWrapper();
 	}

	@Override
	public PoVO entityVO(PoEntity po) {
		PoVO poVO = BeanUtil.copy(po, PoVO.class);

		return poVO;
	}

}

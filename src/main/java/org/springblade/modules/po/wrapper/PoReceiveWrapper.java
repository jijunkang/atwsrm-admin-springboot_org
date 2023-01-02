package org.springblade.modules.po.wrapper;

import org.springblade.core.mp.support.BaseEntityWrapper;
import org.springblade.core.tool.utils.BeanUtil;
import org.springblade.modules.po.entity.PoReceiveEntity;
import org.springblade.modules.po.vo.PoReceiveVO;

/**
 *  包装类,返回视图层所需的字段
 *
 * @author Will
 */
public class PoReceiveWrapper extends BaseEntityWrapper<PoReceiveEntity, PoReceiveVO>  {

	public static PoReceiveWrapper build() {
		return new PoReceiveWrapper();
 	}

	@Override
	public PoReceiveVO entityVO(PoReceiveEntity poReceive) {
		PoReceiveVO poReceiveVO = BeanUtil.copy(poReceive, PoReceiveVO.class);

		return poReceiveVO;
	}

}

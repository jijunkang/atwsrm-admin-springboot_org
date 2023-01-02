package org.springblade.modules.po.wrapper;

import org.springblade.core.mp.support.BaseEntityWrapper;
import org.springblade.core.tool.utils.BeanUtil;
import org.springblade.modules.po.entity.PoRemindEntity;
import org.springblade.modules.po.vo.PoRemindVO;

/**
 *  包装类,返回视图层所需的字段
 *
 * @author Will
 */
public class PoRemindWrapper extends BaseEntityWrapper<PoRemindEntity, PoRemindVO>  {

	public static PoRemindWrapper build() {
		return new PoRemindWrapper();
 	}

	@Override
	public PoRemindVO entityVO(PoRemindEntity poremind) {
		PoRemindVO poremindVO = BeanUtil.copy(poremind, PoRemindVO.class);

		return poremindVO;
	}

}

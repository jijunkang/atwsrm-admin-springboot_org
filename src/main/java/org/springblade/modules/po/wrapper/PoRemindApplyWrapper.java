package org.springblade.modules.po.wrapper;

import org.springblade.core.mp.support.BaseEntityWrapper;
import org.springblade.core.tool.utils.BeanUtil;
import org.springblade.modules.po.entity.PoRemindApplyEntity;
import org.springblade.modules.po.vo.PoRemindApplyVO;

/**
 *  包装类,返回视图层所需的字段
 *
 * @author Will
 */
public class PoRemindApplyWrapper extends BaseEntityWrapper<PoRemindApplyEntity, PoRemindApplyVO>  {

	public static PoRemindApplyWrapper build() {
		return new PoRemindApplyWrapper();
 	}

	@Override
	public PoRemindApplyVO entityVO(PoRemindApplyEntity poremindapply) {
		PoRemindApplyVO poremindapplyVO = BeanUtil.copy(poremindapply, PoRemindApplyVO.class);

		return poremindapplyVO;
	}

}

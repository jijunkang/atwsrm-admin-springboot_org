package org.springblade.modules.po.wrapper;

import org.springblade.core.mp.support.BaseEntityWrapper;
import org.springblade.core.tool.utils.BeanUtil;
import org.springblade.modules.po.entity.PoOffsetViewEntity;
import org.springblade.modules.po.vo.PoOffsetViewVO;

/**
 *  包装类,返回视图层所需的字段
 *
 * @author Will
 */
public class PoOffsetViewWrapper extends BaseEntityWrapper<PoOffsetViewEntity, PoOffsetViewVO>  {

	public static PoOffsetViewWrapper build() {
		return new PoOffsetViewWrapper();
 	}

	@Override
	public PoOffsetViewVO entityVO(PoOffsetViewEntity pooffsetview) {
		PoOffsetViewVO pooffsetviewVO = BeanUtil.copy(pooffsetview, PoOffsetViewVO.class);

		return pooffsetviewVO;
	}

}

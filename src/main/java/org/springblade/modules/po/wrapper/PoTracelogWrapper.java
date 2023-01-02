package org.springblade.modules.po.wrapper;

import org.springblade.core.mp.support.BaseEntityWrapper;
import org.springblade.core.tool.utils.BeanUtil;
import org.springblade.modules.po.entity.PoTracelogEntity;
import org.springblade.modules.po.vo.PoTracelogVO;

/**
 *  包装类,返回视图层所需的字段
 *
 * @author Will
 */
public class PoTracelogWrapper extends BaseEntityWrapper<PoTracelogEntity, PoTracelogVO>  {

	public static PoTracelogWrapper build() {
		return new PoTracelogWrapper();
 	}

	@Override
	public PoTracelogVO entityVO(PoTracelogEntity poTracelog) {
		PoTracelogVO poTracelogVO = BeanUtil.copy(poTracelog, PoTracelogVO.class);

		return poTracelogVO;
	}

}

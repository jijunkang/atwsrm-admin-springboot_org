package org.springblade.modules.outpr.wrapper;

import org.springblade.core.mp.support.BaseEntityWrapper;
import org.springblade.core.tool.utils.BeanUtil;
import org.springblade.modules.outpr.entity.OutPrItemEntity;
import org.springblade.modules.outpr.vo.OutPrItemVO;

/**
 *  包装类,返回视图层所需的字段
 *
 * @author Will
 */
public class OutPrItemWrapper extends BaseEntityWrapper<OutPrItemEntity, OutPrItemVO>  {

	public static OutPrItemWrapper build() {
		return new OutPrItemWrapper();
 	}

	@Override
	public OutPrItemVO entityVO(OutPrItemEntity outpritem) {
		OutPrItemVO outpritemVO = BeanUtil.copy(outpritem, OutPrItemVO.class);

		return outpritemVO;
	}

}

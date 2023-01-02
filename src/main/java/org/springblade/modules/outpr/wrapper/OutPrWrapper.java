package org.springblade.modules.outpr.wrapper;

import org.springblade.core.mp.support.BaseEntityWrapper;
import org.springblade.core.tool.utils.BeanUtil;
import org.springblade.modules.outpr.entity.OutPrEntity;
import org.springblade.modules.outpr.vo.OutPrVO;

/**
 *  包装类,返回视图层所需的字段
 *
 * @author Will
 */
public class OutPrWrapper extends BaseEntityWrapper<OutPrEntity, OutPrVO>  {

	public static OutPrWrapper build() {
		return new OutPrWrapper();
 	}

	@Override
	public OutPrVO entityVO(OutPrEntity outpr) {
		OutPrVO outprVO = BeanUtil.copy(outpr, OutPrVO.class);

		return outprVO;
	}

}

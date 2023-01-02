package org.springblade.modules.outpr.wrapper;

import org.springblade.core.mp.support.BaseEntityWrapper;
import org.springblade.core.tool.utils.BeanUtil;
import org.springblade.modules.outpr.entity.OutPrItemProcessEntity;
import org.springblade.modules.outpr.vo.OutPrItemProcessVO;

/**
 *  包装类,返回视图层所需的字段
 *
 * @author Will
 */
public class OutPrItemProcessWrapper extends BaseEntityWrapper<OutPrItemProcessEntity, OutPrItemProcessVO>  {

	public static OutPrItemProcessWrapper build() {
		return new OutPrItemProcessWrapper();
 	}

	@Override
	public OutPrItemProcessVO entityVO(OutPrItemProcessEntity outpritemprocess) {
		OutPrItemProcessVO outpritemprocessVO = BeanUtil.copy(outpritemprocess, OutPrItemProcessVO.class);

		return outpritemprocessVO;
	}

}

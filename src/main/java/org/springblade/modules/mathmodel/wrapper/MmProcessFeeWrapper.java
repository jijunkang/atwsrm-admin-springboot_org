package org.springblade.modules.mathmodel.wrapper;

import org.springblade.core.mp.support.BaseEntityWrapper;
import org.springblade.core.tool.utils.BeanUtil;
import org.springblade.modules.mathmodel.entity.MmProcessFeeEntity;
import org.springblade.modules.mathmodel.vo.MmProcessFeeVO;

/**
 *  包装类,返回视图层所需的字段
 *
 * @author Will
 */
public class MmProcessFeeWrapper extends BaseEntityWrapper<MmProcessFeeEntity, MmProcessFeeVO>  {

	public static MmProcessFeeWrapper build() {
		return new MmProcessFeeWrapper();
 	}

	@Override
	public MmProcessFeeVO entityVO(MmProcessFeeEntity mmProcessFee) {
		MmProcessFeeVO mmProcessFeeVO = BeanUtil.copy(mmProcessFee, MmProcessFeeVO.class);

		return mmProcessFeeVO;
	}

}

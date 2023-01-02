package org.springblade.modules.ap.wrapper;

import org.springblade.core.mp.support.BaseEntityWrapper;
import org.springblade.core.tool.utils.BeanUtil;
import org.springblade.modules.ap.entity.ApBillEntity;
import org.springblade.modules.ap.vo.ApBillVO;

/**
 *  包装类,返回视图层所需的字段
 *
 * @author Will
 */
public class ApBillWrapper extends BaseEntityWrapper<ApBillEntity, ApBillVO>  {

	public static ApBillWrapper build() {
		return new ApBillWrapper();
 	}

	@Override
	public ApBillVO entityVO(ApBillEntity apBill) {
		ApBillVO apBillVO = BeanUtil.copy(apBill, ApBillVO.class);

		return apBillVO;
	}

}

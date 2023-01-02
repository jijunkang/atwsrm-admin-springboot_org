package org.springblade.modules.aps.wrapper;

import org.springblade.core.mp.support.BaseEntityWrapper;
import org.springblade.core.tool.utils.BeanUtil;
import org.springblade.modules.aps.entity.DeliveryModifyAuditEntity;
import org.springblade.modules.aps.vo.DeliveryModifyAuditVO;

/**
 *  包装类,返回视图层所需的字段
 *
 * @author Will
 */
public class DeliveryModifyAuditWrapper extends BaseEntityWrapper<DeliveryModifyAuditEntity, DeliveryModifyAuditVO>  {

	public static DeliveryModifyAuditWrapper build() {
		return new DeliveryModifyAuditWrapper();
 	}

	@Override
	public DeliveryModifyAuditVO entityVO(DeliveryModifyAuditEntity deliveryModifyAudit) {
		DeliveryModifyAuditVO deliveryModifyAuditVO = BeanUtil.copy(deliveryModifyAudit, DeliveryModifyAuditVO.class);

		return deliveryModifyAuditVO;
	}

}

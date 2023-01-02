package org.springblade.modules.system.wrapper;

import org.springblade.core.mp.support.BaseEntityWrapper;
import org.springblade.core.tool.utils.BeanUtil;
import org.springblade.modules.system.entity.AuditRecordEntity;
import org.springblade.modules.system.vo.AuditRecordVO;

/**
 *  包装类,返回视图层所需的字段
 *
 * @author Will
 */
public class AuditRecordWrapper extends BaseEntityWrapper<AuditRecordEntity, AuditRecordVO>  {

	public static AuditRecordWrapper build() {
		return new AuditRecordWrapper();
 	}

	@Override
	public AuditRecordVO entityVO(AuditRecordEntity auditRecord) {
		AuditRecordVO auditRecordVO = BeanUtil.copy(auditRecord, AuditRecordVO.class);

		return auditRecordVO;
	}

}

package org.springblade.modules.supplier.wrapper;

import org.springblade.core.mp.support.BaseEntityWrapper;
import org.springblade.core.tool.utils.BeanUtil;
import org.springblade.modules.supplier.entity.Supplier;
import org.springblade.modules.supplier.vo.SupplierVO;

/**
 * 供应商 包装类,返回视图层所需的字段
 *
 * @author Will
 */
public class SupplierWrapper extends BaseEntityWrapper<Supplier, SupplierVO>  {

	public static SupplierWrapper build() {
		return new SupplierWrapper();
 	}

	@Override
	public SupplierVO entityVO(Supplier supplier) {
		SupplierVO supplierVO = BeanUtil.copy(supplier, SupplierVO.class);

		return supplierVO;
	}

}

package org.springblade.modules.supplier.vo;

import io.swagger.annotations.ApiModelProperty;
import lombok.Data;
import lombok.EqualsAndHashCode;
import org.springblade.modules.supplier.entity.Supplier;

/**
 * 供应商 模型VO
 *
 * @author Will
 */
@Data
@EqualsAndHashCode(callSuper = true)
public class SupplierVO extends Supplier {

	private static final long serialVersionUID = 1L;

	private String initialPassword;

}

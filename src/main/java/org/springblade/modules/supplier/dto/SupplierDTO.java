package org.springblade.modules.supplier.dto;

import lombok.Data;
import lombok.EqualsAndHashCode;
import org.springblade.modules.supplier.entity.Supplier;

import java.math.BigDecimal;

/**
 * 供应商 模型DTO
 *
 * @author Will
 */
@Data
@EqualsAndHashCode(callSuper = true)
public class SupplierDTO extends Supplier {

	private static final long serialVersionUID = 1L;

	private String taxRateCode;
	private String type;
    private BigDecimal firstPrepayRate;
    private BigDecimal accumPrepayRate;
    private String remark;


}

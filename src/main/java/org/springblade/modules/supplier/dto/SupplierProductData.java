package org.springblade.modules.supplier.dto;

import com.baomidou.mybatisplus.annotation.TableName;
import io.swagger.annotations.ApiModel;
import lombok.Data;
import org.springblade.modules.supplier.dto.SupplierProductDataList;

import java.io.Serializable;
import java.util.List;


/**
 * 采购送货计划表
 *
 * @author Will
 */
@Data
@TableName("supplier_product_data")
@ApiModel(value = "供应商生产数据", description = "供应商生产数据")
public class SupplierProductData implements Serializable {

    private static final long serialVersionUID = 1L;

    /**
     * 供应商编码
     */
    private String supCode;

    /**
     * 供应商名称
     */
    private String supName;

    private List<SupplierProductDataList> data;

}

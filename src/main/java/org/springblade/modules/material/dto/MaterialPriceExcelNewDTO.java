package org.springblade.modules.material.dto;

import cn.afterturn.easypoi.excel.annotation.Excel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import java.io.Serializable;
import java.math.BigDecimal;

/**
 * 模型DTO
 * @author Will
 */
@Data
public
class MaterialPriceExcelNewDTO implements Serializable{

    private static final long serialVersionUID = 1L;

    @Excel(name = "原材料编码")
    private String material;

    @Excel(name = "工艺")
    private String technic;

    @Excel(name = "原材料价格")
    private String materialPrice;

    @Excel(name = "原材料描述")
    private String materialDesc;

    @Excel(name = "状态",replace = {"生效_10","待提交_20","待审批_30","历史_40"})
    private Integer status;



}

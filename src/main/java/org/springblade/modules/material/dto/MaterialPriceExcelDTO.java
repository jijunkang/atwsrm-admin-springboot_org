package org.springblade.modules.material.dto;

import cn.afterturn.easypoi.excel.annotation.Excel;
import lombok.Data;

import java.io.Serializable;
import java.math.BigDecimal;

/**
 * 模型DTO
 * @author Will
 */
@Data
public
class MaterialPriceExcelDTO implements Serializable{

    private static final long serialVersionUID = 1L;

    @Excel(name = "类型")
    private String type;

    @Excel(name = "标准")
    private String std;

    @Excel(name = "规格")
    private String spec;

    @Excel(name = "材质")
    private String material;

    @Excel(name = "供应商编号")
    private String supCode;

    @Excel(name = "供应商名称")
    private String supName;

    @Excel(name = "公斤单价")
    private BigDecimal priceKg;

    @Excel(name = "线切割加工费")
    private BigDecimal priceCutting;

    @Excel(name = "组间转换率")
    private BigDecimal converRate;

    @Excel(name = "毫米单价")
    private BigDecimal priceMm;

    @Excel(name = "冗余")
    private Double redun;

    @Excel(name = "上偏差")
    private Integer upperDeviation;

    @Excel(name = "下偏差")
    private Integer lowerDeviation;

    private Integer isEnable;
    @Excel(name = "是否有效")
    private String isEnableFmt;

    @Excel(name = "铸件加工费用")
    private BigDecimal castProcFees;


    public String getIsEnableFmt(){
        if(isEnable == 0){
            return "否";
        }else{
            return "是";
        }
    }

    public BigDecimal getPriceCutting(){
        if(priceCutting == null){
            return null;
        }
        if(priceCutting.compareTo(new BigDecimal(0E-8)) == 0){
            return new BigDecimal("0.00");
        }
        return priceCutting;
    }
}

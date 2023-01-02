package org.springblade.modules.supplier.dto;

import cn.afterturn.easypoi.excel.annotation.Excel;
import lombok.Data;

import java.io.Serializable;
import java.math.BigDecimal;


@Data
public class OtdExcel implements Serializable {

    private static final long serialVersionUID = 1L;

    @Excel(name = "子项目号")
    private String proNo;

    @Excel(name = "计划交期")
    private String planDate;

    @Excel(name = "物料编号")
    private String itemCode;

    @Excel(name = "物料描述")
    private String itemName;

    @Excel(name = "供应商编码")
    private String supCode;

    @Excel(name = "供应商名称")
    private String supName;

    @Excel(name = "采购单号")
    private String poCode;

    @Excel(name = "采购行号")
    private String poLn;

    @Excel(name = "审核修改交期")
    private String checkUpdateDate;

    @Excel(name = "需求日期")
    private String reqDate;

    @Excel(name = "aps最后一次出现时间")
    private String apsEndDate;

    private String apsEndFlag;

    private String bizType;

    private String placeName;

    @Excel(name = "是否及时")
    private String isOtd;
}

package org.springblade.modules.report.vo;

import cn.afterturn.easypoi.excel.annotation.Excel;
import lombok.Data;

import java.io.Serializable;
import java.math.BigDecimal;


@Data
public class KeyItemFixedExcel implements Serializable {

    private static final long serialVersionUID = 1L;

    @Excel(name = "子项目号")
    private String proNo;

    @Excel(name = "子项目需求数量")
    private BigDecimal proNum;

    @Excel(name = "计划交期")
    private String planDate;

    @Excel(name = "编号-行号")
    private String poCodeLn;

    @Excel(name = "物料编号")
    private String itemCode;

    @Excel(name = "物料名称")
    private String itemName;

    @Excel(name = "审核修改交期")
    private String checkUpdateDate;

    @Excel(name = "需求数量")
    private String reqDate;
}

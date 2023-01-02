package org.springblade.modules.outpr.dto;

import cn.afterturn.easypoi.excel.annotation.Excel;
import lombok.Data;

import java.io.Serializable;


/**
 *  实体类
 *
 * @author Will
 */
@Data
public class OutPrReportFormsExcelDTO implements Serializable {

	private static final long serialVersionUID = 1L;

    @Excel(name = "原料号")
    private String oldItemCode;

    @Excel(name = "原料品描述")
    private String oldItemName;

    @Excel(name = "送货数量")
    private Integer deliverNum;

    @Excel(name = "送货单单号")
    private String doCode;

    @Excel(name = "子项目号")
    private String qoCode;

    @Excel(name = "项目交期")
    private Long proDate;

    @Excel(name = "mo工单")
    private String moCode;

    @Excel(name = "新料号")
    private String newItemCode;

    @Excel(name = "新料品描述")
    private String newItemName;

    @Excel(name = "数量")
    private Integer recNum;

    @Excel(name = "备注")
    private String remark;
}

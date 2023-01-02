package org.springblade.modules.po.vo;

import cn.afterturn.easypoi.excel.annotation.Excel;
import lombok.Data;
import org.springblade.core.tool.utils.DateUtil;

import java.math.BigDecimal;
import java.util.Date;

/**
 * 模型VO
 *
 * @author Will
 */
@Data
public class PoOffsetViewExcel {

    private static final long serialVersionUID = 1L;

    @Excel(name = "项目号")
    private String proNo;

    @Excel(name = "订单编号")
    private String poCode;

    @Excel(name = "行号")
    private Integer poLn;

    @Excel(name = "物料编号")
    private String itemCode;

    @Excel(name = "物料名称")
    private String itemName;

    @Excel(name = "数量")
    private BigDecimal priceNum;

    @Excel(name = "要求交期")
    private String reqDateFmt;
    private Long reqDate;

    @Excel(name = "承诺交期")
    private String supConfirmDateFmt;
    private Long supConfirmDate;

    @Excel(name = "修改交期")
    private String supUpdateDateFmt;
    private Long supUpdateDate;

    @Excel(name = "运算交期")
    private String operationDateFmt;
    private Long operationDate;

    @Excel(name = "备注")
    private String remark;

    @Excel(name = "供应商名称")
    private String supName;

    @Excel(name = "联系人")
    private String supContact;

    @Excel(name = "电话号码")
    private String supMobile;

    public String getReqDateFmt() {
        if (reqDate == null || reqDate <= 0) {
            return "";
        }
        return DateUtil.formatDate(new Date(reqDate * 1000));
    }

    public String getSupConfirmDateFmt() {
        if (supConfirmDate == null || supConfirmDate <= 0) {
            return "";
        }
        return DateUtil.formatDate(new Date(supConfirmDate * 1000));
    }

    public String getSupUpdateDateFmt() {
        if (supUpdateDate == null || supUpdateDate <= 0) {
            return "";
        }
        return DateUtil.formatDate(new Date(supUpdateDate * 1000));
    }

    public String getOperationDateFmt() {
        if (operationDate == null || operationDate <= 0) {
            return "";
        }
        return DateUtil.formatDate(new Date(operationDate * 1000));
    }


}

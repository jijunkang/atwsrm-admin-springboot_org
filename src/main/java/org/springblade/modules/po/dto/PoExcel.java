package org.springblade.modules.po.dto;

import cn.afterturn.easypoi.excel.annotation.Excel;
import lombok.Data;
import org.springblade.core.tool.utils.DateUtil;
import org.springblade.core.tool.utils.StringUtil;

import java.io.Serializable;
import java.math.BigDecimal;
import java.util.Date;

/**
 * @author libin
 *
 * 待处理订单导出DTO
 *
 * @date 15:17 2020/8/14
 **/
@Data
public class PoExcel implements Serializable {

    private static final long serialVersionUID = 1L;

    @Excel(name = "订单号")
    private String orderCode;

    private Integer docDate;
    @Excel(name = "下单日期")
    private String docDateFmt;

    @Excel(name = "订单总金额")
    private BigDecimal docAmount;

    @Excel(name = "供应商编号")
    private String supCode;

    @Excel(name = "供应商名称")
    private String supName;

    private Integer isBizClosed;
    @Excel(name = "业务关闭")
    private String isBizClosedFmt;

    private Integer status;
    @Excel(name = "状态")
    private String statusFmt;

    private String templateType;
    @Excel(name = "法律模板")
    private String templateTypeFmt;

    public String getDocDateFmt() {
        if (docDate == null || docDate <= 0) {
            docDateFmt = "";
        } else {
            long date = docDate;
            docDateFmt = DateUtil.formatDate(new Date(date * 1000));
        }
        return docDateFmt;
    }

    public String getIsBizClosedFmt(){
        if(StringUtil.isEmpty(isBizClosed)){
            return "否";
        }
        return isBizClosed == 0 ? "否" : "是";
    }

    public String getStatusFmt(){
        switch (status){
            case 10:
                return "待确认";
            case 20:
                return "退回";
            case 30:
                return "待上传";
            case 40:
                return "待审核";
            case 50:
                return "执行中";
            case 60:
                return "合同拒绝";
            case 70:
                return "关闭";
            default:
                return "";
        }
    }

    public String getTemplateTypeFmt(){
        if("A".equals(templateType)) {
            return "普通模板 - 内仓";
        } else if ("AW".equals(templateType)){
            return "普通模板 - 外仓";
        } else if ("B".equals(templateType)){
            return "法律模板 - 内仓";
        } else if ("AW".equals(templateType)){
            return "法律模板 - 外仓";
        } else if ("C".equals(templateType)){
            return "刀具模板 - 外仓";
        } else if ("CW".equals(templateType)){
            return "刀具模板 - 外仓";
        } else if ("DW".equals(templateType)){
            return "整机模板 - 外仓";
        } else if ("mould".equals(templateType)){
            return "整机模板 - 外仓";
        } else if ("mouldW".equals(templateType)){
            return "模具模板 - 外仓";
        }
        return "";
    }
}

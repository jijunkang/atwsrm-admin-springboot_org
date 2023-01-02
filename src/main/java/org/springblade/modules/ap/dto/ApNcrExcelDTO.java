package org.springblade.modules.ap.dto;

import cn.afterturn.easypoi.excel.annotation.Excel;
import cn.afterturn.easypoi.excel.annotation.ExcelCollection;
import lombok.Data;
import org.springblade.core.tool.utils.StringUtil;

import java.io.Serializable;
import java.math.BigDecimal;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;

/**
 * @author libin
 *
 * @date 17:02 2020/8/6
 **/
@Data
public class ApNcrExcelDTO implements Serializable {

    private static final long serialVersionUID = 1L;

    @Excel(name = "供应商编号", needMerge = true)
    private String supCode;

    @Excel(name = "供应商名称", needMerge = true)
    private String supName;

    @Excel(name = "扣款单号", needMerge = true)
    private String rcvCode;

    @Excel(name = "扣款金额", needMerge = true)
    private BigDecimal taxSubTotal;

    @ExcelCollection(name = "")
    List<ApNcrItemExcelDTO> apNcrItemExcelDTOS;

    @Excel(name = "提交人", needMerge = true)
    private String createUser;

    private Date createTime;
    @Excel(name = "提交时间", needMerge = true)
    private String createTimeFmt;

    private Integer status;
    @Excel(name = "状态", needMerge = true)
    private String statusFmt;

    private String getCreateTimeFmt() {
        SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd");
        if (!StringUtil.isEmpty(createTime)) {
            return sdf.format(createTime);
        }
        return null;
    }

    private BigDecimal getTaxSubTotal(){
        return taxSubTotal.abs();
    }

    private String getStatusFmt(){
        switch (status){
            case 11:
                return "开立";
            case 12:
                return "待审核";
            case 13:
                return "待扣款";
            case 14:
                return "已对账";
            case 15:
                return "已扣款";
            case 16:
                return "已拒绝";
            default:
                return "";
        }
    }

    private String getCreateUser(){
        return "系统";
    }
}

package org.springblade.modules.ncr.dto;

import cn.afterturn.easypoi.excel.annotation.Excel;
import lombok.Data;
import org.springblade.core.tool.utils.StringUtil;

import java.io.Serializable;
import java.math.BigDecimal;
import java.text.SimpleDateFormat;
import java.util.Date;

/**
 * @author libin
 * @date 10:39 2020/8/6
 **/
@Data
public class NcrExcelDTO implements Serializable {

    private static final long serialVersionUID = 1L;

    @Excel(name = "供应商编号")
    private String supCode;

    @Excel(name = "供应商名称")
    private String supName;

    @Excel(name = "不合格品单号")
    private String code;

    @Excel(name = "报告单号")
    private String reportCode;

    @Excel(name = "扣款单号")
    private String rcvCode;

    @Excel(name = "确认人")
    private String confirmName;

    @Excel(name = "处理方式")
    private String processType;

    @Excel(name = "罚款金额")
    private BigDecimal finePrice;

    private Integer status;
    @Excel(name = "是否结案")
    private String statusFmt;

    private Date createTime;
    @Excel(name = "生成日期")
    private String createTimeFmt;

    private Long reqTime;
    @Excel(name = "要求完成日期")
    private String reqTimeFmt;

    private Long caseTime;
    @Excel(name = "结案日期")
    private String caseTimeFmt;


    private String getStatusFmt() {
        return status == 10 ? "是" : "否";
    }

    private String getReqTimeFmt() {
        SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd");
        if(!StringUtil.isEmpty(reqTime)){
            return sdf.format(new Date(reqTime * 1000));
        }
        return null;
    }

    private String getCaseTimeFmt() {
        SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd");
        if(!StringUtil.isEmpty(caseTime)){
            return sdf.format(new Date(caseTime * 1000));
        }
        return null;
    }

    private String getCreateTimeFmt(){
        SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd");
        if(!StringUtil.isEmpty(createTime)){
            return sdf.format(createTime);
        }
        return null;
    }
}

package org.springblade.modules.pr.dto;

import cn.afterturn.easypoi.excel.annotation.Excel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;
import org.springblade.common.utils.WillDateUtil;

import java.io.Serializable;
import java.math.BigDecimal;

/**
 * @author libin
 * @date 14:22 2020/9/28
 **/
@Data
public class U9PrFlowExcelDTO implements Serializable {

    private static final long serialVersionUID = 1L;

    @Excel(name = "请购日期")
    private String prDateFmt;

    @Excel(name = "请购单号")
    private String prCode;

    @Excel(name = "行号")
    private Integer prLn;

    @Excel(name = "物料编号")
    private String itemCode;

    @Excel(name = "物料描述")
    private String itemName;

    @Excel(name = "计价数量")
    private BigDecimal priceNum;

    @Excel(name = "计价单位")
    private String priceUom;

    @Excel(name = "需求交期")
    private String reqDateFmt;

    @Excel(name = "历史最高价")
    private BigDecimal highestPrice;

    @Excel(name = "历史最低价")
    private BigDecimal lowestPrice;

    @Excel(name ="最近采购价")
    private BigDecimal lastPrice;

    @Excel(name = "上次采购供应商")
    private String lastSupName;

    @Excel(name = "生产订单号")
    private String moNo;

    @Excel(name = "项目号")
    private String proNo;

    @Excel(name = "报价编号")
    private String qoNo;

    @Excel(name = "最终用户")
    private String endUser;

    @Excel(name = "是否有供应")
    private String isHavesup;

    @Excel(name = "请购单备注")
    private String requisitionRemark;

    private String piLastSupName;

    @Excel(name = "采购数量")
    private BigDecimal tcNum;

    @Excel(name = "采购单位")
    private String tcUom;

    @Excel(name = "采购单价")
    private String quotePrice;

    @Excel(name = "供应商编码")
    private String supCode;

    @Excel(name = "供应商名称")
    private String supName;

    private Long reqDate;

    private Long prDate;

    private String flowType;

    //@Excel(name = "流标原因")
    private String flowTypeFmt;

    //@Excel(name = "审核备注")
    private String remark;

    private Integer status;

    //@Excel(name = "状态")
    private String statusFmt;

    public String getReqDateFmt() {
        reqDateFmt = WillDateUtil.unixTimeToStr(reqDate, "yyyy-MM-dd");
        return reqDateFmt;
    }

    public String getPrDateFmt() {
        prDateFmt = WillDateUtil.unixTimeToStr(prDate, "yyyy-MM-dd");
        return prDateFmt;
    }

    public String getStatusFmt() {
        if (status == 20) {
            return "挂起";
        } else if (status == 40) {
            return "流标";
        } else if (status == 41) {
            return "待提交";
        } else {
            return "";
        }
    }

    public String getFlowTypeFmt() {
        if ("no_sup".equals(flowType)) {
            return "无供应商";
        } else if ("no_quote".equals(flowType)) {
            return "超时未报价";
        } else if ("sup_refuse".equals(flowType)) {
            return "供应商拒绝";
        } else if ("pr3".equals(flowType)) {
            return "PR3";
        } else if ("priceattr_err".equals(flowType)) {
            return "数据异常";
        } else if ("atwreject".equals(flowType)) {
            return "审核拒绝流标";
        } else if ("inqdate_refuse".equals(flowType)) {
            return "询交期转流标";
        } else if ("inqprice_refuse".equals(flowType)) {
            return "询价转流标";
        } else if ("po_cancel".equals(flowType)) {
            return "PO单取消";
        } else {
            return "";
        }
    }
}

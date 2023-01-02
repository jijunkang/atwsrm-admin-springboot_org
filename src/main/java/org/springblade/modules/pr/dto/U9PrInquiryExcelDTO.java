package org.springblade.modules.pr.dto;

import cn.afterturn.easypoi.excel.annotation.Excel;
import lombok.Data;
import org.springblade.common.utils.WillDateUtil;

import java.io.Serializable;
import java.math.BigDecimal;

/**
 * @author libin
 * @date 13:47 2020/9/28
 **/
@Data
public class U9PrInquiryExcelDTO implements Serializable {

    private static final long serialVersionUID = 1L;

    @Excel(name = "请购单号")
    private String prCode;

    @Excel(name = "行号")
    private Integer prLn;

    @Excel(name = "物料编号")
    private String itemCode;

    @Excel(name = "物料描述")
    private String itemName;

    @Excel(name = "项目号")
    private String proNo;

    @Excel(name = "采购数量")
    private BigDecimal tcNum;

    @Excel(name = "采购单位")
    private String tcUom;

    @Excel(name = "计价数量")
    private BigDecimal priceNum;

    @Excel(name = "计价单位")
    private String priceUom;

    private Long reqDate;
    @Excel(name = "要求交期")
    private String reqDateFmt;

    private Long prDate;
    @Excel(name = "请购日期")
    private String prDateFmt;

    private String inquiryWay;
    @Excel(name = "需求来源")
    private String inquiryWayFmt;

    @Excel(name = "是否有供应")
    private String isHavesup;

    @Excel(name = "最终用户")
    private String endUser;


    public String getReqDateFmt() {
        reqDateFmt = WillDateUtil.unixTimeToStr(reqDate, "yyyy-MM-dd");
        return reqDateFmt;
    }

    public String getPrDateFmt() {
        prDateFmt = WillDateUtil.unixTimeToStr(prDate, "yyyy-MM-dd");
        return prDateFmt;
    }

    public String getInquiryWayFmt() {
        if ("assign".equals(inquiryWay)) {
            return "指定供应商";
        } else if ("exclusive".equals(inquiryWay)) {
            return "独家采购";
        } else if ("have_price".equals(inquiryWay)) {
            return "有价格";
        } else if ("have_price1date".equals(inquiryWay)) {
            return "有价格1";
        } else if ("have_model".equals(inquiryWay)) {
            return "有数学模型";
        } else if ("have_protocol".equals(inquiryWay)) {
            return "有框架协议";
        } else if ("compete".equals(inquiryWay)) {
            return "供应商询价";
        } else if ("no_sup".equals(inquiryWay)) {
            return "无供应商";
        }
        return "";
    }
}

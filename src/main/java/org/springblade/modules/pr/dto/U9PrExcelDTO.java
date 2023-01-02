package org.springblade.modules.pr.dto;

import cn.afterturn.easypoi.excel.annotation.Excel;
import com.fasterxml.jackson.annotation.JsonFormat;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;
import org.springblade.common.utils.WillDateUtil;
import org.springblade.modules.system.service.IDictBizService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Lazy;

import java.io.Serializable;
import java.math.BigDecimal;
import java.text.SimpleDateFormat;
import java.util.Date;

/**
 * 请购单 模型DTO
 * @author Will
 */
@Data
public
class U9PrExcelDTO implements Serializable{

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

    @Excel(name = "报价编号")
    private String qoNo;

    @Excel(name = "交易数量")
    private BigDecimal tcNum;

    @Excel(name = "交易单位")
    private String tcUom;

    @Excel(name = "计价数量")
    private BigDecimal priceNum;

    @Excel(name = "计价单位")
    private String priceUom;

    private Long   reqDate;
    @Excel(name = "要求交期")
    private String reqDateFmt;

    private Long   prDate;
    @Excel(name = "请购日期")
    private String prDateFmt;

    private Date createTime;
    @Excel(name = "SRM请购日期")
    private String createTimeFmt;

    private Long orderTime;
    @Excel(name = "下单日期")
    private String orderTimeFmt;

    private String inquiryWay;
    @Excel(name = "询价方式")
    private String inquiryWayFmt;

    @Excel(name = "流标原因")
    private String flowType;

    @Excel(name = "附件")
    private String     attachment;

    @Excel(name = "采购员名称")
    private String     purchName;

    @Excel(name = "最终用户")
    private String     endUser;

    private Integer status;
    @Excel(name = "状态")
    private String statusFmt;

    @Excel(name =  "是否有供应")
    private String isHavesup;

    public
    String getReqDateFmt(){
        reqDateFmt = WillDateUtil.unixTimeToStr(reqDate, "yyyy-MM-dd");
        return reqDateFmt;
    }

    public
    String getPrDateFmt(){
        prDateFmt = WillDateUtil.unixTimeToStr(prDate, "yyyy-MM-dd");
        return prDateFmt;
    }

    public String getOrderTimeFmt(){
        orderTimeFmt = WillDateUtil.unixTimeToStr(orderTime, "yyyy-MM-dd");
        return orderTimeFmt;
    }

    public String getCreateTimeFmt(){
        SimpleDateFormat format = new SimpleDateFormat("yyyy-MM-dd");
        createTimeFmt = format.format(createTime);
        return createTimeFmt;
    }

}

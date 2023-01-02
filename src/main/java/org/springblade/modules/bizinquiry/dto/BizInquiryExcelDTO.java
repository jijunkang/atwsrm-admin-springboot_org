package org.springblade.modules.bizinquiry.dto;

import cn.afterturn.easypoi.excel.annotation.Excel;
import com.fasterxml.jackson.databind.annotation.JsonSerialize;
import com.fasterxml.jackson.databind.ser.std.ToStringSerializer;
import lombok.Data;
import org.springblade.core.tool.utils.StringUtil;

import java.io.Serializable;
import java.math.BigDecimal;
import java.text.SimpleDateFormat;
import java.util.Date;

/**
 * @author libin
 * @date 14:35 2020/6/24
 **/
@Data
public class BizInquiryExcelDTO implements Serializable {

    private static final long serialVersionUID = 1L;

    /**
     * ID
     */
    @Excel(name = "ID")
    @JsonSerialize(using = ToStringSerializer.class)
    private Long id;
    /**
     * 报价编号
     */
    @Excel(name = "报价编号")
    private String qoCode;
    /**
     * 项目保护
     */
    private Integer projProtect;
    @Excel(name = "项目保护")
    private String projProtectFmt;
    /**
     * 型号
     */
    @Excel(name = "阀门类型")
    private String type;
    /**
     * 物料描述
     */
    @Excel(name = "物料描述")
    private String model;
    /**
     * 品牌
     */
    @Excel(name = "品牌")
    private String brand;
    /**
     * 单位
     */
    @Excel(name = "单位")
    private String uom;
    /**
     * 数量
     */
    @Excel(name = "数量")
    private BigDecimal num;
    /**
     * 单价
     */
    @Excel(name = "单价")
    private BigDecimal price;
    /**
     * 供应商
     */
    @Excel(name = "供应商")
    private String supName;
    /**
     * 交货期
     */
    @Excel(name = "交货期")
    private String deliveryDate;
    /**
     * 报价有效期
     */
    @Excel(name = "报价有效期")
    private String offerValidity;
    /**
     * 价格归属
     */
    @Excel(name = "价格归属")
    private String attribution;
    /**
     * 供应商反馈
     */
    @Excel(name = "供应商反馈")
    private String supFeedback;
    /**
     * 最终用户
     */
    @Excel(name = "最终用户")
    private String endUser;
    /**
     * 联系人
     */
    @Excel(name = "联系人")
    private String contactName;
    /**
     * 联系电话
     */
    @Excel(name = "联系电话")
    private String contactPhone;
    /**
     * 申请日期
     */
    private Date createTime;
    @Excel(name = "申请日期")
    private String createTimeFmt;
    /**
     * 截止日期
     */
    private Long reqDate;
    @Excel(name = "截止日期")
    private String reqDateFmt;
    /**
     * 完成日期
     */
    private Long confirmDate;
    @Excel(name = "完成日期")
    private String confirmDateFmt;
    /**
     * 特殊要求
     */
    @Excel(name = "特殊要求")
    private String remark;
    /**
     * 招标单位
     */
    @Excel(name = "招标单位")
    private String tenderingOrg;
    /**
     * 状态
     */
    private Integer status;
    @Excel(name = "状态")
    private String statusFmt;
    /**
     * 拒绝原因
     */
    @Excel(name = "拒绝原因")
    private String backReason;


    public String getCreateTimeFmt(){
        SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd");
        if (!StringUtil.isEmpty(createTime)) {
            return sdf.format(createTime);
        }
        if (!StringUtil.isEmpty(createTimeFmt)) {
            return createTimeFmt;
        }
        return null;
    }

    public String getReqDateFmt() {
        SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd");
        if (!StringUtil.isEmpty(reqDate)) {
            return sdf.format(new Date(reqDate * 1000));
        }
        if(!StringUtil.isEmpty(reqDateFmt)){
            return reqDateFmt;
        }
        return null;
    }

    public String getConfirmDateFmt() {
        SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd");
        if (!StringUtil.isEmpty(confirmDate)) {
            return sdf.format(new Date(confirmDate * 1000));
        }
        if(!StringUtil.isEmpty(confirmDateFmt)){
            return confirmDateFmt;
        }
        return null;
    }

    public String getStatusFmt(){
        switch (status){
            case 10:
                return "待报价";
            case 20:
                return "待提交";
            case 30:
                return "待审核";
            case 40:
                return "审核拒绝";
            case 50:
                return "已报价";
            default:
                return "";
        }
    }

    public String getProjProtectFmt(){
        if(StringUtil.isNotBlank(projProtectFmt)){
            return projProtectFmt;
        }
        if(projProtect == 0){
            return "否";
        }else{
            return "是";
        }
    }

}

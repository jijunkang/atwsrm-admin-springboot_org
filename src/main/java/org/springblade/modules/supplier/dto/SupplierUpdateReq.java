package org.springblade.modules.supplier.dto;

import com.fasterxml.jackson.annotation.JsonFormat;
import com.fasterxml.jackson.databind.annotation.JsonSerialize;
import com.fasterxml.jackson.databind.ser.std.ToStringSerializer;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import java.io.Serializable;
import java.math.BigDecimal;
import java.util.Date;

/**
 * 供应商 模型DTO
 * @author Will
 */
@Data
public
class SupplierUpdateReq implements Serializable{
    @JsonSerialize(using = ToStringSerializer.class)
    @ApiModelProperty("主键id")
    private Long id;

    private String     code;

    @ApiModelProperty(value = "供应商名称")
    private String     name;
    /**
     * 主分类编码
     */
    @ApiModelProperty(value = "主分类编码")
    private String     typeCode;
    /**
     * 主分类名称
     */
    @ApiModelProperty(value = "主分类名称")
    private String     typeName;
    /**
     * 国税号
     */
    @ApiModelProperty(value = "国税号")
    private String     nationalTaxCode;
    /**
     * 地税号
     */
    @ApiModelProperty(value = "地税号")
    private String     stateTaxCode;
    /**
     * 税率
     */
    @ApiModelProperty(value = "税率")
    private BigDecimal taxRate;
    /**
     * 手机号码
     */
    @ApiModelProperty(value = "手机号码")
    private String     mobile;
    /**
     * 电话号码
     */
    @ApiModelProperty(value = "电话号码")
    private String     phone;
    /**
     * email
     */
    @ApiModelProperty(value = "email")
    private String     email;
    /**
     * 传真
     */
    @ApiModelProperty(value = "传真")
    private String     fax;
    /**
     * 联系人
     */
    @ApiModelProperty(value = "联系人")
    private String     ctcName;
    /**
     * 联系人职务
     */
    @ApiModelProperty(value = "联系人职务")
    private String     ctcDuty;
    /**
     * 地址
     */
    @ApiModelProperty(value = "地址")
    private String     address;
    /**
     * 付款方式
     */
    @ApiModelProperty(value = "付款方式")
    private String     payWay;
    /**
     * 要更的付款方式
     */
    @ApiModelProperty(value = "要更的付款方式")
    private String     payWayChange;
    /**
     * 付款方式
     */
    @ApiModelProperty(value = "付款方式")
    private String     payWayStatus;
    /**
     * 企业名称
     */
    @ApiModelProperty(value = "企业名称")
    private String     comName;
    /**
     * 采购员工号
     */
    @ApiModelProperty(value = "采购员工号")
    private String     purchCode;
    /**
     * 采购员工姓名
     */
    @ApiModelProperty(value = "采购员工姓名")
    private String     purchName;
    /**
     * 采购员邮箱
     */
    @ApiModelProperty(value = "采购员邮箱")
    private String     purchEmail;

    @ApiModelProperty(value = "跟单员工号[资源]")
    private String     placeCode;

    @ApiModelProperty(value = "跟单员姓名")
    private String     placeName;

    @ApiModelProperty(value = "跟单员邮箱")
    private String     placeEmail;

    /**
     * 采购类型
     */
    @ApiModelProperty(value = "采购类型")
    private String     purchType;
    /**
     * 检验类型
     */
    @ApiModelProperty(value = "检验类型")
    private String     checkType;
    /**
     * 抽检比例
     */
    @ApiModelProperty(value = "抽检比例")
    private BigDecimal checkRate;
    /**
     * 开户行账号
     */
    @ApiModelProperty(value = "开户行账号")
    private String     bankAccountCode;
    /**
     * 开户行名称
     */
    @ApiModelProperty(value = "开户行名称")
    private String     bankName;

    @ApiModelProperty(value = "账期")
    private Integer payDate;

    @ApiModelProperty(value = "预付款比例%")
    private Integer payRate;

    @ApiModelProperty(value = "更新次数")
    private Integer updateCnt;

    @ApiModelProperty(value = "采购合约")
    private String purchContract;

//    @ApiModelProperty(value = "中标调价次数")
//    private Integer readjustCount;
//
//    @ApiModelProperty(value = "弃标次数")
//    private Integer    giveupCount;

//    @ApiModelProperty(value = "质量合格率")
//    private BigDecimal passRate;

//    @ApiModelProperty(value = "交货率")
//    private BigDecimal arvRate;

    @ApiModelProperty(value = "是否同意采购合同")
    private Integer    isAgreePurchContract;
//    @ApiModelProperty(value = "供应风险等级")
//    private Integer    riskLevel;
//    @ApiModelProperty(value = "连续出现高供应风险的次数")
//    private Integer    riskHightCount;

//    @ApiModelProperty(value = "资质分")
//    private BigDecimal techScoreA;
//
//    @ApiModelProperty(value = "技术分_b")
//    private BigDecimal techScoreB;
//
//    @ApiModelProperty(value = "技术分_c")
//    private BigDecimal techScoreC;
//
//    @ApiModelProperty(value = "商务分")
//    private BigDecimal bizScore;
//
//    @ApiModelProperty(value = "资质过期的数量")
//    private Integer    qlfExceedCount;
//
//    @ApiModelProperty(value = "资质需要审核的数量")
//    private Integer    qlfCheckCount;
//
//    @ApiModelProperty(value = "信用分数合计")
//    private BigDecimal creditTotal;


    @ApiModelProperty(value = "模板类型 ")
    private String     templateType;

    @ApiModelProperty(value = "径度")
    private BigDecimal longitude;

    @ApiModelProperty(value = "纬度")
    private BigDecimal latitude;

    @ApiModelProperty(value = "法人 ")
    private String legalPresent;

    @JsonFormat(pattern = "yyyy-MM-dd")
    @ApiModelProperty(value = "成立日期 ")
    private Date foundDate;

    @ApiModelProperty(value = "企业类型 ")
    private String enterpriseType;

    @ApiModelProperty(value = "币种 ")
    private String currencyType;

    @ApiModelProperty(value = "开票抬头 ")
    private String invoiceTitle;

    private String taxRateCode;

    private String supCodes;

    private String supGrade;

    private String supType;

    private String supBrief;

}

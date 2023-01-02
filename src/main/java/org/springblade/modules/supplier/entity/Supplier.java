package org.springblade.modules.supplier.entity;

import com.baomidou.mybatisplus.annotation.TableName;
import com.fasterxml.jackson.annotation.JsonFormat;
import org.springblade.core.mp.base.BaseEntity;
import lombok.Data;
import lombok.EqualsAndHashCode;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;

import java.math.BigDecimal;
import java.util.Date;
import java.time.LocalDateTime;


/**
 * 供应商 实体类
 *
 * @author Will
 */
@Data
@TableName("atw_supplier")
@EqualsAndHashCode(callSuper = true)
@ApiModel(value = "Supplier对象", description = "供应商")
public class Supplier extends BaseEntity {

    private static final long serialVersionUID = 1L;

    /**
     * 供应商登录用户id
     */
    @ApiModelProperty(value = "供应商登录用户id")
    private Long supId;
    /**
     * 供应商编号
     */
    @ApiModelProperty(value = "供应商编号")
    private String code;
    /**
     * 供应商名称
     */
    @ApiModelProperty(value = "供应商名称")
    private String name;
    /**
     * 主分类编码
     */
    @ApiModelProperty(value = "主分类编码")
    private String typeCode;
    /**
     * 主分类名称
     */
    @ApiModelProperty(value = "主分类名称")
    private String typeName;
    /**
     * 国税号
     */
    @ApiModelProperty(value = "国税号")
    private String nationalTaxCode;
    /**
     * 地税号
     */
    @ApiModelProperty(value = "地税号")
    private String stateTaxCode;
    /**
     * 税率
     */
    @ApiModelProperty(value = "税率")
    private BigDecimal taxRate;
    /**
     * 手机号码
     */
    @ApiModelProperty(value = "手机号码")
    private String mobile;
    /**
     * 电话号码
     */
    @ApiModelProperty(value = "电话号码")
    private String phone;
    /**
     * email
     */
    @ApiModelProperty(value = "email")
    private String email;
    /**
     * 传真
     */
    @ApiModelProperty(value = "传真")
    private String fax;
    /**
     * 联系人
     */
    @ApiModelProperty(value = "联系人")
    private String ctcName;
    /**
     * 地址
     */
    @ApiModelProperty(value = "地址")
    private String address;
    /**
     * 付款方式
     */
    @ApiModelProperty(value = "付款方式")
    private String payWay;
    /**
     * 要更的付款方式
     */
    @ApiModelProperty(value = "要更的付款方式")
    private String payWayChange;
    /**
     * 付款方式
     */
    @ApiModelProperty(value = "付款方式")
    private String payWayStatus;
    /**
     * 企业名称
     */
    @ApiModelProperty(value = "企业名称")
    private String comName;
    /**
     * 采购员工号
     */
    @ApiModelProperty(value = "采购员工号")
    private String purchCode;
    /**
     * 采购员工姓名
     */
    @ApiModelProperty(value = "采购员工姓名")
    private String purchName;
    /**
     * 采购员邮箱
     */
    @ApiModelProperty(value = "采购员邮箱")
    private String purchEmail;
    /**
     * 采购类型
     */
    @ApiModelProperty(value = "采购类型")
    private String purchType;
    /**
     * 检验类型
     */
    @ApiModelProperty(value = "检验类型")
    private String checkType;
    /**
     * 抽检比例
     */
    @ApiModelProperty(value = "抽检比例")
    private BigDecimal checkRate;
    /**
     * 开户行账号
     */
    @ApiModelProperty(value = "开户行账号")
    private String bankAccountCode;
    /**
     * 开户行名称
     */
    @ApiModelProperty(value = "开户行名称")
    private String bankName;

    @ApiModelProperty(value = "账期")
    private Integer payDate;

    @ApiModelProperty(value = "预付款比例%")
    private Integer payRate;

    @ApiModelProperty(value = "更新次数")
    private Integer updateCnt;

    @ApiModelProperty(value = "采购合约")
    private String purchContract;

    @ApiModelProperty(value = "中标调价次数")
    private Integer readjustCount;

    @ApiModelProperty(value = "弃标次数")
    private Integer giveupCount;
    /**
     * 质量合格率
     */
    @ApiModelProperty(value = "质量合格率")
    private BigDecimal passRate;
    /**
     * 交货率
     */
    @ApiModelProperty(value = "交货率")
    private BigDecimal arvRate;
    /**
     * 是否同意采购合同
     */
    @ApiModelProperty(value = "是否同意采购合同")
    private Integer isAgreePurchContract;
    /**
     * 供应风险等级
     */
    @ApiModelProperty(value = "供应风险等级")
    private Integer riskLevel;
    /**
     * 连续出现高供应风险的次数
     */
    @ApiModelProperty(value = "连续出现高供应风险的次数")
    private Integer riskHightCount;
    /**
     * 资质分
     */
    @ApiModelProperty(value = "资质分")
    private BigDecimal techScoreA;
    /**
     * 技术分_b
     */
    @ApiModelProperty(value = "技术分_b")
    private BigDecimal techScoreB;
    /**
     * 技术分_c
     */
    @ApiModelProperty(value = "技术分_c")
    private BigDecimal techScoreC;
    /**
     * 商务分
     */
    @ApiModelProperty(value = "商务分")
    private BigDecimal bizScore;
    /**
     * 资质过期的数量
     */
    @ApiModelProperty(value = "资质过期的数量")
    private Integer qlfExceedCount;
    /**
     * 资质需要审核的数量
     */
    @ApiModelProperty(value = "资质需要审核的数量")
    private Integer qlfCheckCount;
    /**
     * 信用分数合计
     */
    @ApiModelProperty(value = "信用分数合计")
    private BigDecimal creditTotal;
    /**
     * 模板类型
     */
    @ApiModelProperty(value = "模板类型 ")
    private String templateType;

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

    @ApiModelProperty(value = "联系人职务 ")
    private String ctcDuty;

    @ApiModelProperty(value = "是否为主联系人 ")
    private String primaryContact;

    @ApiModelProperty(value = "采购员工号")
    private String placeCode;

    @ApiModelProperty(value = "采购员工姓名")
    private String placeName;

    @ApiModelProperty(value = "采购员邮箱")
    private String placeEmail;

    @ApiModelProperty(value = "供应商等级")
    private String supGrade;

    @ApiModelProperty(value = "供应商分类")
    private String supType;

    @ApiModelProperty(value = "供应商简称")
    private String supBrief;
}

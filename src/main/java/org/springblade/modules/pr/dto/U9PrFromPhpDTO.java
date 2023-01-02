package org.springblade.modules.pr.dto;


import com.fasterxml.jackson.annotation.JsonProperty;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import java.math.BigInteger;
import java.util.Date;

/**
 * 请购单 模型DTO
 *
 * @author Will
 */

@Data
public class U9PrFromPhpDTO  {

    private static final long serialVersionUID = 1L;

    @JsonProperty("ID")
    private BigInteger ID;
    @JsonProperty("PRNo")
    private String PRNO;
    @JsonProperty("PRDate")
    private int PRDate;
    @JsonProperty("PRLineNo")
    private int PRLineNo;
    @JsonProperty("ItemCode")
    private String ItemCode;
    @JsonProperty("ItemName")
    private String ItemName;
    @JsonProperty("ItemDesc")
    private String ItemDesc;
    @JsonProperty("ProNo")
    private String ProNo;
    @JsonProperty("PriceUOMCode")
    private String PriceUOMCode;
    @JsonProperty("PriceUOM")
    private String PriceUOM;
    @JsonProperty("PriceNum")
    private int PriceNum;
    @JsonProperty("TCUOMCode")
    private String TCUOMCode;
    @JsonProperty("TCUOM")
    private String TCUOM;
    @JsonProperty("TCNum")
    private int TCNum;
    @JsonProperty("RequireDate")
    private Long RequireDate;
    @JsonProperty("RequireDateBz")
    private int RequireDateBz;
    @JsonProperty("Status")
    private int Status;
    private String modifiedon;
    @JsonProperty("Code")
    private String Code;
    @JsonProperty("IsSpilt")
    private String IsSpilt;
    @JsonProperty("bizType")
    private String bizType;
    private String is_havesup;
    @JsonProperty("EndUser")
    private String EndUser;
    @JsonProperty("qoNo")
    private String qoNo;
    @JsonProperty("moNo")
    private String moNo;
    @JsonProperty("ProjectOccupancyNum")
    private int ProjectOccupancyNum;
    @JsonProperty("AvailableQuantity")
    private int AvailableQuantity;
    @JsonProperty("RequisitionRemark")
    private String RequisitionRemark;
    private String last_sup_name;
    private String aps_xmh;
    private int trigger_sync_info_id;
    private String wg_biz;
    @JsonProperty("IsAps")
    private String IsAps;
    @JsonProperty("CreateUser")
    private String CreateUser;
    @JsonProperty("u9Data")
    private String u9Data;//接口数据

    private Long bzReqDate;//标准交期
    private String problemDesc;//异常问题描述
    private String problemDesc2;//异常问题描述
    private Date plan_date;//计划交期
    private String handle_dept;//处理部门
    private String duty_dept;//责任部门
    private String problem_type;//问题分类
    private String abc_type;//问题分类
    private Date prspDate;//问题分类





}

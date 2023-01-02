package org.springblade.modules.pr.entity;

import cn.afterturn.easypoi.excel.annotation.Excel;
import com.baomidou.mybatisplus.annotation.TableName;
import com.fasterxml.jackson.annotation.JsonFormat;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;
import lombok.EqualsAndHashCode;
import org.springblade.core.mp.base.BaseEntity;

import java.math.BigDecimal;
import java.util.Date;


/**
 * 请购单 实体类
 *
 * @author Will
 */
@Data
@TableName("atw_u9_pr_no_project")
@EqualsAndHashCode(callSuper = true)
@ApiModel(value = "U9Pr对象", description = "请购单")
public class U9PrEntityNoPro extends BaseEntity {

	private static final long serialVersionUID = 1L;

	@ApiModelProperty(value = "请购单号")
	private String prCode;

	@ApiModelProperty(value = "请购单行号")
	private Integer prLn;

	@ApiModelProperty(value = "物料编号")
	private String itemCode;

	@ApiModelProperty(value = "料品")
	private String itemName;

	@ApiModelProperty(value = "物料描述")
	private String itemDesc;

	@ApiModelProperty(value = "项目号")
	private String proNo;

	@ApiModelProperty(value = "交易数量")
	private BigDecimal tcNum;

	@ApiModelProperty(value = "交易单位")
	private String     tcUom;

	@ApiModelProperty(value = "计价数量")
	private BigDecimal priceNum;

	@ApiModelProperty(value = "计价单位")
	private String     priceUom;

	@ApiModelProperty(value = "需求日期")
	private Long reqDate;

	@ApiModelProperty(value = "请购日期")
	private Long prDate;

	@ApiModelProperty(value = "u9状态")
	private String u9Status;

	@ApiModelProperty(value = "业务类型")
	private Integer bizType;

	@ApiModelProperty(value = "询价方式  ")
	private String inquiryWay;

	@ApiModelProperty(value = "是否指定供应商")
	private Integer isAppointSup;

	@ApiModelProperty(value = "指定供应商的编码")
	private String appointSupCode;

	@ApiModelProperty(value = "审批备注")
	private String checkRemark;

	@ApiModelProperty(value = "报价截止日期")
	private Long quoteEndtime;

	@ApiModelProperty(value = "是否强行询价")
	private Integer isForceInquiry;

	@ApiModelProperty(value = "单据修改时间")
	private Integer docModifyOn;

	@ApiModelProperty(value = "行修改时间")
	private Integer lineModifyOn;

	@ApiModelProperty(value = "交易单位编码")
	private String tcUomCode;

	@ApiModelProperty(value = "计价单位编码")
	private String priceUomCode;

	@ApiModelProperty(value = "最终用户")
	private String endUser;

	@ApiModelProperty(value = "最终用户修改次数")
	private Integer endUserUpdateTimes;

    @ApiModelProperty(value = "是否有供应商 0=没有 1=有")
    private String isHavesup;

	@ApiModelProperty(value = "")
	private Integer isHavesupUpdate;

	@ApiModelProperty(value = "isSpilt ")
	private Integer isSpilt;

	@ApiModelProperty(value = "流标类型")
	private String flowType;

	@Excel(name = "采购员工号")
	@ApiModelProperty(value = "采购员工号")
	private String     purchCode;

	@Excel(name = "采购员名称")
	@ApiModelProperty(value = "采购员名称")
	private String     purchName;

	@ApiModelProperty(value = "生产单号")
	private String     moNo;

	@ApiModelProperty(value = "报价编号")
	private String     qoNo;

	@ApiModelProperty(value = "下单员编号")
	private String     placeCode;

	@ApiModelProperty(value = "下单员名称")
	private String     placeName;

	@ApiModelProperty(value = "附件")
	private String     attachment; // 2020.05.11 新加 存放流标录入的附件和报名单

    @JsonFormat(pattern = "yyyy-MM-dd")
    @ApiModelProperty(value = "SRM请购日期")
    private Date createTime;

    @ApiModelProperty(value = "下单日期")
    private Integer orderTime;

    @ApiModelProperty(value = "可用量")
    private BigDecimal availableQuantity;

    @ApiModelProperty(value = "项目占用量")
    private BigDecimal projectOccupancyNum;

    @ApiModelProperty(value = "请购单备注")
    private String requisitionRemark;

    @ApiModelProperty(value = "上次采购供应商")
    private String lastSupName;

    @ApiModelProperty(value = "采购类型")
    private String purchaseType;

    @ApiModelProperty(value = "材料费")
    private BigDecimal materialCost;

    @ApiModelProperty(value = "加工费")
    private BigDecimal laborCost;

    @ApiModelProperty(value = "是否需要审批：0：需要,1: 不需要")
    private String isNeedCheck;

    @ApiModelProperty(value = "标准需求日期")
    private Long bzReqDate;

    @ApiModelProperty(value = "APS项目号")
    private String apsProNo;

    @ApiModelProperty(value = "是否紧急")
    private String isUrgent;

	@ApiModelProperty(value = "接口数据")
	private String u9Data;
}

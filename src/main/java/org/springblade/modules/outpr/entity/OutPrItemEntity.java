package org.springblade.modules.outpr.entity;

import cn.afterturn.easypoi.excel.annotation.Excel;
import com.baomidou.mybatisplus.annotation.TableName;
import com.fasterxml.jackson.databind.annotation.JsonSerialize;
import com.fasterxml.jackson.databind.ser.std.ToStringSerializer;
import org.springblade.core.mp.base.BaseEntity;
import lombok.Data;
import lombok.EqualsAndHashCode;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;

import java.math.BigDecimal;

/**
 *  实体类
 *
 * @author Will
 */
@Data
@TableName("atw_out_pr_item")
@EqualsAndHashCode(callSuper = true)
@ApiModel(value = "OutPrItem对象", description = "")
public class OutPrItemEntity extends BaseEntity {

	private static final long serialVersionUID = 1L;

	/**
	 * 请购单id(物供)
	 */
	@JsonSerialize(using = ToStringSerializer.class)
	@ApiModelProperty(value = "请购单id(物供)")
	private Long prId;
	/**
	 * 请购单号
	 */
	@ApiModelProperty(value = "请购单号")
	private String prCode;

    /**
     * 请购行号
     */
    @ApiModelProperty(value = "请购单号")
    private Integer prLn;

    /**
     * 附件
     */
    @ApiModelProperty(value = "请购单号")
    private String attachment;

	@ApiModelProperty(value = "请购日期")
	private Long prDate;
	/**
	 * 采购类型
	 */
	@ApiModelProperty(value = "采购类型")
	private String purchaseType;
	/**
	 * 物料编号
	 */
	@ApiModelProperty(value = "物料编号")
	private String itemCode;
	/**
	 * 物料名称
	 */
	@ApiModelProperty(value = "物料名称")
	private String itemName;
	/**
	 * 计价数量
	 */
	@ApiModelProperty(value = "计价数量")
	private BigDecimal priceNum;
	/**
	 * 计价单位
	 */
	@ApiModelProperty(value = "计价单位")
	private String priceUom;
	/**
	 * 需求日期
	 */
	@ApiModelProperty(value = "需求日期")
	private Long    reqDate;

	/**
	 * 安特威内部估价
	 */
	@ApiModelProperty(value = "安特威内部估价")
	private BigDecimal atwPrice;
	/**
	 * 材料费
	 */
	@ApiModelProperty(value = "材料费")
	private BigDecimal materialCost;
	/**
	 * 询价时间
	 */
	@ApiModelProperty(value = "询价时间")
	private Long enquiryTime;
	/**
	 * 接单时间
	 */
	@ApiModelProperty(value = "接单时间")
	private Long rcvTime;
	/**
	 * 流标时间
	 */
	@ApiModelProperty(value = "流标时间")
	private Long flowTime;
	/**
	 * 审核通过时间
	 */
	@ApiModelProperty(value = "审核通过时间")
	private Long approveTime;
	/**
	 * 下单时间，自然关闭时间
	 */
	@ApiModelProperty(value = "下单时间，自然关闭时间")
	private Long orderTime;
	/**
	 * 取消时间，短缺关闭时间
	 */
	@ApiModelProperty(value = "取消时间，短缺关闭时间")
	private Long closeTime;
	/**
	 * 流标原因
	 */
	@ApiModelProperty(value = "流标原因")
	private String flowCause;
	/**
	 * 是否需要刷新
	 */
	@ApiModelProperty(value = "是否需要刷新")
	private Integer isNeedUpdate;
	/**
	 * 询价方式
	 */
	@ApiModelProperty(value = "询价方式")
	private String inquiryWay;

    @ApiModelProperty(value = "可用量")
    private BigDecimal availableQuantity;

    @ApiModelProperty(value = "项目占用量")
    private BigDecimal projectOccupancyNum;

    @ApiModelProperty(value = "请购单备注")
    private String requisitionRemark;

    @ApiModelProperty(value = "采购员工号")
    private String     purchCode;

    @ApiModelProperty(value = "采购员名称")
    private String     purchName;

    @ApiModelProperty(value = "生产订单号")
    private String     moNo;

    @ApiModelProperty(value = "上次采购供应商")
    private String     lastSupName;

    @ApiModelProperty(value = "流标类型")
    private String flowType;

    @ApiModelProperty(value = "审批备注")
    private String checkRemark;

    @ApiModelProperty(value = "是否需要审批：0：需要,1: 不需要")
    private String isNeedCheck;
}

package org.springblade.modules.po.entity;

import com.baomidou.mybatisplus.annotation.TableName;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;
import lombok.EqualsAndHashCode;
import org.springblade.core.mp.base.BaseEntity;

import java.math.BigDecimal;


/**
 *  实体类
 *
 * @author Will
 */
@Data
@TableName("atw_po_remind")
@EqualsAndHashCode(callSuper = true)
@ApiModel(value = "PoRemind对象", description = "")
public class PoRemindEntity extends BaseEntity {

	private static final long serialVersionUID = 1L;

	/**
	 * 项目号
	 */
	@ApiModelProperty(value = "项目号")
	private String proNo;
	/**
	 * 采购订单号
	 */
	@ApiModelProperty(value = "采购订单号")
	private String poCode;
	/**
	 * 采购订单行号
	 */
	@ApiModelProperty(value = "采购订单行号")
	private Integer poLn;
	/**
	 * 偏移类型
	 */
	@ApiModelProperty(value = "偏移类型")
	private String type;
	/**
	 * 料品号
	 */
	@ApiModelProperty(value = "料品号")
	private String itemCode;
	/**
	 * 料品描述
	 */
	@ApiModelProperty(value = "料品描述")
	private String itemName;
	/**
	 * 供应商编号
	 */
	@ApiModelProperty(value = "供应商编号")
	private String supCode;
	/**
	 * 供应商名称
	 */
	@ApiModelProperty(value = "供应商名称")
	private String supName;
	/**
	 * 联系人
	 */
	@ApiModelProperty(value = "联系人")
	private String supContact;
	/**
	 * 电话号码
	 */
	@ApiModelProperty(value = "电话号码")
	private String supMobile;
	/**
	 * 计量数量
	 */
	@ApiModelProperty(value = "计量数量")
	private Integer priceNum;
	/**
	 * 计量单位
	 */
	@ApiModelProperty(value = "计量单位")
	private String priceUom;
	/**
	 * 交易数量
	 */
	@ApiModelProperty(value = "交易数量")
	private Integer tcNum;
	/**
	 * 交易单位
	 */
	@ApiModelProperty(value = "交易单位")
	private String tcUom;
	/**
	 * 项目数量
	 */
	@ApiModelProperty(value = "项目数量")
	private Integer proNum;
	/**
	 * 偏移量
	 */
	@ApiModelProperty(value = "偏移量")
	private Integer offsetDays;
	/**
	 * 要求交期
	 */
	@ApiModelProperty(value = "要求交期")
	private Long reqDate;
	/**
	 * 供应商确认交期
	 */
	@ApiModelProperty(value = "供应商确认交期")
	private Long supConfirmDate;
	/**
	 * 供应商修改后的交期
	 */
	@ApiModelProperty(value = "供应商修改后的交期")
	private Long supUpdateDate;
	/**
	 * 运算交期
	 */
	@ApiModelProperty(value = "运算交期")
	private Long operationDate;
	/**
	 * 采购单价
	 */
	@ApiModelProperty(value = "采购单价")
	private BigDecimal price;
	/**
	 * 金额
	 */
	@ApiModelProperty(value = "金额")
	private BigDecimal amount;
	/**
	 * 采购员编号
	 */
	@ApiModelProperty(value = "采购员编号")
	private String purchCode;
	/**
	 * 采购员工姓名
	 */
	@ApiModelProperty(value = "采购员工姓名")
	private String purchName;
	/**
	 * 跟单员编号
	 */
	@ApiModelProperty(value = "跟单员编号")
	private String traceCode;
	/**
	 * 跟单员
	 */
	@ApiModelProperty(value = "跟单员")
	private String traceName;
	/**
	 * 到货数量
	 */
	@ApiModelProperty(value = "到货数量")
	private BigDecimal rcvGoodsNum;
	/**
	 * 实收数量
	 */
	@ApiModelProperty(value = "实收数量")
	private BigDecimal arvGoodsNum;
	/**
	 * 未到货数量
	 */
	@ApiModelProperty(value = "未到货数量")
	private BigDecimal proGoodsNum;
	/**
	 * 退货数量
	 */
	@ApiModelProperty(value = "退货数量")
	private BigDecimal returnGoodsNum;
	/**
	 * 退货需要补货数量
	 */
	@ApiModelProperty(value = "退货需要补货数量")
	private BigDecimal fillGoodsNum;
	/**
	 * 备注
	 */
	@ApiModelProperty(value = "备注")
	private String remark;

}

package org.springblade.modules.report.entity;

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
 *  实体类
 *
 * @author Will
 */
@Data
@TableName("jit_management")
@EqualsAndHashCode(callSuper = true)
@ApiModel(value = "JitManagementEntity", description = "")
public class JitManagementEntity extends BaseEntity {

	private static final long serialVersionUID = 1L;

	/**
	 * 供应商编码
	 */
    @Excel(name = "供应商编码")
	@ApiModelProperty(value = "供应商编码")
	private String supCode;
	/**
	 * 供应商名称
	 */
    @Excel(name = "供应商名称")
	@ApiModelProperty(value = "供应商名称")
	private String supName;
	/**
	 * 物料编号
	 */
    @Excel(name = "物料编号")
	@ApiModelProperty(value = "物料编号")
	private String itemCode;

    /**
     * 物料名称
     */
    @Excel(name = "物料名称")
    @ApiModelProperty(value = "物料名称")
    private String itemName;
	/**
	 * 要求备库数量
	 */
    @Excel(name = "要求备库数量")
	@ApiModelProperty(value = "要求备库数量")
	private int reqPerpareNum;
	/**
	 * 实际库存数量
	 */
    @Excel(name = "实际库存数量")
	@ApiModelProperty(value = "实际库存数量")
	private int actualRcvNum;
	/**
	 * 库存数量警戒线
	 */
    @Excel(name = "库存数量警戒线")
	@ApiModelProperty(value = "库存数量警戒线")
	private int rcvWarnNum;
	/**
	 * 要求备库完成时间
	 */
    @JsonFormat(pattern = "yyyy-MM-dd")
    @Excel(name = "要求备库完成时间",format = "yyyy-MM-dd")
	@ApiModelProperty(value = "要求备库完成时间")
	private Date reqPerpareDate;
	/**
	 * 预估金额
	 */
    @Excel(name = "预估金额")
	@ApiModelProperty(value = "预估金额")
	private BigDecimal expectMoney;
	/**
	 * JIT签订时间
	 */
    @JsonFormat(pattern = "yyyy-MM-dd")
    @Excel(name = "JIT签订时间",format = "yyyy-MM-dd")
	@ApiModelProperty(value = "JIT签订时间")
	private Date jitDate;

    /**
     * 备注
     */
    @Excel(name = "备注")
	@ApiModelProperty(value = "备注")
	private String remark;



}

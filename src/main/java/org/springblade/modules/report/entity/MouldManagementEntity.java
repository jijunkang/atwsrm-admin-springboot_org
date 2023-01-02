package org.springblade.modules.report.entity;

import cn.afterturn.easypoi.excel.annotation.Excel;
import com.baomidou.mybatisplus.annotation.TableName;
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
@TableName("mould_management")
@EqualsAndHashCode(callSuper = true)
@ApiModel(value = "MouldManagement对象", description = "")
public class MouldManagementEntity extends BaseEntity {

	private static final long serialVersionUID = 1L;

	/**
	 * 模具状态
	 */
    @Excel(name = "模具状态")
	@ApiModelProperty(value = "模具状态")
	private String mouldStatus;
	/**
	 * 美标国标
	 */
    @Excel(name = "美标国标")
	@ApiModelProperty(value = "美标国标")
	private String standard;
	/**
	 * 密封形式
	 */
    @Excel(name = "密封形式")
	@ApiModelProperty(value = "密封形式")
	private String sealType;

    /**
     * 浮动固定
     */
    @Excel(name = "浮动固定")
    @ApiModelProperty(value = "浮动固定")
    private String floatOrFixed;
	/**
	 * 阀种
	 */
    @Excel(name = "阀种")
	@ApiModelProperty(value = "阀种")
	private String valveType;
	/**
	 * 模具可浇注的材质
	 */
    @Excel(name = "模具可浇注的材质")
	@ApiModelProperty(value = "模具可浇注的材质")
	private String productMaterial;
	/**
	 * 口径尺寸
	 */
    @Excel(name = "口径尺寸")
	@ApiModelProperty(value = "口径尺寸")
	private String caliberSize;
	/**
	 * 压力磅级
	 */
    @Excel(name = "压力磅级")
	@ApiModelProperty(value = "压力磅级")
	private String pressureSize;
	/**
	 * 模具规格
	 */
    @Excel(name = "模具规格")
	@ApiModelProperty(value = "模具规格")
	private String mouldStandards;
	/**
	 * 固定资产编号（采购）
	 */
    @Excel(name = "固定资产编号（采购）")
	@ApiModelProperty(value = "固定资产编号（采购）")
	private String     fixedAssetsCode;
	/**
	 * 铸件供应商
	 */
    @Excel(name = "铸件供应商")
	@ApiModelProperty(value = "铸件供应商")
	private String       zjSupplier;
	/**
	 * 模具供应商
	 */
    @Excel(name = "模具供应商")
	@ApiModelProperty(value = "模具供应商")
	private String       mouldSupplier;
	/**
	 * 系列化
	 */
    @Excel(name = "系列化")
	@ApiModelProperty(value = "系列化")
	private String     isSeries;
	/**
	 * 模具材质
	 */
    @Excel(name = "模具材质")
	@ApiModelProperty(value = "模具材质")
	private String mouldMaterial;
    /**
     * 备注
     */
    @Excel(name = "备注")
	@ApiModelProperty(value = "备注")
	private String remark;



}

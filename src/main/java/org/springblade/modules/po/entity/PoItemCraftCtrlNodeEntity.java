package org.springblade.modules.po.entity;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableLogic;
import com.baomidou.mybatisplus.annotation.TableName;
import com.fasterxml.jackson.databind.annotation.JsonSerialize;
import com.fasterxml.jackson.databind.ser.std.ToStringSerializer;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;
import org.springblade.core.mp.base.BaseEntity;

import java.io.Serializable;

/**
 * @author libin
 *
 * @date 11:10 2020/7/22
 **/
@Data
@TableName("atw_po_item_craftctrl_node")
@ApiModel(value = "PoItemCraftCtrlNodeEntity对象", description = "PoItemCraftCtrlNodeEntity对象")
public class PoItemCraftCtrlNodeEntity extends BaseEntity implements Serializable {

	private static final long serialVersionUID = 1L;

	/**
	 * 主键
	 */
	@JsonSerialize(using = ToStringSerializer.class)
	@ApiModelProperty(value = "主键")
	@TableId(value = "id", type = IdType.ID_WORKER)
	private Long id;

	/**
	 * 租户ID
	 */
	@ApiModelProperty(value = "租户ID")
	private String tenantId;

	/**
	 * poItemId
	 */
	@JsonSerialize(using = ToStringSerializer.class)
	@ApiModelProperty(value = "poItemId")
	private Long poItemId;

    /**
     * 卡控类型父ID
     */
    @JsonSerialize(using = ToStringSerializer.class)
    @ApiModelProperty(value = "卡控类型父ID")
    private Long ccnodeParentId;

    /**
     * 卡控类型子ID
     */
    @JsonSerialize(using = ToStringSerializer.class)
    @ApiModelProperty(value = "卡控类型子ID")
    private Long ccnodeChildId;

    @ApiModelProperty(value = "计划完成日期")
    private Long planConfirmDate;

    @ApiModelProperty(value = "是否完工")
    private Integer isComplete;

	@ApiModelProperty(value = "供应商备注")
	private String supRemark;

    @ApiModelProperty(value = "采购部备注")
    private String purchRemark;


	/**
	 * 是否已删除
	 */
	@TableLogic
	@ApiModelProperty(value = "是否已删除")
	private Integer isDeleted;


}

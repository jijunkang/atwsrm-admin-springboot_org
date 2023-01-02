package org.springblade.modules.outpr.entity;

import com.baomidou.mybatisplus.annotation.TableName;
import com.fasterxml.jackson.databind.annotation.JsonSerialize;
import com.fasterxml.jackson.databind.ser.std.ToStringSerializer;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;
import lombok.EqualsAndHashCode;
import org.springblade.core.mp.base.BaseEntity;

import java.math.BigDecimal;

/**
 * 实体类
 * @author Will
 */
@Data
@TableName("atw_out_pr_item_artifact")
@EqualsAndHashCode(callSuper = true)
@ApiModel(value = "", description = "")
public
class OutPrItemArtifactEntity extends BaseEntity{

    private static final long serialVersionUID = 1L;

    /**
     * 请购单id(物供)
     */
    @JsonSerialize(using = ToStringSerializer.class)
    @ApiModelProperty(value = "请购单id(物供)")
    private Long   prId;
    /**
     * 请购单号
     */
    @ApiModelProperty(value = "请购单号")
    private String prCode;

    @ApiModelProperty(value = "请购日期")
    private Long       prDate;
    /**
     * 采购类型
     */
    @ApiModelProperty(value = "采购类型")
    private String     purchaseType;
    /**
     * 物料编号
     */
    @ApiModelProperty(value = "物料编号")
    private String     itemCode;
    /**
     * 物料名称
     */
    @ApiModelProperty(value = "物料名称")
    private String     itemName;
    /**
     * 计价数量
     */
    @ApiModelProperty(value = "计价数量")
    private BigDecimal priceNum;
    /**
     * 计价单位
     */
    @ApiModelProperty(value = "计价单位")
    private String     priceUom;
    /**
     * 需求日期
     */
    @ApiModelProperty(value = "需求日期")
    private Long       reqDate;

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
     * 委外物料id
     */
    @ApiModelProperty(value = "委外物料id")
    private Long    prItemId;
    /**
     * 请购单行号
     */
    @ApiModelProperty(value = "请购单行号")
    private Integer prLn;
    /**
     * 工序代码
     */
    @ApiModelProperty(value = "工序代码")
    private String  processCode;
    /**
     * 工序名称
     */
    @ApiModelProperty(value = "工序名称")
    private String  processName;

    @ApiModelProperty(value = "价格")
    private BigDecimal price;

    @ApiModelProperty(value = "供应商编号")
    private String supCode;

    @ApiModelProperty(value = "供应商名称")
    private String supName;

    @ApiModelProperty(value = "供应商承诺交期")
    private Long supDeliveryTime;

    @ApiModelProperty(value = "附件")
    private String attachment;

    @ApiModelProperty(value = "备注")
    private String remark;
}

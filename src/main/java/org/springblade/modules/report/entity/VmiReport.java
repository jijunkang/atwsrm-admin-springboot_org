package org.springblade.modules.report.entity;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;
import java.io.Serializable;

/**
 * VMI物料消耗表
 * @author Will
 */
@Data
@ApiModel(value = "VMI采购计划表", description = "VMI采购计划表")
public class VmiReport implements Serializable {

    private static final long serialVersionUID = 1L;

    @ApiModelProperty(value = "供应商编码")
    private String supCode;

    @ApiModelProperty(value = "供应商名称")
    private String supName;

    @ApiModelProperty(value = "物料编码")
    private String itemCode;

    @ApiModelProperty(value = "物料描述")
    private String itemName;

    @ApiModelProperty(value = "订单总数量")
    private String poNum;

    @ApiModelProperty(value = "入存总数量")
    private String storeTotalNum;

    @ApiModelProperty(value = "库存数量")
    private String storeNum;

    @ApiModelProperty(value = "已结算数量")
    private String balNum;

    @ApiModelProperty(value = "库存未结算数量")
    private String notBalNum;

    @ApiModelProperty(value = "已用但未结算数量")
    private String usedButNotBalNum;

    @ApiModelProperty(value = "组织代码")
    private String orgCode;

}

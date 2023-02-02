package org.springblade.modules.report.entity;


import com.baomidou.mybatisplus.annotation.TableName;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;
import lombok.EqualsAndHashCode;
import org.springblade.core.mp.base.BaseEntity;


/**
 * 供应商产能分析 实体类供应商产能分析基础数据表 实体类
 */
@Data
@EqualsAndHashCode(callSuper = true)
@TableName("atw_supplier_output_info")
@ApiModel(value = "供应商产能分析", description = "实体类供应商产能分析基础数据表")
public class SupplierOutputInfoQZEntity extends BaseEntity {

    private static final long serialVersionUID = 1L;

    @ApiModelProperty(value = "供应商")
    private String supName;

    @ApiModelProperty(value = "供应商代码")
    private String supCode;

    @ApiModelProperty(value = "物料描述")
    private String itemCode;

    @ApiModelProperty(value = "物料名称")
    private String itemName;

    @ApiModelProperty(value = "生产产能")
    private String produceCapacity;

    @ApiModelProperty(value = "单价")
    private String singlePrice;

    @ApiModelProperty(value = "时间")
    private String date;

    @ApiModelProperty(value = "物料类型")
    private String materialType;

    @ApiModelProperty(value = "寸级")
    private String cj;

    @ApiModelProperty(value = "形式")
    private String type;

    @ApiModelProperty(value = "磅级")
    private String bj;

    @ApiModelProperty(value = "等级")
    private String dj;

    @ApiModelProperty(value = "涂层")
    private String tc;

    @ApiModelProperty(value = "材质")
    private String material;

    @ApiModelProperty(value = "需求数量")
    private String number;

    @ApiModelProperty(value = "工艺")
    private String castingProcess;

    @ApiModelProperty(value = "瓶颈工序")
    private String bottleneckProcesses;

}

package org.springblade.modules.outpr.entity;

import com.baomidou.mybatisplus.annotation.TableName;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;
import lombok.EqualsAndHashCode;
import org.springblade.core.mp.base.BaseEntity;


/**
 *  实体类
 *
 * @author Will
 */
@Data
@TableName("atw_out_pr_report_forms")
@EqualsAndHashCode(callSuper = true)
@ApiModel(value = "OutPrReportForms对象", description = "")
public class OutPrReportFormsEntity extends BaseEntity {

	private static final long serialVersionUID = 1L;

    @ApiModelProperty(value = "原料号")
    private String oldItemCode;

    @ApiModelProperty(value = "原料品描述")
    private String oldItemName;

    @ApiModelProperty(value = "送货数量")
    private Integer deliverNum;

    @ApiModelProperty(value = "送货单单号")
    private String doCode;

    @ApiModelProperty(value = "子项目号")
    private String qoCode;

    @ApiModelProperty(value = "项目交期")
    private Long proDate;

    @ApiModelProperty(value = "mo工单")
    private String moCode;

    @ApiModelProperty(value = "新料号")
    private String newItemCode;

    @ApiModelProperty(value = "新料品描述")
    private String newItemName;

    @ApiModelProperty(value = "数量")
    private Integer recNum;

    @ApiModelProperty(value = "备注：精加工，粗加工")
    private String remark;
}

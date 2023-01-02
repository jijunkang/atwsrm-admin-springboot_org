package org.springblade.modules.report.entity;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import java.io.Serializable;

/**
 * 关键物料日报报表
 * @author Will
 */
@Data
@ApiModel(value = "关键物料日报报表", description = "关键物料日报报表")
public class ItemDailyReport implements Serializable {

    private static final long serialVersionUID = 1L;

    @ApiModelProperty(value = "物料分类")
    private String itemType;

    @ApiModelProperty(value = "影响主计划行数")
    private Integer affectPlanNum;

    @ApiModelProperty(value = "影响子项目个数")
    private Integer affectProNum;

    @ApiModelProperty(value = "影响产出数量（台）")
    private Integer affectTcsNum;

    @ApiModelProperty(value = "条数 1-3 天 （修改审核交期）[条数 1 天 ]")
    private Integer pcsOfAS;

    @ApiModelProperty(value = "条数 4-7 天（修改审核交期）[条数 2 天 ]")
    private Integer pcsOfBS;

    @ApiModelProperty(value = "条数 7 天以上（修改审核交期）[条数 2 天以上 ]")
    private Integer pcsOfCS;

    @ApiModelProperty(value = "台数 1-3 天（修改审核交期）[条数 1 天 ]")
    private Integer tcsOfAS;

    @ApiModelProperty(value = "台数 4-7 天（修改审核交期）[条数 2 天 ]")
    private Integer tcsOfBS;

    @ApiModelProperty(value = "台数 7 天以上（修改审核交期）[条数 2 天以上 ]")
    private Integer tcsOfCS;

    @ApiModelProperty(value = "条数 1-3 天 （需求交期）[条数 1天 ]")
    private Integer pcsOfAX;

    @ApiModelProperty(value = "条数 4-7 天（需求交期）[条数 2天 ]")
    private Integer pcsOfBX;

    @ApiModelProperty(value = "条数 7 天以上（需求交期）[条数 2 天以上 ]")
    private Integer pcsOfCX;

    @ApiModelProperty(value = "台数 1-3 天（需求交期）[条数 1 天 ]")
    private Integer tcsOfAX;

    @ApiModelProperty(value = "台数 4-7 天（需求交期）[条数 2 天 ]")
    private Integer tcsOfBX;

    @ApiModelProperty(value = "台数 7 天以上（需求交期）[条数 2 天以上 ]")
    private Integer tcsOfCX;
}

package org.springblade.modules.report.entity;

import cn.afterturn.easypoi.excel.annotation.Excel;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import java.io.Serializable;

/**
 * 球座报表
 * @author Will
 */
@Data
@ApiModel(value = "球座报表", description = "球座报表")
public class QZReport implements Serializable {

    private static final long serialVersionUID = 1L;

    @Excel(name = "球料号")
    @ApiModelProperty(value = "球料号")
    private String ballCode;

    @Excel(name = "球描述")
    @ApiModelProperty(value = "球描述")
    private String ballName;

    @Excel(name = "座料号")
    @ApiModelProperty(value = "座料号")
    private String seatCode;

    @Excel(name = "座描述")
    @ApiModelProperty(value = "座描述")
    private String seatName;

}

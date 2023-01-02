package org.springblade.modules.pr.entity;

import com.baomidou.mybatisplus.annotation.TableName;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;
import org.apache.poi.hpsf.Decimal;
import org.springblade.core.mp.base.BaseEntity;

import java.io.Serializable;
import java.math.BigDecimal;

/**
 * Author: 昕月
 * Date：2022/5/13 11:35
 * Desc:
 */

@Data
@AllArgsConstructor
@NoArgsConstructor
@EqualsAndHashCode(callSuper = true)
@TableName(value = "atw_maily_material")
@ApiModel(value = "喷涂单价表" ,description = "")
public class ItemInfoSprayingPriceMaily extends BaseEntity implements Serializable {

    @ApiModelProperty(value = "序号id")
    private Long id;
    @ApiModelProperty(value = "供应商名称")
    private String supplierName;
    @ApiModelProperty(value = "供应商编码")
    private String supplierCode;
    @ApiModelProperty(value = "材质")
    private String theMaterial;
    @ApiModelProperty(value = "涂层")
    private String coating;
    @ApiModelProperty(value = "涂层单价")
    private BigDecimal coatingPrice;
}

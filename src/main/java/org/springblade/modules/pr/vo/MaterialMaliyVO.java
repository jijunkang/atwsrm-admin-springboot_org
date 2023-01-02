package org.springblade.modules.pr.vo;

import io.swagger.annotations.ApiModelProperty;
import lombok.*;
import org.springblade.core.mp.base.BaseEntity;

import java.io.Serializable;
import java.math.BigDecimal;
import java.util.Date;

/**
 * Author: 昕月
 * Date：2022/5/12 15:48
 * Desc:
 */
@Data
@AllArgsConstructor
@NoArgsConstructor
@EqualsAndHashCode(callSuper = true)
@ToString
public class MaterialMaliyVO extends BaseEntity implements Serializable {

    private  static  final  long serialVersionUID = 1L;

    @ApiModelProperty(value = "物料编码")
    private  String  itemCode;

    @ApiModelProperty(value = "物料名称")
    private  String itemName;

    @ApiModelProperty(value = "供应商名称")
    private  String supplierName;

    @ApiModelProperty(value = "供应商编码")
    private String supplierCode;

    @ApiModelProperty(value ="外径" )
    private Double externalDiameter;

    @ApiModelProperty(value ="内径" )
    private Double internalDiamete;

    @ApiModelProperty(value ="壁厚" )
    private int wallThickness;

    @ApiModelProperty(value = "长度")
    private Double length;

    @ApiModelProperty(value = "涂层")
    private String coating;

    @ApiModelProperty(value = "材质")
    private String theMaterial;

    @ApiModelProperty(value = "单重")
    private BigDecimal weight;

    @ApiModelProperty(value = "材料单价")
    private BigDecimal theMaterialPrice;

    @ApiModelProperty(value = "材料费")
    private BigDecimal MaterialPrice;

    @ApiModelProperty(value = "涂层单价")
    private BigDecimal coatingPrice;

    @ApiModelProperty(value = "切割费")
    private BigDecimal price;

    @ApiModelProperty(value = "加工费")
    private BigDecimal processingFee;

    @ApiModelProperty(value = "外径范围")
    private String externalDiameterRange;

    @ApiModelProperty(value = "创建人")
    private String createUsers;

    @ApiModelProperty(value = "喷涂内径")
    private BigDecimal sprayInner;

    @ApiModelProperty(value = "喷涂费")
    private BigDecimal sprayPrice;

    @ApiModelProperty(value = "产品单价")
    private BigDecimal unitPrice;

    @ApiModelProperty(value = "修改人")
    private String updateUsers;

    @ApiModelProperty(value = "创建时间")
    private Date createTime;

    @ApiModelProperty(value = "修改时间")
    private Date updateTime;

    private String supCode;
    private String supName;
    private String materialType;
    private String outerSize;
    private String heightSize;
    private String innerSize;


}

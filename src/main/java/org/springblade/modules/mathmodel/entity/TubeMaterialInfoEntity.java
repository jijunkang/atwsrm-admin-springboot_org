package org.springblade.modules.mathmodel.entity;

import cn.afterturn.easypoi.excel.annotation.Excel;
import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.ToString;
import org.springblade.core.mp.base.BaseEntity;

import java.math.BigDecimal;

/**
 * Author: 昕月
 * Date：2022/5/25 18:31
 * Desc:
 */
@Data
@ToString
@EqualsAndHashCode(callSuper = true)
@TableName(value = "atw_item_info_gbl_report")
public class  TubeMaterialInfoEntity extends BaseEntity{


    @TableId(value = "id",type = IdType.AUTO)//指定自增策略
//    @Excel(name = "序号id")
    private  Long id;

    @Excel(name = "请购单号")
    private String prCode;

    @Excel(name = "请购行号")
    private Integer prLn;

    @Excel(name = "供应商名称")
    private  String supplierName;

    @ApiModelProperty(value = "供应商编码")
    private String supplierCode;

    @Excel(name = "物料编号")
    private  String itemCode;

    @Excel(name = "物料描述")
    private  String itemDesc;

    @Excel(name = "数量")
    private  BigDecimal priceNum;

    @Excel(name = "外径余量")
    private Double diameterAllowance;

    @Excel(name = "孔径余量")
    private Double apertureAllowance;

    @Excel(name = "原材料外径")
    private Double externalDiameter;

    @Excel(name = "原材料内径")
    private Double internalDiamete;

    @Excel(name = "原材料长度")
    private Double length;

    @Excel(name = "原材料单重")
    private BigDecimal weight;

    @Excel(name = "材料单价")
    private BigDecimal theMaterialPrice;

    @Excel(name = "材料费")
    private BigDecimal materialPrice;

    @Excel(name = "加工费")
    private BigDecimal processingFee;

    @Excel(name = "切割费")
    private BigDecimal price;

    @Excel(name = "喷涂内径")
    private Double coatingInternalDiameter;

    @Excel(name = "喷涂长度")
    private Double coatingLength;

    @Excel(name = "喷涂面积")
    private BigDecimal coatingArea;

    @Excel(name = "喷涂单价")
    private BigDecimal coatingPrice;

    @Excel(name = "喷涂价格")
    private BigDecimal sprayPrice;

    @Excel(name = "产品单价")
    private BigDecimal unitPrice;

    @Excel(name = "总价")
    private BigDecimal totalPrice;

    @ApiModelProperty(value = "创建人")
    private String createUsers;

    @ApiModelProperty(value = "修改人")
    private String updateUsers;

    @ApiModelProperty(value = "涂层")
    private String coating;

    @ApiModelProperty(value = "材质")
    private String theMaterial;

    @ApiModelProperty(value = "品名")
    private  String name;


}

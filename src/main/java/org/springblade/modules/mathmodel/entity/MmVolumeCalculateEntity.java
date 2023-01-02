package org.springblade.modules.mathmodel.entity;

import com.baomidou.mybatisplus.annotation.TableName;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;
import lombok.EqualsAndHashCode;
import org.springblade.core.mp.base.BaseEntity;

import java.math.BigDecimal;

/**
 * @author libin
 *
 * @date 11:09 2020/9/11
 **/
@Data
@TableName("atw_mm_volume_calculate")
@EqualsAndHashCode(callSuper = true)
@ApiModel(value = "MmVolumeCalculate对象", description = "")
public class MmVolumeCalculateEntity extends BaseEntity {

    private static final long serialVersionUID = 1L;

    /**
     * 主分类
     */
    @ApiModelProperty(value = "主分类")
    private String mainCode;
    /**
     * 子分类
     */
    @ApiModelProperty(value = "子分类")
    private String childCode;
    /**
     * 物料编号
     */
    @ApiModelProperty(value = "物料编号")
    private String itemCode;
    /**
     * 物料描述
     */
    @ApiModelProperty(value = "物料描述")
    private String itemName;
    /**
     * 供应商编号
     */
    @ApiModelProperty(value = "供应商编号")
    private String supCode;
    /**
     * 供应商描述
     */
    @ApiModelProperty(value = "供应商名称")
    private String supName;
    /**
     * 外径1
     */
    @ApiModelProperty(value = "外径1")
    private BigDecimal outD1;
    /**
     * 外径2
     */
    @ApiModelProperty(value = "外径2")
    private BigDecimal outD2;
    /**
     * 高度1
     */
    @ApiModelProperty(value = "高度1")
    private BigDecimal h1;
    /**
     * 高度2
     */
    @ApiModelProperty(value = "高度2")
    private BigDecimal h2;
    /**
     * 外圆高度余量1
     */
    @ApiModelProperty(value = "外圆高度余量1")
    private BigDecimal outMargin1;
    /**
     * 外圆高度余量2
     */
    @ApiModelProperty(value = "外圆高度余量2")
    private BigDecimal outMargin2;
    /**
     * 内控余量
     */
    @ApiModelProperty(value = "内控余量")
    private BigDecimal inMargin;
    /**
     * 内孔
     */
    @ApiModelProperty(value = "内孔")
    private BigDecimal hole;
    /**
     * 密度
     */
    @ApiModelProperty(value = "密度")
    private BigDecimal density;
    /**
     * 公斤单价
     */
    @ApiModelProperty(value = "公斤单价")
    private BigDecimal kgPrice;
    /**
     * 热处理单价
     */
    @ApiModelProperty(value = "热处理单价")
    private BigDecimal hotPrice;
    /**
     * 飞削单价
     */
    @ApiModelProperty(value = "飞削单价")
    private BigDecimal cutPrice;
    /**
     * 体积
     */
    @ApiModelProperty(value = "体积")
    private BigDecimal volume;
    /**
     * 重量
     */
    @ApiModelProperty(value = "重量")
    private BigDecimal weight;
    /**
     * 公斤价
     */
    @ApiModelProperty(value = "公斤价")
    private BigDecimal totalPrice;
    /**
     * 单价
     */
    @ApiModelProperty(value = "单价")
    private BigDecimal price;
    /**
     * 建议采购价
     */
    @ApiModelProperty(value = "建议采购价")
    private BigDecimal advicePrice;
}

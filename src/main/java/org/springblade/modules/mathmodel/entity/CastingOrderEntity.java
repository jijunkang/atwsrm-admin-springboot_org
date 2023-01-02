package org.springblade.modules.mathmodel.entity;

import cn.afterturn.easypoi.excel.annotation.Excel;
import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import io.swagger.annotations.ApiModelProperty;
import lombok.*;
import org.springblade.core.mp.base.BaseEntity;

import java.math.BigDecimal;

/**
 * Author: 昕月
 * Date：2022/6/7 19:54
 * Desc:
 */

@Data
@AllArgsConstructor
@NoArgsConstructor
@EqualsAndHashCode(callSuper = true)
@TableName(value = "atw_auto_order_zj")
public class CastingOrderEntity  extends BaseEntity {


    @TableId(value = "id",type = IdType.AUTO)//指定自增策略
    private  Long id;

    @Excel(name = "请购单号")
    private String prCode;

    @Excel(name = "请购行号")
    private Integer prLn;

    @Excel(name = "供应商名称")
    private String supName;

    @Excel(name = "供应商编码")
    private String supCode;

    @Excel(name = "物料编号")
    private  String itemCode;

    @Excel(name = "物料描述")
    private  String itemDesc;

    @Excel(name = "物料分类")
    private String itemize;

    @Excel(name= "尺寸")
    private String itemSize;

    @Excel(name = "形式")
    private String form;

    @Excel(name = "磅级")
    private String pound;

    @Excel(name = "法兰")
    private String flange;

    @Excel(name = "系列")
    private String series;
    @Excel(name = "材质")
    private String material;

    @Excel(name = "加工费")
    private String charge;

    @Excel(name = "单重")
    private String weight;

    @Excel(name = "单价")
    private BigDecimal quotePrice;

    @Excel(name = "数量")
    private BigDecimal priceNum;

    @Excel(name = "总价")
    private String amount;

    //    @Excel(name = "材质-单重")
    private String materialOfWeight;
    //    @Excel(name = "铸造工艺")
    private String technology;

}

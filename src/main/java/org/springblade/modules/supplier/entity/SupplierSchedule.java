package org.springblade.modules.supplier.entity;

import com.baomidou.mybatisplus.annotation.TableName;
import com.fasterxml.jackson.annotation.JsonFormat;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;
import lombok.EqualsAndHashCode;
import org.springblade.core.mp.base.BaseEntity;

import java.io.Serializable;
import java.math.BigDecimal;
import java.util.Date;


/**
 * 供应商供应计划表
 *
 * @author Will
 */
@Data
@TableName("bi_delivrpt_data")
@ApiModel(value = "供应商供应计划表", description = "供应商供应计划表")
public class SupplierSchedule implements Serializable {

    private static final long serialVersionUID = 1L;

    @ApiModelProperty(value = "类别")
    private String lb;

    @ApiModelProperty(value = "物料号")
    private String itemCode;

    @ApiModelProperty(value = "物料描述")
    private String itemName;

    @ApiModelProperty(value = "供应商代码")
    private String supNo;

    @ApiModelProperty(value = "供应商名称")
    private String supName;

    @ApiModelProperty(value = "项目号(已合并)")
    private String progNo;

    @ApiModelProperty(value = "PO（已合并）")
    private String po;

    @ApiModelProperty(value = "负责人")
    private String fuzr;

    @ApiModelProperty(value = "订单数量")
    private BigDecimal ddsl;

    @ApiModelProperty(value = "收货数量(已交货数量)")
    private BigDecimal shsl;

    @ApiModelProperty(value = "未收货数量（未到货数量）")
    private BigDecimal wshsl;

    @ApiModelProperty(value = "需求数量（计划送货总数）")
    private BigDecimal xqsl;

    @ApiModelProperty(value = "历史未到货数量（延期未交货数量）")
    private BigDecimal lswdh;

    @ApiModelProperty(value = "本周计划收货数量")
    private BigDecimal cwjhshsl;

    @ApiModelProperty(value = "本周欠收数量")
    private BigDecimal cwqssl;

    @ApiModelProperty(value = "本周可到货数量")
    private BigDecimal cwkdhsl;

    @ApiModelProperty(value = "本周可到货日期")
    private Date cwkdhrq;

    @ApiModelProperty(value = "下周计划收货数量")
    private BigDecimal n1wjhshsl;

    @ApiModelProperty(value = "下周可到货数量")
    private BigDecimal n1wkdhsl;

    @ApiModelProperty(value = "下周可到货日期")
    private Date n1wkdhrq;

    @ApiModelProperty(value = "下2周计划收货数量")
    private BigDecimal n2wjhshsl;

    @ApiModelProperty(value = "下2周可到货数量")
    private BigDecimal n2wkdhsl;

    @ApiModelProperty(value = "下2周可到货日期")
    private Date n2wkdhrq;

    @ApiModelProperty(value = "下3周计划收货数量")
    private BigDecimal n3wjhshsl;

    @ApiModelProperty(value = "下3周可到货数量")
    private BigDecimal n3wkdhsl;

    @ApiModelProperty(value = "下3周可到货日期")
    private Date n3wkdhrq;

    @ApiModelProperty(value = "下4周计划收货数量")
    private BigDecimal n4wjhshsl;

    @ApiModelProperty(value = "下4周可到货数量")
    private BigDecimal n4wkdhsl;

    @ApiModelProperty(value = "下4周可到货日期")
    private Date n4wkdhrq;

    @ApiModelProperty(value = "未来周计划收货数量")
    private BigDecimal wljhshsl;

    @ApiModelProperty(value = "未来周可到货数量")
    private BigDecimal wlkdhsl;

    @ApiModelProperty(value = "未来周可到货日期")
    private Date wlkdhrq;

    @ApiModelProperty(value = "年数")
    private String yr;

    @ApiModelProperty(value = "周数")
    private String wk;

    @ApiModelProperty(value = "下周年数")
    private String nyr;

    @ApiModelProperty(value = "下周周数")
    private String nwk;

    @ApiModelProperty(value = "第2周年数")
    private String n2yr;

    @ApiModelProperty(value = "第2周周数")
    private String n2wk;

    @ApiModelProperty(value = "第3周年数")
    private String n3yr;

    @ApiModelProperty(value = "第3周周数")
    private String n3wk;

    @ApiModelProperty(value = "第4周年数")
    private String n4yr;

    @ApiModelProperty(value = "第4周周数")
    private String n4wk;

    @ApiModelProperty(value = "更新时间")
    private Date updateTime;
}

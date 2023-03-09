package org.springblade.modules.supplier.dto;

import com.baomidou.mybatisplus.annotation.TableName;
import com.fasterxml.jackson.annotation.JsonFormat;
import io.swagger.annotations.ApiModel;
import lombok.Data;

import java.io.Serializable;
import java.util.Date;


/**
 * 采购送货计划表
 *
 * @author Will
 */
@Data
@TableName("supplier_product_data")
@ApiModel(value = "供应商生产数据", description = "供应商生产数据")
public class SupplierProductDataList implements Serializable {

    private static final long serialVersionUID = 1L;



    /**
     * 主键
     */
    private Long srcId;

    /**
     * 状态
     */
    private String srcStatus;

    /**
     * 开单日期
     */
    @JsonFormat(pattern = "yyyy-MM-dd")
    private Date billDate;

    /**
     * 合同号
     */
    private String contractNum;

    /**
     * 订单号
     */
    private String orderNum;

    /**
     * 客户
     */
    private String customer;

    /**
     * 物料编号
     */
    private String itemCode;

    /**
     * 流转单号
     */
    private String circulationNum;

    /**
     * 物料名称
     */
    private String itemName;

    /**
     * 物料规格
     */
    private String itemAttribute;

    /**
     * 材质
     */
    private String material;

    /**
     * 炉号
     */
    private String heatNum;

    /**
     * 合同数量
     */
    private Double contractQuantity;

    /**
     * 生产数量
     */
    private Double productionQuantity;

    /**
     * 单重
     */
    private Double weight;

    /**
     * 重量
     */
    private Double weightTotal;

    /**
     * 计量单位
     */
    private String unit;

    /**
     * 交货日期
     */
    @JsonFormat(pattern = "yyyy-MM-dd")
    private Date deliveryDate;

    /**
     * 组树完工
     */
    private Double gtFinishQuantity;

    /**
     * 组树报废
     */
    private Double gtScrapQuantity;

    /**
     * 组树完工日期
     */
    @JsonFormat(pattern = "yyyy-MM-dd")
    private Date gtFinishDate;

    /**
     * 制壳完工
     */
    private Double shellFinishQuantity;

    /**
     * 制壳报废
     */
    private Double shellScrapQuantity;

    /**
     * 制壳完工日期
     */
    @JsonFormat(pattern = "yyyy-MM-dd")
    private Date shellScrapDate;

    /**
     * 浇铸完工
     */
    private Double castFinishQuantity;

    /**
     * 浇铸报废
     */
    private Double castScrapQuantity;

    /**
     * 浇铸完工日期
     */
    @JsonFormat(pattern = "yyyy-MM-dd")
    private Date castFinishDate;

    /**
     * 后处理完工
     */
    private Double reprocessFinishQuantity;

    /**
     * 后处理报废
     */
    private Double reprocessScrapQuantity;

    /**
     * 后处理完工日期
     */
    @JsonFormat(pattern = "yyyy-MM-dd")
    private Date reprocessFinishDate;

    /**
     * 终检完工
     */
    private Double inspectQuantity;

    /**
     * 终检完工日期
     */
    @JsonFormat(pattern = "yyyy-MM-dd")
    private Date inspectDate;

    /**
     * 备注
     */
    private String remark;





}

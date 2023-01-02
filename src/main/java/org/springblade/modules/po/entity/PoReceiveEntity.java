package org.springblade.modules.po.entity;

import com.baomidou.mybatisplus.annotation.TableName;
import com.fasterxml.jackson.databind.annotation.JsonSerialize;
import com.fasterxml.jackson.databind.ser.std.ToStringSerializer;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import io.swagger.models.auth.In;
import lombok.Data;
import lombok.EqualsAndHashCode;
import org.springblade.core.mp.base.BaseEntity;

import java.util.Date;



/**
 *  实体类
 *
 * @author Will
 */
@Data
@TableName("atw_po_receive")
@EqualsAndHashCode(callSuper = true)
@ApiModel(value = "PoReceive对象", description = "")
public
class PoReceiveEntity extends BaseEntity{

    private static final long serialVersionUID = 1L;

    /**
     * 供应商编号
     */
    @ApiModelProperty(value = "供应商编号")
    private String supCode;
    /**
     * 供应商名称
     */
    @ApiModelProperty(value = "供应商名称")
    private String supName;

    @JsonSerialize(using = ToStringSerializer.class)
    @ApiModelProperty(value = "")
    private Long poId;

    @ApiModelProperty(value = "送货单号")
    private String rcvCode;

    @JsonSerialize(using = ToStringSerializer.class)
    @ApiModelProperty(value = "")
    private Long    piId;
    /**
     * 生产日期
     */
    @ApiModelProperty(value = "生产日期")
    private Integer produceDate;
    /**
     * 规格
     */
    @ApiModelProperty(value = "规格")
    private String  specs;
    /**
     * 材质
     */
    @ApiModelProperty(value = "材质")
    private String  matQuality;
    /**
     * 送货数量
     */
    @ApiModelProperty(value = "送货数量")
    private Float   rcvNum;
    /**
     * 炉号
     */
    @ApiModelProperty(value = "炉号")
    private String  heatCode;
    /**
     * 序号
     */
    @ApiModelProperty(value = "序号")
    private Integer seq;
    /**
     * 备注
     */
    @ApiModelProperty(value = "备注")
    private String  remark;
    /**
     * 安特威收货时间
     */
    @ApiModelProperty(value = "安特威收货时间")
    private Integer receivedAt;

    /**
     * 是否外协
     */
    @ApiModelProperty(value = "是否外协")
    private String isOut;

    /**
     * 是否外检
     */
    @ApiModelProperty(value = "是否外检")
    private String isOutCheck;

    /**
     * 备注：精加工；粗加工
     */
    @ApiModelProperty(value = "备注")
    private String process;

    /**
     * 审核状态
     */
    @ApiModelProperty(value = "审核状态")
    private String checkStatus;

    /**
     * 创建人（可记录）
     */
    @ApiModelProperty(value = "创建人（可记录）")
    private Long createUserRecord;

    /**
     * 创建时间（可记录）
     */
    @ApiModelProperty(value = "创建人（可记录）")
    private Date createTimeRecord;

    /**
     * 物流单号
     */
    @ApiModelProperty(value = "物流单号")
    private String businessCode;

    /**
     * 物流商家
     */
    @ApiModelProperty(value = "物流商家")
    private String businessName;

    /**
     * 紧急程度
     */
    @ApiModelProperty(value = "物流商家")
    private Integer urgent;

}

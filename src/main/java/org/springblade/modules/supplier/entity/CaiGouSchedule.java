package org.springblade.modules.supplier.entity;

import cn.afterturn.easypoi.excel.annotation.Excel;
import com.baomidou.mybatisplus.annotation.TableName;
import com.fasterxml.jackson.annotation.JsonFormat;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;
import org.springframework.format.annotation.DateTimeFormat;

import java.io.Serializable;
import java.math.BigDecimal;
import java.util.Date;


/**
 * 采购送货计划表
 *
 * @author Will
 */
@Data
@TableName("BI_Supdeliv_Plan_Data_Detail_Write")
@ApiModel(value = "采购送货计划表", description = "采购送货计划表")
public class CaiGouSchedule implements Serializable {

    private static final long serialVersionUID = 1L;

    @ApiModelProperty(value = "id")
    private Long id;

    @ApiModelProperty(value = "子项目号")
    private String proNo;

    @ApiModelProperty(value = "子项目需求数量")
    private BigDecimal proNum;

    @ApiModelProperty(value = "合同交期")
    private Date agreeDate;

    @ApiModelProperty(value = "计划交期")
    private Date planDate;

    @ApiModelProperty(value = "单据编号")
    private String poCode;

    @ApiModelProperty(value = "行号")
    private String poLn;

    @ApiModelProperty(value = "单据编号-行号")
    private String poCodeLn;

    @ApiModelProperty(value = "供应商代码")
    private String supCode;

    @ApiModelProperty(value = "供应商")
    private String supName;

    @ApiModelProperty(value = "物料编号")
    private String itemCode;

    @ApiModelProperty(value = "物料名称")
    private String itemName;

    @ApiModelProperty(value = "订单数量")
    private BigDecimal orderNum;

    @ApiModelProperty(value = "需求数量")
    private BigDecimal reqNum;

    @ApiModelProperty(value = "缺物料数量")
    private BigDecimal lackItemNum;

    @ApiModelProperty(value = "（WW/PO）交期")
    private Date wwpoDate;

    @ApiModelProperty(value = "供应商承诺交期")
    private Date supConfirmDate;

    @ApiModelProperty(value = "需求日期")
    private Date reqDate;

    @ApiModelProperty(value = "当前需求日期")
    private Date nowReqDate;

    @ApiModelProperty(value = "预判")
    private String anticipation;

    @DateTimeFormat(pattern="yyyy-MM-dd'T'HH:mm:ss.SSS'Z'")
    @JsonFormat(pattern="yyyy-MM-dd'T'HH:mm:ss.SSS'Z'")
    @ApiModelProperty(value = "审核修改交期")
    private Date checkUpdateDate;

    @DateTimeFormat(pattern="yyyy-MM-dd'T'HH:mm:ss.SSS'Z'")
    @JsonFormat(pattern="yyyy-MM-dd'T'HH:mm:ss.SSS'Z'")
    @ApiModelProperty(value = "供应商确认交期")
    private Date checkUpdateDateFrist;

    @ApiModelProperty(value = "备注")
    private String remark;

    @ApiModelProperty(value = "到货状态")
    private String arrivalStatus;

    @ApiModelProperty(value = "检验状态")
    private String checkStatus;

    @ApiModelProperty(value = "报检时间")
    private String snCreateTime;

    @ApiModelProperty(value = "入库状态")
    private String storeStatus;

    @ApiModelProperty(value = "责任人")
    private String dutyPerson;

    @ApiModelProperty(value = "责任人(SRM里面匹配出来的)")
    private String person;

    @ApiModelProperty(value = "是否是四大件")
    private String isFourBigItem;

    @ApiModelProperty(value = "对应的DO的状态")
    private Integer doStatus;

    @ApiModelProperty(value = "订单数量")
    private Integer tcNum;

    @ApiModelProperty(value = "入库数量")
    private Integer storeNum;


    @ApiModelProperty(value = "未收货数量")
    private Integer notArvNum;


    private Integer notRcvNum;
    @ApiModelProperty(value = "未送货数量")
    private Integer notRcvNum2;




    @ApiModelProperty(value = "次序")
    private String seq;

    @ApiModelProperty(value = "删除标志")
    private Integer is_deleted;

    @ApiModelProperty(value = "送货单号")
    private String rcvCode;

    @ApiModelProperty(value = "ABC类")
    private String codeType;

    @ApiModelProperty(value = "修改次数")
    private String limits;

    @ApiModelProperty(value = "修改人")
    private String updateUser;

    @ApiModelProperty(value = "是否紧急")
    private String isUrgent;

    private String bkflag;

    private int ysdsl;

    private int wshsl;

    private String status;





}

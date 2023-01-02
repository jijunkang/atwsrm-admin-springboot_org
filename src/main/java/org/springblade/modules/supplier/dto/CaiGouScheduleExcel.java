package org.springblade.modules.supplier.dto;

import cn.afterturn.easypoi.excel.annotation.Excel;
import com.baomidou.mybatisplus.annotation.TableName;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import java.io.Serializable;
import java.math.BigDecimal;
import java.util.Date;


@Data
public class CaiGouScheduleExcel implements Serializable {

    private static final long serialVersionUID = 1L;

    @Excel(name = "子项目号")
    private String proNo;

    @Excel(name = "子项目需求数量")
    private BigDecimal proNum;

    @Excel(name = "合同交期")
    private String agreeDate;

    @Excel(name = "计划交期")
    private String planDate;

    @Excel(name = "单据编号")
    private String poCode;

    @Excel(name = "行号")
    private String poLn;

    @Excel(name = "编号-行号")
    private String poCodeLn;

    @Excel(name = "供应商")
    private String supName;

    @Excel(name = "物料编号")
    private String itemCode;

    @Excel(name = "物料名称")
    private String itemName;

    @Excel(name = "订单数量")
    private BigDecimal orderNum;

    @Excel(name = "需求数量")
    private BigDecimal reqNum;

    /*@Excel(name = "未收货数量")
    private Integer notRcvNum;*/

    @Excel(name = "未收货数量")
    private Integer notArvNum;

    @Excel(name = "未送货数量")
    private Integer notRcvNum2;

    @Excel(name = "审核修改交期")
    private String checkUpdateDate;

    @Excel(name = "供应商确认交期")
    private String checkUpdateDateFrist;

    @Excel(name = "回厂预判")
    private String anticipation;

    @Excel(name = "是否紧急")
    private String isUrgent;

    @Excel(name = "备注")
    private String remark;

    @Excel(name = "缺物料数量")
    private BigDecimal lackItemNum;

    @Excel(name = "（WW/PO）交期")
    private String wwpoDate;

    @Excel(name = "需求日期")
    private String reqDate;

    //@Excel(name = "当前需求日期")
    private String nowReqDate;

    //@Excel(name = "到货状态")
    private String arrivalStatus;

    @Excel(name = "状态")
    private String checkStatus;

    @Excel(name = "报检时间")
    private String snCreateTime;

    @Excel(name = "入库状态")
    private String storeStatus;

    @Excel(name = "责任人")
    private String person;

    @Excel(name =  "ABC类")
    private String codeType;

    @Excel(name =  "次序")
    private String seq;

    @Excel(name =  "已修改次数")
    private String limits;

    @Excel(name =  "是否备库")
    private String bkflag;

}

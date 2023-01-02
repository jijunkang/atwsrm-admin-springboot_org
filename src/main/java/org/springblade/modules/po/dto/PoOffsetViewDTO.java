package org.springblade.modules.po.dto;

import cn.afterturn.easypoi.excel.annotation.Excel;
import cn.afterturn.easypoi.excel.annotation.ExcelCollection;
import lombok.Data;
import lombok.EqualsAndHashCode;
import org.springblade.core.tool.utils.DateUtil;
import org.springblade.modules.po.entity.PoOffsetViewEntity;

import java.math.BigDecimal;
import java.util.Date;
import java.util.List;

/**
 *  模型DTO
 *
 * @author Will
 */
@Data
@EqualsAndHashCode(callSuper = true)
public class PoOffsetViewDTO extends PoOffsetViewEntity{

    private static final long serialVersionUID = 1L;

    @Excel(name = "项目号", needMerge = true)
    private String proNo;

    @Excel(name = "订单编号", needMerge = true)
    private String poCode;

    @Excel(name = "行号", needMerge = true)
    private Integer poLn;

    @Excel(name = "物料编号", needMerge = true)
    private String itemCode;

    @Excel(name = "物料名称", needMerge = true)
    private String itemName;

    @Excel(name = "数量", needMerge = true)
    private BigDecimal priceNum;

    @Excel(name = "工艺卡控类型", needMerge = true)
    private String craftCtrlNodeName;

    @ExcelCollection(name = "")
    List<PoItemNodeListDTO> poItemNodeList;

    @Excel(name = "要求交期", needMerge = true)
    private String reqDateFmt;

    @Excel(name = "承诺交期", needMerge = true)
    private String supConfirmDateFmt;

    @Excel(name = "修改交期", needMerge = true)
    private String supUpdateDateFmt;

    @Excel(name = "运算交期", needMerge = true)
    private String operationDateFmt;

    @Excel(name = "备注", needMerge = true)
    private String remark;

    @Excel(name = "供应商名称", needMerge = true)
    private String supName;

    @Excel(name = "联系人", needMerge = true)
    private String supContact;

    @Excel(name = "电话号码", needMerge = true)
    private String supMobile;

    public
    String getReqDateFmt(){
        if(this.getReqDate() == null || this.getReqDate() <= 0){
            return "";
        }
        return DateUtil.formatDate(new Date(this.getReqDate()*1000));
    }

    public
    String getSupConfirmDateFmt(){
        if(this.getSupConfirmDate() == null || this.getSupConfirmDate() <= 0){
            return "";
        }
        return DateUtil.formatDate(new Date(this.getSupConfirmDate()*1000));
    }

    public
    String getSupUpdateDateFmt(){
        if(this.getSupUpdateDate() == null || this.getSupUpdateDate() <= 0){
            return "";
        }
        return DateUtil.formatDate(new Date(this.getSupUpdateDate()*1000));
    }

    public
    String getOperationDateFmt(){
        if(this.getOperationDate() == null || this.getOperationDate() <= 0){
            operationDateFmt = "";
        }else {
            operationDateFmt = DateUtil.formatDate(new Date(this.getOperationDate()*1000));
        }
        return operationDateFmt;
    }




}

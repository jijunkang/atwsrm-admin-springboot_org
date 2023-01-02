package org.springblade.modules.po.dto;

import cn.afterturn.easypoi.excel.annotation.Excel;
import lombok.Data;
import org.springblade.common.utils.WillDateUtil;
import org.springblade.core.tool.utils.DateUtil;
import org.springblade.modules.po.entity.PoRemindEntity;


/**
 * 实体类
 * @author Will
 */
@Data
public
class PoRemindExcel extends PoRemindEntity{
    private static final long serialVersionUID = 1L;

    @Excel(name = "子项目号")
    private String proNo;

    @Excel(name = "订单编号")
    private String poCode;

    @Excel(name = "订单行号")
    private Integer poLn;

    //	@Excel(name = "偏移类型")
    private String type;

    @Excel(name = "物料编号")
    private String itemCode;

    @Excel(name = "物料名称")
    private String itemName;

    //    @Excel(name = "计量数量")
    @Excel(name = "数量")
    private Integer priceNum;
    //    @Excel(name = "计量单位")
    private String  priceUom;
    //    @Excel(name = "交易数量")
    private Integer tcNum;
    //    @Excel(name = "交易单位")
    private String  tcUom;

    private Integer proNum;

    @Excel(name = "要求交期")
    private String  reqDateFmt;

    @Excel(name = "确认交期")
    private String  supConfirmDateFmt;

    @Excel(name = "修改交期")
    private String  supUpdateDateFmt;

    @Excel(name = "运算交期")
    private String  operationDateFmt;

    @Excel(name = "供应商编号")
    private String supCode;

    @Excel(name = "供应商名称")
    private String supName;

    @Excel(name = "联系人")
    private String supContact;

    @Excel(name = "电话号码")
    private String supMobile;

    @Excel(name = "备注")
    private String remark;

    public
    String getReqDateFmt(){
        reqDateFmt = WillDateUtil.unixTimeToStr(this.getReqDate(),DateUtil.PATTERN_DATE);
        return reqDateFmt;
    }

    public
    String getSupConfirmDateFmt(){
        supConfirmDateFmt = WillDateUtil.unixTimeToStr(this.getSupConfirmDate(),DateUtil.PATTERN_DATE);
        return supConfirmDateFmt;
    }

    public
    String getSupUpdateDateFmt(){
        supUpdateDateFmt = WillDateUtil.unixTimeToStr(this.getSupUpdateDate(),DateUtil.PATTERN_DATE);
        return supUpdateDateFmt;
    }

    public
    String getOperationDateFmt(){
        operationDateFmt = WillDateUtil.unixTimeToStr(this.getOperationDate(),DateUtil.PATTERN_DATE);
        return operationDateFmt;
    }
}

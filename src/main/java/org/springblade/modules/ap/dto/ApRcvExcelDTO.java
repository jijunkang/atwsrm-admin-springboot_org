package org.springblade.modules.ap.dto;

import cn.afterturn.easypoi.excel.annotation.Excel;
import lombok.Data;
import org.springblade.common.utils.WillDateUtil;
import org.springblade.core.tool.utils.StringUtil;

import java.io.Serializable;
import java.math.BigDecimal;
import java.text.SimpleDateFormat;
import java.util.Date;

/**
 * @author libin
 * @author libin
 *
 * @date 9:59 2020/6/19
 **/
@Data
public class ApRcvExcelDTO implements Serializable {

    private static final long serialVersionUID = 1L;

    @Excel(name = "供应商名称")
    private String supName;

    @Excel(name = "订单单号")
    private String poCode;

    @Excel(name = "订单行号")
    private Integer poLn;

    @Excel(name = "收货单号")
    private String rcvCode;

    @Excel(name = "收货行号")
    private Integer rcvLn;

    @Excel(name = "物料编号")
    private String itemCode;

    @Excel(name = "物料描述")
    private String itemName;

    @Excel(name = "实收数量")
    private BigDecimal rcvActualQty;

    @Excel(name = "累计对账数量")
    private BigDecimal accumRecQty;

    @Excel(name = "计价单位")
    private String uom;

    @Excel(name = "含税单价")
    private BigDecimal taxPrice;

    @Excel(name = "价税合计")
    private BigDecimal taxSubTotal;

    @Excel(name = "税组合")
    private BigDecimal taxRate;

    private Long rcvDate;
    @Excel(name = "到货日期")
    private String rcvDateFmt;

    public
    String getRcvDateFmt(){
        SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd");
        if(!StringUtil.isEmpty(rcvDate)){
            return sdf.format(new Date(rcvDate * 1000));
        }
        return null;
    }
}

package org.springblade.modules.ap.dto;

import cn.afterturn.easypoi.excel.annotation.Excel;
import lombok.Data;
import org.springblade.core.tool.utils.StringUtil;

import java.io.Serializable;
import java.math.BigDecimal;
import java.text.SimpleDateFormat;
import java.util.Date;

/**
 * @author libin
 *
 * @date 17:08 2020/8/6
 **/
@Data
public class ApNcrItemExcelDTO implements Serializable {

    private static final long serialVersionUID = 1L;

    @Excel(name = "NCR单号")
    private String code;

    @Excel(name = "报告单号")
    private String reportCode;

    @Excel(name = "罚款金额")
    private BigDecimal finePrice;

    private Date createTime;
    @Excel(name = "SRM生成日期")
    private String createTimeFmt;

    private String getCreateTimeFmt(){
        SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd");
        if(!StringUtil.isEmpty(createTime)){
            return sdf.format(createTime);
        }
        return null;
    }

}

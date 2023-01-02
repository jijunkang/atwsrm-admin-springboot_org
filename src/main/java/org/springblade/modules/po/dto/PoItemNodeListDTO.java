package org.springblade.modules.po.dto;

import cn.afterturn.easypoi.excel.annotation.Excel;
import lombok.Data;
import org.springblade.core.tool.utils.StringUtil;

import java.text.SimpleDateFormat;
import java.util.Date;

/**
 * @author libin
 *
 * @date 17:09 2020/7/24
 **/
@Data
public class PoItemNodeListDTO {

    private static final long serialVersionUID = 1L;

    @Excel(name = "卡控序号")
    private Integer sort;

    @Excel(name = "卡控节点名称")
    private String name;

    private Long planConfirmDate;
    @Excel(name = "计划完成日期")
    private String planConfirmDateFmt;

    private Integer isComplete;
    @Excel(name = "是否完工")
    private String isCompleteFmt;

    @Excel(name = "供应商备注")
    private String supRemark;

    @Excel(name = "采购备注")
    private String purchRemark;


    public String getPlanConfirmDateFmt() {
        SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd");
        if(!StringUtil.isEmpty(planConfirmDate) && planConfirmDate != 0){
            return sdf.format(new Date(planConfirmDate * 1000));
        }
        return null;
    }

    public String getIsCompleteFmt() {
        if(!StringUtil.isEmpty(isComplete)){
            return isComplete == 1 ? "是" : "否";
        }
        return "否";
    }
}

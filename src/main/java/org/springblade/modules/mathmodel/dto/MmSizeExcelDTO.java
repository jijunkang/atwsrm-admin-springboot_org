package org.springblade.modules.mathmodel.dto;

import cn.afterturn.easypoi.excel.annotation.Excel;
import lombok.Data;
import org.springblade.core.tool.utils.StringUtil;

import java.io.Serializable;
import java.math.BigDecimal;
import java.text.SimpleDateFormat;
import java.util.Date;

/**
 * @author libin
 * @date 17:00 2020/7/30
 **/
@Data
public class MmSizeExcelDTO implements Serializable {

    private static final long serialVersionUID = 1L;

    private String mainCode;

    @Excel(name = "主分类") /* 与页面对于*/
    private String childCode;

    @Excel(name = "子分类")
    private String mainCodeFmt;

    @Excel(name = "物料编号")
    private String itemCode;

    @Excel(name = "物料描述")
    private String itemName;

    @Excel(name = "材质")
    private String metal;

    @Excel(name = "规格")
    private String spec;

    @Excel(name = "外径1")
    private BigDecimal outD1;

    @Excel(name = "高度1")
    private BigDecimal h1;

    @Excel(name = "内孔")
    private BigDecimal hole;

    @Excel(name = "外径2")
    private BigDecimal outD2;

    @Excel(name = "高度2")
    private BigDecimal h2;

    private String createUser;
    @Excel(name = "创建人")
    private String createUserFmt;

    private Date createTime;
    @Excel(name = "创建时间")
    private String createTimeFmt;

    private String updateUser;
    @Excel(name = "修改人")
    private String updateUserFmt;

    private Date updateTime;
    @Excel(name = "修改时间")
    private String updateTimeFmt;


    private String getCreateTimeFmt() {
        SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd hh:mm:ss");
        if (!StringUtil.isEmpty(createTime)) {
            return sdf.format(createTime);
        }
        return null;
    }

    private String getUpdateTimeFmt() {
        SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd hh:mm:ss");
        if (!StringUtil.isEmpty(updateTime)) {
            return sdf.format(updateTime);
        }
        return null;
    }


}

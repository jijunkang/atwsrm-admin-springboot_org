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
 *
 * @date 17:16 2020/7/30
 **/
@Data
public class MmOutMarginExcelDTO implements Serializable {

	private static final long serialVersionUID = 1L;

    private String mainCode;

    @Excel(name = "主分类")
    private String childCode;
    @Excel(name = "子分类")
    private String mainCodeFmt;
    /**
     * 外圆(小)
     */
    @Excel(name = "外圆(小)")
    private BigDecimal outSmall;

    /**
     * 外圆(大)
     */
    @Excel(name = "外圆(大)")
    private BigDecimal outBig;

    /**
     * 高度(小)
     */
    @Excel(name = "高度(小)")
    private BigDecimal hightSmall;

    /**
     * 高度(大)
     */
    @Excel(name = "高度(大)")
    private BigDecimal hightBig;

    /**
     * 余量
     */
    @Excel(name = "余量")
    private BigDecimal margin;


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

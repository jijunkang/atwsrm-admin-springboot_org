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
 * @date 17:20 2020/7/30
 **/
@Data
public class MmDensityExcelDTO implements Serializable {

	private static final long serialVersionUID = 1L;

    /**
     * 材质
     */
    @Excel(name = "材质")
    private String metal;
    /**
     * 密度
     */
    @Excel(name = "密度")
    private BigDecimal density;

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

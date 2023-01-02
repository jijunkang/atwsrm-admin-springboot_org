package org.springblade.modules.supplier.dto;

import cn.afterturn.easypoi.excel.annotation.Excel;
import com.fasterxml.jackson.annotation.JsonFormat;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;
import org.springblade.core.tool.utils.StringUtil;
import org.springframework.format.annotation.DateTimeFormat;

import java.io.Serializable;
import java.math.BigDecimal;
import java.text.SimpleDateFormat;
import java.util.Date;

/**
 * @author zhoulw
 *
 * @date 10:49 2021/07/26
 **/
@Data
public class SupplierExcelDTO implements Serializable {

    private static final long serialVersionUID = 1L;

    /**
     * 供应商编码
     */
    @Excel(name = "供应商编码")
    private String code;

    /**
     * 供应商名称
     */
    @Excel(name = "供应商名称")
    private String name;

    /**
     * 供应商分类
     */
    @Excel(name = "供应商分类")
    private String typeName;

    /**
     * 供应商等级
     */
    @Excel(name = "供应商等级")
    private String supGrade;

    /**
     * 供应类型
     */
    @Excel(name = "供应类型")
    private String supType;

    /**
     * 采购员
     */
    @Excel(name = "采购员")
    private String purchName;

    /**
     * 手机号码
     */
    @Excel(name = "联系人手机")
    private String mobile;

    /**
     * 主联系人
     */
    @Excel(name = "主联系人")
    private String ctcName;

    /**
     * 电话
     */
    @Excel(name = "电话")
    private String phone;

    /**
     * 传真
     */
    @Excel(name = "传真")
    private String fax;

    /**
     * 邮箱
     */
    @Excel(name = "邮箱")
    private String email;

    /**
     * 付款方式
     */
    @Excel(name = "付款方式")
    private String payWay;

    /**
     * 税率
     */
    @Excel(name = "税率")
    private String taxRate;

    /**
     * 地址
     */
    @Excel(name = "地址")
    private String address;

    /**
     * 状态
     */
    @Excel(name = "状态")
    private String status;

    /**
     * 质量合格率
     */
    @Excel(name = "质量合格率")
    private BigDecimal passRate;

    /**
     * 交货及时率
     */
    @Excel(name =  "交货及时率")
    private BigDecimal arvRate;

    /**
     * 修改时间
     */
    @Excel(name =  "修改时间")
    private String updateTime;


    /**
     * 资源专员
     */
    @Excel(name =  "资源专员")
    private String placeName;


    /**
     * 资源专员
     */
    @Excel(name =  "合同模板",replace = {"普通模板 - 内仓_A","普通模板 - 外仓_AW","法律模板 - 内仓_B","法律模板 - 外仓_BW","刀具模板 - 内仓_C","刀具模板 - 外仓_CW","整机模板 - 内仓_D","整机模板 - 外仓_DW","模具模板 - 内仓_mould","模具模板 - 外仓_mouldW"})
    private String templateType;

    public SupplierExcelDTO() {
    }
}

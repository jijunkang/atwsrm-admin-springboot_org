package org.springblade.modules.po.vo;

import cn.afterturn.easypoi.excel.annotation.Excel;
import com.fasterxml.jackson.databind.annotation.JsonSerialize;
import com.fasterxml.jackson.databind.ser.std.ToStringSerializer;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;
import lombok.EqualsAndHashCode;
import org.springblade.modules.po.entity.PoItemEntity;

import java.math.BigDecimal;
import java.util.List;

/**
 * 采购订单明细 模型VO
 *
 * @author Will
 */
@Data
public class PoItemExcelVO {

    private static final long serialVersionUID = 1L;

    private Integer poStatus;

    @Excel(name = "供应商编码")
    private String supCode;

    @Excel(name = "供应商名称")
    private String supName;

    @Excel(name = "采购单号")
    private String poCode;

    @Excel(name = "行号")
    private Integer poLn;

    @Excel(name = "物料编号")
    private String itemCode;

    @Excel(name = "物料名称")
    private String itemName;

    @Excel(name = "交易数量")
    private BigDecimal tcNum;

    @Excel(name = "交易单位")
    private String tcUom;

    @Excel(name = "项目号")
    private String proNo;

    @Excel(name = "报检数量")
    private BigDecimal rcvGoodsNum;

    @Excel(name = "实收数量")
    private BigDecimal arvGoodsNum;

    @Excel(name = "未到货数量")
    private BigDecimal proGoodsNum;

    @Excel(name = "退补数量")
    private BigDecimal fillGoodsNum;

    @Excel(name = "要求交期")
    private String reqDateFmt;

    @Excel(name = "承诺交期")
    private String supConfirmDateFmt;

    @Excel(name = "最终用户")
    private String endUser;

    @Excel(name = "ABC类")
    private String codeType;

    @Excel(name = "备注")
    private String remark;

    @Excel(name = "状态")
    private String u9Status;

}

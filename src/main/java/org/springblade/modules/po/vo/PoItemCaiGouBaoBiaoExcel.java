package org.springblade.modules.po.vo;

import cn.afterturn.easypoi.excel.annotation.Excel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import java.math.BigDecimal;

/**
 * 采购订单明细 模型VO
 *
 * @author Will
 */
@Data
public class PoItemCaiGouBaoBiaoExcel {

    private static final long serialVersionUID = 1L;

    @Excel(name = "采购单号")
    private String poCode;

    @Excel(name = "行号")
    private Integer poLn;

    @Excel(name = "请购单号")
    private String prCode;

    @Excel(name = "物料编号")
    private String itemCode;

    @Excel(name = "物料名称")
    private String itemName;

    @Excel(name = "供应商编码")
    private String supCode;

    @Excel(name = "供应商名称")
    private String supName;

    @Excel(name = "要求交期")
    private String reqDateFmt;

    @Excel(name = "承诺交期")
    private String supConfirmDateFmt;

    @Excel(name = "修改交期")
    private String supUpdateDateFmt;

    @Excel(name = "采购数量")
    private BigDecimal tcNum;

    @Excel(name = "交易单位")
    private String tcUom;

    @Excel(name = "采购单价")
    private BigDecimal price;

    @Excel(name = "小计")
    private BigDecimal amount;

    @Excel(name = "到货数量")
    private BigDecimal rcvGoodsNum;

    @Excel(name = "实收数量")
    private BigDecimal arvGoodsNum;

    @Excel(name = "未到货数量")
    private BigDecimal proGoodsNum;

    @Excel(name = "退货数量")
    private BigDecimal returnGoodsNum;

    @Excel(name = "采购员")
    private String purchName;

    @Excel(name = "项目号")
    private String proNo;

    @Excel(name = "状态")
    private String u9Status;

    @Excel(name = "最终用户")
    private String endUser;

    @Excel(name = "生产订单号")
    private String moNo;

    @Excel(name = "ABC类")
    private String codeType;

    @Excel(name = "最后同步U9时间")
    private String lastSyncTime;


}

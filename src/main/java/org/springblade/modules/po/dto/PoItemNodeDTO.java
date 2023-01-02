package org.springblade.modules.po.dto;

import cn.afterturn.easypoi.excel.annotation.Excel;
import cn.afterturn.easypoi.excel.annotation.ExcelCollection;
import com.fasterxml.jackson.databind.annotation.JsonSerialize;
import com.fasterxml.jackson.databind.ser.std.ToStringSerializer;
import lombok.Data;
import org.springblade.core.tool.utils.StringUtil;

import java.math.BigDecimal;
import java.util.List;

/**
 * @author libin
 *
 * @date 16:52 2020/7/24
 **/
@Data
public class PoItemNodeDTO {

    private static final long serialVersionUID = 1L;

    @JsonSerialize(using = ToStringSerializer.class)
    private Long id;

    @Excel(name = "供应商编码", needMerge = true)
    private String supCode;

    @Excel(name = "供应商名称", needMerge = true)
    private String supName;

    @Excel(name = "订单编号", needMerge = true)
    private String poCode;

    @Excel(name = "订单行号", needMerge = true)
    private Integer poLn;

    @Excel(name = "物料编号", needMerge = true)
    private String itemCode;

    @Excel(name = "物料名称", needMerge = true)
    private String itemName;

    @Excel(name = "工艺卡控类型", needMerge = true)
    private String craftCtrlNodeName;

    @ExcelCollection(name = "")
    List<PoItemNodeListDTO> poItemNodeList;

    @Excel(name = "采购单位", needMerge = true)
    private String priceUom;

    @Excel(name = "采购数量", needMerge = true)
    private BigDecimal priceNum;

    @Excel(name = "到货数量", needMerge = true)
    private BigDecimal rcvGoodsNum;

    @Excel(name = "实收数量", needMerge = true)
    private BigDecimal arvGoodsNum;

    @Excel(name = "未到货数量", needMerge = true)
    private BigDecimal proGoodsNum;

    @Excel(name = "退补数量", needMerge = true)
    private BigDecimal fillGoodsNum;

    @Excel(name = "备注", needMerge = true)
    private String remark;

    @Excel(name = "要求交期", needMerge = true)
    private String reqDateFmt;

    @Excel(name = "承诺交期", needMerge = true)
    private String supConfirmDateFmt;

    @Excel(name = "修改交期", needMerge = true)
    private String supUpdateDateFmt;

    private Integer status;
    @Excel(name = "状态", needMerge = true)
    private String statusFmt;

    private Integer isDeliverablesFull;
    @Excel(name = "资料是否齐全", needMerge = true)
    private String isDeliverablesFullFmt;

    @Excel(name = "最终用户", needMerge = true)
    private String endUser;


    public String getStatusFmt() {
        return "执行中";
    }

    public String getIsDeliverablesFullFmt() {
        if(!StringUtil.isEmpty(isDeliverablesFull)){
            return isDeliverablesFull == 1 ? "是" : "否";
        }
        return "否";
    }
}

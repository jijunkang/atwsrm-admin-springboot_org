package org.springblade.modules.aps.dto;

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
 * @date 13:54 2020/10/14
 **/
@Data
public class ApsExcelDTO implements Serializable {

    private static final long serialVersionUID = 1L;

    /**
     * 项目号
     */
    @Excel(name = "项目号")
    private String proNo;
    /**
     * 子项目号
     */
    @Excel(name = "子项目号")
    private String proNoSub;
    /**
     * 合同交期
     */
    @Excel(name = "合同交期")
    private String contractDeliDateFmt;
    private Long contractDeliDate;
    /**
     * 计划交期
     */
    @Excel(name = "计划交期")
    private String planDeliDateFmt;
    private Long planDeliDate;
    /**
     * 计划交期修改原因
     */
    @Excel(name = "计划交期修改原因")
    private String planUpdateCause;
    /**
     * 订单号
     */
    @Excel(name = "订单号")
    private String poCode;
    /**
     * 订单行号
     */
    @Excel(name = "订单行号")
    private Integer poLn;
    /**
     * 料号
     */
    @Excel(name = "料号")
    private String itemCode;
    /**
     * 物料名称
     */
    @Excel(name = "物料名称")
    private String     itemName;
    /**
     * 订单数量
     */
    @Excel(name = "订单数量",numFormat = "0")
    private BigDecimal tcNum;
    /**
     * 项目需求数量
     */
    @Excel(name = "项目需求数量",numFormat = "0")
    private BigDecimal    proReqNum;
    /**
     * 最早到货日
     */
    @Excel(name = "最早到货日")
    private String poEarliestDeliDateFmt;
    private Long poEarliestDeliDate;
    /**
     * 送货日期
     */
    @Excel(name = "送货日期")
    private String deliveryDateFmt;
    private Long deliveryDate;
    /**
     * 交期
     */
    @Excel(name = "交期")
    private String poDeliDateFmt;
    private Long poDeliDate;
    /**
     * 修改交期
     */
    @Excel(name = "申请修改交期")
    private String applyModifyDeliDateFmt;
    private Long applyModifyDeliDate;
    /**
     * 采购交期偏移
     */
    @Excel(name = "采购交期偏移")
    private Integer offsetDays;
    /**
     * 评审交期
     */
    @Excel(name = "评审交期")
    private String reviewDeliDateFmt;
    private Long reviewDeliDate;
    /**
     * 机加可压缩比例
     */
    @Excel(name = "机加可压缩比例")
    private String machiningCompRate;
    /**
     * 机加最终评审完工日期
     */
    @Excel(name = "机加最终评审完工日期")
    private String machiningReviewCompleteDateFmt;
    private Long machiningReviewCompleteDate;
    /**
     * 装配可压缩比例
     */
    @Excel(name = "装配可压缩比例")
    private String fittingCompRate;
    /**
     * 装配最终评审完工日期
     */
    @Excel(name = "装配最终评审完工日期")
    private String fittingReviewCompleteDateFmt;
    private Long fittingReviewCompleteDate;
    /**
     * 计划交期(评审后)
     */
    @Excel(name = "计划交期(评审后)")
    private String planDeliDateReviewFmt;
    private Long planDeliDateReview;
    /**
     * NCR编号
     */
    @Excel(name = "NCR编号")
    private String ncrNo;
    /**
     * 责任人
     */
    @Excel(name = "责任人")
    private String personInCharge;


    public ApsExcelDTO() {
    }


    public String getContractDeliDateFmt() {
        SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd");
        if (!StringUtil.isEmpty(contractDeliDate)) {
            return sdf.format(new Date(contractDeliDate * 1000));
        }
        return "";
    }

    public String getPlanDeliDateFmt() {
        SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd");
        if (!StringUtil.isEmpty(planDeliDate)) {
            return sdf.format(new Date(planDeliDate * 1000));
        }
        return "";
    }

    public String getPoEarliestDeliDateFmt() {
        SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd");
        if (!StringUtil.isEmpty(poEarliestDeliDate)) {
            return sdf.format(new Date(poEarliestDeliDate * 1000));
        }
        return "";
    }

    public String getPoDeliDateFmt() {
        SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd");
        if (!StringUtil.isEmpty(poDeliDate)) {
            return sdf.format(new Date(poDeliDate * 1000));
        }
        return "";
    }

    public String getDeliveryDateFmt() {
        SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd");
        if (!StringUtil.isEmpty(deliveryDate)) {
            return sdf.format(new Date(deliveryDate * 1000));
        }
        return "";
    }

    public String getApplyModifyDeliDateFmt() {
        SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd");
        if (!StringUtil.isEmpty(applyModifyDeliDate)) {
            return sdf.format(new Date(applyModifyDeliDate * 1000));
        }
        return "";
    }

    public String getReviewDeliDateFmt() {
        SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd");
        if (!StringUtil.isEmpty(reviewDeliDate)) {
            return sdf.format(new Date(reviewDeliDate * 1000));
        }
        return "";
    }

    public String getMachiningReviewCompleteDateFmt() {
        SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd");
        if (!StringUtil.isEmpty(machiningReviewCompleteDate)) {
            return sdf.format(new Date(machiningReviewCompleteDate * 1000));
        }
        return "";
    }

    public String getFittingReviewCompleteDateFmt() {
        SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd");
        if (!StringUtil.isEmpty(fittingReviewCompleteDate)) {
            return sdf.format(new Date(fittingReviewCompleteDate * 1000));
        }
        return "";
    }

    public String getPlanDeliDateReviewFmt() {
        SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd");
        if (!StringUtil.isEmpty(planDeliDateReview)) {
            return sdf.format(new Date(planDeliDateReview * 1000));
        }
        return "";
    }
}

package org.springblade.modules.pr.dto;

import cn.afterturn.easypoi.excel.annotation.Excel;
import lombok.Data;
import org.springblade.common.utils.WillDateUtil;

import java.io.Serializable;
import java.math.BigDecimal;
import java.text.SimpleDateFormat;
import java.util.Date;

/**
 * 请购单 模型DTO
 *
 * @author Will
 */
@Data
public
class U9PrExExcelDTO implements Serializable {

    private static final long serialVersionUID = 1L;

    @Excel(name = "PR审核时间", format = "yyyy-MM-dd")
    private Date prspDate;

    @Excel(name = "请购单号")
    private String prCode;

    @Excel(name = "行号")
    private Integer prLn;

    @Excel(name = "物料编号")
    private String itemCode;

    @Excel(name = "物料描述")
    private String itemName;

    @Excel(name = "采购数量")
    private BigDecimal tcNum;

    @Excel(name = "项目需求日期", format = "yyyy-MM-dd")
    private Date proNeedDate;


    private Long   reqDate;
    @Excel(name = "请购单需求交期")
    private String reqDateFmt;

    @Excel(name = "标准交期天数")
    private Long bzReqDate;

    @Excel(name = "ABC分类")
    private String abcType;

    @Excel(name = "请购单备注")
    private String requisitionRemark;

    @Excel(name = "业务类型", replace = {"标准采购_0", "全程委外采购_1", "VMI采购_2"})
    private Integer bizType;


    @Excel(name = "子项目")
    private String subproject;


    @Excel(name = "计划交期", format = "yyyy-MM-dd")
    private Date planDate;


    @Excel(name = "APS是否计算")
    private String isAps;

    @Excel(name = "制单人")
    private String prCreateUser;

    @Excel(name = "问题分类")
    private String problemType;

    @Excel(name = "异常问题描述",width = 100)
    private String problemDesc;

    @Excel(name = "处理部门")
    private String handleDept;


    @Excel(name = "责任部门")
    private String dutyDept;

    @Excel(name = "根因分析")
    private String problemAnal;

    @Excel(name = "解决方案")
    private String solution;

    @Excel(name = "完成日期", format = "yyyy-MM-dd")
    private Date finishDate;

    /*@Excel(name = "创建时间", format = "yyyy-MM-dd")
    private Date createTime;*/


    @Excel(name = "是否删除", replace = {"否_0", "是_1"})
    private Integer isDeleted;

    public
    String getReqDateFmt(){
        reqDateFmt = WillDateUtil.unixTimeToStr(reqDate, "yyyy-MM-dd");
        return reqDateFmt;
    }


}

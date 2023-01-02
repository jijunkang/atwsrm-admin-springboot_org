package org.springblade.modules.report.entity;

import cn.afterturn.easypoi.excel.annotation.Excel;
import com.baomidou.mybatisplus.annotation.TableName;
import com.fasterxml.jackson.annotation.JsonFormat;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;
import lombok.EqualsAndHashCode;
import org.springblade.core.mp.base.BaseEntity;

import java.util.Date;


/**
 *  实体类
 *
 * @author Will
 */
@Data
@TableName("mould_management_whole")
@EqualsAndHashCode(callSuper = true)
@ApiModel(value = "MouldManagement对象", description = "")
public class MouldManagementWholeEntity extends BaseEntity {

	private static final long serialVersionUID = 1L;

    /**
     * 阀种
     */
    @Excel(name = "阀种")
    private String valveType;

    /**
     * 物料规格
     */
    @Excel(name = "物料规格")
    private String material;

    /**
     * 系列
     */
    @Excel(name = "系列")
    private String series;

    /**
     * 铸件供应商代码
     */
    @Excel(name = "铸件供应商代码")
    private String zjSupCode;

    /**
     * 模具供应商代码
     */
    @Excel(name = "模具供应商代码")
    private String mjSupCode;

    /**
     * 样品材质
     */
    @Excel(name = "样品材质")
    private String sampleMaterial;

    /**
     * 模具版本
     */
    @Excel(name = "模具版本")
    private String mjVersion;

    /**
     * 模具状态
     */
    @Excel(name = "模具状态")
    private String mjStatus;

    /**
     * 试样申请日期
     */
    @Excel(name = "试样申请日期")
    @JsonFormat(pattern = "yyyy-MM-dd")
    private Date sampleApplyDate;

    /**
     * pr日期
     */
    @Excel(name = "pr日期")
    @JsonFormat(pattern = "yyyy-MM-dd")
    private Date prDate;

    /**
     * 模具图纸上传日期
     */
    @Excel(name = "模具图纸上传日期")
    @JsonFormat(pattern = "yyyy-MM-dd")
    private Date mjPaperUpdate;

    /**
     * 计划模具po日期
     */
    @Excel(name = "计划模具po日期")
    @JsonFormat(pattern = "yyyy-MM-dd")
    private Date planDate1;

    /**
     * 实际模具po日期
     */
    @Excel(name = "系列")
    @JsonFormat(pattern = "yyyy-MM-dd")
    private Date realDate1;

    /**
     * 计划模具完成日期
     */
    @Excel(name = "计划模具完成日期")
    @JsonFormat(pattern = "yyyy-MM-dd")
    private Date planDate2;

    /**
     * 实际模具完成日期
     */
    @Excel(name = "实际模具完成日期")
    @JsonFormat(pattern = "yyyy-MM-dd")
    private Date realDate2;

    /**
     * 计划模具检验完成日期
     */
    @Excel(name = "计划模具检验完成日期")
    @JsonFormat(pattern = "yyyy-MM-dd")
    private Date planDate3;

    /**
     * 实际模具检验完成日期
     */
    @Excel(name = "实际模具检验完成日期")
    @JsonFormat(pattern = "yyyy-MM-dd")
    private Date realDate3;

    /**
     * 计划试样订单下达日期
     */
    @Excel(name = "计划试样订单下达日期")
    @JsonFormat(pattern = "yyyy-MM-dd")
    private Date planDate4;

    /**
     * 实际试样订单下达日期
     */
    @Excel(name = "实际试样订单下达日期")
    @JsonFormat(pattern = "yyyy-MM-dd")
    private Date realDate4;

    /**
     * 计划试样铸件完成日期
     */
    @Excel(name = "计划试样铸件完成日期")
    @JsonFormat(pattern = "yyyy-MM-dd")
    private Date planDate5;

    /**
     * 实际试样铸件完成日期
     */
    @Excel(name = "实际试样铸件完成日期")
    @JsonFormat(pattern = "yyyy-MM-dd")
    private Date realDate5;

    /**
     * 计划样品检验完成日期
     */
    @Excel(name = "计划样品检验完成日期")
    @JsonFormat(pattern = "yyyy-MM-dd")
    private Date planDate6;

    /**
     * 实际样品检验完成日期
     */
    @Excel(name = "实际样品检验完成日期")
    @JsonFormat(pattern = "yyyy-MM-dd")
    private Date realDate6;

    /**
     * 计划加工(厂内/厂外）完成日期
     */
    @Excel(name = "计划加工(厂内/厂外）完成日期")
    @JsonFormat(pattern = "yyyy-MM-dd")
    private Date planDate7;

    /**
     * 实际加工(厂内/厂外）完成日期
     */
    @Excel(name = "实际加工(厂内/厂外）完成日期")
    @JsonFormat(pattern = "yyyy-MM-dd")
    private Date realDate7;

    /**
     * 计划试装物料确认到位日期
     */
    @Excel(name = "计划试装物料确认到位日期")
    @JsonFormat(pattern = "yyyy-MM-dd")
    private Date planDate8;

    /**
     * 实际试装物料确认到位日期
     */
    @Excel(name = "实际试装物料确认到位日期")
    @JsonFormat(pattern = "yyyy-MM-dd")
    private Date realDate8;

    /**
     * 计划样品装配试压完成日期
     */
    @Excel(name = "计划样品装配试压完成日期")
    @JsonFormat(pattern = "yyyy-MM-dd")
    private Date planDate9;

    /**
     * 实际样品装配试压完成日期
     */
    @Excel(name = "实际样品装配试压完成日期")
    @JsonFormat(pattern = "yyyy-MM-dd")
    private Date realDate9;

    /**
     * 计划试样完成日期（品管签字）
     */
    @Excel(name = "计划试样完成日期（品管签字）")
    @JsonFormat(pattern = "yyyy-MM-dd")
    private Date planDate10;

    /**
     * 实际试样完成日期（品管签字）
     */
    @Excel(name = "实际试样完成日期（品管签字）")
    @JsonFormat(pattern = "yyyy-MM-dd")
    private Date realDate10;

    /**
     * 小批量验证
     */
    @Excel(name = "小批量验证")
    private String smallLotTest;

    /**
     * 成熟度
     */
    @Excel(name = "成熟度")
    private String maturity;

    /**
     * 优先级
     */
    @Excel(name = "优先级")
    private String priority;

    /**
     * 项目关联号
     */
    @Excel(name = "项目关联号")
    private String projectLinkNum;

    /**
     * 备注栏
     */
    @Excel(name = "备注栏")
    private String remark;

    /**
     * 当前状态栏
     */
    @Excel(name = "当前状态栏")
    private String currentState;

    /**
     * 问题
     */
    @Excel(name = "问题")
    private String problemDesc;

    /**
     * 对策
     */
    @Excel(name = "对策")
    private String solution;

    private String isFinish;

    private String isOverdue;

    private String creatimeStart;

    private String creatimeEnd;



}

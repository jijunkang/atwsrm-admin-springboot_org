package org.springblade.modules.ncr.entity;

import com.baomidou.mybatisplus.annotation.TableName;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;
import lombok.EqualsAndHashCode;
import org.springblade.core.mp.base.BaseEntity;


/**
 * @author libin
 *
 * @date 16:09 2020/8/4
 **/
@Data
@TableName("atw_ncr_measures")
@EqualsAndHashCode(callSuper = true)
@ApiModel(value = "Ncr对象", description = "")
public class NcrMeasuresEntity extends BaseEntity {

    private static final long serialVersionUID = 1L;

    /**
     * NCRID
     */
    @ApiModelProperty(value = "NCRID")
    private Long ncrId;
    /**
     * 类型 rectify = 纠正措施 / prevent=预防措施
     */
    @ApiModelProperty(value = "类型")
    private String type;
    /**
     * 部门类型 purch=采购部 / sup=供应商 / quality=质量部
     */
    @ApiModelProperty(value = "部门类型")
    private String deptType;
    /**
     * 部门名称
     */
    @ApiModelProperty(value = "部门名称")
    private String deptName;
    /**
     * 回复人
     */
    @ApiModelProperty(value = "回复人")
    private String replyUser;
    /**
     * 内容
     */
    @ApiModelProperty(value = "内容")
    private String content;


}

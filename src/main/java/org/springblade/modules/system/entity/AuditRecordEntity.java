package org.springblade.modules.system.entity;

import com.baomidou.mybatisplus.annotation.TableName;
import com.fasterxml.jackson.databind.annotation.JsonSerialize;
import com.fasterxml.jackson.databind.ser.std.ToStringSerializer;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;
import lombok.EqualsAndHashCode;
import org.springblade.core.mp.base.BaseEntity;



/**
 *  实体类
 *
 * @author Will
 */
@Data
@TableName("atw_audit_record")
@EqualsAndHashCode(callSuper = true)
@ApiModel(value = "AuditRecord对象", description = "")
public class AuditRecordEntity extends BaseEntity {

    private static final long serialVersionUID = 1L;

    /**
     * 审核人
     */
    @JsonSerialize(
        using = ToStringSerializer.class
    )
    @ApiModelProperty(value = "审核人")
    private Long auditUser;
    /**
     * 审核时间
     */
    @ApiModelProperty(value = "审核时间")
    private Long auditTime;
    /**
     * 审核状态
     */
    @ApiModelProperty(value = "审核状态")
    private Integer auditStatus;
    /**
     * 备注
     */
    @ApiModelProperty(value = "备注")
    private String remark;
    /**
     * 对象类型
     */
    @ApiModelProperty(value = "对象类型")
    private String objType;
    /**
     * 对象ID
     */
    @ApiModelProperty(value = "对象ID")
    private Long objId;

}

package org.springblade.modules.pr.entity;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableLogic;
import com.baomidou.mybatisplus.annotation.TableName;
import com.fasterxml.jackson.databind.annotation.JsonSerialize;
import com.fasterxml.jackson.databind.ser.std.ToStringSerializer;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;
import lombok.EqualsAndHashCode;
import org.springblade.core.mp.base.BaseEntity;

import java.io.Serializable;

@Data
@TableName("atw_sup_item_others")
@ApiModel(value = "其他小零件的供应商交叉表", description = "")
public class SupItemOthers implements Serializable {
    private static final long serialVersionUID = 1L;


    /**
     * 主键
     */
    @JsonSerialize(using = ToStringSerializer.class)
    @ApiModelProperty(value = "主键")
    @TableId(value = "id", type = IdType.ID_WORKER)
    private Long id;

    @ApiModelProperty(value = "类型名称")
    private String typeName;

    @ApiModelProperty(value = "材质")
    private String matQuality;

    @ApiModelProperty(value = "物料编码前六位")
    private String mainCode;

    @ApiModelProperty(value = "供应商编码")
    private String supCode;

    @ApiModelProperty(value = "供应商名称")
    private String supName;

    @ApiModelProperty(value = "状态 1:生效 0：不生效")
    private Integer status;

    @TableLogic
    @ApiModelProperty(value = "是否已删除")
    private Integer isDeleted;
}

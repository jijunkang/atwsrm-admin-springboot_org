package org.springblade.modules.pr.entity;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableId;
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
@TableName("atw_item_info_dj_res_fm")
@ApiModel(value = "锻件 - 阀帽余量表", description = "")
public class ItemInfoEntityResFMOfDJ implements Serializable {
    private static final long serialVersionUID = 1L;

    @ApiModelProperty(value = "id")
    private Long id;

    @ApiModelProperty(value = "供应商编码")
    private String supCode;

    @ApiModelProperty(value = "供应商名称")
    private String supName;

    @ApiModelProperty(value = "规格")
    private String standards;

    @ApiModelProperty(value = "材质")
    private String material;

    @ApiModelProperty(value = "外径余量")
    private String outerRemain;

    @ApiModelProperty(value = "内径余量")
    private String innerRemain;

    @ApiModelProperty(value = "高度余量")
    private String heightRemain;
}

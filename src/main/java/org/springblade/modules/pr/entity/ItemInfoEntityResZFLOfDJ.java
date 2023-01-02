package org.springblade.modules.pr.entity;

import com.baomidou.mybatisplus.annotation.TableName;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import java.io.Serializable;

@Data
@TableName("atw_item_info_dj_res_zfl")
@ApiModel(value = "锻件 - 中法兰余量表", description = "")
public class ItemInfoEntityResZFLOfDJ implements Serializable {
    private static final long serialVersionUID = 1L;

    @ApiModelProperty(value = "id")
    private Long id;

    @ApiModelProperty(value = "供应商编码")
    private String supCode;

    @ApiModelProperty(value = "供应商名称")
    private String supName;

    @ApiModelProperty(value = "材质")
    private String material;

    @ApiModelProperty(value = "外径范围")
    private String outerRange;

    @ApiModelProperty(value = "外径余量")
    private String outerRemain;

    @ApiModelProperty(value = "内径余量")
    private String innerRemain;

    @ApiModelProperty(value = "高度余量")
    private String heightRemain;
}

package org.springblade.modules.pr.entity;

import com.baomidou.mybatisplus.annotation.TableName;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import java.io.Serializable;

@Data
@ApiModel(value = "锻件 - 余量表", description = "")
public class ItemInfoEntityResALLOfDJ implements Serializable {
    private static final long serialVersionUID = 1L;

    @ApiModelProperty(value = "供应商编码")
    private String supCode;

    @ApiModelProperty(value = "供应商名称")
    private String supName;

    @ApiModelProperty(value = "外径余量")
    private String outerRemain;

    @ApiModelProperty(value = "内径余量")
    private String innerRemain;

    @ApiModelProperty(value = "高度余量")
    private String heightRemain;
}

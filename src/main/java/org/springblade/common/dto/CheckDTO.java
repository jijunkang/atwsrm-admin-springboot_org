package org.springblade.common.dto;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableId;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import java.io.Serializable;
import java.util.Date;

/**
 * 审核DTO
 * @author Will
 */
@Data
public
class CheckDTO implements Serializable{

    private static final long serialVersionUID = 1L;

    @ApiModelProperty("主键id")
    private Long id;

    @ApiModelProperty("ioId")
    private Long ioId;

    @ApiModelProperty("业务状态")
    private Integer status;

    @ApiModelProperty("时间戳")
    private Long date;

    @ApiModelProperty("备注")
    private String remark;

    @ApiModelProperty("附件")
    private String attach;

    @ApiModelProperty(value = "合同状态")
    private Integer contractStatus;

}

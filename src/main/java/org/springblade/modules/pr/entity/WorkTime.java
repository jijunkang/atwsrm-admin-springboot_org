package org.springblade.modules.pr.entity;

import com.baomidou.mybatisplus.annotation.TableName;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;
import lombok.EqualsAndHashCode;
import org.springblade.core.mp.base.BaseEntity;

@Data
public class WorkTime  {

    @ApiModelProperty(value = "料号")
    private String itemCode;

    @ApiModelProperty(value = "工序代码")
    private String workCode;

    @ApiModelProperty(value = "标准工时")
    private String pipTime;

    @ApiModelProperty(value = "准备工时")
    private String preTime;
}

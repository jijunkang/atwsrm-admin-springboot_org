package org.springblade.modules.po.vo;


import io.swagger.annotations.ApiModelProperty;
import lombok.Data;
import lombok.EqualsAndHashCode;
import org.springblade.modules.po.entity.PoItemCraftCtrlNodeEntity;


/**
 * @author libin
 * @date 16:36 2020/7/22
 **/
@Data
@EqualsAndHashCode(callSuper = true)
public class PoItemCraftCtrlNodeVO extends PoItemCraftCtrlNodeEntity {

    private static final long serialVersionUID = 1L;

    @ApiModelProperty(value = "卡控类型名称")
    private String name;

    @ApiModelProperty(value = "序号")
    private Integer sort;

    @ApiModelProperty(value = "工艺卡控类型名称")
    private String craftCtrlNodeName;
}

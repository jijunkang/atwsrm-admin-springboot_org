package org.springblade.modules.system.dto;

import cn.afterturn.easypoi.excel.annotation.Excel;
import cn.afterturn.easypoi.excel.annotation.ExcelIgnore;
import cn.afterturn.easypoi.excel.annotation.ExcelTarget;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import java.io.Serializable;
import java.util.List;

/**
 * @program: DictImportDTO
 * @description: Dict导入DTO
 * @author: yh
 * @create: 2019-12-05 09:52
 **/
@Data
public class DictImportDTO implements Serializable {
    private static final long serialVersionUID = 1L;

    @ApiModelProperty(value = "id")
    private Long id;

    /**
     * 父主键
     */
    @Excel(name = "父级ID")
    @ApiModelProperty(value = "父主键")
    private Long parentId;

    /**
     * 字典码
     */
    @Excel(name = "字典码")
    @ApiModelProperty(value = "字典码")
    private String code;

    /**
     * 字典值
     */
    @Excel(name = "字典值")
    @ApiModelProperty(value = "字典值")
    private String dictKey;

    /**
     * 字典名称
     */
    @Excel(name = "字典名称")
    @ApiModelProperty(value = "字典名称")
    private String dictValue;

    /**
     * 排序
     */
    @Excel(name = "排序")
    @ApiModelProperty(value = "排序")
    private Integer sort;

    /**
     * 字典备注
     */
    @Excel(name = "字典备注")
    @ApiModelProperty(value = "字典备注")
    private String remark;

    @ExcelIgnore
    private List<DictImportDTO> child;
}

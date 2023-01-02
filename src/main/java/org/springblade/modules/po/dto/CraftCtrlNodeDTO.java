package org.springblade.modules.po.dto;

import cn.afterturn.easypoi.excel.annotation.Excel;
import lombok.Data;

import java.io.Serializable;

/**
 * @author libin
 *
 * @date 17:27 2020/7/23
 **/
@Data
public class CraftCtrlNodeDTO implements Serializable {

    private static final long serialVersionUID = 1L;

    @Excel(name = "卡控节点类型名称")
    private String parentName;

    @Excel(name = "卡控节点序号")
    private Integer sort;

    @Excel(name = "卡控节点名称")
    private String childName;

    @Excel(name = "卡控节点备注")
    private String remark;

}

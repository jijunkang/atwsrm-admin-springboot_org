package org.springblade.modules.po.dto;

import lombok.Data;


/**
 * @author libin
 * @date 16:13 2020/8/31
 **/
@Data
public class PoOffsetViewValue {

    private static final long serialVersionUID = 1L;

    private String valueName;

    private String valueType;

    private String stringVal;

    private Long longVal1;

    private Long longVal2;

    private Integer intVal1;

    private Integer intVal2;

    private String traceCode;
}

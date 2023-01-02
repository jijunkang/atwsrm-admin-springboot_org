package org.springblade.modules.report.dto;


import io.swagger.annotations.ApiModel;
import lombok.Data;
import lombok.EqualsAndHashCode;
import org.springblade.modules.report.entity.SupplierOutputInfoEntity;


/**
 * 供应商产能分析 实体类供应商产能分析基础数据表 实体类
 */
@Data
@EqualsAndHashCode(callSuper = true)
@ApiModel(value = "供应商产能分析", description = "实体类供应商产能分析基础数据表")
public class SupplierOutputInfoDto extends SupplierOutputInfoEntity {

    private String weight;

    private String productionCapacity1;

    private String productionCapacity2;

    private String castingProcess;

    private String bottleneckProcesses;


    private String material_type;

    private String material_belong;

}

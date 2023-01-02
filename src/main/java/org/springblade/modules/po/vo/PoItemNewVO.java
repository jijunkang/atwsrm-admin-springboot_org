package org.springblade.modules.po.vo;

import cn.afterturn.easypoi.excel.annotation.Excel;
import com.fasterxml.jackson.databind.annotation.JsonSerialize;
import com.fasterxml.jackson.databind.ser.std.ToStringSerializer;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;
import lombok.EqualsAndHashCode;
import org.springblade.modules.po.entity.PoItemEntity;

import java.math.BigDecimal;
import java.util.List;

/**
 * 采购订单明细 模型VO
 *
 * @author Will
 */
@Data
@EqualsAndHashCode(callSuper = true)
public class PoItemNewVO extends PoItemEntity {

    private static final long serialVersionUID = 1L;
    private Integer poStatus;
}

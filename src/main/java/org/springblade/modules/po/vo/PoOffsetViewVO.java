package org.springblade.modules.po.vo;

import com.fasterxml.jackson.databind.annotation.JsonSerialize;
import com.fasterxml.jackson.databind.ser.std.ToStringSerializer;
import lombok.Data;
import lombok.EqualsAndHashCode;
import org.springblade.modules.po.entity.PoOffsetViewEntity;

import java.util.List;

/**
 *  模型VO
 *
 * @author Will
 */
@Data
@EqualsAndHashCode(callSuper = true)
public class PoOffsetViewVO extends PoOffsetViewEntity {

	private static final long serialVersionUID = 1L;

    List<PoItemCraftCtrlNodeVO> poItemCraftCtrlNodeVos;

    private String craftCtrlNodeName;

    @JsonSerialize(using = ToStringSerializer.class)
    private Long craftCtrlNodeId;

    private Integer nodeStatus;

}

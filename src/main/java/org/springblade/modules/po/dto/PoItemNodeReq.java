package org.springblade.modules.po.dto;

import com.fasterxml.jackson.databind.annotation.JsonSerialize;
import com.fasterxml.jackson.databind.ser.std.ToStringSerializer;
import lombok.Data;
import org.springblade.modules.po.vo.PoItemCraftCtrlNodeVO;

import java.util.List;

/**
 * @author libin
 * @date 15:18 2020/7/25
 **/
@Data
public class PoItemNodeReq {

    private static final long serialVersionUID = 1L;

    @JsonSerialize(using = ToStringSerializer.class)
    private Long id;

    @JsonSerialize(using = ToStringSerializer.class)
    private Long craftCtrlNodeId;

    List<PoItemCraftCtrlNodeVO> poItemCraftCtrlNodeVos;


}

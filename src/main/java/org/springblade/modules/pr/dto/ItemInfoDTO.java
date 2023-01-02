package org.springblade.modules.pr.dto;

import lombok.Data;
import org.springblade.modules.pr.vo.MaterialMaliyVO;
import org.springblade.modules.pr.vo.PriceVO;

import java.util.List;

@Data
public class ItemInfoDTO {
    private static final long serialVersionUID = 1L;

    private String id;

    private String itemCode;

    private String itemName;

    private String supColorType;

    private List<PriceVO> supAndPriceList;

    private List<MaterialMaliyVO> maliyVOS;

}

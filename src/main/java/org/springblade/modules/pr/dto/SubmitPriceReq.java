package org.springblade.modules.pr.dto;


import io.swagger.annotations.ApiModelProperty;
import lombok.Data;
import org.springblade.modules.mathmodel.entity.CastingOrderEntity;
import org.springblade.modules.mathmodel.entity.MailyMaterialTotalEntity;
import org.springblade.modules.outpr.dto.OutPrItemDTO;
import org.springblade.modules.pr.entity.*;
import org.springblade.modules.pr.vo.MaterialMaliyVO;

import java.util.List;

@Data
public class SubmitPriceReq {

    private List<SubmitPriceDTO> submitPriceDTOs;

    private List<U9PrDTO> u9PrDTOS;

    private List<OutPrItemDTO> outPrItemDTOS;

    private List<SupItemOthers>  supItemOthers;

    private List<ItemInfoEntityBasOfDJ> itemInfoEntityBasOfDJList;

    private List<ItemInfoEntityBasOfXLJ> itemInfoEntityBasOfXLJList;

    private List<ItemInfoEntityBasOfXLJRX> itemInfoEntityBasOfXLJRXList;

    //管棒料信息
    private List<MailyMaterialTotalEntity> mailyMaterialTotalEntityList;

    //铸件信息
    private List<CastingOrderEntity> castingOrderEntityList;

    //底轴信息
    private List<ItemInfoEntityBasOfDZ> itemInfoEntityBasOfDZList;

    @ApiModelProperty(value = "物料编号")
    private String itemCode;

    @ApiModelProperty(value = "物料名称")
    private String itemName;

    @ApiModelProperty(value = "供应商代码")
    private String supCode;

    @ApiModelProperty(value = "请购单号")
    private String prCode;

    @ApiModelProperty(value = "请购行号")
    private String prLn;
}

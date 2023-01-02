package org.springblade.modules.po.vo;

import lombok.Data;
import org.springblade.modules.po.dto.PoItemReqRepotCurrMonthDTO;
import org.springblade.modules.po.entity.PoItemEntity;

import java.util.List;

/**
 * @author libin
 *
 * @date 14:54 2020/10/21
 **/
@Data
public class PoItemNewReportVO {

    private static final long serialVersionUID = 1L;
    private String poCode;
    private Integer poLn;
    private String supCode;
    private String supName;
    private String itemCode;
    private String itemName;

    private Integer total;
    private String poCodeAndPoLn;
    private String proNo;
    private String type;
    private Integer delayQties;
    private List<PoItemReqRepotCurrMonthDTO> columnValues;

    NewReportColumnVO actualColumnVo;
    NewReportColumnVO predictColumnVo;

}

package org.springblade.modules.po.vo;

import lombok.Data;
import org.springblade.modules.po.dto.PoItemReqRepotCurrMonthDTO;
import org.springblade.modules.po.entity.PoItemEntity;

import java.util.List;

/**
 * @author libin
 *
 * @date 15:01 2020/10/21
 **/
@Data
public class NewReportColumnVO {

    private static final long serialVersionUID = 1L;

    private Integer total;
    private String poCodeAndPoLn;
    private String proNo;
    private String type;
    private List<PoItemReqRepotCurrMonthDTO> columnValues;


}

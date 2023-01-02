package org.springblade.modules.ap.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import org.springblade.modules.ap.entity.ApInvoiceEntity;

/**
 * @author libin
 *
 * @date 15:16 2020/6/3
 **/
public interface ApInvoiceMapper extends BaseMapper<ApInvoiceEntity> {

    String getInvoiceCodeByApId(Long id, String type);

    Long getLastInvoiceDate(Long id, String type);
}

package org.springblade.modules.ap.service;

import org.springblade.core.mp.base.BaseService;
import org.springblade.modules.ap.entity.ApInvoiceEntity;

import java.util.List;

/**
 * @author libin
 *
 * @date 15:14 2020/6/3
 **/
public interface IApInvoiceService extends BaseService<ApInvoiceEntity> {

    List<ApInvoiceEntity> getApInvoiceEntities(Long id, String type);

    String getInvoiceCodesByApId(Long id, String type);

    /**
     * 获取 最晚的发票日期
     * @param id
     * @param type
     * @return
     */
    Long getLastInvoiceDate(Long id, String type);
}

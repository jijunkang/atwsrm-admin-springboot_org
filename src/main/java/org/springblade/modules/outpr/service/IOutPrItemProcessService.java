package org.springblade.modules.outpr.service;

import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import org.springblade.core.mp.base.BaseService;
import org.springblade.core.mp.support.Query;
import org.springblade.modules.outpr.entity.OutPrItemProcessEntity;
import org.springblade.modules.pr.dto.PrReq;
import org.springblade.modules.pr.dto.U9PrDTO;

import java.util.List;

/**
 *  服务类
 *
 * @author Will
 */
public interface IOutPrItemProcessService extends BaseService<OutPrItemProcessEntity> {

    /**
     * 根据out_pr_item_id 查询
     * @param prItemId
     * @return
     */
    List<OutPrItemProcessEntity> getListByItemId(Long prItemId);

    IPage<OutPrItemProcessEntity> getPageByItemId(Query query, Long prItemId);

    OutPrItemProcessEntity getByProcessCode(Long itemPriceId, String processCode);
}

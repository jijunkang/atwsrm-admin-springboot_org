package org.springblade.modules.mathmodel.service;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import org.springblade.core.mp.base.BaseService;
import org.springblade.core.mp.support.Query;
import org.springblade.modules.mathmodel.entity.MmHotPriceEntity;
import org.springframework.web.multipart.MultipartFile;

import javax.servlet.http.HttpServletResponse;
import java.util.List;
import java.util.Map;

/**
 *  服务类
 *
 * @author Will
 */
public interface IMmHotPriceService extends BaseService<MmHotPriceEntity> {

    QueryWrapper<MmHotPriceEntity> getQueryWrapper(MmHotPriceEntity mmHotPriceEntity);

    void export(MmHotPriceEntity mmHotPriceEntity, Query query, HttpServletResponse response) throws Exception;

    Map<String, Object> importExcel(MultipartFile file) throws Exception;

    List<MmHotPriceEntity> getByHistoryId(Long id);

    boolean update(MmHotPriceEntity mmHotPriceEntity);

    MmHotPriceEntity getByMetalAndSupCode(String brand, String supCode);
}

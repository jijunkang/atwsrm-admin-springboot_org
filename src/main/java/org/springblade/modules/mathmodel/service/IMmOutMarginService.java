package org.springblade.modules.mathmodel.service;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import org.springblade.core.mp.base.BaseService;
import org.springblade.core.mp.support.Query;
import org.springblade.modules.mathmodel.entity.MmOutMarginEntity;
import org.springframework.web.multipart.MultipartFile;

import javax.servlet.http.HttpServletResponse;
import java.math.BigDecimal;
import java.util.List;
import java.util.Map;

/**
 *  服务类
 *
 * @author Will
 */
public interface IMmOutMarginService extends BaseService<MmOutMarginEntity> {

    QueryWrapper<MmOutMarginEntity> getQueryWrapper(MmOutMarginEntity mmOutMarginEntity);

    void export(MmOutMarginEntity mmOutMarginEntity, Query query, HttpServletResponse response) throws Exception;

    Map<String, Object> importExcel(MultipartFile file) throws Exception;

    List<MmOutMarginEntity> getByHistoryId(Long id);

    boolean update(MmOutMarginEntity mmOutMarginEntity);

    boolean updateMargins(List<MmOutMarginEntity> mmOutMarginEntities);

    MmOutMarginEntity getByChildCode(String childCode);

    MmOutMarginEntity getOutMargin(String childCode, BigDecimal out, BigDecimal hight);
}

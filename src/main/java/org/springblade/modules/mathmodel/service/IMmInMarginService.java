package org.springblade.modules.mathmodel.service;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import org.springblade.core.mp.base.BaseService;
import org.springblade.core.mp.support.Query;
import org.springblade.modules.mathmodel.entity.MmInMarginEntity;
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
public interface IMmInMarginService extends BaseService<MmInMarginEntity> {

    QueryWrapper<MmInMarginEntity> getQueryWrapper(MmInMarginEntity mmInMarginEntity);

    void export(MmInMarginEntity mmInMarginEntity, Query query, HttpServletResponse response) throws Exception;

    Map<String, Object> importExcel(MultipartFile file) throws Exception;

    List<MmInMarginEntity> getByHistoryId(Long id);

    boolean update(MmInMarginEntity mmInMarginEntity);

    boolean updateMargins(List<MmInMarginEntity> mmInMarginEntities);

    MmInMarginEntity getByChildCode(String childCode);

    MmInMarginEntity getInMargin(String childCode, BigDecimal hole, BigDecimal h1, BigDecimal h2);
}

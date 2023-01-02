package org.springblade.modules.mathmodel.service;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import org.springblade.core.mp.base.BaseService;
import org.springblade.core.mp.support.Query;
import org.springblade.modules.mathmodel.entity.MmCoefEntity;
import org.springframework.web.multipart.MultipartFile;

import javax.servlet.http.HttpServletResponse;
import java.util.List;
import java.util.Map;

/**
 *  服务类
 *
 * @author Will
 */
public interface IMmCoefService extends BaseService<MmCoefEntity> {

    QueryWrapper<MmCoefEntity> getQueryWrapper(MmCoefEntity mmCoefEntity);

    void export(MmCoefEntity mmCoefEntity, Query query, HttpServletResponse response) throws Exception;

    Map<String, Object> importExcel(MultipartFile file) throws Exception;

    List<MmCoefEntity> getByHistoryId(Long id);

    boolean update(MmCoefEntity mmCoefEntity);
}

package org.springblade.modules.mathmodel.service;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import org.springblade.core.mp.base.BaseService;
import org.springblade.core.mp.support.Query;
import org.springblade.modules.mathmodel.entity.MmDensityEntity;
import org.springframework.web.multipart.MultipartFile;

import javax.servlet.http.HttpServletResponse;
import java.util.List;
import java.util.Map;

/**
 *  服务类
 *
 * @author Will
 */
public interface IMmDensityService extends BaseService<MmDensityEntity> {

    QueryWrapper<MmDensityEntity> getQueryWrapper(MmDensityEntity mmDensityEntity);

    void export(MmDensityEntity mmDensityEntity, Query query, HttpServletResponse response) throws Exception;

    Map<String, Object> importExcel(MultipartFile file) throws Exception;

    List<MmDensityEntity> getByHistoryId(Long id);

    boolean update(MmDensityEntity mmDensityEntity);

    MmDensityEntity getByMetal(String metal);
}

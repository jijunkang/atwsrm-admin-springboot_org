package org.springblade.modules.mathmodel.service;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import org.springblade.core.mp.base.BaseService;
import org.springblade.core.mp.support.Query;
import org.springblade.modules.mathmodel.entity.MmProcessFeeEntity;
import org.springframework.web.multipart.MultipartFile;

import javax.servlet.http.HttpServletResponse;
import java.util.List;
import java.util.Map;

/**
 *  服务类
 *
 * @author Will
 */
public interface IMmProcessFeeService extends BaseService<MmProcessFeeEntity> {

    QueryWrapper<MmProcessFeeEntity> getQueryWrapper(MmProcessFeeEntity mmProcessFeeEntity);

    void export(MmProcessFeeEntity mmProcessFeeEntity, Query query, HttpServletResponse response) throws Exception;

    Map<String, Object> importExcel(MultipartFile file) throws Exception;

    List<MmProcessFeeEntity> getByHistoryId(Long id);

    boolean update(MmProcessFeeEntity mmProcessFeeEntity);
}

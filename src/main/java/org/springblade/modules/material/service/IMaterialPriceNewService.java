package org.springblade.modules.material.service;

import com.baomidou.mybatisplus.core.conditions.Wrapper;
import org.springblade.core.mp.base.BaseService;
import org.springblade.modules.material.dto.MaterialPriceNewDTO;
import org.springblade.modules.material.entity.MaterialPriceNewEntity;
import org.springframework.web.multipart.MultipartFile;

import javax.servlet.http.HttpServletResponse;
import java.math.BigDecimal;
import java.util.List;

/**
 *  服务类
 *
 * @author Will
 */
public interface IMaterialPriceNewService extends BaseService<MaterialPriceNewEntity> {

    Wrapper<MaterialPriceNewEntity> getQueryWrapper(MaterialPriceNewDTO materialPrice);

    boolean importExcel(MultipartFile file) throws RuntimeException;

    void exportExcel(MaterialPriceNewDTO dto, HttpServletResponse response) throws RuntimeException;

    boolean deleteById(List<Long> ids);

    boolean   passMateralPrice(List<MaterialPriceNewEntity> entityList);


}

package org.springblade.modules.material.service;

import com.baomidou.mybatisplus.core.conditions.Wrapper;
import org.springblade.core.mp.base.BaseService;
import org.springblade.modules.material.dto.MaterialPriceDTO;
import org.springblade.modules.material.entity.MaterialPriceEntity;
import org.springframework.web.multipart.MultipartFile;

import javax.servlet.http.HttpServletResponse;
import java.math.BigDecimal;

/**
 *  服务类
 *
 * @author Will
 */
public interface IMaterialPriceService extends BaseService<MaterialPriceEntity> {

    Wrapper<MaterialPriceEntity> getQueryWrapper(MaterialPriceDTO materialPrice);

    boolean importExcel(MultipartFile file) throws RuntimeException;

    void exportExcel(MaterialPriceDTO dto, HttpServletResponse response) throws RuntimeException;

    BigDecimal getPriceKg(String itemName);
}

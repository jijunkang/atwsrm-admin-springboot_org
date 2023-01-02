package org.springblade.modules.material.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.core.toolkit.Wrappers;
import com.google.api.client.util.Lists;
import org.springblade.common.utils.ExcelUtils;
import org.springblade.core.mp.base.BaseServiceImpl;
import org.springblade.core.tool.utils.BeanUtil;
import org.springblade.core.tool.utils.DateUtil;
import org.springblade.core.tool.utils.StringUtil;
import org.springblade.modules.material.dto.MaterialPriceDTO;
import org.springblade.modules.material.dto.MaterialPriceExcelDTO;
import org.springblade.modules.material.entity.MaterialPriceEntity;
import org.springblade.modules.material.mapper.MaterialPriceMapper;
import org.springblade.modules.material.service.IMaterialPriceService;
import org.springblade.modules.supplier.entity.Supplier;
import org.springblade.modules.supplier.service.ISupplierService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import javax.servlet.http.HttpServletResponse;
import java.math.BigDecimal;
import java.util.Date;
import java.util.List;

/**
 * 服务实现类
 * @author Will
 */
@Service
public
class MaterialPriceServiceImpl extends BaseServiceImpl<MaterialPriceMapper, MaterialPriceEntity> implements IMaterialPriceService{

    @Autowired
    ISupplierService supplierService;

    @Override
    public
    boolean save(MaterialPriceEntity entity){
        //【公斤单价】*【组间转换率】
        entity.setPriceMm( entity.getPriceKg().multiply(entity.getConverRate()));
        return super.save(entity);
    }

    @Override
    public
    QueryWrapper<MaterialPriceEntity> getQueryWrapper(MaterialPriceDTO materialPrice){
        return Wrappers.<MaterialPriceEntity>query().eq(materialPrice.getStatus() != null, "status", materialPrice.getStatus())
                                  .like(StringUtil.isNotBlank(materialPrice.getType()), "type", materialPrice.getType())
                                  .like(StringUtil.isNotBlank(materialPrice.getStd()), "std", materialPrice.getStd())
                                  .like(StringUtil.isNotBlank(materialPrice.getSpec()), "spec", materialPrice.getSpec())
                                  .like(StringUtil.isNotBlank(materialPrice.getMaterial()), "material", materialPrice.getMaterial())
                                  .like(StringUtil.isNotBlank(materialPrice.getSupCode()), "sup_code", materialPrice.getSupCode())
                                  .like(StringUtil.isNotBlank(materialPrice.getSupName()), "sup_name", materialPrice.getSupName())
                                  .eq(materialPrice.getIsEnable() != null, "is_enable", materialPrice.getIsEnable())
                                  .ge(materialPrice.getCreateTimeStart() != null, "create_time", materialPrice.getCreateTimeStart())
                                  .le(materialPrice.getCreateTimeEnd() != null, "create_time", materialPrice.getCreateTimeEnd());
    }


    @Override
    public
    boolean importExcel(MultipartFile file) throws RuntimeException{
        List<MaterialPriceExcelDTO> dtoList    = ExcelUtils.importExcel(file, 0, 1, MaterialPriceExcelDTO.class);
        List<MaterialPriceEntity>   entityList = BeanUtil.copy(dtoList, MaterialPriceEntity.class);
        for(MaterialPriceEntity entity : entityList){
            Supplier supplier = supplierService.getByCode(entity.getSupCode());
            if(supplier!=null){
                entity.setSupName(supplier.getName());
            }
            save(entity);
        }
        return true;
    }


    @Override
    public
    void exportExcel(MaterialPriceDTO dto, HttpServletResponse response) throws RuntimeException{
        QueryWrapper<MaterialPriceEntity> qw         = getQueryWrapper(dto);
        List<MaterialPriceEntity>         entityList = list(qw);
        if(entityList == null){
            throw new RuntimeException("暂无数据");
        }
        List<MaterialPriceExcelDTO> excelList = Lists.newArrayList();
        for(MaterialPriceEntity entity : entityList){
            MaterialPriceExcelDTO excelDTO = BeanUtil.copy(entity, MaterialPriceExcelDTO.class);
            excelList.add(excelDTO);
        }

        ExcelUtils.defaultExport(excelList, MaterialPriceExcelDTO.class, "原材料价格表" + DateUtil.formatDate(new Date()), response);

    }

    @Override
    public BigDecimal getPriceKg(String itemName) {
        return this.baseMapper.getPriceKg(itemName);
    }
}

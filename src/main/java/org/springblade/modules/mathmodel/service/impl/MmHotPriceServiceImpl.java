package org.springblade.modules.mathmodel.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.core.toolkit.Wrappers;
import org.springblade.common.utils.ExcelUtils;
import org.springblade.core.mp.base.BaseServiceImpl;
import org.springblade.core.mp.support.Condition;
import org.springblade.core.mp.support.Query;
import org.springblade.core.tool.utils.BeanUtil;
import org.springblade.core.tool.utils.DateUtil;
import org.springblade.core.tool.utils.StringUtil;
import org.springblade.modules.mathmodel.dto.MmHotPriceExcelDTO;
import org.springblade.modules.mathmodel.entity.MmHotPriceEntity;
import org.springblade.modules.mathmodel.mapper.MmHotPriceMapper;
import org.springblade.modules.mathmodel.service.IMmHotPriceService;
import org.springblade.modules.system.entity.User;
import org.springblade.modules.system.service.IUserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;

import javax.servlet.http.HttpServletResponse;
import java.util.*;

/**
 *  服务实现类
 *
 * @author Will
 */
@Service
public class MmHotPriceServiceImpl extends BaseServiceImpl<MmHotPriceMapper, MmHotPriceEntity> implements IMmHotPriceService {

    @Autowired
    IUserService userService;

    @Override
    public QueryWrapper<MmHotPriceEntity> getQueryWrapper(MmHotPriceEntity mmHotPriceEntity) {
        QueryWrapper<MmHotPriceEntity> queryWrapper = Condition.getQueryWrapper(new MmHotPriceEntity());
        queryWrapper.isNull("history_id");
        if(!StringUtil.isEmpty(mmHotPriceEntity.getMetal())){
            queryWrapper.like("metal", mmHotPriceEntity.getMetal());
        }
        if(!StringUtil.isEmpty(mmHotPriceEntity.getSupCode())){
            queryWrapper.like("sup_code", mmHotPriceEntity.getSupCode());
        }
        if(!StringUtil.isEmpty(mmHotPriceEntity.getSupName())){
            queryWrapper.like("sup_name", mmHotPriceEntity.getSupName());
        }
        return queryWrapper;
    }

    @Override
    public void export(MmHotPriceEntity mmHotPriceEntity, Query query, HttpServletResponse response) throws Exception {
        List<MmHotPriceEntity> list = list(getQueryWrapper(mmHotPriceEntity));
        if(list.size() == 0){
            throw new RuntimeException("暂无数据");
        }
        List<MmHotPriceExcelDTO> excels = new ArrayList<>();
        list.forEach(entity ->{
            MmHotPriceExcelDTO dto = BeanUtil.copy(entity, MmHotPriceExcelDTO.class);
            User user = new User();
            user.setId(entity.getCreateUser());
            String createUser = userService.getOne(Condition.getQueryWrapper(user)).getName();
            if(!StringUtil.isEmpty(entity.getUpdateUser())){
                user.setId(entity.getUpdateUser());
                String updateUser = userService.getOne(Condition.getQueryWrapper(user)).getName();
                dto.setUpdateUserFmt(updateUser);
            }
            dto.setCreateUserFmt(createUser);
            excels.add(dto);
        });
        ExcelUtils.defaultExport(excels, MmHotPriceExcelDTO.class,"热处理(飞削)单价表" + DateUtil.formatDate(new Date()), response);
    }

    @Override
    @Transactional(rollbackFor = {Exception.class, RuntimeException.class})
    public Map<String, Object> importExcel(MultipartFile file) throws Exception {
        List<MmHotPriceExcelDTO> dtos = ExcelUtils.importExcel(file, 0, 1, MmHotPriceExcelDTO.class);
        Map<String, Object> result = new HashMap<>(2);
        if (dtos.size() == 0) {
            result.put("msg", "暂无数据");
            result.put("flag", false);
            return result;
        }
        dtos.forEach(dto ->{
            MmHotPriceEntity mmHotPriceEntity = new MmHotPriceEntity();
            mmHotPriceEntity.setMetal(dto.getMetal());
            mmHotPriceEntity.setHotPrice(dto.getHotPrice());
            mmHotPriceEntity.setCutPrice(dto.getCutPrice());
            mmHotPriceEntity.setSupCode(dto.getSupCode());
            mmHotPriceEntity.setSupName(dto.getSupName());
            save(mmHotPriceEntity);
        });
        result.put("msg", "导入成功");
        result.put("flag", true);
        return result;
    }

    @Override
    public List<MmHotPriceEntity> getByHistoryId(Long id) {
        QueryWrapper<MmHotPriceEntity> queryWrapper = Condition.getQueryWrapper(new MmHotPriceEntity());
        queryWrapper.eq("history_id", id);
        queryWrapper.orderByDesc("create_time");
        return list(queryWrapper);
    }

    @Transactional(rollbackFor = Exception.class)
    @Override
    public boolean update(MmHotPriceEntity mmHotPriceEntity) {
        MmHotPriceEntity oldEntity = BeanUtil.copy(mmHotPriceEntity, MmHotPriceEntity.class);
        oldEntity.setHistoryId(null);
        MmHotPriceEntity newEntity = BeanUtil.copy(getById(mmHotPriceEntity.getId()), MmHotPriceEntity.class);
        newEntity.setId(null);
        newEntity.setHistoryId(oldEntity.getId());
        newEntity.setDeletedTime(new Date());
        updateById(oldEntity);
        save(newEntity);
        return true;
    }

    @Override
    public MmHotPriceEntity getByMetalAndSupCode(String metal, String supCode) {
        return getOne(Wrappers.<MmHotPriceEntity>query().eq("metal", metal).eq("sup_code", supCode).isNull("deleted_time"));
    }
}

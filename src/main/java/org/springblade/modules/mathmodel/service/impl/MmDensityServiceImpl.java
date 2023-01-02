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
import org.springblade.modules.mathmodel.dto.MmDensityExcelDTO;
import org.springblade.modules.mathmodel.entity.MmDensityEntity;
import org.springblade.modules.mathmodel.mapper.MmDensityMapper;
import org.springblade.modules.mathmodel.service.IMmDensityService;
import org.springblade.modules.system.entity.User;
import org.springblade.modules.system.service.IUserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;

import javax.servlet.http.HttpServletResponse;
import java.util.*;

/**
 * 服务实现类
 *
 * @author Will
 */
@Service
public class MmDensityServiceImpl extends BaseServiceImpl<MmDensityMapper, MmDensityEntity> implements IMmDensityService {

    @Autowired
    IUserService userService;

    @Override
    public QueryWrapper<MmDensityEntity> getQueryWrapper(MmDensityEntity mmDensityEntity) {
        QueryWrapper<MmDensityEntity> queryWrapper = Condition.getQueryWrapper(new MmDensityEntity());
        queryWrapper.isNull("history_id");
        if (!StringUtil.isEmpty(mmDensityEntity.getMetal())) {
            queryWrapper.like("metal", mmDensityEntity.getMetal());
        }
        return queryWrapper;
    }

    @Override
    public void export(MmDensityEntity mmDensityEntity, Query query, HttpServletResponse response) throws Exception {
        List<MmDensityEntity> list = list(getQueryWrapper(mmDensityEntity));
        if (list.size() == 0) {
            throw new RuntimeException("暂无数据");
        }
        List<MmDensityExcelDTO> excels = new ArrayList<>();
        list.forEach(entity -> {
            MmDensityExcelDTO dto = BeanUtil.copy(entity, MmDensityExcelDTO.class);
            User user = new User();
            user.setId(entity.getCreateUser());
            String createUser = userService.getOne(Condition.getQueryWrapper(user)).getName();
            if (!StringUtil.isEmpty(entity.getUpdateUser())) {
                user.setId(entity.getUpdateUser());
                String updateUser = userService.getOne(Condition.getQueryWrapper(user)).getName();
                dto.setUpdateUserFmt(updateUser);
            }
            dto.setCreateUserFmt(createUser);
            excels.add(dto);
        });
        ExcelUtils.defaultExport(excels, MmDensityExcelDTO.class, "密度表" + DateUtil.formatDate(new Date()), response);
    }

    @Override
    @Transactional(rollbackFor = {Exception.class, RuntimeException.class})
    public Map<String, Object> importExcel(MultipartFile file) throws Exception {
        List<MmDensityExcelDTO> dtos = ExcelUtils.importExcel(file, 0, 1, MmDensityExcelDTO.class);
        Map<String, Object> result = new HashMap<>(2);
        if (dtos.size() == 0) {
            result.put("msg", "暂无数据");
            result.put("flag", false);
            return result;
        }
        dtos.forEach(dto -> {
            if(getByMetal(dto.getMetal()) != null){
                throw new RuntimeException("该材质已存在：" + dto.getMetal());
            }
            MmDensityEntity mmDensityEntity = new MmDensityEntity();
            mmDensityEntity.setMetal(dto.getMetal());
            mmDensityEntity.setDensity(dto.getDensity());
            save(mmDensityEntity);
        });
        result.put("msg", "导入成功");
        result.put("flag", true);
        return result;
    }

    @Override
    public List<MmDensityEntity> getByHistoryId(Long id) {
        QueryWrapper<MmDensityEntity> queryWrapper = Condition.getQueryWrapper(new MmDensityEntity());
        queryWrapper.eq("history_id", id);
        queryWrapper.orderByDesc("create_time");
        return list(queryWrapper);
    }

    @Transactional(rollbackFor = Exception.class)
    @Override
    public boolean update(MmDensityEntity mmDensityEntity) {
        MmDensityEntity oldEntity = BeanUtil.copy(mmDensityEntity, MmDensityEntity.class);
        oldEntity.setHistoryId(null);
        MmDensityEntity newEntity = BeanUtil.copy(getById(mmDensityEntity.getId()), MmDensityEntity.class);
        newEntity.setId(null);
        newEntity.setHistoryId(oldEntity.getId());
        newEntity.setDeletedTime(new Date());
        updateById(oldEntity);
        save(newEntity);
        return true;
    }

    @Override
    public MmDensityEntity getByMetal(String metal) {
        return getOne(Wrappers.<MmDensityEntity>query().eq("metal",metal).isNull("deleted_time"));
    }
}

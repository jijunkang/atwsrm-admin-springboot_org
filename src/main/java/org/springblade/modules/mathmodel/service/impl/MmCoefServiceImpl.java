package org.springblade.modules.mathmodel.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import org.springblade.common.utils.ExcelUtils;
import org.springblade.core.mp.base.BaseServiceImpl;
import org.springblade.core.mp.support.Condition;
import org.springblade.core.mp.support.Query;
import org.springblade.core.tool.utils.BeanUtil;
import org.springblade.core.tool.utils.DateUtil;
import org.springblade.core.tool.utils.StringUtil;
import org.springblade.modules.mathmodel.dto.MmCoefExcelDTO;
import org.springblade.modules.mathmodel.entity.MmCoefEntity;
import org.springblade.modules.mathmodel.mapper.MmCoefMapper;
import org.springblade.modules.mathmodel.service.IMmCoefService;
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
public class MmCoefServiceImpl extends BaseServiceImpl<MmCoefMapper, MmCoefEntity> implements IMmCoefService {

    @Autowired
    IUserService userService;

    @Override
    public QueryWrapper<MmCoefEntity> getQueryWrapper(MmCoefEntity mmCoefEntity) {
        QueryWrapper<MmCoefEntity> queryWrapper = Condition.getQueryWrapper(new MmCoefEntity());
        queryWrapper.isNull("history_id");
        if (!StringUtil.isEmpty(mmCoefEntity.getMetal())) {
            queryWrapper.like("metal", mmCoefEntity.getMetal());
        }
        return queryWrapper;
    }


    @Override
    public void export(MmCoefEntity mmCoefEntity, Query query, HttpServletResponse response) throws Exception {
        List<MmCoefEntity> list = list(getQueryWrapper(mmCoefEntity));
        if (list.size() == 0) {
            throw new RuntimeException("暂无数据");
        }
        List<MmCoefExcelDTO> excels = new ArrayList<>();
        list.forEach(entity -> {
            MmCoefExcelDTO dto = BeanUtil.copy(entity, MmCoefExcelDTO.class);
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
        ExcelUtils.defaultExport(excels, MmCoefExcelDTO.class, "平铺系数表" + DateUtil.formatDate(new Date()), response);
    }

    @Override
    @Transactional(rollbackFor = {Exception.class, RuntimeException.class})
    public Map<String, Object> importExcel(MultipartFile file) throws Exception {
        List<MmCoefExcelDTO> dtos = ExcelUtils.importExcel(file, 0, 1, MmCoefExcelDTO.class);
        Map<String, Object> result = new HashMap<>(2);
        if (dtos.size() == 0) {
            result.put("msg", "暂无数据");
            result.put("flag", false);
            return result;
        }
        dtos.forEach(dto -> {
            MmCoefEntity mmCoefEntity = new MmCoefEntity();
            mmCoefEntity.setMetal(dto.getMetal());
            mmCoefEntity.setCoef(dto.getCoef());
            save(mmCoefEntity);
        });
        result.put("msg", "导入成功");
        result.put("flag", true);
        return result;
    }

    @Override
    public List<MmCoefEntity> getByHistoryId(Long id) {
        QueryWrapper<MmCoefEntity> queryWrapper = Condition.getQueryWrapper(new MmCoefEntity());
        queryWrapper.eq("history_id", id);
        queryWrapper.orderByDesc("create_time");
        return list(queryWrapper);
    }

    @Transactional(rollbackFor = Exception.class)
    @Override
    public boolean update(MmCoefEntity mmCoefEntity) {
        MmCoefEntity oldEntity = BeanUtil.copy(mmCoefEntity, MmCoefEntity.class);
        oldEntity.setHistoryId(null);
        MmCoefEntity newEntity = BeanUtil.copy(getById(mmCoefEntity.getId()), MmCoefEntity.class);
        newEntity.setId(null);
        newEntity.setHistoryId(oldEntity.getId());
        newEntity.setDeletedTime(new Date());
        updateById(oldEntity);
        save(newEntity);
        return true;
    }
}

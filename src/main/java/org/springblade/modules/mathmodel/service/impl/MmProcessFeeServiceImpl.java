package org.springblade.modules.mathmodel.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import org.springblade.common.utils.ExcelUtils;
import org.springblade.core.mp.base.BaseServiceImpl;
import org.springblade.core.mp.support.Condition;
import org.springblade.core.mp.support.Query;
import org.springblade.core.tool.utils.BeanUtil;
import org.springblade.core.tool.utils.DateUtil;
import org.springblade.core.tool.utils.StringUtil;
import org.springblade.modules.mathmodel.dto.MmProcessFeeExcelDTO;
import org.springblade.modules.mathmodel.entity.MmProcessFeeEntity;
import org.springblade.modules.mathmodel.mapper.MmProcessFeeMapper;
import org.springblade.modules.mathmodel.service.IMmProcessFeeService;
import org.springblade.modules.system.entity.User;
import org.springblade.modules.system.service.IDictBizService;
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
public class MmProcessFeeServiceImpl extends BaseServiceImpl<MmProcessFeeMapper, MmProcessFeeEntity> implements IMmProcessFeeService {

    @Autowired
    IDictBizService dictBizService;
    @Autowired
    IUserService userService;

    @Override
    public QueryWrapper<MmProcessFeeEntity> getQueryWrapper(MmProcessFeeEntity mmProcessFeeEntity) {
        QueryWrapper<MmProcessFeeEntity> queryWrapper = Condition.getQueryWrapper(new MmProcessFeeEntity());
        queryWrapper.isNull("history_id");
        if(!StringUtil.isEmpty(mmProcessFeeEntity.getMainCode())){
            queryWrapper.like("main_code", mmProcessFeeEntity.getMainCode());
        }
        if(!StringUtil.isEmpty(mmProcessFeeEntity.getItemCode())){
            queryWrapper.like("item_code", mmProcessFeeEntity.getItemCode());
        }
        if(!StringUtil.isEmpty(mmProcessFeeEntity.getItemName())){
            queryWrapper.like("item_name", mmProcessFeeEntity.getItemName());
        }
        if(!StringUtil.isEmpty(mmProcessFeeEntity.getMetal())){
            queryWrapper.like("metal", mmProcessFeeEntity.getMetal());
        }
        return queryWrapper;
    }

    @Override
    public void export(MmProcessFeeEntity mmProcessFeeEntity, Query query, HttpServletResponse response) throws Exception {
        List<MmProcessFeeEntity> list = list(getQueryWrapper(mmProcessFeeEntity));
        if(list.size() == 0){
            throw new RuntimeException("暂无数据");
        }
        List<MmProcessFeeExcelDTO> excels = new ArrayList<>();
        list.forEach(entity ->{
            MmProcessFeeExcelDTO dto = BeanUtil.copy(entity, MmProcessFeeExcelDTO.class);
            String mainCode = dictBizService.getValue("item_main_code", dto.getMainCode());
            User user = new User();
            user.setId(entity.getCreateUser());
            String createUser = userService.getOne(Condition.getQueryWrapper(user)).getName();
            if(!StringUtil.isEmpty(entity.getUpdateUser())){
                user.setId(entity.getUpdateUser());
                String updateUser = userService.getOne(Condition.getQueryWrapper(user)).getName();
                dto.setUpdateUserFmt(updateUser);
            }
            dto.setCreateUserFmt(createUser);
            if (!StringUtil.isEmpty(mainCode)) {
                dto.setMainCodeFmt(mainCode);
            }
            excels.add(dto);
        });
        ExcelUtils.defaultExport(excels, MmProcessFeeExcelDTO.class,"加工费表" + DateUtil.formatDate(new Date()), response);
    }

    @Override
    @Transactional(rollbackFor = {Exception.class, RuntimeException.class})
    public Map<String, Object> importExcel(MultipartFile file) throws Exception {
        List<MmProcessFeeExcelDTO> dtos = ExcelUtils.importExcel(file, 0, 1, MmProcessFeeExcelDTO.class);
        Map<String, Object> result = new HashMap<>(2);
        if (dtos.size() == 0) {
            result.put("msg", "暂无数据");
            result.put("flag", false);
            return result;
        }
        dtos.forEach(dto ->{
            MmProcessFeeEntity mmProcessFeeEntity = new MmProcessFeeEntity();
            mmProcessFeeEntity.setMainCode(dto.getMainCodeFmt());
            mmProcessFeeEntity.setItemCode(dto.getItemCode());
            mmProcessFeeEntity.setItemName(dto.getItemName());
            mmProcessFeeEntity.setMetal(dto.getMetal());
            mmProcessFeeEntity.setPrice(dto.getPrice());
            save(mmProcessFeeEntity);
        });
        result.put("msg", "导入成功");
        result.put("flag", true);
        return result;
    }

    @Override
    public List<MmProcessFeeEntity> getByHistoryId(Long id) {
        QueryWrapper<MmProcessFeeEntity> queryWrapper = Condition.getQueryWrapper(new MmProcessFeeEntity());
        queryWrapper.eq("history_id", id);
        queryWrapper.orderByDesc("create_time");
        return list(queryWrapper);
    }

    @Transactional(rollbackFor = Exception.class)
    @Override
    public boolean update(MmProcessFeeEntity mmProcessFeeEntity) {
        MmProcessFeeEntity oldEntity = BeanUtil.copy(mmProcessFeeEntity, MmProcessFeeEntity.class);
        oldEntity.setHistoryId(null);
        MmProcessFeeEntity newEntity = BeanUtil.copy(getById(mmProcessFeeEntity.getId()), MmProcessFeeEntity.class);
        newEntity.setId(null);
        newEntity.setHistoryId(oldEntity.getId());
        newEntity.setDeletedTime(new Date());
        updateById(oldEntity);
        save(newEntity);
        return true;
    }
}

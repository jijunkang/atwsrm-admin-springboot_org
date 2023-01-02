package org.springblade.modules.mathmodel.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import org.springblade.common.utils.ExcelUtils;
import org.springblade.core.mp.base.BaseServiceImpl;
import org.springblade.core.mp.support.Condition;
import org.springblade.core.mp.support.Query;
import org.springblade.core.tool.utils.BeanUtil;
import org.springblade.core.tool.utils.DateUtil;
import org.springblade.core.tool.utils.StringUtil;
import org.springblade.modules.mathmodel.dto.MmSizeExcelDTO;
import org.springblade.modules.mathmodel.entity.MmSizeEntity;
import org.springblade.modules.mathmodel.mapper.MmSizeMapper;
import org.springblade.modules.mathmodel.service.IMmSizeService;
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
 * 服务实现类
 *
 * @author Will
 */
@Service
public class MmSizeServiceImpl extends BaseServiceImpl<MmSizeMapper, MmSizeEntity> implements IMmSizeService {

    @Autowired
    IDictBizService dictBizService;
    @Autowired
    IUserService userService;

    @Override
    public QueryWrapper<MmSizeEntity> getQueryWrapper(MmSizeEntity mmSizeEntity) {
        QueryWrapper<MmSizeEntity> queryWrapper = Condition.getQueryWrapper(new MmSizeEntity());
        queryWrapper.isNull("history_id");
        if (!StringUtil.isEmpty(mmSizeEntity.getMainCode())) {
            queryWrapper.like("main_code", mmSizeEntity.getMainCode());
        }
        if (!StringUtil.isEmpty(mmSizeEntity.getChildCode())) {
            queryWrapper.like("child_code", mmSizeEntity.getChildCode());
        }
        if (!StringUtil.isEmpty(mmSizeEntity.getItemCode())) {
            queryWrapper.like("item_code", mmSizeEntity.getItemCode());
        }
        if (!StringUtil.isEmpty(mmSizeEntity.getItemName())) {
            queryWrapper.like("item_name", mmSizeEntity.getItemName());
        }
        if (!StringUtil.isEmpty(mmSizeEntity.getMetal())) {
            queryWrapper.like("metal", mmSizeEntity.getMetal());
        }
        return queryWrapper;
    }

    @Override
    public void export(MmSizeEntity mmSizeEntity, Query query, HttpServletResponse response) throws Exception {
        List<MmSizeEntity> list = list(getQueryWrapper(mmSizeEntity));
        if (list.size() == 0) {
            throw new RuntimeException("暂无数据");
        }
        List<MmSizeExcelDTO> excels = new ArrayList<>();
        list.forEach(entity -> {
            MmSizeExcelDTO dto = BeanUtil.copy(entity, MmSizeExcelDTO.class);
            String mainCode = dictBizService.getValue("item_main_code", dto.getMainCode());
            User user = new User();
            user.setId(entity.getCreateUser());
            String createUser = userService.getOne(Condition.getQueryWrapper(user)).getName();
            if (!StringUtil.isEmpty(entity.getUpdateUser())) {
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
        ExcelUtils.defaultExport(excels, MmSizeExcelDTO.class, "物料尺寸表" + DateUtil.formatDate(new Date()), response);
    }

    @Override
    @Transactional(rollbackFor = {Exception.class, RuntimeException.class})
    public Map<String, Object> importExcel(MultipartFile file) throws Exception {
        List<MmSizeExcelDTO> dtos = ExcelUtils.importExcel(file, 0, 1, MmSizeExcelDTO.class);
        Map<String, Object> result = new HashMap<>(2);
        if (dtos.size() == 0) {
            result.put("msg", "暂无数据");
            result.put("flag", false);
            return result;
        }
        dtos.forEach(dto -> {
            if(getByItemCode(dto.getItemCode()) != null){
                throw new RuntimeException("该料号已存在：" + dto.getItemCode());
            }
            MmSizeEntity mmSizeEntity = new MmSizeEntity();
            mmSizeEntity.setMainCode(dto.getMainCodeFmt());
            mmSizeEntity.setChildCode(dto.getChildCode());
            mmSizeEntity.setItemCode(dto.getItemCode());
            mmSizeEntity.setItemName(dto.getItemName());
            mmSizeEntity.setMetal(dto.getMetal());
            mmSizeEntity.setHole(dto.getHole());
            mmSizeEntity.setH1(dto.getH1());
            mmSizeEntity.setH2(dto.getH2());
            mmSizeEntity.setOutD1(dto.getOutD1());
            mmSizeEntity.setOutD2(dto.getOutD2());
            //规格
            mmSizeEntity.setSpec(dto.getSpec());
            save(mmSizeEntity);
        });
        result.put("msg", "导入成功");
        result.put("flag", true);
        return result;
    }

    @Override
    public List<MmSizeEntity> getByHistoryId(Long id) {
        QueryWrapper<MmSizeEntity> queryWrapper = Condition.getQueryWrapper(new MmSizeEntity());
        queryWrapper.eq("history_id", id);
        queryWrapper.orderByDesc("create_time");
        return list(queryWrapper);
    }

    @Transactional(rollbackFor = Exception.class)
    @Override
    public boolean update(MmSizeEntity mmSize) {
        MmSizeEntity oldEntity = BeanUtil.copy(mmSize, MmSizeEntity.class);
        oldEntity.setHistoryId(null);
        MmSizeEntity newEntity = BeanUtil.copy(getById(mmSize.getId()), MmSizeEntity.class);
        newEntity.setId(null);
        newEntity.setHistoryId(oldEntity.getId());
        newEntity.setDeletedTime(new Date());
        updateById(oldEntity);
        save(newEntity);
        return true;
    }

    @Override
    public MmSizeEntity getByItemCode(String itemCode) {
        MmSizeEntity mmSizeEntity = new MmSizeEntity();
        mmSizeEntity.setItemCode(itemCode);
        return getOne(getQueryWrapper(mmSizeEntity).isNull("history_id"));
    }


}

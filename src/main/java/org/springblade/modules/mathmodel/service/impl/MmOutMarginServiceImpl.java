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
import org.springblade.modules.mathmodel.dto.MmOutMarginExcelDTO;
import org.springblade.modules.mathmodel.entity.MmOutMarginEntity;
import org.springblade.modules.mathmodel.mapper.MmOutMarginMapper;
import org.springblade.modules.mathmodel.service.IMmOutMarginService;
import org.springblade.modules.system.entity.User;
import org.springblade.modules.system.service.IDictBizService;
import org.springblade.modules.system.service.IUserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;

import javax.servlet.http.HttpServletResponse;
import java.math.BigDecimal;
import java.util.*;

/**
 *  服务实现类
 *
 * @author Will
 */
@Service
public class MmOutMarginServiceImpl extends BaseServiceImpl<MmOutMarginMapper, MmOutMarginEntity> implements IMmOutMarginService {

    @Autowired
    IDictBizService dictBizService;
    @Autowired
    IUserService userService;


    @Override
    public QueryWrapper<MmOutMarginEntity> getQueryWrapper(MmOutMarginEntity mmOutMarginEntity) {
        QueryWrapper<MmOutMarginEntity> queryWrapper = Condition.getQueryWrapper(new MmOutMarginEntity());
        queryWrapper.isNull("history_id");
        if(!StringUtil.isEmpty(mmOutMarginEntity.getMainCode())){
            queryWrapper.like("main_code", mmOutMarginEntity.getMainCode());
        }
        if(!StringUtil.isEmpty(mmOutMarginEntity.getChildCode())){
            queryWrapper.like("child_code", mmOutMarginEntity.getChildCode());
        }
        return queryWrapper;
    }

    @Override
    public void export(MmOutMarginEntity mmOutMarginEntity, Query query, HttpServletResponse response) throws Exception {
        List<MmOutMarginEntity> list = list(getQueryWrapper(mmOutMarginEntity));
        if(list.size() == 0){
            throw new RuntimeException("暂无数据");
        }
        List<MmOutMarginExcelDTO> excels = new ArrayList<>();
        list.forEach(entity ->{
            MmOutMarginExcelDTO dto = BeanUtil.copy(entity, MmOutMarginExcelDTO.class);
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
        ExcelUtils.defaultExport(excels, MmOutMarginExcelDTO.class,"外圆高度余量表" + DateUtil.formatDate(new Date()), response);
    }

    @Override
    @Transactional(rollbackFor = {Exception.class, RuntimeException.class})
    public Map<String, Object> importExcel(MultipartFile file) throws Exception {
        List<MmOutMarginExcelDTO> dtos = ExcelUtils.importExcel(file, 0, 1, MmOutMarginExcelDTO.class);
        Map<String, Object> result = new HashMap<>(2);
        if (dtos.size() == 0) {
            result.put("msg", "暂无数据");
            result.put("flag", false);
            return result;
        }
        dtos.forEach(dto ->{
            if(getByChildCode(dto.getChildCode()) != null){
                throw new RuntimeException("该子分类已存在：" + dto.getChildCode());
            }
            MmOutMarginEntity mmOutMarginEntity = new MmOutMarginEntity();
            mmOutMarginEntity.setMainCode(dto.getMainCodeFmt());
            mmOutMarginEntity.setHightBig(dto.getHightBig());
            mmOutMarginEntity.setHightSmall(dto.getHightSmall());
            mmOutMarginEntity.setOutBig(dto.getOutBig());
            mmOutMarginEntity.setOutSmall(dto.getOutSmall());
            mmOutMarginEntity.setMargin(dto.getMargin());
            mmOutMarginEntity.setChildCode(dto.getChildCode());
            save(mmOutMarginEntity);
        });
        result.put("msg", "导入成功");
        result.put("flag", true);
        return result;
    }

    @Override
    public List<MmOutMarginEntity> getByHistoryId(Long id) {
        QueryWrapper<MmOutMarginEntity> queryWrapper = Condition.getQueryWrapper(new MmOutMarginEntity());
        queryWrapper.eq("history_id", id);
        queryWrapper.orderByDesc("create_time");
        return list(queryWrapper);
    }

    @Transactional(rollbackFor = Exception.class)
    @Override
    public boolean update(MmOutMarginEntity mmOutMarginEntity) {
        MmOutMarginEntity oldEntity = BeanUtil.copy(mmOutMarginEntity, MmOutMarginEntity.class);
        oldEntity.setHistoryId(null);
        MmOutMarginEntity newEntity = BeanUtil.copy(getById(mmOutMarginEntity.getId()), MmOutMarginEntity.class);
        newEntity.setId(null);
        newEntity.setHistoryId(oldEntity.getId());
        newEntity.setDeletedTime(new Date());
        updateById(oldEntity);
        save(newEntity);
        return true;
    }

    @Transactional(rollbackFor = Exception.class)
    @Override
    public boolean updateMargins(List<MmOutMarginEntity> mmOutMarginEntities) {
        if (mmOutMarginEntities.size() == 1 && StringUtil.isEmpty(mmOutMarginEntities.get(0).getId())) {
            //修改全部
            list(getQueryWrapper(new MmOutMarginEntity())).forEach(temp -> {
                temp.setMargin(temp.getMargin().add(mmOutMarginEntities.get(0).getMargin()));
                if(temp.getMargin().compareTo(new BigDecimal("0")) < 0){
                    throw new RuntimeException("余量小于0");
                }
                update(temp);
            });
        } else {
            //修改指定
            mmOutMarginEntities.forEach(temp -> {
                MmOutMarginEntity entity = getById(temp.getId());
                entity.setMargin(temp.getMargin().add(entity.getMargin()));
                if(entity.getMargin().compareTo(new BigDecimal("0")) < 0){
                    throw new RuntimeException("余量小于0");
                }
                update(entity);
            });
        }
        return true;
    }

    @Override
    public MmOutMarginEntity getByChildCode(String childCode) {
        return getOne(Wrappers.<MmOutMarginEntity>query().eq("child_code", childCode).isNull("deleted_time"));
    }

    @Override
    public MmOutMarginEntity getOutMargin(String childCode, BigDecimal out, BigDecimal hight) {
        QueryWrapper<MmOutMarginEntity> queryWrapper = Condition.getQueryWrapper(new MmOutMarginEntity());
        queryWrapper.isNull("deleted_time");
        queryWrapper.eq("child_code", childCode);
        queryWrapper.le("out_small", out);
        queryWrapper.gt("out_big", out);
        queryWrapper.le("hight_small", hight);
        queryWrapper.gt("hight_big", hight);
        return getOne(queryWrapper);
    }

}

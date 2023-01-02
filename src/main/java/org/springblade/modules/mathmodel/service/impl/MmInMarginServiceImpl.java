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
import org.springblade.modules.mathmodel.dto.MmInMarginExcelDTO;
import org.springblade.modules.mathmodel.entity.MmInMarginEntity;
import org.springblade.modules.mathmodel.mapper.MmInMarginMapper;
import org.springblade.modules.mathmodel.service.IMmInMarginService;
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
 * 服务实现类
 *
 * @author Will
 */
@Service
public class MmInMarginServiceImpl extends BaseServiceImpl<MmInMarginMapper, MmInMarginEntity> implements IMmInMarginService {

    @Autowired
    IDictBizService dictBizService;
    @Autowired
    IUserService userService;

    @Override
    public QueryWrapper<MmInMarginEntity> getQueryWrapper(MmInMarginEntity mmInMarginEntity) {
        QueryWrapper<MmInMarginEntity> queryWrapper = Condition.getQueryWrapper(new MmInMarginEntity());
        queryWrapper.isNull("history_id");
        if (!StringUtil.isEmpty(mmInMarginEntity.getMainCode())) {
            queryWrapper.like("main_code", mmInMarginEntity.getMainCode());
        }
        if (!StringUtil.isEmpty(mmInMarginEntity.getChildCode())) {
            queryWrapper.like("child_code", mmInMarginEntity.getChildCode());
        }
        return queryWrapper;
    }

    @Override
    public void export(MmInMarginEntity mmInMarginEntity, Query query, HttpServletResponse response) throws Exception {
        List<MmInMarginEntity> list = list(getQueryWrapper(mmInMarginEntity));
        if (list.size() == 0) {
            throw new RuntimeException("暂无数据");
        }
        List<MmInMarginExcelDTO> excels = new ArrayList<>();
        list.forEach(entity -> {
            MmInMarginExcelDTO dto = BeanUtil.copy(entity, MmInMarginExcelDTO.class);
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
        ExcelUtils.defaultExport(excels, MmInMarginExcelDTO.class, "内圆余量表" + DateUtil.formatDate(new Date()), response);
    }

    @Override
    @Transactional(rollbackFor = {Exception.class, RuntimeException.class})
    public Map<String, Object> importExcel(MultipartFile file) throws Exception {
        List<MmInMarginExcelDTO> dtos = ExcelUtils.importExcel(file, 0, 1, MmInMarginExcelDTO.class);
        Map<String, Object> result = new HashMap<>(2);
        if (dtos.size() == 0) {
            result.put("msg", "暂无数据");
            result.put("flag", false);
            return result;
        }
        dtos.forEach(dto -> {
            if(getByChildCode(dto.getChildCode()) != null){
                throw new RuntimeException("改子分类已存在：" + dto.getChildCode());
            }
            MmInMarginEntity mmInMarginEntity = new MmInMarginEntity();
            mmInMarginEntity.setMainCode(dto.getMainCodeFmt());
            mmInMarginEntity.setInSmall(dto.getInSmall());
            mmInMarginEntity.setInBig(dto.getInBig());
            mmInMarginEntity.setHightSmall(dto.getHightSmall());
            mmInMarginEntity.setHightBig(dto.getHightBig());
            mmInMarginEntity.setMargin(dto.getMargin());
            save(mmInMarginEntity);
        });
        result.put("msg", "导入成功");
        result.put("flag", true);
        return result;
    }

    @Override
    public List<MmInMarginEntity> getByHistoryId(Long id) {
        QueryWrapper<MmInMarginEntity> queryWrapper = Condition.getQueryWrapper(new MmInMarginEntity());
        queryWrapper.eq("history_id", id);
        queryWrapper.orderByDesc("create_time");
        return list(queryWrapper);
    }

    @Transactional(rollbackFor = Exception.class)
    @Override
    public boolean update(MmInMarginEntity mmInMarginEntity) {
        MmInMarginEntity oldEntity = BeanUtil.copy(mmInMarginEntity, MmInMarginEntity.class);
        oldEntity.setHistoryId(null);
        MmInMarginEntity newEntity = BeanUtil.copy(getById(mmInMarginEntity.getId()), MmInMarginEntity.class);
        newEntity.setId(null);
        newEntity.setHistoryId(oldEntity.getId());
        newEntity.setDeletedTime(new Date());
        updateById(oldEntity);
        save(newEntity);
        return true;
    }

    @Transactional(rollbackFor = Exception.class)
    @Override
    public boolean updateMargins(List<MmInMarginEntity> mmInMarginEntities) {
        if (mmInMarginEntities.size() == 1 && StringUtil.isEmpty(mmInMarginEntities.get(0).getId())) {
            //修改全部
            list(getQueryWrapper(new MmInMarginEntity())).forEach(temp -> {
                temp.setMargin(temp.getMargin().add(mmInMarginEntities.get(0).getMargin()));
                if(temp.getMargin().compareTo(new BigDecimal("0")) < 0){
                    throw new RuntimeException("余量小于0");
                }
                update(temp);
            });
        } else {
            //修改指定
            mmInMarginEntities.forEach(temp -> {
                MmInMarginEntity entity = getById(temp.getId());
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
    public MmInMarginEntity getByChildCode(String childCode) {
        return getOne(Wrappers.<MmInMarginEntity>query().eq("child_code", childCode).isNull("deleted_time"));
    }

    @Override
    public MmInMarginEntity getInMargin(String childCode, BigDecimal hole, BigDecimal h1, BigDecimal h2) {
        QueryWrapper<MmInMarginEntity> queryWrapper = Condition.getQueryWrapper(new MmInMarginEntity());
        queryWrapper.isNull("deleted_time");
        queryWrapper.eq("child_code", childCode);
        queryWrapper.le("in_small", hole);
        queryWrapper.gt("in_big", hole);
        queryWrapper.le("hight_small", h1.add(h2));
        queryWrapper.gt("hight_big", h1.add(h2));
        return getOne(queryWrapper);
    }
}

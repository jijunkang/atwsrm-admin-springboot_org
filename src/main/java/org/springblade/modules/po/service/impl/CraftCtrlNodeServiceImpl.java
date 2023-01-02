package org.springblade.modules.po.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.core.toolkit.Wrappers;
import com.baomidou.mybatisplus.extension.exceptions.ApiException;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import org.springblade.common.utils.ExcelUtils;
import org.springblade.core.mp.support.Condition;
import org.springblade.core.mp.support.Query;
import org.springblade.core.tool.constant.BladeConstant;
import org.springblade.core.tool.node.ForestNodeMerger;
import org.springblade.core.tool.utils.DateUtil;
import org.springblade.core.tool.utils.Func;
import org.springblade.core.tool.utils.StringPool;
import org.springblade.core.tool.utils.StringUtil;
import org.springblade.modules.po.dto.CraftCtrlNodeDTO;
import org.springblade.modules.po.dto.PoItemDTO;
import org.springblade.modules.po.entity.CraftCtrlNodeEntity;
import org.springblade.modules.po.entity.PoItemCraftCtrlNodeEntity;
import org.springblade.modules.po.entity.PoItemEntity;
import org.springblade.modules.po.mapper.CraftCtrlNodeMapper;
import org.springblade.modules.po.service.ICraftCtrlNodeService;
import org.springblade.modules.po.service.IPoItemCraftCtrlNodeService;
import org.springblade.modules.po.service.IPoItemService;
import org.springblade.modules.po.vo.CraftCtrlNodeVO;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Lazy;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;

import javax.servlet.http.HttpServletResponse;
import java.util.*;


/**
 * @author libin
 * @date 11:49 2020/7/22
 **/
@Service
public class CraftCtrlNodeServiceImpl extends ServiceImpl<CraftCtrlNodeMapper, CraftCtrlNodeEntity> implements ICraftCtrlNodeService {

    @Autowired
    IPoItemCraftCtrlNodeService poItemCraftCtrlNodeService;

    @Autowired
    @Lazy
    IPoItemService poItemService;


    @Override
    public List<CraftCtrlNodeVO> tree() {
        return ForestNodeMerger.merge(baseMapper.tree());
    }

    @Override
    public String getValue(String code, String name) {
        return Func.toStr(baseMapper.getValue(code, name), StringPool.EMPTY);
    }

    @Override
    public List<CraftCtrlNodeEntity> getList(String code) {
        return baseMapper.getList(code);
    }

    @Transactional(rollbackFor = Exception.class)
    @Override
    public boolean submit(CraftCtrlNodeEntity craftCtrlNodeEntity) {
        QueryWrapper<CraftCtrlNodeEntity> queryWrapper = Condition.getQueryWrapper(new CraftCtrlNodeEntity());
        if (Func.isEmpty(craftCtrlNodeEntity.getParentId())) {
            //父新增
            if (!StringUtil.isEmpty(getByName(craftCtrlNodeEntity.getName()))) {
                throw new RuntimeException("该卡控类型已存在");
            }
            craftCtrlNodeEntity.setParentId(BladeConstant.TOP_PARENT_ID);
            craftCtrlNodeEntity.setSort(10);
            craftCtrlNodeEntity.setCode(genCode());
            craftCtrlNodeEntity.setIsDeleted(BladeConstant.DB_NOT_DELETED);
            return save(craftCtrlNodeEntity);
        } else {
            //子新增
            queryWrapper.eq("parent_id", craftCtrlNodeEntity.getParentId());
            queryWrapper.eq("sort", craftCtrlNodeEntity.getSort());
            if (!StringUtil.isEmpty(getOne(queryWrapper))) {
                throw new RuntimeException("该卡控节点序号已存在");
            }
            craftCtrlNodeEntity.setCode(genCode());
            craftCtrlNodeEntity.setIsDeleted(BladeConstant.DB_NOT_DELETED);
            save(craftCtrlNodeEntity);
            //更新 交付中心-工艺卡控
            PoItemDTO poItemDTO = new PoItemDTO();
            poItemDTO.setCraftCtrlCode(getById(craftCtrlNodeEntity.getParentId()).getCode());
            List<PoItemEntity> items = poItemService.getPoItemEntity(poItemDTO);
            items.forEach(temp ->{
                PoItemCraftCtrlNodeEntity poItemCraftCtrlNodeEntity = new PoItemCraftCtrlNodeEntity();
                poItemCraftCtrlNodeEntity.setPoItemId(temp.getId());
                poItemCraftCtrlNodeEntity.setCcnodeParentId(craftCtrlNodeEntity.getParentId());
                poItemCraftCtrlNodeEntity.setCcnodeChildId(craftCtrlNodeEntity.getId());
                poItemCraftCtrlNodeService.save(poItemCraftCtrlNodeEntity);
            });
            return true;
        }
    }

    @Override
    public boolean update(CraftCtrlNodeEntity craftCtrlNodeEntity) {
        QueryWrapper<CraftCtrlNodeEntity> queryWrapper = Condition.getQueryWrapper(new CraftCtrlNodeEntity());
        if (Func.isEmpty(craftCtrlNodeEntity.getParentId())) {
            if (!StringUtil.isEmpty(getByName(craftCtrlNodeEntity.getName()))) {
                throw new RuntimeException("该卡控类型已存在");
            }
            return updateById(craftCtrlNodeEntity);
        }else{
            CraftCtrlNodeEntity child = getById(craftCtrlNodeEntity.getId());
            if(!craftCtrlNodeEntity.getSort().equals(child.getSort())){
                queryWrapper.eq("parent_id", craftCtrlNodeEntity.getParentId());
                queryWrapper.eq("sort", craftCtrlNodeEntity.getSort());
                if (!StringUtil.isEmpty(getOne(queryWrapper))) {
                    throw new RuntimeException("该卡控节点序号已存在");
                }
                child.setSort(craftCtrlNodeEntity.getSort());
            }
            child.setName(craftCtrlNodeEntity.getName());
            child.setRemark(craftCtrlNodeEntity.getRemark());
            return updateById(child);
        }
    }

    @Transactional(rollbackFor = Exception.class)
    @Override
    public boolean removeCraftCtrlNode(String ids) {
        Integer cnt = baseMapper.selectCount(Wrappers.<CraftCtrlNodeEntity>query().lambda().in(CraftCtrlNodeEntity::getParentId, Func.toLongList(ids)));
        if (cnt > 0) {
            throw new ApiException("请先删除子节点!");
        }
        //更新 交付中心-工艺卡控
        List<PoItemCraftCtrlNodeEntity> entities = poItemCraftCtrlNodeService.getByChildIds(ids);
        if(entities.size() > 0){
            for (PoItemCraftCtrlNodeEntity entity : entities) {
                poItemCraftCtrlNodeService.removeById(entity);
            }
        }
        return removeByIds(Func.toLongList(ids));
    }

    @Override
    public String genCode() {
        String maxCode = this.baseMapper.getMaxCode();
        if (StringUtil.isEmpty(maxCode)) {
            return "CC" + String.format("%03d", 1);
        }
        return "CC" + String.format("%03d", (Integer.parseInt(maxCode.substring(2))) + 1);
    }

    @Override
    public CraftCtrlNodeEntity getByCode(String code) {
        QueryWrapper<CraftCtrlNodeEntity> qw = Condition.getQueryWrapper(new CraftCtrlNodeEntity());
        qw.eq("code", code);
        return getOne(qw);
    }

    @Override
    public CraftCtrlNodeEntity getByName(String name) {
        QueryWrapper<CraftCtrlNodeEntity> qw = Condition.getQueryWrapper(new CraftCtrlNodeEntity());
        qw.eq("name", name);
        return getOne(qw);
    }

    @Override
    public CraftCtrlNodeEntity getChild(String parentName, Integer sort, String childName) {
        QueryWrapper<CraftCtrlNodeEntity> qw = Condition.getQueryWrapper(new CraftCtrlNodeEntity());
        qw.eq("parent_id", getByName(parentName).getId());
        qw.eq("sort", sort);
        if (!StringUtil.isEmpty(childName)) {
            qw.eq("name", childName);
        }
        return getOne(qw);
    }

    @Override
    public void export(CraftCtrlNodeEntity craftCtrlNodeEntity, Query query, HttpServletResponse response) throws Exception {
        QueryWrapper<CraftCtrlNodeEntity> queryWrapper = Condition.getQueryWrapper(new CraftCtrlNodeEntity());
        queryWrapper.eq("parent_id", 0);
        if(!StringUtil.isEmpty(craftCtrlNodeEntity.getName())){
            queryWrapper.like("name", craftCtrlNodeEntity.getName());
        }
        List<CraftCtrlNodeEntity> entities = list(queryWrapper);
        if (entities == null) {
            throw new RuntimeException("暂无数据");
        }
        List<CraftCtrlNodeDTO> dtos = new ArrayList<>();
        entities.forEach(temp ->{
            List<CraftCtrlNodeEntity> childs = list(Condition.getQueryWrapper(new CraftCtrlNodeEntity()).eq("parent_id", temp.getId()));
            if(childs.size() > 0){
                childs.forEach(child ->{
                    CraftCtrlNodeDTO dto = new CraftCtrlNodeDTO();
                    dto.setParentName(temp.getName());
                    dto.setSort(child.getSort());
                    dto.setChildName(child.getName());
                    dto.setRemark(child.getRemark());
                    dtos.add(dto);
                });
            }else{
                CraftCtrlNodeDTO dto = new CraftCtrlNodeDTO();
                dto.setParentName(temp.getName());
                dto.setSort(temp.getSort());
                dto.setChildName(null);
                dto.setRemark(null);
                dtos.add(dto);
            }
        });
        ExcelUtils.defaultExport(dtos, CraftCtrlNodeDTO.class, "工艺卡控类型" + DateUtil.formatDate(new Date()), response);
    }

    @Transactional(rollbackFor = Exception.class)
    @Override
    public Map<String, Object> importExcel(MultipartFile file) throws Exception {
        List<CraftCtrlNodeDTO> dtos = ExcelUtils.importExcel(file, 0, 1, CraftCtrlNodeDTO.class);
        Map<String, Object> result = new HashMap<>(2);
        if (dtos == null) {
            result.put("msg", "暂无数据");
            result.put("flag", false);
            return result;
        }
        for (CraftCtrlNodeDTO dto : dtos) {
            CraftCtrlNodeEntity parent = getByName(dto.getParentName());
            if (!StringUtil.isEmpty(parent)) {
                if (!StringUtil.isEmpty(parent) && !StringUtil.isEmpty(getChild(dto.getParentName(), dto.getSort(), dto.getChildName()))) {
                    result.put("msg", "卡控节点类型名称：" + dto.getParentName() + " 序号：" + dto.getSort() + " 卡控节点名称：" + dto.getChildName() + " 已存在");
                    result.put("flag", false);
                    return result;
                }
            } else {
                //父级不存在
                if (StringUtil.isEmpty(parent)) {
                    CraftCtrlNodeEntity entity = new CraftCtrlNodeEntity();
                    entity.setCode(genCode());
                    entity.setName(dto.getParentName());
                    entity.setSort(10);
                    save(entity);
                }
            }
            //子级是否存在
            CraftCtrlNodeEntity child = getChild(dto.getParentName(), dto.getSort(), "");
            if (StringUtil.isEmpty(child)) {
                CraftCtrlNodeEntity entity = new CraftCtrlNodeEntity();
                entity.setCode(genCode());
                entity.setName(dto.getChildName());
                entity.setSort(dto.getSort());
                entity.setRemark(StringUtil.isEmpty(dto.getRemark()) ? null : dto.getRemark());
                entity.setParentId(getByName(dto.getParentName()).getId());
                save(entity);
            } else {
                child.setName(dto.getChildName());
                child.setRemark(StringUtil.isEmpty(dto.getRemark()) ? null : dto.getRemark());
                updateById(child);
            }
        }
        result.put("msg", "导入成功");
        result.put("flag", true);
        return result;
    }


}

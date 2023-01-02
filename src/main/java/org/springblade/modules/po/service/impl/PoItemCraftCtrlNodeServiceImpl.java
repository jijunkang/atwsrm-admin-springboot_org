package org.springblade.modules.po.service.impl;


import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import org.springblade.core.mp.base.BaseServiceImpl;
import org.springblade.core.mp.support.Condition;
import org.springblade.core.tool.utils.BeanUtil;
import org.springblade.modules.po.dto.PoItemNodeListDTO;
import org.springblade.modules.po.entity.CraftCtrlNodeEntity;
import org.springblade.modules.po.entity.PoItemCraftCtrlNodeEntity;
import org.springblade.modules.po.mapper.PoItemCraftCtrlNodeMapper;
import org.springblade.modules.po.service.ICraftCtrlNodeService;
import org.springblade.modules.po.service.IPoItemCraftCtrlNodeService;
import org.springblade.modules.po.vo.PoItemCraftCtrlNodeVO;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Lazy;
import org.springframework.stereotype.Service;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Comparator;
import java.util.List;

/**
 * @author libin
 *
 * @date 17:14 2020/7/22
 **/
@Service
public class PoItemCraftCtrlNodeServiceImpl extends BaseServiceImpl<PoItemCraftCtrlNodeMapper, PoItemCraftCtrlNodeEntity> implements IPoItemCraftCtrlNodeService {

    @Autowired
    @Lazy
    ICraftCtrlNodeService craftCtrlNodeService;

    @Override
    public List<PoItemCraftCtrlNodeVO> getByPoItemId(Long poItemId) {
        QueryWrapper<PoItemCraftCtrlNodeEntity> queryWrapper = Condition.getQueryWrapper(new PoItemCraftCtrlNodeEntity());
        queryWrapper.eq("po_item_id", poItemId);
        List<PoItemCraftCtrlNodeEntity> list = list(queryWrapper);
        List<PoItemCraftCtrlNodeVO> voList = new ArrayList<>();
        list.forEach(temp -> {
            PoItemCraftCtrlNodeVO poItemCraftCtrlNodeVo = BeanUtil.copy(temp, PoItemCraftCtrlNodeVO.class);
            CraftCtrlNodeEntity craftCtrlNodeEntity = craftCtrlNodeService.getById(temp.getCcnodeChildId());
            poItemCraftCtrlNodeVo.setName(craftCtrlNodeEntity.getName());
            poItemCraftCtrlNodeVo.setSort(craftCtrlNodeEntity.getSort());
            voList.add(poItemCraftCtrlNodeVo);
        });
        voList.sort(new Comparator<PoItemCraftCtrlNodeVO>() {
            @Override
            public int compare(PoItemCraftCtrlNodeVO o1, PoItemCraftCtrlNodeVO o2) {
                try {
                    return Long.compare(o1.getSort(), o2.getSort());
                } catch (Exception e) {
                    e.printStackTrace();
                }
                return 0;
            }
        });
        return voList;
    }

    @Override
    public List<PoItemNodeListDTO> getDTOS(Long poItemId) {
        QueryWrapper<PoItemCraftCtrlNodeEntity> queryWrapper = Condition.getQueryWrapper(new PoItemCraftCtrlNodeEntity());
        queryWrapper.eq("po_item_id", poItemId);
        List<PoItemCraftCtrlNodeEntity> list = list(queryWrapper);
        List<PoItemNodeListDTO> dtos = new ArrayList<>();
        list.forEach(temp -> {
            PoItemNodeListDTO poItemNodeListDTO = BeanUtil.copy(temp, PoItemNodeListDTO.class);
            CraftCtrlNodeEntity craftCtrlNodeEntity = craftCtrlNodeService.getById(temp.getCcnodeChildId());
            poItemNodeListDTO.setName(craftCtrlNodeEntity.getName());
            poItemNodeListDTO.setSort(craftCtrlNodeEntity.getSort());
            dtos.add(poItemNodeListDTO);
        });
        dtos.sort(new Comparator<PoItemNodeListDTO>() {
            @Override
            public int compare(PoItemNodeListDTO o1, PoItemNodeListDTO o2) {
                try {
                    return Long.compare(o1.getSort(), o2.getSort());
                } catch (Exception e) {
                    e.printStackTrace();
                }
                return 0;
            }
        });
        return dtos;
    }

    @Override
    public List<PoItemCraftCtrlNodeEntity> getByParentId(Long parentId) {
        QueryWrapper<PoItemCraftCtrlNodeEntity> queryWrapper = Condition.getQueryWrapper(new PoItemCraftCtrlNodeEntity());
        queryWrapper.eq("ccnode_parent_id", parentId);
        return list(queryWrapper);
    }

    @Override
    public List<PoItemCraftCtrlNodeEntity> getGroupByPoItemId(Long parentId) {
        QueryWrapper<PoItemCraftCtrlNodeEntity> queryWrapper = Condition.getQueryWrapper(new PoItemCraftCtrlNodeEntity());
        queryWrapper.eq("ccnode_parent_id", parentId);
        queryWrapper.groupBy("po_item_id");
        return list(queryWrapper);
    }

    @Override
    public List<PoItemCraftCtrlNodeEntity> getByChildIds(String ids) {
        QueryWrapper<PoItemCraftCtrlNodeEntity> queryWrapper = Condition.getQueryWrapper(new PoItemCraftCtrlNodeEntity());
        List<String> idList = Arrays.asList(ids.split(","));
        queryWrapper.in("ccnode_child_id", idList);
        return list(queryWrapper);
    }
}

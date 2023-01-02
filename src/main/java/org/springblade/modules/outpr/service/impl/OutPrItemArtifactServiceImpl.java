package org.springblade.modules.outpr.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.core.toolkit.Wrappers;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.google.api.client.util.ArrayMap;
import com.google.api.client.util.Lists;
import org.springblade.common.dto.CheckDTO;
import org.springblade.core.mp.base.BaseServiceImpl;
import org.springblade.core.mp.support.Condition;
import org.springblade.core.mp.support.Query;
import org.springblade.core.tool.utils.BeanUtil;
import org.springblade.core.tool.utils.StringUtil;
import org.springblade.modules.item.entity.Item;
import org.springblade.modules.item.service.IItemService;
import org.springblade.modules.outpr.entity.OutPrItemArtifactEntity;
import org.springblade.modules.outpr.entity.OutPrItemEntity;
import org.springblade.modules.outpr.mapper.OutPrItemArtifactMapper;
import org.springblade.modules.outpr.service.IOutPrItemArtifactService;
import org.springblade.modules.outpr.service.IOutPrItemService;
import org.springblade.modules.outpr.vo.OutPrItemArtifactVO;
import org.springblade.modules.po.entity.PoItemEntity;
import org.springblade.modules.po.service.IPoItemService;
import org.springblade.modules.supplier.entity.Supplier;
import org.springblade.modules.supplier.service.ISupplierService;
import org.springblade.modules.system.service.IParamService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Lazy;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Arrays;
import java.util.List;
import java.util.Map;

import static org.springblade.core.secure.utils.AuthUtil.getUser;

/**
 * 服务实现类
 * @author Will
 */
@Service
public
class OutPrItemArtifactServiceImpl extends BaseServiceImpl<OutPrItemArtifactMapper, OutPrItemArtifactEntity> implements IOutPrItemArtifactService{

    @Autowired
    ISupplierService supplierService;

    @Autowired
    @Lazy
    IOutPrItemService outPrItemService;

    @Autowired
    @Lazy
    IPoItemService poItemService;

    @Autowired
    @Lazy
    IParamService paramService;

    @Autowired
    @Lazy
    IItemService itemService;

    @Override
    public
    QueryWrapper<OutPrItemArtifactEntity> getQueryWrapper(OutPrItemArtifactEntity entity){
        return Wrappers.<OutPrItemArtifactEntity>query()
                .like(StringUtil.isNotBlank(entity.getPrCode()), "pr_code", entity.getPrCode())
                .like(StringUtil.isNotBlank(entity.getItemCode()), "item_code", entity.getItemCode())
                .like(StringUtil.isNotBlank(entity.getItemName()), "item_name", entity.getItemName());
    }

    @Override
    public List<Map<String, Object>> centerCount() {
        List<Map<String, Object>> result = Lists.newArrayList();
        result.add(new ArrayMap<String, Object>() {{
            put("status", STATUS_INIT);
            put("title", "待处理");
            put("count", countByStatus(STATUS_INIT));
        }});
        result.add(new ArrayMap<String, Object>() {{
            put("status", STATUS_CHECK1 + "," + STATUS_CHECK2);
            put("title", "待审核");
            put("count", countCheck(STATUS_CHECK1 + "," + STATUS_CHECK2));
        }});
        return result;
    }

    @Override
    public int getCount() {
        QueryWrapper<OutPrItemArtifactEntity> qw = getQueryWrapper(new OutPrItemArtifactEntity());

        String managerRoleId = paramService.getValue("purch_manager.role_id");
        if (StringUtil.containsAny(getUser().getRoleId(), managerRoleId)) {
            qw.in("status", STATUS_CHECK1, STATUS_CHECK2);
        } else {
            qw.in("status", STATUS_INIT, STATUS_CHECK1, STATUS_CHECK2);
        }
        qw.ne("status", IOutPrItemArtifactService.STATUS_ORDER);
        return list(qw).size();
    }

    @Override
    public boolean isExistPrItemId(long prItemId) {
        QueryWrapper<OutPrItemArtifactEntity> queryWrapper = Condition.getQueryWrapper(new OutPrItemArtifactEntity());
        queryWrapper.eq("pr_item_id", prItemId);
        return count(queryWrapper) > 0;
    }

    @Override
    public IPage<OutPrItemArtifactVO> getVoPage(OutPrItemArtifactVO vo, Query query) {
        OutPrItemArtifactEntity entity = BeanUtil.copy(vo, OutPrItemArtifactEntity.class);
        QueryWrapper<OutPrItemArtifactEntity> qw = getQueryWrapper(entity);
        qw.in(vo.getStatuss() != null,"status", vo.getStatusList());

        String managerRoleId = paramService.getValue("purch_manager.role_id");
        if(StringUtil.containsAny(getUser().getRoleId(), managerRoleId)){
            qw.in("status", 30, 31);
        }
        IPage<OutPrItemArtifactEntity> pages = page(Condition.getPage(query), qw.ne("status", IOutPrItemArtifactService.STATUS_ORDER)
                .orderByDesc("update_time"));

        IPage<OutPrItemArtifactVO> voPage = new Page<>(pages.getCurrent(), pages.getSize(), pages.getTotal());
        List<OutPrItemArtifactVO> voList = Lists.newArrayList();
            pages.getRecords().forEach(temp ->{
                OutPrItemArtifactVO outPrItemArtifactVO = BeanUtil.copy(temp, OutPrItemArtifactVO.class);
                outPrItemArtifactVO.setHighestPrice(poItemService.getHighestPrice(temp.getItemCode()));
                outPrItemArtifactVO.setLowestPrice(poItemService.getLowestPrice(temp.getItemCode()));
                PoItemEntity poItemEntity = poItemService.getLastPoInfos(temp.getItemCode(),temp.getItemName());
                if(poItemEntity!=null) {
                    outPrItemArtifactVO.setLastPrice(poItemEntity.getPrice());
                }

                Item item = itemService.getByCode(temp.getItemCode());
                if(item != null){
                    outPrItemArtifactVO.setPurchMix(item.getPurchMix());
                    outPrItemArtifactVO.setStockLowerLimit(item.getStockLowerLimit());
                }

                OutPrItemEntity outPrItemEntity = outPrItemService.getById(temp.getPrItemId());
                if(outPrItemEntity != null){
                    outPrItemArtifactVO.setAvailableQuantity(outPrItemEntity.getAvailableQuantity());
                    outPrItemArtifactVO.setProjectOccupancyNum(outPrItemEntity.getProjectOccupancyNum());
                    outPrItemArtifactVO.setRequisitionRemark(outPrItemEntity.getRequisitionRemark());
                }

                voList.add(outPrItemArtifactVO);
        });
        voPage.setRecords(voList);
        return voPage;
    }

    @Override
    public
    boolean assignSup(OutPrItemArtifactEntity dto){
        OutPrItemArtifactEntity entity = getById(dto.getId());
        entity.setStatus(STATUS_CHECK1);
        entity.setPrice(dto.getPrice());
        entity.setSupDeliveryTime(dto.getSupDeliveryTime());
        entity.setSupCode(dto.getSupCode());
        entity.setAttachment(dto.getAttachment());
        entity.setRemark(dto.getRemark());

        Supplier sup = supplierService.getByCode(dto.getSupCode());
        entity.setSupName(sup.getName());
        return updateById(entity);
    }

    @Transactional
    @Override
    public
    boolean check(CheckDTO checkDto){
        OutPrItemArtifactEntity entity = getById(checkDto.getId());
        entity.setStatus(checkDto.getStatus());

        // 一级审核通过 +已阅
        if(STATUS_CHECK1.equals(checkDto.getStatus()) || STATUS_CHECK2.equals(checkDto.getStatus())){

        }

        // 拒绝
        if(STATUS_REJECT.equals(checkDto.getStatus())){
            OutPrItemEntity prItem = outPrItemService.getById(entity.getPrItemId());
            prItem.setStatus(IOutPrItemService.STATUS_FLOW);
            outPrItemService.updateById(prItem);
        }
        // 二级审核通过
        if(STATUS_WAIT.equals(checkDto.getStatus())){
            OutPrItemEntity prItem = outPrItemService.getById(entity.getPrItemId());
            // STATUS_ACCORD = 60.原本是代下单
            // prItem.setStatus(IOutPrItemService.STATUS_ACCORD);
            prItem.setStatus(IOutPrItemService.STATUS_ORDER);
            outPrItemService.updateById(prItem);
            poItemService.createByOutArtifact(entity, "转人工审核通过");
        }

        return updateById(entity);
    }

    @Transactional
    @Override public
    boolean check(List<CheckDTO> checkDtos){
        for(CheckDTO dto :checkDtos){
            check(dto);
        }
        return true;
    }

    /**
     * 根据状态统计数量
     *
     * @param status Integer
     * @return Integer
     */
    private Integer countByStatus(Integer status){
        QueryWrapper<OutPrItemArtifactEntity> qw = getQueryWrapper(new OutPrItemArtifactEntity());
        qw.eq("status", status);

        String managerRoleId = paramService.getValue("purch_manager.role_id");
        String deputyRoleId  = paramService.getValue("purch_deputy_manager.role_id");
        if(StringUtil.containsAny(getUser().getRoleId(), managerRoleId)){
            qw.in("status", 30, 31);
        }
        if(StringUtil.containsAny(getUser().getRoleId(), deputyRoleId)){
            qw.in("status", 20);
        }
        qw.ne("status", IOutPrItemArtifactService.STATUS_ORDER);
        return list(qw).size();
    }


    /**
     * 统计30，31
     *
     * @param status Integer
     * @return Integer
     */
    private Integer countCheck(String status){
        QueryWrapper<OutPrItemArtifactEntity> qw = getQueryWrapper(new OutPrItemArtifactEntity());
        List<String> idList = Arrays.asList(status.split(","));
        qw.in("status", idList);

        String managerRoleId = paramService.getValue("purch_manager.role_id");
        String deputyRoleId  = paramService.getValue("purch_deputy_manager.role_id");
        if(StringUtil.containsAny(getUser().getRoleId(), managerRoleId)){
            qw.in("status", 30, 31);
        }
        if(StringUtil.containsAny(getUser().getRoleId(), deputyRoleId)){
            qw.in("status", 20);
        }
        qw.ne("status", IOutPrItemArtifactService.STATUS_ORDER);
        return list(qw).size();
    }
}

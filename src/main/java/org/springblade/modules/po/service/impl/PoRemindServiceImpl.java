package org.springblade.modules.po.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.core.toolkit.Wrappers;
import com.google.api.client.util.Lists;
import org.springblade.common.dto.CheckDTO;
import org.springblade.common.utils.ExcelUtils;
import org.springblade.core.mp.base.BaseServiceImpl;
import org.springblade.core.mp.support.Condition;
import org.springblade.core.mp.support.Query;
import org.springblade.core.secure.utils.AuthUtil;
import org.springblade.core.secure.utils.SecureUtil;
import org.springblade.core.tool.utils.BeanUtil;
import org.springblade.core.tool.utils.DateUtil;
import org.springblade.modules.po.dto.PoRemindApplyDTO;
import org.springblade.modules.po.dto.PoRemindDTO;
import org.springblade.modules.po.dto.PoRemindExcel;
import org.springblade.modules.po.entity.PoOffsetViewEntity;
import org.springblade.modules.po.entity.PoRemindEntity;
import org.springblade.modules.po.mapper.PoRemindMapper;
import org.springblade.modules.po.service.IPoOffsetViewService;
import org.springblade.modules.po.service.IPoRemindService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import javax.servlet.http.HttpServletResponse;
import java.util.Date;
import java.util.List;

/**
 * 服务实现类
 * @author Will
 */
@Service
public
class PoRemindServiceImpl extends BaseServiceImpl<PoRemindMapper, PoRemindEntity> implements IPoRemindService{

    @Autowired
    IPoOffsetViewService poOffsetViewService;

    @Override public
    boolean apply(PoRemindApplyDTO applyDTO, Long userId){
        PoOffsetViewEntity poOffset = poOffsetViewService.getById(applyDTO.getPoOffsetId());
        PoRemindEntity poRemind = new PoRemindEntity();
        BeanUtil.copy(poOffset,poRemind);
        poRemind.setStatus(STATUS_INIT);
        poRemind.setId(null);
        poRemind.setRemark(null);
        save(poRemind);
        return poOffsetViewService.removeById(poOffset.getId());
    }

    @Override public
    boolean check(CheckDTO checkDto){
        PoRemindEntity poRemind = getById(checkDto.getId());
        poRemind.setStatus(checkDto.getStatus());
        return updateById(poRemind);
    }

    @Override public
    boolean check(List<CheckDTO>  checkDtos){
        for(CheckDTO dto:checkDtos ){
            check(dto);
        }
        return true;
    }

    @Override public
    boolean complete(CheckDTO checkDto){
        PoRemindEntity poRemind = getById(checkDto.getId());
        poRemind.setStatus(STATUS_FINISH);
        poRemind.setRemark(checkDto.getRemark());
        return updateById(poRemind);
    }

    @Override
    public
    IPage<PoRemindEntity> myRemind(IPage<PoRemindEntity> page, PoRemindEntity poremind){
        poremind.setTraceCode(SecureUtil.getUserAccount());
        poremind.setStatus(STATUS_PASS);
        IPage<PoRemindEntity> retPage = page(page, Condition.getQueryWrapper(poremind));
        return retPage;
    }

    /**
     * 导出
     */
    @Override
    public
    void export(PoRemindDTO remindDTO, Query query, HttpServletResponse response) throws Exception{
        QueryWrapper<PoRemindEntity> qw         = getQueryMapper(remindDTO);
        List<PoRemindEntity>         entityList = list(qw);

        if(entityList == null || entityList.isEmpty()){
            throw new Exception("暂无数据");
        }

        List<PoRemindExcel> excelList = Lists.newArrayList();
        for(PoRemindEntity entity : entityList){
            PoRemindExcel dto = BeanUtil.copy(entity, PoRemindExcel.class);
            excelList.add(dto);
        }

        ExcelUtils.defaultExport(excelList, PoRemindExcel.class, "现场催单" + DateUtil.formatDate(new Date()), response);

    }

    @Override
    public
    QueryWrapper<PoRemindEntity> getQueryMapper(PoRemindDTO remindDTO){
        return Wrappers.<PoRemindEntity>query().eq(remindDTO.getStatus() != null, "status", remindDTO.getStatus())
                                               .eq(remindDTO.getTraceCode() != null, "trace_code", remindDTO.getTraceCode())
                                               .like(remindDTO.getPoCode() != null, "po_code", remindDTO.getPoCode())
                                               .eq(remindDTO.getPoLn() != null, "po_ln", remindDTO.getPoLn())
                                               .like(remindDTO.getItemCode() != null, "item_code", remindDTO.getItemCode())
                                               .like(remindDTO.getItemName() != null, "item_name", remindDTO.getItemName())
                                               .like(remindDTO.getSupCode() != null, "sup_code", remindDTO.getSupCode())
                                               .like(remindDTO.getSupName() != null, "sup_name", remindDTO.getSupName());
    }

}

package org.springblade.modules.aps.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.google.api.client.util.Lists;
import org.springblade.common.utils.ExcelUtils;
import org.springblade.core.mp.base.BaseServiceImpl;
import org.springblade.core.mp.support.Condition;
import org.springblade.core.mp.support.Query;
import org.springblade.core.tool.utils.BeanUtil;
import org.springblade.core.tool.utils.DateUtil;
import org.springblade.core.tool.utils.StringUtil;
import org.springblade.modules.aps.dto.ApsExcelDTO;
import org.springblade.modules.aps.entity.ApsReportExdevEntity;
import org.springblade.modules.aps.entity.DeliveryModifyAuditEntity;
import org.springblade.modules.aps.mapper.ApsReportExdevMapper;
import org.springblade.modules.aps.service.IApsReportExdevService;
import org.springblade.modules.aps.service.IDeliveryModifyAuditService;
import org.springblade.modules.aps.vo.ApsReportExdevVO;
import org.springblade.modules.po.service.IPoItemService;
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
class ApsReportExdevServiceImpl extends BaseServiceImpl<ApsReportExdevMapper, ApsReportExdevEntity> implements IApsReportExdevService{

    @Autowired
    IPoItemService poItemService;

    @Autowired
    IDeliveryModifyAuditService deliveryModifyAuditService;


    @Override
    public
    QueryWrapper<ApsReportExdevEntity> getQueryWrapper(ApsReportExdevEntity entity){
        QueryWrapper<ApsReportExdevEntity> queryWrapper = Condition.getQueryWrapper(new ApsReportExdevEntity());
        queryWrapper.like(StringUtil.isNotBlank(entity.getProNo()), "pro_no", entity.getProNo());
        queryWrapper.like(StringUtil.isNotBlank(entity.getProNoSub()), "pro_no_sub", entity.getProNoSub());
        queryWrapper.like(StringUtil.isNotBlank(entity.getPoCode()), "po_code", entity.getPoCode());
        queryWrapper.like(StringUtil.isNotBlank(entity.getItemCode()), "item_code", entity.getItemCode());
        queryWrapper.like(StringUtil.isNotBlank(entity.getItemName()), "item_name", entity.getItemName());
        return queryWrapper;
    }

    @Override
    public
    void export(ApsReportExdevEntity apsReportExdev, HttpServletResponse response){
        List<ApsReportExdevEntity> voList = this.baseMapper.getPage(apsReportExdev);
        List<ApsExcelDTO>      dtos   = Lists.newArrayList();
        voList.forEach(entity->{
            ApsExcelDTO dto = BeanUtil.copy(entity, ApsExcelDTO.class);
            DeliveryModifyAuditEntity deliveryModifyAuditEntity = deliveryModifyAuditService.getByProNoSubAndItemCodeLimit(entity.getProNoSub(), entity.getItemCode());
            if(deliveryModifyAuditEntity != null){
                dto.setApplyModifyDeliDate(deliveryModifyAuditEntity.getApplyModifyDeliDate());
                //采购交期偏移
                long offsetDays = (entity.getDeliveryDate() - deliveryModifyAuditEntity.getApplyModifyDeliDate())/(3600*24);
                dto.setOffsetDays(Integer.parseInt(String.valueOf(offsetDays)));
            }
            dtos.add(dto);
        });
        ExcelUtils.defaultExport(dtos, ApsExcelDTO.class, "采购执行偏差报表" + DateUtil.formatDate(new Date()), response);
    }

    @Override
    public
    IPage<ApsReportExdevVO> getPage(Query query, ApsReportExdevEntity apsReportExdev){
        IPage<ApsReportExdevEntity> page = this.baseMapper.getPage(Condition.getPage(query), apsReportExdev);
        IPage<ApsReportExdevVO> voiPage = new Page<>(page.getCurrent(), page.getSize(), page.getTotal());
        List<ApsReportExdevVO> voList = Lists.newArrayList();
        page.getRecords().forEach(temp->{
            ApsReportExdevVO vo = BeanUtil.copy(temp, ApsReportExdevVO.class);
            DeliveryModifyAuditEntity deliveryModifyAuditEntity = deliveryModifyAuditService.getByProNoSubAndItemCodeLimit(temp.getProNoSub(), temp.getItemCode());
            if(deliveryModifyAuditEntity != null){
                vo.setApplyModifyDeliDate(deliveryModifyAuditEntity.getApplyModifyDeliDate());
                //采购交期偏移
                long offsetDays = (temp.getDeliveryDate() - deliveryModifyAuditEntity.getApplyModifyDeliDate())/(3600*24);
                vo.setOffsetDays(Integer.parseInt(String.valueOf(offsetDays)));
            }
            voList.add(vo);
        });
        voiPage.setRecords(voList);
        return voiPage;
    }

    @Override
    public List<ApsReportExdevEntity> getByPoCodeAndLns(String poCode, Integer poLn) {
        return this.baseMapper.getByPoCodeAndLns(poCode,poLn);
    }


}

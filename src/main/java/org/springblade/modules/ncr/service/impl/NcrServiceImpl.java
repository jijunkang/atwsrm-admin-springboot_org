package org.springblade.modules.ncr.service.impl;

import com.baomidou.mybatisplus.core.conditions.Wrapper;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.core.toolkit.Wrappers;
import com.google.api.client.util.Lists;
import org.apache.commons.lang3.StringUtils;
import org.springblade.common.utils.ExcelUtils;
import org.springblade.core.mp.base.BaseServiceImpl;
import org.springblade.core.mp.support.Condition;
import org.springblade.core.secure.utils.SecureUtil;
import org.springblade.core.tool.api.R;
import org.springblade.core.tool.utils.BeanUtil;
import org.springblade.core.tool.utils.DateUtil;
import org.springblade.core.tool.utils.StringUtil;
import org.springblade.modules.ap.entity.ApRcvEntity;
import org.springblade.modules.ap.service.IApRcvService;
import org.springblade.modules.ncr.dto.NcrDTO;
import org.springblade.modules.ncr.dto.NcrExcelDTO;
import org.springblade.modules.ncr.entity.NcrEntity;
import org.springblade.modules.ncr.entity.NcrMeasuresEntity;
import org.springblade.modules.ncr.mapper.NcrMapper;
import org.springblade.modules.ncr.service.INcrMeasuresService;
import org.springblade.modules.ncr.service.INcrService;
import org.springblade.modules.ncr.vo.NcrVO;
import org.springblade.modules.supplier.entity.Supplier;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import javax.servlet.http.HttpServletResponse;
import java.math.BigDecimal;
import java.util.*;

/**
 *  服务实现类
 *
 * @author Will
 */
@Service
public class NcrServiceImpl extends BaseServiceImpl<NcrMapper, NcrEntity> implements INcrService {

    @Autowired
    INcrMeasuresService ncrMeasuresService;

    @Autowired
    IApRcvService apRcvService;

    @Override
    public
    Wrapper<NcrEntity> getQueryWrapper(NcrDTO ncr){
        QueryWrapper<NcrEntity> queryWrapper = Wrappers.<NcrEntity>query()
                .like(StringUtils.isNotBlank(ncr.getCode()) ,"code",ncr.getCode())
                .like(StringUtils.isNotBlank(ncr.getSupName()) ,"sup_name",ncr.getSupName())
                .like(StringUtils.isNotBlank(ncr.getSource()) ,"source",ncr.getSource())
                .like(StringUtils.isNotBlank(ncr.getReportCode()) ,"report_code",ncr.getReportCode())
                .like(StringUtils.isNotBlank(ncr.getItemCode()) ,"item_code",ncr.getItemCode())
                .like(StringUtils.isNotBlank(ncr.getItemName()) ,"item_name",ncr.getItemName())
                .eq(ncr.getStatus()!= null,"status",ncr.getStatus());
        return queryWrapper;
    }

    @Override
    public
    Wrapper<NcrEntity> getCenter(NcrDTO ncr){
        QueryWrapper<NcrEntity> queryWrapper = Wrappers.<NcrEntity>query()
            .like(StringUtils.isNotBlank(ncr.getCode()) ,"code",ncr.getCode())
            .like(StringUtils.isNotBlank(ncr.getSupName()) ,"sup_name",ncr.getSupName())
            .like(StringUtils.isNotBlank(ncr.getPoCode()) ,"po_code",ncr.getPoCode())
            .like(StringUtils.isNotBlank(ncr.getReportCode()) ,"report_code",ncr.getReportCode())
            .eq("status",STATUS_HAS)
            .eq("is_accept",0)
            .gt("fine_price", 0)
            .isNull("rcv_code");
            //.eq("rcv_code", "");
        return queryWrapper;
    }

    @Override
    public NcrVO detail(NcrEntity ncr) {
        NcrEntity ncrEntity = getById(ncr.getId());
        NcrVO vo = BeanUtil.copy(ncrEntity, NcrVO.class);

        NcrMeasuresEntity rectifyEntity = new NcrMeasuresEntity();
        rectifyEntity.setType(TYPE_RECTIFY);
        rectifyEntity.setNcrId(ncrEntity.getId());
        vo.setRectifyList(ncrMeasuresService.getList(rectifyEntity));

        NcrMeasuresEntity preventEntity = new NcrMeasuresEntity();
        preventEntity.setType(TYPE_PREVENT);
        preventEntity.setNcrId(ncrEntity.getId());
        vo.setPreventList(ncrMeasuresService.getList(preventEntity));
        return vo;
    }

    @Override
    public void export(HttpServletResponse response, NcrDTO ncr) {
        List<NcrEntity> entities = list(getQueryWrapper(ncr));
        List<NcrExcelDTO> excelDTOS = Lists.newArrayList();
        entities.forEach(entity ->{
            NcrExcelDTO dto = BeanUtil.copy(entity, NcrExcelDTO.class);
            excelDTOS.add(dto);
        });
        ExcelUtils.defaultExport(excelDTOS, NcrExcelDTO.class, "NCR对外单" + DateUtil.formatDate(new Date()), response);
    }

    @Transactional(rollbackFor = Exception.class)
    @Override
    public boolean createRcv(List<NcrEntity> ncrEntityList) {
        ApRcvEntity apRcvEntity = new ApRcvEntity();
        apRcvEntity.setType("KK");
        apRcvEntity.setStatus(IApRcvService.STATUS_INIT);
        apRcvEntity.setRcvCode(apRcvService.genCode(apRcvEntity.getType()));
        apRcvEntity.setRcvActualQty(new BigDecimal("-1"));
        apRcvEntity.setUom("PCS");
        apRcvEntity.setSupCode(ncrEntityList.get(0).getSupCode());
        apRcvEntity.setSupName(ncrEntityList.get(0).getSupName());
        apRcvEntity.setIsNcr(1);
        apRcvService.save(apRcvEntity);
        BigDecimal sumPrice = new BigDecimal("0");
        for (NcrEntity ncrEntity : ncrEntityList) {
            ncrEntity.setRcvCode(apRcvEntity.getRcvCode());
            sumPrice = sumPrice.add(ncrEntity.getFinePrice());
            updateById(ncrEntity);
        }
        apRcvEntity.setTaxPrice(sumPrice);
        apRcvEntity.setTaxSubTotal(apRcvEntity.getRcvActualQty().multiply(apRcvEntity.getTaxPrice()));
        apRcvService.updateById(apRcvEntity);
        return true;
    }

    @Override
    public List<NcrEntity> queryByRcvCode(String rcvCode) {
        QueryWrapper<NcrEntity> queryWrapper = Condition.getQueryWrapper(new NcrEntity());
        if(StringUtil.isNotBlank(rcvCode)){
            queryWrapper.like("rcv_code", rcvCode);
        }
        return list(queryWrapper);
    }



    @Transactional(rollbackFor = Exception.class)
    @Override
    public void createRcvBatch() {
        List<NcrEntity> entities = list(getCenter(new NcrDTO()));
        //以供应商code作为key分类
        Map<String, List<NcrEntity>> map = new HashMap<>();
        entities.forEach(entity ->{
            if(map.containsKey(entity.getSupCode())){
                map.get(entity.getSupCode()).add(entity);
            }else{
                List<NcrEntity> addList = Lists.newArrayList();
                addList.add(entity);
                map.put(entity.getSupCode(), addList);
            }
        });
        //生成扣款单
        for(List<NcrEntity> key : map.values()){
            createRcv(key);
        }
    }

    @Override
    public Integer getCount() {
        return list(getCenter(new NcrDTO())).size();
    }

    @Transactional(rollbackFor = Exception.class)
    @Override
    public boolean updateByRcvCode(String rcvCode) {
        queryByRcvCode(rcvCode).forEach(temp ->{
            temp.setRcvCode("");
            updateById(temp);
        });
        return true;
    }

    @Override
    public Map<String, Object> getNotCount() {
        NcrDTO dto = new NcrDTO();
        dto.setStatus(STATUS_NOT);
        dto.setSupCode(SecureUtil.getTenantId());
        Map<String, Object> map = new HashMap<>(1);
        map.put("count", list(getQueryWrapper(dto)).size());
        return map;
    }
}

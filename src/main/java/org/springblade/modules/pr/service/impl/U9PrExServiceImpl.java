package org.springblade.modules.pr.service.impl;

import com.baomidou.mybatisplus.core.conditions.Wrapper;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.core.toolkit.Wrappers;
import com.google.api.client.util.Lists;
import lombok.extern.slf4j.Slf4j;
import org.springblade.common.utils.ExcelUtils;
import org.springblade.core.mp.base.BaseServiceImpl;
import org.springblade.core.tool.utils.BeanUtil;
import org.springblade.core.tool.utils.DateUtil;
import org.springblade.modules.material.dto.MaterialPriceExcelNewDTO;
import org.springblade.modules.pr.dto.*;
import org.springblade.modules.pr.entity.*;
import org.springblade.modules.pr.mapper.U9PrEntityExMapper;
import org.springblade.modules.pr.mapper.U9PrEntityNoProMapper;
import org.springblade.modules.pr.service.IU9PrExService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import javax.servlet.http.HttpServletResponse;
import java.util.*;

import static org.springblade.core.secure.utils.AuthUtil.getUser;

/**
 * 请购单 服务实现类
 *
 * @author Will
 */
@Service
@Slf4j
public
class U9PrExServiceImpl extends BaseServiceImpl<U9PrEntityExMapper, U9PrEntityEx> implements IU9PrExService{


    @Autowired
    U9PrEntityExMapper u9PrEntityExMapper;

    @Autowired
    U9PrEntityNoProMapper u9PrEntityNoProMapper;

    @Override
    public Wrapper<U9PrEntityEx> getQueryWrapper(U9PrEntityEx u9PrEntityEx) {
        return Wrappers.<U9PrEntityEx>query().eq("pr_code", u9PrEntityEx.getPrCode())
            .eq("pr_ln",u9PrEntityEx.getPrLn())
            .eq("is_deleted",0);
    }

    @Override
    public IPage<U9PrEntityEx> getU9PrEx(IPage<U9PrEntityEx> page, U9PrExReq u9PrExReq){
        IPage<U9PrEntityEx> entityPage = u9PrEntityExMapper.selectPageByReq(page,u9PrExReq);

        return entityPage;
    }


    @Override
    public IPage<U9PrEntityNoPro> getU9PrNoPro(IPage<U9PrEntityNoPro> page, U9PrExReq u9PrExReq){
        IPage<U9PrEntityNoPro> entityPage = u9PrEntityNoProMapper.selectPageByReq(page,u9PrExReq);

        return entityPage;
    }

    @Override
    @Transactional
    public boolean freePr(List<U9PrEntityEx> u9PrEntityExList) {
        for (U9PrEntityEx u9PrEntityEx:u9PrEntityExList) {

            if (u9PrEntityExMapper.lockExPrById(u9PrEntityEx.getId())) {
                 u9PrEntityExMapper.freePrByPrLn(u9PrEntityEx.getPrCode(),u9PrEntityEx.getPrLn());
            }
        }
        return true;
    }

    @Override
    public void exportExcel(U9PrExReq u9PrExReq, HttpServletResponse response)  {
        List<U9PrEntityEx> entityList =this.baseMapper.selectListByReq(u9PrExReq);
        if(entityList == null){
            throw new RuntimeException("暂无数据");
        }
        List<U9PrExExcelDTO> excelList = Lists.newArrayList();
        for(U9PrEntityEx entity : entityList){
            U9PrExExcelDTO excelDTO = BeanUtil.copy(entity, U9PrExExcelDTO.class);
            excelList.add(excelDTO);
        }

        ExcelUtils.defaultExport(excelList, U9PrExExcelDTO.class, "请购单异常分析报表" + DateUtil.formatDate(new Date()), response);
    }

}

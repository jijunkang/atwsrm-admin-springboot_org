package org.springblade.modules.pricelib.service.impl;

import com.baomidou.mybatisplus.core.conditions.Wrapper;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.core.toolkit.Wrappers;
import com.google.api.client.util.Lists;
import lombok.SneakyThrows;
import org.apache.commons.lang3.StringUtils;
import org.springblade.common.dto.CheckDTO;
import org.springblade.common.utils.ExcelUtils;
import org.springblade.core.mp.base.BaseServiceImpl;
import org.springblade.core.mp.support.Condition;
import org.springblade.core.mp.support.Query;
import org.springblade.core.secure.BladeUser;
import org.springblade.core.tool.utils.BeanUtil;
import org.springblade.core.tool.utils.DateUtil;
import org.springblade.core.tool.utils.StringUtil;
import org.springblade.modules.item.entity.Item;
import org.springblade.modules.item.service.IItemService;
import org.springblade.modules.po.dto.IoWinbidReq;
import org.springblade.modules.po.entity.IoEntity;
import org.springblade.modules.po.entity.OutIoEntity;
import org.springblade.modules.po.service.IIoOutService;
import org.springblade.modules.po.service.IIoService;
import org.springblade.modules.pr.entity.U9PrEntity;
import org.springblade.modules.pricelib.dto.PriceLibExcelDTO;
import org.springblade.modules.pricelib.entity.PriceLibEntity;
import org.springblade.modules.pricelib.mapper.PriceLibMapper;
import org.springblade.modules.pricelib.service.IPriceLibService;
import org.springblade.modules.supplier.entity.Supplier;
import org.springblade.modules.supplier.service.ISupplierService;
import org.springblade.modules.system.service.IParamService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Lazy;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;

import javax.servlet.http.HttpServletResponse;
import javax.swing.text.html.parser.Entity;
import java.math.BigDecimal;
import java.text.SimpleDateFormat;
import java.util.*;

import static org.springblade.core.secure.utils.AuthUtil.getUser;

/**
 * 服务实现类
 * @author Will
 */
@Service
public
class PriceLibServiceImpl extends BaseServiceImpl<PriceLibMapper, PriceLibEntity> implements IPriceLibService{

    @Autowired
    @Lazy
    private IIoService ioService;

    @Autowired
    IParamService paramService;

    @Autowired
    IItemService itemService;

    @Autowired
    ISupplierService supplierService;

    @Autowired
    @Lazy
    IIoOutService iIoOutService;


    @Override
    public
    QueryWrapper<PriceLibEntity> getQueryWrapper(PriceLibEntity priceLib){
        QueryWrapper<PriceLibEntity> queryWrapper = Condition.getQueryWrapper(new PriceLibEntity()).orderByDesc("item_code", "update_time");

        if(StringUtil.isNotBlank(priceLib.getItemCode())){
            queryWrapper.like("item_code", priceLib.getItemCode());
        }
        if(StringUtil.isNotBlank(priceLib.getItemName())){
            queryWrapper.like("item_name", priceLib.getItemName());
        }
        if(StringUtil.isNotBlank(priceLib.getSupCode())){
            queryWrapper.like("sup_code", priceLib.getSupCode());
        }
        if(StringUtil.isNotBlank(priceLib.getSupName())){
            queryWrapper.like("sup_name", priceLib.getSupName());
        }
        if(priceLib.getStatus() != null){
            queryWrapper.eq("status", priceLib.getStatus());
        }
        return queryWrapper;
    }

    @SneakyThrows
    @Override
    public
    boolean save(PriceLibEntity entity){
        if(StringUtils.isAnyBlank(entity.getSupCode(),entity.getItemCode())){
            return false;
        }
        if(entity.getExpirationDate() != null){
            Date expDate = DateUtil.plusSeconds(entity.getExpirationDate(), 24*60*60 - 1);
            entity.setExpirationDate(expDate);
        }
        //如果是当前料号下的新供应商，删除所有后保留新的
        if(isNewSup(entity)){
            this.baseMapper.deleteByItemCode(entity.getItemCode());
        }
        //是否已存在白名单
        if(checkPriceLib(entity)){
            return true;
        }

        entity.setStatus(STATUS_INIT);
        entity.setSubmitterCode(StringUtils.isBlank(entity.getSubmitterCode())?getUser().getAccount():entity.getSubmitterCode());

        Item item = itemService.getByCode(entity.getItemCode());
        if(item == null){
           throw new RuntimeException("物料编号不存在:"+entity.getItemCode());
        }
        entity.setUom(item.getPriceUom());

        return super.save(entity);
    }


    /**
     * 是否为当前物料下的新供应商
     *
     * @param entity PriceLibEntity
     * @return boolean
     */
    private boolean isNewSup(PriceLibEntity entity){
        QueryWrapper<PriceLibEntity> queryWrapper = Condition.getQueryWrapper(new PriceLibEntity());
        queryWrapper.eq("item_code", entity.getItemCode());
        queryWrapper.eq("sup_code", entity.getSupCode());
        return list(queryWrapper).size() == 0;
    }


    /**
     * 根据物料编号、供应商编号、默认状态、默认最小数、失效日期
     * 判断是否已存在白名单
     * checkPriceLib
     *
     * @param entity PriceLibEntity
     * @return boolean
     */
    private boolean checkPriceLib(PriceLibEntity entity){
        QueryWrapper<PriceLibEntity> queryWrapper = Condition.getQueryWrapper(new PriceLibEntity());
        queryWrapper.eq("item_code", entity.getItemCode());
        queryWrapper.eq("sup_code", entity.getSupCode());
        queryWrapper.eq("expiration_date", entity.getExpirationDate());
        queryWrapper.eq("limit_min", 1);
        queryWrapper.eq("status", STATUS_INIT);
        return getOne(queryWrapper) != null;
    }


    /**
     * 使 entity 过期
     * @param entity
     */
    private
    boolean expire(PriceLibEntity entity){
        entity.setExpirationDate(new Date());
        return updateById(entity);
    }

    /**
     * 获取最新的
     * @return
     */
    private
    PriceLibEntity getLastOne(String itemCode,  BigDecimal limitMin){
        return getOne(Condition.getQueryWrapper(new PriceLibEntity()).eq("item_code", itemCode)
                              .eq("limit_min", limitMin)
                              .eq("status", STATUS_ENABLED)
                              .orderByDesc("expiration_date")
                              .last("LIMIT 1"));
    }

    /**
     * io 加入白名单
     * @return
     */
    @Override public
    boolean ioToPriceLib(IoWinbidReq ioReq){
        if(ioReq.getEffectiveDate() == null || ioReq.getExpirationDate() == null){
            return false;
        }

        PriceLibEntity priceLib = new PriceLibEntity();
        IoEntity       io       = ioService.getById(ioReq.getIoId());
        priceLib.setItemCode(io.getItemCode());
        priceLib.setItemName(io.getItemName());
        priceLib.setSupCode(io.getSupCode());
        priceLib.setSupName(io.getSupName());
        priceLib.setPrice(io.getQuotePrice());
        priceLib.setUom(io.getPriceUom());
        priceLib.setAttachment(ioReq.getAttachment());
        priceLib.setEffectiveDate(ioReq.getEffectiveDate());
        priceLib.setExpirationDate(ioReq.getExpirationDate());
        priceLib.setLimitMin(ioReq.getLimitMin());
        priceLib.setSubmitterCode(getUser().getAccount());
        priceLib.setStatus(STATUS_INIT);
        return save(priceLib);
    }

    /**
     * io 加入白名单 - 委外
     * @return
     */
    @Override public
    boolean ioToPriceLibOfWW(IoWinbidReq ioReq){
        if(ioReq.getEffectiveDate() == null || ioReq.getExpirationDate() == null){
            return false;
        }
        PriceLibEntity priceLib = new PriceLibEntity();
        OutIoEntity io       = iIoOutService.getById(ioReq.getIoId());
        priceLib.setItemCode(io.getItemCode());
        priceLib.setItemName(io.getItemName());
        priceLib.setSupCode(io.getSupCode());
        priceLib.setSupName(io.getSupName());
        priceLib.setPrice(io.getQuotePrice());
        priceLib.setUom(io.getPriceUom());
        priceLib.setAttachment(ioReq.getAttachment());
        priceLib.setEffectiveDate(ioReq.getEffectiveDate());
        priceLib.setExpirationDate(ioReq.getExpirationDate());
        priceLib.setLimitMin(ioReq.getLimitMin());
        priceLib.setSubmitterCode(getUser().getAccount());
        priceLib.setStatus(STATUS_INIT);
        return save(priceLib);
    }

    @Override public
    boolean check(CheckDTO checkDto){
        if(checkDto.getId()==null){
            return false;
        }
        PriceLibEntity priceLib  = getById(checkDto.getId());
        String         preRemark = "";
        if(Objects.equals(checkDto.getStatus(), STATUS_CHEKE)){
            preRemark = "审核通过";
        }
        if(Objects.equals(checkDto.getStatus(), STATUS_ENABLED)){
            preRemark = "审核通过";
        }
        if(Objects.equals(checkDto.getStatus(), STATUS_CHEKE1)){
            preRemark = "审核已阅";
        }
        if(Objects.equals(checkDto.getStatus(), STATUS_REJECT)){
            preRemark = "审核拒绝";
        }
        System.out.println("test:" + checkDto);
        priceLib.setCheckRemark("ESB" + preRemark + (StringUtils.isBlank(checkDto.getRemark()) ? "" : ":" + checkDto.getRemark()));
        priceLib.setStatus(checkDto.getStatus());
        // 若审核通过 更新商品 价格属性
        if(Objects.equals(checkDto.getStatus(), STATUS_ENABLED)){
//            Item item = itemService.getByCode(priceLib.getItemCode());
//            item.setPurchAttr(IItemService.PUR_ATTR_PRICELIB);
//            itemService.updateById(item);

            this.baseMapper.updatePricelibByIemCdoe(priceLib.getItemCode(),IItemService.PUR_ATTR_PRICELIB);

            PriceLibEntity last = getLastOne(priceLib.getItemCode(), priceLib.getLimitMin());
            if(last != null){
                expire(last);
                priceLib.setEffectiveDate(new Date());
            }
        }

        return updateById(priceLib);
    }

    @Override public
    IPage<PriceLibEntity> toCheckPage(IPage<PriceLibEntity> page, PriceLibEntity priceLib){
        BladeUser     user     = getUser();
        String        dmRoleId = paramService.getValue("purch_deputy_manager.role_id"); //副经理角色ID
        String        mRoleId    = paramService.getValue("purch_manager.role_id");//经理角色ID
        List<Integer> statusList = new ArrayList<>();
        statusList.add(0);
        if(StringUtil.containsAny(user.getRoleId(), dmRoleId)){
            statusList.add(STATUS_INIT);
        }
        if(StringUtil.containsAny(user.getRoleId(), mRoleId)){
            statusList.add(STATUS_CHEKE);
            statusList.add(STATUS_CHEKE1);
        }

        Wrapper<PriceLibEntity> queryWrapper = Condition.getQueryWrapper(new PriceLibEntity())
                .in("status", statusList)
                .like(StringUtils.isNotBlank(priceLib.getItemCode()), "item_code", priceLib.getItemCode())
                .like(StringUtils.isNotBlank(priceLib.getSubmitterCode()), "item_name", priceLib.getItemName())
                .like(StringUtils.isNotBlank(priceLib.getSupCode()), "item_sup", priceLib.getSupCode())
                .like(StringUtils.isNotBlank(priceLib.getSupName()), "item_sup", priceLib.getSupName())
                .orderByDesc("update_time");
        return baseMapper.selectPage(page, queryWrapper);
    }

    @Override
    public
    int toCheckCount(){
        BladeUser     user     = getUser();
        String        dmRoleId = paramService.getValue("purch_deputy_manager.role_id"); //副经理角色ID
        String        mRoleId    = paramService.getValue("purch_manager.role_id");//经理角色ID
        List<Integer> statusList = new ArrayList<>();
        statusList.add(0);
        if(StringUtil.containsAny(user.getRoleId(), dmRoleId)){
            statusList.add(STATUS_INIT);
        }
        if(StringUtil.containsAny(user.getRoleId(), mRoleId)){
            statusList.add(STATUS_CHEKE);
            statusList.add(STATUS_CHEKE1);
        }
        Wrapper<PriceLibEntity> queryWrapper = Wrappers.<PriceLibEntity>query().in("status",statusList);

        return count(queryWrapper);
    }

    @Override public
    IPage<PriceLibEntity> myPage(IPage<PriceLibEntity> page, PriceLibEntity priceLib){
        BladeUser     user       = getUser();
        List<Integer> statusList = Arrays.asList(STATUS_INIT, STATUS_CHEKE, STATUS_CHEKE1, STATUS_REJECT);

        Wrapper<PriceLibEntity> queryWrapper = Condition.getQueryWrapper(new PriceLibEntity())
                .in("status", statusList)
                .eq("create_user", user.getUserId())
                .like(StringUtils.isNotBlank(priceLib.getItemCode()), "item_code", priceLib.getItemCode())
                .like(StringUtils.isNotBlank(priceLib.getItemName()), "item_name", priceLib.getItemName())
                .like(StringUtils.isNotBlank(priceLib.getSupCode()), "sup_code", priceLib.getSupCode())
                .like(StringUtils.isNotBlank(priceLib.getSupName()), "sup_name", priceLib.getSupName())
                .orderByDesc("update_time");
        return baseMapper.selectPage(page, queryWrapper);
    }

    /**
     * 导入excel
     * @return
     */
    @Override
    @Transactional(rollbackFor = Exception.class)
    public boolean importExcel(MultipartFile file) throws Exception{
        List<PriceLibExcelDTO> dtoList    = ExcelUtils.importExcel(file, 0, 1, PriceLibExcelDTO.class);
        List<PriceLibEntity>   entityList = BeanUtil.copy(dtoList, PriceLibEntity.class);
        for(PriceLibEntity entity: entityList){
            if (entity.getSupCode() == null ) {
                continue;
            }
            Supplier supplier = supplierService.getByCode(entity.getSupCode());
            if(supplier == null){
                throw new RuntimeException("不存在的供应商："+entity.getSupCode());
            }
            entity.setSupName(supplier.getName());
            if(!save(entity)){
                throw new RuntimeException("保存失败，请检查模版和数据是否规范");
            };
            CheckDTO checkDto = new CheckDTO();
            checkDto.setId(entity.getId());
            checkDto.setStatus(STATUS_ENABLED);
            check(checkDto);
        }
        return true;
    }

    /**
     * 导入excel
     * @return
     */
    @Override
    @Transactional(rollbackFor = Exception.class)
    public boolean importexcelfromESB(List<PriceLibEntity> file) throws Exception{
        List<PriceLibEntity>   entityList = file;
        for(PriceLibEntity entity: entityList){
            if (entity.getSupCode() == null ) {
                continue;
            }
            Supplier supplier = supplierService.getByCode(entity.getSupCode());
            if(supplier == null){
                throw new RuntimeException("不存在的供应商："+entity.getSupCode());
            }
            entity.setSupName(supplier.getName());
            if(!save(entity)){
                throw new RuntimeException("保存失败，请检查模版和数据是否规范");
            };
            CheckDTO checkDto = new CheckDTO();
            checkDto.setId(entity.getId());
            checkDto.setStatus(STATUS_ENABLED);
            check(checkDto);
        }
        return true;
    }

    //maily
    @Override
    public
    IPage<PriceLibEntity> selectPage(IPage<PriceLibEntity> page, PriceLibEntity priceLib){
        QueryWrapper<PriceLibEntity> queryWrapper = getQueryWrapper(priceLib);
        if (StringUtils.isNotEmpty(priceLib.getItemCode()) && priceLib.getItemCode().split(",").length>1){
            String[] split = priceLib.getItemCode().split(",");
            for (String code : split) {
                queryWrapper.or().apply(priceLib.getItemCode() != null,"FIND_IN_SET ('"+code+"',item_code)");
            }
        }
        return page(page, queryWrapper);
    }

    @Override
    public
    IPage<PriceLibEntity> getPriceLib(IPage<PriceLibEntity> page, U9PrEntity u9PrEntity){
        QueryWrapper<PriceLibEntity> queryWrapper = Condition.getQueryWrapper(new PriceLibEntity()).orderByDesc("limit_min");
        SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd");
        queryWrapper.eq("item_code", u9PrEntity.getItemCode())
            .ge("expiration_date", sdf.format(new Date()) )
            .le("effective_date", sdf.format(new Date()))
            .le("limit_min", u9PrEntity.getPriceNum())
            .eq("status", STATUS_ENABLED);
        page.setSize(1);
        return page(page, queryWrapper);
    }

    /**
     * 导出
     */
    @Override
    public
    void export(PriceLibEntity priceLib, Query query, HttpServletResponse response) throws Exception{
        QueryWrapper<PriceLibEntity> qw   = getQueryWrapper(priceLib);
        List<PriceLibEntity>         entityList = list(qw);
        if(entityList == null){
            throw new Exception("暂无数据");
        }
        List<PriceLibExcelDTO> excelList = Lists.newArrayList();
        for(PriceLibEntity entity :entityList){
            PriceLibExcelDTO dto = BeanUtil.copy(entity,PriceLibExcelDTO.class);
            excelList.add(dto);
        }

        ExcelUtils.defaultExport(excelList, PriceLibExcelDTO.class, "白名单" + DateUtil.formatDate(new Date()), response);

    }

    @Override
    public List<PriceLibEntity> getByItemCodes(List<String> itemCodes) {
        return this.baseMapper.getByItemCodes(itemCodes);
    }

    @Override
    public boolean update(List<String> itemCodes) {
        ArrayList<PriceLibEntity> list = new ArrayList<>();
        itemCodes.forEach((itemCode) -> {
            PriceLibEntity entity = this.baseMapper.selectByItemCode(itemCode);
            entity.setExpirationDate(new Date());
            list.add(entity);
        });
        return super.updateBatchById(list);
    }

}

package org.springblade.modules.priceframe.service.impl;

import com.baomidou.mybatisplus.core.conditions.Wrapper;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.core.toolkit.Wrappers;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.google.api.client.util.ArrayMap;
import com.google.api.client.util.Lists;
import lombok.SneakyThrows;
import org.apache.commons.lang3.StringUtils;
import org.springblade.common.dto.CheckDTO;
import org.springblade.common.utils.ExcelUtils;
import org.springblade.core.mp.base.BaseServiceImpl;
import org.springblade.core.mp.support.Condition;
import org.springblade.core.mp.support.Query;
import org.springblade.core.secure.BladeUser;
import org.springblade.core.secure.utils.AuthUtil;
import org.springblade.core.secure.utils.SecureUtil;
import org.springblade.core.tool.utils.BeanUtil;
import org.springblade.core.tool.utils.DateUtil;
import org.springblade.core.tool.utils.StringUtil;
import org.springblade.modules.item.entity.Item;
import org.springblade.modules.item.service.IItemService;
import org.springblade.modules.mathmodel.service.IMmVolumeCalculateService;
import org.springblade.modules.po.dto.IoDTO;
import org.springblade.modules.po.dto.IoWinbidReq;
import org.springblade.modules.po.entity.IoEntity;
import org.springblade.modules.po.service.IIoService;
import org.springblade.modules.po.service.IPoItemService;
import org.springblade.modules.po.service.IPoService;
import org.springblade.modules.pr.dto.U9PrDTO;
import org.springblade.modules.pr.entity.U9PrEntity;
import org.springblade.modules.pr.service.IU9PrService;
import org.springblade.modules.priceframe.dto.CenterPriceFrame;
import org.springblade.modules.priceframe.dto.PriceFrameExcelDTO;
import org.springblade.modules.priceframe.entity.PriceFrameEntity;
import org.springblade.modules.priceframe.mapper.PriceFrameMapper;
import org.springblade.modules.priceframe.service.IPriceFrameService;
import org.springblade.modules.priceframe.vo.PriceFrameVO;
import org.springblade.modules.supplier.entity.Supplier;
import org.springblade.modules.supplier.service.ISupplierService;
import org.springblade.modules.system.service.IParamService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Lazy;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;

import javax.servlet.http.HttpServletResponse;
import javax.xml.transform.Result;
import java.math.BigDecimal;
import java.text.SimpleDateFormat;
import java.util.*;

/**
 * 服务实现类
 *
 * @author Will
 */
@Service
public
class PriceFrameServiceImpl extends BaseServiceImpl<PriceFrameMapper, PriceFrameEntity> implements IPriceFrameService {

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
    IU9PrService u9PrService;

    @Autowired
    IPoService poService;

    @Autowired @Lazy
    IMmVolumeCalculateService mmVolumeCalculateService;

    @Override
    public QueryWrapper<PriceFrameEntity> getQueryWrapper(PriceFrameEntity priceFrameEntity) {
        QueryWrapper<PriceFrameEntity> queryWrapper = Condition.getQueryWrapper(new PriceFrameEntity()).orderByDesc("item_code", "update_time");

        if (StringUtil.isNotBlank(priceFrameEntity.getItemCode())) {
            queryWrapper.like("item_code", priceFrameEntity.getItemCode());
        }
        if (StringUtil.isNotBlank(priceFrameEntity.getItemName())) {
            queryWrapper.like("item_name", priceFrameEntity.getItemName());
        }
        if (StringUtil.isNotBlank(priceFrameEntity.getSupCode())) {
            queryWrapper.like("sup_code", priceFrameEntity.getSupCode());
        }
        if (StringUtil.isNotBlank(priceFrameEntity.getSupName())) {
            queryWrapper.like("sup_name", priceFrameEntity.getSupName());
        }
        if (priceFrameEntity.getStatus() != null) {
            queryWrapper.eq("status", priceFrameEntity.getStatus());
        }
        return queryWrapper;
    }

    @SneakyThrows
    @Override
    public boolean save(PriceFrameEntity entity) {
        if (StringUtils.isAnyBlank(entity.getSupCode(), entity.getItemCode())) {
            return false;
        }
        if (entity.getExpirationDate() != null) {
            Date expDate = DateUtil.plusSeconds(entity.getExpirationDate(), 24 * 60 * 60 - 1);
            entity.setExpirationDate(expDate);
        }
        entity.setStatus(STATUS_AUDIT);
        entity.setSubmitterCode(SecureUtil.getUser().getAccount());

        Item item = itemService.getByCode(entity.getItemCode());
        if (item == null) {
            throw new RuntimeException("物料编号不存在:" + entity.getItemCode());
        }
        entity.setUom(item.getPriceUom());

        return super.save(entity);
    }

    /**
     * 使 entity 过期
     *
     * @param entity
     */
    private boolean expire(PriceFrameEntity entity) {
        entity.setExpirationDate(new Date());
        return updateById(entity);
    }

    /**
     * 获取最新的
     *
     * @return
     */
    private PriceFrameEntity getLastOne(String itemCode, BigDecimal limitMin) {
        return getOne(Condition.getQueryWrapper(new PriceFrameEntity()).eq("item_code", itemCode)
            .eq("limit_min", limitMin)
            .eq("status", STATUS_SUCCESS)
            .orderByDesc("expiration_date")
            .last("LIMIT 1"));
    }

    /**
     * io 加入白名单
     *
     * @return
     */
    @Override
    public boolean ioToPriceFrame(IoWinbidReq ioReq) {
        if (ioReq.getEffectiveDate() == null || ioReq.getExpirationDate() == null) {
            return false;
        }

        PriceFrameEntity priceFrameEntity = new PriceFrameEntity();
        IoEntity io = ioService.getById(ioReq.getIoId());
        priceFrameEntity.setItemCode(io.getItemCode());
        priceFrameEntity.setItemName(io.getItemName());
        priceFrameEntity.setSupCode(io.getSupCode());
        priceFrameEntity.setSupName(io.getSupName());
        priceFrameEntity.setPrice(io.getQuotePrice());
        priceFrameEntity.setUom(io.getPriceUom());
        priceFrameEntity.setAttachment(ioReq.getAttachment());
        priceFrameEntity.setEffectiveDate(ioReq.getEffectiveDate());
        priceFrameEntity.setExpirationDate(ioReq.getExpirationDate());
        priceFrameEntity.setLimitMin(ioReq.getLimitMin());
        priceFrameEntity.setSubmitterCode(AuthUtil.getUser().getAccount());
        priceFrameEntity.setStatus(STATUS_AUDIT);
        return save(priceFrameEntity);
    }

    @Override
    public boolean check(CheckDTO checkDto) {
        BladeUser currUser = AuthUtil.getUser();
        PriceFrameEntity priceFrameEntity = getById(checkDto.getId());
        String preRemark = "";
        if (Objects.equals(checkDto.getStatus(), STATUS_AUDIT)) {
            preRemark = "待审核";
        }
        if (Objects.equals(checkDto.getStatus(), STATUS_SUCCESS)) {
            preRemark = "审核通过";
        }
        if (Objects.equals(checkDto.getStatus(), STATUS_REJECT)) {
            preRemark = "审核拒绝";
        }
        priceFrameEntity.setCheckRemark(currUser.getNickName() + preRemark + (StringUtils.isBlank(checkDto.getRemark()) ? "" : ":" + checkDto.getRemark()));
        priceFrameEntity.setStatus(checkDto.getStatus());
        // 若审核通过 更新商品 价格属性
        if (Objects.equals(checkDto.getStatus(), STATUS_SUCCESS)) {
            Item item = itemService.getByCode(priceFrameEntity.getItemCode());
            //更改除白名单以外的价格属性为框架协议
            if (!IItemService.PUR_ATTR_PRICELIB.equals(item.getPurchAttr())) {
                item.setPurchAttr(IItemService.PUR_ATTR_PROTOCOL);
            }
            itemService.updateById(item);
        }

        return updateById(priceFrameEntity);
    }

    @Override
    public IPage<PriceFrameEntity> toCheckPage(IPage<PriceFrameEntity> page, PriceFrameEntity priceFrameEntity) {
        BladeUser user = AuthUtil.getUser();
        String dmRoleId = paramService.getValue("purch_deputy_manager.role_id"); //副经理角色ID
        List<Integer> statusList = new ArrayList<>();
        statusList.add(0);
        statusList.add(STATUS_AUDIT);
        Wrapper<PriceFrameEntity> queryWrapper = Condition.getQueryWrapper(new PriceFrameEntity())
            .in("status", statusList)
            .like(StringUtils.isNotBlank(priceFrameEntity.getItemCode()), "item_code", priceFrameEntity.getItemCode())
            .like(StringUtils.isNotBlank(priceFrameEntity.getSubmitterCode()), "item_name", priceFrameEntity.getItemName())
            .like(StringUtils.isNotBlank(priceFrameEntity.getSupCode()), "sup_code", priceFrameEntity.getSupCode())
            .like(StringUtils.isNotBlank(priceFrameEntity.getSupName()), "sup_name", priceFrameEntity.getSupName())
            .orderByDesc("update_time");
        return baseMapper.selectPage(page, queryWrapper);
    }

    @Override
    public int toCheckCount() {
        BladeUser user = AuthUtil.getUser();
        String dmRoleId = paramService.getValue("purch_deputy_manager.role_id"); //副经理角色ID
        List<Integer> statusList = new ArrayList<>();
        statusList.add(0);
        if (StringUtil.containsAny(user.getRoleId(), dmRoleId)) {
            statusList.add(STATUS_AUDIT);
        }
        Wrapper<PriceFrameEntity> queryWrapper = Wrappers.<PriceFrameEntity>query().in("status", statusList);

        return count(queryWrapper);
    }

    @Override
    public IPage<PriceFrameEntity> myPage(IPage<PriceFrameEntity> page, PriceFrameEntity priceFrameEntity) {
        BladeUser user = AuthUtil.getUser();
        List<Integer> statusList = Arrays.asList(STATUS_AUDIT, STATUS_SUCCESS, STATUS_REJECT);

        Wrapper<PriceFrameEntity> queryWrapper = Condition.getQueryWrapper(new PriceFrameEntity())
            .in("status", statusList)
            .eq("create_user", user.getUserId())
            .like(StringUtils.isNotBlank(priceFrameEntity.getItemCode()), "item_code", priceFrameEntity.getItemCode())
            .like(StringUtils.isNotBlank(priceFrameEntity.getItemName()), "item_name", priceFrameEntity.getItemName())
            .like(StringUtils.isNotBlank(priceFrameEntity.getSupCode()), "sup_code", priceFrameEntity.getSupCode())
            .like(StringUtils.isNotBlank(priceFrameEntity.getSupName()), "sup_name", priceFrameEntity.getSupName())
            .orderByDesc("update_time");
        return baseMapper.selectPage(page, queryWrapper);
    }

    /**
     * 导入excel
     *
     * @return
     */
    @Transactional(rollbackFor = Exception.class)
    @Override
    public Map<String, Object> importExcel(MultipartFile file) throws Exception {
        List<PriceFrameExcelDTO> dtoList = ExcelUtils.importExcel(file, 0, 1, PriceFrameExcelDTO.class);
        List<PriceFrameEntity> entityList = BeanUtil.copy(dtoList, PriceFrameEntity.class);
        Map<String, Object> map = new HashMap<>(2);
        for (PriceFrameEntity entity : entityList) {
            //导入时同新增区间卡控
            PriceFrameEntity result = saveCheck(entity);
            if (!StringUtil.isEmpty(result)) {
                map.put("msg", result.getItemCode() + "该采购数量区间" + result.getLimitMin() + "-" + result.getLimitMax() + "已存在，若需修改，请将冲突的旧数据失效后在录入");
                map.put("flag", false);
                break;
                //throw new RuntimeException(result.getItemCode() + "该采购数量区间" + result.getLimitMin() + "-" + result.getLimitMax() + "已存在，若需修改，请将冲突的旧数据失效后在录入");
            }
            Supplier supplier = supplierService.getByCode(entity.getSupCode());
            if (supplier == null) {
                continue;
            }
            entity.setSupName(supplier.getName());
            if (!save(entity)) {
                continue;
            }
            ;
            CheckDTO checkDto = new CheckDTO();
            checkDto.setId(entity.getId());
            checkDto.setStatus(STATUS_SUCCESS);
            check(checkDto);
            map.put("msg", "导入成功");
            map.put("flag", true);
        }
        return map;
    }

    @Override
    public IPage<PriceFrameEntity> selectPage(IPage<PriceFrameEntity> page, PriceFrameEntity priceFrameEntity) {
        QueryWrapper<PriceFrameEntity> queryWrapper = getQueryWrapper(priceFrameEntity);
        return page(page, queryWrapper);
    }

    @Override
    public IPage<PriceFrameEntity> getPriceFrame(IPage<PriceFrameEntity> page, U9PrEntity u9PrEntity) {
        QueryWrapper<PriceFrameEntity> queryWrapper = Condition.getQueryWrapper(new PriceFrameEntity()).orderByDesc("limit_min");
        SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd");
        queryWrapper.eq("item_code", u9PrEntity.getItemCode())
            .ge("expiration_date", sdf.format(new Date()))
            .le("effective_date", sdf.format(new Date()))
            .le("limit_min", u9PrEntity.getPriceNum())
            .eq("status", STATUS_SUCCESS);
        page.setSize(1);
        return page(page, queryWrapper);
    }

    /**
     * 导出
     */
    @Override
    public void export(PriceFrameEntity priceFrameEntity, Query query, HttpServletResponse response) throws Exception {
        QueryWrapper<PriceFrameEntity> qw = getQueryWrapper(priceFrameEntity);
        List<PriceFrameEntity> entityList = list(qw);
        if (entityList == null) {
            throw new Exception("暂无数据");
        }
        List<PriceFrameExcelDTO> excelList = Lists.newArrayList();
        for (PriceFrameEntity entity : entityList) {
            PriceFrameExcelDTO dto = BeanUtil.copy(entity, PriceFrameExcelDTO.class);
            excelList.add(dto);
        }

        ExcelUtils.defaultExport(excelList, PriceFrameExcelDTO.class, "框架协议" + DateUtil.formatDate(new Date()), response);

    }

    @Override
    public IPage<CenterPriceFrame> center(IPage<CenterPriceFrame> page, PriceFrameEntity priceFrameEntity) {
        if (priceFrameEntity.getStatus() == 99) {
            IPage<U9PrEntity> u9page = u9PrService.getU9Page(new Page<>(page.getCurrent(), page.getSize(), page.getTotal()));
            List<CenterPriceFrame> centerPriceFrames = Lists.newArrayList();
            for (U9PrEntity record : u9page.getRecords()) {
                if (getByPr(record.getItemCode(), record.getPriceNum()).size() == 0) {
                    CenterPriceFrame temp = new CenterPriceFrame();
                    temp.setPrId(record.getId());
                    temp.setPrCode(record.getPrCode());
                    temp.setItemCode(record.getItemCode());
                    temp.setItemName(record.getItemName());
                    temp.setPriceNum(record.getPriceNum());
                    temp.setPriceUom(record.getPriceUom());
                    temp.setReqDate(record.getReqDate());
                    temp.setRemark(record.getCheckRemark());

                    temp.setAvailableQuantity(record.getAvailableQuantity());
                    temp.setProjectOccupancyNum(record.getProjectOccupancyNum());
                    temp.setRequisitionRemark(record.getRequisitionRemark());
                    Item item = itemService.getByCode(record.getItemCode());
                    if(item != null){
                        temp.setPurchMix(item.getPurchMix());
                        temp.setStockLowerLimit(item.getStockLowerLimit());
                    }

                    centerPriceFrames.add(temp);
                } else {
                    for (PriceFrameEntity frameEntity : getByPr(record.getItemCode(), record.getPriceNum())) {
                        CenterPriceFrame temp = new CenterPriceFrame();
                        temp.setPrId(record.getId());
                        temp.setPrCode(record.getPrCode());
                        temp.setItemCode(record.getItemCode());
                        temp.setItemName(record.getItemName());
                        temp.setPriceNum(record.getPriceNum());
                        temp.setPriceUom(record.getPriceUom());
                        temp.setReqDate(record.getReqDate());
                        temp.setPfId(frameEntity.getId());
                        temp.setSupName(frameEntity.getSupName());
                        temp.setPrice(frameEntity.getPrice());
                        temp.setRemark(record.getCheckRemark());
                        temp.setReferencePrice(mmVolumeCalculateService.getPrice(frameEntity.getItemCode(), frameEntity.getSupCode()));

                        temp.setAvailableQuantity(record.getAvailableQuantity());
                        temp.setProjectOccupancyNum(record.getProjectOccupancyNum());
                        temp.setRequisitionRemark(record.getRequisitionRemark());
                        Item item = itemService.getByCode(record.getItemCode());
                        if(item != null){
                            temp.setPurchMix(item.getPurchMix());
                            temp.setStockLowerLimit(item.getStockLowerLimit());
                        }
                        centerPriceFrames.add(temp);
                    }
                }
            }
            IPage<CenterPriceFrame> framePage = new Page<>(u9page.getCurrent(), u9page.getSize(), u9page.getTotal());
            framePage.setRecords(centerPriceFrames);
            return framePage;
        } else {
            return ioService.getByStatus(page, priceFrameEntity.getStatus());
        }
    }


    @Override
    public List<Map<String, Object>> countList() {
        List<Map<String, Object>> result = Lists.newArrayList();
        Integer countCenter = this.baseMapper.countCenter(STATUS_SUCCESS);
        result.add(new ArrayMap<String, Object>() {{
            put("status", 99 + "");
            put("title", "待询交期");
            put("count", countCenter);
        }});
        result.add(new ArrayMap<String, Object>() {{
            put("status", 98 + "");
            put("title", "待确认");
            put("count", ioService.getStatusCount());
        }});
        return result;
    }

    @Transactional(rollbackFor = Exception.class)
    @Override
    public boolean submitDates(List<CenterPriceFrame> centerPriceFrames) {
        boolean result = false;
        for (CenterPriceFrame centerPriceFrame : centerPriceFrames) {
            U9PrEntity u9PrEntity = u9PrService.getById(centerPriceFrame.getPrId());
            PriceFrameEntity priceFrameEntity = getById(centerPriceFrame.getPfId());
            if(priceFrameEntity == null){
                throw new RuntimeException("料号：" + u9PrEntity.getItemCode() + ",数量：" + u9PrEntity.getPriceNum() +
                    " 未匹配到框架协议，请维护好框架协议");
            }
            IoEntity ioEntity = new IoEntity();
            ioEntity.setPrId(u9PrEntity.getId());
            ioEntity.setPrLn(u9PrEntity.getPrLn());
            ioEntity.setPrCode(u9PrEntity.getPrCode());
            ioEntity.setItemCode(u9PrEntity.getItemCode());
            ioEntity.setItemName(u9PrEntity.getItemName());
            if (!StringUtil.isEmpty(priceFrameEntity)) {
                ioEntity.setSupCode(priceFrameEntity.getSupCode());
                ioEntity.setSupName(priceFrameEntity.getSupName());
                ioEntity.setQuotePrice(priceFrameEntity.getPrice());
            }
            ioEntity.setQuoteEndtime(u9PrEntity.getQuoteEndtime());
            ioEntity.setPriceNum(u9PrEntity.getPriceNum());
            ioEntity.setPriceUom(u9PrEntity.getPriceUom());
            ioEntity.setTcNum(u9PrEntity.getTcNum());
            ioEntity.setTcUom(u9PrEntity.getTcUom());
            ioEntity.setReqDate(u9PrEntity.getReqDate());
            ioEntity.setBizBranch(IIoService.BIZ_PRICE_FRAME);
            ioEntity.setStatus(IIoService.STATUS_INIT);
            ioEntity.setSource("quote");
            result = ioService.save(ioEntity);

            //pr状态改为询交期31
            u9PrEntity.setStatus(IU9PrService.STATUS_INQUIRYDATE);
            u9PrService.updateById(u9PrEntity);
        }
        return result;
    }

    @Transactional(rollbackFor = Exception.class)
    @Override
    public boolean submitBatch(List<CenterPriceFrame> centerPriceFrames) {
        boolean result = false;
        for (CenterPriceFrame centerPriceFrame : centerPriceFrames) {
            U9PrEntity u9PrEntity = u9PrService.getById(centerPriceFrame.getPrId());
            IoEntity ioEntity = ioService.getById(centerPriceFrame.getId());
            //确认70
            if (IU9PrService.STATUS_WAIT.equals(centerPriceFrame.getStatus())) {
                u9PrEntity.setStatus(IU9PrService.STATUS_WAIT);
                u9PrService.updateById(u9PrEntity);
                ioEntity.setStatus(IIoService.STATUS_WAIT);
                if(!StringUtil.isEmpty(centerPriceFrame.getPromiseDate())){
                    ioEntity.setPromiseDate(centerPriceFrame.getPromiseDate());
                }
                if(!StringUtil.isEmpty(centerPriceFrame.getRemark())){
                    ioEntity.setRemark(centerPriceFrame.getRemark());
                }
                result = ioService.updateById(ioEntity);

                //生成待下单
                poService.placeOrderByIo(ioEntity, IPoItemService.SOURCE_PROTOCOL, centerPriceFrame.getId());
            }
            //流标40
            if (IU9PrService.STATUS_FLOW.equals(centerPriceFrame.getStatus())) {
                IoDTO ioDTO =  BeanUtil.copy(ioEntity, IoDTO.class);
                ioDTO.setFlowType(centerPriceFrame.getFlowType());
                result = ioService.letIoFlow(ioDTO);
            }
        }
        return result;
    }

    @Override
    public PriceFrameEntity saveCheck(PriceFrameEntity priceFrameEntity) {
        return this.baseMapper.saveCheck(priceFrameEntity.getItemCode(), priceFrameEntity.getSupCode(),
            priceFrameEntity.getEffectiveDate(), priceFrameEntity.getLimitMin());
    }

    @Override
    public BigDecimal getLimitMin(PriceFrameEntity priceFrameEntity) {
        return this.baseMapper.getLimitMin(priceFrameEntity.getItemCode(), priceFrameEntity.getSupCode());
    }

    @Override
    public String checkOnly(List<CenterPriceFrame> centerPriceFrames) {
        BladeUser user = AuthUtil.getUser();
        List<PriceFrameVO> voList = this.baseMapper.getGroupByCount(user.getUserId());
        //根据物料编号分组
        Map<String, List<PriceFrameEntity>> map = new HashMap<>();
        for (CenterPriceFrame centerPriceFrame : centerPriceFrames) {
            PriceFrameEntity priceFrameEntity = getById(centerPriceFrame.getId());
            if (map.containsKey(centerPriceFrame.getItemCode())) {
                map.get(centerPriceFrame.getItemCode()).add(priceFrameEntity);
            } else {
                List<PriceFrameEntity> priceFrameEntities = new ArrayList<>();
                priceFrameEntities.add(priceFrameEntity);
                map.put(centerPriceFrame.getItemCode(), priceFrameEntities);
            }
        }
        StringBuilder sb = new StringBuilder();
        voList.forEach(vo -> {
            if(!StringUtil.isEmpty(map.get(vo.getItemCode()))){
                if (vo.getCount() - map.get(vo.getItemCode()).size() < 1) {
                    sb.append(vo.getItemCode());
                    sb.append(",");
                }
            }
        });
        return sb.length() > 0 ? sb.substring(0, sb.length() - 1) : "";
    }

    @Transactional(rollbackFor = Exception.class)
    @Override
    public boolean invalids(List<CenterPriceFrame> centerPriceFrames) {
        boolean result = false;
        for (CenterPriceFrame centerPriceFrame : centerPriceFrames) {
            PriceFrameEntity priceFrameEntity = getById(centerPriceFrame.getId());
            //是否为最后一个物料
            if(getByItemCode(priceFrameEntity.getItemCode()).size() == 1){
                Item item = itemService.getByCode(priceFrameEntity.getItemCode());
                //是否为框架协议
                if(item.getPurchAttr().equals(IItemService.PUR_ATTR_PROTOCOL)){
                    item.setPurchAttr("");
                    itemService.updateById(item);
                }
            }
            result = removeById(priceFrameEntity);
        }
        return result;
    }

    @Override
    public int auditCount() {
        Wrapper<PriceFrameEntity> queryWrapper = Condition.getQueryWrapper(new PriceFrameEntity())
            .eq("status", STATUS_AUDIT);
        return count(queryWrapper);
    }

    @Override
    public BigDecimal getPrice(String itemCode, String supCode, BigDecimal priceNum) {
        QueryWrapper<PriceFrameEntity> queryWrapper = Condition.getQueryWrapper(new PriceFrameEntity());
        queryWrapper.eq("status", STATUS_SUCCESS);
        queryWrapper.like("item_code", itemCode);
        queryWrapper.like("sup_code", supCode);
        queryWrapper.le("limit_min", priceNum);
        queryWrapper.ge("limit_max", priceNum);
        queryWrapper.le("effective_date", new Date());
        queryWrapper.ge("expiration_date", new Date());
        PriceFrameEntity priceFrameEntity = getOne(queryWrapper);
        if(priceFrameEntity == null){
            return null;
        }
        return priceFrameEntity.getPrice();
    }

    @Override
    public List<PriceFrameEntity> getNumberInterval(String itemCode, BigDecimal priceNum) {
        QueryWrapper<PriceFrameEntity> queryWrapper = Condition.getQueryWrapper(new PriceFrameEntity()).orderByDesc("create_time");
        queryWrapper.eq("status", STATUS_SUCCESS);
        queryWrapper.le("limit_min", priceNum);
        queryWrapper.ge("limit_max", priceNum);
        queryWrapper.le("effective_date", new Date());
        queryWrapper.ge("expiration_date", new Date());
        queryWrapper.eq("item_code", itemCode);
        return list(queryWrapper);
    }


    /**
     * getByPr
     *
     * @param itemCode String
     * @param priceNum BigDecimal
     * @return
     */
    private List<PriceFrameEntity> getByPr(String itemCode, BigDecimal priceNum) {
        return this.baseMapper.getByPr(itemCode, priceNum);
    }

    /**
     * getByItemCode
     *
     * @param itemCode String
     * @return List
     */
    private List<PriceFrameEntity> getByItemCode(String itemCode) {
        QueryWrapper<PriceFrameEntity> queryWrapper = Condition.getQueryWrapper(new PriceFrameEntity());
        queryWrapper.eq("item_code", itemCode);
        queryWrapper.le("effective_date", new Date());
        queryWrapper.ge("expiration_date", new Date());
        return list(queryWrapper);
    }

}

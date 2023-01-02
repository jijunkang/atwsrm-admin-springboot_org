package org.springblade.modules.mathmodel.service.impl;

import com.baomidou.mybatisplus.core.conditions.Wrapper;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import org.apache.commons.lang3.StringUtils;
import org.springblade.common.utils.ExcelUtils;
import org.springblade.core.mp.base.BaseServiceImpl;
import org.springblade.core.mp.support.Condition;
import org.springblade.core.mp.support.Query;
import org.springblade.core.tool.utils.BeanUtil;
import org.springblade.core.tool.utils.DateUtil;
import org.springblade.modules.mathmodel.entity.CastingOrderEntity;
import org.springblade.modules.mathmodel.mapper.CastingOrderMapper;
import org.springblade.modules.mathmodel.service.CastingOrderService;
import org.springblade.modules.supplier.service.ISupplierService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import static org.springblade.common.utils.ItemAnalysisUtil.*;
import javax.servlet.http.HttpServletResponse;
import java.math.BigDecimal;
import java.math.RoundingMode;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

/**
 * Author: 昕月
 * Date：2022/6/7 19:53
 * Desc:
 */
@Service
public class CastingOrderServiceimpl extends BaseServiceImpl<CastingOrderMapper, CastingOrderEntity> implements CastingOrderService {

    @Autowired
    private CastingOrderMapper castingOrderMapper;

    @Autowired
    private ISupplierService supplierService;


    @Override
    public Wrapper<CastingOrderEntity> getQueryWrapper(CastingOrderEntity castingOrder) {

        QueryWrapper<CastingOrderEntity> queryWrapper = Condition.getQueryWrapper(castingOrder);
//        QueryWrapper<CastingOrderEntity> queryWrapper = new QueryWrapper<>(castingOrder);
        return queryWrapper;
    }

    @Override
    public void export(CastingOrderEntity castingOrder, Query query, HttpServletResponse response) {
        List<CastingOrderEntity> list = list(getQueryWrapper(castingOrder));
        if (list.size() == 0){
            throw new RuntimeException("暂无数据,数据导出失败");
        }
        ArrayList<CastingOrderEntity> excel = new ArrayList<>();
        list.forEach( entity ->{
            CastingOrderEntity order = BeanUtil.copy(entity, CastingOrderEntity.class);
            excel.add(order);
        });
        ExcelUtils.defaultExport(excel,CastingOrderEntity.class,"铸件自动下单报表"+ DateUtil.formatDate(new Date()),response);
    }


    /**
     * 铸件
     * @param castingOrder
     * @return
     */
    @Override
    public List<CastingOrderEntity> submitCastingReport(CastingOrderEntity castingOrder) {
        List<CastingOrderEntity> list = new ArrayList<>();
        CastingOrderEntity entity = getItemInfoZhuJian(castingOrder.getItemDesc());
            if (entity.getSeries() != null){
                // 查找多家供应商
                entity.setSupCode(castingOrder.getSupCode());
                List<CastingOrderEntity> castingOrderEntityList = this.baseMapper.selectSupName(entity);
                String price = "";
                //遍历供应商
                if (castingOrderEntityList.size()>0){

                    for (CastingOrderEntity castingOrderEntity : castingOrderEntityList) {
                        //获取加工费
                        String processPrice = castingOrderEntity.getCharge();
                        if (StringUtils.isEmpty(processPrice)){
                            processPrice = "0";
                        }
                        price = this.baseMapper.selectItemPrice(castingOrderEntity);

                        //若能找到单价
                        if (StringUtils.isNotEmpty(price)){
                            CastingOrderEntity orderEntity = new CastingOrderEntity();
                            orderEntity.setPrCode(castingOrder.getPrCode()); // 物料分类
                            orderEntity.setPrLn(castingOrder.getPrLn()); // 物料分类
                            orderEntity.setItemize(castingOrderEntity.getItemize()); // 物料分类
                            orderEntity.setItemSize(entity.getItemSize()); // 尺寸
                            orderEntity.setForm(castingOrderEntity.getForm()); // 形式
                            orderEntity.setPound(castingOrderEntity.getPound()); // 磅级
                            orderEntity.setFlange(castingOrderEntity.getFlange()); //法兰结构
                            orderEntity.setMaterial(castingOrderEntity.getMaterial()); //材质
                            orderEntity.setItemCode(castingOrder.getItemCode());  // 物料编号
                            orderEntity.setItemDesc(castingOrder.getItemDesc());  // 物料名称
                            orderEntity.setSupCode(castingOrderEntity.getSupCode());  // 供应商编码
                            orderEntity.setSupName(castingOrderEntity.getSupName());  // 供应商名称
                            orderEntity.setCharge(processPrice); // 加工费
                            orderEntity.setSeries(castingOrderEntity.getSeries()); // 系列
                            orderEntity.setWeight(castingOrderEntity.getWeight());  // 单重
                            orderEntity.setPriceNum(castingOrder.getPriceNum()); // 数量
                            orderEntity.setCreateTime(new Date());
                            orderEntity.setQuotePrice(new BigDecimal(price).multiply(new BigDecimal(orderEntity.getWeight())).add(new BigDecimal(processPrice)).setScale(2, RoundingMode.HALF_UP));
                            orderEntity.setAmount(castingOrder.getPriceNum().multiply(orderEntity.getQuotePrice()).toString());

                            this.baseMapper.insert(orderEntity);
                            list.add(orderEntity);
                        }
                    }
                }
            }
        return list;
    }
}

package org.springblade.modules.mathmodel.service;

import org.springblade.core.mp.base.BaseService;
import org.springblade.modules.mathmodel.entity.MmSizeEntity;
import org.springblade.modules.mathmodel.entity.MmVolumeCalculateEntity;
import org.springblade.modules.mathmodel.vo.MmVolumeCalculateVO;

import java.math.BigDecimal;
import java.util.List;
import java.util.Map;


/**
 * @author libin
 *
 * @date 11:16 2020/9/11
 **/
public interface IMmVolumeCalculateService  extends BaseService<MmVolumeCalculateEntity> {


    /**
     * 批量选中计算
     *
     * @param mmSizeEntity MmSizeEntity
     * @return boolean
     */
    boolean countBatch(MmSizeEntity mmSizeEntity);

    /**
     * 重新计算全部价格
     *
     * @return boolean
     */
    boolean countAll();

    /**
     * 根据供应商编码和物料编号获取参考价格
     *
     * @param itemCode String
     * @param supCode String
     * @return
     */
    BigDecimal getPrice(String itemCode, String supCode);

    /**
     * 获取价格
     *
     * @param mmVolumeCalculateVO MmVolumeCalculateVO
     * @return Map
     */
    Map<String, Object> getPriceByCode(MmVolumeCalculateVO mmVolumeCalculateVO);

    /**
     * 根据料号查询价格表
     *
     * @param itemCode String
     * @return List
     */
    List<MmVolumeCalculateEntity> getByItemCode(String itemCode);
}

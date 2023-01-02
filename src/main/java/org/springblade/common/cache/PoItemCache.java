package org.springblade.common.cache;


import cn.afterturn.easypoi.excel.entity.params.ExcelExportEntity;
import org.springblade.core.cache.utils.CacheUtil;
import org.springblade.core.tool.utils.SpringUtil;
import org.springblade.modules.po.dto.PoItemReqRepotCurrMonthDTO;
import org.springblade.modules.po.entity.PoItemEntity;
import org.springblade.modules.po.entity.PoItemReqRepotTotal;
import org.springblade.modules.po.service.IPoItemService;
import org.springblade.modules.po.vo.PoItemReqRepotVO;

import java.util.List;
import java.util.Map;

import static org.springblade.core.cache.constant.CacheConstant.BIZ_CACHE;

/**
 * @author libin
 * @date 10:54 2020/7/16
 **/
public class PoItemCache {

    private static final String PO_CACHE_ID = "po:key:";

    private static IPoItemService poItemService;

    private static final String KEY_VO = "allVoList";
    private static final String KEY_ENTITY = "allEntity";
    private static final String KEY_VALUE = "allValues";


    static {
        poItemService = SpringUtil.getBean(IPoItemService.class);
    }

    /**
     * getAllPo
     *
     * @return List
     */
    public static List<PoItemReqRepotVO> getVoList() {
        return CacheUtil.get(BIZ_CACHE, PO_CACHE_ID, KEY_VO, () -> null);
    }

    /**
     * getAllPo
     *
     */
    public static void putVoList() {
        List<PoItemReqRepotTotal> totalList = poItemService.getTotalList(new PoItemEntity());
        CacheUtil.put(BIZ_CACHE, PO_CACHE_ID, KEY_VO, poItemService.getVoList(totalList));
    }


    /**
     * getAllEntity
     *
     * @return List
     */
    public static List<ExcelExportEntity> getAllEntity() {
        return CacheUtil.get(BIZ_CACHE, PO_CACHE_ID, KEY_ENTITY, () -> null);
    }


    /**
     * putAllEntity
     *
     */
    public static void putAllEntity() {
        List<PoItemReqRepotVO> voList = getVoList();
        List<PoItemReqRepotCurrMonthDTO> dateTitle = voList.get(0).getColumnValues();
        CacheUtil.put(BIZ_CACHE, PO_CACHE_ID, KEY_ENTITY, poItemService.getAllEntity(dateTitle));
    }

    /**
     * getValueList
     *
     * @return List
     */
    public static List<Map<String, Object>> getValueList() {
        return CacheUtil.get(BIZ_CACHE, PO_CACHE_ID, KEY_VALUE, () -> null);
    }


    /**
     * putValueList
     *
     */
    public static void putValueList() {
        List<PoItemReqRepotVO> voList = getVoList();
        List<PoItemReqRepotCurrMonthDTO> dateTitle = voList.get(0).getColumnValues();
        List<ExcelExportEntity> entity = getAllEntity();
        CacheUtil.get(BIZ_CACHE, PO_CACHE_ID, KEY_VALUE, () -> poItemService.getValueList(voList, dateTitle, entity));
    }
}

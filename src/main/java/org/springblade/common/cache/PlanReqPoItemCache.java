package org.springblade.common.cache;


import cn.afterturn.easypoi.excel.entity.params.ExcelExportEntity;
import org.springblade.core.cache.utils.CacheUtil;
import org.springblade.core.tool.utils.SpringUtil;
import org.springblade.modules.po.dto.PoItemReqRepotCurrMonthDTO;
import org.springblade.modules.po.entity.PoItemEntity;
import org.springblade.modules.po.entity.PoItemReqRepotTotal;
import org.springblade.modules.po.service.IPoItemService;
import org.springblade.modules.po.vo.PoItemNewReportVO;
import org.springblade.modules.po.vo.PoItemNewReportVO;

import java.util.List;
import java.util.Map;

import static org.springblade.core.cache.constant.CacheConstant.BIZ_CACHE;

/**
 * @author libin
 *
 * @date 15:36 2020/10/23
 **/
public class PlanReqPoItemCache {

    private static final String PO_CACHE_ID = "planPo:key:";

    private static IPoItemService poItemService;

    private static final String KEY_VO = "planPoVoList";
    private static final String KEY_ENTITY = "planPoEntities";
    private static final String KEY_VALUE = "planPoValues";
    private static final String KEY_EXCEL_VO = "planExcelVoList";


    static {
        poItemService = SpringUtil.getBean(IPoItemService.class);
    }

    /**
     * getVoList
     *
     * @return List
     */
    public static List<PoItemNewReportVO> getVoList() {
        return CacheUtil.get(BIZ_CACHE, PO_CACHE_ID, KEY_VO, () -> null);
    }

    /**
     * putVoList
     *
     */
    public static void putVoList() {
        List<PoItemReqRepotTotal> totalList = poItemService.getNewTotalList(new PoItemEntity());
        CacheUtil.put(BIZ_CACHE, PO_CACHE_ID, KEY_VO, poItemService.getNewVoList(totalList));
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
        List<PoItemNewReportVO> voList = getVoList();
        List<PoItemReqRepotCurrMonthDTO> dateTitle = voList.get(0).getColumnValues();
        CacheUtil.put(BIZ_CACHE, PO_CACHE_ID, KEY_ENTITY, poItemService.getNewAllEntity(dateTitle));
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
        List<PoItemNewReportVO> voList = getVoList();
        List<PoItemReqRepotCurrMonthDTO> dateTitle = voList.get(0).getColumnValues();
        List<ExcelExportEntity> entity = getAllEntity();
        CacheUtil.get(BIZ_CACHE, PO_CACHE_ID, KEY_VALUE, () -> poItemService.getNewValueList(voList, dateTitle, entity));
    }


    /**
     * getExportVoList
     *
     * @return List
     */
    public static List<PoItemNewReportVO> getExportVoList() {
        return CacheUtil.get(BIZ_CACHE, PO_CACHE_ID, KEY_EXCEL_VO, () -> null);
    }

    /**
     * putExportVoList
     *
     */
    public static void putExportVoList() {
        List<PoItemReqRepotTotal> totalList = poItemService.getNewTotalList(new PoItemEntity());
        CacheUtil.put(BIZ_CACHE, PO_CACHE_ID, KEY_VO, poItemService.getExportVoList(totalList));
    }
}

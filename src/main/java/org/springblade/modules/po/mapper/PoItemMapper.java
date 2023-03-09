package org.springblade.modules.po.mapper;

import com.baomidou.dynamic.datasource.annotation.DS;
import com.baomidou.mybatisplus.annotation.SqlParser;
import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import org.apache.ibatis.annotations.Param;
import org.apache.ibatis.annotations.Select;
import org.apache.ibatis.annotations.Update;
import org.springblade.modules.po.dto.PoItemDTO;
import org.springblade.modules.po.dto.PoItemReqRepotCurrMonthDTO;
import org.springblade.modules.po.entity.PoEntity;
import org.springblade.modules.po.entity.PoItemEntity;
import org.springblade.modules.po.entity.PoItemReqRepotTotal;
import org.springblade.modules.po.entity.PoItemReqRepotTotal2;
import org.springblade.modules.po.vo.PoItemNewReportVO;
import org.springblade.modules.po.vo.PoItemExcelVO;
import org.springblade.modules.po.vo.PoItemNewVO;
import org.springblade.modules.po.vo.PoItemVO;
import org.springblade.modules.supplier.entity.CaiGouSchedule;
import org.springframework.core.annotation.Order;

import java.math.BigDecimal;
import java.util.List;
import java.util.Set;

/**
 * 采购订单明细 Mapper 接口
 *
 * @author Will
 */
public interface PoItemMapper extends BaseMapper<PoItemEntity> {

    BigDecimal getHighestPrice(String itemCode);

    BigDecimal getLowestPrice(String itemCode);

    PoItemEntity getLastPoInfo(@Param("itemCode")String itemCode,@Param("itemName")String itemName);

    IPage<PoItemVO> pageWithPr(IPage<Object> page,PoItemDTO poItem );

    IPage<PoItemVO> pageWithPrList(IPage<Object> page,PoItemDTO poItem );

    List<PoItemVO> fzPrList(@Param("poItem") PoItemDTO poItem);

    IPage<PoItemVO> pageWithItemPoContract(IPage<Object> page,PoItemDTO poItem );

    IPage<PoItemReqRepotTotal> getPoItemReqRepotTotal(IPage<Object> page, PoItemEntity poItemEntity);

    IPage<PoItemReqRepotTotal> getNewReportTotal(IPage<Object> page, PoItemEntity poItemEntity);

    IPage<PoItemReqRepotTotal2> getNewReportTotal2(IPage<Object> page, PoItemEntity poItemEntity);

    List<String> planreqreportsendEmail(IPage<Object> page, PoItemEntity poItemEntity);

    List<PoItemReqRepotTotal> getNewReportTotal(@Param("poItemEntity") PoItemEntity poItemEntity);

    List<PoItemReqRepotTotal2> getNewReportTotal2(@Param("poItemEntity") PoItemEntity poItemEntity);

    List<PoItemReqRepotTotal> getPoItemReqRepotTotal(@Param("poItemEntity") PoItemEntity poItemEntity);


    List<PoItemEntity> getPoItems(
        @Param("supCode") String supCode,
        @Param("itemCode") String itemCode
    );

    PoItemReqRepotCurrMonthDTO getCurrMouth(
        @Param("supCode") String supCode,
        @Param("itemCode") String itemCode,
        @Param("time") String time
    );

    PoItemReqRepotCurrMonthDTO getNexHalfYear(
        @Param("supCode") String supCode,
        @Param("itemCode") String itemCode,
        @Param("time") String time
    );

    PoItemReqRepotCurrMonthDTO getNexHalfYearPredict(@Param("supCode") String supCode, @Param("itemCode") String itemCode, @Param("time") String time);

    String connectProNoByPoCode(@Param("poCodes") Set<String> poCodes);

    List<String> getRemindPoCodes(int days);

    List<PoItemEntity> getPoItemEntity(@Param("poItem") PoItemDTO poItem);

    IPage<PoItemEntity> getPoItemEntityPage(IPage<PoItemEntity> page, PoItemDTO poItem);

    PoItemNewReportVO getActualVo(@Param("supCode") String supCode, @Param("itemCode") String itemCode);

    PoItemNewReportVO getPredictVo(@Param("supCode") String supCode, @Param("itemCode") String itemCode);

    PoItemReqRepotCurrMonthDTO getActualColumnValue(@Param("supCode") String supCode, @Param("itemCode") String itemCode, @Param("fullDate") String fullDate);

    PoItemReqRepotCurrMonthDTO getPredictColumnValue(@Param("supCode") String supCode, @Param("itemCode") String itemCode, @Param("fullDate") String fullDate);

    PoItemReqRepotCurrMonthDTO getActualIsMeetOptDate(@Param("supCode") String supCode, @Param("itemCode") String itemCode, @Param("fullDate") String fullDate);

    PoItemReqRepotCurrMonthDTO getActualIsMeetOptDate2(@Param("supCode") String supCode, @Param("itemCode") String itemCode,@Param("poCode") String poCode,@Param("poLn") Integer poLn,@Param("proNo") String proNo, @Param("fullDate") String fullDate);

    PoItemReqRepotCurrMonthDTO getActualNextColumnValue(@Param("supCode") String supCode, @Param("itemCode") String itemCode, @Param("timeCurr") String timeCurr);

    PoItemReqRepotCurrMonthDTO getPredictNextColumnValue(@Param("supCode") String supCode, @Param("itemCode") String itemCode, @Param("timeCurr") String timeCurr);

    List<PoItemNewReportVO> getActualVos();

    List<PoItemNewReportVO> getActualVos2();

    List<PoItemNewReportVO> getPredictVos();

    List<PoItemReqRepotCurrMonthDTO> getActualColumnValues();

    List<PoItemReqRepotCurrMonthDTO> getActualColumnValues2();

    List<PoItemReqRepotCurrMonthDTO> getPredictColumnValues();

    List<PoItemReqRepotCurrMonthDTO> getActualNextColumnValues();

    List<PoItemReqRepotCurrMonthDTO> getActualNextColumnValues2();

    List<PoItemReqRepotCurrMonthDTO> getPredictNextColumnValues();

    List<PoItemVO> getHistoryPrice(IPage page, String itemCode);

    List<PoItemVO> getExcelData(@Param("poItemEntity") PoItemEntity poItemEntity);

    @Select("select COALESCE(SUM(rcv_num),0) from atw_po_receive where pi_id = #{piId} and is_deleted = 0 and status <=30")
    Integer getRcvAllNumByPiId(String piId);

    @Select("select count(*) from atw_over_charge where main_code = #{mainCode}")
    Integer getOverCharge(String mainCode);

    @Select("select IFNULL(purch_email,'') from atw_supplier where code = #{supCode} and primary_contact = 1 and is_deleted = 0")
    String getPurchEmail(String supCode);

    @Select("select purch_code from atw_item where code = #{itemCode} and is_deleted = 0")
    String getPurchCode(String itemCode);

    @Select("select * from atw_po_item where po_code=#{pocode} and po_ln=#{poln} and is_deleted=0")
    PoItemDTO getPOItemByPOCode(@Param("pocode") String pocode,@Param("poln") String poln);

    @Select("select po.* from atw_po po left join atw_po_item pi on pi.po_id = po.id where pi.po_code like 'VMI%' AND pi.item_code = #{itemCode} AND pi.sup_code = #{supCode}")
    List<PoEntity> getVmiPoInfo(@Param("itemCode") String itemCode,@Param("supCode") String supCode);

    @Select("select s_or_n from atw_item where code =#{code} and is_deleted=0")
    String getABCType(@Param("code") String code);

    @Select("select count(*) from ust_print_zj where po_code = #{poCode} and po_ln = #{poLn}")
    Integer getUstPrintZJ(@Param("poCode") String poCode,@Param("poLn") String poLn);

    @Update("update atw_u9_pr set is_urgent = 1 where pr_code = #{pr_code} and pr_ln = #{pr_ln}")
    boolean setIsUrgent(@Param("pr_code")String prCode,@Param("pr_ln") String prLn);

    @Update("update atw_po_receive t set t.status=30  where t.rcv_code=#{do_code}")
    boolean closedofromESB(@Param("do_code")String DoCode);

    @Update("update atw_u9_pr set is_urgent = 0 where pr_code = #{pr_code} and pr_ln = #{pr_ln}")
    boolean cancelIsUrgent(@Param("pr_code")String prCode,@Param("pr_ln") String prLn);


    @DS("oracle")
    @SqlParser(filter = true)
    IPage<PoItemEntity> getPoItemEntityPageFromOracle(IPage<PoItemEntity> page, @Param("poItem")PoItemDTO poItem);

    @DS("oracle")
    @SqlParser(filter = true)
    List<PoItemEntity> getPoItemEntityPageFromOracleList(@Param("poItem")PoItemDTO poItem);

    @Select("select * from atw_po_item where is_reserve='Y'")
    List<PoItemEntity> selectPoItemLock();

    @Select("select * from atw_caigou_plan_data_lock where po_code=#{poCode} and po_ln=#{poLn} ORDER BY req_date LIMIT 1")
    CaiGouSchedule isLocked(@Param("poCode") String poCode, @Param("poLn") String poLn);

    @Update("update atw_po_item set sup_confirm_date=#{SupConfirmDate},is_reserve='' where po_code=#{poCode} and po_ln=#{poLn}")
    Boolean updateSupConfirmDateJOB(@Param("SupConfirmDate")Long SupConfirmDate,@Param("poCode") String poCode, @Param("poLn") String poLn);

    @Update("update atw_po_item set sup_confirm_date=#{SupConfirmDate} where pr_code=#{poCode} and pr_ln=#{poLn}")
    Boolean updateSupConfirmDate(@Param("SupConfirmDate")Long SupConfirmDate,@Param("poCode") String poCode, @Param("poLn") String poLn);

    @Select("select * from atw_po where is_reserve='Y' and  id not in (select DISTINCT a.id from atw_po a left join atw_po_item b on a.order_code=b.po_code where a.is_reserve='Y' and b.is_reserve='Y')")
    List<PoEntity> selectUnLockPo();

    @Update("update atw_po set is_reserve=''  where id=#{id}")
    Boolean updateIsReserve(@Param("id")Long id);
}

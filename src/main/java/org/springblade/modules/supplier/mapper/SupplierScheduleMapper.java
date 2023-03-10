package org.springblade.modules.supplier.mapper;

import com.baomidou.mybatisplus.annotation.SqlParser;
import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import io.swagger.models.auth.In;
import org.apache.ibatis.annotations.*;
import org.springblade.modules.po.entity.PoItemEntity;
import org.springblade.modules.po.entity.PoReceiveEntity;
import org.springblade.modules.po.entity.PoSnEntity;
import org.springblade.modules.pr.entity.ItemInfoEntityOfZDJ;
import org.springblade.modules.report.entity.AllOtdReport;
import org.springblade.modules.supplier.dto.CaiGouScheduleReq;
import org.springblade.modules.supplier.dto.SupplierScheduleReq;
import org.springblade.modules.supplier.entity.CaiGouSchedule;
import org.springblade.modules.supplier.entity.OtdReport;
import org.springblade.modules.supplier.entity.Supplier;
import org.springblade.modules.supplier.entity.SupplierSchedule;
import org.springblade.modules.supplier.vo.EchartVo;
import org.springblade.modules.supplier.vo.SupplierScheduleVO;
import org.springblade.modules.supplier.vo.SupplierVO;

import java.math.BigDecimal;
import java.util.Date;
import java.util.List;
import java.util.Map;

/**
 * 供应商 Mapper 接口
 *
 * @author Will
 */
public interface SupplierScheduleMapper {

    IPage<SupplierSchedule> getSupplierSchedule(IPage page, @Param("supplierSchedule") SupplierSchedule supplierSchedule);

    IPage<SupplierSchedule> getSupplierScheduleOfOms(IPage page, @Param("supplierScheduleReq") SupplierScheduleReq supplierScheduleReq);

    @SqlParser(filter = true) // 为了防止sql不被mybatisplus 识别
    IPage<CaiGouSchedule> getCaiGouSchedule(IPage page, @Param("caiGouScheduleReq") CaiGouScheduleReq caiGouScheduleReq);

    @SqlParser(filter = true)
    List<CaiGouSchedule> getCaiGouScheduleList(@Param("caiGouScheduleReq") CaiGouScheduleReq caiGouScheduleReq);

    @SqlParser(filter = true) // 为了防止sql不被mybatisplus 识别
    IPage<CaiGouSchedule> getCaiGouScheduleAutoSort(IPage page, @Param("caiGouScheduleReq") CaiGouScheduleReq caiGouScheduleReq);

    @SqlParser(filter = true)
    List<CaiGouSchedule> getCaiGouScheduleAutoSortList(@Param("caiGouScheduleReq") CaiGouScheduleReq caiGouScheduleReq);

    @SqlParser(filter = true) // 为了防止sql不被mybatisplus 识别
    IPage<CaiGouSchedule> getCaiGouScheduleUnchecked(IPage page, @Param("caiGouScheduleReq") CaiGouScheduleReq caiGouScheduleReq);

    @SqlParser(filter = true) // 为了防止sql不被mybatisplus 识别
    IPage<CaiGouSchedule> getCaiGouScheduleUnpip(IPage page, @Param("caiGouScheduleReq") CaiGouScheduleReq caiGouScheduleReq);

    @SqlParser(filter = true) // 为了防止sql不被mybatisplus 识别
    IPage<CaiGouSchedule> getCaiGouScheduleOffset(IPage page, @Param("caiGouScheduleReq") CaiGouScheduleReq caiGouScheduleReq);

    @SqlParser(filter = true) // 为了防止sql不被mybatisplus 识别
    List<CaiGouSchedule> getCaiGouScheduleUncheckedList(@Param("caiGouScheduleReq") CaiGouScheduleReq caiGouScheduleReq);

    @SqlParser(filter = true) // 为了防止sql不被mybatisplus 识别
    List<CaiGouSchedule> getCaiGouScheduleUnpipList(@Param("caiGouScheduleReq") CaiGouScheduleReq caiGouScheduleReq);

    @SqlParser(filter = true) // 为了防止sql不被mybatisplus 识别
    List<CaiGouSchedule> getCaiGouScheduleOffsetList(@Param("caiGouScheduleReq") CaiGouScheduleReq caiGouScheduleReq);


    int getSupplierScheduleCountOfOms(@Param("supplierScheduleReq") SupplierScheduleReq supplierScheduleReq);

    SupplierSchedule getSupplierScheduleByItemCodeAndSupNo(@Param("supplierSchedule") SupplierSchedule supplierSchedule);

    String getSenderEmail(String code);

    String getReceiverEmail(String code);

    boolean updateByItemCodeAndSupNo(@Param("supplierSchedule") SupplierSchedule supplierSchedule);

    boolean updateCaiGou(@Param("caiGouSchedule") CaiGouSchedule caiGouSchedule, @Param("updater") String updater, @Param("updateTime") String updateTime);

    boolean updateCaiGouNoProNo(@Param("caiGouSchedule") CaiGouSchedule caiGouSchedule, @Param("updater") String updater, @Param("updateTime") String updateTime);

    boolean insertCaiGou(@Param("caiGouSchedule") CaiGouSchedule caiGouSchedule, @Param("updater") String updater, @Param("updateTime") String updateTime);

    boolean insertCaiGouNoProNo(@Param("caiGouSchedule") CaiGouSchedule caiGouSchedule, @Param("updater") String updater, @Param("updateTime") String updateTime);

    boolean updateCaiGouRemark(@Param("caiGouSchedule") CaiGouSchedule caiGouSchedule, @Param("updater") String updater, @Param("updateTime") String updateTime);

    boolean insertCaiGouRemark(@Param("caiGouSchedule") CaiGouSchedule caiGouSchedule, @Param("updater") String updater, @Param("updateTime") String updateTime);

    @Select("SELECT count(*) from bi_supdeliv_plan_data_remark_write where po_code = #{caiGouSchedule.poCode} and po_ln = #{caiGouSchedule.poLn} and pro_no= #{caiGouSchedule.proNo}")
    Integer isExistedByRemark(@Param("caiGouSchedule") CaiGouSchedule caiGouSchedule);

    @Select("SELECT * from bi_supdeliv_plan_data_remark_write where po_code = #{caiGouSchedule.poCode} and po_ln = #{caiGouSchedule.poLn} and pro_no= #{caiGouSchedule.proNo} limit 1")
    CaiGouSchedule selectCaiGouSchedule(@Param("caiGouSchedule") CaiGouSchedule caiGouSchedule);

    boolean updateCaiGouSeq(@Param("caiGouSchedule") CaiGouSchedule caiGouSchedule);

    boolean insertCaiGouSeq(@Param("caiGouSchedule") CaiGouSchedule caiGouSchedule);



    @Select("SELECT count(*) from BI_Supdeliv_Plan_Data_Detail_Write where po_code = #{caiGouSchedule.poCode} and po_ln = #{caiGouSchedule.poLn}")
    Integer isExistedByCG(@Param("caiGouSchedule") CaiGouSchedule caiGouSchedule);

    @Select("SELECT * from BI_Supdeliv_Plan_Data_Detail_Write where po_code = #{caiGouSchedule.poCode} and po_ln = #{caiGouSchedule.poLn}")
    CaiGouSchedule getWriteInfo(@Param("caiGouSchedule") CaiGouSchedule caiGouSchedule);

    @Select("SELECT count(*) from BI_Supdeliv_Plan_Data_Detail_Lock where pro_no = #{caiGouSchedule.proNo} and po_code = #{caiGouSchedule.poCode} and po_ln = #{caiGouSchedule.poLn}")
    Integer isExistedByCGOfLock(@Param("caiGouSchedule") CaiGouSchedule caiGouSchedule);

    @Select("SELECT count(*) from BI_Supdeliv_Plan_Data_Detail_Seq where pro_no = #{caiGouSchedule.proNo}")
    Integer isExistedSeqByCG(@Param("caiGouSchedule") CaiGouSchedule caiGouSchedule);

    @Select("SELECT po.* FROM atw_po_receive po,atw_po_item pi WHERE po.pi_id = pi.id and pi.po_code = #{caiGouSchedule.poCode}  and pi.po_ln = #{caiGouSchedule.poLn} and po.is_deleted = 0 and pi.is_deleted = 0 ORDER BY update_time DESC limit 1")
    PoReceiveEntity getDoInfo(@Param("caiGouSchedule") CaiGouSchedule caiGouSchedule);

    @Select("SELECT NEXTVAL('TestSeq')")
    Integer selectNextTestSeq();

    List<CaiGouSchedule> selectNewHaveAndLockNotHave();

    List<CaiGouSchedule> selectLockHaveAndNewNotHave();


    @Select("SELECT DISTINCT pro_no from BI_Supdeliv_Plan_Data_Detail where pro_no is not null  ")
    List<String> selectAllProNo();

    @Delete("delete from atw_caigou_plan_data_lock WHERE pro_no = #{prono} AND item_code = #{itemcode} AND id =#{id}  ")
    Boolean DeleteCaiGouPlan(@Param("itemcode") String itemcode ,@Param("prono") String prono,@Param("id") String id);

    @Update("update atw_caigou_plan_data_lock set plan_date= #{planDate},req_date= #{reqDate},req_num= #{reqNum} where id= #{id}  ")
    Boolean updateReqNum(@Param("planDate") Date planDate, @Param("reqDate") Date reqDate, @Param("reqNum") String reqNum, @Param("id") String id);


    @Update("update  atw_caigou_plan_data_lock a set status =1 where DATE_FORMAT(plan_date,'%Y-%m')=#{monthString}  ")
    Boolean LockCaiGouPlan(@Param("monthString") String monthString);


    @Delete("update atw_caigou_plan_data_lock set is_deleted=1  ")
    Boolean deleteAllCaiGouPlan();

    @Delete("delete FROM atw_caigou_plan_data_lock where id=#{id}")
    Boolean deleteAllCaiGouPlanFromDisk(@Param("id") String id);


    @Insert("INSERT atw_caigou_plan_data_lock_history select * from atw_caigou_plan_data_lock")
    Boolean saveAllCaiGouPlanAsHistory();

    @Update("update atw_caigou_plan_data_lock_history set updatetime=NOW() where updatetime is null")
    Boolean updateCaiGouPlanHistoryDate();

    /*@Select("select * from BI_Supdeliv_Plan_Data_Detail_View where item_code like '15110%' and pro_no is not null and plan_date is not null ORDER BY plan_date  ")
    List<CaiGouSchedule> selectAllCaiGouPlan();*/

    @Select("SELECT\n" +
        "\tpro_no,\n" +
        "\titem_code,\n" +
        "\tagree_date,\n" +
        "\tplan_date,\n" +
        "\tpro_num,\n" +
        "\treq_date,\n" +
        "\tSUM( req_num ) req_num ,\n" +
        "\torgcode\n" +
        "FROM\n" +
        "\tBI_Supdeliv_Plan_Data_Detail_View \n" +
        "WHERE\n" +
        "\t(\n" +
        "\t\titem_code LIKE '15110%' \n" +
        "\t\tOR item_code LIKE '130301%' \n" +
        "\t\tOR item_code LIKE '130302%' \n" +
        "\t\tOR item_code LIKE '130101%' \n" +
        "\t\tOR item_code LIKE '130102%' \n" +
        "\t\tOR item_code LIKE '131111%' \n" +
        "\t\tOR item_code LIKE '131106%' \n" +
        "\t\tOR item_name LIKE '%锻%' \n" +
        "\t) \n" +
        "\tAND pro_no IS NOT NULL \n" +
        "\tAND plan_date IS NOT NULL \n" +
        "GROUP BY\n" +
        "\tpro_no,\n" +
        "\titem_code,\n" +
        "\tagree_date,\n" +
        "\tplan_date,\n" +
        "\treq_date,\n" +
        "\tpro_num,\n" +
        "\torgcode \n" +
        "ORDER BY\n" +
        "\tplan_date")
    List<CaiGouSchedule> selectAllCaiGouPlan();




    @Select(" SELECT * FROM atw_caigou_plan_data_lock a WHERE not  EXISTS (select * from BI_Supdeliv_Plan_Data_Detail b where a.pro_no=b.pro_no and a.item_code=b.item_code )  ")
    List<CaiGouSchedule> selectCaiGouPlandiff();

    @Select("SELECT * FROM atw_caigou_plan_data_lock a WHERE po_code_ln NOT IN (SELECT po_code_ln FROM BI_Supdeliv_Plan_Data_Detail) or  req_num<=0 ")
    List<CaiGouSchedule> selectCaiGouPlandiff2();

    @Select("SELECT * FROM (SELECT (SELECT IFNULL(SUM(acpdl.req_num), 0) FROM atw_caigou_plan_data_lock acpdl WHERE acpdl.po_code = a.po_code AND acpdl.po_ln = a.po_ln and acpdl.is_deleted=0) ysdsl, (SELECT api.tc_num - api.arv_goods_num FROM atw_po_item api WHERE api.po_code = a.po_code AND api.po_ln = a.po_ln) wshsl, a.* FROM BI_Supdeliv_Plan_Data_Detail_View a WHERE item_code = #{itemcode}  ) selectpo WHERE wshsl > ysdsl and orgcode=#{orgcode} GROUP BY po_code_ln  ORDER BY wwpo_date")
    List<CaiGouSchedule> selectAllCaiGouPlanPo(@Param("itemcode") String itemcode ,@Param("orgcode") String orgcode);

    @Select("SELECT * from BI_Supdeliv_Plan_Data_Detail_View where po_code=#{pr_code} and po_ln=#{pr_ln}")
    List<CaiGouSchedule> getDataFromShjh(@Param("pr_code") String pr_code,@Param("pr_ln") String pr_ln);

    @Select("SELECT * from atw_po_item where pr_code=#{pr_code} and pr_ln=#{pr_ln} and is_deleted=0")
    List<PoItemEntity> getPoItemFromPR(@Param("pr_code") String pr_code,@Param("pr_ln") String pr_ln);


    @Select("SELECT * FROM (SELECT (SELECT IFNULL(SUM(acpdl.req_num), 0) FROM atw_caigou_plan_data_lock acpdl WHERE acpdl.po_code = a.po_code AND acpdl.po_ln = a.po_ln and acpdl.is_deleted=0) ysdsl, IFNULL((\n" +
        "\t\tSELECT\n" +
        "\t\t\tapi.tc_num - api.arv_goods_num \n" +
        "\t\tFROM\n" +
        "\t\t\tatw_po_item api \n" +
        "\t\tWHERE\n" +
        "\t\t\tapi.po_code = a.po_code \n" +
        "\t\t\tAND api.po_ln = a.po_ln \n" +
        "\t\t) ,(select tc_num from atw_u9_pr where pr_code=a.po_code  and pr_ln=a.po_ln)) wshsl, a.* FROM BI_Supdeliv_Plan_Data_Detail_View a WHERE item_code = #{itemcode}  ) selectpo WHERE  po_code_ln=#{poln} and orgcode=#{orgcode} GROUP BY po_code_ln ORDER BY wwpo_date")
    List<CaiGouSchedule> selectAllCaiGouPlanPoWithPoln(@Param("itemcode") String itemcode ,@Param("poln") String poln,@Param("orgcode") String orgcode);


    @Select("select  * from atw_caigou_plan_data_lock where pro_no =#{prono} and item_code=#{itemcode}  ")
    List<CaiGouSchedule> selectLockData(@Param("itemcode") String itemcode ,@Param("prono") String prono);

    @Select("select * from BI_Supdeliv_Plan_Data_Detail_View where pro_no =#{prono} and item_code=#{itemcode} ")
    List<CaiGouSchedule> selectQtData(@Param("itemcode") String itemcode ,@Param("prono") String prono);

    @Select("select * from BI_Supdeliv_Plan_Data_Detail_View where po_code_ln=#{poln} and pro_no=#{prono} and req_num is not null limit 1")
    CaiGouSchedule selectQtDataReqNum(@Param("poln") String poln,@Param("prono") String prono);

    @Select("select * from (select (SELECT IFNULL(SUM(acpdl.req_num), 0) FROM atw_caigou_plan_data_lock acpdl WHERE acpdl.po_code = a.po_code AND acpdl.po_ln = a.po_ln and acpdl.is_deleted=0) ysdsl, b.tc_num,b.pr_date,a.* from BI_Supdeliv_Plan_Data_Detail_View a left join atw_u9_pr b on a.po_code=b.pr_code and a.po_ln=b.pr_ln where  a.item_code=#{itemcode}  and a.orgcode=#{orgcode} and b.pr_date is not null ORDER BY b.pr_date) p where tc_num>ysdsl LIMIT 1 ")
    CaiGouSchedule selectCaiGouPlanPR(@Param("itemcode") String itemcode,@Param("orgcode") String orgcode );

    boolean insertLockCaiGou(@Param("caiGouSchedule") CaiGouSchedule caiGouSchedule);

    boolean insertMatchLockCaiGou(@Param("caiGouSchedule") CaiGouSchedule caiGouSchedule);

    @Select("SELECT count(*) from BI_Supdeliv_Plan_Data_Detail_Lock where pro_no = #{proNo} and is_deleted = 0")
    Integer selectLockedProNoIsExisted(@Param("proNo") String proNo);

    @Select("SELECT * from BI_Supdeliv_Plan_Data_Detail_Lock where pro_no = #{proNo} and is_deleted = 0")
    List<CaiGouSchedule> selectLockedInfo(@Param("proNo") String proNo);

    @Select("SELECT * from BI_Supdeliv_Plan_Data_Detail_Lock where pro_no = #{proNo} and item_code = #{itemCode} and left(po_code_ln,2)='PO' and is_deleted = 0")
    List<CaiGouSchedule> selectLockedInfoByProAndItemCode(@Param("proNo") String proNo,@Param("itemCode") String itemCode);

    @Select("SELECT a.* FROM BI_Supdeliv_Plan_Data_Detail a left join atw_item  b on a.item_code = b.`code` and b.is_deleted = 0 WHERE pro_no = #{proNo} and b.main_code not in (SELECT main_code from BI_Supdeliv_Plan_Data_Detail_Lock_Item)")
    List<CaiGouSchedule> selectToLockInfo(@Param("proNo") String proNo);

    @Select("SELECT * from BI_Supdeliv_Plan_Data_Detail where pro_no = #{proNo}")
    List<CaiGouSchedule> selectNewBIInfo(@Param("proNo") String proNo);

    @Select("SELECT * from d where po_code = #{poCode} and po_ln = #{poLn} and is_deleted = 0")
    PoItemEntity selectPoItemInfo(@Param("poCode") String poCode,@Param("poLn") String poLn);

    @Delete("delete from BI_Supdeliv_Plan_Data_Detail_Lock where pro_no = #{proNo} and po_code = #{poCode} and po_ln = #{poLn}")
    boolean deleteLockedInfo(@Param("poCode") String poCode,@Param("poLn") String poLn,@Param("proNo") String proNo);

    @Update("update BI_Supdeliv_Plan_Data_Detail_Lock set pro_num = #{proNum} where pro_no = #{proNo}")
    boolean updateLockedInfoProNum(@Param("proNo") String proNo,@Param("proNum") Integer proNum);

    @Update("update BI_Supdeliv_Plan_Data_Detail_Lock set plan_date=#{planDate} where pro_no = #{proNo}")
    boolean updatePlanDateFromproNo(@Param("planDate") String planDate,@Param("proNo") String proNo);

    boolean updateLockedInfo(@Param("proNo") String proNo,@Param("planDate") String planDate,@Param("reqDate") String reqDate,@Param("lackItemNum") String lackItemNum,@Param("poCodeLn") String poCodeLn,@Param("proNum") Integer proNum);

    @Select("select count(*) from BI_Supdeliv_Plan_Data_Detail_Lock where pro_no = #{proNo} and item_code = #{itemCode}")
    Integer selectItemIsExistedOfPro(@Param("proNo") String proNo,@Param("itemCode") String itemCode);

    Integer selectIsNeedLock(@Param("itemCode") String itemCode);

    boolean updateLockCaiGou(@Param("caiGouSchedule") CaiGouSchedule caiGouSchedule);

    boolean updateWWBomInfoByHisInfo(@Param("caiGouSchedule") CaiGouSchedule caiGouScheduleDate);

    List<EchartVo> selectTotalNumOfItem(@Param("supCode") String supCode, @Param("year") String year, @Param("startMonth") String startMonth, @Param("endMonth") String endMonth);
    List<EchartVo> selectOrderNumOfItem(@Param("supCode") String supCode,@Param("year") String year,@Param("startMonth") String startMonth,@Param("endMonth") String endMonth);
    List<EchartVo> selectDeliveredNumOfItem(@Param("supCode") String supCode,@Param("year") String year,@Param("startMonth") String startMonth,@Param("endMonth") String endMonth);

    List<EchartVo> selectTotalPriceNeed(@Param("supCode") String supCode, @Param("year") String year, @Param("startMonth") String startMonth, @Param("endMonth") String endMonth);
    List<EchartVo> selectOrderPriceNeed(@Param("supCode") String supCode,@Param("year") String year,@Param("startMonth") String startMonth,@Param("endMonth") String endMonth);
    List<EchartVo> selectDeliveredPriceNeed(@Param("supCode") String supCode,@Param("year") String year,@Param("startMonth") String startMonth,@Param("endMonth") String endMonth);

    List<EchartVo> selectTotalNumNeed(@Param("supCode") String supCode, @Param("year") String year, @Param("startMonth") String startMonth, @Param("endMonth") String endMonth);
    List<EchartVo> selectOrderNumNeed(@Param("supCode") String supCode,@Param("year") String year,@Param("startMonth") String startMonth,@Param("endMonth") String endMonth);
    List<EchartVo> selectDeliveredNumNeed(@Param("supCode") String supCode,@Param("year") String year,@Param("startMonth") String startMonth,@Param("endMonth") String endMonth);

    List<String> selectAllTypeItemName(@Param("supCode") String supCode, @Param("year") String year, @Param("startMonth") String startMonth, @Param("endMonth") String endMonth);
    List<ItemInfoEntityOfZDJ> selectZJWeightOfSup(@Param("itemInfoEntity") ItemInfoEntityOfZDJ itemInfoEntity);

    List<EchartVo> selectTotalZJNeed(@Param("supCode") String supCode, @Param("year") String year, @Param("startMonth") String startMonth, @Param("endMonth") String endMonth);
    List<EchartVo> selectOrderZJNeed(@Param("supCode") String supCode,@Param("year") String year,@Param("startMonth") String startMonth,@Param("endMonth") String endMonth);
    List<EchartVo> selectDeliveredZJNeed(@Param("supCode") String supCode,@Param("year") String year,@Param("startMonth") String startMonth,@Param("endMonth") String endMonth);

    List<EchartVo> selectZJTotalNumNeed(@Param("supCode") String supCode, @Param("year") String year, @Param("startMonth") String startMonth, @Param("endMonth") String endMonth);
    List<EchartVo> selectZJDeliveredNumNeed(@Param("supCode") String supCode,@Param("year") String year,@Param("startMonth") String startMonth,@Param("endMonth") String endMonth);

    @SqlParser(filter = true)
    List<OtdReport> getOkMap(@Param("startDate") String startDate, @Param("endDate") String endDate, @Param("type") String type);

    @SqlParser(filter = true)
    List<OtdReport> getDelayMap(@Param("startDate") String startDate, @Param("endDate") String endDate, @Param("type") String type);

    @SqlParser(filter = true)
    List<OtdReport> getOkMapSeven(@Param("startDate") String startDate, @Param("endDate") String endDate, @Param("type") String type);

    @SqlParser(filter = true)
    List<OtdReport> getDelayMapSeven(@Param("startDate") String startDate, @Param("endDate") String endDate, @Param("type") String type);

    @SqlParser(filter = true)
    List<OtdReport> getTotalMap(@Param("startDate") String startDate,@Param("endDate")  String endDate, @Param("type") String type);

    @SqlParser(filter = true)
    List<OtdReport> getTotalMapSeven(@Param("startDate") String startDate,@Param("endDate")  String endDate, @Param("type") String type);

    @SqlParser(filter = true)
    List<OtdReport> getOkMapOfReqDate(@Param("startDate") String startDate, @Param("endDate") String endDate, @Param("type") String type);

    @SqlParser(filter = true)
    List<OtdReport> getTotalMapOfReqDate(@Param("startDate") String startDate,@Param("endDate")  String endDate, @Param("type") String type);

    @SqlParser(filter = true)
    List<OtdReport> getOkMapWeek(@Param("startDate") String startDate, @Param("endDate") String endDate, @Param("type") String type,@Param("year") String year);

    @SqlParser(filter = true)
    List<OtdReport> getTotalMapWeek(@Param("startDate") String startDate,@Param("endDate")  String endDate, @Param("type") String type,@Param("year") String year);

    @SqlParser(filter = true)
    List<OtdReport> getTotalMapWeekWithOutCheckUpdateTime(@Param("startDate") String startDate,@Param("endDate")  String endDate, @Param("type") String type,@Param("year") String year);

    @SqlParser(filter = true)
    List<OtdReport> getOkMapWeekOfReqDate(@Param("startDate") String startDate, @Param("endDate") String endDate, @Param("type") String type,@Param("year") String year);

    @SqlParser(filter = true)
    List<OtdReport> getOkMapWeekOfReqDateSeven(@Param("startDate") String startDate, @Param("endDate") String endDate, @Param("type") String type,@Param("year") String year);

    @SqlParser(filter = true)
    List<OtdReport> getTotalMapWeekOfReqDate(@Param("startDate") String startDate,@Param("endDate")  String endDate, @Param("type") String type,@Param("year") String year);

    @SqlParser(filter = true)
    List<OtdReport> getTotalMapWeekOfReqDateSeven(@Param("startDate") String startDate,@Param("endDate")  String endDate, @Param("type") String type,@Param("year") String year);

    @SqlParser(filter = true)
    List<OtdReport> getOkThreeMotnthOtdForSup(@Param("startDate") String startDate, @Param("endDate") String endDate, @Param("supCode") String supCode);

    @SqlParser(filter = true)
    List<OtdReport> getTotalThreeMotnthOtdForSup(@Param("startDate") String startDate,@Param("endDate")  String endDate, @Param("supCode") String supCode);

    @SqlParser(filter = true)
    List<AllOtdReport> getMonthForestInfoOfReqDate(@Param("month") String month, @Param("type") String type, @Param("year") String year);

    @SqlParser(filter = true)
    List<AllOtdReport> getMonthForestInfoOfPlanDate(@Param("month") String month, @Param("type") String type, @Param("year") String year);

    @SqlParser(filter = true)
    List<AllOtdReport> getWeekForestInfoOfReqDate(@Param("week") String week, @Param("type") String type, @Param("year") String year);

    @Update("Update BI_Supdeliv_Plan_Data_Detail_Write set limits = '0' where 1=1")
    boolean resetUpdateCheckTimeLimits();

    @Update("update atw_supplier set arv_rate = 0 where is_deleted = 0")
    boolean resetArrRate();

    @Select("Select ifnull(sum(rcv_num),0) from atw_po_sn where po_code = #{poCode} and po_ln = #{poLn} and create_time <= #{time} and is_deleted = 0")
    Integer getSnNumByPoCodeLnAndTime(@Param("poCode") String poCode,@Param("poLn") String poLn,@Param("time") String time);

    @Update("update atw_supplier set arv_rate = #{arvRate} where code = #{supCode} and is_deleted = 0")
    boolean updateArrRate(@Param("supCode") String supCode, @Param("arvRate")BigDecimal arvRate);

}

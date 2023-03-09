package org.springblade.modules.pr.mapper;

import com.baomidou.dynamic.datasource.annotation.DS;
import com.baomidou.mybatisplus.annotation.SqlParser;
import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import lombok.Data;
import org.apache.ibatis.annotations.*;
import org.springblade.modules.po.entity.IoEntity;
import org.springblade.modules.pr.dto.*;
import org.springblade.modules.pr.entity.*;
import org.springblade.modules.pr.vo.ItemInfoOfQZVO;
import org.springblade.modules.pr.vo.ItemInfoVO;
import org.springblade.modules.pr.vo.MaterialMaliyVO;
import org.springblade.modules.priceframe.dto.CenterPriceFrame;

import java.math.BigDecimal;
import java.util.Date;
import java.util.List;

/**
 * 请购单 Mapper 接口
 *
 * @author Will
 */
public interface U9PrMapper extends BaseMapper<U9PrEntity> {

    IPage<U9PrDTO> selectPageByReq(IPage<U9PrDTO> page,@Param("prReq") PrReq prReq);

    List<U9PrDTO> selectListByReq(@Param("prReq") PrReq prReq);

    List<U9PrDTO> selectListByReqOfOthers(@Param("prReq") PrReq prReq);

    IPage<U9PrEntity> getU9Page(IPage<CenterPriceFrame> page);

    int getPriceFrameCount();

    IPage<U9PrDTO> selectWaitPageByReq(IPage<U9PrDTO> page,@Param("prReq") PrReq prReq);

    IPage<U9PrDTO>  selectAllPrPage(IPage<U9PrDTO> page,@Param("prReq") PrReq prReq);

    List<U9PrDTO>  selectWaitListByReq(@Param("prReq") PrReq prReq);

    IPage<U9PrDTO> selectInquiryPage(IPage<U9PrDTO> page,@Param("prReq") PrReq prReq);

    List<U9PrDTO> selectInquiryList(@Param("prReq") PrReq prReq);

    IPage<U9PrDTO> selectInquiryCheckPage(IPage<U9PrDTO> page,@Param("prReq") PrReq prReq);

    List<U9PrDTO> selectInquiryCheckList(@Param("prReq") PrReq prReq);


    IPage<U9PrDTO> selectFlowPage(IPage<U9PrDTO> page,@Param("prReq") PrReq prReq);

    IPage<U9PrDTO> selectFlowPageOfOthers(IPage<U9PrDTO> page,@Param("prReq") PrReq prReq);

    List<U9PrDTO> selectFlowPageOfOthersForExcel(@Param("prReq") PrReq prReq);

    List<U9PrDTO> selectFlowPageList(@Param("prReq") PrReq prReq);

    List<U9PrDTO> selectFlowListToExport(@Param("prReq") PrReq prReq);

    List<U9PrDTO> selectFlowPageListForExcelOfOthers(@Param("prReq") PrReq prReq);

    IPage<U9PrDTO> selectFlowCheckPage(IPage<U9PrDTO> page,@Param("prReq") PrReq prReq);

    IPage<U9PrDTO> selectPageWithoutIo(IPage<U9PrDTO> page,@Param("prReq") PrReq prReq);

    List<U9PrDTO> selectFlowCheckList(@Param("prReq") PrReq prReq);

    @Select("select count(1) from atw_po where is_reserve='Y'")
    int getPoReserveCount();

    List<U9PrDTO> selectFlowCheckListOfOthers(@Param("prReq") PrReq prReq);

    String selectSupRemarks(@Param("qoNo") String qoNo, @Param("itemDesc") String itemDesc);

    List<ItemInfoEntityOfZDJ> selectSupAndWeightOfZDJ(@Param("itemInfoEntity") ItemInfoEntityOfZDJ itemInfoEntity);

    List<ItemInfoEntityOfWW> selectSupAndWeightOfWW(@Param("itemInfoEntity") ItemInfoEntityOfZDJ itemInfoEntity);

    List<ItemInfoEntityOfWW> selectSupAndWeightOfQCWW(@Param("itemInfoEntity") ItemInfoEntityOfZDJ itemInfoEntity);

    List<ItemInfoEntityOfQZ> selectSupAndWeightOfQZ(@Param("itemInfoEntity") ItemInfoEntityOfQZ itemInfoEntity);

    List<ItemInfoEntityOfQZNew> selectBasicInfoOfQZ(@Param("itemInfoEntity") ItemInfoEntityOfQZNew itemInfoEntity);

    ItemInfoOfQZVO selectMaterialPriceOfQZ(@Param("itemInfoEntity") ItemInfoEntityOfQZNew itemInfoEntity);
    @SqlParser(filter = true)
    ItemInfoOfQZVO selectSprayOfQZ(@Param("itemInfoEntity") ItemInfoEntityOfQZNew itemInfoEntity);

    @SqlParser(filter = true)
    ItemInfoOfQZVO selectSpraySmallOfQZ(@Param("itemInfoEntity") ItemInfoEntityOfQZNew itemInfoEntity);


    String selectItemPriceOfZDJ(@Param("itemInfoEntity") ItemInfoEntityOfZDJ itemInfoEntity);

    String selectItemPriceOfQZ(@Param("itemInfoEntity") ItemInfoEntityOfQZ itemInfoEntity);

    String selectPtPriceOfQZ(@Param("itemInfoEntity") ItemInfoEntityOfQZ itemInfoEntity,@Param("range") String range);

    boolean updateU9Pr(@Param("id")Long id);

    boolean deleteIo(@Param("id")Long id);

    boolean removeIo(@Param("pr_id")Long pr_id);

    boolean updatePrToOthers(@Param("id")Long id);

    String selectAttachment(@Param("id")Long id);

    List<IoEntity>  selectPriceFromIo(@Param("pr_id")Long pr_id);

    String getPrMoNO(@Param("pr_code") String prCode, @Param("pr_ln") String prLn);

    U9PrEntity getPrLn(@Param("pr_code") String prCode, @Param("pr_ln") int prLn);

    boolean winTheBid(@Param("id")String id);

    int countTheBid(@Param("id")String id);

    boolean cancelTheBid(@Param("id")String id);

    boolean addOtherInfos(@Param("supItemOthers") SupItemOthers supItemOthers);

    boolean updateOtherInfos(@Param("supItemOthers") SupItemOthers supItemOthers);

    boolean removeOtherInfos(@Param("supItemOthers") SupItemOthers supItemOthers);

    IPage<SupItemOthers> getOthersInfo(IPage<SupItemOthers> page, @Param("mainCode") String mainCode, @Param("matQuality") String matQuality);

    List<SupItemOthers> getOthersInfoList(@Param("mainCode") String mainCode, @Param("matQuality") String matQuality);

    boolean updateStatusOfOthers(@Param("supItemOthers") SupItemOthers supItemOthers);

    List<IoEntity> getIoListByPrId(@Param("prId") Long prId);

    U9PrEntity getPrById(@Param("id") Long id);

    BigDecimal getMinPriceByPrId(@Param("prId") Long prId);

    Long getEarliestDateByPrId(@Param("prId") Long prId);

    BigDecimal getMaterialCostByItemCode(@Param("itemCode") String itemCode);

    BigDecimal getLaborCostByItemCode(@Param("itemCode") String itemCode);

    boolean updateEvaluateScore(@Param("score") BigDecimal score,@Param("id") Long id);

    boolean setWinTheBid(@Param("id") Long id, @Param("time") Long time);

    boolean setLoseTheBid(@Param("ioEntity") IoEntity ioEntity);

    boolean setPrFlow(@Param("id") Long id);

    boolean setPrToChoose(@Param("id") Long id);

    boolean setPrToWait(@Param("id") Long id);

    @Select("Select * from atw_item_info_dj_basic where item_code = #{itemCode}")
    ItemInfoEntityBasOfDJ selectBasicItemInfoOfDJ(@Param("itemCode") String itemCode);

    @Select("Select * from atw_item_info_part_not_split where item_code = #{itemCode}")
    ItemInfoEntityBasOfXLJ selectBasicItemInfoOfXLJ(@Param("itemCode") String itemCode);

    @Select("Select * from atw_item_info_part_bl_rx where item_code = #{itemCode}")
    ItemInfoEntityBasOfXLJRX selectBasicItemInfoOfXLJRX(@Param("itemCode") String itemCode);

    @Select("Select * from atw_item_info_dz_not_split where item_code = #{itemCode}")
    ItemInfoEntityBasOfDZ selectBasicItemInfoOfDZ(@Param("itemCode") String itemCode);


    @Insert("insert atw_item_info_dz_not_split (item_code,item_name,outer_size,inner_size,p_outer_size,p_inner_size) " +
        "VALUE (#{itemInfo.itemCode},#{itemInfo.itemName},#{itemInfo.outerSize},#{itemInfo.innerSize},#{itemInfo.pOuterSize},#{itemInfo.pInnerSize})")
    boolean insertBasicItemInfoOfDZ(@Param("itemInfo") ItemInfoEntityBasOfDZ itemInfoEntityBasOfDZ);

    @Update("update atw_item_info_dz_not_split set outer_size=#{itemInfo.outerSize},inner_size=#{itemInfo.innerSize},p_outer_size=#{itemInfo.pOuterSize},p_inner_size=#{itemInfo.pInnerSize}" +
        " where item_code=#{itemInfo.itemCode}")
    boolean updateBasicItemInfoOfDZ(@Param("itemInfo") ItemInfoEntityBasOfDZ itemInfoEntityBasOfDZ);

    @Delete("delete from atw_item_info_dj_basic where item_code = #{itemCode}")
    boolean deleteOldBasicDJInfo(@Param("itemCode") String itemCode);

    @Delete("delete from atw_item_info_part_not_split where item_code = #{itemCode}")
    boolean deleteOldBasicXLJInfo(@Param("itemCode") String itemCode);

    @Select("Select * from atw_item_info_dj_res_common where #{outerSize} >= substring_index(outer_range,'-', 1 ) and #{outerSize} <= substring_index(outer_range,'-', -1 ) and find_in_set(#{material},material) and #{height} >= substring_index(height_range,'-', 1 ) and #{height} <= substring_index(height_range,'-', -1 )")
    List<ItemInfoEntityResCommonOfDJ> selectResItemInfoOfNew(@Param("outerSize") Double outerSize, @Param("material") String material, @Param("height") Double height);

    @Select("Select * from atw_item_info_dj_res_fm where standards=#{standards} and find_in_set(#{material},material)")
    List<ItemInfoEntityResFMOfDJ> selectResItemInfoOfFm(@Param("standards") String standards, @Param("material") String material);

    @Select("Select * from atw_item_info_dj_res_zfl where #{outerSize} >= substring_index(outer_range,'-', 1 ) and #{outerSize} <= substring_index(outer_range,'-', -1 ) and find_in_set(#{material},material)")
    List<ItemInfoEntityResZFLOfDJ> selectResItemInfoOfZfl(@Param("outerSize") Double outerSize, @Param("material") String material);

    @Select("Select * from atw_item_info_dj_res_zyd where #{outerSize} >= substring_index(outer_range,'-', 1 ) and #{outerSize} <= substring_index(outer_range,'-', -1 ) and find_in_set(#{material},material)")
    List<ItemInfoEntityResZYDOfDJ> selectResItemInfoOfZyd(@Param("outerSize") Double outerSize, @Param("material") String material);

    @Insert("insert atw_item_info_dj_basic(item_code,item_name,big_outer_size,inner_size,big_height_size,small_outer_size,total_height_size,outer_size,height_size,type,res) " +
        "value(#{itemInfo.itemCode},#{itemInfo.itemName},#{itemInfo.bigOuterSize},#{itemInfo.innerSize},#{itemInfo.bigHeightSize},#{itemInfo.smallOuterSize},#{itemInfo.totalHeightSize},#{itemInfo.outerSize},#{itemInfo.heightSize},#{itemInfo.type},#{itemInfo.res})")
    boolean insertBasicItemInfoOfDJ(@Param("itemInfo") ItemInfoEntityBasOfDJ itemInfoEntityBasZYDOfDJ);

    @Insert("insert atw_item_info_part_not_split(item_code,item_name,outer_size,inner_size,height_size,xqg_size,cbk_size,cbzxj_size,xqgk_size) " +
        "value(#{itemInfo.itemCode},#{itemInfo.itemName},#{itemInfo.outerSize},#{itemInfo.innerSize},#{itemInfo.heightSize},#{itemInfo.xqgSize},#{itemInfo.cbkSize},#{itemInfo.cbzxjSize},#{itemInfo.xqgkSize})")
    boolean insertBasicItemInfoOfXLJ(@Param("itemInfo") ItemInfoEntityBasOfXLJ itemInfoEntityBasOfXLJ);

    @Insert("insert atw_item_info_part_bl_rx(item_code,r,x) " +
        "value(#{itemInfo.itemCode},#{itemInfo.r},#{itemInfo.x})")
    boolean insertBasicItemInfoOfXLJRX(@Param("itemInfo") ItemInfoEntityBasOfXLJRX itemInfoEntityBasOfXLJ);

    @Update("update atw_item_info_dj_basic set big_outer_size = #{itemInfo.bigOuterSize},inner_size=#{itemInfo.innerSize},big_height_size=#{itemInfo.bigHeightSize},small_outer_size=#{itemInfo.smallOuterSize},total_height_size=#{itemInfo.totalHeightSize},outer_size = #{itemInfo.outerSize},height_size = #{itemInfo.heightSize},res=#{itemInfo.res},type=#{itemInfo.type} where item_code = #{itemInfo.itemCode}")
    boolean updateBasicItemInfoOfDJ(@Param("itemInfo") ItemInfoEntityBasOfDJ itemInfoEntityBasZYDOfDJ);

    @Update("update atw_item_info_part_not_split set outer_size = #{itemInfo.outerSize},inner_size=#{itemInfo.innerSize},height_size=#{itemInfo.heightSize},xqg_size=#{itemInfo.xqgSize},cbk_size=#{itemInfo.cbkSize},cbzxj_size=#{itemInfo.cbzxjSize},xqgk_size=#{itemInfo.xqgkSize} where item_code = #{itemInfo.itemCode}")
    boolean updateBasicItemInfoOfXLJ(@Param("itemInfo") ItemInfoEntityBasOfXLJ itemInfoEntityBasOfXLJ);

    @Update("update atw_item_info_part_bl_rx set r = #{itemInfo.r},x=#{itemInfo.x} where item_code = #{itemInfo.itemCode}")
    boolean updateBasicItemInfoOfXLJRX(@Param("itemInfo") ItemInfoEntityBasOfXLJRX itemInfoEntityBasOfXLJ);

    @Select("Select price from atw_item_info_dj_price where sup_code = #{supCode} and material = #{material} ")
    String selectSinglePrice(@Param("supCode") String supCode, @Param("material") String material);

    @Select("Select item_code,item_name from atw_item_info_dj_basic where is_deleted = 0 ")
    List<ItemInfoVO> selectAllDjItemCodes();

    @Select("Select count(*) from atw_item_info_dj_report where item_code = #{itemCode} and is_deleted = 0 ")
    Integer itemCodeOfDJIsExisted(@Param("itemCode") String itemCode);

    @Select("Select count(*) from atw_item_info_part_report where item_code = #{itemCode}")
    Integer itemCodeOfXLJIsExisted(@Param("itemCode") String itemCode);

    @Delete("Update atw_item_info_dj_report set is_deleted = 1 where item_code = #{itemCode} and is_deleted = 0 ")
    boolean deleteHistoryDjInfo(@Param("itemCode") String itemCode);

    @Insert("insert atw_item_info_dj_report(item_code,item_name,sup_code,sup_name,weight,material_price,price,big_outer_size,big_height_size,small_outer_size,total_height_size,inner_size,outer_size,height_size,type,res,height_remain,outer_remain,inner_remain,create_user,create_time,new_inner_remain) " +
        "value(#{itemCode},#{itemName},#{supCode},#{supName},#{weight},#{materialPrice},#{price},#{bigOuterSize},#{bigHeightSize},#{smallOuterSize},#{totalHeightSize},#{innerSize},#{outerSize},#{heightSize},#{formulaType},#{res},#{heightRemain},#{outerRemain},#{innerRemain},#{createUser},#{time},#{newInnerRemain})")
    boolean insertDjInfoReport(@Param("itemCode") String itemCode,@Param("itemName") String itemName,@Param("supCode") String supCode,@Param("supName") String supName,@Param("weight") String weight,@Param("materialPrice") String materialPrice,@Param("price") String price,
                               @Param("bigOuterSize") String bigOuterSize,@Param("bigHeightSize") String bigHeightSize,@Param("smallOuterSize") String smallOuterSize,@Param("totalHeightSize") String totalHeightSize,
                               @Param("innerSize") String innerSize,@Param("outerSize") String outerSize,@Param("heightSize") String heightSize,@Param("formulaType") String formulaType,@Param("res") String res,
                               @Param("heightRemain") String heightRemain,@Param("outerRemain") String outerRemain,@Param("innerRemain") String innerRemain,@Param("createUser") String createUser,@Param("time") String time,@Param("newInnerRemain") String newInnerRemain);

    @Delete("update atw_item_info_dj_report set is_deleted = 1,remove_user= #{removeUser},remove_time=#{time} where item_code = #{itemCode} and is_deleted = 0")
    boolean deleteDjInfoReport(@Param("itemCode") String itemCode,@Param("removeUser") String removeUser,@Param("time") String time);

    @Delete("delete from atw_item_info_part_report where item_code = #{itemCode}")
    boolean deleteXLJInfoReport(@Param("itemCode") String itemCode);

    @Delete("delete from atw_item_info_dj_basic where item_code = #{itemCode} ")
    boolean deleteDjInfoBasic(@Param("itemCode") String itemCode);

    @Delete("delete from atw_item_info_part_not_split where item_code = #{itemCode} ")
    boolean deleteXLJInfoBasic(@Param("itemCode") String itemCode);

    @Select("Select name from atw_item where code = #{itemCode} and is_deleted = 0 ")
    String getItemInfoByItemCode(@Param("itemCode") String itemCode);

    @Select("Select code from atw_item where name = #{itemName} and is_deleted = 0 ")
    String getItemInfoByItemName(@Param("itemName") String itemName);



    IPage<ItemInfoEntityDJReport> selectDjReportPage(IPage<ItemInfoEntityDJReport> page,@Param("req") SubmitPriceReq req);

    IPage<ItemInfoEntityOfXLJ> selectXLJReportPage(IPage<ItemInfoEntityOfXLJ> page,@Param("req") SubmitPriceReq req);

    IPage<ItemInfoEntityOfLZQ> selectLZQReportPage(IPage<ItemInfoEntityOfLZQ> page,@Param("req") SubmitPriceReq req);

    IPage<ItemInfoEntityOfQZNew> selectQZReportPage(IPage<ItemInfoEntityOfQZNew> page,@Param("req") SubmitPriceReq req);

    List<ItemInfoEntityOfQZNew> selectQZReportList(@Param("req") SubmitPriceReq req);

    IPage<ItemInfoEntityOfDZ> selectDZReportPage(IPage<ItemInfoEntityOfDZ> page,@Param("req") SubmitPriceReq req);


    IPage<ItemInfoEntityOfFL> selectFLReportPage(IPage<ItemInfoEntityOfFL> page,@Param("req") SubmitPriceReq req);

    List<ItemInfoEntityOfXLJ> selectXLJReportList(@Param("req") SubmitPriceReq req);

    List<ItemInfoEntityOfLZQ> selectLZQReportList(@Param("req") SubmitPriceReq req);

    List<ItemInfoEntityOfDZ> selectDZReportList(@Param("req") SubmitPriceReq req);

    List<ItemInfoEntityOfFL> selectFLReportList(@Param("req") SubmitPriceReq req);



    List<ItemInfoEntityDJReport> selectDjReportList(@Param("req") SubmitPriceReq req);

    @Select("select pr.* from atw_po_item pi LEFT JOIN atw_u9_pr pr ON ( pi.pr_id = pr.id AND pi.is_deleted = 0 ) WHERE pi.is_deleted = 0 AND pr.create_time >= #{time} ")
    List<U9PrDTO> getOrderInfo(@Param("time") String recordTime);

    @Select("select count(*) from atw_auto_order_item where item_code = #{itemCode} ")
    Integer autoItemIsExisted(@Param("itemCode") String itemCode);

    @Insert("Insert atw_auto_order_item(item_code,item_name) value(#{itemCode},#{itemName})")
    void insertAutoItem(@Param("itemCode") String itemCode,@Param("itemName") String itemName);

    @Insert("insert atw_auto_order_dj(pr_code,pr_ln,item_code,item_name,sup_code,sup_name,weight,material_price,price,big_outer_size,big_height_size,small_outer_size,total_height_size,inner_size,outer_size,height_size,type,res,height_remain,outer_remain,inner_remain,swnning_bid,new_inner_remain) " +
        "value(#{prCode},#{prLn},#{itemCode},#{itemName},#{supCode},#{supName},#{weight},#{materialPrice},#{price},#{bigOuterSize},#{bigHeightSize},#{smallOuterSize},#{totalHeightSize},#{innerSize},#{outerSize},#{heightSize},#{formulaType},#{res},#{heightRemain},#{outerRemain},#{innerRemain},#{swnningBid},#{newInnerRemain})")
    boolean insertDjAutoOrder(@Param("prCode") String prCode,@Param("prLn") String prLn,@Param("itemCode") String itemCode,@Param("itemName") String itemName,@Param("supCode") String supCode,@Param("supName") String supName,@Param("weight") String weight,@Param("materialPrice") String materialPrice,@Param("price") String price,
                               @Param("bigOuterSize") String bigOuterSize,@Param("bigHeightSize") String bigHeightSize,@Param("smallOuterSize") String smallOuterSize,@Param("totalHeightSize") String totalHeightSize,
                               @Param("innerSize") String innerSize,@Param("outerSize") String outerSize,@Param("heightSize") String heightSize,@Param("formulaType") String formulaType,@Param("res") String res,
                               @Param("heightRemain") String heightRemain,@Param("outerRemain") String outerRemain,@Param("innerRemain") String innerRemain,@Param("swnningBid") String swnningBid,@Param("newInnerRemain") String newInnerRemain);

    @Delete("update atw_auto_order_dj set is_deleted = 1 where pr_code = #{prCode} and pr_ln = #{prLn} and sup_code = #{supCode} and is_deleted = 0")
    boolean deleteDjAutoOrder(@Param("prCode") String prCode,@Param("prLn") String prLn,@Param("supCode") String supCode);

    @Select("Select count(*) from atw_auto_order_dj where pr_code = #{prCode} and pr_ln = #{prLn} and sup_code = #{supCode} and is_deleted = 0")
    Integer isExistedDjAutoOrder(@Param("prCode") String prCode,@Param("prLn") String prLn,@Param("supCode") String supCode);

    List<U9PrDTO> selectFzList(@Param("itemCode") String itemCode,@Param("statuss") String statuss);

    List<U9PrDTO> selectAllFzList(@Param("statuss") String statuss);

    IPage<AutoOrderOfDJ> getAutoOrderOfDJ(IPage<AutoOrderOfDJ> page,@Param("req") SubmitPriceReq req);

    List<AutoOrderOfDJ> selectAutoOrderOfDJList(@Param("req") SubmitPriceReq req);

    IPage<AutoOrderOfXLJ> getAutoOrderOfXLJ(IPage<AutoOrderOfXLJ> page,@Param("req") AutoOrderOfXLJ autoOrderOfXLJ);

    List<AutoOrderOfXLJ> selectAutoOrderOfXLJList(@Param("req") AutoOrderOfXLJ autoOrderOfXLJ);

    List<ItemInfoEntityDJReport> selectNoSwnningBid(@Param("req") SubmitPriceReq req);

    void deleteByItemCode(String itemCode);

    @Select("Select * from atw_item_info_part_not_split where item_code = #{itemCode}")
    ItemInfoEntityOfXLJ selectNotSplitXLJItemInfo(@Param("itemCode") String itemCode);

    @Select("Select * from atw_item_info_part_not_split where item_code = #{itemCode}")
    ItemInfoEntityOfLZQ selectNotSplitLZQItemInfo(@Param("itemCode") String itemCode);



    @SqlParser(filter = true)
    List<ItemInfoEntityOfXLJ> selectXLJResItemInfo(@Param("req") ItemInfoEntityOfXLJ req);

    @SqlParser(filter = true)
    List<ItemInfoEntityOfLZQ> selectLZQResItemInfo(@Param("req") ItemInfoEntityOfLZQ req);


    @Select("select * from atw_item_info_part_res GROUP BY sup_code")
    List<ItemInfoEntityOfXLJ> selectXLJResItemInfoOfDJ(@Param("req") ItemInfoEntityOfXLJ req);

    @SqlParser(filter = true)
    String getMaterialTypeByItemWithOutMaterial(@Param("req") ItemInfoEntityOfXLJ req);

    @SqlParser(filter = true)
    String getMaterialTypeByItemWithMaterial(@Param("req") ItemInfoEntityOfXLJ req);

    @Select("Select outer_size from atw_item_info_part_gl_size where sup_code = #{supCode} and (outer_size - #{outerSize}) >= 0 and (outer_size - #{outerSize}) <= 10 group by outer_size order by outer_size - #{outerSize} ")
    List<String> selectNewOuterSize(@Param("supCode") String supCode,@Param("outerSize") String outerSize);

    @Select("Select inner_size from atw_item_info_part_gl_size where sup_code = #{supCode} and outer_size = #{outerSize} and (#{innerSize} - inner_size) >= 0 and (#{innerSize} - inner_size) <= 10 order by #{innerSize} - inner_size LIMIT 1 ")
    String selectNewInnerSize(@Param("supCode") String supCode,@Param("outerSize") String outerSize,@Param("innerSize") String innerSize);

    @Select("SELECT thickness FROM atw_item_info_part_bl_thickness WHERE thickness - #{thickness} >= 0  order by thickness LIMIT 1")
    String selectThickness(@Param("thickness") String thickness);

    @SqlParser(filter = true)
    ItemInfoEntityOfXLJ selectItemPriceOfXLJ(@Param("req") ItemInfoEntityOfXLJ itemInfoEntity);

    @SqlParser(filter = true)
    ItemInfoEntityOfLZQ selectItemPriceOfLZQ(@Param("req") ItemInfoEntityOfLZQ itemInfoEntity);

    @Select("Select single_cost from atw_item_info_lzq_xqg_price where sup_code = #{req.supCode}")
    String selectItemXQGPriceOfLZQ(@Param("req") ItemInfoEntityOfLZQ itemInfoEntity);

    @SqlParser(filter = true)
    ItemInfoEntityOfLZQ selectItemHJFPriceOfLZQ(@Param("req") ItemInfoEntityOfLZQ itemInfoEntity);

    @SqlParser(filter = true)
    List<ItemInfoEntityOfXLJ> selectBLItemPriceOfXLJ(@Param("req") ItemInfoEntityOfXLJ itemInfoEntity);

    @Select("Select density from atw_item_info_part_density where sup_code = #{req.supCode} and  material = #{req.material}")
    String getDensity(@Param("req") ItemInfoEntityOfXLJ itemInfoEntity);


    @Select("Select work_price from atw_item_info_part_work_price where sup_code = #{req.supCode} and  equip_type = 'CNC'")
    String getCNCWorkPrice(@Param("req") ItemInfoEntityOfXLJ itemInfoEntity);

    @Select("Select work_price from atw_item_info_part_work_price where sup_code = #{req.supCode} and  equip_type = '车床'")
    String getJCWorkPrice(@Param("req") ItemInfoEntityOfXLJ itemInfoEntity);

    @Select("Select work_price,k from atw_item_info_lzq_work_price where sup_code = #{req.supCode} and  equip_type = 'CNC'")
    ItemInfoEntityOfLZQ getLZQCNCWorkPrice(@Param("req") ItemInfoEntityOfLZQ itemInfoEntity);

    @Select("Select work_price,k from atw_item_info_lzq_work_price where sup_code = #{req.supCode} and  equip_type = '车床'")
    ItemInfoEntityOfLZQ getLZQJCWorkPrice(@Param("req") ItemInfoEntityOfLZQ itemInfoEntity);



    @Insert("insert atw_item_info_part_report(item_code,item_name,sup_code,sup_name,outer_size,outer_res,inner_size,inner_res,height_size,height_res,outer_size_old,inner_size_old,height_size_old,weight,single_cost,material_cost,jc_minutes,jc_price,cnc_minutes,cnc_price,process_cost,price,k,r,x,pre_minutes) " +
        "value(#{req.itemCode},#{req.itemName},#{req.supCode},#{req.supName},#{req.outerSize},#{req.outerRes},#{req.innerSize},#{req.innerRes},#{req.heightSize},#{req.heightRes},#{req.outerSizeOld},#{req.innerSizeOld},#{req.heightSizeOld},#{req.weight},#{req.singleCost},#{req.materialCost},#{req.jcMinutes},#{req.jcPrice},#{req.cncMinutes},#{req.cncPrice},#{req.processCost},#{req.price},#{req.k},#{req.r},#{req.x},#{req.preMinutes})")
    boolean insertXLJInfoReport(@Param("req") ItemInfoEntityOfXLJ itemInfoEntityOfXLJ);

    @Insert("insert atw_item_info_lzq_report(item_code,item_name,sup_code,sup_name,outer_size,outer_res,inner_size,inner_res,height_size,height_res,outer_size_old,inner_size_old,height_size_old,weight,single_cost,material_cost,jc_minutes,jc_price,cnc_minutes,cnc_price,process_cost,price,k,r,x,pre_minutes," +
        "pr_code,pr_ln,quantity,xqg_size,cbk_size,cbzxj_size,xqgk_size,xqg_cost,hjf_cost,total_cost) " +
        "value(#{req.itemCode},#{req.itemName},#{req.supCode},#{req.supName},#{req.outerSize},#{req.outerRes},#{req.innerSize},#{req.innerRes},#{req.heightSize},#{req.heightRes},#{req.outerSizeOld},#{req.innerSizeOld},#{req.heightSizeOld},#{req.weight},#{req.singleCost},#{req.materialCost},#{req.jcMinutes},#{req.jcPrice},#{req.cncMinutes},#{req.cncPrice},#{req.processCost},#{req.price},#{req.k},#{req.r},#{req.x},#{req.preMinutes}" +
        ",#{req.prCode},#{req.prLn},#{req.quantity},#{req.xqgSize},#{req.cbkSize},#{req.cbzxjSize},#{req.xqgkSize},#{req.xqgCost},#{req.hjfCost},#{req.totalCost})")
    boolean insertLZQInfoReport(@Param("req") ItemInfoEntityOfLZQ itemInfoEntityOfLZQ);
    @Insert("INSERT atw_qz_report (pr_code,pr_ln,sup_name,sup_code,item_name,item_code,quantity,ball_diam,ball_height,qz_weight,qt_material_price,qz_material_cost,qz_spray_price," +
        "qz_spray_area,qz_spray_cost,qz_charge,qz_cost,fz_weight,fz_spray_charge,fz_charge,deliver_cost,fz_price,k,total_cost,fz_material_cost) VALUES " +
        "(#{req.prCode},#{req.prLn},#{req.supName},#{req.supCode},#{req.itemName},#{req.itemCode},#{req.quantity},#{req.ballDiam},#{req.ballHeight},#{req.qzWeight}" +
        ",#{req.qtMaterialPrice},#{req.qzMaterialCost},#{req.qzSprayPrice},#{req.qzSprayArea},#{req.qzSprayCost},#{req.qzCharge},#{req.qzCost},#{req.fzWeight}," +
        "#{req.fzSprayCharge},#{req.fzCharge},#{req.deliverCost},#{req.fzPrice},#{req.k},#{req.totalCost},#{req.fzMaterialCost}" +
        ")")
    boolean insertQZInfoReport(@Param("req") ItemInfoEntityOfQZNew itemInfoEntityOfQZNew);

    @Select("select count(*) from atw_item_info_part_report where item_code = #{itemCode}")
    Integer selectXLJInfoReportExisted(@Param("itemCode") String itemCode);

    @Select("select count(*) from atw_item_info_lzq_report where item_code = #{itemCode}")
    Integer selectLZQInfoReportExisted(@Param("itemCode") String itemCode);

    @Select("select count(*) from atw_qz_report where item_code = #{itemCode}")
    Integer selectQZInfoReportExisted(@Param("itemCode") String itemCode);

    @Delete("delete from atw_item_info_part_report where item_code = #{itemCode}")
    boolean deletedXLJInfoReportByItemCode(@Param("itemCode") String itemCode);

    @Delete("delete from atw_item_info_lzq_report where item_code = #{itemCode}")
    boolean deletedLZQInfoReportByItemCode(@Param("itemCode") String itemCode);

    @Delete("delete from atw_qz_report where item_code = #{itemCode}")
    boolean deletedQZInfoReportByItemCode(@Param("itemCode") String itemCode);

    @Select("select * from atw_item_info_part_report where item_code = #{itemCode} and sup_code = #{supCode}")
    ItemInfoEntityOfXLJ selectXLJInfoReportByItemAndSupCode(@Param("itemCode") String itemCode,@Param("supCode") String supCode);

    @DS("oracle")
    PrFromOracleDTO SelectSrmRpt(@Param("prCodeAndprLn") String prCodeAndprLn);

    @DS("oracle")
    @Select("select iteminfo_itemid from atwerp.pr_pr a,atwerp.pr_prline  b   where a.id=b.pr and a.docno=#{prCode} and b.doclineno=#{prLn} ")
    Long SelectItemid(@Param("prCode") String prCode,@Param("prLn") String prLn);

    @DS("oracle")
    @Select("select count(*) from atwerp.cbo_bommaster a left join atwerp.CBO_BOMCOMPONENT b on a.id=b.bommaster where b.subcompguid is not null and a.status=2 and b.itemmaster=#{itemId} and a.DISABLEDATE>sysdate")
    int SelectIsReplace(@Param("itemId") Long itemId);

    @DS("oracle")
    int SelectIsProject(@Param("prCodeAndprLn") String prCodeAndprLn);

    @DS("oracle")
    @Select("select id from atwerp.cbo_itemmaster where code=#{itemCode} and org='1001606030000019' ")
    Long selectItemIdByCode(@Param("itemCode") String itemCode);



    @DS("oracle")
    @Select("select nvl(SafetyStockQty,0) SafetyStockQty from  atwerp.CBO_InventoryInfo a where a.itemmaster=#{itemId}")
    Double SelectIsSafeStock(@Param("itemId") Long itemId);

    @DS("oracle")
    Double selectSafeStockDiff(@Param("itemId") Long itemId);


    @DS("oracle")
    List<PrFromOracleDTO> selectDSTreeByItemId(@Param("itemId") Long itemId);

    @DS("oracle")
    PrFromOracleDTO selectDSTreeByDSinfo(@Param("dsinfo") String dsinfo);

    @DS("oracle")
    @Select("select b.code,b.id,a.project,c.code projectcode from  atwerp.MRP_DSInfo a left join  atwerp.cbo_itemmaster b on a.item=b.id left join atwerp.cbo_project c on a.project=c.id where a.id=#{DSInfoId}")
    PrFromOracleDTO selectDSInfo(@Param("DSInfoId") String DSInfoId);

    @DS("oracle")
    @Select("select  t.子项目号 zxmh,t.项目交期 xmjq,t.销售单号 xsdh,t.物料编号 wlbh,t.供应号 gyh,t.供应类型 gylx,t.需求时间 xqsj from APSDB.PEG_RESULT_ALL_BY_COLUMN t where 物料编号 =#{itemCode}  order by t.项目交期 desc")
    List<PrFromOracleDTO> SelectQTData(@Param("itemCode") String itemCode);

    @DS("oracle")
    @Select("select  t.子项目号 zxmh,t.项目交期 xmjq,t.销售单号 xsdh,t.物料编号 wlbh,t.供应号 gyh,t.供应类型 gylx,t.需求时间 xqsj from APSDB.PEG_RESULT_ALL_BY_COLUMN t where 子项目号 =#{project} and t.物料编号=#{itemId} order by t.项目交期 desc")
    List<PrFromOracleDTO> SelectQTDataByProject(@Param("project") String project,@Param("itemId") Long itemId);

    @DS("oracle")
    @Select("select  t.子项目号 zxmh,t.项目交期 xmjq,t.销售单号 xsdh,t.物料编号 wlbh,t.供应号 gyh,t.供应类型 gylx,t.需求时间 xqsj from APSDB.PEG_RESULT_ALL_BY_COLUMN t where 子项目号 =#{project} and t.供应类型='无供应' order by t.项目交期 desc")
    List<PrFromOracleDTO> SelectQTDataByProjectWGY(@Param("project") String project);

    @DS("oracle")
    PrFromOracleDTO SelectQTDataByPrLn(@Param("prCodeAndprLn") String prCodeAndprLn);

    @DS("oracle")
    Date selectBomDateByProject(@Param("project") String project);

    @DS("oracle")
    @Select("select t.approvedon  from atwerp.pr_pr t where t.docno=#{prCode} and rownum=1")
    Date selectPrdata(@Param("prCode") String prCode);

    @Update("update atw_u9_pr set is_deleted=1 where pr_code=#{prCode} and pr_ln=#{prLn}")
    boolean deletedPrByPrLn(@Param("prCode") String prCode,@Param("prLn") int prLn);

    @SqlParser(filter = true)
    List<ItemInfoEntityOfDZ> selectDZResItemInfo(@Param("req") ItemInfoEntityOfDZ req);


    @SqlParser(filter = true)
    ItemInfoEntityOfDZ selectItemPriceOfDZ(@Param("req") ItemInfoEntityOfDZ itemInfoEntity);


    @SqlParser(filter = true)
    ItemInfoEntityOfDZ selectItemSprayingOfDZ(@Param("req") ItemInfoEntityOfDZ itemInfoEntity);


    @SqlParser(filter = true)
    ItemInfoEntityOfDZ selectItemGrindingOfDZ(@Param("req") ItemInfoEntityOfDZ itemInfoEntity);

    @SqlParser(filter = true)
    ItemInfoEntityOfDZ selectItemProcessingOfDZ(@Param("req") ItemInfoEntityOfDZ itemInfoEntity);

    @Select("select * from atw_fl_standard_relate where inch=#{req.size}")
    ItemInfoEntityOfFL selectItemStandardOfFL(@Param("req") ItemInfoEntityOfFL itemInfoEntity);


    @SqlParser(filter = true)
    List<ItemInfoEntityOfFL> selectItemPriceOfFL(@Param("req") ItemInfoEntityOfFL itemInfoEntity);

    @SqlParser(filter = true)
    List<ItemInfoEntityOfBZX> selectItemPriceOfBZX(@Param("req") ItemInfoEntityOfBZX itemInfoEntityOfBZX);

    @Select("SELECT * FROM atw_fl_weight where sup_code=#{req.supCode} and item_code=#{req.itemCode}")
    ItemInfoEntityOfFL selectItemWeightOfFL(@Param("req") ItemInfoEntityOfFL itemInfoEntity);



    @Insert("INSERT atw_item_info_dz_report (sup_code,sup_name,item_code,item_name,quantity,outer_size,outer_res,length_size,length_res,weight,single_cost,material_cost,process_cost,cutting_cost,grinding_cost,spraying_outer_size,spraying_length_size,spraying_area,spraying_price,spraying_fee,price,total_cost,outer_size_old,height_size,k) VALUES (#{req.supCode}," +
        "#{req.supName},#{req.itemCode},#{req.itemName},#{req.quantity},#{req.outerSize},#{req.outerRes},#{req.lengthSize},#{req.lengthRes},#{req.weight},#{req.singleCost},#{req.materialCost},#{req.processCost},#{req.cuttingCost},#{req.grindingCost},#{req.sprayingOuterSize},#{req.sprayingLengthSize},#{req.sprayingArea},#{req.sprayingPrice},#{req.sprayingFee},#{req.price},#{req.totalCost},#{req.outerSizeOld},#{req.heightSize},#{req.k})")
    boolean insertDZInfoReport(@Param("req") ItemInfoEntityOfDZ itemInfoEntityOfDZ);

    @Select("select count(*) from atw_item_info_dz_report where item_code = #{itemCode}")
    Integer selectDZInfoReportExisted(@Param("itemCode") String itemCode);

    @Delete("delete from atw_item_info_dz_report where item_code = #{itemCode}")
    boolean deletedDZInfoReportByItemCode(@Param("itemCode") String itemCode);


    @Insert("insert atw_item_info_fl_report (pr_code,pr_ln,sup_name,sup_code,item_code,item_name,quantity,weight,single_cost,price,total_cost) " +
        "VALUES (#{req.prCode},#{req.prLn},#{req.supName},#{req.supCode},#{req.itemCode},#{req.itemName},#{req.quantity},#{req.weight},#{req.singleCost},#{req.price},#{req.totalCost})")
    boolean insertFLInfoReport(@Param("req") ItemInfoEntityOfFL itemInfoEntityOfFL);

    @Select("select count(*) from atw_item_info_fl_report where item_code = #{itemCode}")
    Integer selectFLInfoReportExisted(@Param("itemCode") String itemCode);

    @Delete("delete from atw_item_info_fl_report where item_code = #{itemCode}")
    boolean deleteFLInfoReportByItemCode(@Param("itemCode") String itemCode);


    @Insert("INSERT INTO atw_u9_pr_no_project  (pr_code,pr_ln,item_code,item_name,u9_data,create_time) VALUES (#{req.prCode},#{req.prLn},#{req.itemCode},#{req.itemName},#{req.u9Data},NOW())")
    boolean insertPrNoProject(@Param("req") U9PrFromPhpDTO u9PrFromPhpDTO);


    @Select("SELECT *  from atw_u9_pr_no_project where pr_code=#{req.prCode} and pr_ln=#{req.prLn}")
    U9PrFromPhpDTO selectPrNoProject(@Param("req") U9PrFromPhpDTO u9PrFromPhpDTO);


}

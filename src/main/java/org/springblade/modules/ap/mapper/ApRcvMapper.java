package org.springblade.modules.ap.mapper;

import com.baomidou.mybatisplus.annotation.SqlParser;
import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import org.apache.ibatis.annotations.*;
import org.springblade.core.tool.utils.BeanUtil;
import org.springblade.modules.ap.dto.ApReq;
import org.springblade.modules.ap.entity.ApRcvEntity;
import org.springblade.modules.ap.entity.ApRcvReqEntity;
import org.springblade.modules.ap.entity.ApReqSettle;
import org.springblade.modules.ap.vo.ApRcvVO;

import java.util.Date;
import java.util.List;

/**
 *  Mapper 接口
 *
 * @author Will
 */
public interface ApRcvMapper extends BaseMapper<ApRcvEntity> {

    List<ApRcvEntity> getList(@Param("apReq") ApReq apReq);

    IPage<ApRcvEntity> getPage(IPage<ApRcvEntity> page, ApReq apReq);

    @SqlParser(filter = true)
    List<ApRcvReqEntity> getVmiList(@Param("apReq") ApReq apReq);

    IPage<ApRcvReqEntity> getVmiPage(IPage<ApRcvEntity> page, ApReq apReq);

    @Select("SELECT ifnull(SUM(req_accum_rec_qty),0) from  atw_ap_req_settle where settle_rcv_code=#{settleCode} ")
    int getAccumCount(@Param("settleCode") String settleCode);

    @Select("SELECT * from  atw_ap_req_settle where settle_rcv_code=#{settleCode} ")
    List<ApReqSettle> getApReqSettleBySettleCode(@Param("settleCode") String settleCode);

    @Select("select * from atw_ap_rcv where rcv_code=#{RcvCode} and rcv_ln=#{RcvLn} limit 1")
    ApRcvEntity getApReqByCodeLn(@Param("RcvCode") String RcvCode,@Param("RcvLn") String RcvLn);

    @Update("update atw_ap_rcv set  accum_rec_qty =accum_rec_qty-#{AccQty}  where rcv_code=#{RcvCode} and rcv_ln=#{RcvLn}")
    Boolean updateApReqByCodeLn(@Param("RcvCode") String RcvCode,@Param("RcvLn") String RcvLn,@Param("AccQty") String AccQty);


    @Delete("delete from  atw_ap_rcv where rcv_code=#{settleCode}")
    Boolean deleteApRcv(@Param("settleCode") String settleCode);

    @Delete("delete from  atw_ap_req_settle where settle_rcv_code=#{settleCode}")
    Boolean deleteSettleRcv(@Param("settleCode") String settleCode);

    int getPageCount();

    String getProNoByApId(Long id, String type);

    int getTodayCount(Date start, Date end,String type);

    @Update("update atw_ap_req_settle set vmi_status_new = #{vmiStatus} where id = #{id}")
    boolean reviewVmiRcv(@Param("id") String id, @Param("vmiStatus") String vmiStatus);

    List<ApRcvEntity> selectByIdList(@Param("ids") String[] ids);

    List<ApRcvEntity> getSettleVmiList();

    List<ApRcvEntity> getSettleVmiListNow();

    List<ApRcvEntity> getReqVmiList(@Param("supCode") String supCode,@Param("itemCode") String itemCode);

    @Update("update atw_ap_rcv set accum_rec_qty = accum_rec_qty + #{accumRecQty} where id = #{id}")
    boolean updateReqRcv(@Param("id") String id, @Param("accumRecQty") String accumRecQty);

    @Update("update atw_po_item set vmi_accum_rec_qty = vmi_accum_rec_qty + #{qty} where po_code = #{poCode} and po_ln = #{poLn}")
    boolean updatePoItem(@Param("poCode") String poCode, @Param("poLn") String poLn, @Param("qty") int qty);

    @Insert("Insert atw_ap_req_settle(settle_rcv_code,settle_rcv_ln,req_rcv_code,req_rcv_ln,req_po_code,req_po_ln,req_rcv_num,req_accum_rec_qty) value (#{settleRcvCode},#{settleRcvLn},#{reqRcvCode},#{reqRcvLn},#{reqPoCode},#{reqPoLn},#{reqRcvNum},#{reqAccumReqNum})")
    boolean insertReqSettle(@Param("settleRcvCode") String settleRcvCode, @Param("settleRcvLn") String settleRcvLn, @Param("reqRcvCode") String reqRcvCode,@Param("reqRcvLn") String reqRcvLn,@Param("reqPoCode") String reqPoCode,@Param("reqPoLn") String reqPoLn,@Param("reqRcvNum") String reqRcvNum,@Param("reqAccumReqNum") String reqAccumReqNum);

    ApRcvEntity getSettleVmiInfoByReqId(@Param("id") String id);

    ApRcvReqEntity getReqVmiInfoByReqId(@Param("id") String id);

    List<ApRcvVO> getListOfVmi(@Param("ids") String ids);

    @Update("update atw_ap_req_settle set req_accum_rec_qty = req_accum_rec_qty - #{num} where id = #{id}")
    boolean updateReqRcvRemove(@Param("id") String id, @Param("num") Integer num);

    @Update("update atw_ap_req_settle set req_accum_rec_qty = req_accum_rec_qty + #{num} where id = #{id}")
    boolean updateReqRcvAdd(@Param("id") String id, @Param("num") Integer num);
}


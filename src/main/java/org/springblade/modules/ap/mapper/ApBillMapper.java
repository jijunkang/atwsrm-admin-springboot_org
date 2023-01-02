package org.springblade.modules.ap.mapper;

import com.baomidou.mybatisplus.annotation.SqlParser;
import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import org.apache.ibatis.annotations.Param;
import org.apache.ibatis.annotations.Select;
import org.apache.ibatis.annotations.Update;
import org.springblade.modules.ap.dto.ApReq;
import org.springblade.modules.ap.entity.ApBillEntity;
import org.springblade.modules.ap.entity.ApItemEntity;
import org.springblade.modules.ap.entity.ApRcvReqEntity;
import org.springblade.modules.ap.entity.ApReqSettle;
import org.springblade.modules.po.dto.PoItemDTO;
import org.springblade.modules.po.vo.PoItemVO;

import java.math.BigDecimal;
import java.util.List;

/**
 *  Mapper 接口
 *
 * @author Will
 */
public interface ApBillMapper extends BaseMapper<ApBillEntity> {

    @SqlParser(filter = true)
    boolean backToRecOfRCV(String apCode);

    @SqlParser(filter = true)
    boolean backToRecOfRCVOFVMI(String apCode);

    @SqlParser(filter = true)
    boolean backToRecOfAPI(String apCode);

    @SqlParser(filter = true)
    boolean backToRecOfAPB(String apCode);

    @Select("Select * from atw_ap_item where bill_code = #{apCode}")
    List<ApItemEntity> getApInfoByApCode(String apCode);


    @Select("Select * from atw_ap_req_settle where id = #{id}")
    ApReqSettle getReqInfoById(String id);

    IPage<ApBillEntity> selectApPage(IPage<ApBillEntity> page, ApReq apReq);

    @Update("update atw_ap_req_settle set req_accum_rec_qty = req_accum_rec_qty - #{recThisQty} where id = #{id}")
    void updateApReqSettleById(@Param("id") String reqRcvId, @Param("recThisQty") BigDecimal recThisQty);

    @Update("update atw_ap_rcv set accum_rec_qty = accum_rec_qty - #{recThisQty} where rcv_code = #{rcvCode} and rcv_ln =#{rcvLn}")
    void updateApRcvByCodeLn(@Param("rcvCode") String rcvCode,@Param("rcvLn") String rcvLn, @Param("recThisQty") BigDecimal recThisQty);
}

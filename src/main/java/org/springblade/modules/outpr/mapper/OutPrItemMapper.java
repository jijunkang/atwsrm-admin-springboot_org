package org.springblade.modules.outpr.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import org.apache.ibatis.annotations.Param;
import org.apache.ibatis.annotations.Select;
import org.springblade.modules.outpr.dto.OutPrItemDTO;
import org.springblade.modules.outpr.entity.OutPrItemEntity;
import org.springblade.modules.outpr.vo.OutPrItemVO;

import java.util.List;


/**
 *  Mapper 接口
 *
 * @author Will
 */
public interface OutPrItemMapper extends BaseMapper<OutPrItemEntity> {

    @Select({
        "select * from atw_out_pr_item where pr_code = #{prCode} and item_code = #{itemCode}"
    })
    OutPrItemEntity getByPrcodeAndItemcode(@Param("prCode") String prCode, @Param("itemCode") String itemCode);

    int getCountFromOut(@Param("dto") OutPrItemDTO outPrItemDTO);

    List<OutPrItemEntity> selectOutPageList();

    String selectAttachment(@Param("id")Long id);

    boolean deleteIo(@Param("id")Long id);
    boolean updateOutPr(@Param("id")Long id);

    IPage<OutPrItemVO> selectOutPrFlowPage(IPage<OutPrItemVO> page, @Param("dto")  OutPrItemDTO queryDto);

    IPage<OutPrItemVO> selectOutPrInquiryTabPage(IPage<OutPrItemVO> page, @Param("dto")  OutPrItemDTO queryDto);

    IPage<OutPrItemVO> selectOutPrCheckPage(IPage<OutPrItemVO> page, @Param("dto")  OutPrItemDTO queryDto);

    IPage<OutPrItemVO> selectOutPrInquiryBidPage(IPage<OutPrItemVO> page, @Param("dto")  OutPrItemDTO queryDto);

    List<OutPrItemVO> selectInquiryPageOfWW(@Param("dto")  OutPrItemDTO queryDto);

    int selectInquiryPageOfWWForZT(@Param("dto")  OutPrItemDTO queryDto);

}






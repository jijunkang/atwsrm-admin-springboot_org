package org.springblade.modules.ap.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import org.apache.ibatis.annotations.Param;
import org.springblade.modules.ap.dto.ApReq;
import org.springblade.modules.ap.entity.ApEntity;
import org.springblade.modules.pr.dto.PrReq;
import org.springblade.modules.pr.dto.U9PrDTO;

/**
 *  Mapper 接口
 *
 * @author Will
 */
public interface ApMapper extends BaseMapper<ApEntity> {

    int  getApCountWithoutVmi(@Param("apReq") ApReq apReq);

    int  getApCountWithVmi(@Param("apReq") ApReq apReq);

    IPage<ApEntity> getApPageWithVmi(IPage<ApEntity> page, @Param("apReq") ApReq apReq);

}

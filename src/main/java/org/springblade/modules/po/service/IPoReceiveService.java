package org.springblade.modules.po.service;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import org.apache.ibatis.annotations.Param;
import org.springblade.core.mp.base.BaseService;
import org.springblade.core.mp.support.Condition;
import org.springblade.core.tool.api.R;
import org.springblade.modules.po.dto.PoReceiveDTO;
import org.springblade.modules.po.entity.PoReceiveEntity;
import org.springblade.modules.po.vo.PoReceiveVO;
import org.springblade.modules.pr.dto.PrReq;
import org.springblade.modules.pr.dto.U9PrDTO;

import javax.servlet.http.HttpServletResponse;
import java.util.List;

/**
 *  服务类
 *
 * @author Will
 */
public interface IPoReceiveService extends BaseService<PoReceiveEntity> {

    // 状态同 po_item
    public final static Integer STATUS_INIT  = 10; // '待下单'
    public final static Integer STATUS_ORDER = 20; // 待审核
    public final static Integer STATUS_INNER_TOCHECK = 21; // 厂内已报检
    public final static Integer STATUS_OUT_TOCHECK = 22; // 厂外已报检
    public final static Integer STATUS_INNER_CHECK = 23; // 厂内已检验
    public final static Integer STATUS_OUT_CHECK = 24; // 厂内已检验
    public final static Integer STATUS_TOHANDLE = 25; // 待处理
    public final static Integer STATUS_RECEIVE = 26; // 已点收
    public final static Integer STATUS_VI_STORE = 27; // 虚拟已入库

    Integer STATUS_CLOSE = 30; // 关闭
    Integer STATUS_CANCEL = 40; // 作废

    String STATUS_UPDATE = "20,25"; // 更新

    // 是否外检
    String OUT = "1";
    String INNER = "0";

    R createAsR(List<PoReceiveDTO> poReceiveList);

    QueryWrapper<PoReceiveEntity> getQueryWrapper(PoReceiveEntity poReceive);

    R reCreateAsR(List<PoReceiveDTO> poReceiveList);

    R doCancel(List<PoReceiveDTO> poReceiveList);

    R doClose(List<PoReceiveDTO> poReceiveList);

    R doBusiness(List<PoReceiveDTO> poReceiveList);

    R doRecovery(List<PoReceiveDTO> poReceiveList);

    R setOut(List<PoReceiveDTO> poReceiveList);

    void export(PoReceiveDTO poReceiveDTO, HttpServletResponse response);

    IPage<PoReceiveVO> selectPageOfParams(IPage<PoReceiveDTO> page,PoReceiveDTO poReceive);
}

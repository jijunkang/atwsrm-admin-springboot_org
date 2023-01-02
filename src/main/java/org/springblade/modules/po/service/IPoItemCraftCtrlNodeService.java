package org.springblade.modules.po.service;

import com.baomidou.mybatisplus.extension.service.IService;
import org.springblade.modules.po.dto.PoItemNodeListDTO;
import org.springblade.modules.po.entity.PoItemCraftCtrlNodeEntity;
import org.springblade.modules.po.vo.PoItemCraftCtrlNodeVO;

import java.util.List;

/**
 * @author libin
 *
 * @date 15:19 2020/7/22
 **/
public interface IPoItemCraftCtrlNodeService extends IService<PoItemCraftCtrlNodeEntity> {

    List<PoItemCraftCtrlNodeVO> getByPoItemId(Long poItemId);

    List<PoItemNodeListDTO> getDTOS(Long poItemId);

    List<PoItemCraftCtrlNodeEntity> getByParentId(Long parentId);

    List<PoItemCraftCtrlNodeEntity> getGroupByPoItemId(Long parentId);

    List<PoItemCraftCtrlNodeEntity> getByChildIds(String ids);
}

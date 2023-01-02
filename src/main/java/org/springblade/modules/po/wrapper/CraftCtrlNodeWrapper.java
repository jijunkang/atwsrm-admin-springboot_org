
package org.springblade.modules.po.wrapper;

import org.springblade.core.mp.support.BaseEntityWrapper;
import org.springblade.core.tool.node.ForestNodeMerger;
import org.springblade.core.tool.node.INode;
import org.springblade.core.tool.utils.BeanUtil;
import org.springblade.modules.po.entity.CraftCtrlNodeEntity;
import org.springblade.modules.po.vo.CraftCtrlNodeVO;

import java.util.List;
import java.util.stream.Collectors;


/**
 * @author libin
 *
 * @date 11:27 2020/7/22
 **/
public class CraftCtrlNodeWrapper extends BaseEntityWrapper<CraftCtrlNodeEntity, CraftCtrlNodeVO> {

    public static CraftCtrlNodeWrapper build() {
        return new CraftCtrlNodeWrapper();
    }

    @Override
    public CraftCtrlNodeVO entityVO(CraftCtrlNodeEntity craftCtrlNodeEntity) {
        CraftCtrlNodeVO craftCtrlNodeVO = BeanUtil.copy(craftCtrlNodeEntity, CraftCtrlNodeVO.class);
        return craftCtrlNodeVO;
    }

    public List<INode> listNodeVO(List<CraftCtrlNodeEntity> list) {
        List<INode> collect = list.stream().map(dict -> BeanUtil.copy(dict, CraftCtrlNodeVO.class)).collect(Collectors.toList());
        return ForestNodeMerger.merge(collect);
    }

}

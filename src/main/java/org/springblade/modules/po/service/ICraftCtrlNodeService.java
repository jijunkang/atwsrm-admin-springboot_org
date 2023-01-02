package org.springblade.modules.po.service;

import com.baomidou.mybatisplus.extension.service.IService;
import org.springblade.core.mp.support.Query;
import org.springblade.modules.po.entity.CraftCtrlNodeEntity;
import org.springblade.modules.po.vo.CraftCtrlNodeVO;
import org.springframework.web.multipart.MultipartFile;

import javax.servlet.http.HttpServletResponse;
import java.util.List;
import java.util.Map;

/**
 * @author libin
 *
 * @date 11:48 2020/7/22
 **/
public interface ICraftCtrlNodeService extends IService<CraftCtrlNodeEntity> {

	/**
	 * 树形结构
	 *
	 * @return
	 */
	List<CraftCtrlNodeVO> tree();

	/**
	 * 获取字典表对应中文
	 *
	 * @param code
	 * @param name
	 * @return
	 */
	String getValue(String code, String name);

	/**
	 * 获取字典表
	 *
	 * @param code 字典编号
	 * @return
	 */
	List<CraftCtrlNodeEntity> getList(String code);

	/**
	 * 新增或修改
	 *
	 * @param dict
	 * @return
	 */
	boolean submit(CraftCtrlNodeEntity dict);


    boolean update(CraftCtrlNodeEntity dict);

	/**
	 * 删除字典
	 *
	 * @param ids
	 * @return
	 */
	boolean removeCraftCtrlNode(String ids);

	String genCode();

    CraftCtrlNodeEntity getByCode(String code);

    CraftCtrlNodeEntity getByName(String name);

    CraftCtrlNodeEntity getChild(String parentName, Integer sort, String childName);

    void export(CraftCtrlNodeEntity craftCtrlNodeEntity, Query query, HttpServletResponse response) throws Exception;

    Map<String, Object> importExcel(MultipartFile file) throws Exception;
}

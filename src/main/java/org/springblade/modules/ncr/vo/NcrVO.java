package org.springblade.modules.ncr.vo;

import lombok.Data;
import lombok.EqualsAndHashCode;
import org.springblade.modules.ncr.entity.NcrEntity;
import org.springblade.modules.ncr.entity.NcrMeasuresEntity;

import java.util.List;

/**
 *  模型VO
 *
 * @author Will
 */
@Data
@EqualsAndHashCode(callSuper = true)
public class NcrVO extends NcrEntity {

	private static final long serialVersionUID = 1L;

    /**
     * 纠正措施集
     */
	List<NcrMeasuresEntity> rectifyList;
    /**
     * 预防措施集
     */
	List<NcrMeasuresEntity> preventList;
}

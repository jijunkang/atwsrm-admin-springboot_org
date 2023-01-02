package org.springblade.modules.outpr.vo;

import lombok.Data;
import org.springblade.modules.outpr.dto.DoDTO;

import java.io.Serializable;
import java.util.List;

/**
 * 模型DTO
 * @author Will
 */
@Data
public
class ResultVO implements Serializable {

	private static final long serialVersionUID = 1L;

	private String code;

	private String msg;

	private List<ParamsVO> result;
}

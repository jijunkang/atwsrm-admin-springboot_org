package org.springblade.modules.outpr.vo;

import lombok.Data;

import java.io.Serializable;

/**
 * 模型DTO
 * @author Will
 */
@Data
public
class MesVo implements Serializable {

	private static final long serialVersionUID = 1L;

    private String code;
    private String msg;
    private String RCVDocNo;
    private String U9RCVDocNo;
}

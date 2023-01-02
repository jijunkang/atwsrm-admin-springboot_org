package org.springblade.modules.po.dto;

import lombok.Data;
import org.springblade.modules.po.entity.IoEntity;

import java.util.Date;

/**
 * 模型DTO
 * @author Will
 */
@Data
public
class PoOffsetViewInfo extends IoEntity{

    private static final long serialVersionUID = 1L;

    Date lastSyncTime;

}

package org.springblade.modules.queue.service.impl;

import org.springblade.core.mp.base.BaseServiceImpl;
import org.springblade.modules.queue.entity.QueueSmsEntity;
import org.springblade.modules.queue.mapper.QueueSmsMapper;
import org.springblade.modules.queue.service.IQueueSmsService;
import org.springframework.stereotype.Service;

/**
 *  服务实现类
 *
 * @author Will
 */
@Service
public class QueueSmsServiceImpl extends BaseServiceImpl<QueueSmsMapper, QueueSmsEntity> implements IQueueSmsService {

}

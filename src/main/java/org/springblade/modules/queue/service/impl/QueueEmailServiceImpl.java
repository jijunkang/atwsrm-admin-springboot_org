package org.springblade.modules.queue.service.impl;

import org.springblade.core.mp.base.BaseServiceImpl;
import org.springblade.modules.queue.entity.QueueEmailEntity;
import org.springblade.modules.queue.mapper.QueueEmailMapper;
import org.springblade.modules.queue.service.IQueueEmailService;
import org.springframework.stereotype.Service;

/**
 *  服务实现类
 *
 * @author Will
 */
@Service
public class QueueEmailServiceImpl extends BaseServiceImpl<QueueEmailMapper, QueueEmailEntity> implements IQueueEmailService {

}

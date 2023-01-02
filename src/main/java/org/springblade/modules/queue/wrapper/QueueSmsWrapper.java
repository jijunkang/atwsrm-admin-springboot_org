package org.springblade.modules.queue.wrapper;

import org.springblade.core.mp.support.BaseEntityWrapper;
import org.springblade.core.tool.utils.BeanUtil;
import org.springblade.modules.queue.entity.QueueSmsEntity;
import org.springblade.modules.queue.vo.QueueSmsVO;

/**
 *  包装类,返回视图层所需的字段
 *
 * @author Will
 */
public class QueueSmsWrapper extends BaseEntityWrapper<QueueSmsEntity, QueueSmsVO>  {

	public static QueueSmsWrapper build() {
		return new QueueSmsWrapper();
 	}

	@Override
	public QueueSmsVO entityVO(QueueSmsEntity queueSms) {
		QueueSmsVO queueSmsVO = BeanUtil.copy(queueSms, QueueSmsVO.class);

		return queueSmsVO;
	}

}

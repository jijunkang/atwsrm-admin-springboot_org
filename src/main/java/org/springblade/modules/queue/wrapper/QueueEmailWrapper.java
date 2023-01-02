package org.springblade.modules.queue.wrapper;

import org.springblade.core.mp.support.BaseEntityWrapper;
import org.springblade.core.tool.utils.BeanUtil;
import org.springblade.modules.queue.entity.QueueEmailEntity;
import org.springblade.modules.queue.vo.QueueEmailVO;

/**
 *  包装类,返回视图层所需的字段
 *
 * @author Will
 */
public class QueueEmailWrapper extends BaseEntityWrapper<QueueEmailEntity, QueueEmailVO>  {

	public static QueueEmailWrapper build() {
		return new QueueEmailWrapper();
 	}

	@Override
	public QueueEmailVO entityVO(QueueEmailEntity queueEmail) {
		QueueEmailVO queueEmailVO = BeanUtil.copy(queueEmail, QueueEmailVO.class);

		return queueEmailVO;
	}

}

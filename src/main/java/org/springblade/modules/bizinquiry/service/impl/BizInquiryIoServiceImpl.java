package org.springblade.modules.bizinquiry.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import org.springblade.core.log.exception.ServiceException;
import org.springblade.core.mp.base.BaseServiceImpl;
import org.springblade.core.mp.support.Condition;
import org.springblade.core.tool.api.R;
import org.springblade.modules.bizinquiry.dto.BizInquiryReq;
import org.springblade.modules.bizinquiry.entity.BizInquiryEntity;
import org.springblade.modules.bizinquiry.entity.BizInquiryIoEntity;
import org.springblade.modules.bizinquiry.entity.BizInquiryIoFileListEntity;
import org.springblade.modules.bizinquiry.mapper.BizInquiryIoFileNameMapper;
import org.springblade.modules.bizinquiry.mapper.BizInquiryIoMapper;
import org.springblade.modules.bizinquiry.service.IBizInquiryIoService;
import org.springblade.modules.bizinquiry.service.IBizInquiryService;
import org.springblade.modules.queue.entity.QueueEmailEntity;
import org.springblade.modules.queue.service.IQueueEmailService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.ArrayList;
import java.util.List;

/**
 * 服务实现类
 *
 * @author Will
 */
@Service
public class BizInquiryIoServiceImpl extends BaseServiceImpl<BizInquiryIoMapper, BizInquiryIoEntity> implements IBizInquiryIoService {

    @Autowired
    IBizInquiryService bizInquiryService;

    @Autowired
    BizInquiryIoFileNameMapper bizInquiryIoFileNameMapper;

    @Autowired
    private IQueueEmailService queueEmailService;

    @Transactional(rollbackFor = Exception.class)
    @Override
    public boolean saveBizBatch(BizInquiryReq bizInquiryReq) {
        List<BizInquiryEntity> bizInquiryEntities = bizInquiryReq.getBizInquiryEntities();
        List<BizInquiryIoEntity> bizInquiryIoEntities = bizInquiryReq.getBizInquiryIoEntities();
        boolean result = false;
        for (BizInquiryEntity bizInquiryEntity : bizInquiryEntities) {
            for (BizInquiryIoEntity bizInquiryIoEntity : bizInquiryIoEntities) {
                BizInquiryIoEntity entity = new BizInquiryIoEntity();
                entity.setQoId(bizInquiryEntity.getId());
                entity.setQoCode(bizInquiryEntity.getQoCode());
                entity.setSupName(bizInquiryIoEntity.getSupName());
                entity.setPrice(bizInquiryIoEntity.getPrice());
                entity.setConfirmDate(bizInquiryIoEntity.getConfirmDate());
                entity.setBackReason(bizInquiryIoEntity.getBackReason());
                entity.setAttachment(bizInquiryIoEntity.getAttachment());
                result = save(entity);
            }
            //bizInquiryEntity.setStatus(IBizInquiryService.STATUS_HAS);
            bizInquiryService.updateById(bizInquiryEntity);
        }
        return result;
    }

    /**
     * 保存文件
     *
     * @param bizInquiryReq
     * @return
     */
    @Transactional(rollbackFor = Exception.class)
    @Override
    public boolean saveFile(BizInquiryReq bizInquiryReq) {
        String attachment = bizInquiryReq.getAttachment();

        String[] files = attachment.split(",");
        if (files.length > 0) {
            for (String fileToUpload : files) {
                BizInquiryIoFileListEntity bizInquiryIoFileListEntity = new BizInquiryIoFileListEntity();
                // qo_id
                bizInquiryIoFileListEntity.setQoId(bizInquiryReq.getQoId());
                // 未发送
                bizInquiryIoFileListEntity.setStatus(0);
                // 上传时间
                long timeStampSec = System.currentTimeMillis() / 1000;
                String timestamp = String.format("%010d", timeStampSec);
                bizInquiryIoFileListEntity.setUploadTime(Long.valueOf(timestamp));
                // 文件名称
                bizInquiryIoFileListEntity.setFileName(fileToUpload.substring(fileToUpload.lastIndexOf("/") + 1));
                // 文件url
                bizInquiryIoFileListEntity.setFileUrl(fileToUpload.substring(0, fileToUpload.lastIndexOf("/")));

                boolean isSuccess = bizInquiryIoFileNameMapper.insertFile(bizInquiryIoFileListEntity);

                boolean success = bizInquiryIoFileNameMapper.updateQoAttachment(bizInquiryReq.getQoId());

                if (!(isSuccess && success)) {
                    throw new ServiceException(bizInquiryIoFileListEntity.getFileName() + "：上传失败");
                }
            }
        }
        return true;
    }

    /**
     * 删除文件
     *
     * @param bizInquiryReq
     * @return
     */
    @Transactional(rollbackFor = Exception.class)
    @Override
    public boolean removeFileList(BizInquiryReq bizInquiryReq) {
        List<BizInquiryIoEntity> bizInquiryIoEntityList = bizInquiryReq.getBizInquiryIoEntities();
        for(BizInquiryIoEntity bizInquiryIoEntity : bizInquiryIoEntityList) {
            bizInquiryIoFileNameMapper.removeFileList(bizInquiryIoEntity.getQoId(),bizInquiryIoEntity.getAttachment());
        }

        // 如果不存在附件了，则变更状态
        if (bizInquiryIoFileNameMapper.fileIsExisted(bizInquiryIoEntityList.get(0).getQoId()) == 0) {
            bizInquiryIoFileNameMapper.updateIoAttachmentToZero(bizInquiryIoEntityList.get(0).getQoId());
        }

        return true;
    }

    /**
     * 批量保存文件
     *
     * @param bizInquiryReq
     * @return
     */
    @Override
    @Transactional(rollbackFor = Exception.class)
    public boolean saveFileList(BizInquiryReq bizInquiryReq) {
        List<BizInquiryIoEntity> bizInquiryIoEntityList = bizInquiryReq.getBizInquiryIoEntities();
        for (BizInquiryIoEntity bizInquiryIoEntity : bizInquiryIoEntityList) {
            String attachment = bizInquiryIoEntity.getAttachment();
            String[] files = attachment.split(",");
            if (files.length > 0) {
                for (String fileToUpload : files) {
                    BizInquiryIoFileListEntity bizInquiryIoFileListEntity = new BizInquiryIoFileListEntity();
                    // qo_id
                    bizInquiryIoFileListEntity.setQoId(bizInquiryIoEntity.getQoId());
                    // 未发送
                    bizInquiryIoFileListEntity.setStatus(0);
                    // 上传时间
                    long timeStampSec = System.currentTimeMillis() / 1000;
                    String timestamp = String.format("%010d", timeStampSec);
                    bizInquiryIoFileListEntity.setUploadTime(Long.valueOf(timestamp));
                    // 文件名称
                    bizInquiryIoFileListEntity.setFileName(fileToUpload.substring(fileToUpload.lastIndexOf("/") + 1));
                    // 文件url
                    bizInquiryIoFileListEntity.setFileUrl(fileToUpload.substring(0, fileToUpload.lastIndexOf("/")));
                    boolean isSuccess = bizInquiryIoFileNameMapper.insertFile(bizInquiryIoFileListEntity);
                    boolean success = bizInquiryIoFileNameMapper.updateQoAttachment(bizInquiryIoEntity.getQoId());
                    if (!(isSuccess && success)) {
                        throw new ServiceException(bizInquiryIoFileListEntity.getFileName() + "：上传失败");
                    }
                }
            }
        }
        return true;
    }

    @Override
    public BizInquiryIoEntity getByQoId(Long qoId) {
        QueryWrapper<BizInquiryIoEntity> qw = Condition.getQueryWrapper(new BizInquiryIoEntity());
        qw.eq("qo_id", qoId);
        return getOne(qw);
    }

    /**
     * 获得文件一栏
     * @param id
     * @return
     */
    @Override
    public List<BizInquiryIoFileListEntity> getFileList(Long id) {
        return bizInquiryIoFileNameMapper.getFileList(id);
    }

    /**
     * 发送邮件
     * @param bizInquiryReq
     * @return
     */
    @Override
    @Transactional(rollbackFor = Exception.class)
    public boolean sendEmailToBusiness(BizInquiryReq bizInquiryReq) {
        List<BizInquiryIoEntity> bizInquiryIoEntityList = bizInquiryReq.getBizInquiryIoEntities();
        BizInquiryEntity   entity   = bizInquiryService.getById(bizInquiryIoEntityList.get(0).getQoId());
        if(entity != null) {
            String emailContent = "<br>\n 报价单号：" + entity.getQoCode();
            emailContent += "<br>\n 型号：" + entity.getModel();
            emailContent += "<br>\n 商务部附件：" + entity.getCdAttachment();
            emailContent += "<br>\n 采购部上传附件：";
            for(BizInquiryIoEntity bizInquiryIoEntity : bizInquiryIoEntityList ){
                String attach = bizInquiryIoEntity.getAttachment();
                emailContent +=  "<br>\n" + attach;

                // 发送时间
                long timeStampSec = System.currentTimeMillis() / 1000;
                String timestamp = String.format("%010d", timeStampSec);
                Long sendTime = Long.valueOf(timestamp);

                boolean isSuccess = bizInquiryIoFileNameMapper.updateFileStatus(bizInquiryIoEntityList.get(0).getQoId(),attach.split("：")[1],sendTime);
                if(!isSuccess)  {
                    throw new ServiceException(bizInquiryIoEntityList.get(0).getQoId() + " + " + attach.split("：")[1] + "：发送附件失败");
                }
            }
            QueueEmailEntity queueEmailEntity = new QueueEmailEntity();
            queueEmailEntity.setSender(IQueueEmailService.BIZ_INTI_SENDER);
            queueEmailEntity.setReceiver(entity.getApplyEmail());
            queueEmailEntity.setSubject(entity.getQoCode() + "商务询价");
            queueEmailEntity.setContent(emailContent);
            queueEmailEntity.setSendCount(0);
            queueEmailEntity.setStatus(IQueueEmailService.STATUS_INIT);
            queueEmailService.save(queueEmailEntity);
            return true;
        } else {
            return false;
        }
    }

    /**
     * 批量发送未发送邮件
     * @param bizInquiryReq
     * @return
     */
    @Override
    @Transactional(rollbackFor = Exception.class)
    public boolean sendEmailListToBusiness(BizInquiryReq bizInquiryReq) {
        List<BizInquiryEntity> bizInquiryEntityList = bizInquiryReq.getBizInquiryEntities();

        // 对每一个报价编号进行循环操作
        for (BizInquiryEntity bizInquiryEntity : bizInquiryEntityList) {
            // 取得报价信息
            BizInquiryEntity entity = bizInquiryService.getById(bizInquiryEntity.getId());

            String emailContent = "<br>\n 报价单号：" + entity.getQoCode();
            emailContent += "<br>\n 型号：" + entity.getModel();
            emailContent += "<br>\n 商务部附件：" + entity.getCdAttachment();
            emailContent += "<br>\n 采购部上传附件：";

            //获得未发送的附件一览
            List<BizInquiryIoFileListEntity> fileListEntities = bizInquiryIoFileNameMapper.getFileListOfNotSend(bizInquiryEntity.getId());
            // 若没有，则不发送
            if(fileListEntities.size() > 0) {
                for (BizInquiryIoFileListEntity fileToSend : fileListEntities) {
                    emailContent += "<br>\n" + fileToSend.getFileName() + "：" + fileToSend.getFileUrl();

                    // 发送时间
                    long timeStampSec = System.currentTimeMillis() / 1000;
                    String timestamp = String.format("%010d", timeStampSec);
                    Long sendTime = Long.valueOf(timestamp);

                    // 改变发送状态
                    boolean isSuccess = bizInquiryIoFileNameMapper.updateFileStatus(bizInquiryEntity.getId(), fileToSend.getFileUrl(),sendTime);
                    if (!isSuccess) {
                        throw new ServiceException(bizInquiryEntity.getId() + " + " + fileToSend.getFileUrl() + "：发送附件失败");
                    }
                }
                QueueEmailEntity queueEmailEntity = new QueueEmailEntity();
                queueEmailEntity.setSender(IQueueEmailService.BIZ_INTI_SENDER);
                queueEmailEntity.setReceiver(entity.getApplyEmail());
                queueEmailEntity.setSubject(entity.getQoCode() + "商务询价");
                queueEmailEntity.setContent(emailContent);
                queueEmailEntity.setSendCount(0);
                queueEmailEntity.setStatus(IQueueEmailService.STATUS_INIT);
                queueEmailService.save(queueEmailEntity);
            }
        }
        return true;
    }
}

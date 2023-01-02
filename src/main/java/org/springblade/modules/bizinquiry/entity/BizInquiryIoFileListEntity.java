package org.springblade.modules.bizinquiry.entity;

import com.baomidou.mybatisplus.annotation.*;
import com.fasterxml.jackson.databind.annotation.JsonSerialize;
import com.fasterxml.jackson.databind.ser.std.ToStringSerializer;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;
import lombok.EqualsAndHashCode;
import org.springblade.core.mp.base.BaseEntity;

import java.io.Serializable;
import java.math.BigDecimal;


/**
 *  实体类
 *
 * @author Will
 */
@Data
@TableName("atw_biz_inquiry_io_file_list")
@ApiModel(value = "BizInquiryIoFileList对象", description = "")
public class BizInquiryIoFileListEntity implements Serializable {

	private static final long serialVersionUID = 1L;

    /**
     * 主键
     */
    @JsonSerialize(using = ToStringSerializer.class)
    @ApiModelProperty(value = "主键")
    @TableId(value = "id", type = IdType.ID_WORKER)
    private Long id;

    /**
     * atw_biz_inquiry_io的id
     */
    @JsonSerialize(using = ToStringSerializer.class)
    @ApiModelProperty(value = "atw_biz_inquiry的id")
    private Long qoId;

    /**
     * 文件名
     */
    @ApiModelProperty(value = "文件名")
    private String fileName;

    /**
     * 文件url
     */
    @ApiModelProperty(value = "文件url")
    private String fileUrl;

    /**
     * 上传日期
     */
    @ApiModelProperty(value = "上传日期")
    private Long uploadTime;


    /**
     * 发送日期
     */
    @ApiModelProperty(value = "发送日期")
    private Long sendTime;

    /**
     * 发送状态
     */
    @ApiModelProperty(value = "发送状态")
    private Integer status;

    /**
     * 是否已删除
     */
    @TableLogic
    @ApiModelProperty(value = "是否已删除")
    private Integer isDeleted;
}

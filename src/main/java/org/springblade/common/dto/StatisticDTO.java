package org.springblade.common.dto;

import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import java.io.Serializable;

/**
 * {"title":"待处理询价请购单行数","subtitle":"实时","count":908,"allcount":10222,"text":"累计处理询价请购单总行数","color":"rgb(178, 159,
 * 255)","key":"PR"}
 * @author Will
 */
@Data
public
class StatisticDTO implements Serializable{

    private static final long serialVersionUID = 1L;

    @ApiModelProperty("key")
    private String key;

    @ApiModelProperty("标题")
    private String title;

    @ApiModelProperty("副标题")
    private String subtitle;

    @ApiModelProperty("text")
    private String text;

    @ApiModelProperty("数量")
    private int count;

    @ApiModelProperty("数量")
    private int allcount;


}

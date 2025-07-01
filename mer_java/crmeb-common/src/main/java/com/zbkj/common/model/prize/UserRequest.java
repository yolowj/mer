package com.zbkj.common.model.prize;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.experimental.Accessors;

import java.io.Serializable;

@Data
@EqualsAndHashCode(callSuper = false)
@Accessors(chain = true)
@ApiModel(value = "UserRequest", description = "用户公共搜索请求对象")
public class UserRequest implements Serializable {


    @ApiModelProperty(value = "用户ID")
    private Integer userId;

}

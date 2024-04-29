package org.example.common.domain;

import org.example.common.constant.Constant;
import lombok.Data;

/**
 * @desc:
 * @author: Ryan
 * @date: 2020/7/16 15:08
 **/
@Data
public class Result<T> {
    protected String code;
    protected String message;
    protected T data;

    public void setCode(String code) {
        this.code = code;
        if (Constant.SUCCESS.equals(code)) {
            this.message = "成功";
        } else {
            this.message = "请求失败";
        }
    }
}

package com.example.spms.exception;

import com.example.spms.enums.ResultCode;
import lombok.Getter;

/**
 * 自定义业务异常
 *
 * @Author SPMS
 * @Date 2026/07/03
 */
@Getter
public class CustomException extends RuntimeException {

    private final int code;

    public CustomException(ResultCode resultCode) {
        super(resultCode.getMessage());
        this.code = resultCode.getCode();
    }

    public CustomException(int code, String message) {
        super(message);
        this.code = code;
    }

    public CustomException(ResultCode resultCode, String message) {
        super(message);
        this.code = resultCode.getCode();
    }
}

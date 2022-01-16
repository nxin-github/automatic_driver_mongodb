package com.liang.internal;

/**
 * @author ：宁鑫
 * @date ：2022/1/14 9:39
 * @description：
 */
public class AutomaticDriveException extends RuntimeException {
    public AutomaticDriveException(String message, Throwable cause) {
        super(message, cause);
    }

    public AutomaticDriveException(Throwable cause) {
        super(cause);
    }

    public AutomaticDriveException(String message) {
        super(message);
    }

    public AutomaticDriveException() {
    }
}

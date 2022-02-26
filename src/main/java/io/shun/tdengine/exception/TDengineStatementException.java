package io.shun.tdengine.exception;

/**
 * @ClassName: DataSourceException
 * @Description:
 * @Author: ShunZ
 * @CreatedAt: 2021-12-01 11:35
 * @Version: 1.0
 **/

public class TDengineStatementException extends RuntimeException {
    private static final long serialVersionUID = -8609384473766534468L;

    public TDengineStatementException() {
    }

    public TDengineStatementException(String message) {
        super(message);
    }

    public TDengineStatementException(String message, Throwable cause) {
        super(message, cause);
    }

    public TDengineStatementException(Throwable cause) {
        super(cause);
    }
}

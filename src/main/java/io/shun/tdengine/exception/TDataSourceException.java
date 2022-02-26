package io.shun.tdengine.exception;

/**
 * @ClassName: DataSourceException
 * @Description:
 * @Author: ShunZ
 * @CreatedAt: 2021-12-01 11:35
 * @Version: 1.0
 **/

public class TDataSourceException extends RuntimeException {
    private static final long serialVersionUID = -8609384473766534468L;

    public TDataSourceException() {
    }

    public TDataSourceException(String message) {
        super(message);
    }

    public TDataSourceException(String message, Throwable cause) {
        super(message, cause);
    }

    public TDataSourceException(Throwable cause) {
        super(cause);
    }
}

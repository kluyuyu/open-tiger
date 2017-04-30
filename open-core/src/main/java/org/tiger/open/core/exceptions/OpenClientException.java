package org.tiger.open.core.exceptions;

import org.tiger.open.core.defaults.OpenCode;

/**
 * Created by fish on 17/3/5.
 */
public class OpenClientException extends OpenException {

    public OpenClientException() {
        super(OpenCode.CODE_400, "Bad Request");
    }

    public OpenClientException(String reason) {
        super(OpenCode.CODE_400, reason);
    }

    public OpenClientException(int status, String reason) {
        super(status, reason);
    }

    public OpenClientException(int status, String reason, Object... args) {
        super(status, reason, args);
    }
}

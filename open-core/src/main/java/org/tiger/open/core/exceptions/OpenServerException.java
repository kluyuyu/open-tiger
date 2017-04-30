package org.tiger.open.core.exceptions;

import org.tiger.open.core.defaults.OpenCode;

/**
 * Created by fish on 17/3/5.
 */
public class OpenServerException extends OpenException{

    public OpenServerException() {
        this(OpenCode.CODE_500, "server.internal.error");
    }

    public OpenServerException(String reason) {
        super(OpenCode.CODE_500, reason);
    }

    public OpenServerException(int status, String reason) {
        super(status, reason);
    }

    public OpenServerException(int status, String reason, Object... args) {
        super(status, reason, args);
    }
}

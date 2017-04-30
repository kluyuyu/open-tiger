package org.tiger.open.core.exceptions;

import org.tiger.open.core.defaults.OpenCode;

/**
 * Created by liufish on 17/3/5.
 */
public class OpenException extends RuntimeException {

    protected int status = OpenCode.CODE_400;
    protected String reason = "Bad Request";
    protected Object[] args;

    public OpenException(String reason) {
        super(reason);
        this.reason = reason;
    }

    public OpenException(int status, String reason) {
        super(reason);
        this.status = status;
        this.reason = reason;
    }

    public OpenException(int status, String reason, Object... args) {
        super(reason);
        this.status = status;
        this.reason = reason;
        this.args = args;
    }

    public int getStatus() {
        return this.status;
    }

    public Object[] getArgs() {
        return this.args;
    }

    public String getReason() {
        return this.reason;
    }
}

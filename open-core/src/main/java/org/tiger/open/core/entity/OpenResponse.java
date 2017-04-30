package org.tiger.open.core.entity;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.google.common.base.MoreObjects;
import org.tiger.open.core.defaults.OpenCode;

import java.io.Serializable;

/**
 * Created by fish on 17/3/5.
 */
@JsonInclude(JsonInclude.Include.NON_NULL)
public class OpenResponse<T> implements Serializable {

    private int code;

    private T result;

    private String error;

    private String errorMessage;

    public void setResult(T result) {
        this.code = OpenCode.CODE_SUCCESS;
        this.result = result;
    }

    public void setError(String error) {
        this.code = OpenCode.CODE_FAIL;
        this.error = error;
    }

    private void setError(String error, String errorMessage) {
        this.code = OpenCode.CODE_FAIL;
        this.error = error;
        this.errorMessage = errorMessage;
    }

    public static <T> OpenResponse<T> ok(T data) {
        OpenResponse resp = new OpenResponse();
        resp.setResult(data);
        return resp;
    }

    public static <T> OpenResponse<T> ok() {
        return ok(null);
    }

    public static <T> OpenResponse<T> fail(String error) {
        OpenResponse resp = new OpenResponse();
        resp.setError(error);
        return resp;
    }

    public static <T> OpenResponse<T> fail(String error, String errorMessage) {
        OpenResponse resp = new OpenResponse();
        resp.setError(error, errorMessage);
        return resp;
    }

    public String toString() {
        return MoreObjects.toStringHelper(this).add("code", this.code).add("result", this.result).add("error", this.error).add("errorMessage", this.errorMessage).omitNullValues().toString();
    }

    public int getCode() {
        return code;
    }

    public void setCode(int code) {
        this.code = code;
    }

    public void setErrorMessage(String errorMessage) {
        this.errorMessage = errorMessage;
    }

    public T getResult() {
        return this.result;
    }

    public String getError() {
        return this.error;
    }

    public String getErrorMessage() {
        return this.errorMessage;
    }
}

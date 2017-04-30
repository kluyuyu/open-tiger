package org.tiger.open.core;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.MessageSource;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.tiger.open.core.defaults.Output;
import org.tiger.open.core.entity.OpenResponse;
import org.tiger.open.core.exceptions.OpenException;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.util.Locale;

/**
 * 错误拦截器
 * Created by fish on 17/3/5.
 */
@ControllerAdvice
public class RouterExceptionHandler {

    @Autowired
    private ObjectMapper objectMapper;

    @Autowired
    private MessageSource messageSource;

    @ExceptionHandler({OpenException.class})
    public void onOpenException(OpenException ex, HttpServletRequest request, HttpServletResponse response){
        response.setStatus(ex.getStatus());
        OpenResponse result = OpenResponse.fail(ex.getReason(),getErrorMessage(ex));
        Output.write(objectMapper,result,response);
    }
    private String getErrorMessage(OpenException ex) {
        String code = ex.getReason();
        return this.messageSource == null?code:this.messageSource.getMessage(code, ex.getArgs(), code, Locale.SIMPLIFIED_CHINESE);
    }
}

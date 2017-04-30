package org.tiger.open.core.defaults;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.tiger.open.core.exceptions.OpenClientException;

import javax.validation.ConstraintViolation;
import javax.validation.Validation;
import javax.validation.executable.ExecutableValidator;
import java.lang.reflect.Method;
import java.util.Set;

/**
 * Created by fish on 17/3/5.
 */
public class InvokeValidator {

    private static final Logger log = LoggerFactory.getLogger(InvokeValidator.class);
    private static final InvokeValidator instance = new InvokeValidator();
    private ExecutableValidator validator = Validation.buildDefaultValidatorFactory().getValidator().forExecutables();

    public static InvokeValidator instance() {
        return instance;
    }

    private InvokeValidator() {
    }

    public void validateParams(Object target, Method method, Object[] args) {
        Set violations = this.validator.validateParameters(target, method, args, new Class[0]);
        if(violations.size() > 0) {
            log.error("failed to validate service({})\'s method({})\'s params: {}", new Object[]{target, method, violations});
            throw new OpenClientException(((ConstraintViolation)violations.iterator().next()).getMessage());
        }
    }
}

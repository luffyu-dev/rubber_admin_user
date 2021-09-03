package com.rubber.admin.security.auth.exception;

import com.rubber.admin.core.exceptions.AdminRunTimeException;
import com.rubber.common.utils.result.code.ICodeHandle;

/**
 * @author luffyu
 * Created on 2019-10-22
 */
public class TokenVerifyException extends AdminRunTimeException {


    public TokenVerifyException(ICodeHandle handle) {
        super(handle);
    }

    public TokenVerifyException(ICodeHandle handle, String msg, Object... arguments) {
        super(handle, msg, arguments);
    }
}

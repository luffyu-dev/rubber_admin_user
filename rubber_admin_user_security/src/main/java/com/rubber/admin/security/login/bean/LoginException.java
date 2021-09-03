package com.rubber.admin.security.login.bean;

import com.rubber.admin.core.exceptions.AdminRunTimeException;
import com.rubber.common.utils.result.IResultHandle;
import com.rubber.common.utils.result.code.ICodeHandle;

/**
 * @author luffyu
 * Created on 2019-10-23
 */
public class LoginException extends AdminRunTimeException {

    public LoginException(IResultHandle handle) {
        super(handle);
    }

    public LoginException(ICodeHandle handle) {
        super(handle);
    }


    public LoginException(ICodeHandle handle, String msg, Object... arguments) {
        super(handle, msg, arguments);
    }
}

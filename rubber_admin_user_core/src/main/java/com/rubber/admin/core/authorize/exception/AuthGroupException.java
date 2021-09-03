package com.rubber.admin.core.authorize.exception;

import com.rubber.admin.core.exceptions.AdminException;
import com.rubber.common.utils.result.code.ICodeHandle;

/**
 * <p>
 *     权限相关的异常信息
 * </p>
 *
 * @author luffyu
 * @date 2020-03-14 10:32
 **/
public class AuthGroupException extends AdminException {

    public AuthGroupException(ICodeHandle handle) {
        super(handle);
    }

    public AuthGroupException(ICodeHandle handle, String msg, Object... arguments) {
        super(handle, msg, arguments);
    }
}

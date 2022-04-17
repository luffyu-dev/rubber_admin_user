package com.rubber.admin.core.system.exception;

import com.rubber.admin.core.exceptions.AdminException;
import com.rubber.base.components.util.result.code.ICodeHandle;

/**
 * @author luffyu
 * Created on 2019-11-01
 */
public class DictException extends AdminException {

    public DictException(ICodeHandle handle) {
        super(handle);
    }

    public DictException(ICodeHandle handle, String msg, Object... arguments) {
        super(handle, msg, arguments);
    }
}

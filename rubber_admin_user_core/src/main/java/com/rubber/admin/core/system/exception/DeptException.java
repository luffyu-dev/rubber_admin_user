package com.rubber.admin.core.system.exception;

import com.rubber.admin.core.exceptions.AdminException;
import com.rubber.common.utils.result.code.ICodeHandle;

/**
 * @author luffyu
 * Created on 2019-11-06
 */
public class DeptException extends AdminException {





    public DeptException(ICodeHandle handle) {
        super(handle);
    }

    public DeptException(ICodeHandle handle, String msg, Object... arguments) {
        super(handle, msg, arguments);
    }
}

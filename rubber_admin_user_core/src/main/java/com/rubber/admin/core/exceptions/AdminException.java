package com.rubber.admin.core.exceptions;


import com.rubber.common.utils.result.IResultHandle;
import com.rubber.common.utils.result.code.ICodeHandle;
import com.rubber.common.utils.result.exception.BaseResultException;

/**
 * @author luffyu
 * Created on 2019-11-01
 */
public class AdminException extends BaseResultException {


    public AdminException(String msg) {
        super(msg);
    }

    public AdminException(IResultHandle handle) {
        super(handle);
    }

    public AdminException(String code, String msg, Object data) {
        super(code, msg, data);
    }

    public AdminException(ICodeHandle handle, Object data) {
        super(handle, data);
    }

    public AdminException(ICodeHandle handle) {
        super(handle);
    }

    public AdminException(ICodeHandle handle, String msg, Object... arguments) {
        super(handle, msg, arguments);
    }

}

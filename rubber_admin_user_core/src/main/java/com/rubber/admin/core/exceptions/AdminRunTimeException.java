package com.rubber.admin.core.exceptions;


import com.rubber.common.utils.result.IResultHandle;
import com.rubber.common.utils.result.code.ICodeHandle;
import com.rubber.common.utils.result.exception.BaseResultRunTimeException;

/**
 * @author luffyu
 * Created on 2019-05-13
 */
public class AdminRunTimeException extends BaseResultRunTimeException {


    public AdminRunTimeException(String msg) {
        super(msg);
    }

    public AdminRunTimeException(IResultHandle handle) {
        super(handle);
    }

    public AdminRunTimeException(String code, String msg, Object data) {
        super(code, msg, data);
    }

    public AdminRunTimeException(ICodeHandle handle, Object data) {
        super(handle, data);
    }

    public AdminRunTimeException(ICodeHandle handle) {
        super(handle);
    }

    public AdminRunTimeException(ICodeHandle handle, String msg, Object... arguments) {
        super(handle, msg, arguments);
    }
}

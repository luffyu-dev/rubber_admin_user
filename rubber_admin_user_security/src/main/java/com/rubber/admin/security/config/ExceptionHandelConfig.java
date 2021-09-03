package com.rubber.admin.security.config;

import com.rubber.admin.core.exceptions.AdminException;
import com.rubber.admin.core.exceptions.AdminRunTimeException;
import com.rubber.admin.core.tools.ExceptionUtils;
import com.rubber.common.utils.result.IResultHandle;
import com.rubber.common.utils.result.ResultMsg;
import com.rubber.common.utils.result.exception.IResultException;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.ResponseBody;

/**
 * @author luffyu
 * Created on 2019-10-31
 */
@ControllerAdvice
@Slf4j
public class ExceptionHandelConfig {


    @ExceptionHandler(value = {AdminRunTimeException.class, AdminException.class})
    @ResponseBody
    public ResultMsg handel(Exception e) throws Exception {
        ExceptionUtils.printErrorMsg(e);
        if(e instanceof IResultException){
            IResultException re = (IResultException)e;
            IResultHandle resultHandle = re.getResult();
            if( resultHandle instanceof ResultMsg){
                return (ResultMsg) resultHandle;
            }else {
                return ResultMsg.error();
            }
        }else {
            throw e;
        }
    }

}

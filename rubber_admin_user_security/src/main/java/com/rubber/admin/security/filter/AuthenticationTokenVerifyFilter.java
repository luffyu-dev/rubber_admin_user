package com.rubber.admin.security.filter;

import cn.hutool.extra.servlet.ServletUtil;
import com.alibaba.fastjson.JSON;
import com.rubber.admin.core.enums.AdminCode;
import com.rubber.admin.core.exceptions.AdminRunTimeException;
import com.rubber.admin.core.tools.ServletUtils;
import com.rubber.admin.security.auth.ITokenVerifyService;
import com.rubber.admin.security.config.properties.RubberPropertiesUtils;
import com.rubber.admin.security.login.bean.LoginUserDetail;
import com.rubber.common.utils.result.IResultHandle;
import com.rubber.common.utils.result.ResultMsg;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.web.authentication.WebAuthenticationDetailsSource;
import org.springframework.web.filter.OncePerRequestFilter;

import javax.servlet.FilterChain;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;

/**
 * @author luffyu
 * Created on 2019-10-22
 */
public class AuthenticationTokenVerifyFilter extends OncePerRequestFilter {

    private static AuthenticationTokenVerifyFilter filer = new AuthenticationTokenVerifyFilter();

    private AuthenticationTokenVerifyFilter() {
    }
    /**
     * 返回一个单例的模式
     * @return
     */
    public static AuthenticationTokenVerifyFilter builder(){
        return filer;
    }

    /**
     * 解析token的service方法
     * 注入的哪一个就用哪一个
     */
    private ITokenVerifyService tokenAuth;

    public ITokenVerifyService getTokenAuth() {
        if(tokenAuth == null){
            tokenAuth = RubberPropertiesUtils.getApplicationContext().getBean(ITokenVerifyService.class);
        }
        if(tokenAuth == null){
            throw new RuntimeException("tokenAuth is null");
        }
        return tokenAuth;
    }


    @Override
    protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response, FilterChain filterChain) throws IOException, ServletException {
        try {
            LoginUserDetail loginUserDetail = getTokenAuth().verify(request);
            UsernamePasswordAuthenticationToken authenticationToken = new UsernamePasswordAuthenticationToken(loginUserDetail, null, loginUserDetail.getAuthorities());
            authenticationToken.setDetails(new WebAuthenticationDetailsSource().buildDetails(request));
            SecurityContextHolder.getContext().setAuthentication(authenticationToken);
            //写入用户的登陆id
            ServletUtils.writeUserToHttp(loginUserDetail.getUserId(),request);
        }catch (Exception e){
            unSuccessJwtResult(response,e);
            return;
        }
        filterChain.doFilter(request,response);
    }
    /**
     * 验证没有登陆成功的请求
     * @param response 返回值
     * @param e 异常的信息
     */
    public void unSuccessJwtResult(HttpServletResponse response,Exception e){
        ResultMsg error = null ;
        if(e instanceof AdminRunTimeException){
            IResultHandle result = ((AdminRunTimeException) e).getResult();
            if(result instanceof ResultMsg){
                error = (ResultMsg) result;
            }
        }
        if(error == null){
            error = ResultMsg.create(AdminCode.TOKEN_IS_ERROR,null);
        }
        response.setCharacterEncoding("UTF-8");
        ServletUtil.write(response, JSON.toJSONString(error), "application/json");
    }



    /**
     * 可以重写
     * @param request
     * @return 返回为true时，则不过滤即不会执行doFilterInternal
     * @throws ServletException
     */
    @Override
    protected boolean shouldNotFilter(HttpServletRequest request) throws ServletException {
        return RubberPropertiesUtils.verifyNotLoginFilter(request);
    }

}

package com.jeeplus.modules.sys.security;

import org.apache.shiro.authc.AuthenticationInfo;
import org.apache.shiro.authc.AuthenticationToken;
import org.apache.shiro.authc.credential.SimpleCredentialsMatcher;

/**
 * Created by LJR on 2018/12/29.
 */
public class MyCredentialsMatcher extends SimpleCredentialsMatcher {
    @Override
    public boolean doCredentialsMatch(AuthenticationToken authcToken, AuthenticationInfo info) {
        UsernamePasswordToken token = (UsernamePasswordToken) authcToken;
        String tokenPassword =  String.valueOf(token.getPassword());
        Object accountCredentials = getCredentials(info);
        return equals(tokenPassword,accountCredentials);
    }

}















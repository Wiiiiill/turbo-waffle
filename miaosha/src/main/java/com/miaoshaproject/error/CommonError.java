package com.miaoshaproject.error;

import java.net.CookieHandler;

public interface CommonError {
    public int getErrCode();
    public String getErrMsg();
    public CommonError setErrMsg(String errMsg);
}

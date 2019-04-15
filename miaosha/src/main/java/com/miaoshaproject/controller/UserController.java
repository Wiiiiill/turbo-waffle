package com.miaoshaproject.controller;

import com.alibaba.druid.util.StringUtils;
import com.miaoshaproject.controller.viewobject.UserVO;
import com.miaoshaproject.error.BusinessException;
import com.miaoshaproject.error.EmBusinessError;
import com.miaoshaproject.response.CommonReturnType;
import com.miaoshaproject.service.UserService;
import com.miaoshaproject.service.model.UserModel;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.*;

import javax.servlet.http.HttpServletRequest;
import java.security.MessageDigest;
import java.util.Base64;
import java.util.Random;


@Controller("user")
@RequestMapping("/user")
@CrossOrigin(allowCredentials = "true",allowedHeaders = "*")
public class UserController extends BaseController{

    @Autowired
    private UserService userService;
    @Autowired
    //通过springbean包装的request 本质是一个proxy,内部拥有threadlocal方式的map,使得用户在每个线程当中处理自己的request
    private HttpServletRequest request;

    //用户登录接口
    @RequestMapping(value = "/login",method = {RequestMethod.POST},consumes = {CONTENT_TYPE_FORMED})
    @ResponseBody
    public CommonReturnType login(@RequestParam(name="telphone")String telphone,
                                  @RequestParam(name = "password")String password) throws Exception {
        //入参校验
        if(StringUtils.isEmpty(telphone)
                || StringUtils.isEmpty(password)){
            throw new BusinessException(EmBusinessError.PARAMTER_VALIDATION_ERROR);
        }
        //用户登录服务,用来校验登录是否合法
        UserModel userModel = userService.validateLogin(telphone,this.EncodedByMd5(password));
        //将登录凭证加入到用户登录成功的session中
        this.request.getSession().setAttribute("IS_LOGIN",true);
         this.request.getSession().setAttribute("LOGIN_USER",userModel);

         return CommonReturnType.create(null);
    }

    //用户注册接口
//    @RequestMapping(value = "/register",method = {RequestMethod.POST},consumes = {CONTENT_TYPE_FORMED})
//    @ResponseBody
//    public CommonReturnType register(@RequestParam(name="telphone")String telphone,
//                                     @RequestParam(name="otpCode")String otpCode,
//                                     @RequestParam(name="name")String name,
//                                     @RequestParam(name="gender")Integer gender,
//                                     @RequestParam(name="age")Integer age,
//                                     @RequestParam(name="password") String password) throws BusinessException {
//
//        //验证手机号和对应的otpcode相符合
//        String inSessionOtpCode = (String)this.request.getSession().getAttribute(telphone);
//        if(!StringUtils.equals(otpCode,inSessionOtpCode)){
//            throw new BusinessException(EmBusinessError.PARAMTER_VALIDATION_ERROR,"短信验证码不符合");
//        }
//        //用户注册的流程
//        UserModel userModel = new UserModel();
//        userModel.setEncrptPassword(MD5Encoder.encode(password.getBytes()));
//        userModel.setName(name);
//        userModel.setAge(age);
//        userModel.setGender(new Byte(gender.byteValue()));
//        userModel.setTelphone(telphone);
//        userModel.setRegisterMode("byphone");
//        userService.register(userModel);
//        return CommonReturnType.create(null);
//    }

    public String EncodedByMd5(String str) throws Exception{
        //确定计算方法
        MessageDigest md5 = MessageDigest.getInstance("MD5");
        Base64.Encoder encoder = Base64.getEncoder();
        //加密字符串
        String newStr = encoder.encodeToString(md5.digest(str.getBytes("utf-8")));
        return newStr;
    }

    @RequestMapping(value = "/register",method = {RequestMethod.POST},consumes = {CONTENT_TYPE_FORMED})
    @ResponseBody
    public CommonReturnType register(UserModel userModel,
                                     @RequestParam(name="otpCode")String otpCode,
                                     @RequestParam(name="password") String password) throws Exception {

        //验证手机号和对应的otpcode相符合
        String inSessionOtpCode = (String)this.request.getSession().getAttribute(userModel.getTelphone());
        if(!StringUtils.equals(otpCode,inSessionOtpCode)){
            throw new BusinessException(EmBusinessError.PARAMTER_VALIDATION_ERROR,"短信验证码不符合");
        }
        //用户注册的流程
        userModel.setEncrptPassword(EncodedByMd5(password));
        userModel.setRegisterMode("byphone");
        System.out.println(userModel);
        userService.register(userModel);
        return CommonReturnType.create(null);
    }

    //用户获取top短信接口
    @RequestMapping(value = "/getotp",method = {RequestMethod.POST},consumes = {CONTENT_TYPE_FORMED})
    @ResponseBody
    public CommonReturnType getOtp(@RequestParam("telphone") String telphone){
        //需要按照一定规则生成OTP验证码
        Random random = new Random();
        int randomInt = random.nextInt(9999);
        randomInt+=10000;
        String otpCode = String.valueOf(randomInt);

        //将OTP从对应用户的手机号关联(分布式的话使用redis)，使用httpsession绑定手机号与TOPCODE
        request.getSession().setAttribute(telphone,otpCode);

        //将OTP验证码通过都寒心通道发送给用户，省略
        System.out.println("telphone:"+telphone+"& OTPCODE:"+request.getSession().getAttribute(telphone)    );

        return CommonReturnType.create(null);
    }

    @RequestMapping("/get")
    @ResponseBody
    public CommonReturnType getUser(@RequestParam(name="id") Integer id) throws BusinessException{
        //调用service服务获取对应id的用户对象并返回给前端
        UserModel userModel = userService.getUserById(id);

        //若获取的对应用户信息不存在
        if(userModel == null){
//            userModel.setEncrptPassword("123");
            throw new BusinessException(EmBusinessError.USER_NOT_EXIST);
        }

        //将核心领域模型用户对象转化为可供UI使用的viewobject
        UserVO userVO = convertFromModel(userModel);
        //返回通用对象
        return  CommonReturnType.create(userVO);
    }

    private UserVO convertFromModel(UserModel userModel){
        if(userModel == null){
            return null;
        }
        UserVO userVO = new UserVO();
        BeanUtils.copyProperties(userModel,userVO);
        return userVO;
    }

}

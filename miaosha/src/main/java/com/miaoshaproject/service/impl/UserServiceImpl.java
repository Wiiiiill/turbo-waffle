package com.miaoshaproject.service.impl;

import com.miaoshaproject.dao.UserDOMapper;
import com.miaoshaproject.dao.UserPasswordDoMapper;
import com.miaoshaproject.dataobject.UserDO;
import com.miaoshaproject.dataobject.UserPasswordDo;
import com.miaoshaproject.error.BusinessException;
import com.miaoshaproject.error.EmBusinessError;
import com.miaoshaproject.service.UserService;
import com.miaoshaproject.service.model.UserModel;
import com.miaoshaproject.validator.ValidationResult;
import com.miaoshaproject.validator.ValidatorImpl;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.dao.DuplicateKeyException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
public class UserServiceImpl implements UserService {

    @Autowired
    private UserDOMapper userDOMapper;
    @Autowired
    private UserPasswordDoMapper userPasswordDoMapper;

    @Autowired
    private ValidatorImpl validator;

    @Override
    public UserModel getUserById(Integer id) {
        //调用userdomapper获取到对应的用户dataobject
        UserDO userDO = userDOMapper.selectByPrimaryKey(id);
        if(userDO == null){
            return null;
        }
        //通过用户id获取用户的加密密码信息
        UserPasswordDo userPasswordDo = userPasswordDoMapper.selectByUserId(userDO.getId());
        return convertFromDataObject(userDO,userPasswordDo);
    }

    @Override
    @Transactional
    public void register(UserModel userModel) throws BusinessException {
        if(userModel == null){
            throw new BusinessException(EmBusinessError.PARAMTER_VALIDATION_ERROR);
        }
//        if(StringUtils.isEmpty(userModel.getName())
//                || userModel.getGender() == null
//                || userModel.getAge() == null
//                || StringUtils.isEmpty(userModel.getTelphone())
//                ){
//            throw new BusinessException(EmBusinessError.PARAMTER_VALIDATION_ERROR);
//        }

        ValidationResult result = validator.validate(userModel);
        if(result.isHasErrors()){
            throw new BusinessException(EmBusinessError.PARAMTER_VALIDATION_ERROR,result.getErrMsg());
        }

        //实现model->dataobject
        UserDO userDO = convertFromUserModel(userModel);
        System.out.println(userDO);
        try{
            userDOMapper.insertSelective(userDO);
        }catch (DuplicateKeyException e){
            throw  new BusinessException(EmBusinessError.PARAMTER_VALIDATION_ERROR,"手机号已注册");
        }
        userModel.setId(userDO.getId());
        UserPasswordDo userPasswordDo = convertPasswordFromUserModel(userModel);
        System.out.println(userPasswordDo);
        userPasswordDoMapper.insertSelective(userPasswordDo);

    }

    @Override
    public UserModel validateLogin(String telphone, String encriptPassword) throws BusinessException {

        //用户手机获取用户信息,
        UserDO userDO = userDOMapper.selectByTelphone(telphone);
        if(userDO == null){
            System.out.println("user not exit");
            throw new BusinessException(EmBusinessError.USER_LOGIN_FAIL);
        }
        UserPasswordDo userPasswordDo= userPasswordDoMapper.selectByUserId(userDO.getId());
        UserModel userModel = convertFromDataObject(userDO,userPasswordDo);
        //比对用户信息内加密的密码是否和传输的相匹配
        if(!StringUtils.equals(encriptPassword,userModel.getEncrptPassword())){
            throw new BusinessException(EmBusinessError.USER_LOGIN_FAIL);
        }

        return userModel;

    }

    private UserPasswordDo convertPasswordFromUserModel(UserModel userModel){
        if(userModel == null ){
            return null;
        }
        UserPasswordDo userPasswordDo = new UserPasswordDo();
        BeanUtils.copyProperties(userModel,userPasswordDo);
        userPasswordDo.setUserId(userModel.getId());
        return userPasswordDo;
    }
    private UserDO convertFromUserModel(UserModel userModel){
        if(userModel == null){
            return null;
        }
        UserDO userDO = new UserDO();
        BeanUtils.copyProperties(userModel,userDO);
        return userDO;
    }

    private UserModel convertFromDataObject(UserDO userDo, UserPasswordDo userPasswordDo){
        if(userDo == null){
            return null;
        }
        UserModel userModel = new UserModel();
        BeanUtils.copyProperties(userDo,userModel);
        if(userPasswordDo != null){
            userModel.setEncrptPassword(userPasswordDo.getEncrptPassword());
        }
        return userModel;
    }
}

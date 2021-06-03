package com.wly.app.Service;

import com.wly.app.entity.UserDO;
import com.wly.app.entity.UserRegisterParam;
import com.wly.app.model.JsonData;


public interface UserService {

    int save(UserDO user);

    UserDO findByUsername(String username);

    JsonData register(UserRegisterParam param);
}

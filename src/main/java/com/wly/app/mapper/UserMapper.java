package com.wly.app.mapper;

import com.wly.app.entity.UserDO;
import org.apache.ibatis.annotations.Mapper;

@Mapper
public interface UserMapper {

    int insert(UserDO userDO);

    UserDO selectByUsername(String username);

}

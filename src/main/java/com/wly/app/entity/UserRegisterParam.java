package com.wly.app.entity;

public class UserRegisterParam extends UserDO{

    private String code;

    public String getCode() {
        return code;
    }

    public void setCode(String code) {
        this.code = code;
    }

    public UserDO convert(){
        UserDO userDO = new UserDO();
        userDO.setPassword(this.getPassword());
        userDO.setUserRole(this.getUserRole());
        userDO.setUsername(this.getUsername());
        return userDO;
    }
}

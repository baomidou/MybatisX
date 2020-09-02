package com.baomidou.plugin.idea.mybatisx.model;

/**
 * 保存数据库连接对应的用户名，密码存在keepass库中
 * Created by kangtian on 2018/8/3.
 */
public class User {

    //用户名
    private String username;


    public User() {
    }

    public User(String username) {
        this.username = username;

    }



    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        this.username = username;
    }


}

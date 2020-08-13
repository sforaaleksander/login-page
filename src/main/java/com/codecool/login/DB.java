package com.codecool.login;

import java.util.ArrayList;
import java.util.List;

public class DB {
    private final List<User> userList;

    public DB() {
        this.userList = new ArrayList<>();
        userList.add(new User("bobke", "bobek"));
        userList.add(new User("szot", "szot"));
    }

    public List<User> getUserList() {
        return userList;
    }
}

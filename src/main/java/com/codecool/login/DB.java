package com.codecool.login;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class DB {
    private final List<User> userList;
    private final Map<Integer, User> sessionUserMap;

    public DB() {
        this.userList = new ArrayList<>();
        this.sessionUserMap = new HashMap<>();
        userList.add(new User("bobke", "bobek"));
        userList.add(new User("szot", "szot"));
    }

    public Map<Integer, User> getSessionUserMap() {
        return sessionUserMap;
    }

    public List<User> getUserList() {
        return userList;
    }
}

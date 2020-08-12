package com.codecool.login;

import java.util.ArrayList;
import java.util.List;

public class PseudoDB {
    private List<User> userList;

    public PseudoDB() {
        this.userList = new ArrayList<>();
        userList.add(new User("bobke", "bobek"));
        userList.add(new User("szot", "szot"));
    }
}

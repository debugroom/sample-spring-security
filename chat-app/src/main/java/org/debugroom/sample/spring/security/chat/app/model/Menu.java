package org.debugroom.sample.spring.security.chat.app.model;

import lombok.Data;

public enum Menu {
    LOGOUT("ログアウト", "/logout"),
    PORTAL("ポータル", "/portal"),
    USER_MANAGEMENT("ユーザ管理", "/user-management"),
    CHAT("チャット", "/chat");

    private String name;
    private String path;

    private Menu(String name, String path){
        this.name = name;
        this.path = path;
    }

    public String getName() {
        return name;
    }

    public String getPath() {
        return path;
    }

}

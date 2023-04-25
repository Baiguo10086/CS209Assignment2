package cn.edu.sustech.cs209.chatting.common;

import java.io.Serializable;

public class User implements Serializable {

    String name;
    Status status;

    public User() {
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public Status getStatus() {
        return status;
    }

    public void setStatus(Status status) {
        this.status = status;
    }
}

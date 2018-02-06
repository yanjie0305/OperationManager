package com.example.edaibu.operationmanager.bean;

/**
 * Created by ${gyj} on 2018/1/8.
 */

public class InfoBean {
    private String name;
    private String roleId;

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getRoleId() {
        return roleId;
    }

    public void setRoleId(String roleId) {
        this.roleId = roleId;
    }

    public InfoBean(String name, String roleId) {
        this.name = name;
        this.roleId = roleId;
    }
}

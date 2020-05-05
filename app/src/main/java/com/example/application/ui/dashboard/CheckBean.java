package com.example.application.ui.dashboard;

import com.contrarywind.interfaces.IPickerViewData;

import java.util.List;

public class CheckBean implements IPickerViewData {
    private String groupName;
    private List<String> userList;

    public void setGroupName(String groupName) {
        this.groupName = groupName;
    }

    public String getGroupName() {
        return groupName;
    }

    public void setUserList(List<String> userList) {
        this.userList = userList;
    }

    public List<String> getUserList() {
        return userList;
    }

    @Override
    public String getPickerViewText() {
        return groupName;
    }
}

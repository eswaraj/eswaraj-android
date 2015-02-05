package com.next.eswaraj.models;


public class BottomMenuBarItem {
    private int icon;
    private String text;
    private Class targetActivity;

    public BottomMenuBarItem(int icon, String text, Class targetActivity) {
        this.icon = icon;
        this.text = text;
        this.targetActivity = targetActivity;
    }

    public int getIcon() {
        return this.icon;
    }

    public String getText() {
        return this.text;
    }

    public Class getTargetActivity() { return this.targetActivity; }
}

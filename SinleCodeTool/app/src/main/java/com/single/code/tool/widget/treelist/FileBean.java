package com.single.code.tool.widget.treelist;

public class FileBean {
    @TreeNodeId
    private String _id;
    @TreeNodePid
    private String parentId;
    @TreeNodeLabel
    private String name;
    @TreeNodeNumber
    private String number;
    @TreeNodeType
    private int genre;
    @TreeNodeOrgName
    private String orgName;

    private long length;
    private String desc;

    public FileBean(String _id, String parentId, String name, String number, int type, String orgName) {
        this._id = _id;
        this.parentId = parentId;
        this.name = name;
        this.number = number;
        this.genre = type;
        this.orgName = orgName;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getNumber() {
        return number;
    }

    public void setNumber(String number) {
        this.number = number;
    }

    public int getType() {
        return genre;
    }

    public void setType(int type) {
        this.genre = type;
    }

    public String getOrgName() {
        return orgName;
    }

    public void setOrgName(String orgName) {
        this.orgName = orgName;
    }
}

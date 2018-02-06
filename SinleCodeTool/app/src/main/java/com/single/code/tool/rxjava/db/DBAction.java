package com.single.code.tool.rxjava.db;

/**
 * Created by Administrator on 2018/2/5.
 */
public class DBAction {
    private String table;
    private Object value;
    private String command;
    private DBCmd cmd;
    public static enum  DBCmd{
        INSERT_CMD,
        INSERT_LIST_CMD,
        UPDATE_CMD,
        DELETE_CMD,
        QUERY_CMD;
    }
    public DBAction(){

    }

    public String getTable() {
        return table;
    }

    public void setTable(String table) {
        this.table = table;
    }

    public Object getValue() {
        return value;
    }

    public void setValue(Object value) {
        this.value = value;
    }

    public String getCommand() {
        return command;
    }

    public void setCommand(String command) {
        this.command = command;
    }

    public DBCmd getCmd() {
        return cmd;
    }

    public void setCmd(DBCmd cmd) {
        this.cmd = cmd;
    }

    public DBAction(String table,Object value,String command,DBCmd dbCmd){
        this.table = table;
        this.value = value;
        this.command = command;
        this.cmd = dbCmd;
    }
    @Override
    public String toString() {
        return "DBAction{" +
                "table='" + table + '\'' +
                ", value=" + value +
                ", command='" + command +
                '}';
    }
}
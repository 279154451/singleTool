package com.single.code.tool.policy;

/**
 * Created by Administrator on 2017/12/4.
 */
public class PolicyTable {
    public final static String BEACON_ID_TABLE ="beacon_id";
    public final static String BeaconIdSql ="create table if not exists "+BEACON_ID_TABLE+"(id INTEGER PRIMARY KEY,beaconId TEXT,devType INTEGER,policyId INTEGER,policyVer INTEGER)";
}
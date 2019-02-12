package com.single.code.tool.DesignPatterns.proxy;

import android.util.Log;

/**
 * 真实代理，处理具体业务
 * Created by czf on 2019/2/1.
 */

public class RealProxy implements DbProxy {
    @Override
    public void insert(Object object) {
        Log.d("DbProxy","insert");
    }

    @Override
    public void delete(Object object) {
        Log.d("DbProxy","delete");

    }

    @Override
    public void update(Object object) {
        Log.d("DbProxy","update");

    }

    @Override
    public Object query(String selet) {
        Log.d("DbProxy","query");
        return null;

    }
}

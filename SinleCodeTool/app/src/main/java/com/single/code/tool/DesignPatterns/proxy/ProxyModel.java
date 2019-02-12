package com.single.code.tool.DesignPatterns.proxy;

/**
 * 持有一个真实的代理实例
 * Created by czf on 2019/2/1.
 */

public class ProxyModel implements DbProxy{
    private DbProxy proxy;
    public ProxyModel(DbProxy proxy){
        this.proxy = proxy;
    }

    @Override
    public void insert(Object object) {
        proxy.insert(object);
    }

    @Override
    public void delete(Object object) {
        proxy.delete(object);
    }

    @Override
    public void update(Object object) {
        proxy.update(object);
    }

    @Override
    public Object query(String selet) {

        return proxy.query(selet);
    }
}

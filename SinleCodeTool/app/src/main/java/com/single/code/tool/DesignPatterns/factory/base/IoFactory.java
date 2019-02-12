package com.single.code.tool.DesignPatterns.factory.base;

/**
 * Created by czf on 2019/1/29.
 */

public class IoFactory extends Factory {
    @Override
    public <T extends IoProduct> T createProduct(Class<T> clz) {
        IoProduct ioProduct = null;
        try {
            ioProduct = (IoProduct) Class.forName(clz.getName()).newInstance();
        }catch (Exception e){

        }
        return (T) ioProduct;
    }
}

package com.single.code.tool.factory.test;

import com.single.code.tool.factory.DbIoProduct;
import com.single.code.tool.factory.XmlIoProduct;
import com.single.code.tool.factory.base.Factory;
import com.single.code.tool.factory.FileIoProduct;
import com.single.code.tool.factory.base.IoFactory;
import com.single.code.tool.factory.base.IoProduct;
import com.single.code.tool.factory.entity.DbIoEntity;
import com.single.code.tool.factory.entity.FileIoEntity;
import com.single.code.tool.factory.entity.XmlIoEntity;

/**
 * Created by czf on 2019/1/29.
 */

public class ApiTest {
    public void TestFileIO(){
        Factory factory = new IoFactory();
        IoProduct ioProduct = factory.createProduct(FileIoProduct.class);
        if(ioProduct !=null){
            FileIoEntity entity = new FileIoEntity();
            ioProduct.add(entity);
        }
    }
    public void TestDBIO(){
        Factory factory = new IoFactory();
        IoProduct ioProduct = factory.createProduct(DbIoProduct.class);
        if(ioProduct !=null){
            DbIoEntity entity = new DbIoEntity();
            ioProduct.add(entity);
        }
    }
    public void TestXmlIO(){
        Factory factory = new IoFactory();
        IoProduct ioProduct = factory.createProduct(XmlIoProduct.class);
        if(ioProduct !=null){
            XmlIoEntity entity = new XmlIoEntity();
            ioProduct.add(entity);
        }
    }
}

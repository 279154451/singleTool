package com.single.code.tool.DesignPatterns.factory;

import com.single.code.tool.DesignPatterns.factory.base.IoProduct;
import com.single.code.tool.DesignPatterns.factory.entity.XmlIoEntity;

/**
 * Created by czf on 2019/1/29.
 */

public class XmlIoProduct implements IoProduct<XmlIoEntity> {
    @Override
    public void add(XmlIoEntity data) {

    }

    @Override
    public void remove(XmlIoEntity data) {

    }

    @Override
    public XmlIoEntity query(XmlIoEntity data) {

        return null;
    }

    @Override
    public void update(XmlIoEntity data) {

    }
}

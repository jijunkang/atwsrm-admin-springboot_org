package org.springblade.modules.po.util;

import junit.framework.TestCase;
import org.springblade.modules.po.mapper.IoMapper;
import org.springframework.beans.factory.annotation.Autowired;

public class MudownloadPoTest extends TestCase {

    @Autowired
    IoMapper ioMapper;
    public void testTest1() {
        Integer count= ioMapper.poItemIsExisted(1060L);
        System.out.println(count);
    }
}

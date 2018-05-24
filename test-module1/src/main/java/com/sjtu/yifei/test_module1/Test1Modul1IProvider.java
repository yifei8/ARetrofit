package com.sjtu.yifei.test_module1;

import com.sjtu.yifei.annotation.Route;
import com.sjtu.yifei.route.IProvider;

/**
 * 类描述：
 * 创建人：yifei
 * 创建时间：2018/5/24
 * 修改人：
 * 修改时间：
 * 修改备注：
 */

@Route(path = "/provider/Test1Modul1IProvider")
public class Test1Modul1IProvider implements IProvider {
    private String para1;
    private int para2;

    public Test1Modul1IProvider(String para1, int para2) {
        this.para1 = para1;
        this.para2 = para2;
    }

    @Override
    public String login() {
        return "Test1Modul1IProvider para1:" + para1 + ",para2:" + para2;
    }
}

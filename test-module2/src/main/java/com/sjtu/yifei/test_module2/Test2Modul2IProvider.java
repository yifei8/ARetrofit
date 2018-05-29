package com.sjtu.yifei.test_module2;

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

@Route(path = "/provider/Test2Modul2IProvider")
public class Test2Modul2IProvider implements IProvider {

    private String para1;
    private String para2;

    public Test2Modul2IProvider(String para1, String para2) {
        this.para1 = para1;
        this.para2 = para2;
    }

    @Override
    public String login() {
        return "Test2Modul2IProvider para1:" + para1 + ",para2:" + para2;
    }
}

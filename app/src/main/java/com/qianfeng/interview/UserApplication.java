package com.qianfeng.interview;

import android.app.Application;

/**
 * Created by Liu Jianping
 *
 * @date : 16/7/11.
 */
public class UserApplication extends Application {
    @Override
    public void onCreate() {
        super.onCreate();

        UserHelper.init(this);
    }
}

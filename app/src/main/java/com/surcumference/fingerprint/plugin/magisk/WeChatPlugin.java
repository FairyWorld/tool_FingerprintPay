package com.surcumference.fingerprint.plugin.magisk;

import android.app.Activity;
import android.app.Application;
import android.support.annotation.Keep;

import com.surcumference.fingerprint.BuildConfig;
import com.surcumference.fingerprint.network.updateCheck.UpdateFactory;
import com.surcumference.fingerprint.plugin.WeChatBasePlugin;
import com.surcumference.fingerprint.util.ActivityLifecycleCallbacks;
import com.surcumference.fingerprint.util.ApplicationUtils;
import com.surcumference.fingerprint.util.Task;
import com.surcumference.fingerprint.util.Umeng;
import com.surcumference.fingerprint.util.log.L;


/**
 * Created by Jason on 2017/9/8.
 */
public class WeChatPlugin extends WeChatBasePlugin {

    @Keep
    public static void main(String appDataDir) {
        L.d("Xposed plugin init version: " + BuildConfig.VERSION_NAME);
        Task.onApplicationReady(WeChatPlugin::init);
    }

    public static void init() {
        Application application = ApplicationUtils.getApplication();
        WeChatPlugin plugin = new WeChatPlugin();

        Task.onMain(1000, ()-> Umeng.init(application));

        Task.onMain(6000, new Runnable() {
            @Override
            public void run() {
                Activity activity = ApplicationUtils.getCurrentActivity();
                L.d("top activity", activity);
                if (activity == null) {
                    Task.onMain(6000, this);
                    return;
                }
                UpdateFactory.doUpdateCheck(activity);
            }
        });

        application.registerActivityLifecycleCallbacks(new ActivityLifecycleCallbacks() {
            @Override
            public void onActivityResumed(Activity activity) {
                plugin.onActivityResumed(activity);
            }

            @Override
            public void onActivityPaused(Activity activity) {
                plugin.onActivityPaused(activity);
            }
        });
    }
}
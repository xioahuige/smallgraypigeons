package cn.bbjy.android.smallgraypigeons;

import android.app.ActivityManager;
import android.content.Context;
import android.support.multidex.MultiDexApplication;
import android.text.TextUtils;

import cn.bbjy.android.smallgraypigeons.model.SealUserInfoManager;
import cn.bbjy.android.smallgraypigeons.rongyun.AppRYCallback;
import cn.bbjy.android.smallgraypigeons.rongyun.RongYUtils;
import cn.bbjy.android.smallgraypigeons.utils.ConstantUtils;
import cn.bbjy.android.smallgraypigeons.utils.SpUtils;
import io.rong.imkit.RongIM;
import io.rong.push.RongPushClient;

/**
 * Created by Administrator on 2018/9/21.
 */

public class App extends MultiDexApplication {
    @Override
    public void onCreate() {
        super.onCreate();
        initRongYun();
        openSealDBIfHasCachedToken();
    }


    private void openSealDBIfHasCachedToken() {
        String token = "6xdbvSydGJkWIoRYr8FDo0c5pmwyVWbvQxbCqdRITTvinrFhOD+cRgozgrzww9GohCrt4ZaeEd0FXV55k9M+Gg==";

        String cachedToken = (String) SpUtils.get(getApplicationContext(), ConstantUtils.TOKEN, token);
        if (!TextUtils.isEmpty(cachedToken)) {
            String current = getCurProcessName(this);
            String mainProcessName = getPackageName();
            if (mainProcessName.equals(current)) {
                SealUserInfoManager.getInstance().openDB();
            }
        }
    }

    /**
     * 初始化融云
     */
    private void initRongYun() {
        if (getApplicationInfo().packageName.equals(getCurProcessName(getApplicationContext()))) {
            // 小米推送，必须在init（）方法前
            RongPushClient.registerMiPush(this, "2882303761517516557", "5761751652557");
            RongIM.setServerInfo("nav.cn.ronghub.com", "up.qbox.me");
            // 初始化
            RongIM.init(this);
            // 设置聊天相关监听
            AppRYCallback.init(this);

        }
    }


    /**
     * 获取当前进程名
     *
     * @param context 上下文
     */
    public static String getCurProcessName(Context context) {
        int pid = android.os.Process.myPid();
        ActivityManager activityManager = (ActivityManager) context.getSystemService(Context.ACTIVITY_SERVICE);
        for (ActivityManager.RunningAppProcessInfo appProcess : activityManager.getRunningAppProcesses()) {
            if (appProcess.pid == pid) {
                return appProcess.processName;
            }
        }
        return null;
    }

}

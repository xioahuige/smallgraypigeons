package cn.bbjy.android.smallgraypigeons.ui.ac;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.Window;

import cn.bbjy.android.smallgraypigeons.R;
import cn.bbjy.android.smallgraypigeons.rongyun.AppRYCallback;
import cn.bbjy.android.smallgraypigeons.rongyun.RongYUtils;
import cn.bbjy.android.smallgraypigeons.utils.ConstantUtils;
import cn.bbjy.android.smallgraypigeons.utils.LogUtil;
import cn.bbjy.android.smallgraypigeons.utils.SpUtils;
import cn.bbjy.android.smallgraypigeons.utils.ToastUtils;
import io.rong.imkit.RongIM;
import io.rong.imlib.RongIMClient;

public class LaunchActivity extends Activity {


    private Context context;
    private android.os.Handler handler = new android.os.Handler();

String connectResultId;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        requestWindowFeature(Window.FEATURE_NO_TITLE);
        setContentView(R.layout.activity_launch);
        context = this;
        RongYUtils.connect(context);        String cacheToken = (String) SpUtils.get(this, ConstantUtils.TOKEN,"");

        if (!TextUtils.isEmpty(cacheToken)) {
            handler.postDelayed(new Runnable() {
                @Override
                public void run() {
                    goToMain();
                }
            }, 800);
        } else {
            handler.postDelayed(new Runnable() {
                @Override
                public void run() {
                    goToLogin();
                }
            }, 800);
        }
    }


    private void goToMain() {
        startActivity(new Intent(context, MainActivity.class));
        finish();
    }

    private void goToLogin() {
        startActivity(new Intent(context, LoginActivity.class));
        finish();
    }

    private boolean isNetworkConnected(Context context) {
        ConnectivityManager cm = (ConnectivityManager) context.getSystemService(Context.CONNECTIVITY_SERVICE);
        NetworkInfo ni = cm.getActiveNetworkInfo();
        return ni != null && ni.isConnectedOrConnecting();
    }

}

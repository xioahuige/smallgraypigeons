package cn.bbjy.android.smallgraypigeons.rongyun;

import android.content.Context;

import cn.bbjy.android.smallgraypigeons.utils.ConstantUtils;
import cn.bbjy.android.smallgraypigeons.utils.LogUtil;
import cn.bbjy.android.smallgraypigeons.utils.SpUtils;
import cn.bbjy.android.smallgraypigeons.utils.ToastUtils;
import io.rong.imkit.RongIM;
import io.rong.imlib.RongIMClient;

/**
 * <b>功能名：</b>功能 ID 功能名<br>
 *
 * @author 2018/9/22 王巧月
 */
public class RongYUtils {
    /**
     * 连接融云服务器
     */
    public static void connect(final Context context) {

        String token ="6sSYtxAro9PtcoepFihi/kc5pmwyVWbvQxbCqdRITTvinrFhOD+cRvnvzzw2BNYX5Nqg/0pZqXsFXV55k9M+Gg==";
        SpUtils.put(context,ConstantUtils.TOKEN,token);
        if (token != null) {
            RongIM.connect(token, new RongIMClient.ConnectCallback() {

                @Override
                public void onSuccess(String userId) {
                    LogUtil.e("MainActivityOLD", "——onSuccess—-" + userId);

                    ToastUtils.getInstance().showLongMsg(context, "用户id" + userId);
                }

                @Override
                public void onError(RongIMClient.ErrorCode errorCode) {
                    LogUtil.e("MainActivityOLD", "——onError—-" + errorCode);
                }

                @Override
                public void onTokenIncorrect() {
                    //Connect Token 失效的状态处理，需要重新获取 Token
                }
            });

        }
    }

    /**
     * 退出登录的处理
     *
     * @param context
     */
    public static void logout(Context context) {
        SpUtils.put(context, ConstantUtils.TOKEN, "");
//        TODO ChatDbUtils.deleteFriends();
        RongIM.getInstance().disconnect();
    }
}

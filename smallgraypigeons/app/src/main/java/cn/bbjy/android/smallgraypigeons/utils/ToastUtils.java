package cn.bbjy.android.smallgraypigeons.utils;

import android.content.Context;
import android.widget.Toast;

/**
 * <b>功能名：</b>功能 ID 功能名<br>
 * <b>说明：</b>类的说明<br>

 *
 * @author 2018/6/27 王巧月
 */
public class ToastUtils {
    /**
     * 私有化
     */
    private ToastUtils() {
    }


    public static ToastUtils getInstance() {
        return LayzzClass.toastUtils;
    }

    private static class LayzzClass {
        private static final ToastUtils toastUtils = new ToastUtils();

    }
    /**
     * 吐司
     * */
    public void showLongMsg(Context context,int msg){
        Toast.makeText(context,msg,Toast.LENGTH_LONG).show();

    }
    /**
     * 吐司
     * */
    public void showLongMsg(Context context,String msg){
        Toast.makeText(context,msg,Toast.LENGTH_LONG).show();

    } public void showShortMsg(Context context,String msg){
        Toast.makeText(context,msg,Toast.LENGTH_SHORT).show();

    }
}

package cn.bbjy.android.smallgraypigeons.ui.ac;

import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;
import cn.bbjy.android.smallgraypigeons.R;
import cn.bbjy.android.smallgraypigeons.model.SealAction;
import cn.bbjy.android.smallgraypigeons.model.SealUserInfoManager;
import cn.bbjy.android.smallgraypigeons.model.network.async.AsyncTaskManager;
import cn.bbjy.android.smallgraypigeons.model.network.http.HttpException;
import cn.bbjy.android.smallgraypigeons.model.response.GetUserInfoByIdResponse;
import cn.bbjy.android.smallgraypigeons.rongyun.RongGenerate;
import cn.bbjy.android.smallgraypigeons.ui.imactivity.BaseActivity;
import cn.bbjy.android.smallgraypigeons.ui.widget.ClearWriteEditText;
import cn.bbjy.android.smallgraypigeons.utils.ConstantUtils;
import cn.bbjy.android.smallgraypigeons.utils.SpUtils;
import io.rong.imkit.RongIM;
import io.rong.imlib.RongIMClient;
import io.rong.imlib.model.UserInfo;

public class LoginActivity extends BaseActivity {
    private final static String TAG = "LoginActivity";
    private static final int LOGIN = 5;
    private static final int GET_TOKEN = 6;
    private static final int SYNC_USER_INFO = 9;
    @BindView(R.id.de_img_backgroud)
    ImageView deImgBackgroud;
    @BindView(R.id.de_frm_backgroud)
    FrameLayout deFrmBackgroud;
    @BindView(R.id.de_login_logo)
    ImageView deLoginLogo;
    @BindView(R.id.de_login_phone)
    ClearWriteEditText deLoginPhone;
    @BindView(R.id.fr_username_delete)
    FrameLayout frUsernameDelete;
    @BindView(R.id.liner1)
    RelativeLayout liner1;
    @BindView(R.id.de_login_password)
    ClearWriteEditText deLoginPassword;
    @BindView(R.id.fr_pass_delete)
    FrameLayout frPassDelete;
    @BindView(R.id.liner2)
    RelativeLayout liner2;
    @BindView(R.id.de_login_sign)
    Button deLoginSign;
    @BindView(R.id.de_login_forgot)
    TextView deLoginForgot;
    @BindView(R.id.de_login_register)
    TextView deLoginRegister;
    private Context mContext;
    public AsyncTaskManager mAsyncTaskManager;
    protected SealAction action;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);
        ButterKnife.bind(this);
        setTitle("登录");
        setHeadLeftButtonVisibility(View.GONE);
        mContext = this;
    }

    @OnClick({R.id.fr_username_delete, R.id.fr_pass_delete,
            R.id.de_login_sign, R.id.de_login_forgot, R.id.de_login_register})
    public void onViewClicked(View view) {
        switch (view.getId()) {
            case R.id.fr_username_delete:
                break;
            case R.id.fr_pass_delete:
                break;
            case R.id.de_login_sign:
                login();
                break;
            case R.id.de_login_forgot:
                break;
            case R.id.de_login_register:
                break;
        }
    }

    String connectResultId;
    RongIMClient.ConnectCallback connectCallback = new RongIMClient.ConnectCallback() {
        @Override
        public void onTokenIncorrect() {
            Log.e(TAG, "reToken Incorrect");
        }

        @Override
        public void onSuccess(String s) {
            Log.e(TAG, "onSuccess s:" + s);
            connectResultId = s;
            SealUserInfoManager.getInstance().openDB();
            request(SYNC_USER_INFO, true);
        }

        @Override
        public void onError(RongIMClient.ErrorCode e) {
            reGetToken();
        }
    };

    void login() {

        String token = "+POQ4/HIOu/QOgQvPYG1gq5gaNklgbM7Cy0ZbVlsIlDX/9XEGE9UMu1OHtRtXfm4+DMux/09UFg=";
        SpUtils.put(mContext, ConstantUtils.TOKEN, token);
        RongIM.connect(token, connectCallback);
    }

    private void reGetToken() {
        request(GET_TOKEN);
    }

    /**
     * 发送请求
     *
     * @param requestCode    请求码
     * @param isCheckNetwork 是否需检查网络，true检查，false不检查
     */
    public void request(int requestCode, boolean isCheckNetwork) {
        if (mAsyncTaskManager != null) {
            mAsyncTaskManager.request(requestCode, isCheckNetwork, this);
        }
    }

    /**
     * 异步耗时方法
     *
     * @param requestCode 请求码
     * @param parameter
     * @return
     * @throws HttpException
     * @String parameter 请求传参,可不填
     */
    @Override
    public Object doInBackground(int requestCode, String parameter) throws HttpException {
        return null;
    }

    /**
     * 成功方法（可直接更新UI）
     *
     * @param requestCode 请求码
     * @param result      返回结果
     */
    @Override
    public void onSuccess(int requestCode, Object result) {
        if (result != null) {
            switch (requestCode) {

                case SYNC_USER_INFO:
                    GetUserInfoByIdResponse userInfoByIdResponse = (GetUserInfoByIdResponse) result;
                    if (userInfoByIdResponse.getCode() == 200) {
                        if (TextUtils.isEmpty(userInfoByIdResponse.getResult().getPortraitUri())) {
                            userInfoByIdResponse.getResult().setPortraitUri(RongGenerate.generateDefaultAvatar(userInfoByIdResponse.getResult().getNickname(), userInfoByIdResponse.getResult().getId()));
                        }
                        String nickName = userInfoByIdResponse.getResult().getNickname();
                        String portraitUri = userInfoByIdResponse.getResult().getPortraitUri();
                        SpUtils.put(mContext, ConstantUtils.SEALTALK_LOGIN_NAME, nickName);
                        SpUtils.put(mContext, ConstantUtils.SEALTALK_LOGING_PORTRAIT, nickName);

                        RongIM.getInstance().refreshUserInfoCache(new UserInfo(connectResultId, nickName, Uri.parse(portraitUri)));
                    }
                    //不继续在login界面同步好友,群组,群组成员信息
                    SealUserInfoManager.getInstance().getAllUserInfo();
                    startActivity(new Intent(LoginActivity.this, MainActivity.class));
                    finish();
                    break;
            }
        }

    }
}

package cn.bbjy.android.smallgraypigeons.ui.ac;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.net.Uri;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentActivity;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;
import android.text.TextUtils;
import android.view.KeyEvent;
import android.view.View;
import android.widget.TextView;

import java.util.ArrayList;
import java.util.List;

import cn.bbjy.android.smallgraypigeons.R;
import cn.bbjy.android.smallgraypigeons.ui.fragment.FoundFragment;
import cn.bbjy.android.smallgraypigeons.ui.fragment.MainFragment;
import cn.bbjy.android.smallgraypigeons.ui.fragment.MessageFragment;
import cn.bbjy.android.smallgraypigeons.ui.fragment.MySelfFragment;
import cn.bbjy.android.smallgraypigeons.ui.imactivity.NewFriendListActivity;
import cn.bbjy.android.smallgraypigeons.ui.widget.NoScrollViewPager;
import cn.bbjy.android.smallgraypigeons.ui.widget.tabview.TabView;
import cn.bbjy.android.smallgraypigeons.ui.widget.tabview.TabWidget;
import cn.bbjy.android.smallgraypigeons.utils.LoadDialog;
import cn.bbjy.android.smallgraypigeons.utils.ToastUtils;
import io.rong.imkit.RongIM;
import io.rong.imkit.manager.IUnReadMessageObserver;
import io.rong.imlib.RongIMClient;
import io.rong.imlib.model.Conversation;
import io.rong.message.ContactNotificationMessage;

public class MainActivity extends FragmentActivity {
    TabWidget tab;
    public static NoScrollViewPager mViewPager;
    /**
     * 用于对Fragment进行管理
     */
    private FragmentManager supportFragmentManager;
    /**
     * fragment集合
     */
    private List<Fragment> mFragment = new ArrayList<>();
    Fragment homeFragment, messageFragment, myFragment, foundFragment;
    private Context mContext;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_maint);
        mContext = this;
        supportFragmentManager = getSupportFragmentManager();
        initView();
        initData();
    }
    protected void initData() {

        final Conversation.ConversationType[] conversationTypes = {
                Conversation.ConversationType.PRIVATE,
                Conversation.ConversationType.GROUP, Conversation.ConversationType.SYSTEM,
                Conversation.ConversationType.PUBLIC_SERVICE, Conversation.ConversationType.APP_PUBLIC_SERVICE
        };

      //  RongIM.getInstance().addUnReadMessageCountChangedObserver(this, conversationTypes);
        getConversationPush();// 获取 push 的 id 和 target
        getPushMessage();
    }
    private void getConversationPush() {
        if (getIntent() != null && getIntent().hasExtra("PUSH_CONVERSATIONTYPE") && getIntent().hasExtra("PUSH_TARGETID")) {

            final String conversationType = getIntent().getStringExtra("PUSH_CONVERSATIONTYPE");
            final String targetId = getIntent().getStringExtra("PUSH_TARGETID");


            RongIM.getInstance().getConversation(Conversation.ConversationType.valueOf(conversationType), targetId, new RongIMClient.ResultCallback<Conversation>() {
                @Override
                public void onSuccess(Conversation conversation) {

                    if (conversation != null) {

                        if (conversation.getLatestMessage() instanceof ContactNotificationMessage) { //好友消息的push
                            /*TODO 待修改*/
                            startActivity(new Intent(MainActivity.this, NewFriendListActivity.class));
                        } else {
                            Uri uri = Uri.parse("rong://" + getApplicationInfo().packageName).buildUpon().appendPath("conversation")
                                    .appendPath(conversationType).appendQueryParameter("targetId", targetId).build();
                            Intent intent = new Intent(Intent.ACTION_VIEW);
                            intent.setData(uri);
                            startActivity(intent);
                        }
                    }
                }

                @Override
                public void onError(RongIMClient.ErrorCode e) {

                }
            });
        }
    }

    /**
     * 得到不落地 push 消息
     */
    private void getPushMessage() {
        Intent intent = getIntent();
        if (intent != null && intent.getData() != null && intent.getData().getScheme().equals("rong")) {
            String path = intent.getData().getPath();
            if (path.contains("push_message")) {
                SharedPreferences sharedPreferences = getSharedPreferences("config", MODE_PRIVATE);
                String cacheToken = sharedPreferences.getString("loginToken", "");
                if (TextUtils.isEmpty(cacheToken)) {
                    startActivity(new Intent(MainActivity.this, LoginActivity.class));
                } else {
                    if (!RongIM.getInstance().getCurrentConnectionStatus().equals(RongIMClient.ConnectionStatusListener.ConnectionStatus.CONNECTED)) {
                        LoadDialog.show(mContext);
                        RongIM.connect(cacheToken, new RongIMClient.ConnectCallback() {
                            @Override
                            public void onTokenIncorrect() {
                                LoadDialog.dismiss(mContext);
                            }

                            @Override
                            public void onSuccess(String s) {
                                LoadDialog.dismiss(mContext);
                            }

                            @Override
                            public void onError(RongIMClient.ErrorCode e) {
                                LoadDialog.dismiss(mContext);
                            }
                        });
                    }
                }
            }
        }
    }
    private int tabImgs[] = {R.drawable.tab_home, R.drawable.select_tab_find, R.drawable.tab_message,
            R.drawable.tab_mini};

    private String tabTitles[] = {"首页", "发现", "消息", "我的"};

    /**
     * @Description: TODO 底部切换选择fragment
     * @author Sunday
     * @date 2016年3月26日
     */
    public void setTabSelection(int index) {
        // 开启一个Fragment事务
        FragmentTransaction
                transaction = supportFragmentManager.beginTransaction();
        // 先隐藏掉所有的Fragment，以防止有多个Fragment显示在界面上的情况
        hideFragments(transaction);
        switch (index) {

            case 0:
                if (homeFragment == null) {

                    homeFragment = MainFragment.newInstance();
                    transaction.add(R.id.main_new_frame, homeFragment);

                } else {
                    transaction.show(homeFragment);
                }
                break;
            case 1:
                if (foundFragment == null) {

                    foundFragment = FoundFragment.newInstance("", "");
                    transaction.add(R.id.main_new_frame, foundFragment);

                } else {
                    transaction.show(foundFragment);
                }
                break;
            case 2:
                if (messageFragment == null) {
                    messageFragment = MessageFragment.newInstance();
                    transaction.add(R.id.main_new_frame, messageFragment);

                } else {
                    transaction.show(messageFragment);
                }
                break;

            case 3:
                if (myFragment == null) {
                    myFragment = MySelfFragment.newInstance();
                    transaction.add(R.id.main_new_frame, myFragment);

                } else {
                    transaction.show(myFragment);
                }
                break;
        }
        transaction.commit();
    }

    /**
     * @Description: TODO 隐藏所有的fragment，防止多个fragment显示在界面
     * @author Sunday
     * @date 2016年3月26日
     */
    private void hideFragments(FragmentTransaction transaction) {
        if (homeFragment != null) {
            transaction.hide(homeFragment);
        }
        if (foundFragment != null) {
            transaction.hide(foundFragment);
        }
        if (messageFragment != null) {
            transaction.hide(messageFragment);
        }
        if (myFragment != null) {
            transaction.hide(myFragment);
        }
    }

    // 定义一个变量，来标识是否退出
    private static boolean isExit = false;

    Handler mHandler = new Handler() {

        @Override
        public void handleMessage(Message msg) {
            super.handleMessage(msg);
            isExit = false;
        }
    };

    /**
     * 处理返回键事件
     */
    @Override
    public boolean onKeyDown(int keyCode, KeyEvent event) {
        if (keyCode == KeyEvent.KEYCODE_BACK) {
            exit();
            return false;
        }
        return super.onKeyDown(keyCode, event);
    }

    private void exit() {
        if (!isExit) {
            isExit = true;
            ToastUtils.getInstance().showLongMsg(mContext, "再按一次退出应用");
            // 利用handler延迟发送更改状态信息
            mHandler.sendEmptyMessageDelayed(0, 2000);
        } else {
            finish();
            System.exit(0);
        }
    }


    //初始化轮播图
    private void initView() {
        tab =   findViewById(R.id.main_bottom);
        //主页面
        mViewPager = findViewById(R.id.viewpager_fragment);
        mViewPager.setNoScroll(false);
        // 给tab 绑定菜单
        for (int i = 0; i < tabImgs.length; i++) {
            tab.addView(getTabItemView(i));
        }
        // 设定菜单更换事件
        tab.setOnTabSelectionListener(tabListener);
        setTabSelection(2);
        tab.setCurrentTab(2);
    }

    // 设置监听，当点击Tab如果不是当前Tab时进行Fragment替换 当菜单选项改变时
    private TabWidget.OnTabSelectionChanged tabListener = new TabWidget.OnTabSelectionChanged() {
        @Override
        public void onTabSelectionChanged(int tabIndex, boolean clicked) {
            TabView tabView = (TabView) tab.getChildAt(tabIndex);
          /*  if (mViewPager.getCurrentItem()-tabIndex==1||tabIndex-mViewPager.getCurrentItem()==1){
                mViewPager.setCurrentItem(tabIndex,true);
            }else{*/
            // mViewPager.setCurrentItem(tabIndex, false);
            setTabSelection(tabIndex); /*}*/


        }
    };

    /**
     * 给Tab按钮设置图标和文字
     */
    private TextView mUnread;

    private View getTabItemView(int index) {
        TabView tabView = new TabView(mContext);
        tabView.setTitle(tabTitles[index]);
        tabView.setImageResource(tabImgs[index]);
        if (index == 1) {
            mUnread = (TextView) tabView.findViewById(R.id.unread);
        }
        return tabView;
    }

}

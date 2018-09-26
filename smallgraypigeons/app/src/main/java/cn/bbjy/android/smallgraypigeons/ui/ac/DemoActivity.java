package cn.bbjy.android.smallgraypigeons.ui.ac;

import android.content.Context;
import android.os.Bundle;
import android.support.design.widget.TabLayout;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentActivity;
import android.support.v4.app.FragmentPagerAdapter;
import android.support.v4.view.ViewPager;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import java.util.ArrayList;
import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.Unbinder;
import cn.bbjy.android.smallgraypigeons.R;
import cn.bbjy.android.smallgraypigeons.ui.fragment.DiscoverFragment;
import cn.bbjy.android.smallgraypigeons.ui.fragment.MineFragment;
import cn.bbjy.android.smallgraypigeons.utils.ViewTool;

public class DemoActivity extends FragmentActivity {

    @BindView(R.id.tabChild)
    TabLayout tabLayout;
    @BindView(R.id.vpChild)
    ViewPager vpChild;
    @BindView(R.id.addFriends)
    ImageView ivAddFriends;
    Unbinder unbinder;
    private Context context;
    TextView unread;
    private String tabTitles[] = {"好友", "消息"};

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.fragment_message);
        ButterKnife.bind(this);
        //待付款栏目-加载自定义显示小红点的布局
        context = this;
        final List<Fragment> fragmentList = new ArrayList<Fragment>();
        fragmentList.add(new DiscoverFragment());
        fragmentList.add(new MineFragment());
        vpChild.setAdapter(new FragmentPagerAdapter(getSupportFragmentManager()) {
            @Override
            public Fragment getItem(int position) {
                return fragmentList.get(position);
            }

            @Override
            public int getCount() {
                return fragmentList.size();
            }
        });
        tabLayout.setupWithViewPager(vpChild);
        for (int i = 0; i < tabTitles.length; i++) {
            TabLayout.Tab tabOne = tabLayout.newTab();
            tabOne.setCustomView(R.layout.view_tabview);
            TextView tv_tab_title = tabOne.getCustomView().findViewById(R.id.tabtitle);
            tv_tab_title.setText(tabTitles[i]);
            if (i == 1) {
                unread = tabOne.getCustomView().findViewById(R.id.unread);
                unread.setVisibility(View.VISIBLE);
            }
            tabLayout.addTab(tabOne);
        }
     ViewTool.getInstance().reflex(tabLayout);

        //添加tabLayout选中监听
        tabLayout.addOnTabSelectedListener(new TabLayout.OnTabSelectedListener() {
            @Override
            public void onTabSelected(TabLayout.Tab tab) {
                if (tab.getPosition() == 1) {

                }
                //设置选中时的文字颜色
                if (tab.getCustomView() != null) {
                    //tv_tab_title.setTextColor(getResources().getColor(R.color.colorAccent));
                }
            }

            @Override
            public void onTabUnselected(TabLayout.Tab tab) {
                //设置未选中时的文字颜色
                if (tab.getCustomView() != null) {
                    //   tv_tab_title.setTextColor(getResources().getColor(R.color.black));
                }
            }

            @Override
            public void onTabReselected(TabLayout.Tab tab) {

            }
        });
    }
}

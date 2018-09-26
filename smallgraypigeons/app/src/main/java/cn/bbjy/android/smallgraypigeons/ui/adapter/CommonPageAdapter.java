package cn.bbjy.android.smallgraypigeons.ui.adapter;

import android.support.design.widget.TabLayout;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentPagerAdapter;
import android.view.View;

import java.util.List;

import cn.bbjy.android.smallgraypigeons.R;

/**
 * <b>功能名：</b>功能 ID 功能名<br>
 * <b>说明：</b>类的说明<br>
 * <b>著作权：</b> Copyright (C) 2018 DCJINCHAN CORPORATION<br>
 * <b>修改履历：</b>
 * <li>YYYY/MM/DD 姓名 改修案件名
 * <li>YYYY/MM/DD 姓名 改修案件名
 *
 * @author 2018/8/3 王巧月
 */
public class CommonPageAdapter extends FragmentPagerAdapter {
    List<Fragment> mFragments;
    String[] mTitleList = null; //三个Tab
TabLayout tabLayout;
    public CommonPageAdapter(FragmentManager childFragmentManager, List<Fragment> fragmentList,
                             String[] tabTitles    ) {
        super(childFragmentManager);
        this.mFragments = fragmentList;
        this.mTitleList = tabTitles;
        this.tabLayout=tabLayout;
    }

    /**
     * Return the Fragment associated with a specified position.
     *
     * @param position
     */
    @Override
    public Fragment getItem(int position) {
        return mFragments.get(position);
    }

    /**
     * Return the number of views available.
     */
    @Override
    public int getCount() {
        return mFragments != null ? mFragments.size() : 0;
    }
    //ViewPager与TabLayout绑定后，这里获取到PageTitle就是Tab的Text

  /*  @Override
    public CharSequence getPageTitle(int position) {
        return mTitleList[position % mTitleList.length];
    }*/


    @Override
    public boolean isViewFromObject(View arg0, Object arg1) {
        // TODO Auto-generated method stub
        // 刚开始用viewpager就直接写“return arg0 == arg1;”就好啦
        return arg0 == arg1;
    }


}

package cn.bbjy.android.smallgraypigeons.ui.fragment;

import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.design.widget.TabLayout;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentPagerAdapter;
import android.support.v4.view.ViewPager;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import java.util.ArrayList;
import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.Unbinder;
import cn.bbjy.android.smallgraypigeons.R;
import cn.bbjy.android.smallgraypigeons.ui.adapter.ConversationListAdapterEx;
import cn.bbjy.android.smallgraypigeons.ui.imactivity.AddFriendActivity;
import cn.bbjy.android.smallgraypigeons.utils.LogUtil;
import cn.bbjy.android.smallgraypigeons.utils.ViewTool;
import io.rong.imkit.RongContext;
import io.rong.imkit.RongIM;
import io.rong.imkit.fragment.ConversationListFragment;
import io.rong.imkit.manager.IUnReadMessageObserver;
import io.rong.imlib.model.Conversation;

/**
 * A simple {@link Fragment} subclass.
 * Activities that contain this fragment must implement the
 * to handle interaction events.
 * Use the {@link MessageFragment#newInstance} factory method to
 * create an instance of this fragment.
 */
public class MessageFragment extends Fragment implements View.OnClickListener, IUnReadMessageObserver {

    @BindView(R.id.tabChild)
    TabLayout tabLayout;
    @BindView(R.id.vpChild)
    ViewPager vpChild;
    @BindView(R.id.addFriends)
    ImageView ivAddFriends;
    TextView unread;
    Unbinder unbinder;
    private Context context;

    public MessageFragment() {
    }

    /**
     * Use this factory method to create a new instance of
     * this fragment using the provided parameters.
     *
     * @return A new instance of fragment MessageFragment.
     */
    // TODO: Rename and change types and number of parameters
    public static MessageFragment newInstance() {
        MessageFragment fragment = new MessageFragment();
//        Bundle args = new Bundle();
//        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        context = getContext();
    }


    private String tabTitles[] = {"好友", "消息"};

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_message, container, false);
        unbinder = ButterKnife.bind(this, view);
        return view;
    }


    @Override
    public void onViewCreated(final View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        ivAddFriends.setOnClickListener(this);
        final List<Fragment> fragmentList = new ArrayList<Fragment>();
        fragmentList.add(new ContactsFragment());
        Fragment conversationList = initConversationList();
        fragmentList.add(conversationList);
        vpChild.setAdapter(new FragmentPagerAdapter(getChildFragmentManager()) {
            @Override
            public Fragment getItem(int position) {
                return fragmentList.get(position);
            }

            @Override
            public int getCount() {
                return fragmentList.size();
            }
        });
        for (int i = 0; i < tabTitles.length; i++) {
            TabLayout.Tab tabOne = tabLayout.newTab();
            tabOne.setCustomView(R.layout.view_tabview_bottom);
            TextView tv_tab_title = tabOne.getCustomView().findViewById(R.id.tab_bottom_text);
            tv_tab_title.setText(tabTitles[i]);
            if (i == 1) {
                unread = tabOne.getCustomView().findViewById(R.id.tab_bottom_red);
                unread.setVisibility(View.VISIBLE);
                unread.setText("2");
            }

            tabLayout.addTab(tabOne);
        }
        ViewTool.getInstance().showTabTextAdapteIndicator(tabLayout);
        //添加tabLayout选中监听
        tabLayout.addOnTabSelectedListener(new TabLayout.OnTabSelectedListener() {
            @Override
            public void onTabSelected(TabLayout.Tab tab) {
                if (tab.getPosition() != vpChild.getCurrentItem()) {
                    if (tab.getPosition() == 1) {
                        vpChild.setCurrentItem(1);
                        ivAddFriends.setVisibility(View.GONE);
                    } else {
                        vpChild.setCurrentItem(0);
                        ivAddFriends.setVisibility(View.VISIBLE);
                    }
                    //设置选中时的文字颜色
                    if (tab.getCustomView() != null) {
                        TextView tv_tab_title = tab.getCustomView().findViewById(R.id.tab_bottom_text);
                        tv_tab_title.setTextColor(getResources().getColor(R.color.black));
                    }
                }

            }

            @Override
            public void onTabUnselected(TabLayout.Tab tab) {
                //设置未选中时的文字颜色
                if (tab.getCustomView() != null) {
                    TextView tv_tab_title = tab.getCustomView().findViewById(R.id.tab_bottom_text);
                    tv_tab_title.setTextColor(getResources().getColor(R.color.gray));
                }
            }

            @Override
            public void onTabReselected(TabLayout.Tab tab) {

            }
        });
        vpChild.setOnPageChangeListener(new ViewPager.OnPageChangeListener() {
            @Override
            public void onPageScrolled(int position, float positionOffset, int positionOffsetPixels) {

                LogUtil.i("position:" + position + "positionOffset:" + positionOffset + "positionOffsetPixels:" + positionOffsetPixels);
            }

            @Override
            public void onPageSelected(int position) {
                LogUtil.i("position:" + position);
                if (tabLayout.getSelectedTabPosition() != position) {
                    tabLayout.getTabAt(position).select();
                }
            }

            @Override
            public void onPageScrollStateChanged(int state) {
                LogUtil.i("onPageScrollStateChanged:state=" + state);

            }
        });
        final Conversation.ConversationType[] conversationTypes = {
                Conversation.ConversationType.PRIVATE,
                Conversation.ConversationType.GROUP, Conversation.ConversationType.SYSTEM,
                Conversation.ConversationType.PUBLIC_SERVICE, Conversation.ConversationType.APP_PUBLIC_SERVICE
        };

         RongIM.getInstance().addUnReadMessageCountChangedObserver(this, conversationTypes);
    }

    /**
     * 会话列表的fragment
     */
    private boolean isDebug;
    private Conversation.ConversationType[] mConversationsTypes = null;
    private ConversationListFragment mConversationListFragment = null;

    /**
     * 初始化会话列表
     */
    private Fragment initConversationList() {
        if (mConversationListFragment == null) {
            ConversationListFragment listFragment = new ConversationListFragment();
            listFragment.setAdapter(new ConversationListAdapterEx(RongContext.getInstance()));
            Uri uri;
            if (isDebug) {
                uri = Uri.parse("rong://" + context.getApplicationInfo().packageName).buildUpon()
                        .appendPath("conversationlist")
                        .appendQueryParameter(Conversation.ConversationType.PRIVATE.getName(), "true") //设置私聊会话是否聚合显示
                        .appendQueryParameter(Conversation.ConversationType.GROUP.getName(), "true")//群组
                        .appendQueryParameter(Conversation.ConversationType.PUBLIC_SERVICE.getName(), "false")//公共服务号
                        .appendQueryParameter(Conversation.ConversationType.APP_PUBLIC_SERVICE.getName(), "false")//订阅号
                        .appendQueryParameter(Conversation.ConversationType.SYSTEM.getName(), "true")//系统
                        .appendQueryParameter(Conversation.ConversationType.DISCUSSION.getName(), "true")
                        .build();
                mConversationsTypes = new Conversation.ConversationType[]{Conversation.ConversationType.PRIVATE,
                        Conversation.ConversationType.GROUP,
                        Conversation.ConversationType.PUBLIC_SERVICE,
                        Conversation.ConversationType.APP_PUBLIC_SERVICE,
                        Conversation.ConversationType.SYSTEM,
                        Conversation.ConversationType.DISCUSSION
                };

            } else {
                uri = Uri.parse("rong://" + context.getApplicationInfo().packageName).buildUpon()
                        .appendPath("conversationlist")
                        .appendQueryParameter(Conversation.ConversationType.PRIVATE.getName(), "false") //设置私聊会话是否聚合显示
                        .appendQueryParameter(Conversation.ConversationType.GROUP.getName(), "false")//群组
                        .appendQueryParameter(Conversation.ConversationType.PUBLIC_SERVICE.getName(), "false")//公共服务号
                        .appendQueryParameter(Conversation.ConversationType.APP_PUBLIC_SERVICE.getName(), "false")//订阅号
                        .appendQueryParameter(Conversation.ConversationType.SYSTEM.getName(), "true")//系统
                        .build();
                mConversationsTypes = new Conversation.ConversationType[]{Conversation.ConversationType.PRIVATE,
                        Conversation.ConversationType.GROUP,
                        Conversation.ConversationType.PUBLIC_SERVICE,
                        Conversation.ConversationType.APP_PUBLIC_SERVICE,
                        Conversation.ConversationType.SYSTEM
                };
            }
            listFragment.setUri(uri);
            mConversationListFragment = listFragment;
            return listFragment;
        } else {
            return mConversationListFragment;
        }
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        unbinder.unbind();
    }

    @Override
    public void onClick(View view) {
        int id = view.getId();
        switch (id) {
            case R.id.addFriends:
              /*  MorePopWindow morePopWindow = new MorePopWindow(getActivity());
                morePopWindow.showPopupWindow(ivAddFriends);*/
                context.startActivity(new Intent(context, AddFriendActivity.class));
                 break;

        }
    }

    @Override
    public void onCountChanged(int count) {
        if (count == 0) {
            unread.setVisibility(View.GONE);
        } else if (count > 0 && count < 100) {
            unread.setVisibility(View.VISIBLE);
            unread.setText(String.valueOf(count));
        } else {
            unread.setVisibility(View.VISIBLE);
            unread.setText(R.string.no_read_message);
        }
    }

}

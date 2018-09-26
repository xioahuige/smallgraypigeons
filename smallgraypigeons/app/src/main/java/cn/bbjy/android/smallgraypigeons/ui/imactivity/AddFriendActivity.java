package cn.bbjy.android.smallgraypigeons.ui.imactivity;

import android.os.Bundle;
import android.view.View;
import android.widget.ListView;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;
import cn.bbjy.android.smallgraypigeons.R;

public class AddFriendActivity extends BaseActivity implements View.OnClickListener {

    //附近的人列表
    @BindView(R.id.nearby_address_list)
    ListView lvNearbyAddress;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add_friend);
        ButterKnife.bind(this);
        setTitle((R.string.search_friend));
    }

    @Override
    public void onClick(View view) {

    }

    @OnClick({R.id.aliwx_search_layout, R.id.re_seachByCondition,
            R.id.re_AddByContact, R.id.re_AddBynearBy, R.id.showNearbyAddressPeople})
    public void onViewClicked(View view) {
        switch (view.getId()) {
            case R.id.aliwx_search_layout:
                //搜索
                break;
            case R.id.re_seachByCondition:
                //按条件查询
                break;
            case R.id.re_AddByContact:
                // 添加通讯录好友
                break;
            case R.id.re_AddBynearBy:
                //查看附近的人
                break;
            case R.id.showNearbyAddressPeople:
                break;
        }
    }
}

package cn.bbjy.android.smallgraypigeons.ui.widget.tabview;

import android.content.Context;
import android.graphics.drawable.Drawable;
import android.util.AttributeSet;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.RelativeLayout;
import android.widget.TextView;

import cn.bbjy.android.smallgraypigeons.R;

public class TabView extends RelativeLayout {
//	private ImageView image;
	private TextView tv_title;

	public TabView(Context context, AttributeSet attrs) {
		super(context, attrs);
	}

	public TabView(Context context) {
		super(context);
		LayoutInflater layoutInflater = LayoutInflater.from(context);
		View v = layoutInflater.inflate(R.layout.view_tabview, this);
//		image = (ImageView) v.findViewById(R.id.tabImageView);
		tv_title = (TextView) v.findViewById(R.id.tabtitle);
	}

	// 设置图片
	public void setImageResource(int resId) {
//		image.setImageResource(resId);
		Drawable drawable = getResources().getDrawable(resId);
		drawable.setBounds(0, 0, drawable.getIntrinsicWidth(),
				drawable.getIntrinsicHeight());
		tv_title.setCompoundDrawables(null, drawable, null, null);
	}

	// 设置标题
	public void setTitle(String string) {
		tv_title.setText(string);
	}
}

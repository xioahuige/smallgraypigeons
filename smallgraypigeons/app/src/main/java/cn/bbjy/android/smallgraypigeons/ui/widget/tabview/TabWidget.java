package cn.bbjy.android.smallgraypigeons.ui.widget.tabview;

/**
 * Copyright (C) 2006 The Android Open Source Project
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

import android.content.Context;
import android.content.res.Resources;
import android.content.res.TypedArray;
import android.graphics.Canvas;
import android.graphics.Rect;
import android.graphics.drawable.Drawable;
import android.util.AttributeSet;
import android.view.View;
import android.view.View.OnFocusChangeListener;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;

import cn.bbjy.android.smallgraypigeons.R;

/***
 * 修改TabWidget源码文件，修复点击时出现的空指针异常，并使之可以独立于TabHost使用，该TabWidget依然 显示一排tab标签
 * 当用户点击tab时，可以监听切换动作。虽然该类继承自LinearLayout，但是有些LinearLayout 的布局属性并不能应用到该Widget
 * 
 * @attr ref android.R.styleable#TabWidget_divider
 * @attr ref android.R.styleable#TabWidget_tabStripEnabled
 * @attr ref android.R.styleable#TabWidget_tabStripLeft
 * @attr ref android.R.styleable#TabWidget_tabStripRight
 */
public class      TabWidget extends LinearLayout implements OnFocusChangeListener {
	/**
	 * 标签选择监听器
	 */
	private OnTabSelectionChanged mSelectionChangedListener;
	/**
	 * 当前选择的标签
	 */
	private int mSelectedTab = 0;

	private boolean mDrawBottomStrips = true;
	private Drawable mDividerDrawable;
	private Drawable mLeftStrip;
	private Drawable mRightStrip;

	private boolean mStripMoved;

	private final Rect mBounds = new Rect();

	private Context mContext;

	public TabWidget(Context context) {
		this(context, null);
	}

	public TabWidget(Context context, AttributeSet attrs) {
		this(context, attrs, android.R.attr.tabWidgetStyle);
	}

	public TabWidget(Context context, AttributeSet attrs, int defStyle) {
		super(context, attrs);
		mContext = context;
		TypedArray a = context.obtainStyledAttributes(attrs,
				R.styleable.TabWidget_style, defStyle, 0);
		mDrawBottomStrips = a.getBoolean(R.styleable.TabWidget_style_tabStripEnabled,
				true);
		mDividerDrawable = a.getDrawable(R.styleable.TabWidget_style_dividerxin);
		mLeftStrip = a.getDrawable(R.styleable.TabWidget_style_tabStripLeft);
		mRightStrip = a.getDrawable(R.styleable.TabWidget_style_tabStripRight);
		a.recycle();

		initTabWidget();
	}

	private void initTabWidget() {
		setOrientation(LinearLayout.HORIZONTAL);
		// mGroupFlags |= FLAG_USE_CHILD_DRAWING_ORDER;

		Resources resources = mContext.getResources();
		if (mLeftStrip == null) {
			mLeftStrip = resources.getDrawable(R.drawable.tab_strips);
		}
		if (mRightStrip == null) {
			mRightStrip = resources.getDrawable(R.drawable.tab_strips);
		}
		// 因为我们不希望用默认的focus，这里自己设置
		setFocusable(true);
		setOnFocusChangeListener(this);
	}

	/**
	 * 获取焦点时，将焦点赋给子view，同时设置当前的tab为该view
	 */
	@Override
	public void onFocusChange(View v, boolean hasFocus) {
		if (v == this && hasFocus && getTabCount() > 0) {
			getChildTabViewAt(mSelectedTab).requestFocus();
			return;
		}

		if (hasFocus) {
			int i = 0;
			int numTabs = getTabCount();
			while (i < numTabs) {
				if (getChildTabViewAt(i) == v) {
					setCurrentTab(i);
					if (mSelectionChangedListener != null) {
						mSelectionChangedListener.onTabSelectionChanged(i,
								false);
					}
					break;
				}
				i++;
			}
		}
	}

	@Override
	protected void onSizeChanged(int w, int h, int oldw, int oldh) {
		mStripMoved = true;
		super.onSizeChanged(w, h, oldw, oldh);
	}

	@Override
	protected int getChildDrawingOrder(int childCount, int i) {
		// Always draw the selected tab last, so that drop shadows are drawn
		// in the correct z-order.
		if (i == childCount - 1) {
			return mSelectedTab;
		} else if (i >= mSelectedTab) {
			return i + 1;
		} else {
			return i;
		}
	}

	/***
	 * Returns the tab indicator view at the given index.
	 * 
	 * @param index
	 *            the zero-based index of the tab indicator view to return
	 * @return the tab indicator view at the given index
	 */
	public View getChildTabViewAt(int index) {
		// If we are using dividers, then instead of tab views at 0, 1, 2, ...
		// we have tab views at 0, 2, 4, ...
		if (mDividerDrawable != null) {
			index *= 2;
		}
		return getChildAt(index);
	}

	/***
	 * Returns the number of tab indicator views.
	 * 
	 * @return the number of tab indicator views.
	 */
	public int getTabCount() {
		int children = getChildCount();

		// If we have dividers, then we will always have an odd number of
		// children: 1, 3, 5, ... and we want to convert that sequence to
		// this: 1, 2, 3, ...
		if (mDividerDrawable != null) {
			children = (children + 1) / 2;
		}
		return children;
	}

	/***
	 * Sets the drawable to use as a divider between the tab indicators.
	 * 
	 * @param drawable
	 *            the divider drawable
	 */
	public void setDividerDrawable(Drawable drawable) {
		mDividerDrawable = drawable;
		requestLayout();
		invalidate();
	}

	/***
	 * Sets the drawable to use as a divider between the tab indicators.
	 * 
	 * @param resId
	 *            the resource identifier of the drawable to use as a divider.
	 */
	public void setDividerDrawable(int resId) {
		mDividerDrawable = mContext.getResources().getDrawable(resId);
		requestLayout();
		invalidate();
	}

	/***
	 * Sets the drawable to use as the left part of the strip below the tab
	 * indicators.
	 * 
	 * @param drawable
	 *            the left strip drawable
	 */
	public void setLeftStripDrawable(Drawable drawable) {
		mLeftStrip = drawable;
		requestLayout();
		invalidate();
	}

	/***
	 * Sets the drawable to use as the left part of the strip below the tab
	 * indicators.
	 * 
	 * @param resId
	 *            the resource identifier of the drawable to use as the left
	 *            strip drawable
	 */
	public void setLeftStripDrawable(int resId) {
		mLeftStrip = mContext.getResources().getDrawable(resId);
		requestLayout();
		invalidate();
	}

	/***
	 * Sets the drawable to use as the right part of the strip below the tab
	 * indicators.
	 * 
	 * @param drawable
	 *            the right strip drawable
	 */
	public void setRightStripDrawable(Drawable drawable) {
		mRightStrip = drawable;
		requestLayout();
		invalidate();
	}

	/***
	 * Sets the drawable to use as the right part of the strip below the tab
	 * indicators.
	 * 
	 * @param resId
	 *            the resource identifier of the drawable to use as the right
	 *            strip drawable
	 */
	public void setRightStripDrawable(int resId) {
		mRightStrip = mContext.getResources().getDrawable(resId);
		requestLayout();
		invalidate();
	}

	/***
	 * Controls whether the bottom strips on the tab indicators are drawn or
	 * not. The default is to draw them. If the user specifies a custom view for
	 * the tab indicators, then the TabHost class calls this method to disable
	 * drawing of the bottom strips.
	 * 
	 * @param stripEnabled
	 *            true if the bottom strips should be drawn.
	 */
	public void setStripEnabled(boolean stripEnabled) {
		mDrawBottomStrips = stripEnabled;
		invalidate();
	}

	/***
	 * Indicates whether the bottom strips on the tab indicators are drawn or
	 * not.
	 */
	public boolean isStripEnabled() {
		return mDrawBottomStrips;
	}

	@Override
	public void childDrawableStateChanged(View child) {
		if (getTabCount() > 0 && child == getChildTabViewAt(mSelectedTab)) {
			// To make sure that the bottom strip is redrawn
			invalidate();
		}
		super.childDrawableStateChanged(child);
	}

	@Override
	public void dispatchDraw(Canvas canvas) {
		super.dispatchDraw(canvas);

		// Do nothing if there are no tabs.
		if (getTabCount() == 0)
			return;

		// If the user specified a custom view for the tab indicators, then
		// do not draw the bottom strips.
		if (!mDrawBottomStrips) {
			// Skip drawing the bottom strips.
			return;
		}

		final View selectedChild = getChildTabViewAt(mSelectedTab);

		final Drawable leftStrip = mLeftStrip;
		final Drawable rightStrip = mRightStrip;

		leftStrip.setState(selectedChild.getDrawableState());
		rightStrip.setState(selectedChild.getDrawableState());

		if (mStripMoved) {
			final Rect bounds = mBounds;
			bounds.left = selectedChild.getLeft();
			bounds.right = selectedChild.getRight();
			final int myHeight = getHeight();
			leftStrip.setBounds(
					Math.min(0, bounds.left - leftStrip.getIntrinsicWidth()),
					myHeight - leftStrip.getIntrinsicHeight(), bounds.left,
					myHeight);
			rightStrip.setBounds(
					bounds.right,
					myHeight - rightStrip.getIntrinsicHeight(),
					Math.max(getWidth(),
							bounds.right + rightStrip.getIntrinsicWidth()),
					myHeight);
			mStripMoved = false;
		}

		leftStrip.draw(canvas);
		rightStrip.draw(canvas);
	}

	/***
	 * Sets the current tab. This method is used to bring a tab to the front of
	 * the Widget, and is used to post to the rest of the UI that a different
	 * tab has been brought to the foreground.
	 * 
	 * Note, this is separate from the traditional "focus" that is employed from
	 * the view logic.
	 * 
	 * For instance, if we have a list in a tabbed view, a user may be
	 * navigating up and down the list, moving the UI focus (orange
	 * highlighting) through the list items. The cursor movement does not effect
	 * the "selected" tab though, because what is being scrolled through is all
	 * on the same tab. The selected tab only changes when we navigate between
	 * tabs (moving from the list view to the next tabbed view, in this
	 * example).
	 * 
	 * To move both the focus AND the selected tab at once, please use
	 * {@link #setCurrentTab}. Normally, the view logic takes care of adjusting
	 * the focus, so unless you're circumventing the UI, you'll probably just
	 * focus your interest here.
	 * 
	 * @param index
	 *            The tab that you want to indicate as the selected tab (tab
	 *            brought to the front of the widget)
	 * 
	 * @see #focusCurrentTab
	 */
	public void setCurrentTab(int index) {
		if (index < 0 || index >= getTabCount()) {
			return;
		}

		getChildTabViewAt(mSelectedTab).setSelected(false);
		mSelectedTab = index;
		getChildTabViewAt(mSelectedTab).setSelected(true);
		mStripMoved = true;
	}

	/***
	 * Sets the current tab and focuses the UI on it. This method makes sure
	 * that the focused tab matches the selected tab, normally at
	 * {@link #setCurrentTab}. Normally this would not be an issue if we go
	 * through the UI, since the UI is responsible for calling
	 * TabWidget.onFocusChanged(), but in the case where we are selecting the
	 * tab programmatically, we'll need to make sure focus keeps up.
	 * 
	 * @param index
	 *            The tab that you want focused (highlighted in orange) and
	 *            selected (tab brought to the front of the widget)
	 * 
	 * @see #setCurrentTab
	 */
	public void focusCurrentTab(int index) {
		final int oldTab = mSelectedTab;

		// set the tab
		setCurrentTab(index);

		// change the focus if applicable.
		if (oldTab != index) {
			getChildTabViewAt(index).requestFocus();
		}
	}

	@Override
	public void setEnabled(boolean enabled) {
		super.setEnabled(enabled);
		int count = getTabCount();

		for (int i = 0; i < count; i++) {
			View child = getChildTabViewAt(i);
			child.setEnabled(enabled);
		}
	}

	@SuppressWarnings("deprecation")
	@Override
	public void addView(View child) {
		if (child.getLayoutParams() == null) {
			final LayoutParams lp = new LayoutParams(0,
					ViewGroup.LayoutParams.MATCH_PARENT, 1.0f);
			lp.setMargins(0, 0, 0, 0);
			child.setLayoutParams(lp);
		}

		// Ensure you can navigate to the tab with the keyboard, and you can
		// touch it
		child.setFocusable(true);
		child.setClickable(true);

		// If we have dividers between the tabs and we already have at least one
		// tab, then add a divider before adding the next tab.
		if (mDividerDrawable != null && getTabCount() > 0) {
			ImageView divider = new ImageView(mContext);
			final LayoutParams lp = new LayoutParams(
					mDividerDrawable.getIntrinsicWidth(),
					LayoutParams.MATCH_PARENT);
			lp.setMargins(0, 0, 0, 0);
			divider.setLayoutParams(lp);
			divider.setBackgroundDrawable(mDividerDrawable);
			super.addView(divider);
		}
		super.addView(child);

		// TODO: detect this via geometry with a tabwidget listener rather
		// than potentially interfere with the view's listener
		child.setOnClickListener(new TabClickListener(getTabCount() - 1));
		child.setOnFocusChangeListener(this);
	}

	/***
	 * Provides a way for {@linkabHost} to be notified that the user clicked
	 * on a tab indicator.
	 */
	public void setOnTabSelectionListener(OnTabSelectionChanged listener) {
		mSelectionChangedListener = listener;
	}

	// registered with each tab indicator so we can notify tab host
	private class TabClickListener implements OnClickListener {

		private final int mTabIndex;

		private TabClickListener(int tabIndex) {
			mTabIndex = tabIndex;
		}

		public void onClick(View v) {
			setCurrentTab(mTabIndex);// 这一句是我自己加的，当点击tab时，需要当前view聚焦
			if (mSelectionChangedListener != null)
				mSelectionChangedListener
						.onTabSelectionChanged(mTabIndex, true);
		}
	}

	/***
	 * Let {@linkTabHost} know that the user clicked on a tab indicator.
	 */
	public static interface OnTabSelectionChanged {
		/***
		 * 通知tab被选中. It also indicates if the tab was clicked/pressed or just
		 * focused into.
		 * 
		 * @param tabIndex
		 *            index of the tab that was selected
		 * @param clicked
		 *            whether the selection changed due to a touch/click or due
		 *            to focus entering the tab through navigation. Pass true if
		 *            it was due to a press/click and false otherwise.
		 */
		void onTabSelectionChanged(int tabIndex, boolean clicked);
	}

}

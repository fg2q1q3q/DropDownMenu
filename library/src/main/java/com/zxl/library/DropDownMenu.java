package com.zxl.library;

import android.content.Context;
import android.content.res.TypedArray;
import android.support.annotation.NonNull;
import android.text.TextUtils;
import android.util.AttributeSet;
import android.util.DisplayMetrics;
import android.util.Log;
import android.util.TypedValue;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.AnimationUtils;
import android.widget.AdapterView;
import android.widget.FrameLayout;
import android.widget.GridView;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.TextView;

import java.util.Arrays;
import java.util.HashMap;
import java.util.List;


/**
 * Created by dongjunkun on 2015/6/17.
 */
public class DropDownMenu extends LinearLayout {

    //顶部菜单布局
    private LinearLayout tabMenuView;
    //底部容器，包含popupMenuViews，maskView
    private FrameLayout containerView;
    //弹出菜单父布局
    private FrameLayout popupMenuViews;
    //遮罩半透明View，点击可关闭DropDownMenu
    private View maskView;
    //tabMenuView里面选中的tab位置，-1表示未选中
    private int current_tab_position = -1;

    //分割线颜色
//    private int dividerColor = 0xffcccccc;
    //tab选中颜色
    private int textSelectedColor = 0xff890c85;
    //tab未选中颜色
    private int textUnselectedColor = 0xff111111;
    //遮罩颜色
    private int maskColor = 0x88888888;
    //tab字体大小
    private int menuTextSize = 14;
    //最大高度
    private int menuMaxHeight = -1;
    private boolean needSetSelectedColor = false;
    //tab选中图标
    private int menuSelectedIcon;
    //tab未选中图标
    private int menuUnselectedIcon;


    public DropDownMenu(Context context) {
        super(context, null);
    }

    public DropDownMenu(Context context, AttributeSet attrs) {
        this(context, attrs, 0);
    }

    public DropDownMenu(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);

        setOrientation(VERTICAL);

        //为DropDownMenu添加自定义属性
        int menuBackgroundColor = 0xffffffff;
        int underlineColor = 0xffcccccc;
        TypedArray a = context.obtainStyledAttributes(attrs, R.styleable.DropDownMenu);
        textSelectedColor = a.getColor(R.styleable.DropDownMenu_ddtextSelectedColor, textSelectedColor);
        needSetSelectedColor = a.getBoolean(R.styleable.DropDownMenu_ddneedSetSlectedColor, needSetSelectedColor);
        textUnselectedColor = a.getColor(R.styleable.DropDownMenu_ddtextUnselectedColor, textUnselectedColor);
        menuBackgroundColor = a.getColor(R.styleable.DropDownMenu_ddmenuBackgroundColor, menuBackgroundColor);
        maskColor = a.getColor(R.styleable.DropDownMenu_ddmaskColor, maskColor);
        menuTextSize = a.getDimensionPixelSize(R.styleable.DropDownMenu_ddmenuTextSize, menuTextSize);
        menuMaxHeight = a.getDimensionPixelSize(R.styleable.DropDownMenu_ddmenuMaxHeight, menuMaxHeight);
        menuSelectedIcon = a.getResourceId(R.styleable.DropDownMenu_ddmenuSelectedIcon, menuSelectedIcon);
        menuUnselectedIcon = a.getResourceId(R.styleable.DropDownMenu_ddmenuUnselectedIcon, menuUnselectedIcon);
        a.recycle();

        //初始化tabMenuView并添加到tabMenuView
        tabMenuView = new LinearLayout(context);
        LayoutParams params = new LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT);
        tabMenuView.setOrientation(HORIZONTAL);
        tabMenuView.setBackgroundColor(menuBackgroundColor);
        tabMenuView.setLayoutParams(params);
        addView(tabMenuView, 0);

        //为tabMenuView添加下划线
        View underLine = new View(getContext());
        underLine.setLayoutParams(new LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, dpTpPx(1.0f)));
        underLine.setBackgroundColor(underlineColor);
        addView(underLine, 1);

        //初始化containerView并将其添加到DropDownMenu
        containerView = new FrameLayout(context);
        containerView.setLayoutParams(new FrameLayout.LayoutParams(FrameLayout.LayoutParams.MATCH_PARENT, FrameLayout.LayoutParams.MATCH_PARENT));
        addView(containerView, 2);

    }

    public interface OnDefultMenuSelectListener {
        void onSelectDefaultMenu(int index, int pos, String clickstr);
    }

    private OnDefultMenuSelectListener lis;

    public void addMenuSelectListener(OnDefultMenuSelectListener lis) {
        this.lis = lis;
    }

    public static final String KEY = "type_key";
    public static final String VALUE = "type_value";
    public static final String SELECT_POSITION = "type_position";
    //一共包含四中类型：三种默认和自定义
    public static final int TYPE_LIST_CITY = 1;
    public static final int TYPE_LIST_SIMPLE = 2;
    public static final int TYPE_GRID = 3;
    public static final int TYPE_CUSTOM = 4;

    /**
     * 初始化DropDownMenu
     *
     * @param tabTexts    tab标签字符串集合
     * @param viewDatas   每个tab标签对应的类型和数据源
     * @param contentView 主页面view
     */
    public void setDropDownMenu(@NonNull List<String> tabTexts, @NonNull List<HashMap<String, Object>> viewDatas, @NonNull View contentView) {
        if (tabTexts.size() != viewDatas.size()) {
            throw new IllegalArgumentException("params not match, tabTexts.size() should be equal viewDatas.size()");
        }

        for (int i = 0; i < tabTexts.size(); i++) {
            addTab(tabTexts, i);
        }
        containerView.addView(contentView, 0);

        maskView = new View(getContext());
        maskView.setLayoutParams(new FrameLayout.LayoutParams(FrameLayout.LayoutParams.MATCH_PARENT, FrameLayout.LayoutParams.MATCH_PARENT));
        maskView.setBackgroundColor(maskColor);
        maskView.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View v) {
                ((TextView) tabMenuView.getChildAt(current_tab_position)).setTextColor(textUnselectedColor);
                closeMenu();
            }
        });
        containerView.addView(maskView, 1);
        maskView.setVisibility(GONE);

        popupMenuViews = new FrameLayout(getContext());
        popupMenuViews.setVisibility(GONE);
        containerView.addView(popupMenuViews, 2);
        View view = null;
        for (int i = 0; i < viewDatas.size(); i++) {
            HashMap<String, Object> map = viewDatas.get(i);
            int key = (int) map.get(KEY);
            Object value = map.get(VALUE);
            int select_position = -1;
            try {
                select_position = (int) map.get(SELECT_POSITION);
            } catch (Exception e) {
            }
            if (select_position != -1 && select_position < 0) {
                throw new IllegalArgumentException("the select_position must be >= 0");
            }
            Log.d("zxl", "***********" + select_position);
            switch (key) {
                case TYPE_LIST_CITY:
                    if (value instanceof String[] && select_position < ((String[]) value).length)
                        view = setCityListView((String[]) value, i, select_position);
                    else
                        throw new IllegalArgumentException("the type TYPE_LIST_CITY should mapping String[] and the select_position must be < array length");
                    break;
                case TYPE_LIST_SIMPLE:
                    if (value instanceof String[] && select_position < ((String[]) value).length)
                        view = setSimpleListView((String[]) value, i, select_position);
                    else
                        throw new IllegalArgumentException("the type TYPE_LIST_SIMPLE should mapping String[] and the select_position must be < array length");
                    break;
                case TYPE_GRID:
                    if (value instanceof String[] && select_position < ((String[]) value).length)
                        view = setGridView((String[]) value, i, select_position);
                    else
                        throw new IllegalArgumentException("the type TYPE_GRID should mapping String[] and the select_position must be < array length");
                    break;
                default:
                    if (value instanceof View)
                        view = (View) value;
                    else
                        throw new IllegalArgumentException("the type TYPE_CUSTOM should mapping View");
                    break;
            }
            view.setLayoutParams(new ViewGroup.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, menuMaxHeight == -1 ? ViewGroup.LayoutParams.WRAP_CONTENT : menuMaxHeight));
            popupMenuViews.addView(view, i);
        }

    }

    private View setCityListView(final String[] arr, final int index, int select_position) {
        ListView view = new ListView(getContext());
        view.setDividerHeight(0);
        final GirdDropDownAdapter adapter = new GirdDropDownAdapter(getContext(), Arrays.asList(arr));
        if (select_position != -1) {
            adapter.setCheckItem(select_position);
            setTabText(index, arr[select_position]);
        }
        view.setAdapter(adapter);
        view.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> adapterView, View view, int position, long l) {
                adapter.setCheckItem(position);
                setTabText(current_tab_position, arr[position]);
                closeMenu();
                lis.onSelectDefaultMenu(index, position, adapter.getItem(position));
            }
        });
        return view;
    }

    private View setSimpleListView(final String[] arr, final int index, int select_position) {
        ListView view = new ListView(getContext());
        view.setDividerHeight(0);
        final ListDropDownAdapter adapter = new ListDropDownAdapter(getContext(), Arrays.asList(arr));
        if (select_position != -1) {
            adapter.setCheckItem(select_position);
            setTabText(index, arr[select_position]);
        }
        view.setAdapter(adapter);
        view.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> adapterView, View view, int position, long l) {
                adapter.setCheckItem(position);
                setTabText(current_tab_position, arr[position]);
                closeMenu();
                lis.onSelectDefaultMenu(index, position, adapter.getItem(position));
            }
        });
        return view;
    }

    private View setGridView(final String[] arr, final int index, int select_position) {
        final ConstellationAdapter adapter = new ConstellationAdapter(getContext(), Arrays.asList(arr));
        LayoutInflater li = LayoutInflater.from(getContext());
        View v = li.inflate(R.layout.drop_menu_grid_layout, null);
        GridView grid = (GridView) v.findViewById(R.id.constellation);
        if (select_position != -1) {
            adapter.setCheckItem(select_position);
            setTabText(index, arr[select_position]);
        }
        grid.setAdapter(adapter);
        grid.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> adapterView, View view, int position, long l) {
                adapter.setCheckItem(position);
                setTabText(current_tab_position, arr[position]);
                closeMenu();
                lis.onSelectDefaultMenu(index, position, adapter.getItem(position));
            }
        });
        return v;
    }

    private void addTab(@NonNull List<String> tabTexts, int i) {
        final TextView tab = new TextView(getContext());
        tab.setSingleLine();
        tab.setEllipsize(TextUtils.TruncateAt.END);
        tab.setGravity(Gravity.CENTER);
        tab.setTextSize(TypedValue.COMPLEX_UNIT_PX, menuTextSize);
        tab.setLayoutParams(new LayoutParams(0, ViewGroup.LayoutParams.WRAP_CONTENT, 1.0f));
        tab.setTextColor(textUnselectedColor);
        tab.setCompoundDrawablesWithIntrinsicBounds(null, null, getResources().getDrawable(menuUnselectedIcon), null);
        tab.setText(tabTexts.get(i));
        tab.setPadding(dpTpPx(5), dpTpPx(12), dpTpPx(5), dpTpPx(12));
        //添加点击事件
        tab.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View v) {
                switchMenu(tab);
            }
        });
        tabMenuView.addView(tab);
        tabMenuView.setShowDividers(LinearLayout.SHOW_DIVIDER_MIDDLE);
        tabMenuView.setDividerDrawable(getResources().getDrawable(R.drawable.divider_line));
        //添加分割线
       /* if (i < tabTexts.size() - 1) {
            View view = new View(getContext());
            view.setLayoutParams(new LayoutParams(dpTpPx(0.5f), ViewGroup.LayoutParams.MATCH_PARENT));
            view.setBackgroundColor(dividerColor);
            tabMenuView.addView(view);
        }*/
    }

    /**
     * 改变tab文字
     *
     * @param text
     */
    public void setTabText(int tabIndex, String text) {
        if (tabIndex != -1) {
            if (needSetSelectedColor) {
                ((TextView) tabMenuView.getChildAt(tabIndex)).setTextColor(textSelectedColor);
            } else {
                ((TextView) tabMenuView.getChildAt(tabIndex)).setTextColor(textUnselectedColor);
            }
            ((TextView) tabMenuView.getChildAt(tabIndex)).setText(text);
        }
    }

    public void setTabClickable(boolean clickable) {
        for (int i = 0; i < tabMenuView.getChildCount(); i = i + 2) {
            tabMenuView.getChildAt(i).setClickable(clickable);
        }
    }

    /**
     * 关闭菜单
     */
    public void closeMenu() {
        if (current_tab_position != -1) {
            ((TextView) tabMenuView.getChildAt(current_tab_position)).setCompoundDrawablesWithIntrinsicBounds(null, null,
                    getResources().getDrawable(menuUnselectedIcon), null);
            popupMenuViews.setVisibility(View.GONE);
            popupMenuViews.setAnimation(AnimationUtils.loadAnimation(getContext(), R.anim.dd_menu_out));
            maskView.setVisibility(GONE);
            maskView.setAnimation(AnimationUtils.loadAnimation(getContext(), R.anim.dd_mask_out));
            current_tab_position = -1;
        }

    }

    /**
     * DropDownMenu是否处于可见状态
     *
     * @return
     */
    public boolean isShowing() {
        return current_tab_position != -1;
    }

    /**
     * 切换菜单
     *
     * @param target
     */
    private void switchMenu(View target) {
        for (int i = 0; i < tabMenuView.getChildCount(); i++) {
            if (target == tabMenuView.getChildAt(i)) {
                if (current_tab_position == i) {
                    closeMenu();
                } else {
                    if (current_tab_position == -1) {
                        popupMenuViews.setVisibility(View.VISIBLE);
                        popupMenuViews.setAnimation(AnimationUtils.loadAnimation(getContext(), R.anim.dd_menu_in));
                        maskView.setVisibility(VISIBLE);
                        maskView.setAnimation(AnimationUtils.loadAnimation(getContext(), R.anim.dd_mask_in));
                        popupMenuViews.getChildAt(i).setVisibility(View.VISIBLE);
                    } else {
                        popupMenuViews.getChildAt(i).setVisibility(View.VISIBLE);
                    }
                    current_tab_position = i;
                    ((TextView) tabMenuView.getChildAt(i)).setTextColor(textSelectedColor);
                    ((TextView) tabMenuView.getChildAt(i)).setCompoundDrawablesWithIntrinsicBounds(null, null,
                            getResources().getDrawable(menuSelectedIcon), null);
                }
            } else {
//                ((TextView) tabMenuView.getChildAt(i)).setTextColor(textUnselectedColor);
                ((TextView) tabMenuView.getChildAt(i)).setCompoundDrawablesWithIntrinsicBounds(null, null,
                        getResources().getDrawable(menuUnselectedIcon), null);
                popupMenuViews.getChildAt(i).setVisibility(View.GONE);
            }
        }
    }

    public int dpTpPx(float value) {
        DisplayMetrics dm = getResources().getDisplayMetrics();
        return (int) (TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP, value, dm) + 0.5);
    }
}

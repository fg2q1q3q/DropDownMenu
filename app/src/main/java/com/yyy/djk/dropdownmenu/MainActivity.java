package com.yyy.djk.dropdownmenu;

import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.TextView;
import android.widget.Toast;

import com.zxl.library.DropDownMenu;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;


public class MainActivity extends AppCompatActivity {

    DropDownMenu mDropDownMenu;
    private String headers[] = {"城市", "年龄", "性别", "星座"};
    private int[] types = new int[]{DropDownMenu.TYPE_LIST_CITY, DropDownMenu.TYPE_LIST_SIMPLE, DropDownMenu.TYPE_CUSTOM, DropDownMenu.TYPE_GRID};
    private String citys[] = {"不限", "武汉", "北京", "上海", "成都", "广州", "深圳", "重庆", "天津", "西安", "南京", "杭州"};
    private String ages[] = {"不限", "18岁以下", "18-22岁", "23-26岁", "27-35岁", "35岁以上"};
    private String constellations[] = {"不限", "白羊座", "金牛座", "双子座", "巨蟹座", "狮子座", "处女座", "天秤座", "天蝎座", "射手座", "摩羯座", "水瓶座", "双鱼座"};

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        mDropDownMenu= (DropDownMenu) findViewById( R.id.dropDownMenu);
        initView();
    }

    private void initView() {
        View contentView = getLayoutInflater().inflate(R.layout.contentview, null);
        mDropDownMenu.setDropDownMenu(Arrays.asList(headers), initViewData(), contentView);
        //该监听回调只监听默认类型，如果是自定义view请自行设置，参照demo
        mDropDownMenu.addMenuSelectListener(new DropDownMenu.OnDefultMenuSelectListener() {
            @Override
            public void onSelectDefaultMenu(int index, int pos, String clickstr) {
                //index:点击的tab索引，pos：单项菜单中点击的位置索引，clickstr：点击位置的字符串
                Toast.makeText(getBaseContext(), clickstr, Toast.LENGTH_SHORT).show();
            }
        });
    }

    /**
     * 设置类型和数据源：
     * DropDownMenu.KEY对应类型（DropDownMenu中的常量，参考上述核心源码）
     * DropDownMenu.VALUE对应数据源：key不是TYPE_CUSTOM则传递string[],key是TYPE_CUSTOM类型则传递对应view
     */
    private List<HashMap<String, Object>> initViewData() {
        List<HashMap<String, Object>> viewDatas = new ArrayList<>();
        HashMap<String, Object> map;
        for (int i = 0; i < headers.length; i++) {
            map = new HashMap<String, Object>();
            map.put(DropDownMenu.KEY, types[i]);
            switch (types[i]) {
                case DropDownMenu.TYPE_LIST_CITY:
                    map.put(DropDownMenu.VALUE, citys);
                    map.put(DropDownMenu.SELECT_POSITION,2);
                    break;
                case DropDownMenu.TYPE_LIST_SIMPLE:
                    map.put(DropDownMenu.VALUE, ages);
                    map.put(DropDownMenu.SELECT_POSITION,5);
                    break;
                case DropDownMenu.TYPE_GRID:
                    map.put(DropDownMenu.VALUE, constellations);
                    break;
                default:
                    map.put(DropDownMenu.VALUE, getCustomView());
                    break;
            }
            viewDatas.add(map);
        }
        return viewDatas;
    }

    private View getCustomView() {
        View v = getLayoutInflater().inflate(R.layout.custom, null);
        TextView btn = (TextView) v.findViewById(R.id.btn);
        btn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                mDropDownMenu.setTabText(2,"自定义");//设置tab标签文字
                mDropDownMenu.closeMenu();//关闭menu
            }
        });
        return v;
    }

    @Override
    public void onBackPressed() {
        //退出activity前关闭菜单
        if (mDropDownMenu.isShowing()) {
            mDropDownMenu.closeMenu();
        } else {
            super.onBackPressed();
        }
    }
}

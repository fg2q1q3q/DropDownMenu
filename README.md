## 简介
本例是在[DropDownMenu](http://www.jianshu.com/p/d9407f799d2d)基础上改进而来
##相比而言增加如下特性：
 - 增加三种默认样式类型：TYPE_LIST_CITY，TYPE_LIST_SIMPLE，TYPE_GRID
 - 增加默认样式点击回调
 - 修改传参，更易于理解和使用
 - 增加最大高度限制属性
 - 如果你使用默认样式，只需要传递字符串数组即可，如果使用自定义样式只需传递customview即可


##以下为原始特性
 - 支持多级菜单
 - 你可以完全自定义你的菜单样式，我这里只是封装了一些实用的方法，Tab的切换效果，菜单显示隐藏效果等
 - 并非用popupWindow实现，无卡顿

##ScreenShot
<img src="https://github.com/fg2q1q3q/DropDownMenu/blob/master/art/d.gif?raw=true"/>

##Gradle
    compile 'com.github.dongjunkun:DropDownMenu:1.0.3'
使用相关核心源码及参数说明：

```
//一共包含四种类型：三种默认类型和自定义
    public static final int TYPE_LIST_CITY = 1;
    public static final int TYPE_LIST_SIMPLE = 2;
    public static final int TYPE_GRID = 3;
    public static final int TYPE_CUSTOM = 4;
    /**
     * 初始化DropDownMenu
     * @param tabTexts tab标签字符串集合
     * @param viewDatas 每个tab标签对应的类型和数据源
     * @param contentView 主页面view
     */
    public void setDropDownMenu(@NonNull List<String> tabTexts, @NonNull List<HashMap<String,Object>> viewDatas, @NonNull View contentView){}
```
##使用教程
添加DropDownMenu 到你的布局文件，如下
```
<com.yyydjk.library.DropDownMenu
    android:id="@+id/dropDownMenu"
    android:layout_width="match_parent"
    android:layout_height="match_parent"
    app:ddmenuTextSize="13sp" //tab字体大小
    app:ddtextUnselectedColor="@color/drop_down_unselected" //tab未选中颜色
    app:ddtextSelectedColor="@color/drop_down_selected" //tab选中颜色
    app:dddividerColor="@color/gray"    //分割线颜色
    app:ddunderlineColor="@color/gray"  //下划线颜色
    app:ddmenuSelectedIcon="@mipmap/drop_down_selected_icon" //tab选中状态图标
    app:ddmenuUnselectedIcon="@mipmap/drop_down_unselected_icon"//tab未选中状态图标
    app:ddmaskColor="@color/mask_color"     //遮罩颜色，一般是半透明
    app:ddmenuBackgroundColor="@color/white" //tab 背景颜色
    app:ddmenuMaxHeight="280dp"//筛选菜单最大高度，默认为wrap
    ...
 />
```
java代码中设置如下：
```
        View contentView = getLayoutInflater().inflate(R.layout.contentview, null);//主页面view
        mDropDownMenu.setDropDownMenu(Arrays.asList(headers), initViewData(), contentView);
        //该监听回调只监听默认类型，如果是自定义view请自行设置监听，参照demo
        mDropDownMenu.addMenuSelectListener(new DropDownMenu.OnDefultMenuSelectListener() {
            @Override
            public void onSelectDefaultMenu(int index, int pos,String clickstr) {
                //index:点击的tab索引，pos：单项菜单中点击的位置索引，clickstr：点击位置的字符串
                Toast.makeText(getBaseContext(),clickstr,Toast.LENGTH_SHORT).show();
            }
        });
    /* 关于initViewData设置类型和数据源的说明
     * DropDownMenu.KEY对应类型（DropDownMenu中的常量，参考上述核心源码）
     * DropDownMenu.VALUE对应数据源：key不是TYPE_CUSTOM则传递string[],key是TYPE_CUSTOM类型则传递对应view
     */
```
详情可参考demo，[点此下载](https://www.pgyer.com/VGSx)或扫描二维码
![demo下载][1]
##联系我
如有疑问请提issue或QQ群：137824028


  [1]: https://raw.githubusercontent.com/fg2q1q3q/DropDownMenu/master/art/VGSx.png
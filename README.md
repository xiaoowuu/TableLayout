##### 在编辑模式下预览，可以看出修改不同属性产生的变化
![编辑模式预览](http://upload-images.jianshu.io/upload_images/1951791-2dc4834072e6421b.gif?imageMogr2/auto-orient/strip)

##### 属性表

attr|meaning|defaultValue|备注
---|---|---|---
tableRowHeight|单元格的高度|36dp|
tableDividerSize|分割线大小|1px|
tableDividerColor|分割线颜色|Color.GRAY|
tableColumnPadding|单元格左右padding|0|
tableTextGravity|单元格对齐方式|center|可选center/leftCenter/rightCenter
tableTextSize|字体大小|12dp|
tableTextColor|文字颜色|Color.GRAY|
tableTextColorSelected|选中后文字颜色|Color.BLACK|
backgroundColorSelected|单元格选中后的背景色|Color.TRANSPARENT|

##### 使用方法
- 在GitHub上检出此项目，将tableLayout这个library module导入到项目中，并在application module中添加对tableLayout的依赖
```
dependencies {
    compile project(':tableLayout')
}
```
- 在xml中定义表格基础样式
```
    <win.smartown.android.library.tableLayout.TableLayout
        android:id="@+id/main_table"
        android:layout_width="wrap_content"
        android:layout_height="wrap_content"
        android:background="@android:color/white"
        app:backgroundColorSelected="@color/colorAccent"
        app:tableColumnPadding="32dp"
        app:tableDividerColor="#ddd"
        app:tableDividerSize="1px"
        app:tableRowHeight="48dp"
        app:tableTextColor="#333"
        app:tableTextColorSelected="#fff"
        app:tableTextSize="14dp" />
```
- 在Java代码中填充展示数据
```
        TableLayout tableLayout = (TableLayout) findViewById(R.id.main_table);

        contentList = new ArrayList<>();
        contentList.add(new Content("姓名", "语文", "数学", "英语", "物理", "化学", "生物"));
        contentList.add(new Content("张三", newRandomNumber(), newRandomNumber(), newRandomNumber(), newRandomNumber(), newRandomNumber(), newRandomNumber()));
        contentList.add(new Content("李四", newRandomNumber(), newRandomNumber(), newRandomNumber(), newRandomNumber(), newRandomNumber(), newRandomNumber()));
        contentList.add(new Content("王二", newRandomNumber(), newRandomNumber(), newRandomNumber(), newRandomNumber(), newRandomNumber(), newRandomNumber()));
        contentList.add(new Content("王尼玛", newRandomNumber(), newRandomNumber(), newRandomNumber(), newRandomNumber(), newRandomNumber(), newRandomNumber()));
        contentList.add(new Content("张全蛋", newRandomNumber(), newRandomNumber(), newRandomNumber(), newRandomNumber(), newRandomNumber(), newRandomNumber()));
        contentList.add(new Content("赵铁柱", newRandomNumber(), newRandomNumber(), newRandomNumber(), newRandomNumber(), newRandomNumber(), newRandomNumber()));

        tableLayout.setAdapter(new TableAdapter() {
            @Override
            public int getColumnCount() {
                return contentList.size();
            }

            @Override
            public String[] getColumnContent(int position) {
                return contentList.get(position).toArray();
            }
        });
```
 - 现在运行就可以看到下图的效果了

![Demo](http://upload-images.jianshu.io/upload_images/1951791-a33a0c93fd76ec89.gif?imageMogr2/auto-orient/strip)

##### 部分源码分析
这里由内而外的分析，从基础的单元开始。
- win.smartown.android.library.tableLayout.TableColumn
   这个类表示表格中的一列，比较关键的点在于根据填充到此列的数据来确定此列的宽度

  1.根据填充内容确定一个单元格（TextView）显示这些文本要占用的宽度：
  ```
    // 计算出该TextView中文字的长度(像素)
    public static float measureTextViewWidth(TextView textView, String text) {
        // 得到使用该paint写上text的时候,像素为多少
        return textView.getPaint().measureText(text);
    }
  ```
  2.遍历此列中所有的单元格，得到最大单元格的宽度maxTextViewWidth ，将其作为此列的宽度
  ```
    private void initContent() {
        int padding = callback.getTableLayout().getTableColumnPadding();
        maxTextViewWidth = 0;
        ArrayList<TextView> textViews = new ArrayList<>();
        for (String text : content) {
            TextView textView = new TextView(getContext());
            textView.setTextSize(TypedValue.COMPLEX_UNIT_PX, callback.getTableLayout().getTableTextSize());
            textView.setTextColor(callback.getTableLayout().getTableTextColor());
            maxTextViewWidth = Math.max(maxTextViewWidth, Util.measureTextViewWidth(textView, text));
            textView.setGravity(getTextGravity(callback.getTableLayout().getTableTextGravity()));
            textView.setPadding(padding, 0, padding, 0);
            textView.setText(text);
            textViews.add(textView);
        }
        LayoutParams layoutParams = new LayoutParams((int) (padding * 2 + maxTextViewWidth), callback.getTableLayout().getTableRowHeight());
        for (TextView textView : textViews) {
            addView(textView, layoutParams);
        }
    }
  
    @Override
    protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
        super.onMeasure(widthMeasureSpec, heightMeasureSpec);
        setMeasuredDimension((int) (callback.getTableLayout().getTableColumnPadding() * 2 + maxTextViewWidth), callback.getTableLayout().getTableRowHeight() * getChildCount());
    }

  ```

- win.smartown.android.library.tableLayout.TableLayout
  TableLayout就是最终呈现的完整表格，实际上他就是多个TableColumn的组合，其主要负责整个表格的大小测量、分割线绘制和接受数据填充。
   1.单元格大小测量
  ```
    @Override
    protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
        super.onMeasure(widthMeasureSpec, heightMeasureSpec);
        int width = 0;
        int height = 0;
        int childCount = getChildCount();
        for (int i = 0; i < childCount; i++) {
            View child = getChildAt(i);
            width += child.getMeasuredWidth();
            height = Math.max(height, child.getMeasuredHeight());
        }
        setMeasuredDimension(width, height);
    }
  ```
  2.绘制分割线

  > 在ViewGroup要重写onDraw()，需要设置setWillNotDraw(false)，否者onDown()中的绘制不会生效，具体的分割线绘制参见TableLayout源码的onDraw()；

  3.数据的填充
  ```
    public void setAdapter(TableAdapter adapter) {
        this.adapter = adapter;
        useAdapter();
    }
    
    //设置adapter后，先清空原来的数据，然后根据新数据添加TableColumn
    private void useAdapter() {
        removeAllViews();
        int count = adapter.getColumnCount();
        for (int i = 0; i < count; i++) {
            addView(new TableColumn(getContext(), adapter.getColumnContent(i), this));
        }
    }
  ```

- win.smartown.android.library.tableLayout.FreeScrollView
  顾名思义，此类用来实现子View的自用滚动，当子view大小超过FreeScrollView的大小，就可以拖动显示超出的内容
  1.处理滚动
  ```
            @Override from GestureDetector （重写GestureDetector 的onScroll()）
            public boolean onScroll(MotionEvent e1, MotionEvent e2, float distanceX, float distanceY) {
                View view = getChildAt(0);
                int childHeight = view.getHeight();
                int childWidth = view.getWidth();
                int toX, toY;
                if (distanceX > 0) {
                    if (childWidth > getWidth()) {
                        if (getScrollX() + getWidth() >= childWidth) {
                            toX = childWidth - getWidth();
                        } else {
                            toX = (int) (getScrollX() + distanceX);
                        }
                    } else {
                        toX = 0;
                    }
                } else {
                    if (getScrollX() + distanceX < 0) {
                        toX = 0;
                    } else {
                        toX = (int) (getScrollX() + distanceX);
                    }
                }
                if (distanceY > 0) {
                    if (childHeight > getHeight()) {
                        if (getScrollY() + getHeight() >= childHeight) {
                            toY = childHeight - getHeight();
                        } else {
                            toY = (int) (getScrollY() + distanceY);
                        }
                    } else {
                        toY = 0;
                    }
                } else {
                    if (getScrollY() + distanceY < 0) {
                        toY = 0;
                    } else {
                        toY = (int) (getScrollY() + distanceY);
                    }
                }
                scrollTo(toX, toY);
                return false;
            }
  ```
  2.处理点击事件，达到选中效果
  ```
            //由于FreeScrollView拦截了TouchEvent，所以要在FreeScrollView处理点击事件，
            //通过计算坐标来定位点击的是哪个单元格，点击处理顺序：
            //FreeScrollView.onSingleTapUp() -> TableLayout.onClick()  -> TableLayout.onClick() -> TableColumn.onClick()
            @Override from GestureDetector 
            public boolean onSingleTapUp(MotionEvent e) {
                View view = getChildAt(0);
                if (view instanceof TableLayout) {
                    ((TableLayout) view).onClick(e.getX() + getScrollX(), e.getY() + getScrollY());
                }
                return false;
            }
  ```

##### Github
- [TableLayout](https://github.com/smartown/TableLayout)
- [TableView](https://github.com/smartown/TableView)

![TableView](http://upload-images.jianshu.io/upload_images/1951791-53e9b9f852e53ede.png?imageMogr2/auto-orient/strip%7CimageView2/2/w/1240)
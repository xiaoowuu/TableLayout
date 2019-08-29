package win.smartown.android.library.tableLayout;

import android.annotation.TargetApi;
import android.content.Context;
import android.content.res.TypedArray;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.os.Build;
import android.util.AttributeSet;
import android.util.Log;
import android.view.MotionEvent;
import android.view.View;
import android.widget.LinearLayout;

/**
 * Created by Smartown on 2017/7/19.
 */
public class TableLayout extends LinearLayout implements TableColumn.Callback {

    private int tableMode;
    private int tableRowHeight;
    private int tableDividerSize;
    private int tableDividerColor;
    private int tableColumnPadding;
    private int tableTextGravity;
    private int tableTextSize;
    private int tableTextColor;
    private int tableTextColorSelected;
    private int backgroundColorSelected;
    private TableAdapter adapter;

    private Paint paint;

    public TableLayout(Context context) {
        super(context);
        init(null);
    }

    public TableLayout(Context context, AttributeSet attrs) {
        super(context, attrs);
        init(attrs);
    }

    @TargetApi(Build.VERSION_CODES.HONEYCOMB)
    public TableLayout(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        init(attrs);
    }

    @TargetApi(Build.VERSION_CODES.LOLLIPOP)
    public TableLayout(Context context, AttributeSet attrs, int defStyleAttr, int defStyleRes) {
        super(context, attrs, defStyleAttr, defStyleRes);
        init(attrs);
    }

    private void init(AttributeSet attrs) {
        Log.i("TableLayout", "init");
        setOrientation(HORIZONTAL);
        setWillNotDraw(false);
        paint = new Paint();
        paint.setAntiAlias(true);

        if (attrs != null) {
            TypedArray typedArray = getResources().obtainAttributes(attrs, R.styleable.TableLayout);
            tableMode = typedArray.getInt(R.styleable.TableLayout_tableMode, 0);
            tableRowHeight = typedArray.getDimensionPixelSize(R.styleable.TableLayout_tableRowHeight, (int) Util.dip2px(getResources(), 36));
            tableDividerSize = typedArray.getDimensionPixelSize(R.styleable.TableLayout_tableDividerSize, 1);
            tableDividerColor = typedArray.getColor(R.styleable.TableLayout_tableDividerColor, Color.GRAY);
            tableColumnPadding = typedArray.getDimensionPixelSize(R.styleable.TableLayout_tableColumnPadding, 0);
            tableTextGravity = typedArray.getInt(R.styleable.TableLayout_tableTextGravity, 0);
            tableTextSize = typedArray.getDimensionPixelSize(R.styleable.TableLayout_tableTextSize, (int) Util.dip2px(getResources(), 12));
            tableTextColor = typedArray.getColor(R.styleable.TableLayout_tableTextColor, Color.GRAY);
            tableTextColorSelected = typedArray.getColor(R.styleable.TableLayout_tableTextColorSelected, Color.BLACK);
            backgroundColorSelected = typedArray.getColor(R.styleable.TableLayout_backgroundColorSelected, Color.TRANSPARENT);
            typedArray.recycle();
        } else {
            tableMode = 0;
            tableRowHeight = (int) Util.dip2px(getResources(), 36);
            tableDividerSize = 1;
            tableDividerColor = Color.GRAY;
            tableColumnPadding = 0;
            tableTextGravity = 0;
            tableTextSize = (int) Util.dip2px(getResources(), 12);
            tableTextColor = Color.GRAY;
            tableTextColorSelected = Color.BLACK;
            backgroundColorSelected = Color.TRANSPARENT;
        }
        if (isInEditMode()) {
            String[] content = {"a", "aa", "aaa", "aaaa", "aaaaa", "aaaaaa", "aaaaaaa", "aaaaaaaa"};
            addView(new TableColumn(getContext(), content, this));
            addView(new TableColumn(getContext(), content, this));
            addView(new TableColumn(getContext(), content, this));
            addView(new TableColumn(getContext(), content, this));
            addView(new TableColumn(getContext(), content, this));
            addView(new TableColumn(getContext(), content, this));
            addView(new TableColumn(getContext(), content, this));
        }
    }

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

    @Override
    public boolean onInterceptTouchEvent(MotionEvent ev) {
        return true;
    }

    @Override
    protected void onDraw(Canvas canvas) {
        super.onDraw(canvas);
        paint.setColor(tableDividerColor);
        int drawnWidth = 0;
        int maxRowCount = 0;
        int childCount = getChildCount();
        for (int i = 0; i < childCount; i++) {
            TableColumn column = (TableColumn) getChildAt(i);
            maxRowCount = Math.max(maxRowCount, column.getChildCount());
            if (i > 0) {
                if (tableDividerSize > 1) {
                    canvas.drawRect(drawnWidth - tableDividerSize / 2, 0, drawnWidth + tableDividerSize / 2, getHeight(), paint);
                } else {
                    canvas.drawRect(drawnWidth - tableDividerSize, 0, drawnWidth, getHeight(), paint);
                }
            }
            drawnWidth += column.getWidth();
        }
        for (int i = 1; i < maxRowCount; i++) {
            float y = i * tableRowHeight;
            if (tableDividerSize > 1) {
                canvas.drawRect(0, y - tableDividerSize / 2, getWidth(), y + tableDividerSize / 2, paint);
            } else {
                canvas.drawRect(0, y - tableDividerSize, getWidth(), y, paint);
            }
        }
        canvas.drawRect(0, 0, tableDividerSize, getHeight(), paint);
        canvas.drawRect(getWidth() - tableDividerSize, 0, getWidth(), getHeight(), paint);
        canvas.drawRect(0, 0, getWidth(), tableDividerSize, paint);
        canvas.drawRect(0, getHeight() - tableDividerSize, getWidth(), getHeight(), paint);
    }

    @Override
    public TableLayout getTableLayout() {
        return this;
    }

    public int getTableMode() {
        return tableMode;
    }

    public int getTableRowHeight() {
        return tableRowHeight;
    }

    public int getTableDividerSize() {
        return tableDividerSize;
    }

    public int getTableDividerColor() {
        return tableDividerColor;
    }

    public int getTableColumnPadding() {
        return tableColumnPadding;
    }

    public int getTableTextGravity() {
        return tableTextGravity;
    }

    public int getTableTextSize() {
        return tableTextSize;
    }

    public int getTableTextColor() {
        return tableTextColor;
    }

    public int getTableTextColorSelected() {
        return tableTextColorSelected;
    }

    public int getBackgroundColorSelected() {
        return backgroundColorSelected;
    }

    public void setAdapter(TableAdapter adapter) {
        this.adapter = adapter;
        useAdapter();
    }

    private void useAdapter() {
        removeAllViews();
        int count = adapter.getColumnCount();
        for (int i = 0; i < count; i++) {
            addView(new TableColumn(getContext(), adapter.getColumnContent(i), this));
        }
    }

    public void onClick(float x, float y) {
        int childCount = getChildCount();
        for (int i = 0; i < childCount; i++) {
            TableColumn tableColumn = (TableColumn) getChildAt(i);
            if (tableColumn.getRight() >= x) {
                if (i==0){
                    return;
                }
                tableColumn.onClick(y);
                return;
            }
        }
    }
}

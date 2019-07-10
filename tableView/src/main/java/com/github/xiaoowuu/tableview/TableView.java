package com.github.xiaoowuu.tableview;

import android.content.Context;
import android.util.AttributeSet;
import android.util.SparseArray;
import android.util.SparseIntArray;
import android.view.GestureDetector;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.Nullable;

import java.util.Locale;


/**
 * @author xiaoowuu
 */
public class TableView extends ViewGroup implements GestureDetector.OnGestureListener {

    private boolean fixFirstRow = true;
    private boolean fixFirstColumn = true;
    private Adapter adapter;

    private int scrollX, scrollY;
    private GestureDetector gestureDetector;

    private SparseArray<SparseArray<View>> views = new SparseArray<>();

    private int contentWidth, contentHeight;

    public TableView(Context context) {
        super(context);
        init();
    }

    public TableView(Context context, @Nullable AttributeSet attrs) {
        super(context, attrs);
        init();
    }

    public TableView(Context context, @Nullable AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        init();
    }

    private void init() {
        gestureDetector = new GestureDetector(getContext(), this);
        setAdapter(new PreviewAdapter());
    }

    @Override
    protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
        if (adapter == null) {
            super.onMeasure(widthMeasureSpec, heightMeasureSpec);
            return;
        }
        int widthSize = MeasureSpec.getSize(widthMeasureSpec);
        int heightSize = MeasureSpec.getSize(heightMeasureSpec);

        setMeasuredDimension(Math.min(widthSize, contentWidth), Math.min(heightSize, contentHeight));
    }

    private void initMeasure() {
        int columnWeightSum = 0;
        int rowWeightSum = 0;

        SparseIntArray widthArray = new SparseIntArray();
        SparseIntArray heightArray = new SparseIntArray();

        for (int column = 0; column < adapter.getColumnCount(); column++) {
            columnWeightSum += adapter.getCellWidthWeight(column);
            for (int row = 0; row < adapter.getRowCount(); row++) {
                if (column == 0) {
                    rowWeightSum += adapter.getCellHeightWeight(row);
                }
                View view = getCellView(getContext(), column, row);
                view.measure(
                        view.getLayoutParams().width > 0 ?
                                MeasureSpec.makeMeasureSpec(view.getLayoutParams().width, MeasureSpec.EXACTLY) : MeasureSpec.makeMeasureSpec(0, MeasureSpec.UNSPECIFIED),
                        view.getLayoutParams().height > 0 ?
                                MeasureSpec.makeMeasureSpec(view.getLayoutParams().height, MeasureSpec.EXACTLY) : MeasureSpec.makeMeasureSpec(0, MeasureSpec.UNSPECIFIED));

                widthArray.put(column, Math.max(widthArray.get(column, 0), view.getMeasuredWidth()));
                heightArray.put(row, Math.max(heightArray.get(row, 0), view.getMeasuredHeight()));
                System.out.println(String.format(Locale.getDefault(), "width:%d,height:%d", view.getMeasuredWidth(), view.getMeasuredHeight()));
            }
        }

        contentWidth = 0;
        contentHeight = 0;
        for (int column = 0; column < adapter.getColumnCount(); column++) {
            int width = widthArray.get(column);
            contentWidth += width;
            for (int row = 0; row < adapter.getRowCount(); row++) {
                int height = heightArray.get(row);
                if (column == 0) {
                    contentHeight += height;
                }
                View view = getCellView(getContext(), column, row);
                view.measure(MeasureSpec.makeMeasureSpec(width, MeasureSpec.EXACTLY), MeasureSpec.makeMeasureSpec(height, MeasureSpec.EXACTLY));
            }
        }
    }


    @Override
    protected void onLayout(boolean changed, int l, int t, int r, int b) {
        if (adapter == null) {
            return;
        }
        int drawnWidth = 0;
        for (int column = 0; column < adapter.getColumnCount(); column++) {
            int width = 0;
            int drawnHeight = 0;
            for (int row = 0; row < adapter.getRowCount(); row++) {
                View view = getCellView(getContext(), column, row);
                width = view.getMeasuredWidth();
                int height = view.getMeasuredHeight();
                int left = 0;
                if (fixFirstColumn && column == 0) {
                } else {
                    left = drawnWidth - scrollX;
                }
                int top = 0;
                if (fixFirstRow && row == 0) {
                } else {
                    top = drawnHeight - scrollY;
                }
                int right = left + width;
                int bottom = top + height;

                if (left > getMeasuredWidth() || right < 0 || top > getMeasuredHeight() || bottom < 0) {
                    view.setVisibility(GONE);
                } else {
                    view.setVisibility(VISIBLE);
                    view.layout(left, top, right, bottom);
                }

                drawnHeight += height;
            }
            drawnWidth += width;
        }
    }

    @Override
    public boolean dispatchTouchEvent(MotionEvent ev) {
        gestureDetector.onTouchEvent(ev);
        return super.dispatchTouchEvent(ev);
    }

    @Override
    public boolean onInterceptTouchEvent(MotionEvent ev) {
        if (ev.getAction() == MotionEvent.ACTION_MOVE) {
            return true;
        }
        return super.onInterceptTouchEvent(ev);
    }

    public void setAdapter(Adapter adapter) {
        this.adapter = adapter;
        adapter.table = this;
        notifyAllCellChanged();
    }

    public View getCellView(Context context, int x, int y) {
        View view = null;
        SparseArray<View> array = views.get(x);
        if (array == null) {
            array = new SparseArray<>();
        } else {
            view = array.get(y);
        }
        if (view == null) {
            view = adapter.createCellView(context, x, y);
            array.put(y, view);
            views.put(x, array);
        }
        return view;
    }

    public void notifyAllCellChanged() {
        views.clear();
        initMeasure();
    }

    public void notifyCellChanged(int column, int row) {
        SparseArray<View> array = views.get(column);
        if (array != null) {
            View oldView = array.get(row);
            removeView(oldView);
            View view = adapter.createCellView(getContext(), column, row);
            view.measure(MeasureSpec.makeMeasureSpec(oldView.getMeasuredWidth(), MeasureSpec.EXACTLY), MeasureSpec.makeMeasureSpec(oldView.getMeasuredHeight(), MeasureSpec.EXACTLY));
            array.setValueAt(row, view);
        }
    }

    @Override
    public boolean onDown(MotionEvent e) {
        return true;
    }

    @Override
    public void onShowPress(MotionEvent e) {

    }

    @Override
    public boolean onSingleTapUp(MotionEvent e) {
        return false;
    }

    @Override
    public boolean onScroll(MotionEvent e1, MotionEvent e2, float distanceX, float distanceY) {
        if (e1 == null || e2 == null) {
            return true;
        }
        int maxScrollX = contentWidth - getWidth();
        int maxScrollY = contentHeight - getHeight();

        scrollX += distanceX;
        scrollY += distanceY;

        if (scrollX < 0) {
            scrollX = 0;
        } else if (scrollX > maxScrollX) {
            scrollX = maxScrollX;
        }
        if (scrollY < 0) {
            scrollY = 0;
        } else if (scrollY > maxScrollY) {
            scrollY = maxScrollY;
        }
        requestLayout();
        return false;
    }

    @Override
    public void onLongPress(MotionEvent e) {

    }

    @Override
    public boolean onFling(MotionEvent e1, MotionEvent e2, float velocityX, float velocityY) {
        return false;
    }

    public static abstract class Adapter {

        private TableView table;

        public TableView getTable() {
            return table;
        }

        public abstract int getColumnCount();

        public abstract int getRowCount();

        public abstract int getCellWidthWeight(int column);

        public abstract int getCellHeightWeight(int row);

        public abstract View createCellView(Context context, int column, int row);

    }

    private static class PreviewAdapter extends Adapter implements OnClickListener {

        private String[][] data = {
                {"阿萨斯大所多", "阿萨德的发", "嘘嘘", "阿斯达撒旦阿达萨达", "本色股份大声道", "水电费跟我说", "东方故事东方大道", "东方故事东方大道", "东方故事东方大道", "东方故事东方大道"},
                {"阿萨斯大所多", "阿萨德的发", "嘘嘘", "阿斯达撒旦阿达萨达", "本色股份大声道", "水电费跟我说", "东方故事东方大道", "东方故事东方大道", "东方故事东方大道", "东方故事东方大道"},
                {"阿萨斯大所多", "阿萨德的发", "嘘嘘", "阿斯达撒旦阿达萨达", "本色股份大声道", "水电费跟我说", "东方故事东方大道", "东方故事东方大道", "东方故事东方大道", "东方故事东方大道"},
                {"阿萨斯大所多", "阿萨德的发", "嘘嘘", "阿斯达撒旦阿达萨达", "本色股份大声道", "水电费跟我说", "东方故事东方大道", "东方故事东方大道", "东方故事东方大道", "东方故事东方大道"},
                {"阿萨斯大所多", "阿萨德的发", "嘘嘘", "阿斯达撒旦阿达萨达", "本色股份大声道", "水电费跟我说", "东方故事东方大道", "东方故事东方大道", "东方故事东方大道", "东方故事东方大道"},
                {"阿萨斯大所多", "阿萨德的发", "嘘嘘", "阿斯达撒旦阿达萨达", "本色股份大声道", "水电费跟我说", "东方故事东方大道", "东方故事东方大道", "东方故事东方大道", "东方故事东方大道"},
                {"阿萨斯大所多", "阿萨德的发", "嘘嘘", "阿斯达撒旦阿达萨达", "本色股份大声道", "水电费跟我说", "东方故事东方大道", "东方故事东方大道", "东方故事东方大道", "东方故事东方大道"},
                {"阿萨斯大所多", "阿萨德的发", "嘘嘘", "阿斯达撒旦阿达萨达", "本色股份大声道", "水电费跟我说", "东方故事东方大道", "东方故事东方大道", "东方故事东方大道", "东方故事东方大道"},
                {"阿萨斯大所多", "阿萨德的发", "嘘嘘", "阿斯达撒旦阿达萨达", "本色股份大声道", "水电费跟我说", "东方故事东方大道", "东方故事东方大道", "东方故事东方大道", "东方故事东方大道"},
                {"阿萨斯大所多", "阿萨德的发", "嘘嘘", "阿斯达撒旦阿达萨达", "本色股份大声道", "水电费跟我说", "东方故事东方大道", "东方故事东方大道", "东方故事东方大道", "东方故事东方大道"},
                {"阿萨斯大所多", "阿萨德的发", "嘘嘘", "阿斯达撒旦阿达萨达", "本色股份大声道", "水电费跟我说", "东方故事东方大道", "东方故事东方大道", "东方故事东方大道", "东方故事东方大道"},
                {"阿萨斯大所多", "阿萨德的发", "嘘嘘", "阿斯达撒旦阿达萨达", "本色股份大声道", "水电费跟我说", "东方故事东方大道", "东方故事东方大道", "东方故事东方大道", "东方故事东方大道"},
                {"阿萨斯大所多", "阿萨德的发", "嘘嘘", "阿斯达撒旦阿达萨达", "本色股份大声道", "水电费跟我说", "东方故事东方大道", "东方故事东方大道", "东方故事东方大道", "东方故事东方大道"},
                {"阿萨斯大所多", "阿萨德的发", "嘘嘘", "阿斯达撒旦阿达萨达", "本色股份大声道", "水电费跟我说", "东方故事东方大道", "东方故事东方大道", "东方故事东方大道", "东方故事东方大道"},
                {"阿萨斯大所多", "阿萨德的发", "嘘嘘", "阿斯达撒旦阿达萨达", "本色股份大声道", "水电费跟我说", "东方故事东方大道", "东方故事东方大道", "东方故事东方大道", "东方故事东方大道"},
                {"阿萨斯大所多", "阿萨德的发", "嘘嘘", "阿斯达撒旦阿达萨达", "本色股份大声道", "水电费跟我说", "东方故事东方大道", "东方故事东方大道", "东方故事东方大道", "东方故事东方大道"},
                {"阿萨斯大所多", "阿萨德的发", "嘘嘘", "阿斯达撒旦阿达萨达", "本色股份大声道", "水电费跟我说", "东方故事东方大道", "东方故事东方大道", "东方故事东方大道", "东方故事东方大道"},
                {"阿萨斯大所多", "阿萨德的发", "嘘嘘", "阿斯达撒旦阿达萨达", "本色股份大声道", "水电费跟我说", "东方故事东方大道", "东方故事东方大道", "东方故事东方大道", "东方故事东方大道"},
                {"阿萨斯大所多", "阿萨德的发", "嘘嘘", "阿斯达撒旦阿达萨达", "本色股份大声道", "水电费跟我说", "东方故事东方大道", "东方故事东方大道", "东方故事东方大道", "东方故事东方大道"},
                {"阿萨斯大所多", "阿萨德的发", "嘘嘘", "阿斯达撒旦阿达萨达", "本色股份大声道", "水电费跟我说", "东方故事东方大道", "东方故事东方大道", "东方故事东方大道", "东方故事东方大道"},
                {"阿萨斯大所多", "阿萨德的发", "嘘嘘", "阿斯达撒旦阿达萨达", "本色股份大声道", "水电费跟我说", "东方故事东方大道", "东方故事东方大道", "东方故事东方大道", "东方故事东方大道"},
                {"阿萨斯大所多", "阿萨德的发", "嘘嘘", "阿斯达撒旦阿达萨达", "本色股份大声道", "水电费跟我说", "东方故事东方大道", "东方故事东方大道", "东方故事东方大道", "东方故事东方大道"},
                {"阿萨斯大所多", "阿萨德的发", "嘘嘘", "阿斯达撒旦阿达萨达", "本色股份大声道", "水电费跟我说", "东方故事东方大道", "东方故事东方大道", "东方故事东方大道", "东方故事东方大道"},
                {"阿萨斯大所多", "阿萨德的发", "嘘嘘", "阿斯达撒旦阿达萨达", "本色股份大声道", "水电费跟我说", "东方故事东方大道", "东方故事东方大道", "东方故事东方大道", "东方故事东方大道"},
                {"阿萨斯大所多", "阿萨德的发", "嘘嘘", "阿斯达撒旦阿达萨达", "本色股份大声道", "水电费跟我说", "东方故事东方大道", "东方故事东方大道", "东方故事东方大道", "东方故事东方大道"},
                {"阿萨斯大所多", "阿萨德的发", "嘘嘘", "阿斯达撒旦阿达萨达", "本色股份大声道", "水电费跟我说", "东方故事东方大道", "东方故事东方大道", "东方故事东方大道", "东方故事东方大道"},
                {"阿萨斯大所多", "阿萨德的发", "嘘嘘", "阿斯达撒旦阿达萨达", "本色股份大声道", "水电费跟我说", "东方故事东方大道", "东方故事东方大道", "东方故事东方大道", "东方故事东方大道"},
                {"阿萨斯大所多", "阿萨德的发", "嘘嘘", "阿斯达撒旦阿达萨达", "本色股份大声道", "水电费跟我说", "东方故事东方大道", "东方故事东方大道", "东方故事东方大道", "东方故事东方大道"},
                {"阿萨斯大所多", "阿萨德的发", "嘘嘘", "阿斯达撒旦阿达萨达", "本色股份大声道", "水电费跟我说", "东方故事东方大道", "东方故事东方大道", "东方故事东方大道", "东方故事东方大道"},
                {"阿萨斯大所多", "阿萨德的发", "嘘嘘", "阿斯达撒旦阿达萨达", "本色股份大声道", "水电费跟我说", "东方故事东方大道", "东方故事东方大道", "东方故事东方大道", "东方故事东方大道"},
                {"阿萨斯大所多", "阿萨德的发", "嘘嘘", "阿斯达撒旦阿达萨达", "本色股份大声道", "水电费跟我说", "东方故事东方大道", "东方故事东方大道", "东方故事东方大道", "东方故事东方大道"},
                {"阿萨斯大所多", "阿萨德的发", "嘘嘘", "阿斯达撒旦阿达萨达", "本色股份大声道", "水电费跟我说", "东方故事东方大道", "东方故事东方大道", "东方故事东方大道", "东方故事东方大道"},
                {"阿萨斯大所多", "阿萨德的发", "嘘嘘", "阿斯达撒旦阿达萨达", "本色股份大声道", "水电费跟我说", "东方故事东方大道", "东方故事东方大道", "东方故事东方大道", "东方故事东方大道"},
                {"阿萨斯大所多", "阿萨德的发", "嘘嘘", "阿斯达撒旦阿达萨达", "本色股份大声道", "水电费跟我说", "东方故事东方大道", "东方故事东方大道", "东方故事东方大道", "东方故事东方大道"},
                {"阿萨斯大所多", "阿萨德的发", "嘘嘘", "阿斯达撒旦阿达萨达", "本色股份大声道", "水电费跟我说", "东方故事东方大道", "东方故事东方大道", "东方故事东方大道", "东方故事东方大道"},
                {"阿萨斯大所多", "阿萨德的发", "嘘嘘", "阿斯达撒旦阿达萨达", "本色股份大声道", "水电费跟我说", "东方故事东方大道", "东方故事东方大道", "东方故事东方大道", "东方故事东方大道"},
                {"阿萨斯大所多", "阿萨德的发", "嘘嘘", "阿斯达撒旦阿达萨达", "本色股份大声道", "水电费跟我说", "东方故事东方大道", "东方故事东方大道", "东方故事东方大道", "东方故事东方大道"},
                {"阿萨斯大所多", "阿萨德的发", "嘘嘘", "阿斯达撒旦阿达萨达", "本色股份大声道", "水电费跟我说", "东方故事东方大道", "东方故事东方大道", "东方故事东方大道", "东方故事东方大道"},
                {"阿萨斯大所多", "阿萨德的发", "嘘嘘", "阿斯达撒旦阿达萨达", "本色股份大声道", "水电费跟我说", "东方故事东方大道", "东方故事东方大道", "东方故事东方大道", "东方故事东方大道"},
                {"阿萨斯大所多", "阿萨德的发", "嘘嘘", "阿斯达撒旦阿达萨达", "本色股份大声道", "水电费跟我说", "东方故事东方大道", "东方故事东方大道", "东方故事东方大道", "东方故事东方大道"},
                {"阿萨斯大所多", "阿萨德的发", "嘘嘘", "阿斯达撒旦阿达萨达", "本色股份大声道", "水电费跟我说", "东方故事东方大道", "东方故事东方大道", "东方故事东方大道", "东方故事东方大道"},
                {"阿萨斯大所多", "阿萨德的发", "嘘嘘", "阿斯达撒旦阿达萨达", "本色股份大声道", "水电费跟我说", "东方故事东方大道", "东方故事东方大道", "东方故事东方大道", "东方故事东方大道"},
                {"阿萨斯大所多", "阿萨德的发", "嘘嘘", "阿斯达撒旦阿达萨达", "本色股份大声道", "水电费跟我说", "东方故事东方大道", "东方故事东方大道", "东方故事东方大道", "东方故事东方大道"},
                {"阿萨斯大所多", "阿萨德的发", "嘘嘘", "阿斯达撒旦阿达萨达", "本色股份大声道", "水电费跟我说", "东方故事东方大道", "东方故事东方大道", "东方故事东方大道", "东方故事东方大道"},
                {"阿萨斯大所多", "阿萨德的发", "嘘嘘", "阿斯达撒旦阿达萨达", "本色股份大声道", "水电费跟我说", "东方故事东方大道", "东方故事东方大道", "东方故事东方大道", "东方故事东方大道"},
                {"阿萨斯大所多", "阿萨德的发", "嘘嘘", "阿斯达撒旦阿达萨达", "本色股份大声道", "水电费跟我说", "东方故事东方大道", "东方故事东方大道", "东方故事东方大道", "东方故事东方大道"},
                {"阿萨斯大所多", "阿萨德的发", "嘘嘘", "阿斯达撒旦阿达萨达", "本色股份大声道", "水电费跟我说", "东方故事东方大道", "东方故事东方大道", "东方故事东方大道", "东方故事东方大道"},
                {"阿萨斯大所多", "阿萨德的发", "嘘嘘", "阿斯达撒旦阿达萨达", "本色股份大声道", "水电费跟我说", "东方故事东方大道", "东方故事东方大道", "东方故事东方大道", "东方故事东方大道"},
                {"阿萨斯大所多", "阿萨德的发", "嘘嘘", "阿斯达撒旦阿达萨达", "本色股份大声道", "水电费跟我说", "东方故事东方大道", "东方故事东方大道", "东方故事东方大道", "东方故事东方大道"},
                {"阿萨斯大所多", "阿萨德的发", "嘘嘘", "阿斯达撒旦阿达萨达", "本色股份大声道", "水电费跟我说", "东方故事东方大道", "东方故事东方大道", "东方故事东方大道", "东方故事东方大道"},
                {"阿萨斯大所多", "阿萨德的发", "嘘嘘", "阿斯达撒旦阿达萨达", "本色股份大声道", "水电费跟我说", "东方故事东方大道", "东方故事东方大道", "东方故事东方大道", "东方故事东方大道"},
                {"阿萨斯大所多", "阿萨德的发", "嘘嘘", "阿斯达撒旦阿达萨达", "本色股份大声道", "水电费跟我说", "东方故事东方大道", "东方故事东方大道", "东方故事东方大道", "东方故事东方大道"},
                {"阿萨斯大所多", "阿萨德的发", "嘘嘘", "阿斯达撒旦阿达萨达", "本色股份大声道", "水电费跟我说", "东方故事东方大道", "东方故事东方大道", "东方故事东方大道", "东方故事东方大道"},
                {"阿萨斯大所多", "阿萨德的发", "嘘嘘", "阿斯达撒旦阿达萨达", "本色股份大声道", "水电费跟我说", "东方故事东方大道", "东方故事东方大道", "东方故事东方大道", "东方故事东方大道"},
                {"阿萨斯大所多", "阿萨德的发", "嘘嘘", "阿斯达撒旦阿达萨达", "本色股份大声道", "水电费跟我说", "东方故事东方大道", "东方故事东方大道", "东方故事东方大道", "东方故事东方大道"},
                {"阿萨斯大所多", "阿萨德的发", "嘘嘘", "阿斯达撒旦阿达萨达", "本色股份大声道", "水电费跟我说", "东方故事东方大道", "东方故事东方大道", "东方故事东方大道", "东方故事东方大道"}
        };
        private Cell selectedCell = null;

        @Override
        public int getColumnCount() {
            if (getRowCount() > 0) {
                return data[0].length;
            }
            return 0;
        }

        @Override
        public int getRowCount() {
            return data.length;
        }

        @Override
        public int getCellWidthWeight(int column) {
            return 1;
        }

        @Override
        public int getCellHeightWeight(int y) {
            return 0;
        }

        @Override
        public View createCellView(Context context, int column, int row) {
            Cell cell = new Cell(row, column);
            View view = LayoutInflater.from(context).inflate(column == 0 || row == 0 || cell.equals(selectedCell) ?
                    R.layout.item_cell_first : R.layout.item_cell, getTable(), false);
            view.setTag(cell);

            getTable().addView(view, 0);
            TextView textView = view.findViewById(R.id.text);
            textView.setText(data[row][column]);
            view.setOnClickListener(this);
            return view;
        }

        @Override
        public void onClick(View v) {
            Cell cell = (Cell) v.getTag();
            if (cell.row == 0 || cell.column == 0) {
                return;
            }
            Cell oldCell = selectedCell;
            selectedCell = cell.equals(oldCell) ? null : cell;
            if (oldCell != null) {
                getTable().notifyCellChanged(oldCell.column, oldCell.row);
            }
            if (selectedCell != null) {
                getTable().notifyCellChanged(selectedCell.column, selectedCell.row);
            }
        }
    }

    private static class Cell {

        private int row;
        private int column;

        public Cell(int row, int column) {
            this.row = row;
            this.column = column;
        }

        @Override
        public boolean equals(@Nullable Object obj) {
            if (obj instanceof Cell) {
                Cell another = (Cell) obj;
                return another.column == this.column && another.row == this.row;
            }
            return false;
        }
    }

}
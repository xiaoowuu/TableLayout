package win.smartown.android.library.tableLayout;

import android.content.res.Resources;
import android.util.DisplayMetrics;
import android.widget.TextView;

/**
 * Created by Smartown on 2017/7/19.
 */
public class Util {

    public static DisplayMetrics getDisplayMetrics(Resources resources) {
        return resources.getDisplayMetrics();
    }

    public static float getScreenWidth(Resources resources) {
        return getDisplayMetrics(resources).widthPixels;
    }

    public static float getScreenHeight(Resources resources) {
        return getDisplayMetrics(resources).heightPixels;
    }

    public static float dip2px(Resources resources, float dpSize) {
        return getDisplayMetrics(resources).density * dpSize;
    }

    // 计算出该TextView中文字的长度(像素)
    public static float measureTextViewWidth(TextView textView, String text) {
        // 得到使用该paint写上text的时候,像素为多少
        return textView.getPaint().measureText(text);
    }
}

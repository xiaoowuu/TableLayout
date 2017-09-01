package win.smartown.android.app.tablelayout;

import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;

import java.lang.reflect.Field;
import java.util.ArrayList;
import java.util.List;
import java.util.Random;

import win.smartown.android.library.tableLayout.TableAdapter;
import win.smartown.android.library.tableLayout.TableLayout;

public class MainActivity extends AppCompatActivity {

    private List<Content> contentList;
    private TableLayout tableLayout;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        tableLayout = (TableLayout) findViewById(R.id.main_table);
        initContent();
//        firstRowAsTitle();
        firstColumnAsTitle();
    }

    private void initContent() {
        contentList = new ArrayList<>();
        contentList.add(new Content("姓名", "语文", "数学", "英语", "物理", "化学", "生物"));
        contentList.add(new Content("张三", newRandomNumber(), newRandomNumber(), newRandomNumber(), newRandomNumber(), newRandomNumber(), newRandomNumber()));
        contentList.add(new Content("李四", newRandomNumber(), newRandomNumber(), newRandomNumber(), newRandomNumber(), newRandomNumber(), newRandomNumber()));
        contentList.add(new Content("王二", newRandomNumber(), newRandomNumber(), newRandomNumber(), newRandomNumber(), newRandomNumber(), newRandomNumber()));
        contentList.add(new Content("王尼玛", newRandomNumber(), newRandomNumber(), newRandomNumber(), newRandomNumber(), newRandomNumber(), newRandomNumber()));
        contentList.add(new Content("张全蛋", newRandomNumber(), newRandomNumber(), newRandomNumber(), newRandomNumber(), newRandomNumber(), newRandomNumber()));
        contentList.add(new Content("赵铁柱", newRandomNumber(), newRandomNumber(), newRandomNumber(), newRandomNumber(), newRandomNumber(), newRandomNumber()));
    }

    //将第一行作为标题
    private void firstRowAsTitle() {
        //fields是表格中要显示的数据对应到Content类中的成员变量名，其定义顺序要与表格中显示的相同
        final String[] fields = {"姓名", "语文", "数学", "英语", "物理", "化学", "生物"};
        tableLayout.setAdapter(new TableAdapter() {
            @Override
            public int getColumnCount() {
                return fields.length;
            }

            @Override
            public String[] getColumnContent(int position) {
                int rowCount = contentList.size();
                String contents[] = new String[rowCount];
                try {
                    Field field = Content.class.getDeclaredField(fields[position]);
                    field.setAccessible(true);
                    for (int i = 0; i < rowCount; i++) {
                        contents[i] = (String) field.get(contentList.get(i));
                    }
                } catch (NoSuchFieldException e) {
                    e.printStackTrace();
                } catch (IllegalAccessException e) {
                    e.printStackTrace();
                }
                return contents;
            }
        });
    }

    //将第一列作为标题
    private void firstColumnAsTitle() {
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
    }

    private String newRandomNumber() {
        return (new Random().nextInt(50) + 50) + "";
    }

    public static class Content {

        private String 姓名;
        private String 语文;
        private String 数学;
        private String 英语;
        private String 物理;
        private String 化学;
        private String 生物;

        public Content(String 姓名, String 语文, String 数学, String 英语, String 物理, String 化学, String 生物) {
            this.姓名 = 姓名;
            this.语文 = 语文;
            this.数学 = 数学;
            this.英语 = 英语;
            this.物理 = 物理;
            this.化学 = 化学;
            this.生物 = 生物;
        }

        public String[] toArray() {
            return new String[]{姓名, 语文, 数学, 英语, 物理, 化学, 生物};
        }

    }

}

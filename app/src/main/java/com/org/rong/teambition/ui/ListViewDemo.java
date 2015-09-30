package com.org.rong.teambition.ui;

import android.app.Activity;
import android.os.Bundle;
import android.widget.ListView;
import android.widget.Toast;

import com.org.rong.teambition.R;
import com.org.rong.teambition.adapter.SlideAdapter;
import com.org.rong.teambition.bean.EmailMessage;

import java.util.ArrayList;
import java.util.List;

public class ListViewDemo extends Activity {

    private ListView lv;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_list_view_demo);

        lv = (ListView)findViewById(R.id.eMessage_lv);
        initData();
    }

    //初始化数据
    private void initData(){
        List<EmailMessage> data = new ArrayList<EmailMessage>();
        for(int i = 0 ; i < 18 ; i++)
        {
            data.add(new EmailMessage());
        }
        SlideAdapter adapter = new SlideAdapter(this,data);
        adapter.setOnDeleteListener(new SlideAdapter.OnDeteleListener() {
            @Override
            public void onDelete(int position) {
                //添加删除动作时执行的动作

            }
        });
        lv.setAdapter(adapter);
    }
}

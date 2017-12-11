package com.synaric.marquee;

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Toast;

import java.util.ArrayList;
import java.util.List;

public class MainActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        //初始化测试数据
        List<MarqueeData> lst = new ArrayList<>();
        lst.add(new MarqueeData("震惊！xxxxx", "xxxxx\nyyyyyy\nzzzzzzz"));
        lst.add(new MarqueeData("震惊！yyyyy", "aaaaa\nbbbbbb\nccccccc"));
        lst.add(new MarqueeData("震惊！zzzzz", "哈哈"));

        LinearScrollMarquee marquee = (LinearScrollMarquee) findViewById(R.id.marquee);
        marquee.setAdapter(new LinearScrollMarquee.ItemAdapter<MarqueeData>(lst) {
            @Override
            public String onBind(MarqueeData data) {
                return data.summery;
            }
        });
        marquee.setOnItemClickListener(new LinearScrollMarquee.OnItemClickListener<MarqueeData>() {
            @Override
            public void onClick(View view, int position, MarqueeData data) {
                Toast.makeText(MainActivity.this, data.content, Toast.LENGTH_SHORT).show();
            }
        });

    }

    class MarqueeData {

         MarqueeData(String summery, String content) {
            this.summery = summery;
            this.content = content;
        }

        String summery;
        String content;
    }
}

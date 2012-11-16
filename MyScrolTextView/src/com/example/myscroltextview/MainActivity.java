
package com.example.myscroltextview;

import java.util.ArrayList;
import java.util.List;

import android.app.Activity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.LinearLayout.LayoutParams;
import android.widget.TextView;
import android.widget.Toast;

import com.example.myscroltextview.view.ScrollTextView;

public class MainActivity extends Activity {

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        final ScrollTextView scrollTextView = (ScrollTextView)findViewById(R.id.scroll);
        final List<String> strs = new ArrayList<String>();

        strs.add("good morning 它们晨量力而里曼城蛟龙得水影The Internet site you have attempted to access响力里它们晨量力而good morning of class里曼城蛟龙得水影响力里good morning of class");
        strs.add("The Internet site you have attempted to access is prohibited. Accenture's internal webfilters indicate that the site likely contains content considered inappropriate according to Policy 57: Information Security");
        strs.add("这是测试文字!");
        strs.add("this is a test!");
        strs.add("setContentView(R.layout.activity_main);final ScrollTextView scrollTextView = (ScrollTextView)findViewById(R.id.scroll);");
        strs.add("02-08 01:39:24.585: D/dalvikvm(4948): GC_CONCURRENT freed 511K, 10% free 14542K/16007K, paused 1ms+12ms");
        strs.add("good morning 它们晨量力而里曼城蛟龙得水影The Internet site you have attempted to access响力里它们晨量力而good morning of class里曼城蛟龙得水影响力里good morning of classgood morning 它们晨量力而里曼城蛟龙得水影The Internet site you have attempted to access响力里它们晨量力而good morning of class里曼城蛟龙得水影响力里good morning of classgood morning 它们晨量力而里曼城蛟龙得水影The Internet site you have attempted to access响力里它们晨量力而good morning of class里曼城蛟龙得水影响力里good morning of classgood morning 它们晨量力而里曼城蛟龙得水影The Internet site you have attempted to access响力里它们晨量力而good morning of class里曼城蛟龙得水影响力里good morning of classgood morning 它们晨量力而里曼城蛟龙得水影The Internet site you have attempted to access响力里它们晨量力而good morning of class里曼城蛟龙得水影响力里good morning of classgood morning 它们晨量力而里曼城蛟龙得水影The Internet site you have attempted to access响力里它们晨量力而good morning of class里曼城蛟龙得水影响力里good morning of classgood morning 它们晨量力而里曼城蛟龙得水影The Internet site you have attempted to access响力里它们晨量力而good morning of class里曼城蛟龙得水影响力里good morning of classgood morning 它们晨量力而里曼城蛟龙得水影The Internet site you have attempted to access响力里它们晨量力而good morning of class里曼城蛟龙得水影响力里good morning of classgood morning 它们晨量力而里曼城蛟龙得水影The Internet site you have attempted to access响力里它们晨量力而good morning of class里曼城蛟龙得水影响力里good morning of classgood morning 它们晨量力而里曼城蛟龙得水影The Internet site you have attempted to access响力里它们晨量力而good morning of class里曼城蛟龙得水影响力里good morning of classgood morning 它们晨量力而里曼城蛟龙得水影The Internet site you have attempted to access响力里它们晨量力而good morning of class里曼城蛟龙得水影响力里good morning of classgood morning 它们晨量力而里曼城蛟龙得水影The Internet site you have attempted to access响力里它们晨量力而good morning of class里曼城蛟龙得水影响力里good morning of classgood morning 它们晨量力而里曼城蛟龙得水影The Internet site you have attempted to access响力里它们晨量力而good morning of class里曼城蛟龙得水影响力里good morning of classgood morning 它们晨量力而里曼城蛟龙得水影The Internet site you have attempted to access响力里它们晨量力而good morning of class里曼城蛟龙得水影响力里good morning of classgood morning 它们晨量力而里曼城蛟龙得水影The Internet site you have attempted to access响力里它们晨量力而good morning of class里曼城蛟龙得水影响力里good morning of classgood morning 它们晨量力而里曼城蛟龙得水影The Internet site you have attempted to access响力里它们晨量力而good morning of class里曼城蛟龙得水影响力里good morning of classgood morning 它们晨量力而里曼城蛟龙得水影The Internet site you have attempted to access响力里它们晨量力而good morning of class里曼城蛟龙得水影响力里good morning of classgood morning 它们晨量力而里曼城蛟龙得水影The Internet site you have attempted to access响力里它们晨量力而good morning of class里曼城蛟龙得水影响力里good morning of classgood morning 它们晨量力而里曼城蛟龙得水影The Internet site you have attempted to access响力里它们晨量力而good morning of class里曼城蛟龙得水影响力里good morning of class");
        final List<LinearLayout.LayoutParams> layoutParams = new ArrayList<LinearLayout.LayoutParams>();
        layoutParams.add(new LayoutParams(LayoutParams.FILL_PARENT, LayoutParams.WRAP_CONTENT));
        layoutParams.add(new LayoutParams(LayoutParams.WRAP_CONTENT, LayoutParams.WRAP_CONTENT));
        layoutParams.add(new LayoutParams(120, LayoutParams.WRAP_CONTENT));
        layoutParams.add(new LayoutParams(550, LayoutParams.WRAP_CONTENT));
        layoutParams.add(new LayoutParams(200, LayoutParams.WRAP_CONTENT));
        layoutParams.add(new LayoutParams(550, 300));
        layoutParams.add(new LayoutParams(350, 500));
        layoutParams.add(new LayoutParams(550, 900));
        layoutParams.add(new LayoutParams(LayoutParams.WRAP_CONTENT, 80));
        layoutParams.add(new LayoutParams(200, 200));
        layoutParams.add(new LayoutParams(LayoutParams.WRAP_CONTENT, 300));
        layoutParams.add(new LayoutParams(LayoutParams.FILL_PARENT, 200));
        layoutParams.add(new LayoutParams(LayoutParams.WRAP_CONTENT, 130));
        layoutParams.add(new LayoutParams(LayoutParams.WRAP_CONTENT, LayoutParams.FILL_PARENT));
        layoutParams.add(new LayoutParams(LayoutParams.WRAP_CONTENT, LayoutParams.FILL_PARENT));
        layoutParams.add(new LayoutParams(LayoutParams.WRAP_CONTENT, LayoutParams.FILL_PARENT));
        scrollTextView.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View v) {
                // TODO Auto-generated method stub
                scrollTextView.updateScrollStatus();
            }
        });
        final TextView layout = (TextView)findViewById(R.id.layout);
        Button button = (Button)findViewById(R.id.update_text);
        button.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View v) {
                // TODO Auto-generated method stub

                int i = (int)(strs.size() * Math.random());
                scrollTextView.setScrollText(strs.get(i));
            }
        });
        Button button2 = (Button)findViewById(R.id.update_layout);
        button2.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View v) {
                // TODO Auto-generated method stub

                int i = (int)(layoutParams.size() * Math.random());
                scrollTextView.setLayoutParams(layoutParams.get(i));
                String width = layoutParamsToString(layoutParams.get(i).width);
                String height = layoutParamsToString(layoutParams.get(i).height);
                layout.setText("width=" + width + "  height=" + height);
            }
        });
    }

    String layoutParamsToString(int size) {

        String result = "";
        if (size == LayoutParams.FILL_PARENT) {
            result = "fill_parent";
        } else if (size == LayoutParams.WRAP_CONTENT) {
            result = "wrap_content";
        } else {
            result = "" + size;
        }
        return result;
    }
}

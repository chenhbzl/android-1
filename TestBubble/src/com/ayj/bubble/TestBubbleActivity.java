package com.ayj.bubble;

import android.app.Activity;
import android.app.Dialog;
import android.os.Bundle;
import android.text.Html;
import android.util.DisplayMetrics;
import android.view.View;
import android.view.Window;
import android.view.WindowManager.LayoutParams;
import android.widget.TextView;

public class TestBubbleActivity extends Activity {

	/** 全局屏幕的高和宽 */
	private static int SCREEN_WIDTH = 0 ;
	private static int SCREEN_HEIGHT = 0;
	
	/**气泡view*/
    private View bubbleView = null;
    /**气泡dialog*/
    private Dialog bubbleAlert = null;
    /**我知道了*/
	private TextView tvKnow = null;
	/**气泡显示内容*/
	private TextView tvBubContent = null;
    
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.main);
        
        getDimension();
        
        bubbleView = getLayoutInflater().inflate(R.layout.overlay_pop, null);
		tvKnow = (TextView)bubbleView.findViewById(R.id.bubble_btn);
		tvKnow.setText(Html.fromHtml("<u>"+"我知道了"+"</u>"));
		tvBubContent = (TextView)bubbleView.findViewById(R.id.bubble_text);
		tvBubContent.setText("上次程序异常退出，正在传输历史数据...");
		
		tvKnow.setOnClickListener(new View.OnClickListener(){
			@Override
			public void onClick(View v) {
				bubbleAlert.cancel();
			}
			
		});

		int tmpWidth = SCREEN_WIDTH/5*3;
		int tmpHeight =SCREEN_HEIGHT/8;
		System.out.println("tmpWidth=****=" + tmpWidth);
		System.out.println("tmpHeight=++++=" + tmpHeight);
		
		//设置TextView宽度
		tvKnow.setMinWidth(tmpWidth);
		tvBubContent.setMaxWidth(tmpWidth);
		//以指定的样式初始化dialog
		bubbleAlert = new Dialog(this,R.style.bubble_dialog);
        Window win = bubbleAlert.getWindow();//获取所在window
		LayoutParams params = win.getAttributes();//获取LayoutParams
		params.x = -(SCREEN_WIDTH/8);//设置x坐标
		params.y = -tmpHeight;//设置y坐标
		params.width = tmpWidth;
		
		win.setAttributes(params);//设置生效

		bubbleAlert.setCancelable(false);
		bubbleAlert.setContentView(bubbleView);
		bubbleAlert.show();
    }
    
	/**
	 * 获取屏幕尺寸
	 */
	private void getDimension(){
		/** 获取屏幕的宽和高 */
		DisplayMetrics dm = new DisplayMetrics();
		getWindowManager().getDefaultDisplay().getMetrics(dm);

		SCREEN_WIDTH = dm.widthPixels;
		SCREEN_HEIGHT = dm.heightPixels;
	}
    
}
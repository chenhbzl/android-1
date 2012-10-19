package com.yangfuhai.animation1;

import java.util.ArrayList;

import android.app.ListActivity;
import android.os.Bundle;
import android.view.MotionEvent;
import android.view.View;
import android.view.View.OnTouchListener;
import android.view.animation.Animation;
import android.view.animation.Animation.AnimationListener;
import android.view.animation.AnimationUtils;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ListView;
import android.widget.Toast;
/**
 * @title 模仿android 4.0 通知栏动画
 * @description listview 滑动删除item
 * @company 探索者网络工作室(www.tsz.net)
 * @author michael Young (www.YangFuhai.com)
 * @version 1.0
 * @created 2012-9-29
 */
public class MainActivity extends ListActivity {
	private ArrayList<String> array;
	private ArrayAdapter<String> adapter;
	

	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		ListView listView = getListView();
		array = new ArrayList<String>();
		String aa[] = { "items1", "item2", "items3", "item4", "items5",
				"item6", "items7", "item8", "items9", "item10", "items11",
				"item12" };
		for (int i = 0; i < aa.length; i++) {
			array.add(aa[i]);
		}
		adapter = new ArrayAdapter<String>(this,android.R.layout.simple_list_item_1, array);
		listView.setAdapter(adapter);


		/**
		 * 添加listview滑动接听
		 */
		listView.setOnTouchListener(new OnTouchListener() {
			float x, y, upx, upy;
			public boolean onTouch(View view, MotionEvent event) {
				if (event.getAction() == MotionEvent.ACTION_DOWN) {
					x = event.getX();
					y = event.getY();
				}
				if (event.getAction() == MotionEvent.ACTION_UP) {
					upx = event.getX();
					upy = event.getY();
					int position1 = ((ListView) view).pointToPosition((int) x, (int) y);
					int position2 = ((ListView) view).pointToPosition((int) upx,(int) upy);
					
					if (position1 == position2 && Math.abs(x - upx) > 10) {
						View v = ((ListView) view).getChildAt(position1);
						removeListItem(v,position1);
					}
				}
				return false;
			}

		});
		
		/**
		 * listview 的item 点击事件
		 */
		listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {

			@Override
			public void onItemClick(AdapterView<?> parent, View rowView,int positon, long id) {
				Toast.makeText(rowView.getContext(), "你点击了第" + positon +"位置的item",Toast.LENGTH_SHORT).show();
//				removeListItem(rowView, positon);
			}
		});
	}

	
	/**
	 * 删除item，并播放动画
	 * @param rowView 播放动画的view
	 * @param positon 要删除的item位置
	 */
	protected void removeListItem(View rowView, final int positon) {
		
		final Animation animation = (Animation) AnimationUtils.loadAnimation(rowView.getContext(), R.anim.item_anim);
		animation.setAnimationListener(new AnimationListener() {
			public void onAnimationStart(Animation animation) {}

			public void onAnimationRepeat(Animation animation) {}

			public void onAnimationEnd(Animation animation) {
				array.remove(positon);
				adapter.notifyDataSetChanged();
				animation.cancel();
			}
		});
		

		rowView.startAnimation(animation);

	}
}

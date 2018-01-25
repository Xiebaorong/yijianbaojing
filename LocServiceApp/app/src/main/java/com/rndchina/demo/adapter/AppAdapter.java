package com.rndchina.demo.adapter;

import java.util.ArrayList;
import java.util.List;



import android.content.Context;
import android.content.pm.PackageManager;
import android.content.pm.ResolveInfo;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import com.rndchina.demo.R;
import com.rndchina.demo.util.ImageUtil;


public class AppAdapter extends BaseAdapter {
	private List<Integer> resIdList;
	private List<String> txtList;
	private Context mContext;
	public static final int APP_PAGE_SIZE = 16;
	private PackageManager pm;
	
	public AppAdapter(Context context, List<Integer> resid_list,List txt_list, int page) {
		mContext = context;
		
		resIdList = new ArrayList<Integer>();
		txtList = new ArrayList<String>();

		for(int i=0;i<resid_list.size();i++){
			resIdList.add(resid_list.get(i));
			txtList.add((String)txt_list.get(i));
		}
	}
	public int getCount() {
		// TODO Auto-generated method stub
		return resIdList.size();
	}

	public Object getItem(int position) {
		// TODO Auto-generated method stub
		return resIdList.get(position);
	}

	public long getItemId(int position) {
		// TODO Auto-generated method stub
		return position;
	}

	public View getView(int position, View convertView, ViewGroup parent) {
		// TODO Auto-generated method stub

		AppItem appItem;
		if (convertView == null) {
			View v = LayoutInflater.from(mContext).inflate(R.layout.menu_app_item, null);
			
			appItem = new AppItem();
			appItem.mAppIcon = (ImageView)v.findViewById(R.id.ivAppIcon);
			appItem.mAppName = (TextView)v.findViewById(R.id.tvAppName);
			
			v.setTag(appItem);
			convertView = v;
		} else {
			appItem = (AppItem)convertView.getTag();
		}
		// set the icon
		appItem.mAppIcon.setImageBitmap(ImageUtil.getBitmapFromResources(mContext, resIdList.get(position)));
		// set the app name
		appItem.mAppName.setText(txtList.get(position));
		
		return convertView;
	}

	/**
	 * 每个应用显示的内容，包括图标和名称
	 * @author Yao.GUET
	 *
	 */
	class AppItem {
		ImageView mAppIcon;
		TextView mAppName;
	}
}

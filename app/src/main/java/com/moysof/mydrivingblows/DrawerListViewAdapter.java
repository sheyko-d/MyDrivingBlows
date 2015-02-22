package com.moysof.mydrivingblows;

import com.moysof.mydrivingblows.FeedActivity.updateTask;
import com.nostra13.universalimageloader.core.DisplayImageOptions;
import com.nostra13.universalimageloader.core.ImageLoader;
import com.nostra13.universalimageloader.core.ImageLoaderConfiguration;

import android.content.Context;
import android.content.SharedPreferences;
import android.media.Image;
import android.preference.PreferenceManager;
import android.text.Html;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.TextView;

public class DrawerListViewAdapter extends BaseAdapter {

	private Context context;
	private String[] stringArray;
	private LayoutInflater inflater;
	private View itemView;
	private int[] drawerIconsArray = { 0, 0, R.drawable.ic_news, 0,
			R.drawable.ic_tag, 0, R.drawable.ic_list, 0 };
	private int selected = 0;
	private SharedPreferences preferences;

	public DrawerListViewAdapter(Context context, int selected) {
		this.context = context;
		this.stringArray = context.getResources().getStringArray(
				R.array.drawer_array);
		this.selected = selected;
		preferences = PreferenceManager.getDefaultSharedPreferences(context);
	}

	public int getCount() {
		return stringArray.length;
	}

	@Override
	public Object getItem(int position) {
		return null;
	}

	@Override
	public long getItemId(int position) {
		return 0;
	}

	public View getView(int position, View convertView, ViewGroup parent) {

		inflater = (LayoutInflater) context
				.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
		if (position == 0) {
			if (preferences.getString("username", "").equals("")) {
				itemView = inflater.inflate(R.layout.drawer_account_guest,
						parent, false);
			} else {
				itemView = inflater.inflate(R.layout.drawer_account, parent,
						false);
				ImageLoader imgLoader = ImageLoader.getInstance();

				DisplayImageOptions options = new DisplayImageOptions.Builder()
						.cacheInMemory(false).cacheOnDisc(true).build();
				ImageLoaderConfiguration config = new ImageLoaderConfiguration.Builder(
						context).defaultDisplayImageOptions(options)
						.threadPoolSize(5).build();
				imgLoader.init(config);
				if (!preferences.getString("picture", "").equals("")) {
					imgLoader.displayImage(
							preferences.getString("picture", ""),
							(ImageView) itemView.findViewById(R.id.avatar),
							options);
				}
				((TextView) itemView.findViewById(R.id.username))
						.setText(preferences.getString("username", ""));
				((TextView) itemView.findViewById(R.id.email))
						.setText(preferences.getString("email", ""));
			}
		} else if (position == 1) {
			itemView = inflater.inflate(R.layout.divider_drawer_big, parent,
					false);
		} else if (position == 3 || position == 5 || position == 7) {
			itemView = inflater.inflate(R.layout.divider_drawer, parent, false);
		} else if (position == 6
				& preferences.getString("username", "").equals("")) {
			itemView = inflater
					.inflate(R.layout.drawer_news_off, parent, false);

			((ImageView) itemView.findViewById(R.id.image))
					.setImageResource(R.drawable.ic_list_off);
			((TextView) itemView.findViewById(R.id.name))
					.setText(stringArray[position]);
		} else {
			if (position == selected) {
				itemView = inflater.inflate(R.layout.drawer_item_selected,
						parent, false);
			} else {
				itemView = inflater
						.inflate(R.layout.drawer_item, parent, false);
			}
			((TextView) itemView.findViewById(R.id.name))
					.setText(Html.fromHtml("<b>"+stringArray[position]+"</b>"));
			((ImageView) itemView.findViewById(R.id.image))
					.setImageResource(drawerIconsArray[position]);
		}
		return itemView;
	}

}

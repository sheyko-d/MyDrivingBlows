package com.moysof.mydrivingblows;

import android.app.Activity;
import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import com.nostra13.universalimageloader.core.DisplayImageOptions;
import com.nostra13.universalimageloader.core.ImageLoader;
import com.nostra13.universalimageloader.core.ImageLoaderConfiguration;

import java.util.ArrayList;

public class NewsListViewAdapter extends BaseAdapter {

	Context context;
	LayoutInflater inflater;
	private Activity activity;
	private ArrayList<String> comments = new ArrayList<String>();
	private ArrayList<String> photos = new ArrayList<String>();
    private ArrayList<String> videos = new ArrayList<String>();
	private ArrayList<String> plate_nums = new ArrayList<String>();
	private ArrayList<String> plate_states = new ArrayList<String>();
	private ImageLoader imgLoader;
	private DisplayImageOptions options;

	public NewsListViewAdapter(Activity activity, Context context,
			ArrayList<String> comments, ArrayList<String> photos, ArrayList<String> videos,
			ArrayList<String> plate_nums, ArrayList<String> plate_states) {
		this.activity = activity;
		this.context = context;
		this.comments = comments;
		this.photos = photos;
        this.videos = videos;
		this.plate_nums = plate_nums;
		this.plate_states = plate_states;
		imgLoader = ImageLoader.getInstance();

		options = new DisplayImageOptions.Builder().cacheInMemory(false)
				.cacheOnDisc(true).build();
		ImageLoaderConfiguration config = new ImageLoaderConfiguration.Builder(
				context).defaultDisplayImageOptions(options).build();
		imgLoader.init(config);
	}

	@Override
	public int getCount() {
		return comments.size();
	}

	@Override
	public Object getItem(int position) {
		return null;
	}

	@Override
	public long getItemId(int position) {
		return 0;
	}

	public View getView(final int position, View convertView, ViewGroup parent) {
		TextView comment;
		TextView plateNum;
		ImageView photo;
		ImageView plateImage;

		inflater = (LayoutInflater) context
				.getSystemService(Context.LAYOUT_INFLATER_SERVICE);

		View itemView = inflater.inflate(R.layout.my_listview_item, parent,
				false);

		comment = (TextView) itemView.findViewById(R.id.comment);
		plateNum = (TextView) itemView.findViewById(R.id.plateNum);
		photo = (ImageView) itemView.findViewById(R.id.photo);
		plateImage = (ImageView) itemView.findViewById(R.id.plateImage);

		photo.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
				((ImageView) activity.findViewById(R.id.image))
						.setVisibility(View.VISIBLE);
				imgLoader.displayImage(photos.get(position),
						((ImageView) activity.findViewById(R.id.image)),
						options);
			}
		});

		if (!photos.get(position).equals("")) {
			imgLoader.displayImage(photos.get(position), photo);
			photo.setVisibility(View.VISIBLE);
		} else if (!videos.get(position).equals("")) {
            photo.setVisibility(View.VISIBLE);
        }

        if (!photos.get(position).equals("") & !videos.get(position).equals("")) {
            itemView.findViewById(R.id.videoIcon).setVisibility(View.VISIBLE);
        } else {
            itemView.findViewById(R.id.videoIcon).setVisibility(View.GONE);
        }

		plateNum.setText(plate_nums.get(position));
		comment.setText(comments.get(position));

		if (plate_states.get(position).equals("AL")) {
			plateImage.setImageResource(R.drawable.al_sm);
		} else if (plate_states.get(position).equals("AK")) {
			plateImage.setImageResource(R.drawable.ak_sm);
		} else if (plate_states.get(position).equals("AZ")) {
			plateImage.setImageResource(R.drawable.az_sm);
		} else if (plate_states.get(position).equals("AR")) {
			plateImage.setImageResource(R.drawable.ar_sm);
		} else if (plate_states.get(position).equals("CA")) {
			plateImage.setImageResource(R.drawable.ca_sm);
		} else if (plate_states.get(position).equals("CO")) {
			plateImage.setImageResource(R.drawable.co_sm);
		} else if (plate_states.get(position).equals("CT")) {
			plateImage.setImageResource(R.drawable.ct_sm);
		} else if (plate_states.get(position).equals("DC")) {
			plateImage.setImageResource(R.drawable.dc_sm);
		} else if (plate_states.get(position).equals("DE")) {
			plateImage.setImageResource(R.drawable.de_sm);
		} else if (plate_states.get(position).equals("FL")) {
			plateImage.setImageResource(R.drawable.fl_sm);
		} else if (plate_states.get(position).equals("GA")) {
			plateImage.setImageResource(R.drawable.ga_sm);
		} else if (plate_states.get(position).equals("GOV")) {
			plateImage.setImageResource(R.drawable.gov_sm);
		} else if (plate_states.get(position).equals("HI")) {
			plateImage.setImageResource(R.drawable.hi_sm);
		} else if (plate_states.get(position).equals("ID")) {
			plateImage.setImageResource(R.drawable.id_sm);
		} else if (plate_states.get(position).equals("IL")) {
			plateImage.setImageResource(R.drawable.il_sm);
		} else if (plate_states.get(position).equals("IN")) {
			plateImage.setImageResource(R.drawable.in_sm);
		} else if (plate_states.get(position).equals("IA")) {
			plateImage.setImageResource(R.drawable.ia_sm);
		} else if (plate_states.get(position).equals("KS")) {
			plateImage.setImageResource(R.drawable.ks_sm);
		} else if (plate_states.get(position).equals("KY")) {
			plateImage.setImageResource(R.drawable.ky_sm);
		} else if (plate_states.get(position).equals("LA")) {
			plateImage.setImageResource(R.drawable.la_sm);
		} else if (plate_states.get(position).equals("ME")) {
			plateImage.setImageResource(R.drawable.me_sm);
		} else if (plate_states.get(position).equals("MD")) {
			plateImage.setImageResource(R.drawable.md_sm);
		} else if (plate_states.get(position).equals("MA")) {
			plateImage.setImageResource(R.drawable.ma_sm);
		} else if (plate_states.get(position).equals("MI")) {
			plateImage.setImageResource(R.drawable.mi_sm);
		} else if (plate_states.get(position).equals("MN")) {
			plateImage.setImageResource(R.drawable.mn_sm);
		} else if (plate_states.get(position).equals("MS")) {
			plateImage.setImageResource(R.drawable.ms_sm);
		} else if (plate_states.get(position).equals("MO")) {
			plateImage.setImageResource(R.drawable.mo_sm);
		} else if (plate_states.get(position).equals("MT")) {
			plateImage.setImageResource(R.drawable.mt_sm);
		} else if (plate_states.get(position).equals("NE")) {
			plateImage.setImageResource(R.drawable.ne_sm);
		} else if (plate_states.get(position).equals("NV")) {
			plateImage.setImageResource(R.drawable.nv_sm);
		} else if (plate_states.get(position).equals("NH")) {
			plateImage.setImageResource(R.drawable.nh_sm);
		} else if (plate_states.get(position).equals("NJ")) {
			plateImage.setImageResource(R.drawable.nj_sm);
		} else if (plate_states.get(position).equals("NM")) {
			plateImage.setImageResource(R.drawable.nm_sm);
		} else if (plate_states.get(position).equals("NY")) {
			plateImage.setImageResource(R.drawable.ny_sm);
		} else if (plate_states.get(position).equals("NC")) {
			plateImage.setImageResource(R.drawable.nc_sm);
		} else if (plate_states.get(position).equals("ND")) {
			plateImage.setImageResource(R.drawable.nd_sm);
		} else if (plate_states.get(position).equals("OH")) {
			plateImage.setImageResource(R.drawable.oh_sm);
		} else if (plate_states.get(position).equals("OK")) {
			plateImage.setImageResource(R.drawable.ok_sm);
		} else if (plate_states.get(position).equals("OR")) {
			plateImage.setImageResource(R.drawable.or_sm);
		} else if (plate_states.get(position).equals("PA")) {
			plateImage.setImageResource(R.drawable.pa_sm);
		} else if (plate_states.get(position).equals("RI")) {
			plateImage.setImageResource(R.drawable.ri_sm);
		} else if (plate_states.get(position).equals("SC")) {
			plateImage.setImageResource(R.drawable.sc_sm);
		} else if (plate_states.get(position).equals("SD")) {
			plateImage.setImageResource(R.drawable.sd_sm);
		} else if (plate_states.get(position).equals("TN")) {
			plateImage.setImageResource(R.drawable.tn_sm);
		} else if (plate_states.get(position).equals("TX")) {
			plateImage.setImageResource(R.drawable.tx_sm);
		} else if (plate_states.get(position).equals("UT")) {
			plateImage.setImageResource(R.drawable.ut_sm);
		} else if (plate_states.get(position).equals("VT")) {
			plateImage.setImageResource(R.drawable.vt_sm);
		} else if (plate_states.get(position).equals("VA")) {
			plateImage.setImageResource(R.drawable.va_sm);
		} else if (plate_states.get(position).equals("WA")) {
			plateImage.setImageResource(R.drawable.wa_sm);
		} else if (plate_states.get(position).equals("WV")) {
			plateImage.setImageResource(R.drawable.wv_sm);
		} else if (plate_states.get(position).equals("WI")) {
			plateImage.setImageResource(R.drawable.wi_sm);
		} else if (plate_states.get(position).equals("WY")) {
			plateImage.setImageResource(R.drawable.wy_sm);
		}

		// imgflag.setImageResource(plates.[position]);

		return itemView;
	}
}

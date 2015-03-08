package app.moysof.mydrivingblows;

import java.util.ArrayList;

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

public class NewsListViewAdapterDelete extends BaseAdapter {

	Context context;
	LayoutInflater inflater;
	private Activity activity;
	private ArrayList<String> comments = new ArrayList<String>();
	private ArrayList<String> plate_nums = new ArrayList<String>();
	private ArrayList<String> plate_states = new ArrayList<String>();
	private ImageLoader imgLoader;
	private DisplayImageOptions options;

	public NewsListViewAdapterDelete(Activity activity, Context context,
			ArrayList<String> comments, ArrayList<String> photos,
			ArrayList<String> plate_nums, ArrayList<String> plate_states) {
		this.activity = activity;
		this.context = context;
		this.comments = comments;
		this.plate_nums = plate_nums;
		this.plate_states = plate_states;
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

		View itemView = inflater.inflate(R.layout.my_listview_item_delete, parent,
				false);

		comment = (TextView) itemView.findViewById(R.id.comment);
		plateNum = (TextView) itemView.findViewById(R.id.plateNum);
		photo = (ImageView) itemView.findViewById(R.id.photo);
		plateImage = (ImageView) itemView.findViewById(R.id.plateImage);

		imgLoader = ImageLoader.getInstance();

		options = new DisplayImageOptions.Builder().cacheInMemory(false)
				.cacheOnDisc(true).build();
		ImageLoaderConfiguration config = new ImageLoaderConfiguration.Builder(
				context).defaultDisplayImageOptions(options)
				.threadPoolSize(5).build();
		imgLoader.init(config);

		plateNum.setText(plate_nums.get(position));
		comment.setText(comments.get(position));

		if (plate_states.get(position).equals("AL")) {
			plateImage.setImageResource(R.drawable.al);
		} else if (plate_states.get(position).equals("AK")) {
			plateImage.setImageResource(R.drawable.ak);
		} else if (plate_states.get(position).equals("AZ")) {
			plateImage.setImageResource(R.drawable.az);
		} else if (plate_states.get(position).equals("AR")) {
			plateImage.setImageResource(R.drawable.ar);
		} else if (plate_states.get(position).equals("CA")) {
			plateImage.setImageResource(R.drawable.ca);
		} else if (plate_states.get(position).equals("CO")) {
			plateImage.setImageResource(R.drawable.co);
		} else if (plate_states.get(position).equals("CT")) {
			plateImage.setImageResource(R.drawable.ct);
		} else if (plate_states.get(position).equals("DC")) {
			plateImage.setImageResource(R.drawable.dc);
		} else if (plate_states.get(position).equals("DE")) {
			plateImage.setImageResource(R.drawable.de);
		} else if (plate_states.get(position).equals("FL")) {
			plateImage.setImageResource(R.drawable.fl);
		} else if (plate_states.get(position).equals("GA")) {
			plateImage.setImageResource(R.drawable.ga);
		} else if (plate_states.get(position).equals("GOV")) {
			plateImage.setImageResource(R.drawable.gov);
		} else if (plate_states.get(position).equals("HI")) {
			plateImage.setImageResource(R.drawable.hi);
		} else if (plate_states.get(position).equals("ID")) {
			plateImage.setImageResource(R.drawable.id);
		} else if (plate_states.get(position).equals("IL")) {
			plateImage.setImageResource(R.drawable.il);
		} else if (plate_states.get(position).equals("IN")) {
			plateImage.setImageResource(R.drawable.in);
		} else if (plate_states.get(position).equals("IA")) {
			plateImage.setImageResource(R.drawable.ia);
		} else if (plate_states.get(position).equals("KS")) {
			plateImage.setImageResource(R.drawable.ks);
		} else if (plate_states.get(position).equals("KY")) {
			plateImage.setImageResource(R.drawable.ky);
		} else if (plate_states.get(position).equals("LA")) {
			plateImage.setImageResource(R.drawable.la);
		} else if (plate_states.get(position).equals("ME")) {
			plateImage.setImageResource(R.drawable.me);
		} else if (plate_states.get(position).equals("MD")) {
			plateImage.setImageResource(R.drawable.md);
		} else if (plate_states.get(position).equals("MA")) {
			plateImage.setImageResource(R.drawable.ma);
		} else if (plate_states.get(position).equals("MI")) {
			plateImage.setImageResource(R.drawable.mi);
		} else if (plate_states.get(position).equals("MN")) {
			plateImage.setImageResource(R.drawable.mn);
		} else if (plate_states.get(position).equals("MS")) {
			plateImage.setImageResource(R.drawable.ms);
		} else if (plate_states.get(position).equals("MO")) {
			plateImage.setImageResource(R.drawable.mo);
		} else if (plate_states.get(position).equals("MT")) {
			plateImage.setImageResource(R.drawable.mt);
		} else if (plate_states.get(position).equals("NE")) {
			plateImage.setImageResource(R.drawable.ne);
		} else if (plate_states.get(position).equals("NV")) {
			plateImage.setImageResource(R.drawable.nv);
		} else if (plate_states.get(position).equals("NH")) {
			plateImage.setImageResource(R.drawable.nh);
		} else if (plate_states.get(position).equals("NJ")) {
			plateImage.setImageResource(R.drawable.nj);
		} else if (plate_states.get(position).equals("NM")) {
			plateImage.setImageResource(R.drawable.nm);
		} else if (plate_states.get(position).equals("NY")) {
			plateImage.setImageResource(R.drawable.ny);
		} else if (plate_states.get(position).equals("NC")) {
			plateImage.setImageResource(R.drawable.nc);
		} else if (plate_states.get(position).equals("ND")) {
			plateImage.setImageResource(R.drawable.nd);
		} else if (plate_states.get(position).equals("OH")) {
			plateImage.setImageResource(R.drawable.oh);
		} else if (plate_states.get(position).equals("OK")) {
			plateImage.setImageResource(R.drawable.ok);
		} else if (plate_states.get(position).equals("OR")) {
			plateImage.setImageResource(R.drawable.or);
		} else if (plate_states.get(position).equals("PA")) {
			plateImage.setImageResource(R.drawable.pa);
		} else if (plate_states.get(position).equals("RI")) {
			plateImage.setImageResource(R.drawable.ri);
		} else if (plate_states.get(position).equals("SC")) {
			plateImage.setImageResource(R.drawable.sc);
		} else if (plate_states.get(position).equals("SD")) {
			plateImage.setImageResource(R.drawable.sd);
		} else if (plate_states.get(position).equals("TN")) {
			plateImage.setImageResource(R.drawable.tn);
		} else if (plate_states.get(position).equals("TX")) {
			plateImage.setImageResource(R.drawable.tx);
		} else if (plate_states.get(position).equals("UT")) {
			plateImage.setImageResource(R.drawable.ut);
		} else if (plate_states.get(position).equals("VT")) {
			plateImage.setImageResource(R.drawable.vt);
		} else if (plate_states.get(position).equals("VA")) {
			plateImage.setImageResource(R.drawable.va);
		} else if (plate_states.get(position).equals("WA")) {
			plateImage.setImageResource(R.drawable.wa);
		} else if (plate_states.get(position).equals("WV")) {
			plateImage.setImageResource(R.drawable.wv);
		} else if (plate_states.get(position).equals("WI")) {
			plateImage.setImageResource(R.drawable.wi);
		} else if (plate_states.get(position).equals("WY")) {
			plateImage.setImageResource(R.drawable.wy);
		}

		// imgflag.setImageResource(plates.[position]);

		return itemView;
	}
}

package app.moysof.mydrivingblows;

import java.util.Locale;

import org.json.JSONArray;

import android.content.Context;
import android.text.Html;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.TextView;

public class GridViewSuggestionsAdapter extends BaseAdapter {

	private Context context;
	private LayoutInflater inflater;
	private JSONArray suggestionsJSON;
	private View itemView;

	public GridViewSuggestionsAdapter(Context context, JSONArray suggestionsJSON) {
		this.context = context;
		this.suggestionsJSON = suggestionsJSON;
	}

	@Override
	public int getCount() {
		return suggestionsJSON.length();
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
		inflater = (LayoutInflater) context
				.getSystemService(Context.LAYOUT_INFLATER_SERVICE);

		try {
			itemView = inflater
					.inflate(R.layout.suggestion_item, parent, false);

			String number = suggestionsJSON.getJSONObject(position).getString(
					"num");
			number = number.replaceAll(FeedActivity.searchQuery.toUpperCase(Locale.US), "<font color=\"#fff600\">"+FeedActivity.searchQuery.toUpperCase(Locale.US)+"</font>");
			((TextView) itemView.findViewById(R.id.plateText))
					.setText(Html.fromHtml(number));

			ImageView plateImage = (ImageView) itemView
					.findViewById(R.id.plateImage);
			

			itemView.findViewById(R.id.suggestionClick).setTag(position);

			String state = suggestionsJSON.getJSONObject(position).getString(
					"state");
			if (state.equals("AL")) {
				plateImage.setImageResource(R.drawable.al);
			} else if (state.equals("AK")) {
				plateImage.setImageResource(R.drawable.ak);
			} else if (state.equals("AZ")) {
				plateImage.setImageResource(R.drawable.az);
			} else if (state.equals("AR")) {
				plateImage.setImageResource(R.drawable.ar);
			} else if (state.equals("CA")) {
				plateImage.setImageResource(R.drawable.ca);
			} else if (state.equals("CO")) {
				plateImage.setImageResource(R.drawable.co);
			} else if (state.equals("CT")) {
				plateImage.setImageResource(R.drawable.ct);
			} else if (state.equals("DC")) {
				plateImage.setImageResource(R.drawable.dc);
			} else if (state.equals("DE")) {
				plateImage.setImageResource(R.drawable.de);
			} else if (state.equals("FL")) {
				plateImage.setImageResource(R.drawable.fl);
			} else if (state.equals("GA")) {
				plateImage.setImageResource(R.drawable.ga);
			} else if (state.equals("GOV")) {
				plateImage.setImageResource(R.drawable.gov);
			} else if (state.equals("HI")) {
				plateImage.setImageResource(R.drawable.hi);
			} else if (state.equals("ID")) {
				plateImage.setImageResource(R.drawable.id);
			} else if (state.equals("IL")) {
				plateImage.setImageResource(R.drawable.il);
			} else if (state.equals("IN")) {
				plateImage.setImageResource(R.drawable.in);
			} else if (state.equals("IA")) {
				plateImage.setImageResource(R.drawable.ia);
			} else if (state.equals("KS")) {
				plateImage.setImageResource(R.drawable.ks);
			} else if (state.equals("KY")) {
				plateImage.setImageResource(R.drawable.ky);
			} else if (state.equals("LA")) {
				plateImage.setImageResource(R.drawable.la);
			} else if (state.equals("ME")) {
				plateImage.setImageResource(R.drawable.me);
			} else if (state.equals("MD")) {
				plateImage.setImageResource(R.drawable.md);
			} else if (state.equals("MA")) {
				plateImage.setImageResource(R.drawable.ma);
			} else if (state.equals("MI")) {
				plateImage.setImageResource(R.drawable.mi);
			} else if (state.equals("MN")) {
				plateImage.setImageResource(R.drawable.mn);
			} else if (state.equals("MS")) {
				plateImage.setImageResource(R.drawable.ms);
			} else if (state.equals("MO")) {
				plateImage.setImageResource(R.drawable.mo);
			} else if (state.equals("MT")) {
				plateImage.setImageResource(R.drawable.mt);
			} else if (state.equals("NE")) {
				plateImage.setImageResource(R.drawable.ne);
			} else if (state.equals("NV")) {
				plateImage.setImageResource(R.drawable.nv);
			} else if (state.equals("NH")) {
				plateImage.setImageResource(R.drawable.nh);
			} else if (state.equals("NJ")) {
				plateImage.setImageResource(R.drawable.nj);
			} else if (state.equals("NM")) {
				plateImage.setImageResource(R.drawable.nm);
			} else if (state.equals("NY")) {
				plateImage.setImageResource(R.drawable.ny);
			} else if (state.equals("NC")) {
				plateImage.setImageResource(R.drawable.nc);
			} else if (state.equals("ND")) {
				plateImage.setImageResource(R.drawable.nd);
			} else if (state.equals("OH")) {
				plateImage.setImageResource(R.drawable.oh);
			} else if (state.equals("OK")) {
				plateImage.setImageResource(R.drawable.ok);
			} else if (state.equals("OR")) {
				plateImage.setImageResource(R.drawable.or);
			} else if (state.equals("PA")) {
				plateImage.setImageResource(R.drawable.pa);
			} else if (state.equals("RI")) {
				plateImage.setImageResource(R.drawable.ri);
			} else if (state.equals("SC")) {
				plateImage.setImageResource(R.drawable.sc);
			} else if (state.equals("SD")) {
				plateImage.setImageResource(R.drawable.sd);
			} else if (state.equals("TN")) {
				plateImage.setImageResource(R.drawable.tn);
			} else if (state.equals("TX")) {
				plateImage.setImageResource(R.drawable.tx);
			} else if (state.equals("UT")) {
				plateImage.setImageResource(R.drawable.ut);
			} else if (state.equals("VT")) {
				plateImage.setImageResource(R.drawable.vt);
			} else if (state.equals("VA")) {
				plateImage.setImageResource(R.drawable.va);
			} else if (state.equals("WA")) {
				plateImage.setImageResource(R.drawable.wa);
			} else if (state.equals("WV")) {
				plateImage.setImageResource(R.drawable.wv);
			} else if (state.equals("WI")) {
				plateImage.setImageResource(R.drawable.wi);
			} else if (state.equals("WY")) {
				plateImage.setImageResource(R.drawable.wy);
			}
		} catch (Exception e) {

		}

		return itemView;
	}
}

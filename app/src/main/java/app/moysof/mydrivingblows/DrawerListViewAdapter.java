package app.moysof.mydrivingblows;

import android.content.Context;
import android.content.SharedPreferences;
import android.preference.PreferenceManager;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import com.nostra13.universalimageloader.core.DisplayImageOptions;
import com.nostra13.universalimageloader.core.ImageLoader;
import com.nostra13.universalimageloader.core.ImageLoaderConfiguration;

public class DrawerListViewAdapter extends BaseAdapter {

    private Context context;
    private String[] stringArray;
    private LayoutInflater inflater;
    private View itemView;
    private int[] drawerIconsArray = {0, R.drawable.ic_news,
            R.drawable.ic_tag, R.drawable.ic_list, 0, R.drawable.ic_settings,
            R.drawable.ic_terms, R.drawable.ic_contact, R.drawable.ic_info};
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
        } else if (position == 4) {
            itemView = inflater.inflate(R.layout.divider_drawer, parent, false);
        } else {
            if (position == selected) {
                itemView = inflater.inflate(R.layout.drawer_item_selected,
                        parent, false);
            } else {
                itemView = inflater
                        .inflate(R.layout.drawer_item, parent, false);
            }
            ((TextView) itemView.findViewById(R.id.name))
                    .setText(stringArray[position]);
            ((ImageView) itemView.findViewById(R.id.image))
                    .setImageResource(drawerIconsArray[position]);
        }

        if (position == 3
                & preferences.getString("username", "").equals("")) {
            itemView.setAlpha(0.6f);
        }
        return itemView;
    }

}

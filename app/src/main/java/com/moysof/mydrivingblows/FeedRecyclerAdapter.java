package com.moysof.mydrivingblows;

import android.content.Context;
import android.graphics.Bitmap;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.text.Html;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.nostra13.universalimageloader.core.DisplayImageOptions;
import com.nostra13.universalimageloader.core.ImageLoader;
import com.nostra13.universalimageloader.core.ImageLoaderConfiguration;
import com.nostra13.universalimageloader.core.assist.FailReason;
import com.nostra13.universalimageloader.core.display.FadeInBitmapDisplayer;
import com.nostra13.universalimageloader.core.listener.ImageLoadingListener;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

public class FeedRecyclerAdapter extends RecyclerView.Adapter<FeedRecyclerAdapter.ItemViewHolder> {

    private JSONArray commentsArray = null;
    private final ImageLoader mImgLoader;
    private final DisplayImageOptions mOptions;
    private final static int TYPE_HEADER = 0;
    private final static int TYPE_ITEM = 1;
    private final static int TYPE_ITEM_WITHOUT_PHOTO = 2;
    private final JSONObject feedJSON;

    // Provide a reference to the views for each data item
    // Complex data items may need more than one view per item, and
    // you provide access to all the views for a data item in a view holder
    public static class ItemViewHolder extends RecyclerView.ViewHolder {
        // each data item is just a string in this case
        public TextView mPlateNumTxt;
        public TextView mCommentInfoTxt;
        public TextView mCommentTxt;
        public View mPhotoLayoutView;
        public ImageView mPhotoImg;
        public ImageView mPlateImageImg;
        public View mCategoryBg;
        public ImageView mWorstImage;
        public TextView mWorstText;
        public TextView mWorstComment;

        public ItemViewHolder(View v) {
            super(v);
            // Items
            mPlateNumTxt = (TextView) v.findViewById(R.id.plateNum);
            mCommentInfoTxt = (TextView) v.findViewById(R.id.commentInfo);
            mCommentTxt = (TextView) v.findViewById(R.id.comment);
            mPhotoLayoutView = v.findViewById(R.id.photoLayout);
            mPhotoImg = (ImageView) v.findViewById(R.id.photo);
            mPlateImageImg = (ImageView) v.findViewById(R.id.plateImage);
            mCategoryBg = v.findViewById(R.id.categoryBg);
            //Header
            mWorstImage = (ImageView) v.findViewById(R.id.worstImage);
            mWorstText = (TextView) v.findViewById(R.id.worstText);
            mWorstComment = (TextView) v.findViewById(R.id.worstComment);
        }
    }

    // Provide a suitable constructor (depends on the kind of dataset)
    public FeedRecyclerAdapter(Context context, RecyclerView recyclerView, JSONObject feedJSON) {
        this.feedJSON = feedJSON;
        try {
            commentsArray = new JSONArray(
                    feedJSON.getString("comments"));
        } catch (Exception e) {
        }

        mOptions = new DisplayImageOptions.Builder().cacheInMemory(true)
                .cacheOnDisk(true)
                .displayer(new FadeInBitmapDisplayer(250, true, false, false))
                .build();
        ImageLoaderConfiguration config = new ImageLoaderConfiguration.Builder(
                context).defaultDisplayImageOptions(mOptions).build();
        mImgLoader = ImageLoader.getInstance();
        mImgLoader.init(config);

        final GridLayoutManager layoutManager = (GridLayoutManager) (recyclerView
                .getLayoutManager());
        layoutManager
                .setSpanSizeLookup(new GridLayoutManager.SpanSizeLookup() {
                    @Override
                    public int getSpanSize(int position) {
                        return getItemViewType(position) == TYPE_HEADER ? layoutManager
                                .getSpanCount() : 1;
                    }
                });
    }

    // Create new views (invoked by the layout manager)
    @Override
    public FeedRecyclerAdapter.ItemViewHolder onCreateViewHolder(ViewGroup parent,
                                                                 int viewType) {
        // create a new view
        View v;
        if (viewType == TYPE_HEADER) {
            v = LayoutInflater.from(parent.getContext())
                    .inflate(R.layout.feed_item_header, parent, false);
        } else if (viewType == TYPE_ITEM) {
            v = LayoutInflater.from(parent.getContext())
                    .inflate(R.layout.feed_item, parent, false);
        } else {
            v = LayoutInflater.from(parent.getContext())
                    .inflate(R.layout.feed_item_without_photo, parent, false);
        }
        ItemViewHolder vh = new ItemViewHolder(v);

        return vh;
    }

    // Replace the contents of a view (invoked by the layout manager)
    @Override
    public void onBindViewHolder(ItemViewHolder holder, int position) {

        int type = getItemViewType(position);

        // - get element from your dataset at this position
        // - replace the contents of the view with that element
        try {
            if (type == TYPE_ITEM || type == TYPE_ITEM_WITHOUT_PHOTO) {
                position--;

                holder.mCategoryBg.setTag(position + 1);

                holder.mPlateImageImg.setImageBitmap(null);
                if (holder.mPhotoImg != null)
                    holder.mPhotoImg.setImageBitmap(null);

                holder.mPlateNumTxt.setText(commentsArray.getJSONObject(position)
                        .getString("comment_plate_num"));
                holder.mCommentInfoTxt
                        .setText(Html.fromHtml(commentsArray
                                .getJSONObject(position).getString(
                                        "comment_info")));
                holder.mCommentTxt
                        .setText(Html.fromHtml(commentsArray
                                .getJSONObject(position).getString(
                                        "comment_text")));
                if (type == TYPE_ITEM) {
                    holder.mPhotoLayoutView.setVisibility(
                            View.VISIBLE);

                    if (!TextUtils.isEmpty(commentsArray.getJSONObject(position).getString(
                            "comment_photo"))) {
                        mImgLoader.displayImage(
                                commentsArray.getJSONObject(position).getString(
                                        "comment_photo"),
                                holder.mPhotoImg, mOptions, new ImageLoadingListener() {
                                    @Override
                                    public void onLoadingStarted(String imageUri, View view) {
                                        ((ViewGroup) view.getParent()).findViewById(R.id.photoProgressBar).setVisibility(View.VISIBLE);
                                    }

                                    @Override
                                    public void onLoadingFailed(String imageUri, View view, FailReason failReason) {
                                        ((ViewGroup) view.getParent()).findViewById(R.id.errorImage).setVisibility(View.VISIBLE);
                                        ((ViewGroup) view.getParent()).findViewById(R.id.photoProgressBar).setVisibility(View.GONE);
                                    }

                                    @Override
                                    public void onLoadingComplete(String imageUri, View view, Bitmap loadedImage) {
                                        ((ViewGroup) view.getParent()).findViewById(R.id.errorImage).setVisibility(View.GONE);
                                        ((ViewGroup) view.getParent()).findViewById(R.id.photoProgressBar).setVisibility(View.GONE);
                                    }

                                    @Override
                                    public void onLoadingCancelled(String imageUri, View view) {
                                    }
                                }
                        );
                    }
                    if (!TextUtils.isEmpty(commentsArray.getJSONObject(position).getString(
                            "comment_photo")) & !TextUtils.isEmpty(commentsArray.getJSONObject(position).getString(
                            "comment_video"))) {
                        ((ViewGroup) holder.itemView).findViewById(R.id.videoIcon).setVisibility(View.VISIBLE);
                    } else {
                        ((ViewGroup) holder.itemView).findViewById(R.id.videoIcon).setVisibility(View.GONE);
                    }
                }

                setImageSmall(
                        holder.mPlateImageImg,
                        commentsArray.getJSONObject(position).getString(
                                "comment_plate_state"));

            } else {
                String worstNum = feedJSON.getString("worst_num");
                holder.mWorstText.setText(worstNum);
                holder.mWorstText.setSelected(true);

                String worstState = feedJSON.getString("worst_state");
                holder.mWorstComment.setText(Html
                        .fromHtml(feedJSON.getString("worst_comment")));
                setImage(holder.mWorstImage, worstState);

                holder.mCategoryBg.setTag(0);
            }
        } catch (JSONException e) {
        }
    }

    private void setImage(ImageView image, String itemState) {
        if (itemState.equals("AL")) {
            image.setImageResource(R.drawable.al);
        } else if (itemState.equals("AK")) {
            image.setImageResource(R.drawable.ak);
        } else if (itemState.equals("AZ")) {
            image.setImageResource(R.drawable.az);
        } else if (itemState.equals("AR")) {
            image.setImageResource(R.drawable.ar);
        } else if (itemState.equals("CA")) {
            image.setImageResource(R.drawable.ca);
        } else if (itemState.equals("CO")) {
            image.setImageResource(R.drawable.co);
        } else if (itemState.equals("CT")) {
            image.setImageResource(R.drawable.ct);
        } else if (itemState.equals("DC")) {
            image.setImageResource(R.drawable.dc);
        } else if (itemState.equals("DE")) {
            image.setImageResource(R.drawable.de);
        } else if (itemState.equals("FL")) {
            image.setImageResource(R.drawable.fl);
        } else if (itemState.equals("GA")) {
            image.setImageResource(R.drawable.ga);
        } else if (itemState.equals("GOV")) {
            image.setImageResource(R.drawable.gov);
        } else if (itemState.equals("HI")) {
            image.setImageResource(R.drawable.hi);
        } else if (itemState.equals("ID")) {
            image.setImageResource(R.drawable.id);
        } else if (itemState.equals("IL")) {
            image.setImageResource(R.drawable.il);
        } else if (itemState.equals("IN")) {
            image.setImageResource(R.drawable.in);
        } else if (itemState.equals("IA")) {
            image.setImageResource(R.drawable.ia);
        } else if (itemState.equals("KS")) {
            image.setImageResource(R.drawable.ks);
        } else if (itemState.equals("KY")) {
            image.setImageResource(R.drawable.ky);
        } else if (itemState.equals("LA")) {
            image.setImageResource(R.drawable.la);
        } else if (itemState.equals("ME")) {
            image.setImageResource(R.drawable.me);
        } else if (itemState.equals("MD")) {
            image.setImageResource(R.drawable.md);
        } else if (itemState.equals("MA")) {
            image.setImageResource(R.drawable.ma);
        } else if (itemState.equals("MI")) {
            image.setImageResource(R.drawable.mi);
        } else if (itemState.equals("MN")) {
            image.setImageResource(R.drawable.mn);
        } else if (itemState.equals("MS")) {
            image.setImageResource(R.drawable.ms);
        } else if (itemState.equals("MO")) {
            image.setImageResource(R.drawable.mo);
        } else if (itemState.equals("MT")) {
            image.setImageResource(R.drawable.mt);
        } else if (itemState.equals("NE")) {
            image.setImageResource(R.drawable.ne);
        } else if (itemState.equals("NV")) {
            image.setImageResource(R.drawable.nv);
        } else if (itemState.equals("NH")) {
            image.setImageResource(R.drawable.nh);
        } else if (itemState.equals("NJ")) {
            image.setImageResource(R.drawable.nj);
        } else if (itemState.equals("NM")) {
            image.setImageResource(R.drawable.nm);
        } else if (itemState.equals("NY")) {
            image.setImageResource(R.drawable.ny);
        } else if (itemState.equals("NC")) {
            image.setImageResource(R.drawable.nc);
        } else if (itemState.equals("ND")) {
            image.setImageResource(R.drawable.nd);
        } else if (itemState.equals("OH")) {
            image.setImageResource(R.drawable.oh);
        } else if (itemState.equals("OK")) {
            image.setImageResource(R.drawable.ok);
        } else if (itemState.equals("OR")) {
            image.setImageResource(R.drawable.or);
        } else if (itemState.equals("PA")) {
            image.setImageResource(R.drawable.pa);
        } else if (itemState.equals("RI")) {
            image.setImageResource(R.drawable.ri);
        } else if (itemState.equals("SC")) {
            image.setImageResource(R.drawable.sc);
        } else if (itemState.equals("SD")) {
            image.setImageResource(R.drawable.sd);
        } else if (itemState.equals("TN")) {
            image.setImageResource(R.drawable.tn);
        } else if (itemState.equals("TX")) {
            image.setImageResource(R.drawable.tx);
        } else if (itemState.equals("UT")) {
            image.setImageResource(R.drawable.ut);
        } else if (itemState.equals("VT")) {
            image.setImageResource(R.drawable.vt);
        } else if (itemState.equals("VA")) {
            image.setImageResource(R.drawable.va);
        } else if (itemState.equals("WA")) {
            image.setImageResource(R.drawable.wa);
        } else if (itemState.equals("WV")) {
            image.setImageResource(R.drawable.wv);
        } else if (itemState.equals("WI")) {
            image.setImageResource(R.drawable.wi);
        } else if (itemState.equals("WY")) {
            image.setImageResource(R.drawable.wy);
        }
    }

    private void setImageSmall(ImageView image, String itemState) {
        if (itemState.equals("AL")) {
            image.setImageResource(R.drawable.al_sm);
        } else if (itemState.equals("AK")) {
            image.setImageResource(R.drawable.ak_sm);
        } else if (itemState.equals("AZ")) {
            image.setImageResource(R.drawable.az_sm);
        } else if (itemState.equals("AR")) {
            image.setImageResource(R.drawable.ar_sm);
        } else if (itemState.equals("CA")) {
            image.setImageResource(R.drawable.ca_sm);
        } else if (itemState.equals("CO")) {
            image.setImageResource(R.drawable.co_sm);
        } else if (itemState.equals("CT")) {
            image.setImageResource(R.drawable.ct_sm);
        } else if (itemState.equals("DC")) {
            image.setImageResource(R.drawable.dc_sm);
        } else if (itemState.equals("DE")) {
            image.setImageResource(R.drawable.de_sm);
        } else if (itemState.equals("FL")) {
            image.setImageResource(R.drawable.fl_sm);
        } else if (itemState.equals("GA")) {
            image.setImageResource(R.drawable.ga_sm);
        } else if (itemState.equals("GOV")) {
            image.setImageResource(R.drawable.gov_sm);
        } else if (itemState.equals("HI")) {
            image.setImageResource(R.drawable.hi_sm);
        } else if (itemState.equals("ID")) {
            image.setImageResource(R.drawable.id_sm);
        } else if (itemState.equals("IL")) {
            image.setImageResource(R.drawable.il_sm);
        } else if (itemState.equals("IN")) {
            image.setImageResource(R.drawable.in_sm);
        } else if (itemState.equals("IA")) {
            image.setImageResource(R.drawable.ia_sm);
        } else if (itemState.equals("KS")) {
            image.setImageResource(R.drawable.ks_sm);
        } else if (itemState.equals("KY")) {
            image.setImageResource(R.drawable.ky_sm);
        } else if (itemState.equals("LA")) {
            image.setImageResource(R.drawable.la_sm);
        } else if (itemState.equals("ME")) {
            image.setImageResource(R.drawable.me_sm);
        } else if (itemState.equals("MD")) {
            image.setImageResource(R.drawable.md_sm);
        } else if (itemState.equals("MA")) {
            image.setImageResource(R.drawable.ma_sm);
        } else if (itemState.equals("MI")) {
            image.setImageResource(R.drawable.mi_sm);
        } else if (itemState.equals("MN")) {
            image.setImageResource(R.drawable.mn_sm);
        } else if (itemState.equals("MS")) {
            image.setImageResource(R.drawable.ms_sm);
        } else if (itemState.equals("MO")) {
            image.setImageResource(R.drawable.mo_sm);
        } else if (itemState.equals("MT")) {
            image.setImageResource(R.drawable.mt_sm);
        } else if (itemState.equals("NE")) {
            image.setImageResource(R.drawable.ne_sm);
        } else if (itemState.equals("NV")) {
            image.setImageResource(R.drawable.nv_sm);
        } else if (itemState.equals("NH")) {
            image.setImageResource(R.drawable.nh_sm);
        } else if (itemState.equals("NJ")) {
            image.setImageResource(R.drawable.nj_sm);
        } else if (itemState.equals("NM")) {
            image.setImageResource(R.drawable.nm_sm);
        } else if (itemState.equals("NY")) {
            image.setImageResource(R.drawable.ny_sm);
        } else if (itemState.equals("NC")) {
            image.setImageResource(R.drawable.nc_sm);
        } else if (itemState.equals("ND")) {
            image.setImageResource(R.drawable.nd_sm);
        } else if (itemState.equals("OH")) {
            image.setImageResource(R.drawable.oh_sm);
        } else if (itemState.equals("OK")) {
            image.setImageResource(R.drawable.ok_sm);
        } else if (itemState.equals("OR")) {
            image.setImageResource(R.drawable.or_sm);
        } else if (itemState.equals("PA")) {
            image.setImageResource(R.drawable.pa_sm);
        } else if (itemState.equals("RI")) {
            image.setImageResource(R.drawable.ri_sm);
        } else if (itemState.equals("SC")) {
            image.setImageResource(R.drawable.sc_sm);
        } else if (itemState.equals("SD")) {
            image.setImageResource(R.drawable.sd_sm);
        } else if (itemState.equals("TN")) {
            image.setImageResource(R.drawable.tn_sm);
        } else if (itemState.equals("TX")) {
            image.setImageResource(R.drawable.tx_sm);
        } else if (itemState.equals("UT")) {
            image.setImageResource(R.drawable.ut_sm);
        } else if (itemState.equals("VT")) {
            image.setImageResource(R.drawable.vt_sm);
        } else if (itemState.equals("VA")) {
            image.setImageResource(R.drawable.va_sm);
        } else if (itemState.equals("WA")) {
            image.setImageResource(R.drawable.wa_sm);
        } else if (itemState.equals("WV")) {
            image.setImageResource(R.drawable.wv_sm);
        } else if (itemState.equals("WI")) {
            image.setImageResource(R.drawable.wi_sm);
        } else if (itemState.equals("WY")) {
            image.setImageResource(R.drawable.wy_sm);
        }
    }

    @Override
    public int getItemViewType(int position) {
        if (position == 0) {
            return TYPE_HEADER;
        } else {
            try {
                if (TextUtils.isEmpty(commentsArray.getJSONObject(position - 1)
                        .getString("comment_photo")) & TextUtils.isEmpty(commentsArray.getJSONObject(position - 1)
                        .getString("comment_video"))) {
                    return TYPE_ITEM_WITHOUT_PHOTO;
                } else {
                    return TYPE_ITEM;
                }
            } catch (JSONException e) {
                return TYPE_ITEM;
            }
        }
    }

    // Return the size of your dataset (invoked by the layout manager)
    @Override
    public int getItemCount() {
        if (commentsArray == null)
            return 0;
        else
            return 1 + commentsArray.length();
    }
}
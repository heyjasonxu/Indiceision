package edu.uw.info448.indiceision;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by Jason on 12/5/2017.
 */

public class ReviewsAdapter extends BaseAdapter {

    private Context mContext;
    private LayoutInflater mInflater;
    private List<Review> mDataSource;

    public ReviewsAdapter(Context context, List<Review> items) {
        mContext = context;
        mDataSource = items;
        mInflater = (LayoutInflater) mContext.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
    }

    @Override
    public int getCount() {
        return mDataSource.size();
    }

    //2
    @Override
    public Object getItem(int position) {
        return mDataSource.get(position);
    }

    //3
    @Override
    public long getItemId(int position) {
        return position;
    }

    //4
    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        Review r = (Review) getItem(position);
        // Get view for row item
        View rowView = mInflater.inflate(R.layout.review_item, parent, false);
        TextView name = rowView.findViewById(R.id.Name);
        TextView rating = rowView.findViewById(R.id.Rating);
        TextView date = rowView.findViewById(R.id.Date);
        TextView review = rowView.findViewById(R.id.Review);

        name.setText(r.getName());
        rating.setText("  Rating: " + r.getRating());
        date.setText("    " + r.getDate());
        review.setText(r.getReview());
        return rowView;
    }
}

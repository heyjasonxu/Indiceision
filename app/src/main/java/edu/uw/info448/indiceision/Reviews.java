package edu.uw.info448.indiceision;

import android.app.ListActivity;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ListView;
import android.widget.TextView;

import java.util.ArrayList;
import java.util.List;

public class Reviews extends AppCompatActivity {

//    private RecyclerView mRecyclerView;
//    private RecyclerView.Adapter mAdapter;
//    private RecyclerView.LayoutManager mLayoutManager;

    private ListView mListView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_reviews);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        FloatingActionButton fab = (FloatingActionButton) findViewById(R.id.fab);
        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Snackbar.make(view, "Replace with your own action", Snackbar.LENGTH_LONG)
                        .setAction("Action", null).show();
            }
        });

        mListView = (ListView) findViewById(R.id.reviews_list_view);
// 1
        final List<Review> reviewList = DrawingSurfaceView.reviews;
// 2
        String[] listItems = new String[reviewList.size()];
// 3
        for(int i = 0; i < reviewList.size(); i++){
            Review review = reviewList.get(i);
            listItems[i] = review.getName();
        }
// 4
        ReviewsAdapter adapter = new ReviewsAdapter(this, reviewList);
        mListView.setAdapter(adapter);
    }
//
//    public class SimpleItemRecyclerViewAdapter
//            extends RecyclerView.Adapter<SimpleItemRecyclerViewAdapter.ViewHolder> {
//
//        private final List<Review> mValues;
//
//        public SimpleItemRecyclerViewAdapter(List<Review> items) {
//            mValues = items;
//        }
//
//        @Override
//        public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
//            View view = LayoutInflater.from(parent.getContext())
//                    .inflate(R.layout.review_item, parent, false);
//            return new ViewHolder(view);
//        }
//
//        @Override
//        public void onBindViewHolder(final ViewHolder holder, int position) {
//            holder.mItem = mValues.get(position);
////            holder.mIdView.setText(mValues.get(position).id);
////            holder.mContentView.setText(mValues.get(position).id);
//
//            holder.name.setText(mValues.get(position).getName());
//            holder.date.setText(mValues.get(position).getDate());
//            holder.rating.setText(mValues.get(position).getRating());
//            holder.text.setText(mValues.get(position).getReview());
//
////            holder.mView.setOnClickListener(new View.OnClickListener() {
////                @Override
////                public void onClick(View v) {
////                    if (mTwoPane) {
////                        Bundle arguments = new Bundle();
////                        arguments.putString(NewsArticleDetailFragment.ARG_ITEM_ID, holder.mItem.id);
//////                        arguments.putString(NewsArticleDetailFragment.ARG_ITEM_ID, "1");
////                        NewsArticleDetailFragment fragment = new NewsArticleDetailFragment();
////                        fragment.setArguments(arguments);
////                        getSupportFragmentManager().beginTransaction()
////                                .replace(R.id.newsarticle_detail_container, fragment)
////                                .commit();
////                    } else {
////                        Context context = v.getContext();
////                        Intent intent = new Intent(context, NewsArticleDetailActivity.class);
////                        intent.putExtra(NewsArticleDetailFragment.ARG_ITEM_ID, holder.mItem.id);
////                        intent.putExtra("Headline", holder.mItem.headline);
////                        intent.putExtra("Details", holder.mItem.description);
////                        intent.putExtra("Link", holder.mItem.webUrl);
////                        intent.putExtra("Image", holder.mItem.imageUrl);
////
//////                        intent.putExtra(NewsArticleDetailFragment.ARG_ITEM_ID, holder.mItem.headline);
////
////                        context.startActivity(intent);
////                    }
////                }
////            });
//        }
//
//        @Override
//        public int getItemCount() {
//            return mValues.size();
//        }
//
//        public class ViewHolder extends RecyclerView.ViewHolder {
//            public final View mView;
//            public final TextView name;
//            public final TextView rating;
//            public final TextView date;
//            public final TextView text;
//            public Review mItem;
//
//            public ViewHolder(View view) {
//                super(view);
//                mView = view;
//                name = (TextView) view.findViewById(R.id.Name);
//                rating = (TextView) view.findViewById(R.id.Rating);
//                date = (TextView) view.findViewById(R.id.Date);
//                text = (TextView) view.findViewById(R.id.Review);
//            }
//
//            @Override
//            public String toString() {
//                return super.toString() + " '" + name.getText() + "'";
//            }
//        }
//    }

}

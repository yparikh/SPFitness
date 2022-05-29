package com.example.senior_proj.ui.nutrition;

import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.RecyclerView;

import android.app.Activity;
import android.content.Intent;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.example.senior_proj.R;
import com.example.senior_proj.fdc.Food;

import java.util.List;
import java.util.Objects;


public class MyStringRecyclerViewAdapter extends RecyclerView.Adapter<MyStringRecyclerViewAdapter.ViewHolder>  {

    private static final String TAG = "ResultTag";
    private final List<Food> mValues;
    private Fragment foodResultFragment;

    public MyStringRecyclerViewAdapter(List<Food> items, Fragment FoodResultFragment) {
        mValues = items;
        foodResultFragment = FoodResultFragment;
    }

    @Override
    public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.fragment_food_result, parent, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(final ViewHolder holder, int position) {
        holder.mItem = mValues.get(position);
        holder.mIdView.setText(String.valueOf(mValues.get(position).getFdcId()) );
        holder.mContentView.setText(mValues.get(position).getDescription());

        Log.d(TAG, "onBindViewHolder: " + mValues.get(position).getFdcId());
    }

    @Override
    public int getItemCount() {
        return mValues.size();
    }

    public class ViewHolder extends RecyclerView.ViewHolder  implements View.OnClickListener{
        public final View mView;
        public final TextView mIdView;
        public final TextView mContentView;
        public Food mItem;


        public ViewHolder(View view) {
            super(view);
            mView = view;
            mIdView = (TextView) view.findViewById(R.id.item_number);
            mContentView = (TextView) view.findViewById(R.id.content);
            view.setOnClickListener(this);

        }

        @Override
        public void onClick(View v) {
            String fdcID = mIdView.getText().toString();
            Intent intent = new Intent(foodResultFragment.getContext(), foodResultFragment.getClass());
            intent.putExtra("FDCID", fdcID);

            foodResultFragment.getActivity();
            Objects.requireNonNull(foodResultFragment.getTargetFragment()).onActivityResult(foodResultFragment.getTargetRequestCode(),
                    Activity.RESULT_OK, intent);
            foodResultFragment.getParentFragmentManager().popBackStackImmediate();

        }

        @Override
        public String toString() {
            return super.toString() + " '" + mContentView.getText() + "'";
        }
    }
}
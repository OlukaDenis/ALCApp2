package com.mcdenny.alcapp2.adapter;

import android.content.Intent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.RecyclerView;

import com.google.firebase.database.ChildEventListener;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.mcdenny.alcapp2.FirebaseUtil;
import com.mcdenny.alcapp2.R;
import com.mcdenny.alcapp2.TravelList;
import com.mcdenny.alcapp2.UpdateActivity;
import com.mcdenny.alcapp2.model.TravelDeal;
import com.mcdenny.alcapp2.viewholder.ItemClickListener;
import com.mcdenny.alcapp2.viewholder.TraveDealViewHolder;
import com.squareup.picasso.Picasso;

import java.text.DecimalFormat;
import java.text.NumberFormat;
import java.util.ArrayList;

public class TravelDealAdapter extends RecyclerView.Adapter<TraveDealViewHolder> {
    private ArrayList<TravelDeal> travelDeals;
    private TravelList activity;

    private FirebaseDatabase database;
    private DatabaseReference travelReference;
    private ChildEventListener childEventListener;

    public TravelDealAdapter(TravelList activity) {
        this.activity = activity;
        FirebaseUtil.openFirebaseReference("traveldeals", activity);

        database = FirebaseUtil.mFirebaseDatabase;
        travelReference = FirebaseUtil.mDatabaseReference;
        travelDeals = FirebaseUtil.travelDeals;
        childEventListener = new ChildEventListener() {
            @Override
            public void onChildAdded(@NonNull DataSnapshot dataSnapshot, @Nullable String s) {
                if (dataSnapshot.exists()) {

                    TravelDeal deals = dataSnapshot.getValue(TravelDeal.class);
                    // Log.d("Deal: ", deals.getTitle());
                    deals.setId(dataSnapshot.getKey());
                    travelDeals.add(deals);
                    notifyItemInserted(travelDeals.size() - 1);
                }

            }

            @Override
            public void onChildChanged(@NonNull DataSnapshot dataSnapshot, @Nullable String s) {

            }

            @Override
            public void onChildRemoved(@NonNull DataSnapshot dataSnapshot) {

            }

            @Override
            public void onChildMoved(@NonNull DataSnapshot dataSnapshot, @Nullable String s) {

            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        };
        travelReference.addChildEventListener(childEventListener);
    }


    @NonNull
    @Override
    public TraveDealViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.layout_travel_deal, parent, false);
        return new TraveDealViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull TraveDealViewHolder holder, int position) {
        String money = formatCurrency(travelDeals.get(position).getPrice());
        holder.price.setText(money);

        holder.description.setText(travelDeals.get(position).getDescription());

        holder.title.setText(travelDeals.get(position).getTitle());
        String image_url = travelDeals.get(position).getImage();

        Picasso.get()
                .load(image_url)
                .placeholder(R.drawable.ic_action_name)
                .error(R.drawable.ic_action_name)
                .into(holder.image);



        holder.setItemClickListener(new ItemClickListener() {
            @Override
            public void onClick(View view, int position, boolean isLongClick) {
                //Bundle bundle = new Bundle();
                AppCompatActivity activity = (AppCompatActivity) view.getContext();
                TravelDeal selectedDeal = travelDeals.get(position);
                Intent intent = new Intent(view.getContext(), UpdateActivity.class);
                intent.putExtra("TravelDeal", selectedDeal);
                // bundle.putSerializable("TravelDeal", selectedDeal);
                view.getContext().startActivity(intent);

                //open a fragment
                /**
                 UpdateDealFragmet fragment = new UpdateDealFragmet();
                 fragment.setArguments(bundle);
                 FragmentManager fm = activity.getSupportFragmentManager();
                 FragmentTransaction fragmentTransaction = fm.beginTransaction();
                 fragmentTransaction.replace(R.id.fragment_container, fragment);
                 fragmentTransaction.setCustomAnimations(R.anim.fui_slide_out_left, R.anim.fui_slide_in_right);
                 fragmentTransaction.commit();
                 */
                //travelDeals.clear();
            }
        });
    }

    @Override
    public int getItemCount() {
        return travelDeals.size();
    }

    private static String formatCurrency(String money) {
        NumberFormat formatter = new DecimalFormat("#,###");
        double currency = Double.parseDouble(money);
        String formattedCurrency = formatter.format(currency);
        return "UGX " + formattedCurrency;
    }
}

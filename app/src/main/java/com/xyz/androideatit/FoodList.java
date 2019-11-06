package com.xyz.androideatit;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.content.Intent;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import com.firebase.ui.database.FirebaseRecyclerAdapter;
import com.firebase.ui.database.FirebaseRecyclerOptions;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.mancj.materialsearchbar.MaterialSearchBar;
import com.squareup.picasso.Picasso;
import com.xyz.androideatit.Common.Common;
import com.xyz.androideatit.Database.Database;
import com.xyz.androideatit.Interface.ItemClickListener;
import com.xyz.androideatit.Model.Category;
import com.xyz.androideatit.Model.Food;
import com.xyz.androideatit.ViewHolder.FoodViewHolder;
import com.xyz.androideatit.ViewHolder.MenuViewHolder;

import java.util.ArrayList;
import java.util.List;

public class FoodList extends AppCompatActivity {

    RecyclerView recyclerView;
    RecyclerView.LayoutManager layoutManager;

    FirebaseDatabase database;
    DatabaseReference foodList;
    FirebaseRecyclerAdapter<Food, FoodViewHolder> adapter;
    String categoryId = "";

    // search functionalllity
    FirebaseRecyclerAdapter<Food, FoodViewHolder> searchAdapter;
    List<String> suggestList = new ArrayList<>();
    MaterialSearchBar materialSearchBar;

    Database localDB;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_food_list);

        //init firebase
        database = FirebaseDatabase.getInstance();
        foodList = database.getReference("Foods");

        localDB = new Database(this);

        recyclerView = findViewById(R.id.recycler_food);
        recyclerView.hasFixedSize();
        layoutManager = new LinearLayoutManager(this);
        recyclerView.setLayoutManager(layoutManager);

        // get the position through intent of category id
        if (getIntent() != null)
            categoryId = getIntent().getStringExtra("CategoryId");
        if (!categoryId.isEmpty() && categoryId != null) {
            if (Common.isConnectedToInternet(getBaseContext()))
                loadListFood(categoryId);
            else {
                Toast.makeText(this, "Please Check the Internet Connection", Toast.LENGTH_SHORT).show();
                return;
            }
        }

        // Search
        materialSearchBar = findViewById(R.id.searchBar);
        materialSearchBar.setHint("Enter your Food");
        loadSuggest(); //  function for suggesting from firebase
        materialSearchBar.setLastSuggestions(suggestList);
        materialSearchBar.setCardViewElevation(10);
        materialSearchBar.addTextChangeListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                // when user type their text , change the suggest list
                List<String> suggest = new ArrayList<String>();
                for (String search : suggestList) {  // looping di suggestLIst
                    if (search.toLowerCase().contains(materialSearchBar.getText().toLowerCase()))
                        suggest.add(search);
                }
                materialSearchBar.setLastSuggestions(suggest);
                ;
            }

            @Override
            public void afterTextChanged(Editable s) {

            }
        });
        materialSearchBar.setOnSearchActionListener(new MaterialSearchBar.OnSearchActionListener() {
            @Override
            public void onSearchStateChanged(boolean enabled) {
                //when the search bar is close restore original adapter
                if (!enabled) {
                    recyclerView.setAdapter(adapter);
                }
            }

            @Override
            public void onSearchConfirmed(CharSequence text) {
                // when search is finish show the result of adapter
                startSearch(text);

            }

            @Override
            public void onButtonClicked(int buttonCode) {

            }
        });

    }

    private void startSearch(CharSequence text) {
        FirebaseRecyclerOptions<Food> options =
                new FirebaseRecyclerOptions.Builder<Food>().setQuery(foodList.orderByChild("name").equalTo(text.toString()), Food.class).build(); // compare name

        searchAdapter = new FirebaseRecyclerAdapter<Food, FoodViewHolder>(options) {
            @Override
            protected void onBindViewHolder(@NonNull final FoodViewHolder foodViewHolder, final int i, @NonNull final Food food) {
                foodViewHolder.food_name.setText(food.getName());
                Picasso.get().load(food.getImage()).into(foodViewHolder.food_image);

                // add to favourites
                if (localDB.isFavorites(adapter.getRef(i).getKey()))
                    foodViewHolder.fav_image.setImageResource(R.drawable.ic_favorite_black_24dp);

                // click to change the favourites
                foodViewHolder.fav_image.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        if (!localDB.isFavorites(adapter.getRef(i).getKey())) {
                            localDB.addToFavourites(adapter.getRef(i).getKey());
                            foodViewHolder.fav_image.setImageResource(R.drawable.ic_favorite_black_24dp);
                            Toast.makeText(FoodList.this, "" + food.getName() + "is Added to Favourites", Toast.LENGTH_SHORT).show();
                        } else {
                            localDB.removeFromFavourites(adapter.getRef(i).getKey());
                            foodViewHolder.fav_image.setImageResource(R.drawable.ic_favorite_border_black_24dp);
                            Toast.makeText(FoodList.this, "" + food.getName() + " is remove from Favourites", Toast.LENGTH_SHORT).show();
                        }
                    }
                });

                foodViewHolder.setItemClickListener(new ItemClickListener() {
                    @Override
                    public void onClick(View view, int position, boolean isLongClick) {
                        // Start activity of food details
                        Intent foodDetails = new Intent(FoodList.this, FoodDetails.class);
                        foodDetails.putExtra("FoodId", searchAdapter.getRef(position).getKey()); //send FoodId to new Activity
                        startActivity(foodDetails);
                    }
                });
            }

            @NonNull
            @Override
            public FoodViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
                View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.food_item, parent, false);
                return new FoodViewHolder(view);
            }
        };
        searchAdapter.startListening();
        searchAdapter.notifyDataSetChanged();
        recyclerView.setAdapter(searchAdapter); // set adapter for recycle view is search result

    }


    private void loadSuggest() {
        foodList.orderByChild("menuId").equalTo(categoryId).addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                for (DataSnapshot postSnapshot : dataSnapshot.getChildren()) {
                    Food item = postSnapshot.getValue(Food.class);
                    suggestList.add(item.getName()); // add name of food to suggest list
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });
    }

    private void loadListFood(String categoryId) {


        FirebaseRecyclerOptions<Food> options =
                new FirebaseRecyclerOptions.Builder<Food>().setQuery(foodList.orderByChild("menuId").equalTo(categoryId), Food.class).build();

        adapter = new FirebaseRecyclerAdapter<Food, FoodViewHolder>(options) {
            @Override
            protected void onBindViewHolder(@NonNull FoodViewHolder foodViewHolder, int i, @NonNull final Food food) {
                foodViewHolder.food_name.setText(food.getName());
                Picasso.get().load(food.getImage()).into(foodViewHolder.food_image);

                foodViewHolder.setItemClickListener(new ItemClickListener() {
                    @Override
                    public void onClick(View view, int position, boolean isLongClick) {
                        // Start activity of food details
                        Intent foodDetails = new Intent(FoodList.this, FoodDetails.class);
                        foodDetails.putExtra("FoodId", adapter.getRef(position).getKey()); //send FoodId to new Activity
                        startActivity(foodDetails);
                    }
                });

            }

            @NonNull
            @Override
            public FoodViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
                View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.food_item, parent, false);
                return new FoodViewHolder(view);
            }
        };
        //set Adapter
        adapter.startListening();
        adapter.notifyDataSetChanged();
        recyclerView.setAdapter(adapter);
    }
}

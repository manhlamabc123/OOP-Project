package com.example.oopproject;

import static android.view.View.*;

import android.app.ProgressDialog;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.Menu;
import android.view.ViewGroup;
import android.widget.TextView;

import com.example.oopproject.classes.Product;
import com.example.oopproject.classes_for_controll.Prevalent;
import com.example.oopproject.classes_for_controll.ProductViewHolder;
import com.firebase.ui.database.FirebaseRecyclerAdapter;
import com.firebase.ui.database.FirebaseRecyclerOptions;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.android.material.navigation.NavigationView;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

import androidx.annotation.NonNull;
import androidx.appcompat.app.ActionBarDrawerToggle;
import androidx.core.view.GravityCompat;
import androidx.drawerlayout.widget.DrawerLayout;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import de.hdodenhof.circleimageview.CircleImageView;
import io.paperdb.Paper;

public class HomeActivity extends AppCompatActivity implements NavigationView.OnNavigationItemSelectedListener{

    private RecyclerView recyclerView;
    RecyclerView.LayoutManager layoutManager;
    private DatabaseReference productReference;
    private int productCounter = 1;
    private ProgressDialog loadingBar;

    @Override protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_home);

        loadingBar = new ProgressDialog(this);
        //-------------------------Get product's data from server-------------------------
        productReference = FirebaseDatabase.getInstance().getReference().child("Product");
        //-----------------------------------------------------------------
        //-------------------------Toolbar Setup-------------------------
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        toolbar.setTitle("Home");
        setSupportActionBar(toolbar);
        //-----------------------------------------------------------------
        //-------------------------Floating Forward Button-------------------------
        FloatingActionButton fabForward = (FloatingActionButton) findViewById(R.id.fab_forward);
        fabForward.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View view) {
                //-------------------Loading Bar-------------------
                loadingBar.setTitle("Please wait");
                loadingBar.setMessage("Loading...");
                loadingBar.setCanceledOnTouchOutside(false);
                loadingBar.show();
                //------------------------------------------------------------------------------------------

                if (productCounter < 100) {
                    productCounter += 10;
                    String productIdString = "P0" + productCounter;
                    FirebaseRecyclerOptions<Product> options =
                            new FirebaseRecyclerOptions.Builder<Product>()
                                    .setQuery(productReference.orderByKey().startAt(productIdString).limitToFirst(10), Product.class)
                                    .build();

                    FirebaseRecyclerAdapter<Product, ProductViewHolder> adapter =
                            new FirebaseRecyclerAdapter<Product, ProductViewHolder>(options) {
                                @Override
                                protected void onBindViewHolder(@NonNull ProductViewHolder holder, int position, @NonNull Product model) {

                                    //------------------------------Retrieve Info from Database------------------------------
                                    holder.textProductName.setText(model.getName());
                                    //------------------------------------------------------------------------------------------

                                    //------------------------------to Product Details Activity------------------------------
                                    holder.itemView.setOnClickListener(new OnClickListener() {
                                        @Override
                                        public void onClick(View view) {
                                            Intent intent = new Intent(HomeActivity.this, ProductDetailsActivity.class);
                                            intent.putExtra("productID", model.getId());
                                            startActivity(intent);
                                        }
                                    });
                                    //--------------------------------------------------------------------------------
                                }

                                @NonNull
                                @Override
                                public ProductViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
                                    View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.products_items_layout, parent, false);
                                    ProductViewHolder holder = new ProductViewHolder(view);
                                    return holder;
                                }
                            };
                    recyclerView.setAdapter(adapter);
                    loadingBar.dismiss();
                    adapter.startListening();
                } else {
                    Log.d("WARNING", "Too many");
                }
            }
        });
        //-----------------------------------------------------------------

        //-------------------------Floating Forward Button-------------------------
        FloatingActionButton fabBackward = (FloatingActionButton) findViewById(R.id.fab_backward);
        fabBackward.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View view) {
                if (productCounter > 10) {
                    //-------------------Loading Bar-------------------
                    loadingBar.setTitle("Please wait");
                    loadingBar.setMessage("Loading...");
                    loadingBar.setCanceledOnTouchOutside(false);
                    loadingBar.show();
                    //------------------------------------------------------------------------------------------

                    productCounter -= 10;
                    String productIdString = "P0" + productCounter;
                    if (productCounter <= 9 && productCounter >=1 ) productIdString = "P00" + productCounter;
                    FirebaseRecyclerOptions<Product> options =
                            new FirebaseRecyclerOptions.Builder<Product>()
                                    .setQuery(productReference.orderByKey().startAt(productIdString).limitToFirst(10), Product.class)
                                    .build();

                    FirebaseRecyclerAdapter<Product, ProductViewHolder> adapter =
                            new FirebaseRecyclerAdapter<Product, ProductViewHolder>(options) {
                                @Override
                                protected void onBindViewHolder(@NonNull ProductViewHolder holder, int position, @NonNull Product model) {

                                    //------------------------------Retrieve Info from Database------------------------------
                                    holder.textProductName.setText(model.getName());
                                    //------------------------------------------------------------------------------------------

                                    //------------------------------to Product Details Activity------------------------------
                                    holder.itemView.setOnClickListener(new OnClickListener() {
                                        @Override
                                        public void onClick(View view) {
                                            Intent intent = new Intent(HomeActivity.this, ProductDetailsActivity.class);
                                            intent.putExtra("productID", model.getId());
                                            startActivity(intent);
                                        }
                                    });
                                    //--------------------------------------------------------------------------------
                                }

                                @NonNull
                                @Override
                                public ProductViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
                                    View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.products_items_layout, parent, false);
                                    ProductViewHolder holder = new ProductViewHolder(view);
                                    return holder;
                                }
                            };
                    recyclerView.setAdapter(adapter);
                    loadingBar.dismiss();
                    adapter.startListening();
                } else {
                    Log.d("WARNING", "Too many");
                }
            }
        });
        //-----------------------------------------------------------------

        //-------------------------Drawer Layout-------------------------
        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        ActionBarDrawerToggle toggle = new ActionBarDrawerToggle(this, drawer, toolbar, R.string.navigation_drawer_open, R.string.navigation_drawer_close);
        drawer.addDrawerListener(toggle);
        toggle.syncState();
        //-----------------------------------------------------------------

        //-------------------------Navigation View-------------------------
        NavigationView navigationView = (NavigationView) findViewById(R.id.nav_view);
        navigationView.setNavigationItemSelectedListener(this);
        //-----------------------------------------------------------------

        //-------------------------User Setting-------------------------
        View headerView = navigationView.getHeaderView(0);
        TextView userNameTextView = headerView.findViewById(R.id.user_profile_name);
        CircleImageView profileImageView = headerView.findViewById(R.id.user_profile_image);

        if (Prevalent.getCurrentCustomer() != null) userNameTextView.setText(Prevalent.getCurrentCustomer().getName());
        //-----------------------------------------------------------------

        //-------------------------Show Products on screen-------------------------
        recyclerView = findViewById(R.id.recycler_menu);
        recyclerView.setHasFixedSize(true);
        layoutManager = new LinearLayoutManager(this);
        recyclerView.setLayoutManager(layoutManager);
        //-----------------------------------------------------------------
    }

    @Override protected void onStart() {
        super.onStart();

        //-------------------Loading Bar-------------------
        loadingBar.setTitle("Please wait");
        loadingBar.setMessage("Loading...");
        loadingBar.setCanceledOnTouchOutside(false);
        loadingBar.show();
        //------------------------------------------------------------------------------------------

        String productIdString = "P0" + productCounter;
        if (productCounter <= 9 && productCounter >=1 ) productIdString = "P00" + productCounter;
        FirebaseRecyclerOptions<Product> options =
                new FirebaseRecyclerOptions.Builder<Product>()
                        .setQuery(productReference.orderByKey().startAt(productIdString).limitToFirst(10), Product.class)
                        .build();

        FirebaseRecyclerAdapter<Product, ProductViewHolder> adapter =
                new FirebaseRecyclerAdapter<Product, ProductViewHolder>(options) {
                    @Override
                    protected void onBindViewHolder(@NonNull ProductViewHolder holder, int position, @NonNull Product model) {

                        //------------------------------Retrieve Info from Database------------------------------
                        holder.textProductName.setText(model.getName());
                        //------------------------------------------------------------------------------------------

                        //------------------------------to Product Details Activity------------------------------
                        holder.itemView.setOnClickListener(new OnClickListener() {
                            @Override
                            public void onClick(View view) {
                                Intent intent = new Intent(HomeActivity.this, ProductDetailsActivity.class);
                                intent.putExtra("productID", model.getId());
                                startActivity(intent);
                            }
                        });
                        //------------------------------------------------------------------------------------------
                    }

                    @NonNull
                    @Override
                    public ProductViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
                        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.products_items_layout, parent, false);
                        ProductViewHolder holder = new ProductViewHolder(view);
                        return holder;
                    }
                };
        recyclerView.setAdapter(adapter);
        loadingBar.dismiss();
        adapter.startListening();
    }

    @Override public void onBackPressed() {
        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        if (drawer.isDrawerOpen(GravityCompat.START)) {
            drawer.closeDrawer(GravityCompat.START);
        } else {
            super.onBackPressed();
        }
    }

    @Override public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.home, menu);
        return true;
    }

    @Override public boolean onOptionsItemSelected(MenuItem item) {
        return super.onOptionsItemSelected(item);
    }

    @Override public boolean onNavigationItemSelected(MenuItem item) {
        int id = item.getItemId();
        switch (id) {
            case R.id.nav_cart:
                Intent intentCartActivity = new Intent(HomeActivity.this, CartActivity.class);
                startActivity(intentCartActivity);
                break;
            case R.id.nav_employee:
                Intent intentEmployeeActivity = new Intent(HomeActivity.this, EmployeeActivity.class);
                startActivity(intentEmployeeActivity);
                break;
            case R.id.nav_orders:
                Intent intentOrder = new Intent(HomeActivity.this, OrderActivity.class);
                startActivity(intentOrder);
                break;
            case R.id.nav_search:
                Intent intentSearch = new Intent(HomeActivity.this, SearchActivity.class);
                startActivity(intentSearch);
                break;
            case R.id.nav_settings:
                Intent intent = new Intent(HomeActivity.this, SettingsActivity.class);
                startActivity(intent);
                break;
            case R.id.nav_logout:
                Paper.book().destroy();
                Prevalent.setCurrentCustomer(null);
                Intent intent1 = new Intent(HomeActivity.this, MainActivity.class);
                intent1.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
                startActivity(intent1);
                finish();
                break;
        }

        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        drawer.closeDrawer(GravityCompat.START);
        return true;
    }
}
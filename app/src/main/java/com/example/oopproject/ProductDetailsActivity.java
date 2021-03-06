package com.example.oopproject;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.app.ProgressDialog;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import com.example.oopproject.classes_for_controll.Prevalent;
import com.example.oopproject.classes.Details;
import com.example.oopproject.classes.Product;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.Query;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.List;

public class ProductDetailsActivity extends AppCompatActivity {

    private String productID = "";
    private int counter = 0;

    private ImageView minusButton;
    private ImageView plusButton;
    private TextView textCounter;

    private TextView productName;
    private TextView productWarranty;
    private TextView productPrice;
    private TextView productCategory;
    private TextView productDescription;
    private TextView productInclude;
    private TextView productManufacturer;
    private TextView productMemory;
    private TextView productOS;
    private TextView productRAM;
    private TextView productScreen;
    private Spinner productColor;
    private TextView productQuantity;

    private Button addToCartButton;

    private ProgressDialog loadingBar;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_product_details);

        //------------------------------Data from Home Activity------------------------------
        productID = getIntent().getStringExtra("productID");
        //------------------------------------------------------------------------------------------

        //------------------------------Connect to UI------------------------------
        minusButton = (ImageView) findViewById(R.id.image_minus);
        plusButton = (ImageView) findViewById(R.id.image_plus);
        textCounter = (TextView) findViewById(R.id.text_counter);

        productName = (TextView) findViewById(R.id.product_name_details);
        productWarranty = (TextView) findViewById(R.id.product_warranty_details);
        productPrice = (TextView) findViewById(R.id.product_price_details);
        productCategory = (TextView) findViewById(R.id.product_category_details);
        productDescription = (TextView) findViewById(R.id.product_description_details);
        productInclude = (TextView) findViewById(R.id.product_include_details);
        productManufacturer = (TextView) findViewById(R.id.product_manufacturer_details);
        productMemory = (TextView) findViewById(R.id.product_memory_details);
        productOS = (TextView) findViewById(R.id.product_os_details);
        productRAM = (TextView) findViewById(R.id.product_ram_details);
        productScreen = (TextView) findViewById(R.id.product_screen_details);
        productColor = (Spinner) findViewById(R.id.product_color_picker);
        productQuantity = (TextView) findViewById(R.id.product_quantity_details);

        addToCartButton = (Button) findViewById(R.id.product_add_to_cart_btn);

        loadingBar = new ProgressDialog(this);
        //------------------------------------------------------------------------------------------

        //------------------------------Counter Button------------------------------
        textCounter.setText(String.valueOf(counter));
        minusButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if(counter > 0) {
                    counter--;
                    textCounter.setText(String.valueOf(counter));
                }
            }
        });
        plusButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                counter++;
                textCounter.setText(String.valueOf(counter));
            }
        });
        //------------------------------------------------------------------------------------------

        //------------------------------Add to Cart Button------------------------------
        addToCartButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (counter == 0) {
                    Toast.makeText(ProductDetailsActivity.this, "Please buy something!", Toast.LENGTH_SHORT).show();
                    return;
                }
                //-------------------Loading Bar-------------------
                loadingBar.setTitle("Please wait");
                loadingBar.setMessage("Adding to cart...");
                loadingBar.setCanceledOnTouchOutside(false);
                loadingBar.show();
                //------------------------------------------------------------------------------------------

                //-------------------Update Product's Quantity-------------------
                double check = Double.parseDouble(productQuantity.getText().toString()) - (double) counter;
                if (check < 0) {
                    loadingBar.dismiss();
                    Toast.makeText(ProductDetailsActivity.this, "Over order :v", Toast.LENGTH_SHORT).show();
                } else {
                    DatabaseReference productReference = FirebaseDatabase.getInstance().getReference().child("Product").child(productID);
                    productReference.addListenerForSingleValueEvent(new ValueEventListener() {
                        @Override
                        public void onDataChange(@NonNull DataSnapshot snapshot) {
                            if (snapshot.exists()) {
                                //-------------------Create Cart and Add the current Product to it-------------------
                                Product product = new Product(productID,
                                        productName.getText().toString(),
                                        productCategory.getText().toString(),
                                        productManufacturer.getText().toString(),
                                        productInclude.getText().toString(),
                                        productOS.getText().toString(),
                                        Double.parseDouble(productScreen.getText().toString()),
                                        productDescription.getText().toString(),
                                        Double.parseDouble(productWarranty.getText().toString()));
                                Details details = new Details(Double.parseDouble(productRAM.getText().toString()),
                                        Double.parseDouble(productMemory.getText().toString()),
                                        product.convertToDouble(productPrice.getText().toString()),
                                        productColor.getSelectedItem().toString(),
                                        Double.parseDouble(textCounter.getText().toString()));
                                product.getDetails().add(details);
                                Prevalent.getCurrentCustomer().addProductToCart(product);
                                //------------------------------------------------------------------------------------------

                                //-------------------Local: update Product-------------------
                                Product product1 = snapshot.getValue(Product.class);
                                product1.setQuantity(productColor.getSelectedItem().toString(), check);
                                //------------------------------------------------------------------------------

                                //-------------------On Server: Update Product's Quantity-------------------
                                DatabaseReference productReference1 = FirebaseDatabase.getInstance().getReference().child("Product").child(productID);
                                productReference1.updateChildren(product1.toMap());
                                //------------------------------------------------------------------------------

                                //-------------------On Server: Update Customer's Cart-------------------
                                DatabaseReference customerReference = FirebaseDatabase.getInstance().getReference().child("Customer");
                                Query query = customerReference.orderByChild("phoneNumber").equalTo(Prevalent.getCurrentCustomer().getPhoneNumber());
                                query.addListenerForSingleValueEvent(new ValueEventListener() {
                                    @Override
                                    public void onDataChange(@NonNull DataSnapshot snapshot) {
                                        if (snapshot.child(Prevalent.getCurrentCustomer().getPhoneNumber()).exists()) {
                                            FirebaseDatabase.getInstance().getReference().child("Customer").child(Prevalent.getCurrentCustomer().getPhoneNumber())
                                                    .updateChildren(Prevalent.getCurrentCustomer().toMap())
                                                    .addOnCompleteListener(new OnCompleteListener<Void>() {
                                                        @Override
                                                        public void onComplete(@NonNull Task<Void> task) {
                                                            if (task.isSuccessful()) {
                                                                Toast.makeText(ProductDetailsActivity.this, "Added to Cart", Toast.LENGTH_SHORT).show();
                                                                loadingBar.dismiss();
                                                                Intent intent = new Intent(ProductDetailsActivity.this, HomeActivity.class);
                                                                startActivity(intent);
                                                            }
                                                            else {
                                                                loadingBar.dismiss();
                                                                Toast.makeText(ProductDetailsActivity.this, "Network error! Please try again later.", Toast.LENGTH_SHORT).show();
                                                            }
                                                        }
                                                    });
                                        }
                                    }

                                    @Override
                                    public void onCancelled(@NonNull DatabaseError error) {
                                        System.out.println(error.toString());
                                    }
                                });
                                //-----------------------------------------------------------------------------------------
                            }
                        }

                        @Override
                        public void onCancelled(@NonNull DatabaseError error) {

                        }
                    });
                }
                //------------------------------------------------------------------------------------------
            }
        });
        //------------------------------------------------------------------------------------------

        //------------------------------Display Product's Detail------------------------------
        DatabaseReference productReference = FirebaseDatabase.getInstance().getReference().child("Product").child(productID);
        productReference.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                if (snapshot.exists()) {
                    Product product = snapshot.getValue(Product.class);
                    productName.setText(product.getName());
                    productWarranty.setText((int)product.getWarranty() + "");
                    productCategory.setText(product.getCategory());
                    productDescription.setText(product.getDescription());
                    productInclude.setText(product.getInclude());
                    productManufacturer.setText(product.getManufacturer());
                    productOS.setText(product.getOs());
                    productScreen.setText(product.getScreen() + "");

                    //------------------------------Color Picker------------------------------
                    final List<String> colorsPicker = new ArrayList<String>();
                    for (int i = 0; i < product.getDetails().size(); i++) {
                        colorsPicker.add(product.getDetails().get(i).getColor());
                    }
                    ArrayAdapter<String> spinnerArrayAdapter = new ArrayAdapter<String>(ProductDetailsActivity.this, android.R.layout.simple_spinner_dropdown_item, colorsPicker); //selected item will look like a spinner set from XML
                    spinnerArrayAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
                    productColor.setAdapter(spinnerArrayAdapter);
                    productColor.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
                        @Override
                        public void onItemSelected(AdapterView<?> adapterView, View view, int i, long l) {
                            productPrice.setText(product.getPriceInMoneyFormat(i));
                            productMemory.setText((int)product.getDetails().get(i).getMemory() + "");
                            productRAM.setText((int)product.getDetails().get(i).getRam() + "");
                            productQuantity.setText((int)(product.getDetails().get(i).getQuantity()) + "");
                        }

                        @Override
                        public void onNothingSelected(AdapterView<?> adapterView) {

                        }
                    });
                    //------------------------------------------------------------------------------------------
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });
        //------------------------------------------------------------------------------------------
    }
}
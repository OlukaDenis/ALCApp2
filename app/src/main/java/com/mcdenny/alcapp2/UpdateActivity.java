package com.mcdenny.alcapp2;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.res.Resources;
import android.net.Uri;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Toast;

import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;
import com.mcdenny.alcapp2.model.TravelDeal;
import com.squareup.picasso.Picasso;

public class UpdateActivity extends AppCompatActivity {
    private FirebaseDatabase database;
    private DatabaseReference travelReference;
    private EditText etTitle, etDesc, etPrice;
    private Button btnAdd,  btnImage;
    private ImageView imageView;
    TravelDeal travelDeal;
    private static String TAG = UpdateActivity.class.getSimpleName();
    TravelList travelList;
    AlertDialog.Builder dialog;
    Uri saveUri;
    private String imageURL;
    private static final int PICTURE_RESULT = 55;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_update);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        FirebaseUtil.openFirebaseReference("traveldeals", travelList);

        database = FirebaseUtil.mFirebaseDatabase;
        travelReference = FirebaseUtil.mDatabaseReference;

        etTitle = findViewById(R.id.update_travel_title);
        etDesc = findViewById(R.id.update_travel_description);
        etPrice = findViewById(R.id.update_travel_price);
        btnAdd = findViewById(R.id.btn_update);
        imageView = findViewById(R.id.update_travel_image);
        btnImage = findViewById(R.id.update_btn_image);

        //Updating the travel deal
        Intent intent = getIntent();
        TravelDeal deals = (TravelDeal) intent.getSerializableExtra("TravelDeal");


        if (deals == null) {
            deals = new TravelDeal();
        }
        this.travelDeal = deals;

        etTitle.setText(travelDeal.getTitle());
        etPrice.setText(travelDeal.getPrice());
        etDesc.setText(travelDeal.getDescription());
        showImage(travelDeal.getImage());

        getSupportActionBar().setTitle("Update " + travelDeal.getTitle());

        btnAdd.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                updateTravelDeal();
            }
        });

        btnImage.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(Intent.ACTION_GET_CONTENT);
                intent.setType("image/*");
                intent.putExtra(Intent.EXTRA_LOCAL_ONLY, true);
                startActivityForResult(intent.createChooser(intent,
                        "Insert Picture"), PICTURE_RESULT);
            }
        });

        chekAdmin();
    }


    private void chekAdmin() {
        if (FirebaseUtil.isAdmin) {
            btnAdd.setVisibility(View.VISIBLE);
            btnImage.setVisibility(View.VISIBLE);
            enableEditText(true);
        } else {
            btnAdd.setVisibility(View.INVISIBLE);
            btnImage.setVisibility(View.INVISIBLE);
            enableEditText(false);
        }
    }


    private void enableEditText(boolean isEnabled) {
        etPrice.setEnabled(isEnabled);
        etDesc.setEnabled(isEnabled);
        etTitle.setEnabled(isEnabled);
    }


    private void updateTravelDeal() {
        travelDeal.setTitle(etTitle.getText().toString());
        travelDeal.setDescription(etDesc.getText().toString());
        travelDeal.setPrice(etPrice.getText().toString());
        travelDeal.setImage(imageURL);
        if (travelDeal.getId() == null) {
            travelReference.push().setValue(travelDeal);
        } else {
            travelReference.child(travelDeal.getId()).setValue(travelDeal);
        }

        Toast.makeText(getApplicationContext(), "Travel Deal updated", Toast.LENGTH_SHORT).show();
        finish();
    }


    private void deleteDeal() {
        travelReference.child(travelDeal.getId()).removeValue();
        if (travelDeal.getImageName() != null && !travelDeal.getImageName().isEmpty()){
            StorageReference picRef = FirebaseUtil.mStorage.getReference().child(travelDeal.getImageName());
            picRef.delete().addOnSuccessListener(new OnSuccessListener<Void>() {
                @Override
                public void onSuccess(Void aVoid) {
                    Log.i(TAG, "Picture deleted ");
                }
            }) .addOnFailureListener(new OnFailureListener() {
                @Override
                public void onFailure(@NonNull Exception e) {
                    Log.i(TAG, "Failed to delete picture! Error:"+ e.getMessage());
                }
            });
        }
        finish();
    }

    private void showImage(String url) {
        if (url != null && !url.isEmpty()) {
            int width = Resources.getSystem().getDisplayMetrics().widthPixels;
            Picasso.get()
                    .load(url)
                    .error(R.drawable.ic_action_name)
                    .placeholder(R.drawable.ic_action_name)
                    .resize(width, width * 2 / 3)
                    .centerCrop()
                    .into(imageView);
        }
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == PICTURE_RESULT && resultCode == RESULT_OK
                && data != null && data.getData() != null) {
            saveUri = data.getData();
            final StorageReference ref = FirebaseUtil.mStorageReference.child(saveUri.getLastPathSegment());

            ref.putFile(saveUri).addOnSuccessListener(new OnSuccessListener<UploadTask.TaskSnapshot>() {
                @Override
                public void onSuccess(UploadTask.TaskSnapshot taskSnapshot) {
                    ref.getDownloadUrl().addOnSuccessListener(new OnSuccessListener<Uri>() {
                        @Override
                        public void onSuccess(Uri uri) {
                            imageURL = uri.toString();
                            travelDeal.setImage(uri.toString());
                            showImage(imageURL);

                        }
                    });

                }
            })
                    .addOnFailureListener(new OnFailureListener() {
                        @Override
                        public void onFailure(@NonNull Exception e) {
                            Toast.makeText(getApplicationContext(), "" + e.getMessage(), Toast.LENGTH_SHORT).show();
                        }
                    });
        }
    }


    private void confirmDelete() {

        dialog = new AlertDialog.Builder(this);
        dialog.setMessage("Are sure you want to delete this travel deal?");
        dialog.setTitle("Warning");

        dialog.setPositiveButton("YES", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialogInterface, int i) {
                deleteDeal();
            }
        });

        dialog.setNegativeButton("No", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialogInterface, int i) {
                dialogInterface.dismiss();
            }
        });
        dialog.show();

    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.update_menu, menu);

        MenuItem addMenu = menu.findItem(R.id.action_delete);
        if (FirebaseUtil.isAdmin) {
            addMenu.setVisible(true);
        } else {
            addMenu.setVisible(false);
        }
        return true;
    }


    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int id = item.getItemId();
        if (id == android.R.id.home) {
            finish();
            return true;
        } else if (id == R.id.action_delete) {
           confirmDelete();
        }
        return super.onOptionsItemSelected(item);

    }
}

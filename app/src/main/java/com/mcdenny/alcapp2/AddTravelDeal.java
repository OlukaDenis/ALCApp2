package com.mcdenny.alcapp2;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import android.app.ProgressDialog;
import android.content.Intent;
import android.content.res.Resources;
import android.net.Uri;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Toast;

import com.google.android.gms.tasks.Continuation;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.storage.OnProgressListener;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;
import com.mcdenny.alcapp2.model.TravelDeal;
import com.squareup.picasso.Picasso;

public class AddTravelDeal extends AppCompatActivity {
    private FirebaseDatabase database;
    private DatabaseReference travelReference;
    private EditText etTitle, etDesc, etPrice;
    private Button btnAdd, btnImage;
    private ImageView imageView;
    private static final int PICTURE_RESULT = 55;
    TravelDeal travelDeal;
    TravelList travelList;
    Uri saveUri;
    private String imageURL;
    private String imageName;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add_travel_deal);
        getSupportActionBar().setTitle("Add Travel Deal");

        FirebaseUtil.openFirebaseReference("traveldeals", travelList);

        database = FirebaseUtil.mFirebaseDatabase;
        travelReference = FirebaseUtil.mDatabaseReference;

        etTitle = findViewById(R.id.travel_title);
        etDesc = findViewById(R.id.travel_description);
        etPrice = findViewById(R.id.travel_price);
        btnAdd = findViewById(R.id.btn_save);
        btnImage = findViewById(R.id.btn_image);
        imageView = findViewById(R.id.travel_image);

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

        btnAdd.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                saveDeal();
            }
        });

    }


    private void clean() {
        etTitle.setText("");
        etDesc.setText("");
        etPrice.setText("");
        etTitle.requestFocus();
    }

    private void saveDeal() {
            String mTitle = etTitle.getText().toString();
            String mDesc = etDesc.getText().toString();
            String mPrice = etPrice.getText().toString();
            if (mTitle.isEmpty()) {
                etTitle.setError("Required");
                etTitle.requestFocus();


            } else if (mDesc.isEmpty()) {
                etDesc.setError("Required");
                etDesc.requestFocus();
            } else if (mPrice.isEmpty()) {
                etPrice.setError("Required");
                etPrice.requestFocus();
            } else {
                travelDeal = new TravelDeal();
                travelDeal.setImage(imageURL);
                travelDeal.setDescription(mDesc);
                travelDeal.setPrice(mPrice);
                travelDeal.setTitle(mTitle);
                travelDeal.setImageName(imageName);

                travelReference.push().setValue(travelDeal);
                Toast.makeText(getApplicationContext(), "Travel deal added!", Toast.LENGTH_SHORT).show();
                clean();
                finish();
            }


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

        final ProgressDialog progressDialog = new ProgressDialog(this);
        progressDialog.setMessage("Uploading....");
        progressDialog.show();

        if (requestCode == PICTURE_RESULT && resultCode == RESULT_OK
                && data != null && data.getData() != null) {
            saveUri = data.getData();
            final StorageReference ref = FirebaseUtil.mStorageReference.child(saveUri.getLastPathSegment());

            ref.putFile(saveUri).addOnSuccessListener(new OnSuccessListener<UploadTask.TaskSnapshot>() {
                @Override
                public void onSuccess(final UploadTask.TaskSnapshot taskSnapshot) {
                    ref.getDownloadUrl().addOnSuccessListener(new OnSuccessListener<Uri>() {
                        @Override
                        public void onSuccess(Uri uri) {
                            progressDialog.dismiss();
                            imageURL = uri.toString();
                          //  travelDeal.setImage(uri.toString());
                            imageName = taskSnapshot.getStorage().getPath();
                            showImage(imageURL);

                        }
                    });

                }
            })
                    .addOnFailureListener(new OnFailureListener() {
                        @Override
                        public void onFailure(@NonNull Exception e) {
                            progressDialog.dismiss();
                            Toast.makeText(getApplicationContext(), "" + e.getMessage(), Toast.LENGTH_SHORT).show();
                        }
                    })

                    .addOnProgressListener(new OnProgressListener<UploadTask.TaskSnapshot>() {
                @Override
                public void onProgress(UploadTask.TaskSnapshot taskSnapshot) {
                    double progress = (100 * taskSnapshot.getBytesTransferred() / taskSnapshot.getTotalByteCount());
                    progressDialog.setMessage("Uploading " + progress + "%");

                }
            });
        }
    }
}

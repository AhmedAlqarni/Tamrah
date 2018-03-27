package com.example.ahmed.tamrah;

import android.content.Intent;
import android.graphics.Bitmap;
import android.net.Uri;
import android.provider.MediaStore;
import android.support.design.widget.TextInputEditText;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Spinner;
import android.widget.Toast;

import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;

import java.io.BufferedReader;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.HashMap;
import java.util.Map;

import static com.example.ahmed.tamrah.AccountActivity.getCorrectlyOrientedImage;
import static com.example.ahmed.tamrah.AccountActivity.getOrientation;

public class AddOfferActivity extends AppCompatActivity {

    private Offer offer;
    static final int REQUEST_IMAGE_CAPTURE = 1;
    private static final int SELECTED_PICTURE = 1;
    private StorageReference mStorage;
    private Uri ImagedownloadUrl ; //offer image download link


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add_offer);
        mStorage = FirebaseStorage.getInstance().getReference();

        final TextInputEditText title = (TextInputEditText) findViewById(R.id.OfferTitleInput);
        final EditText descView = (EditText) findViewById(R.id.OfferDiscInput);
        final EditText priceView = (EditText) findViewById(R.id.OfferPriceInput);
        final EditText typeView = (EditText) findViewById(R.id.OfferTypeInput);
        final Spinner citySpinner = (Spinner) findViewById(R.id.Region);
        Button submitBtn = (Button) findViewById(R.id.PublishOfferBtn);


        BufferedReader reader = new BufferedReader(new InputStreamReader(this.getResources().openRawResource(R.raw.cities_en)));
        String[] citiesArray = new String[118];

        try {
            for(int i = 0; i < 118; i++) {
                citiesArray[i] = reader.readLine();
            }
            reader.close();
        } catch (IOException e) {
            e.printStackTrace();
        }

        ArrayAdapter<String> adp2 = new ArrayAdapter<String>(this, android.R.layout.simple_spinner_item, citiesArray);
        adp2.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        citySpinner.setAdapter(adp2);

        submitBtn.setOnClickListener(new View.OnClickListener()

        {
            @Override
            public void onClick(View view) {
                offer = new Offer(title.getText().toString(), typeView.getText().toString(),
                        citySpinner.getSelectedItem().toString(), (priceView.getText().toString()),
                        "-1", descView.getText().toString(),String.valueOf(ImagedownloadUrl));
                publish();
            }
        });


    }

    //For reading a picture from the device
    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        //For reading a picture from the deviceif(requestCode ==   SELECTED_PICTURE && data != null) {
        Uri uri = data.getData();
        // Show the Selected Image onImageView ImageView cV = (ImageView) findViewById(R.id.imageViewAdding);
        ImageView iV = (ImageView) findViewById(R.id.imageViewAdding);
        getOrientation(this, uri);
        try {
            //Offer Image
            Bitmap loadedBitmap = getCorrectlyOrientedImage(this, uri,1000);
            iV.setImageBitmap(loadedBitmap);
            //cV.setImageURI(uri);
            ByteArrayOutputStream baos = new ByteArrayOutputStream();
            loadedBitmap.compress(Bitmap.CompressFormat.JPEG, 50, baos);
            byte[] data2 = baos.toByteArray();

            //firebase add to storage
            final StorageReference filepath = mStorage.child("OfferPhotos").child(uri.getLastPathSegment());
            filepath.putBytes(data2).addOnSuccessListener(new OnSuccessListener<UploadTask.TaskSnapshot>() {

                @Override
                public void onSuccess(UploadTask.TaskSnapshot taskSnapshot) {
                    Toast.makeText(AddOfferActivity.this, "Image uplaod is Done", Toast.LENGTH_LONG);
                    ImagedownloadUrl = taskSnapshot.getDownloadUrl();
                }
            });


        } catch (IOException e) {
            e.printStackTrace();
        }

        //for the Camera App>>>
        if (requestCode == REQUEST_IMAGE_CAPTURE && resultCode == RESULT_OK) {
            Bundle extras = data.getExtras();
            Bitmap imageBitmap = (Bitmap) extras.get("data");
            //mImageView.setImageBitmap(imageBitmap); Image result
        }

    }


    //Button Handler
    //for selecting image in the Add OfferFrag page
    public void selectPictureBtn(View view) {
        Intent i = new Intent(Intent.ACTION_PICK, MediaStore.Images.Media.EXTERNAL_CONTENT_URI);
        startActivityForResult(i, SELECTED_PICTURE);

    }

    public void publish() {
        DatabaseReference FBofferNode = FirebaseDatabase.getInstance().getReference().child("Offer");
        Map offerPost = new HashMap();
        offerPost.put("Seller", Auth.fbAuth.getUid()); //STUB
        offerPost.put("Title", offer.getTitle());
        offerPost.put("Description", offer.getDesc());
        offerPost.put("Price", offer.getPrice());
        offerPost.put("Type", offer.getType());
        offerPost.put("Rate", offer.getRate());
        offerPost.put("OID", "");
        offerPost.put("OfferImage", offer.getOfferImage());

        //FBofferNode.setValue(offerPost);
        FBofferNode = FBofferNode.child(FBofferNode.push().getKey());
        FBofferNode.setValue(offerPost);
        String OID = FBofferNode.getKey();

        Log.i("X", "XXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXX " + OID);
        FBofferNode = FirebaseDatabase.getInstance().getReference().child("Offer").child(OID).child("OID");
        FBofferNode.setValue(OID);


//        FirebaseDatabase.getInstance().getReference().child("User")
//                .child(Auth.fbAuth.getCurrentUser().getUid()).child("Offer").push()
//                .setValue(new HashMap().put("OID",OID));

        String UID = Auth.fbAuth.getCurrentUser().getUid();
        DatabaseReference DBRef = FirebaseDatabase.getInstance().getReference().child("User").child(UID).child("Offer");
        DBRef.child(OID).setValue(OID);

    }
}

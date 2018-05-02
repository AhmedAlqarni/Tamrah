package com.example.ahmed.tamrah;
//Ahmed Alqarni
import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;

import org.apache.http.Header;
import org.apache.http.HttpEntity;
import org.apache.http.message.BasicHeader;
import android.app.AlertDialog;
import android.app.Fragment;
import android.app.ProgressDialog;
import android.content.ClipData;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Typeface;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.Drawable;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Parcel;
import android.os.Parcelable;
import android.os.StrictMode;
import android.provider.MediaStore;
import android.support.annotation.NonNull;
import android.support.design.widget.NavigationView;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.res.ResourcesCompat;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.SearchView;
import android.support.v7.widget.Toolbar;
import android.telephony.TelephonyManager;
import android.text.format.DateFormat;
import android.util.Base64;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.app.FragmentManager;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.HeaderViewListAdapter;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;
import android.widget.ViewSwitcher;

import com.android.volley.AuthFailureError;
import com.android.volley.Request;
import com.android.volley.VolleyLog;
import com.firebase.ui.database.FirebaseListAdapter;
import com.google.android.gms.common.api.Response;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

import org.apache.http.HttpResponse;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.util.EntityUtils;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.io.OutputStreamWriter;
import java.io.Serializable;
import java.io.UnsupportedEncodingException;
import java.lang.reflect.Method;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.ProtocolException;
import java.net.URL;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.List;

import javax.net.ssl.HttpsURLConnection;

import de.hdodenhof.circleimageview.CircleImageView;

import static com.example.ahmed.tamrah.AccountActivity.getCorrectlyOrientedImage;
import static com.example.ahmed.tamrah.AccountActivity.getOrientation;


public class MainActivity extends AppCompatActivity {
    private DrawerLayout mDrawerLayout;
    private ActionBarDrawerToggle mToggle;
    private Toolbar toolBar;
    static User user;
    private TextView toolBarTitle;
    static CircleImageView userImg;
    private NavigationView nvDrawer;
    static final int REQUEST_IMAGE_CAPTURE = 1;
    private Intent intent;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        //Created by Khalid
        user = (User) getIntent().getSerializableExtra("User");
        setResult(-1, null);
        user = new User();
        retrieveUserFromCache();
        //ToolBar
        toolBar = (Toolbar) findViewById(R.id.toolBar);
        setSupportActionBar(toolBar);
        //Typeface typeface = Typeface.createFromAsset(getAssets(),"fonts/alegreya_sans_italic.tff");
        //toolBarTitle = (TextView) findViewById(R.id.toolbar_title);
        //toolBarTitle.setTypeface(typeface);

        //Left menu Drawer
        mDrawerLayout = (DrawerLayout) findViewById(R.id.drawer);
        mToggle = new ActionBarDrawerToggle(this, mDrawerLayout, R.string.open, R.string.close);
        mDrawerLayout.addDrawerListener(mToggle);
        nvDrawer = (NavigationView) findViewById(R.id.nV);
        mToggle.syncState();
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        setupDrawerContent(nvDrawer);

        //set the Default page to HomeFrag fragment
        FragmentManager fm = getFragmentManager();
        fm.beginTransaction().replace(R.id.flContents, new HomeFrag()).commit();
        updateMenuBar();


    }


    public void updateMenuBar() {
        Menu nav_menu = nvDrawer.getMenu();
        View header = nvDrawer.getHeaderView(0);
        TextView username = (TextView) header.findViewById(R.id.user_name);
        TextView email = (TextView) header.findViewById(R.id.user_email);
        userImg = (CircleImageView) header.findViewById(R.id.nav_profile_image);
        userImg.setVisibility(View.VISIBLE);
        if (user.isLoggedIn()) {
            nav_menu.findItem(R.id.ShoppingCart).setVisible(true);
            nav_menu.findItem(R.id.Profile).setVisible(true);
            nav_menu.findItem(R.id.Orders).setVisible(true);
            nav_menu.findItem(R.id.Account_Settings).setVisible(true);
            nav_menu.findItem(R.id.Messages).setVisible(true);
            nav_menu.findItem(R.id.logout).setVisible(true);
            nav_menu.findItem(R.id.Login).setVisible(false);
            nav_menu.findItem(R.id.Signup).setVisible(false);
            username.setText(user.getName());
            email.setText(user.getEmail());
            if (!user.getProfilePic().equals(""))
                userImg.setImageDrawable(new ImageFetcher().fetch(user.getProfilePic()));
            else
                userImg.setVisibility(View.INVISIBLE);
        } else {
            nav_menu.findItem(R.id.ShoppingCart).setVisible(false);
            nav_menu.findItem(R.id.Profile).setVisible(false);
            nav_menu.findItem(R.id.Orders).setVisible(false);
            nav_menu.findItem(R.id.Account_Settings).setVisible(false);
            nav_menu.findItem(R.id.Messages).setVisible(false);
            nav_menu.findItem(R.id.logout).setVisible(false);
            nav_menu.findItem(R.id.Login).setVisible(true);
            nav_menu.findItem(R.id.Signup).setVisible(true);
            userImg.setVisibility(View.INVISIBLE);
            username.setText("");
            email.setText("");
        }
    }

    private void retrieveUserFromCache() {
        return; //STUB. To be implemented soon, insha'a allah
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        Fragment myFragment = null;
        Class fragmentClass = null;
        //Drawer only
        if (mToggle.onOptionsItemSelected(item)) {
            return true;
        }
        //home buttons
        switch (item.getItemId()) {
            case R.id.cartIconeHome:
                fragmentClass = ShoppingCartFrag.class;
                break;
            case R.id.cameraIconHome:
                //show message
                dispatchTakePictureIntent();
                return true;
        }
        try {
            myFragment = (Fragment) fragmentClass.newInstance();
        } catch (Exception e) {
            e.printStackTrace();
        }
        FragmentManager fragmentManager = getFragmentManager();
        fragmentManager.beginTransaction().replace(R.id.flContents, myFragment).commit();

        return super.onOptionsItemSelected(item);
    }


    //for the left menu choices and transitions
    //and for the buttons action handling
    public void selectItemDrawer(MenuItem menuItem) {
        mDrawerLayout.closeDrawers();
        Fragment myFragment = null;
        Class fragmentClass;
        switch (menuItem.getItemId()) {
            case R.id.Home:
                fragmentClass = HomeFrag.class;
                break;
            case R.id.ShoppingCart:
                intent = new Intent(this, CartActivity.class);
                intent.putExtra("User", this.user);
                startActivity(intent);
                return;
            case R.id.Account_Settings:
                Log.i("......",user.getName());
                intent = new Intent(this, AccountSettingsActivity.class);
                intent.putExtra("User", this.user);
                startActivityForResult(intent, 0);
                return;
            case R.id.Orders:
                fragmentClass = OrdersFrag.class;
                break;
            case R.id.Login:
                intent = new Intent(this, LoginActivity.class);
                intent.putExtra("User", this.user);
                startActivityForResult(intent, 0);
                return;
            case R.id.message:
                intent = new Intent(this, Message_Activity.class);
                intent.putExtra("User", this.user);
                startActivityForResult(intent, 0);
                return;
            case R.id.Signup:
                intent = new Intent(this, SignupActivity.class);
                intent.putExtra("User", this.user);
                startActivityForResult(intent, 0);
                return;
           /* case R.id.acitvity_message:
               // Log.i("k","signUp");
                if(client != null){
                   //displayChatMessages();
                startActivity(new Intent(this,Message_Activity.class));}
                return;*/
            case R.id.logout:
                mDrawerLayout.closeDrawers();
                logout();
                return;
            case R.id.ContactUs:
                fragmentClass = ContactUsFrag.class;
                break;
            //This is profile page
            case R.id.Profile:
                intent = new Intent(this, AccountActivity.class);
                intent.putExtra("UID", "myProfile");
                intent.putExtra("User", user);
                startActivityForResult(intent, 0);
                return;
            case R.id.Messages:
                startActivity(new Intent(this, MessagesListActivity.class));
                return;

            default:
                fragmentClass = HomeFrag.class;
        }
        try {
            myFragment = (Fragment) fragmentClass.newInstance();
        } catch (Exception e) {
            e.printStackTrace();
        }
        FragmentManager fragmentManager = getFragmentManager();
        fragmentManager.beginTransaction().replace(R.id.flContents, myFragment).addToBackStack("tag").commit();
        menuItem.setChecked(true);
        setTitle(menuItem.getTitle());
        mDrawerLayout.closeDrawers();
    }

    private void logout() {
        final Context context = this;
        AlertDialog alertDialog = new AlertDialog.Builder(this).create();
        alertDialog.setTitle("Logout");
        alertDialog.setMessage("You are signed in as: " + user.getEmail());

        alertDialog.setButton(AlertDialog.BUTTON_POSITIVE, "Logout",
                new DialogInterface.OnClickListener() {
                    //firebase logout
                    public void onClick(DialogInterface dialog, int which) {
                        Auth.fbAuth.signOut();
                        user = new User();
                        updateMenuBar();
                        dialog.dismiss();
                        Toast.makeText(context, "Logged Out", Toast.LENGTH_LONG).show();
                    }
                });
        alertDialog.setButton(AlertDialog.BUTTON_NEGATIVE, "Cancel",
                new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int which) {
                        //FirebaseAuth.getInstance().signOut();
                        dialog.dismiss();
                    }
                });
        alertDialog.show();

    }


    private void setupDrawerContent(NavigationView navigationView) {
        navigationView.setNavigationItemSelectedListener(new NavigationView.OnNavigationItemSelectedListener() {
            @Override
            public boolean onNavigationItemSelected(@NonNull MenuItem item) {
                selectItemDrawer(item);
                return false;
            }
        });

    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.toolbar_menu, menu);
        menu.findItem(R.id.cartIconeHome).setVisible(false);
        return super.onCreateOptionsMenu(menu);
    }

    //Button Handler
    //This is to make the app title clickable
    public void goToHome(View view) {
        buttonHandeler(HomeFrag.class);
    }

    //Button Handler
    //this is for the button in profile page "Edit Account"

    //Button Handler
    //this is for the button in shopping cart page "shopping cart"
    public void goToCheckoutpage(View view) {
        buttonHandeler(CheckoutFrag.class);
    }


    //Button Handler
    //this is for any clicked offer in any page
    public void goToOffer(View view) {
        buttonHandeler(OfferFrag.class);
    }

    //Button Handler
    //this is for user photo clicked in left drawer
    public void goToUserProfile(View view) {
        intent = new Intent(this, AccountActivity.class);
        intent.putExtra("UID", "myProfile");
        intent.putExtra("User", user);
        startActivityForResult(intent, 0);
        mDrawerLayout.closeDrawers();

    }

    //Button Handler
    //this is for the plus button in searchin for offer page



    //For reading a picture from the device
    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (requestCode == 0) {
            if (resultCode == -1) return;
            else
                user = (User) data.getSerializableExtra("User");
            updateMenuBar();
        } else if (requestCode == REQUEST_IMAGE_CAPTURE) {
            //For reading a picture from the device
//            /if(requestCode ==   SELECTED_PICTURE && data != null) {
//            Uri uri = data.getData();
//            // Show the Selected Image onImageView ImageView iV = (ImageView) findViewById(R.id.imageViewAdding);
//            ImageView iV = (ImageView) findViewById(R.id.imageViewAdding);
//            getOrientation(this, uri);
//            try {
//                //profile_image
//                Bitmap loadedBitmap = getCorrectlyOrientedImage(this, uri, 1000);
//                iV.setImageBitmap(loadedBitmap);
//                //iV.setImageURI(uri);
//            } catch (IOException e) {
//                e.printStackTrace();
//            }
            //for the Camera App>>>
            if (resultCode == RESULT_OK) {
                Bitmap imageBitmap = (Bitmap) data.getExtras().get("data");
                final String AI_type  = new ImageUploader().sendRequest(imageBitmap, this);
                final Context c = this;
                AlertDialog alertDialog = new AlertDialog.Builder(this).create();
                alertDialog.setTitle("Type");
                alertDialog.setMessage("The Type is: " + AI_type);

                alertDialog.setButton(AlertDialog.BUTTON_POSITIVE, "OK",
                        new DialogInterface.OnClickListener() {
                            //firebase logout
                            public void onClick(DialogInterface dialog, int which) {
                                dialog.dismiss();

                            }
                        });
                alertDialog.setButton(AlertDialog.BUTTON_POSITIVE, "View Information",
                        new DialogInterface.OnClickListener() {
                            //firebase logout
                            public void onClick(DialogInterface dialog, int which) {
                                //GO TO INFO PAGE
                                dialog.dismiss();

                            }
                        });
                alertDialog.setButton(AlertDialog.BUTTON_NEGATIVE, "Search Offers",
                        new DialogInterface.OnClickListener() {
                            public void onClick(DialogInterface dialog, int which) {
                                dialog.dismiss();
                                Intent intent = new Intent(c, SearchResultActivity.class);
                                intent.putExtra("text", AI_type);
                                startActivity(intent);
                            }
                        });
                alertDialog.show();
            }
        }
    }

    //Button Handler main function for all buttons
    //You can use it in any button page transtions only
    public void buttonHandeler(Class f) {
        Fragment myFragment = null;
        Class fragmentClass;
        fragmentClass = f;
        try {
            myFragment = (Fragment) fragmentClass.newInstance();
        } catch (Exception e) {
            e.printStackTrace();
        }
        FragmentManager fragmentManager = getFragmentManager();
        fragmentManager.beginTransaction().replace(R.id.flContents, myFragment).addToBackStack("tag").commit();
    }

    //for the camera
    private void dispatchTakePictureIntent() {
        Intent takePictureIntent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
        if (takePictureIntent.resolveActivity(getPackageManager()) != null) {
            startActivityForResult(takePictureIntent, REQUEST_IMAGE_CAPTURE);
        }
    }
}

 class ImageUploader extends AsyncTask<Bitmap, Void, String> {
    Context c;
    @Override
    protected String doInBackground(Bitmap... img) {

        ByteArrayOutputStream baos = new ByteArrayOutputStream();
        img[0].compress(Bitmap.CompressFormat.PNG, 100, baos);
        byte[] imageBytes = baos.toByteArray();
        String encodedImage = Base64.encodeToString(imageBytes, Base64.DEFAULT);

Log.i("i", "XXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXX" + encodedImage);
        try {

            HttpClient client = new DefaultHttpClient();
            //use your server path of php file
            HttpPost post = new HttpPost("http://10.13.19.244:5000/upload");

            MultipartEntity reqEntity = new MultipartEntity();
            reqEntity.addPart("file","whatev",new ByteArrayInputStream(encodedImage.getBytes(StandardCharsets.UTF_8))
,  "multipart/form-data");

            post.setEntity(reqEntity);

            HttpResponse response = client.execute(post);
            HttpEntity resEntity = response.getEntity();

            try {
                final String response_str = EntityUtils.toString(resEntity);
                if (resEntity != null) {
                    Log.i("RESPONSE", response_str);
                    return response_str;
                }
            } catch (Exception ex) {
                Log.e("Debug", "error: " + ex.getMessage(), ex);
            }
        } catch (Exception e) {
            Log.e("Upload Exception", "");
            e.printStackTrace();
        }
         return null;
    }

     public String sendRequest(Bitmap decodedImg, Context c) {this.c = c; return doInBackground(decodedImg);}
 }

class MultipartEntity implements HttpEntity {

    private String boundary = null;

    ByteArrayOutputStream out = new ByteArrayOutputStream();
    boolean isSetLast = false;
    boolean isSetFirst = false;

    public MultipartEntity() {
        this.boundary = System.currentTimeMillis() + "";
    }

    public void writeFirstBoundaryIfNeeds() {
        if (!isSetFirst) {
            try {
                out.write(("--" + boundary + "\r\n").getBytes());
            } catch (final IOException e) {

            }
        }
        isSetFirst = true;
    }

    public void writeLastBoundaryIfNeeds() {
        if (isSetLast) {
            return;
        }
        try {
            out.write(("\r\n--" + boundary + "--\r\n").getBytes());
        } catch (final IOException e) {

        }
        isSetLast = true;
    }

    public void addPart(final String key, final String value) {
        writeFirstBoundaryIfNeeds();
        try {
            out.write(("Content-Disposition: form-data; name=\"" + key + "\"\r\n")
                    .getBytes());
            out.write("Content-Type: text/plain; charset=UTF-8\r\n".getBytes());
            out.write("Content-Transfer-Encoding: 8bit\r\n\r\n".getBytes());
            out.write(value.getBytes());
            out.write(("\r\n--" + boundary + "\r\n").getBytes());
        } catch (final IOException e) {

        }
    }

    public void addPart(final String key, final String fileName,
                        final InputStream fin) {
        addPart(key, fileName, fin, "application/octet-stream");
    }

    public void addPart(final String key, final String fileName,
                        final InputStream fin, String type) {
        writeFirstBoundaryIfNeeds();
        try {
            type = "Content-Type: " + type + "\r\n";
            out.write(("Content-Disposition: form-data; name=\"" + key
                    + "\"; filename=\"" + fileName + "\"\r\n").getBytes());
            out.write(type.getBytes());
            out.write("Content-Transfer-Encoding: binary\r\n\r\n".getBytes());

            final byte[] tmp = new byte[4096];
            int l = 0;
            while ((l = fin.read(tmp)) != -1) {
                out.write(tmp, 0, l);
            }
            out.flush();
        } catch (final IOException e) {

        } finally {
            try {
                fin.close();
            } catch (final IOException e) {

            }
        }
    }

    public void addPart(final String key, final File value) {
        try {
            addPart(key, value.getName(), new FileInputStream(value));
        } catch (final FileNotFoundException e) {

        }
    }

    public long getContentLength() {
        writeLastBoundaryIfNeeds();
        return out.toByteArray().length;
    }

    public Header getContentType() {
        return new BasicHeader("Content-Type", "multipart/form-data; boundary="
                + boundary);
    }

    public boolean isChunked() {
        return false;
    }

    public boolean isRepeatable() {
        return false;
    }

    public boolean isStreaming() {
        return false;
    }

    public void writeTo(final OutputStream outstream) throws IOException {
        outstream.write(out.toByteArray());
    }

    public Header getContentEncoding() {
        return null;
    }

    public void consumeContent() throws IOException,
            UnsupportedOperationException {
        if (isStreaming()) {
            throw new UnsupportedOperationException(
                    "Streaming entity does not implement #consumeContent()");
        }
    }

    public InputStream getContent() throws IOException,
            UnsupportedOperationException {
        return new ByteArrayInputStream(out.toByteArray());
    }

}
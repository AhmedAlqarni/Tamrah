package com.example.ahmed.tamrah;

import android.content.Intent;
import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.ViewSwitcher;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.List;

public class AccountSettingsActivity extends AppCompatActivity {


    private Toolbar toolBar;
    private User user;
    Intent intent = new Intent();



    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_account_settings);

        user = (User) getIntent().getSerializableExtra("User");
        setResult(-1, null);

        TextView nameTextView = (TextView) findViewById(R.id.nameInput23);
        nameTextView.setText(user.getName());

        TextView phoneTextView = (TextView) findViewById(R.id.PhoneInputField);
        phoneTextView.setText(user.getPhoneNum());

        TextView addressTextView = (TextView) findViewById(R.id.AddressInputField);
        addressTextView.setText(user.getAddress());

        TextView bioTextView = (TextView) findViewById(R.id.BioInputField);
        bioTextView.setText(user.getDescription());

        toolBar = (Toolbar) findViewById(R.id.toolBar);
        setSupportActionBar(toolBar);
        addCitySpinerValues();
    }

    //filling the "City" spinner with KSA cities.
    private void addCitySpinerValues() {
        final Spinner citySpinner = (Spinner) findViewById(R.id.CitySpinner);
        BufferedReader reader = new BufferedReader(new InputStreamReader(this.getResources().openRawResource(R.raw.cities_en)));
        final String[] citiesArray = new String[118];
        try {
            for (int i = 0; i < 118; i++) {
                citiesArray[i] = reader.readLine();
            }
            reader.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
        ArrayAdapter<String> adp2 = new ArrayAdapter<String>(this, android.R.layout.simple_spinner_item, citiesArray);
        adp2.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        citySpinner.setAdapter(adp2);
        for (int i = 0; i < citySpinner.getCount(); i++)
            if (user.getRegion().equals(citySpinner.getItemAtPosition(i))){
                citySpinner.setSelection(i);
                break;
        }

        citySpinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parentView, View selectedItemView, int position, long id) {
                user.setRegion(citySpinner.getSelectedItem().toString().trim());
                intent.putExtra("User",  user);
                setResult(0, intent);
            }

            @Override
            public void onNothingSelected(AdapterView<?> parentView) {return;}

        });
    }

    //Name editing.
    public void settingsEditName(View view) {
        Button b = (Button) findViewById(R.id.button32);
        TextView tV = (TextView) findViewById(R.id.nameInput23);
        EditText edit = (EditText) findViewById(R.id.hidden_edit_view);
        if (b.getText().equals("Done")) {
            b.setText("Edit");
            edit.setVisibility(View.INVISIBLE);
            tV.setVisibility(View.VISIBLE);
            tV.setText(edit.getText().toString());
            user.setName(edit.getText().toString().trim());
            intent.putExtra("User",  user);
            setResult(0, intent);

        } else {
            b.setText("Done");
            edit.setText(tV.getText());
            edit.setVisibility(View.VISIBLE);
            tV.setVisibility(View.INVISIBLE);
        }
    }

    //password editing.
    public void settingsEditPassword(View view) {
        Button b = (Button) findViewById(R.id.editPasswordBtn);
        EditText edit = (EditText) findViewById(R.id.hidden_edit_password);
        TextView tV = (TextView) findViewById(R.id.PasswordInputField);

        if (b.getText().equals("Done")) {
            b.setText("Edit");
            edit.setVisibility(View.INVISIBLE);
            tV.setVisibility(View.VISIBLE);
            tV.setText(edit.getText().toString());
            setPassword(edit.getText().toString().trim());


        } else {
            b.setText("Done");
            edit.setVisibility(View.VISIBLE);
            tV.setVisibility(View.INVISIBLE);
        }
    }

    //updating the password in the database.
    public void setPassword(String newPassword) {
        FirebaseUser firebaseUser = Auth.fbAuth.getInstance().getCurrentUser();
        firebaseUser.updatePassword(newPassword)
                .addOnCompleteListener(new OnCompleteListener<Void>() {
                    @Override
                    public void onComplete(@NonNull Task<Void> task) {
                        if (task.isSuccessful())
                            Log.i("i", "Updated Successfully");
                        else
                            Log.i("i", "Updated FAILED");
                    }
                });
    }

    //phone editing
    public void settingsEditPhone(View view) {
        Button b = (Button) findViewById(R.id.editPhoneBtn);
        EditText edit = (EditText) findViewById(R.id.hidden_edit_phone);
        TextView tV = (TextView) findViewById(R.id.PhoneInputField);

        if (b.getText().equals("Done")) {
            b.setText("Edit");
            edit.setVisibility(View.INVISIBLE);
            tV.setVisibility(View.VISIBLE);
            tV.setText(edit.getText().toString());
            user.setPhoneNum(edit.getText().toString().trim());
            intent.putExtra("User",  user);
            setResult(0, intent);

        } else {
            b.setText("Done");
            edit.setText(tV.getText());
            edit.setVisibility(View.VISIBLE);
            tV.setVisibility(View.INVISIBLE);
        }
    }

    //address editing
    public void settingsEditAddress(View view) {
        Button b = (Button) findViewById(R.id.editAddressBtn);
        EditText edit = (EditText) findViewById(R.id.hidden_edit_address);
        TextView tV = (TextView) findViewById(R.id.AddressInputField);
        if (b.getText().equals("Done")) {
            b.setText("Edit");
            edit.setVisibility(View.INVISIBLE);
            tV.setVisibility(View.VISIBLE);
            tV.setText(edit.getText().toString());
            user.setAddress(edit.getText().toString().trim());
            intent.putExtra("User",  user);
            setResult(0, intent);

        } else {
            b.setText("Done");
            edit.setText(tV.getText());
            edit.setVisibility(View.VISIBLE);
            tV.setVisibility(View.INVISIBLE);
        }
    }

    //bio editing
    public void settingsEditBio(View view) {
        Button b = (Button) findViewById(R.id.editBioBtn);
        EditText edit = (EditText) findViewById(R.id.hidden_edit_Bio);
        TextView tV = (TextView) findViewById(R.id.BioInputField);
        if (b.getText().equals("Done")) {
            b.setText("Edit");
            edit.setVisibility(View.INVISIBLE);
            tV.setVisibility(View.VISIBLE);
            tV.setText(edit.getText().toString());
            user.setBio(edit.getText().toString().trim());
            intent.putExtra("User",  user);
            setResult(0, intent);

        } else {
            b.setText("Done");
            edit.setText(tV.getText());
            edit.setVisibility(View.VISIBLE);
            tV.setVisibility(View.INVISIBLE);
        }
    }

    public void goToHome(View view) {
        finish();
    }
}

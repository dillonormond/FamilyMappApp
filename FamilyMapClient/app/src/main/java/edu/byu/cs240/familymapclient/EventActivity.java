package edu.byu.cs240.familymapclient;

import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;

import android.content.Intent;
import android.os.Bundle;
import android.view.MenuItem;

import com.joanzapata.iconify.Iconify;
import com.joanzapata.iconify.fonts.FontAwesomeModule;

import edu.byu.cs240.familymapclient.ui.login.LoginFragment;

public class EventActivity extends AppCompatActivity {
    private Bundle extras = null;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_event);
        Iconify.with(new FontAwesomeModule());
        extras = getIntent().getExtras();
        FragmentManager fragmentManager = this.getSupportFragmentManager();
        Fragment fragment = fragmentManager.findFragmentById(R.id.eventFrameLayout);
        if(fragment == null) {
            fragment = createMapFragment();
            fragmentManager.beginTransaction()
                    .add(R.id.eventFrameLayout, fragment)
                    .commit();


        } else {
            // If the fragment is not null, the MainActivity was destroyed and recreated
            // so we need to reset the listener to the new instance of the fragment
            if(fragment instanceof LoginFragment) {
               // ((LoginFragment) fragment).registerListener(this);
            }
        }
    }

    private Fragment createMapFragment(){
        MapFragment fragment = new MapFragment();
        fragment.setHasOptionsFalse();
        if(extras != null){
            float latTest = extras.getFloat("latitude");
            fragment.setLatitude(extras.getFloat("latitude"));
            fragment.setLongitude(extras.getFloat("longitude"));
            String testName = extras.getString("personName");
            fragment.setDescription(extras.getString("eventName"));
            fragment.setPersonName(extras.getString("personName"));
            String genderTest = extras.getString("gender");
            fragment.setEventGender(extras.getString("gender"));
            String id = extras.getString("eventID");
            fragment.setEventID(id);
            fragment.setFromEventFragmentTrue();
        }
        return fragment;
    }
    @Override
    public boolean onOptionsItemSelected(MenuItem item){
        if(item.getItemId() == android.R.id.home){
            Intent intent = new Intent(this, MainActivity.class);
            intent.setFlags(Intent.FLAG_ACTIVITY_SINGLE_TOP | Intent.FLAG_ACTIVITY_CLEAR_TOP);
            startActivity(intent);
        }
        return true;
    }
}
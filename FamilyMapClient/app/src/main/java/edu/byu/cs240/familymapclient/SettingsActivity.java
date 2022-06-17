package edu.byu.cs240.familymapclient;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.provider.ContactsContract;
import android.view.View;
import android.widget.CompoundButton;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.ToggleButton;

public class SettingsActivity extends AppCompatActivity {
    ToggleButton lifeStoryToggle;
    ToggleButton familyTreeToggle;
    ToggleButton spouseToggle;
    ToggleButton fatherToggle;
    ToggleButton motherToggle;
    ToggleButton maleToggle;
    ToggleButton femaleToggle;
    LinearLayout logout;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_settings);

        DataCache cache = DataCache.getInstance();

        lifeStoryToggle = findViewById(R.id.lifeStoryToggle);
        lifeStoryToggle.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton compoundButton, boolean b) {
                cache.setLifeStoryFilter(b);
            }
        });
        if(cache.getLifeStoryFilter()){
            lifeStoryToggle.setChecked(true);
        }

        familyTreeToggle = findViewById(R.id.familyTreeToggle);
        familyTreeToggle.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton compoundButton, boolean b) {
                cache.setFamilyTreeFilter(b);
            }
        });
        if(cache.getFamilyTreeFilter()){
            familyTreeToggle.setChecked(true);
        }

        spouseToggle = findViewById(R.id.spouseToggle);
        spouseToggle.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton compoundButton, boolean b) {
                cache.setSpouseFilter(b);
            }
        });
        if(cache.getSpouseFilter()){
            spouseToggle.setChecked(true);
        }

        fatherToggle = findViewById(R.id.fatherToggle);
        fatherToggle.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton compoundButton, boolean b) {
                cache.setFatherFilter(b);
            }
        });
        if(cache.getFatherFilter()){
            fatherToggle.setChecked(true);
        }

        motherToggle = findViewById(R.id.motherToggle);
        motherToggle.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton compoundButton, boolean b) {
                cache.setMotherFilter(b);
            }
        });
        if(cache.getMotherFilter()){
            motherToggle.setChecked(true);
        }

        maleToggle = findViewById(R.id.maleToggle);
        maleToggle.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton compoundButton, boolean b) {
                cache.setMaleFilter(b);
            }
        });
        if(cache.getMaleFilter()){
            maleToggle.setChecked(true);
        }

        femaleToggle = findViewById(R.id.femaleToggle);
        femaleToggle.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton compoundButton, boolean b) {
                cache.setFemaleFilter(b);
            }
        });
        if(cache.getFemaleFilter()){
            femaleToggle.setChecked(true);
        }

        logout = findViewById(R.id.logout);
        logout.setOnClickListener(new View.OnClickListener(){

            @Override
            public void onClick(View view) {
                DataCache.getInstance().destructor();
                Intent intent = new Intent(getApplicationContext(), MainActivity.class);
                intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                startActivity(intent);
            }
        });
    }
}
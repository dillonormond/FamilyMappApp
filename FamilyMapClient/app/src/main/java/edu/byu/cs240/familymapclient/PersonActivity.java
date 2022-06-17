package edu.byu.cs240.familymapclient;

import androidx.appcompat.app.ActionBar;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.os.Parcelable;
import android.provider.ContactsContract;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseExpandableListAdapter;
import android.widget.ExpandableListAdapter;
import android.widget.ExpandableListView;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.joanzapata.iconify.IconDrawable;
import com.joanzapata.iconify.fonts.FontAwesomeIcons;

import org.w3c.dom.Text;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;
import java.util.Map;

import model.Event;
import model.Person;

public class PersonActivity extends AppCompatActivity {
    Person currPerson = null;
    Person father = null;
    Person mother = null;
    Person spouse = null;
    List<Person> children = new ArrayList<>();
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        Bundle extras = getIntent().getExtras();
        String personID = null;

        ActionBar ab = getSupportActionBar();

        if(extras != null){
            personID = extras.getString("personID");
        }
        setContentView(R.layout.activity_person);
        DataCache cache = DataCache.getInstance();
        currPerson = cache.getPersonsMap().get(personID);
        if(currPerson.hasFatherID()){
            father = cache.getPersonsMap().get(currPerson.getFatherID());
        }
        if(currPerson.hasSpouseID()){
            spouse = cache.getPersonsMap().get(currPerson.getSpouseID());
        }
        if(currPerson.hasMotherID()){
            mother = cache.getPersonsMap().get(currPerson.getMotherID());
        }

        for(Map.Entry<String, Person> entry : cache.getPersonsMap().entrySet()){
            if(entry.getValue().hasFatherID() && entry.getValue().hasMotherID()){
                if(entry.getValue().getFatherID().equals(personID) || entry.getValue().getMotherID().equals(personID)){
                    children.add(entry.getValue());
                }
            }
        }



        TextView firstName = findViewById(R.id.personFirstName);
        firstName.setText(currPerson.getFirstName());

        TextView lastName = findViewById(R.id.personLastName);
        lastName.setText(currPerson.getLastName());

        TextView gender = findViewById(R.id.gender);
        if(currPerson.getGender().equals("f")){
            gender.setText("Female");
        }
        else if(currPerson.getGender().equals("m")){
            gender.setText("Male");
        }

        ExpandableListView eventExpandable = findViewById(R.id.expandableEvents);
        eventExpandable.setAdapter(new ExpandableListAdapter(father, mother, spouse, children));


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

    private class ExpandableListAdapter extends BaseExpandableListAdapter implements Comparator<Event>{

        private final List<Event> EventList = new ArrayList<>();
        private final List<Person> PersonList = new ArrayList<>();

        private Person adapterFather = null;
        private Person adapterMother = null;
        private Person adapterSpouse = null;
        private List<Person> adapterChildren = null;

        private static final int EVENT_GROUP_POSITION = 0;
        private static final int PERSON_GROUP_POSITION = 1;

        String personID = null;
        ExpandableListAdapter(Person father, Person mother, Person spouse, List<Person> children){
            adapterChildren = children;
            adapterFather = father;
            adapterMother = mother;
            adapterSpouse = spouse;


            DataCache cache = DataCache.getInstance();
            personID = currPerson.getPersonID();
            for(Map.Entry<String, Event> entry : cache.getEventsMap().entrySet()){
                if(entry.getValue().getPersonID().equals(personID)){
                    EventList.add(entry.getValue());
                }
            }

            Collections.sort(EventList, new Comparator<Event>() {
                @Override
                public int compare(Event event, Event event2) {
                    Integer year1 = event.getYear();
                    Integer year2 = event2.getYear();

                    return year1.compareTo(year2);
                }
            });

            if(adapterFather != null){
                PersonList.add(adapterFather);
            }
            if(adapterMother != null){
                PersonList.add(adapterMother);
            }
            if(adapterSpouse != null){
                PersonList.add(adapterSpouse);
            }
            for(Person p : adapterChildren){
                PersonList.add(p);
            }
        }
        @Override
        public int getGroupCount() {
            return 2;
        }

        @Override
        public int getChildrenCount(int i) {
            switch(i){
                case EVENT_GROUP_POSITION:
                    return EventList.size();
                case PERSON_GROUP_POSITION:
                    return PersonList.size();
                default:
                    throw new IllegalArgumentException("unrecognized group position");
            }
        }

        @Override
        public Object getGroup(int i) {
            switch(i){
                case EVENT_GROUP_POSITION:
                    return getString(R.string.eventListTitle);
                case PERSON_GROUP_POSITION:
                    return getString(R.string.personListTitle);
                default:
                    throw new IllegalArgumentException("unrecognized group position");
            }
        }

        @Override
        public Object getChild(int groupPosition, int childPosition) {
            switch(groupPosition){
                case EVENT_GROUP_POSITION:
                    return EventList.get(childPosition);
                case PERSON_GROUP_POSITION:
                    return PersonList.get(childPosition);
                default:
                    throw new IllegalArgumentException("unrecognized group position");
            }
        }

        @Override
        public long getGroupId(int i) {
            return i;
        }

        @Override
        public long getChildId(int i, int childPosition) {
            return childPosition;
        }

        @Override
        public boolean hasStableIds() {
            return false;
        }

        @Override
        public View getGroupView(int groupPosition, boolean isExpanded, View convertView, ViewGroup parent) {
            if(convertView == null){
                convertView = getLayoutInflater().inflate(R.layout.list_item_group, parent, false);
            }
            TextView titleView = convertView.findViewById(R.id.listTitle);

            switch(groupPosition){
                case EVENT_GROUP_POSITION:
                    titleView.setText(R.string.eventListTitle);
                    break;
                case PERSON_GROUP_POSITION:
                    titleView.setText(R.string.personListTitle);
                    break;
                default:
                    throw new IllegalArgumentException("unrecognized group position");
            }
            return convertView;
        }

        @Override
        public View getChildView(int groupPosition, int childPosition, boolean isLastChild, View convertView, ViewGroup parent) {
            View itemView;
            switch(groupPosition){
                case EVENT_GROUP_POSITION:
                    itemView = getLayoutInflater().inflate(R.layout.event_list_item, parent, false);
                    initializeEventList(itemView, childPosition);
                    break;
                case PERSON_GROUP_POSITION:
                    itemView = getLayoutInflater().inflate(R.layout.event_list_item,parent,false);
                    initializePersonList(itemView, childPosition);
                    break;
                default:
                    throw new IllegalArgumentException("unrecognized group position");
            }
            return itemView;
        }

        private void initializeEventList(View eventListView, final int childPosition){
            TextView descriptionView = eventListView.findViewById(R.id.expandableEventDescription);
            Event currEvent = EventList.get(childPosition);
            String eventName = currEvent.getEventType();
            String city = currEvent.getCity();
            String country = currEvent.getCountry();
            String id = currEvent.getEventID();
            int year = currEvent.getYear();

            String description = eventName + ": " + city + ", " + country + " (" + year + ")";
            descriptionView.setText(description);

            TextView nameView = eventListView.findViewById(R.id.expandableEventPersonName);
            String name = currPerson.getFirstName() + " " + currPerson.getLastName();
            nameView.setText(name);

            ImageView iconView = eventListView.findViewById(R.id.personViewIcon);
            Drawable genderIcon = new IconDrawable(PersonActivity.this, FontAwesomeIcons.fa_map_marker).
                    colorRes(R.color.event_icon).sizeDp(40);
            iconView.setImageDrawable(genderIcon);

            LinearLayout eventLayout = eventListView.findViewById(R.id.expandableListItem);
            eventLayout.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    String bundleDescription = eventName + ": " + city + ", " + country + " (" + year + ")";
                    Intent intent = new Intent(PersonActivity.this, EventActivity.class);
                    intent.putExtra("latitude", currEvent.getLatitude());
                    intent.putExtra("longitude", currEvent.getLongitude());
                    intent.putExtra("eventName", bundleDescription);
                    intent.putExtra("personName", name);
                    intent.putExtra("eventID", id);
                    if(currPerson.getGender().equals("f")){
                        intent.putExtra("gender", "f");
                    }
                    if(currPerson.getGender().equals("m")){
                        intent.putExtra("gender", "m");
                    }
                    startActivity(intent);
                }
            });

        }

        private void initializePersonList(View personListView, final int childPosition){
            TextView nameView = personListView.findViewById(R.id.expandableEventDescription);
            Person person = PersonList.get(childPosition);
            String currName = person.getFirstName() + " " + person.getLastName();
            nameView.setText(currName);
            String id = person.getPersonID();
            TextView relationshipView = personListView.findViewById(R.id.expandableEventPersonName);
            ImageView iconView = personListView.findViewById(R.id.personViewIcon);
            if(adapterFather != null && adapterFather.getPersonID().equals(id)){
                    relationshipView.setText(R.string.father);
                    Drawable genderIcon = new IconDrawable(PersonActivity.this, FontAwesomeIcons.fa_male).
                            colorRes(R.color.male_icon).sizeDp(40);
                    iconView.setImageDrawable(genderIcon);
            }
            else if(adapterMother != null && adapterMother.getPersonID().equals(id)){
                    relationshipView.setText(R.string.mother);
                    Drawable genderIcon = new IconDrawable(PersonActivity.this, FontAwesomeIcons.fa_female).
                            colorRes(R.color.female_icon).sizeDp(40);
                    iconView.setImageDrawable(genderIcon);
            }
            if(adapterSpouse != null && adapterSpouse.getPersonID().equals(id)){
                    relationshipView.setText(R.string.spouse);
                    if(person.getGender().equals("m")){
                        Drawable genderIcon = new IconDrawable(PersonActivity.this, FontAwesomeIcons.fa_male).
                                colorRes(R.color.male_icon).sizeDp(40);
                        iconView.setImageDrawable(genderIcon);
                    }
                    if(person.getGender().equals("f")){
                        Drawable genderIcon = new IconDrawable(PersonActivity.this, FontAwesomeIcons.fa_female).
                                colorRes(R.color.female_icon).sizeDp(40);
                        iconView.setImageDrawable(genderIcon);
                    }
            }
            if(children != null && children.contains(person)){
                    relationshipView.setText(R.string.child);
                    if(person.getGender().equals("m")){
                        Drawable genderIcon = new IconDrawable(PersonActivity.this, FontAwesomeIcons.fa_male).
                                colorRes(R.color.male_icon).sizeDp(40);
                        iconView.setImageDrawable(genderIcon);
                    }
                    if(person.getGender().equals("f")){
                        Drawable genderIcon = new IconDrawable(PersonActivity.this, FontAwesomeIcons.fa_female).
                                colorRes(R.color.female_icon).sizeDp(40);
                        iconView.setImageDrawable(genderIcon);
                    }
            }
            LinearLayout personLayout = personListView.findViewById(R.id.expandableListItem);
            personLayout.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    Intent intent = new Intent(PersonActivity.this, PersonActivity.class);
                    intent.putExtra("personID", id);
                    startActivity(intent);
                }
            });
        }

        @Override
        public boolean isChildSelectable(int i, int i1) {
            return true;
        }

        @Override
        public int compare(Event event1, Event event2) {
            int startComparison = Integer.compare(event1.getYear(), event2.getYear());
            return startComparison;
        }
    }
}
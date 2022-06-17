package edu.byu.cs240.familymapclient;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.content.Intent;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.SearchView;
import android.widget.TextView;

import com.joanzapata.iconify.IconDrawable;
import com.joanzapata.iconify.fonts.FontAwesomeIcons;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;

import model.Event;
import model.Person;

public class Search extends AppCompatActivity {
    Map<String, Event> mapEvents = new HashMap<>();
    Map<String, Person> mapPerson = new HashMap<>();
    List<Event> listEvents = new ArrayList<>();
    List<Person> listPerson = new ArrayList<>();
    String search;
    DataCache cache;
    SearchView searchBox;

    private static final int EVENT_TYPE = 0;
    private static final int PERSON_TYPE = 1;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_search);
        RecyclerView recyclerView = findViewById(R.id.RecyclerView);
        recyclerView.setLayoutManager(new LinearLayoutManager(Search.this));
        cache = DataCache.getInstance();
        mapEvents = cache.getEventsMap();

        for(Map.Entry<String, Event> entry : cache.getFilteredEvents().entrySet()){
            listEvents.add(entry.getValue());
        }

        mapPerson = cache.getPersonsMap();
        for(Map.Entry<String, Person> entry : mapPerson.entrySet()){
            listPerson.add(entry.getValue());
        }

        searchBox = findViewById(R.id.searchBox);
        searchBox.setOnQueryTextListener(new SearchView.OnQueryTextListener(){

            @Override
            public boolean onQueryTextSubmit(String s) {
                return false;
            }

            @Override
            public boolean onQueryTextChange(String newText) {
                List<Event> searchedEvents = new ArrayList<>();
                List<Person> searchedPersons = new ArrayList<>();

                for(Event e : listEvents){
                    DataCache cache = DataCache.getInstance();
                    Person person = cache.getPersonsMap().get(e.getPersonID());
                    String name = person.getFirstName() + " " + person.getLastName();

                    if(name.toLowerCase().contains(newText.toLowerCase())){
                        searchedEvents.add(e);
                    }
                    if(e.getEventType().toLowerCase().contains(newText.toLowerCase())){
                        searchedEvents.add(e);
                    }
                    else if(e.getCity().toLowerCase().contains(newText.toLowerCase())){
                        searchedEvents.add(e);
                    }
                    else if(e.getCountry().toLowerCase().contains(newText.toLowerCase())){
                        searchedEvents.add(e);
                    }
                    else if(Integer.toString(e.getYear()).contains(newText.toLowerCase())){
                        searchedEvents.add(e);
                    }

                }

                for(Person p : listPerson){
                    String name = p.getFirstName() + " " + p.getLastName();
                    if(name.toLowerCase().contains(newText.toLowerCase())){
                        searchedPersons.add(p);
                    }
                }
                SearchAdapter adapter = new SearchAdapter(searchedEvents, searchedPersons);
                adapter.notifyDataSetChanged();
                recyclerView.setAdapter(adapter);
                return false;
            }
        });

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

    private class SearchAdapter extends RecyclerView.Adapter<SearchViewHolder>{
        private final List<Event> events;
        private final List<Person> persons;


        SearchAdapter(List<Event> events, List<Person> persons){
            this.events = events;
            this.persons = persons;
        }

        @NonNull
        @Override
        public SearchViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
            View view;

            view = getLayoutInflater().inflate(R.layout.event_list_item, parent, false);
            SearchViewHolder newHolder = new SearchViewHolder(view, viewType);
            newHolder.setIsRecyclable(false);

            return newHolder;
        }

        @Override
        public int getItemViewType(int position){
            return position < events.size() ? EVENT_TYPE : PERSON_TYPE;
        }


        @Override
        public void onBindViewHolder(@NonNull SearchViewHolder holder, int position) {

            if(position < events.size()){
                Event event = events.get(position);
                holder.topText.setText(event.getEventType() + ": " + event.getCity() + ", " + event.getCountry() + " (" + event.getYear() + ")");
                holder.bottomText.setText(mapPerson.get(event.getPersonID()).getFirstName() + " " + mapPerson.get(event.getPersonID()).getLastName());
                Drawable eventIcon = new IconDrawable(Search.this, FontAwesomeIcons.fa_map_marker).
                        colorRes(R.color.event_icon).sizeDp(20);
                holder.icon.setImageDrawable(eventIcon);
                holder.event = event;
      //          holder.bind(events.get(position));
            }
            else{
                Person person = persons.get(position - events.size());
                holder.topText.setText(person.getFirstName() + " " + person.getLastName());
                holder.person = person;
                if(person.getGender().equals("m")){
                    Drawable genderIcon = new IconDrawable(Search.this, FontAwesomeIcons.fa_male).
                            colorRes(R.color.male_icon).sizeDp(20);
                    holder.icon.setImageDrawable(genderIcon);
                }
                if(person.getGender().equals("f")){
                    Drawable genderIcon = new IconDrawable(Search.this, FontAwesomeIcons.fa_female).
                            colorRes(R.color.female_icon).sizeDp(20);
                    holder.icon.setImageDrawable(genderIcon);
                }
   //             holder.bind(persons.get(position - events.size()));
            }
        }

        @Override
        public int getItemCount() {
            return events.size() + persons.size();
        }
    }

    private class SearchViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener{
        int type;
        TextView topText = findViewById(R.id.expandableEventDescription);
        Person person;
        Event event;
        TextView bottomText = findViewById(R.id.expandableEventPersonName);
        ImageView icon = findViewById(R.id.personViewIcon);
        public SearchViewHolder(@NonNull View itemView, int viewType) {
            super(itemView);
            itemView.setOnClickListener(this);
            topText = itemView.findViewById(R.id.expandableEventDescription);
            bottomText = itemView.findViewById(R.id.expandableEventPersonName);
            icon = itemView.findViewById(R.id.personViewIcon);
            type = viewType;
        }
        @Override
        public void onClick(View view) {
            if(type == EVENT_TYPE){
                Person currPerson = cache.getPersonsMap().get(event.getPersonID());
                String name = currPerson.getFirstName() + " " + currPerson.getLastName();
                String bundleDescription = event.getEventType() + ": " + event.getCity() + ", " + event.getCountry() + " (" + event.getYear() + ")";
                Intent intent = new Intent(Search.this, EventActivity.class);
                intent.putExtra("latitude", event.getLatitude());
                intent.putExtra("longitude", event.getLongitude());
                intent.putExtra("eventName", bundleDescription);
                intent.putExtra("personName", name);
                intent.putExtra("eventID", event.getEventID());
                if(currPerson.getGender().equals("f")){
                    intent.putExtra("gender", "f");
                }
                if(currPerson.getGender().equals("m")){
                    intent.putExtra("gender", "m");
                }
                startActivity(intent);
            }
            else{
                Intent intent = new Intent(Search.this, PersonActivity.class);
                intent.putExtra("personID", person.getPersonID());
                startActivity(intent);
            }
        }
    }

}


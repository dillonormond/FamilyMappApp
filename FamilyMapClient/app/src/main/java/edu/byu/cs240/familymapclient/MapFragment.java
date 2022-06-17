package edu.byu.cs240.familymapclient;


import android.content.Intent;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.Projection;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.BitmapDescriptorFactory;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;
import com.google.android.gms.maps.model.Polyline;
import com.google.android.gms.maps.model.PolylineOptions;
import com.joanzapata.iconify.IconDrawable;
import com.joanzapata.iconify.fonts.FontAwesomeIcons;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

import model.Event;
import model.Person;


public class MapFragment extends Fragment implements OnMapReadyCallback, GoogleMap.OnMapLoadedCallback {
    private GoogleMap map;
    View view;
    boolean hasOptions = true;
    float latitude;
    float longitude;
    String description;
    String personName;
    boolean fromEventFragment = false;
    String eventGender = null;
    String eventID = null;

    @Override
    public View onCreateView(@NonNull LayoutInflater layoutInflater, ViewGroup container, Bundle savedInstanceState) {
        super.onCreateView(layoutInflater, container, savedInstanceState);
        view = layoutInflater.inflate(R.layout.fragment_map, container, false);
        if(hasOptions) {
            setHasOptionsMenu(true);
        }
        SupportMapFragment mapFragment = (SupportMapFragment) getChildFragmentManager().findFragmentById(R.id.map);
        mapFragment.getMapAsync(this);


        return view;
    }

    @Override
    public void onCreateOptionsMenu(Menu menu, MenuInflater inflater) {

        MenuInflater menuInflater = getActivity().getMenuInflater();
        menuInflater.inflate(R.menu.map_frag_menu, menu);

        MenuItem settingsItem = menu.findItem(R.id.menu_settings);
        MenuItem searchItem = menu.findItem(R.id.menu_search);

        searchItem.setIcon(new IconDrawable(getActivity(), FontAwesomeIcons.fa_search).colorRes(R.color.white)
                            .actionBarSize());
        settingsItem.setIcon(new IconDrawable(getActivity(), FontAwesomeIcons.fa_gear).colorRes(R.color.white)
                .actionBarSize());
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle item selection
        switch (item.getItemId()) {
            case R.id.menu_settings:
                Intent menuIntent = new Intent(getActivity(), SettingsActivity.class);
                startActivity(menuIntent);
                return true;

            case R.id.menu_search:
                Intent intent = new Intent(getActivity(), Search.class);
                startActivity(intent);
                return true;
            default:
                return super.onOptionsItemSelected(item);
        }
    }

    @Override
    public void onMapReady(GoogleMap googleMap) {
        map = googleMap;
        map.setOnMapLoadedCallback(this);
        DataCache cache = DataCache.getInstance();
        cache.setStoredMap(map);
        cache.filterEvents();

        Map<String, Event> eventMap = cache.getEventsMap();

        drawMap();

        if(fromEventFragment){
            Event selectedEvent = cache.getEventsMap().get(eventID);
            Projection projection = map.getProjection();
            LatLng coord = new LatLng(latitude, longitude);
            map.animateCamera(CameraUpdateFactory.newLatLng(coord));
            TextView titleText = view.findViewById(R.id.eventTitleText);
            TextView descText = view.findViewById(R.id.eventDescription);
            titleText.setText(personName);
            descText.setText(description);
            ImageView iconView = view.findViewById(R.id.eventIcon);
            if(eventGender.equals("f")){
                Drawable genderIcon = new IconDrawable(getActivity(), FontAwesomeIcons.fa_female).
                        colorRes(R.color.female_icon).sizeDp(40);
                iconView.setImageDrawable(genderIcon);
            }
            if(eventGender.equals("m")){
                Drawable genderIcon = new IconDrawable(getActivity(), FontAwesomeIcons.fa_male).
                        colorRes(R.color.male_icon).sizeDp(40);
                iconView.setImageDrawable(genderIcon);
            }
            drawAllLines(selectedEvent);
        }
    }

    @Override
    public void onResume() {
        super.onResume();
        DataCache cache = DataCache.getInstance();
        if(cache.getStoredMap() != null){
            map = cache.getStoredMap();
            map.clear();
            cache.filterEvents();
            drawMap();
        }

    }


    @Override
    public void onMapLoaded() {
        // You probably don't need this callback. It occurs after onMapReady and I have seen
        // cases where you get an error when adding markers or otherwise interacting with the map in
        // onMapReady(...) because the map isn't really all the way ready. If you see that, just
        // move all code where you interact with the map (everything after
        // map.setOnMapLoadedCallback(...) above) to here.
    }

    private void openPersonActivity(Event event){
        Intent intent = new Intent(getActivity(), PersonActivity.class);
        intent.putExtra("personID", event.getPersonID());
        startActivity(intent);
    }

    private void drawMap(){
        DataCache cache = DataCache.getInstance();
        Map<String, Float> eventColors = new HashMap<>();
        Set<String> usedEvents = new HashSet<>();
        float colorIndex = 0;
        for(Map.Entry<String, Event> entry : cache.getFilteredEvents().entrySet()){
            String eventLowerCase = entry.getValue().getEventType().toLowerCase();
            if (!usedEvents.contains(eventLowerCase)){
                eventColors.put(eventLowerCase, colorIndex);
                colorIndex+=25;
                if(colorIndex > 360){
                    colorIndex = 0;
                }
                usedEvents.add(eventLowerCase);
            }
        }

        List<String> tempMarkerEventList= new ArrayList<>();
        cache.setCurrentMarkers(tempMarkerEventList);
        for(Map.Entry<String, Event> entry : cache.getFilteredEvents().entrySet()){
            float color = eventColors.get(entry.getValue().getEventType().toLowerCase());
            LatLng coordinates = new LatLng(entry.getValue().getLatitude(), entry.getValue().getLongitude());
            Marker marker = map.addMarker(new MarkerOptions().position(coordinates).icon(BitmapDescriptorFactory.defaultMarker(color)));
            cache.addToMarkerEvents(entry.getValue().getEventID());
            marker.setTag(entry.getValue());
            GoogleMap.OnMarkerClickListener listener = new GoogleMap.OnMarkerClickListener() {
                @Override
                public boolean onMarkerClick(@NonNull Marker marker) {
                    for(Polyline l : cache.getLines()){
                        l.remove();
                    }
                    cache.setLines(new ArrayList<>());
                    DataCache cache = DataCache.getInstance();
                    TextView titleText = view.findViewById(R.id.eventTitleText);
                    TextView descText = view.findViewById(R.id.eventDescription);
                    Event markerEvent = (Event) marker.getTag();
                    String personID = markerEvent.getPersonID();

                    String fName = cache.getPersonsMap().get(personID).getFirstName();
                    String lName = cache.getPersonsMap().get(personID).getLastName();

                    String eventName = markerEvent.getEventType();
                    String city = markerEvent.getCity();
                    String country = markerEvent.getCountry();
                    int year = markerEvent.getYear();

                    String description = eventName + ": " + city + ", " + country + " (" + year + ")";
                    String fullName = fName + " " + lName;
                    titleText.setText(fullName);
                    descText.setText(description);

                    String gender = cache.getPersonsMap().get(personID).getGender();
                    ImageView iconView = view.findViewById(R.id.eventIcon);
                    if(gender.equals("m")){
                        Drawable genderIcon = new IconDrawable(getActivity(), FontAwesomeIcons.fa_male).
                                colorRes(R.color.male_icon).sizeDp(40);
                        iconView.setImageDrawable(genderIcon);
                    }
                    else if(gender.equals("f")){
                        Drawable genderIcon = new IconDrawable(getActivity(), FontAwesomeIcons.fa_female).
                                colorRes(R.color.female_icon).sizeDp(40);
                        iconView.setImageDrawable(genderIcon);
                    }

                    LinearLayout eventInfo = view.findViewById(R.id.eventInfo);
                    eventInfo.setOnClickListener(new View.OnClickListener(){

                        @Override
                        public void onClick(View view) {
                            openPersonActivity(markerEvent);
                        }
                    });
                    drawAllLines((Event) marker.getTag());
                    return false;
                }
            };
            map.setOnMarkerClickListener(listener);
        }
    }

    private void drawAllLines(Event selectedEvent){
        DataCache cache = DataCache.getInstance();
        if(cache.getSpouseFilter()){
            Set<String> usedPersons = new HashSet<>();

                if(!usedPersons.contains(selectedEvent.getPersonID())){
                    usedPersons.add(selectedEvent.getPersonID());
                    spouseLines(selectedEvent.getPersonID(), selectedEvent);
                }

        }
        Event origin = null;
        if(cache.getFamilyTreeFilter()){
                    origin = selectedEvent;
            familyTreeLinesHelper(origin);
        }
        if(cache.getLifeStoryFilter()){
            lifeStoryLines(selectedEvent);
        }
    }

    private void spouseLines(String originPersonID, Event selectedEvent){
        DataCache cache = DataCache.getInstance();
        Person originPerson = cache.getPersonsMap().get(originPersonID);
        List<Event> originEvents = cache.getPersonEvents().get(originPersonID);
        List<Event> spouseEvents = cache.getPersonEvents().get(originPerson.getSpouseID());
        Event targetEvent = null;
        int minYear = -1;
        if(spouseEvents != null) {
            for (Event e : spouseEvents) {
                if (e.getEventID().toLowerCase().contains("birth")) {
                    targetEvent = e;
                    break;
                } else if (minYear == -1) {
                    targetEvent = e;
                } else if (e.getYear() < minYear) {
                    targetEvent = e;
                }
            }
                if (targetEvent != null && cache.getCurrentMarkers().contains(targetEvent.getEventID())) {
                    connectMarkers(selectedEvent, targetEvent, getContext().getResources().getColor(R.color.lime_green), 31);
                }
        }
    }

    private void lifeStoryLines(Event originEvent){
        DataCache cache = DataCache.getInstance();
        List<Event> lifeEvents = cache.getPersonEvents().get(originEvent.getPersonID());

        Collections.sort(lifeEvents, new Comparator<Event>() {
            @Override
            public int compare(Event event, Event event2) {
                Integer year1 = event.getYear();
                Integer year2 = event2.getYear();

                return year1.compareTo(year2);
            }
        });

        for(int i = 0; i < lifeEvents.size(); i++){
            if(i < lifeEvents.size() - 1){
                connectMarkers(lifeEvents.get(i), lifeEvents.get(i  + 1), getContext().getResources().getColor(R.color.purple_200), 30);
            }
        }
    }

    private void familyTreeLinesHelper(Event originEvent){
        DataCache cache = DataCache.getInstance();
        Person originPerson = cache.getPersonsMap().get(originEvent.getPersonID());
        familyTreeRecursion(originPerson, originEvent, 31);

    }
    private void familyTreeRecursion(Person originPerson, Event originEvent, int width){
        DataCache cache = DataCache.getInstance();
        if(originPerson.getFatherID() != null) {
            List<Event> fatherEvents = cache.getPersonEvents().get(originPerson.getFatherID());
            List<Event> motherEvents = cache.getPersonEvents().get(originPerson.getMotherID());

            Person father = cache.getPersonsMap().get(originPerson.getFatherID());
            Person mother = cache.getPersonsMap().get(originPerson.getMotherID());

            Event motherEvent = findBirthEvent(motherEvents, originEvent, width);
            Event fatherEvent = findBirthEvent(fatherEvents, originEvent, width);

            if(father.hasFatherID()){
                familyTreeRecursion(father, fatherEvent, width-10);
            }
            if(mother.hasFatherID()){
                familyTreeRecursion(mother, motherEvent, width-8);
            }
        }

    }

    private Event findBirthEvent(List<Event> events, Event originEvent, int width) {
        DataCache cache = DataCache.getInstance();
        Event targetEvent = null;
        int minYear = -1;
        if (events != null) {
            for (Event e : events) {
                if (e.getEventID().toLowerCase().contains("birth")) {
                    targetEvent = e;
                    break;
                } else if (minYear == -1) {
                    targetEvent = e;
                } else if (e.getYear() < minYear) {
                    targetEvent = e;
                }
            }
            if (targetEvent != null && cache.getCurrentMarkers().contains(targetEvent.getEventID())) {
                connectMarkers(originEvent, targetEvent, getContext().getResources().getColor(R.color.teal_700), width);
            }

        }
        return targetEvent;
    }

    private void connectMarkers(Event startEvent, Event endEvent, int googleColor, float width){
        LatLng startPoint = new LatLng(startEvent.getLatitude(), startEvent.getLongitude());
        LatLng endPoint = new LatLng(endEvent.getLatitude(), endEvent.getLongitude());
        map = DataCache.getInstance().getStoredMap();
        PolylineOptions options = new PolylineOptions()
        .add(startPoint)
        .add(endPoint)
        .color(googleColor)
        .width(width);

        Polyline line = map.addPolyline(options);
        DataCache.getInstance().getLines().add(line);
    }


    public void setHasOptionsFalse(){
        hasOptions = false;
    }

    public void setLatitude(float latitude) {
        this.latitude = latitude;
    }

    public void setLongitude(float longitude) {
        this.longitude = longitude;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public void setPersonName(String personName) {
        this.personName = personName;
    }

    public void setFromEventFragmentTrue(){
        fromEventFragment = true;
    }
    public void setEventGender(String gender){
        eventGender = gender;
    }
    public void setEventID(String id){
        eventID = id;
    }
}

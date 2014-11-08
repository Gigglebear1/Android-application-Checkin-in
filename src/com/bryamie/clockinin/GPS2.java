package com.bryamie.clockinin;

import android.location.Criteria;
import android.location.Geocoder;
import android.location.Location;
import android.location.LocationManager;
import android.os.Bundle;
import android.support.v4.app.FragmentActivity;
import android.view.Menu;
import android.view.MenuItem;

import com.google.android.gms.maps.CameraUpdate;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.Circle;
import com.google.android.gms.maps.model.CircleOptions;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;

public class GPS2 extends FragmentActivity {

	private GoogleMap map;
	private Geocoder geocoder;
	private double longitude;
	private double latitude;
	private Circle mCircle;
	private Marker mMarker;
	private LatLng position;
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_gps2);
		setUpMapIfNeeded();
	}

	private void setUpMapIfNeeded() {
		//Check if map has been instantiated
		if(map == null){
			GoogleMap mMap = ((SupportMapFragment) getSupportFragmentManager().findFragmentById(R.id.map)).getMap();
			mMap.setMyLocationEnabled(true);
			
			double lat = Double.parseDouble(getIntent().getExtras().getString("lat"));
			double longitude = Double.parseDouble(getIntent().getExtras().getString("longitude"));
			double radius = Double.parseDouble(getIntent().getExtras().getString("radius"));

			CameraUpdate center=CameraUpdateFactory.newLatLng(new LatLng(lat,longitude));
			CameraUpdate zoom=CameraUpdateFactory.zoomTo(18);
			
			mMap.addMarker(new MarkerOptions()
			    .position(new LatLng(lat,longitude ))
			    .title("Your Address"));
			
			//make latlng
			position = new LatLng(lat,longitude);
			
			
			//circle
			int strokeColor = 0xffff0000; //red outline
		    int shadeColor = 0x44ff0000; //opaque red fill

		    CircleOptions circleOptions = new CircleOptions().center(position).radius(radius).fillColor(shadeColor).strokeColor(strokeColor).strokeWidth(8);
		    mCircle = mMap.addCircle(circleOptions);

		    MarkerOptions markerOptions = new MarkerOptions().position(position);
		    mMarker = mMap.addMarker(markerOptions);

		    mMap.moveCamera(center);
		    mMap.animateCamera(zoom);
		}
		if(map != null){
			setUpMap();
		}
	}

	private void setUpMap() {
		// Enable my location layer on google map
		map.setMyLocationEnabled(true);
		
		//Get LocationManager object from system service
		LocationManager location = (LocationManager) getSystemService(LOCATION_SERVICE);
		
		//Get a criteria object to retrieve provider
		Criteria criteria = new Criteria();
		
		//Get name of best provider
		String provider = location.getBestProvider(criteria, true);
		
		//Get current location
		Location myLocation = location.getLastKnownLocation(provider);
		
		//Set up map type
		map.setMapType(GoogleMap.MAP_TYPE_NORMAL);
		
		//Get latitude of current location
		latitude = myLocation.getLatitude();
		
		//Get longitude for current location
		longitude = myLocation.getLongitude();
		
		//Create LatLng object for current location
		LatLng latLng = new LatLng(latitude, longitude);
		
		//Show current location in google map
		map.moveCamera(CameraUpdateFactory.newLatLng(latLng));
		
		map.animateCamera(CameraUpdateFactory.zoomTo(20));
	}

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		// Inflate the menu; this adds items to the action bar if it is present.
		getMenuInflater().inflate(R.menu.g, menu);
		return true;
	}

	@Override
	public boolean onOptionsItemSelected(MenuItem item) {
		// Handle action bar item clicks here. The action bar will
		// automatically handle clicks on the Home/Up button, so long
		// as you specify a parent activity in AndroidManifest.xml.
		int id = item.getItemId();
		if (id == R.id.action_settings) {
			return true;
		}
		return super.onOptionsItemSelected(item);
	}
	
	}


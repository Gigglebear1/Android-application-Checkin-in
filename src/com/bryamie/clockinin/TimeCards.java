package com.bryamie.clockinin;

import java.util.ArrayList;
import java.util.List;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.Spinner;
import android.widget.Toast;

import com.parse.FindCallback;
import com.parse.ParseObject;
import com.parse.ParseQuery;
import com.parse.ParseUser;

public class TimeCards extends Activity {
	private static Spinner sItems;
	private static List<String> spinnerArray = new ArrayList<String>();
	private static ArrayAdapter<String> adapter;
	private static List<String> your_array_list;
	private static ArrayAdapter<String> arrayAdapter;
	public static String jobTitle;
	public static String bizID;
	public static String date;
	List<ParseObject> copy;
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_time_cards);
		update();
	}
	public void update(){
		
		bizID = (String) ParseUser.getCurrentUser().get("businessID");
		spinnerArray.removeAll(spinnerArray);
		spinnerArray.add("Select Employee");
		
		ParseQuery<ParseObject> query = ParseQuery.getQuery("Employee");
		query.whereEqualTo("businessID", bizID);
		query.findInBackground(new FindCallback<ParseObject>() {
			public void done(List<ParseObject> employees,
					com.parse.ParseException e) {
				if (e == null) {
					ArrayList<ParseObject> arraylist = new ArrayList<ParseObject>(employees);
					copy = employees;
					for (ParseObject element : arraylist) {
						spinnerArray.add((String) element.get("firstName")
								+ " " + (String) element.get("lastName"));
					}
					if (spinnerArray.size() < 1) {
						spinnerArray.removeAll(spinnerArray);
						spinnerArray.add("No Employees avaliable");
					}
				}
			}
		});
		
		adapter = new ArrayAdapter<String>(this,
				android.R.layout.simple_spinner_item, spinnerArray);

		adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
		sItems = (Spinner) findViewById(R.id.spinner1);
		sItems.setAdapter(adapter);


	}
	
	public void viewTimeClick(View view){
		String selected = sItems.getSelectedItem().toString();
		int position = adapter.getPosition(selected);
		

		if (position > 0) {
			ParseObject user = copy.get(position-1);
			String userName = user.get("userName").toString();
			String email = (String) ParseUser.getCurrentUser().get("email");
			Intent intent = new Intent(this, ViewingTimeSheet.class);
			intent.putExtra("userName", userName);
			intent.putExtra("email", email);
	    	startActivity(intent);
		   }
		else{
			Toast.makeText(getBaseContext(),"Select job" , Toast.LENGTH_LONG).show();
		}
		
	}

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		// Inflate the menu; this adds items to the action bar if it is present.
		getMenuInflater().inflate(R.menu.time_cards, menu);
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

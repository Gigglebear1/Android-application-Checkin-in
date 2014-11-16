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
import com.parse.GetCallback;
import com.parse.ParseException;
import com.parse.ParseObject;
import com.parse.ParseQuery;
import com.parse.ParseUser;

public class ManageJob extends Activity {
	private static Spinner sItems;
	private static List<String> spinnerArray;
	private static ArrayAdapter<String> adapter;
	private static String bizID;


	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_manage_job);
		
		//fill spinner
		sItems = (Spinner) findViewById(R.id.spinner1);
		spinnerArray =  new ArrayList<String>();
		spinnerArray.add("Select Job");
		
		bizID = (String) ParseUser.getCurrentUser().get("businessID");
		
		/*
		 * fill the spinner array with job titles under that businessID 
		 */
		 ParseQuery<ParseObject> jobList = ParseQuery.getQuery("Jobsite");
			jobList.whereEqualTo("businessID", bizID);
			jobList.findInBackground(new FindCallback<ParseObject>() {
			    public void done(List<ParseObject> spinnerList, com.parse.ParseException e) {
			        if (e == null) {
			        	ArrayList<ParseObject> arraylist = new ArrayList<ParseObject>(spinnerList);
			        	for (ParseObject element : spinnerList) {
			        		spinnerArray.add((String) element.get("jobName"));
			        		
		        		if(spinnerArray.size() <= 1){
		        			spinnerArray.removeAll(spinnerArray);
				        	spinnerArray.add("No jobs avaliable");
		        		}
			        }
			        	
			        } else {
			        	spinnerArray.removeAll(spinnerArray);
			        	spinnerArray.add("No jobs avaliable");
			        }
			    }
			});
		
		 adapter = new ArrayAdapter<String>(
		    this, android.R.layout.simple_spinner_item, spinnerArray);

		adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
		sItems.setAdapter(adapter);
	}

	public void DeleteJobClick(View view){
		final String selected = sItems.getSelectedItem().toString();
		int position = adapter.getPosition(selected);
		
		//remove from database then ...
		
		if(position > 0){
			
			ParseQuery<ParseObject> query = ParseQuery.getQuery("Jobsite");
			query.whereEqualTo("businessID", bizID);
			query.whereEqualTo("jobName", selected);
			query.getFirstInBackground(new GetCallback<ParseObject>() {
				public void done(ParseObject object, com.parse.ParseException e) {
					if(object != null){
						try {
							object.delete();
							adapter.remove(selected);
							 adapter.notifyDataSetChanged();
						} catch (ParseException e1) {
							// TODO Auto-generated catch block
							e1.printStackTrace();
						}
						 
					}
				}
			});
		   
		    
		    Toast.makeText(getBaseContext(),selected+" Deleted" , Toast.LENGTH_LONG).show();
		   }
		else{
			Toast.makeText(getBaseContext(),"Cannot Delete" , Toast.LENGTH_LONG).show();
		}
	}
	
	public void EditJobClick(View view){
		String selected = sItems.getSelectedItem().toString();
		int position = adapter.getPosition(selected);
		
		if(position > 0){
			Intent intent = new Intent(this, EditJob.class);
			intent.putExtra("jobName", selected);
	    	startActivity(intent);
		   }
		else{
			Toast.makeText(getBaseContext(),"Select job" , Toast.LENGTH_LONG).show();
		}
	}
    
	
	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		// Inflate the menu; this adds items to the action bar if it is present.
		getMenuInflater().inflate(R.menu.manage_job, menu);
		return true;
	}
	
	public void AddEmployeeClick(View view){
		String selected = sItems.getSelectedItem().toString();
		int position = adapter.getPosition(selected);
		
		if(position > 0){
			Intent intent = new Intent(this, AddEmployee.class);
			intent.putExtra("jobName", selected);
	    	startActivity(intent);
		   }
		else{
			Toast.makeText(getBaseContext(),"Select job" , Toast.LENGTH_LONG).show();
		}
		
    	
    }
	
	public void TimeCardClick(View view){
		Intent intent = new Intent(this, TimeCards.class);
    	startActivity(intent);
	
    	
    }

	public void DeleteEmployeeClick(View view){
		String selected = sItems.getSelectedItem().toString();
		int position = adapter.getPosition(selected);
		
		if(position > 0){
			Intent intent = new Intent(this, DeleteEmployee.class);
			intent.putExtra("jobName", selected);
	    	startActivity(intent);
		   }
		else{
			Toast.makeText(getBaseContext(),"Select job" , Toast.LENGTH_LONG).show();
		}
    	
    }
	
	public void EditEmployee(View view){
			Intent intent = new Intent(this, ManageEmployee.class);
	    	startActivity(intent);
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

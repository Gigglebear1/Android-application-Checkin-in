package com.bryamie.clockinin;

import java.util.ArrayList;
import java.util.List;

import com.parse.FindCallback;
import com.parse.ParseObject;
import com.parse.ParseQuery;
import com.parse.ParseUser;

import android.support.v7.app.ActionBarActivity;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.ListView;
import android.widget.Spinner;
import android.widget.Toast;

public class DeleteEmployee extends ActionBarActivity {
	private ListView lv;
	private static Spinner sItems;
	private static List<String> spinnerArray;
	private static ArrayAdapter<String> adapter;
	private static List<String> your_array_list;
	private static ArrayAdapter<String> arrayAdapter;
	public static String jobTitle;
	public static String bizID;
	List<ParseObject> copy;
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_delete_employee);
		jobTitle = getIntent().getExtras().getString("jobName");
		bizID = (String) ParseUser.getCurrentUser().get("businessID");
		
		//spinner
		sItems = (Spinner) findViewById(R.id.spinner1);
		spinnerArray =  new ArrayList<String>();
		spinnerArray.add("Select Employee");

		//this is the list view 
		lv = (ListView) findViewById(R.id.employeeTimeCard);

        // Instanciating an array list (you don't need to do this, 
        // you already have yours).
        your_array_list = new ArrayList<String>();
        
        ParseQuery<ParseObject> employeesOnJob = ParseQuery.getQuery("Timecard");
        employeesOnJob = ParseQuery.getQuery("Timecard");
		employeesOnJob.whereEqualTo("businessID", bizID);
		employeesOnJob.whereEqualTo("jobName", jobTitle);
		employeesOnJob.findInBackground(new FindCallback<ParseObject>() {
		  public void done(List<ParseObject> employeesNotOnJob, com.parse.ParseException e) {
			  ArrayList<ParseObject> arraylist = new ArrayList<ParseObject>(employeesNotOnJob);
			  copy = employeesNotOnJob;
	        	for (ParseObject element : arraylist) {
	        		your_array_list.add((String)element.get("firstName")+" " +(String)element.get("lastName"));
	        		spinnerArray.add((String)element.get("firstName")+" " +(String)element.get("lastName"));
	        	}
      		if(your_array_list.size() < 1){
      			your_array_list.removeAll(your_array_list);
      			your_array_list.add("No Employees avaliable");
      			spinnerArray.removeAll(your_array_list);
      			spinnerArray.add("No Employees avaliable");
      		}
	        	
		  }
		});
		 adapter = new ArrayAdapter<String>(
				    this, android.R.layout.simple_spinner_item, spinnerArray);

				adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
				sItems.setAdapter(adapter);
        
        // This is the array adapter, it takes the context of the activity as a 
        // first parameter, the type of list view as a second parameter and your 
        // array as a third parameter.
        arrayAdapter = new ArrayAdapter<String>(
                this, 
                android.R.layout.simple_list_item_1,
                your_array_list );

        lv.setAdapter(arrayAdapter); 
   
	}
	
	public void DeleteClick(View view){
		String selected = sItems.getSelectedItem().toString();
		int position = adapter.getPosition(selected);
		
		//remove from database then ...
		
		if(position > 0){
			//delete from spinner
		    adapter.remove(selected);
		    adapter.notifyDataSetChanged();
		    
		    arrayAdapter.remove(selected);
		    arrayAdapter.notifyDataSetChanged();
		    ParseObject employee = copy.get( position-1);
		     ParseQuery<ParseObject>toDelete = ParseQuery.getQuery("Timecard");
			   toDelete.whereEqualTo("userName", employee.get("userName"));
			   toDelete.whereEqualTo("jobName", jobTitle);
			   toDelete.findInBackground(new FindCallback<ParseObject>() {
				    public void done(List<ParseObject> spinnerList, com.parse.ParseException e) {
				        if (e == null) {
				        	for (ParseObject element : spinnerList) {
				        		element.deleteInBackground();
				        	}      
				        } 
				    }
				        
				});
		    
		    //popup
		    Toast.makeText(getBaseContext(), selected + " Deleted", Toast.LENGTH_LONG).show();
		   }
		else{
			Toast.makeText(getBaseContext(),"Cannot Delete" , Toast.LENGTH_LONG).show();
		}
	}

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		// Inflate the menu; this adds items to the action bar if it is present.
		getMenuInflater().inflate(R.menu.delete_employee, menu);
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

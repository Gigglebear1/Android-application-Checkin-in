package com.bryamie.clockinin;

import java.util.ArrayList;
import java.util.List;

import android.os.Bundle;
import android.support.v7.app.ActionBarActivity;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.ListView;
import android.widget.Spinner;
import android.widget.Toast;

import com.parse.FindCallback;
import com.parse.GetCallback;
import com.parse.ParseException;
import com.parse.ParseObject;
import com.parse.ParseQuery;
import com.parse.ParseUser;

public class AddEmployee extends ActionBarActivity {
	private ListView lv;
	private static Spinner sItems;
	private static List<String> spinnerArray =  new ArrayList<String>();
	private static ArrayAdapter<String> adapter;
	private static List<String> your_array_list;
	private static ArrayAdapter<String> arrayAdapter;
	public static String jobTitle;
	public static String bizID;
	public static String date;
	public static int tTo;
	public static int tFrom;
	List<ParseObject> copy;
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_add_employee);
		jobTitle = getIntent().getExtras().getString("jobName");
		bizID = (String) ParseUser.getCurrentUser().get("businessID");
		
		ParseQuery<ParseObject> jobInfo = ParseQuery.getQuery("Jobsite");
		jobInfo.whereEqualTo("businessID", bizID);
		jobInfo.whereEqualTo("jobName", jobTitle);
		jobInfo.getFirstInBackground(new GetCallback<ParseObject>() {
			public void done(ParseObject object, com.parse.ParseException e) {
				if(object != null){
				  date = (String) object.get("date");
				  tTo = (Integer) object.get("timeTo");
				  tFrom = (Integer) object.get("timeFrom");
	        	}
			}
		});
		
		
		update();
   
	}
	public void update(){
		
		spinnerArray.removeAll(spinnerArray);
		spinnerArray.add("Select Employee");
		
		ParseQuery<ParseObject> employeesOnJob = ParseQuery.getQuery("Timecard");
		employeesOnJob.whereEqualTo("businessID", bizID);
		employeesOnJob.whereEqualTo("jobName", jobTitle);
		
		ParseQuery<ParseObject> query = ParseQuery.getQuery("Employee");
		query.whereEqualTo("businessID", bizID);
		
		query.whereDoesNotMatchKeyInQuery("userName", "userName", employeesOnJob);
		
		query.findInBackground(new FindCallback<ParseObject>() {
		  public void done(List<ParseObject> employeesNotOnJob, com.parse.ParseException e) {
			  if (e == null){
			  ArrayList<ParseObject> arraylist = new ArrayList<ParseObject>(employeesNotOnJob);
			  copy = employeesNotOnJob;
	        	for (ParseObject element : arraylist) {
	        		spinnerArray.add((String)element.get("firstName")+ " " +(String)element.get("lastName"));
	        	}
      		if(spinnerArray.size() < 1){
      			spinnerArray.removeAll(spinnerArray);
		        	spinnerArray.add("No Employees avaliable");
      		}
	        	
		  }
		  }
		});
			
		adapter = new ArrayAdapter<String>(
		    this, android.R.layout.simple_spinner_item, spinnerArray);

		adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
		sItems = (Spinner) findViewById(R.id.spinner1);
		sItems.setAdapter(adapter);
		
		
		
		
		lv = (ListView) findViewById(R.id.employeeListView2);

        // Instanciating an array list (you don't need to do this, 
        // you already have yours).
        your_array_list = new ArrayList<String>();
        
        employeesOnJob = ParseQuery.getQuery("Timecard");
		employeesOnJob.whereEqualTo("businessID", bizID);
		employeesOnJob.whereEqualTo("jobName", jobTitle);
		employeesOnJob.findInBackground(new FindCallback<ParseObject>() {
		  public void done(List<ParseObject> employeesNotOnJob, com.parse.ParseException e) {
			  ArrayList<ParseObject> arraylist = new ArrayList<ParseObject>(employeesNotOnJob);
	        	for (ParseObject element : arraylist) {
	        		your_array_list.add((String)element.get("firstName")+" " +(String)element.get("lastName"));
	        	}
      		if(your_array_list.size() < 1){
      			your_array_list.removeAll(your_array_list);
      			your_array_list.add("No Employees avaliable");
      		}
	        	
		  }
		});

        // This is the array adapter, it takes the context of the activity as a 
        // first parameter, the type of list view as a second parameter and your 
        // array as a third parameter.
        arrayAdapter = new ArrayAdapter<String>(
                this, 
                android.R.layout.simple_list_item_1,
                your_array_list );

        lv.setAdapter(arrayAdapter); 
        arrayAdapter.notifyDataSetChanged();
        
	}
	
	
	public void AddEmployeeClick(View view){
		String selected = sItems.getSelectedItem().toString();
		int position = adapter.getPosition(selected);
		
		if(position > 0){
			/*
			//delete from spinner
		    adapter.remove(selected);
		    adapter.notifyDataSetChanged();
		    
		    
		    //add to List
		    arrayAdapter.add(selected);
		    arrayAdapter.notifyDataSetChanged();
		    */
		    
			//add selected to jobsite
			ParseObject addEmployee = new ParseObject("Timecard");
			addEmployee.put("jobName", jobTitle);
			addEmployee.put("date", date);
			addEmployee.put("timeFrom",tFrom);
			addEmployee.put("timeTo",tTo);
			addEmployee.put("userName",copy.get(position -1).get("userName"));
			addEmployee.put("firstName",copy.get(position -1).get("firstName"));
			addEmployee.put("lastName",copy.get(position -1).get("lastName"));
			addEmployee.put("businessID",bizID);
			addEmployee.saveInBackground();

			//update veiws
			update();
		    
		    
		    //popup
		    Toast.makeText(getBaseContext(), selected + " Added to Job", Toast.LENGTH_LONG).show();
		   }
		else{
			Toast.makeText(getBaseContext(),"Cannot Add" , Toast.LENGTH_LONG).show();
		}
		
	}

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		// Inflate the menu; this adds items to the action bar if it is present.
		getMenuInflater().inflate(R.menu.edit_mployee, menu);
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

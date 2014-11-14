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

public class ManageJob extends Activity {
	private static Spinner sItems;
	private static List<String> spinnerArray;
	private static ArrayAdapter<String> adapter;
	


	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_manage_job);
		
		//fill spinner
		sItems = (Spinner) findViewById(R.id.spinner1);
		spinnerArray =  new ArrayList<String>();
		
		spinnerArray.add("Select Job");
		for(int i =0; i<20;i++){
        	String istr = Integer.toString(i);
        	spinnerArray.add("Job :"+ istr);
        }

		 adapter = new ArrayAdapter<String>(
		    this, android.R.layout.simple_spinner_item, spinnerArray);

		adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
		sItems.setAdapter(adapter);
	}

	public void DeleteJobClick(View view){
		String selected = sItems.getSelectedItem().toString();
		int position = adapter.getPosition(selected);
		
		//remove from database then ...
		
		if(position > 0){
		    adapter.remove(selected);
		    adapter.notifyDataSetChanged();
		    Toast.makeText(getBaseContext(),selected+" Deleted" , Toast.LENGTH_LONG).show();
		   }
		else{
			Toast.makeText(getBaseContext(),"Cannot Delete" , Toast.LENGTH_LONG).show();
		}
	}
	
	public void EditJobClick(View view){
		Intent intent = new Intent(this, EditJob.class);
    	startActivity(intent);
	}
    
	
	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		// Inflate the menu; this adds items to the action bar if it is present.
		getMenuInflater().inflate(R.menu.manage_job, menu);
		return true;
	}
	
	public void AddEmployeeClick(View view){
    	Intent intent = new Intent(this, AddEmployee.class);
    	startActivity(intent);
    }
	
	public void TimeCardClick(View view){
    	Intent intent = new Intent(this, TimeCards.class);
    	startActivity(intent);
    }

	public void DeleteEmployeeClick(View view){
    	Intent intent = new Intent(this, DeleteEmployee.class);
    	startActivity(intent);
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

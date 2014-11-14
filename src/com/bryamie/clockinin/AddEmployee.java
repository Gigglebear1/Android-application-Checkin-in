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

public class AddEmployee extends ActionBarActivity {
	private ListView lv;
	private static Spinner sItems;
	private static List<String> spinnerArray =  new ArrayList<String>();
	private static ArrayAdapter<String> adapter;
	private static List<String> your_array_list;
	private static ArrayAdapter<String> arrayAdapter;
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_add_employee);
		
		spinnerArray.add("Select Employee");
		
		 /*
	     * TODO: fill with people not on the job
	     */
		for(int i =0; i<20;i++){
        	String istr = Integer.toString(i);
        	spinnerArray.add("the persons: "+ istr);
        }

		adapter = new ArrayAdapter<String>(
		    this, android.R.layout.simple_spinner_item, spinnerArray);

		adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
		sItems = (Spinner) findViewById(R.id.spinner1);
		sItems.setAdapter(adapter);
		
		
		
		
		lv = (ListView) findViewById(R.id.employeeListView2);

        // Instanciating an array list (you don't need to do this, 
        // you already have yours).
        your_array_list = new ArrayList<String>();
        
        /*
         * TODO: uses this to fill the employees already on the job 
         *

        for(int i =0; i<20;i++){
        	String istr = Integer.toString(i);
	        your_array_list.add("foo"+ istr);
        }
        */

        // This is the array adapter, it takes the context of the activity as a 
        // first parameter, the type of list view as a second parameter and your 
        // array as a third parameter.
        arrayAdapter = new ArrayAdapter<String>(
                this, 
                android.R.layout.simple_list_item_1,
                your_array_list );

        lv.setAdapter(arrayAdapter); 
   
	}
	
	
	public void AddEmployeeClick(View view){
		String selected = sItems.getSelectedItem().toString();
		int position = adapter.getPosition(selected);
		
		if(position > 0){
			//delete from spinner
		    adapter.remove(selected);
		    adapter.notifyDataSetChanged();
		    /*
		     * TODO: change in database
		     */
		    
		    //add to List
		    arrayAdapter.add(selected);
		    arrayAdapter.notifyDataSetChanged();
		    /*
		     * TODO: add to data base
		     */
		    
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

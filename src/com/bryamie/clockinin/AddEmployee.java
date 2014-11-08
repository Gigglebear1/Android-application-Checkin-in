package com.bryamie.clockinin;

import java.util.ArrayList;
import java.util.List;

import android.os.Bundle;
import android.support.v7.app.ActionBarActivity;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.ArrayAdapter;
import android.widget.ListView;
import android.widget.Spinner;

public class AddEmployee extends ActionBarActivity {
	private ListView lv;
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_add_employee);
		
		List<String> spinnerArray =  new ArrayList<String>();
		for(int i =0; i<20;i++){
        	String istr = Integer.toString(i);
        	spinnerArray.add("the persons nome will go hereere         "+ istr);
        }

		ArrayAdapter<String> adapter = new ArrayAdapter<String>(
		    this, android.R.layout.simple_spinner_item, spinnerArray);

		adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
		Spinner sItems = (Spinner) findViewById(R.id.spinner1);
		sItems.setAdapter(adapter);
		
		
		
		
		lv = (ListView) findViewById(R.id.employeeListView2);

        // Instanciating an array list (you don't need to do this, 
        // you already have yours).
        List<String> your_array_list = new ArrayList<String>();
        
        for(int i =0; i<20;i++){
        	String istr = Integer.toString(i);
	        your_array_list.add("foo"+ istr);
        }

        // This is the array adapter, it takes the context of the activity as a 
        // first parameter, the type of list view as a second parameter and your 
        // array as a third parameter.
        ArrayAdapter<String> arrayAdapter = new ArrayAdapter<String>(
                this, 
                android.R.layout.simple_list_item_1,
                your_array_list );

        lv.setAdapter(arrayAdapter); 
   
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

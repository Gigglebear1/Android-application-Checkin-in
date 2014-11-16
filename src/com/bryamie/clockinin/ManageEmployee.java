package com.bryamie.clockinin;

import java.util.ArrayList;
import java.util.List;

import android.app.Activity;
import android.app.ProgressDialog;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.EditText;
import android.widget.Spinner;
import android.widget.Toast;

import com.parse.FindCallback;
import com.parse.GetCallback;
import com.parse.LogInCallback;
import com.parse.ParseObject;
import com.parse.ParseQuery;
import com.parse.ParseUser;
import com.parse.SaveCallback;
import com.parse.SignUpCallback;

public class ManageEmployee extends Activity {
	String bizID;
	private static Spinner sItems;
	private static List<String> spinnerArray;
	private static ArrayAdapter<String> adapter;
	int iCurrentSelection = 0;
	List<ParseObject> spinnerList2;
	public EditText firstName;
	public EditText lastName;
	public EditText employeeID;
	public EditText email;
	public EditText phone;
	public EditText pay;
	public EditText address;
	public EditText apt;
	public EditText zip;
	public EditText city;
	public EditText state;
	public EditText password;
	
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_manage_employee);
		
		loadActivity();
	}
	
	public void loadActivity(){
		bizID = (String) ParseUser.getCurrentUser().get("businessID");
		
		firstName = (EditText) findViewById(R.id.firstName1);
		lastName = (EditText) findViewById(R.id.lastName1);
		employeeID= (EditText) findViewById(R.id.employeeIDedit);
		email= (EditText) findViewById(R.id.EmailEdit);
		phone= (EditText) findViewById(R.id.editPhone);
		pay= (EditText) findViewById(R.id.payRateEdit);
		address= (EditText) findViewById(R.id.addressEdit);
		apt= (EditText) findViewById(R.id.AptEdit);
		zip= (EditText) findViewById(R.id.zipEdit);
		city= (EditText) findViewById(R.id.City);
		state= (EditText) findViewById(R.id.StateEdit);
		password= (EditText) findViewById(R.id.passwordEdit);
		
		
		
		//fill spinner
		sItems = (Spinner) findViewById(R.id.spinner1);
		spinnerArray =  new ArrayList<String>();
		
		spinnerArray.add("Select Employee/Add Employee");
		
		 
		updateSpinner();
			
		 adapter = new ArrayAdapter<String>(
				    this, android.R.layout.simple_spinner_item, spinnerArray);

		adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
		sItems.setAdapter(adapter);
		
		sItems.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
		    public void onItemSelected(AdapterView<?> adapterView, View view, int i, long l) {
		    	iCurrentSelection = sItems.getSelectedItemPosition();
		        if(iCurrentSelection > 0){
		        	ParseObject employee = spinnerList2.get( iCurrentSelection-1);
		        	firstName.setText((String)employee.get("firstName"));
		        	lastName.setText((String)employee.get("lastName"));
		        	employeeID.setText((String)employee.get("userName"));
		    		email.setText((String)employee.get("email"));
		    		phone.setText((String)employee.get("phoneNum"));
		    		pay.setText(String.valueOf(employee.get("payRate")));
		    		address.setText((String)employee.get("address"));
		    		apt.setText((String)employee.get("aptNum"));
		    		zip.setText((String)employee.get("zipCode"));
		    		city.setText((String)employee.get("city"));
		    		state.setText((String)employee.get("state"));
		    		password.setText((String)employee.get("Password"));
		        }
		        
		        else{
		        	firstName.setText("");
		        	lastName.setText("");
		        	employeeID.setText("");
		    		email.setText("");
		    		phone.setText("");
		    		pay.setText("");
		    		address.setText("");
		    		apt.setText("");
		    		zip.setText("");
		    		city.setText("");
		    		state.setText("");
		    		password.setText("");
		        }
		    } 

		    public void onNothingSelected(AdapterView<?> adapterView) {
		        return;
		    } 
		}); 
	}

	public void updateSpinner(){
		ParseQuery<ParseObject> jobList = ParseQuery.getQuery("Employee");
		jobList.whereEqualTo("businessID", bizID);
		jobList.findInBackground(new FindCallback<ParseObject>() {
		    public void done(List<ParseObject> spinnerList, com.parse.ParseException e) {
		        if (e == null) {
		        	ArrayList<ParseObject> arraylist = new ArrayList<ParseObject>(spinnerList);
		        	spinnerList2 = spinnerList;
		        	for (ParseObject element : spinnerList) {
		        		spinnerArray.add((String)element.get("firstName")+ " " +(String)element.get("lastName"));
		        		
	        		if(spinnerArray.size() <= 1){
	        			spinnerArray.removeAll(spinnerArray);
			        	spinnerArray.add("No Employees avaliable");
	        		}
		        }
		        	
		        } else {
		        	spinnerArray.removeAll(spinnerArray);
		        	spinnerArray.add("No Employees avaliable");		       
	        	}
		    }
		});
	}

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		// Inflate the menu; this adds items to the action bar if it is present.
		getMenuInflater().inflate(R.menu.manage_employee, menu);
		return true;
	}
	
   public void editEmployeeAddClick(View view){
	   
	 //make sure all info is filled in 
 		if(!TextUtils.isEmpty(firstName.getText().toString()) && !TextUtils.isEmpty(lastName.getText().toString()) && 
 				!TextUtils.isEmpty(password.getText().toString()) && !TextUtils.isEmpty(address.getText().toString()) 
 				&& !TextUtils.isEmpty(city.getText().toString()) && 
 				!TextUtils.isEmpty(email.getText().toString()) && !TextUtils.isEmpty(pay.getText().toString()) && 
 				!TextUtils.isEmpty(phone.getText().toString()) && !TextUtils.isEmpty(state.getText().toString()) && 
 				!TextUtils.isEmpty(zip.getText().toString()) && !TextUtils.isEmpty(employeeID.getText().toString())){
	 			
		  String pass = ParseUser.getCurrentUser().getSessionToken(); 
		  
 			
 		ParseUser AddUser = new ParseUser();
		   AddUser.setUsername(employeeID.getText().toString());
		   AddUser.setPassword(password.getText().toString());
		   AddUser.put("businessID", bizID);
		   AddUser.put("Classification", "Employee");
		   AddUser.put("email", email.getText().toString());
		  
		 
		 //validation passed no to sign up 
			final ProgressDialog dlg = new ProgressDialog(ManageEmployee.this);
			dlg.setTitle("Please wait.");
			dlg.setMessage("Siging up. Please Wait.");
			dlg.show();
		    
		    // Call the Parse signup method
		    AddUser.signUpInBackground(new SignUpCallback() {
		    	@Override  
		    	public void done(com.parse.ParseException e) {
		    		   dlg.dismiss();
		    		     if (e == null) {
		    		    	 Toast.makeText(getBaseContext(),"Employee Added" , Toast.LENGTH_LONG).show();
		    		     } else {
		    		    	 Toast.makeText(ManageEmployee.this, e.getMessage(), Toast.LENGTH_LONG).show();
		    		    	 return;
		    		     }
		    	   }
		    });
		    	ParseObject Addemployee = new ParseObject("Employee");
			   Addemployee.put("firstName", firstName.getText().toString());
			   Addemployee.put("lastName", lastName.getText().toString());
			   Addemployee.put("Password", password.getText().toString());
			   Addemployee.put("address", address.getText().toString());
			   Addemployee.put("aptNum", apt.getText().toString());
			   Addemployee.put("city", city.getText().toString());
			   Addemployee.put("email", email.getText().toString());
			   Addemployee.put("payRate", Double.parseDouble(pay.getText().toString()));
			   Addemployee.put("phoneNum", phone.getText().toString());
			   Addemployee.put("state", state.getText().toString());
			   Addemployee.put("zipCode", zip.getText().toString());
			   Addemployee.put("userName", employeeID.getText().toString());
			   Addemployee.put("businessID", bizID);
			   Addemployee.saveInBackground();
			   
			   	loadActivity();
			   	
			   	ParseUser.logOut();
			   	
			   	ParseUser.becomeInBackground(pass, new LogInCallback() {
			   	  public void done(ParseUser user, com.parse.ParseException e) {
			   	    if (user != null) {
			   	      // The current user is now set to user.
			   	    } else {
			   	      // The token could not be validated.
			   	    }
			   	  }
			   	});
 		}
 		 else{
 			   Toast.makeText(getBaseContext(),"Select User" , Toast.LENGTH_LONG).show();
 		   }
 		
	   
    }

   public void editEmployeeDeleteClick(View view){
	   iCurrentSelection = sItems.getSelectedItemPosition();
	   String pass = ParseUser.getCurrentUser().getSessionToken();
	   if(iCurrentSelection > 0){
		   ParseObject employee = spinnerList2.get( iCurrentSelection-1);
		   
		   ParseQuery<ParseObject> toDelete = ParseQuery.getQuery("Employee");
		   toDelete.whereEqualTo("userName", employee.get("userName"));
		   toDelete.findInBackground(new FindCallback<ParseObject>() {
			    public void done(List<ParseObject> spinnerList, com.parse.ParseException e) {
			        if (e == null) {
			        	try{
			        		ParseObject employee = spinnerList.get(0);
				        	ParseUser user = ParseUser.logIn(employee.get("userName").toString(), employee.get("Password").toString());
				        	user.delete();
				        	user.saveInBackground(); // This succeeds, since the user was authenticated on the device
			        	}
			        	catch(Throwable e1){
			        		Toast.makeText(getBaseContext(),e1.toString() , Toast.LENGTH_LONG).show();
			        	}
			        	for (ParseObject element : spinnerList) {
			        		element.deleteInBackground();
			        	}      
			        } 
			    }
			        
			});
		   

		   	ParseUser.logOut();
		   	
		   	ParseUser.becomeInBackground(pass, new LogInCallback() {
		   	  public void done(ParseUser user, com.parse.ParseException e) {
		   	    if (user != null) {
		   	      // The current user is now set to user.
		   	    } else {
		   	      // The token could not be validated.
		   	    }
		   	  }
		   	});
		   
		   toDelete = ParseQuery.getQuery("Timecard");
		   toDelete.whereEqualTo("userName", employee.get("userName"));
		   toDelete.findInBackground(new FindCallback<ParseObject>() {
			    public void done(List<ParseObject> spinnerList, com.parse.ParseException e) {
			        if (e == null) {
			        	for (ParseObject element : spinnerList) {
			        		element.deleteInBackground();
			        	}      
			        } 
			    }
			        
			});
		  
		   
		   String selected = sItems.getSelectedItem().toString();
		   adapter.remove(selected);
		   adapter.notifyDataSetChanged();
		   
		   
		   Toast.makeText(getBaseContext(),"Delete complete" , Toast.LENGTH_LONG).show();
		   
	   }
	   else{
		   Toast.makeText(getBaseContext(),"Select User" , Toast.LENGTH_LONG).show();
	   }
	   
   }
   	
   public void editEmployeeEditClick(View view){
	  
	   iCurrentSelection = sItems.getSelectedItemPosition();
	   if(iCurrentSelection > 0){
		   ParseObject employee = spinnerList2.get( iCurrentSelection-1);
		   String ID = employee.getObjectId().toString();
		   
		   ParseQuery<ParseObject> query = ParseQuery.getQuery("Employee");
		   query.getInBackground(ID , new GetCallback<ParseObject>() {
			   public void done(ParseObject employeeObject, com.parse.ParseException e) {
			     if (e == null) {
			       // Now let's update it with some new data. In this case, only cheatMode and score
			       // will get sent to the Parse Cloud. playerName hasn't changed.
			    	
			    	 String pass = ParseUser.getCurrentUser().getSessionToken(); 
			    	 try{
			    		 
			    	 ParseUser user = ParseUser.logIn(employeeObject.get("userName").toString(), employeeObject.get("Password").toString());
			        	user.setPassword(password.getText().toString());
			        	user.setUsername(employeeID.getText().toString());
			        	user.setEmail(email.getText().toString());
			        	user.saveInBackground(); // This succeeds, since the user was authenticated on the device
			    	 }catch(Throwable e2){
			    		 Toast.makeText(getBaseContext(),e2.toString() , Toast.LENGTH_LONG).show();
			    	 }
			    	 
			    	 employeeObject.put("firstName", firstName.getText().toString());
			    	 employeeObject.put("lastName", lastName.getText().toString());
			    	 employeeObject.put("Password", password.getText().toString());
			    	 employeeObject.put("address", address.getText().toString());
			    	 employeeObject.put("aptNum", apt.getText().toString());
			    	 employeeObject.put("city", city.getText().toString());
			    	 employeeObject.put("email", email.getText().toString());
			    	 employeeObject.put("payRate", Double.parseDouble(pay.getText().toString()));
			    	 employeeObject.put("phoneNum", phone.getText().toString());
			    	 employeeObject.put("state", state.getText().toString());
			    	 employeeObject.put("zipCode", zip.getText().toString());
			    	 employeeObject.put("userName", employeeID.getText().toString());
			    	 employeeObject.put("businessID", bizID);
			    	 employeeObject.saveInBackground(new SaveCallback() {
			    		   public void done(com.parse.ParseException e) {
			    			     if (e == null) {
			    			       loadActivity();
			    			       Toast.makeText(getBaseContext(),"User Updated" , Toast.LENGTH_LONG).show();
			    			     } else {
			    			    	 Toast.makeText(getBaseContext(),"Issue with save" , Toast.LENGTH_LONG).show();
			    			     }
			    			   }
			    			 });

					   	
					   	
					   	ParseUser.becomeInBackground(pass, new LogInCallback() {
					   	  public void done(ParseUser user, com.parse.ParseException e) {
					   	    if (user != null) {
					   	      // The current user is now set to user.
					   	    } else {
					   	      // The token could not be validated.
					   	    }
					   	  }
					   	});
			   }
			     else{
			    	 Toast.makeText(getBaseContext(),e.toString() , Toast.LENGTH_LONG).show();
				   }
			   }
			  
			 });
		  
		  
	   }
	   else{
		   Toast.makeText(getBaseContext(),"Select User" , Toast.LENGTH_LONG).show();
	   }
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

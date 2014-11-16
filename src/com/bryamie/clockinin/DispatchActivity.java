package com.bryamie.clockinin;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;

import com.parse.Parse;
import com.parse.ParseUser;

/**
 * Activity which starts an intent for either the logged in (MainActivity) or logged out
 * (SignUpOrLoginActivity) activity.
 */
public class DispatchActivity extends Activity {

  public DispatchActivity() {
  }

  @Override
  protected void onCreate(Bundle savedInstanceState) {
    super.onCreate(savedInstanceState);
    setContentView(R.layout.activity_main);
    
    Parse.initialize(this, "qAsHihTXv8ZUzjH7aOxE0aA4Tvrq70dHQPWWyXTn", "00zfQICoDXElEH4Y8gjJEPFofxL61TVGomxpkxVP");
    
    // Check if there is current user info
    if (ParseUser.getCurrentUser() != null) {
      // Start an intent for the logged in activity
    	//need to get weather they are an employee or manager to send them to correct view
    	String classification = (String) ParseUser.getCurrentUser().get("Classification");
    	
    	if(classification.equals("Manager")){
    		startActivity(new Intent(this, Manager_login.class));
    	}
    	else{
    		startActivity(new Intent(this, EmployeeLogin.class));
    	}
    	
      
    } else {
      // Start and intent for the logged out activity
      startActivity(new Intent(this, MainActivity.class));
    }
  }

}
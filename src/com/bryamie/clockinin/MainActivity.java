package com.bryamie.clockinin;

import android.app.Activity;
import android.app.ProgressDialog;
import android.content.Intent;
import android.net.ParseException;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.EditText;
import android.widget.Toast;

import com.parse.LogInCallback;
import com.parse.ParseObject;
import com.parse.ParseUser;


public class MainActivity extends Activity{
	  
	@Override
	public void onBackPressed() {
	}
	
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
    }
    
    public boolean isEmpty(EditText etText){
		if (etText.getText().toString().trim().length() > 0){
			return false;
		}else{
			return true;
		}
	}
    
    
    
    public void LoginClick(View view){
    	EditText userPassword = (EditText) findViewById(R.id.userPassword);
    	EditText userName = (EditText) findViewById(R.id.userName);
    	
    	boolean validationError = false;
		StringBuilder validationErrorMessage = new StringBuilder("Please ");
		if(isEmpty(userName)){
			validationError = true;
			validationErrorMessage.append("enter a user name");
		}
		if (isEmpty(userPassword)){
			if(validationError){
				validationErrorMessage.append(" and ");
			}
			validationError = true;
			validationErrorMessage.append("enter a password");
		}
	
		
		validationErrorMessage.append(".");
		
		//if error display it
		if(validationError){
			Toast.makeText(MainActivity.this,validationErrorMessage.toString() , Toast.LENGTH_LONG).show();
			return;
		}
		

		//validation passed no to sign up 
		final ProgressDialog dlg = new ProgressDialog(MainActivity.this);
		dlg.setTitle("Please wait.");
		dlg.setMessage("Loging in. Please Wait.");
		dlg.show();
		
		ParseUser.logInInBackground(userName.getText().toString(), userPassword.getText().toString(), new LogInCallback() {
		      @Override
		      public void done(ParseUser user, com.parse.ParseException e) {
		        dlg.dismiss();
		        if (e != null) {
		          // Show the error message
		          Toast.makeText(MainActivity.this, e.getMessage(), Toast.LENGTH_LONG).show();
		        } else {
		          // Start an intent for the dispatch activity
		          Intent intent = new Intent(MainActivity.this, DispatchActivity.class);
		          intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK | Intent.FLAG_ACTIVITY_NEW_TASK);
		          startActivity(intent);
		        }
		      }
		    });
    }
    
    public void GPSClick(View view){
    	Intent intent = new Intent(this, NewAccount.class);
    	startActivity(intent);
    }


    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.main, menu);
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

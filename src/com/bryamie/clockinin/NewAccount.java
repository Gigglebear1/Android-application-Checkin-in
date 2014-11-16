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

import com.parse.ParseObject;
import com.parse.ParseUser;
import com.parse.SignUpCallback;

public class NewAccount extends Activity {

	private EditText userNameEditText;
	private EditText passwordEditText;
	private EditText passwordAgainEditText;
	private EditText emailEditText;
	private EditText businessIDEditText;
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_new_account);
		
	}
	
	
	public boolean isEmpty(EditText etText){
		if (etText.getText().toString().trim().length() > 0){
			return false;
		}else{
			return true;
		}
	}
	
	private boolean isMatching(EditText eText1,EditText eText2){
		if(eText1.getText().toString().equals(eText2.getText().toString()))
			return true;
		else
			return false;
	}
		 
	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		// Inflate the menu; this adds items to the action bar if it is present.
		getMenuInflater().inflate(R.menu.new_account, menu);
		return true;
	}
	
	public void MakeAccountClick(View view){
		
		
		EditText userNameEditText = (EditText) findViewById(R.id.tb_userName);
		EditText passwordEditText = (EditText) findViewById(R.id.tb_password);
		EditText passwordAgainEditText = (EditText) findViewById(R.id.firstName1);
		EditText emailEditText = (EditText) findViewById(R.id.tb_email);
		EditText businessIDEditText = (EditText) findViewById(R.id.tb_businessID);
		
		
		boolean validationError = false;
		StringBuilder validationErrorMessage = new StringBuilder("Please ");
		if(isEmpty(userNameEditText)){
			validationError = true;
			validationErrorMessage.append("enter a company name");
		}
		if (isEmpty(passwordEditText)){
			if(validationError){
				validationErrorMessage.append(" and ");
			}
			validationError = true;
			validationErrorMessage.append("enter a password");
		}
		if (isEmpty(emailEditText)){
			if(validationError){
				validationErrorMessage.append(" and ");
			}
			validationError = true;
			validationErrorMessage.append("enter a email");
		}
		if (isEmpty(businessIDEditText)){
			if(validationError){
				validationErrorMessage.append(" and ");
			}
			validationError = true;
			validationErrorMessage.append("enter a email");
		}
		if(!isMatching(passwordEditText,passwordAgainEditText)){
			if(validationError){
				validationErrorMessage.append(" and ");
			}
			validationError = true;
			validationErrorMessage.append("enter the same password twice");
		}
		validationErrorMessage.append(".");
		
		//if error display it
		if(validationError){
			Toast.makeText(NewAccount.this,validationErrorMessage.toString() , Toast.LENGTH_LONG).show();
			return;
		}
		
		//validation passed no to sign up 
		final ProgressDialog dlg = new ProgressDialog(NewAccount.this);
		dlg.setTitle("Please wait.");
		dlg.setMessage("Siging up. Please Wait.");
		dlg.show();
		
		// Set up a new Parse user
	    ParseUser user = new ParseUser();
	    user.setUsername(userNameEditText.getText().toString());
	    user.setPassword(passwordEditText.getText().toString());
	    user.put("businessID", businessIDEditText.getText().toString());
	    user.put("Classification", "Manager");
	    user.put("email",emailEditText.getText().toString());
	    
	    
	    // Call the Parse signup method
	    user.signUpInBackground(new SignUpCallback() {
	    	@Override  
	    	public void done(com.parse.ParseException e) {
	    		   dlg.dismiss();
	    		     if (e == null) {
	    		    	 Intent intent = new Intent(NewAccount.this, DispatchActivity.class);
	    		          intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK | Intent.FLAG_ACTIVITY_NEW_TASK);
	    		          startActivity(intent);
	    		     } else {
	    		    	 Toast.makeText(NewAccount.this, e.getMessage(), Toast.LENGTH_LONG).show();
	    		     }
	    		   }
	    });
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

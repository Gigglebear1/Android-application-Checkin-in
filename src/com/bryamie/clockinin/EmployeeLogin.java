package com.bryamie.clockinin;

import com.google.zxing.qrcode.encoder.QRCode;

import android.app.AlertDialog;
import android.app.NotificationManager;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.ActionBarActivity;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Toast;

public class EmployeeLogin extends ActionBarActivity {
	private AlertDialog.Builder dialogBuilder;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_employee_login);
	}

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		// Inflate the menu; this adds items to the action bar if it is present.
		getMenuInflater().inflate(R.menu.employee_login, menu);
		return true;
	}
	
  public void CheckinClick(View view){
    	//put the stuff for scanner
	  Intent intent = new Intent("com.google.zxing.client.android.SCAN");
	  intent.putExtra("SCAN_MODE", "QR_CODE_MODE");
	  startActivityForResult(intent, 0);
    }
  

public void onActivityResult(int requestCode, int resultCode, Intent intent) {
	   if (requestCode == 0) {
	      if (resultCode == RESULT_OK) {
	         String contents = intent.getStringExtra("SCAN_RESULT");
	         String format = intent.getStringExtra("SCAN_RESULT_FORMAT");
	         
	         // Handle successful scan
	         if((contents.toString()).equals("checkin' in")){
		         dialogBuilder = new AlertDialog.Builder(this);
		         dialogBuilder.setTitle("Your are checked in");
		         dialogBuilder.setPositiveButton("OK", new DialogInterface.OnClickListener() {
					
					@Override
					public void onClick(DialogInterface dialog, int which) {
						// TODO Auto-generated method stub
						Toast.makeText(getApplicationContext(), "Checked in", Toast.LENGTH_SHORT);
					}
				});
		        dialogBuilder.setNegativeButton("Check in cancel", new DialogInterface.OnClickListener() {
					
					@Override
					public void onClick(DialogInterface dialog, int which) {
						// TODO Auto-generated method stub
						Toast.makeText(getApplicationContext(), "Checked in cancelled", Toast.LENGTH_SHORT);
						
					}
				});
		        
		        AlertDialog dialogPopUp = dialogBuilder.create();
		        dialogPopUp.show();
	         }
	         else{
	        	 dialogBuilder = new AlertDialog.Builder(this);
		         dialogBuilder.setTitle("This is the wrong QR for this site");
		         dialogBuilder.setPositiveButton("OK", new DialogInterface.OnClickListener() {
					
					@Override
					public void onClick(DialogInterface dialog, int which) {
						// TODO Auto-generated method stub
						Toast.makeText(getApplicationContext(), "wrong QR", Toast.LENGTH_SHORT);
					}
				});
		         
		         AlertDialog dialogPopUp = dialogBuilder.create();
			     dialogPopUp.show();
	         }
	         
	      } else if (resultCode == RESULT_CANCELED) {
	         // Handle cancel
	      }
	   }
	}
  
  public void TimeCardClick(View view){
    	Intent intent = new Intent(this, EmployeeTimeCard.class);
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

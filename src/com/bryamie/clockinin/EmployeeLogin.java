package com.bryamie.clockinin;

import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.List;
import java.util.Timer;
import java.util.TimerTask;

import android.app.AlertDialog;
import android.app.NotificationManager;
import android.app.ProgressDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.support.v4.app.NotificationCompat;
import android.support.v7.app.ActionBarActivity;
import android.text.format.Time;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.TextView;
import android.widget.Toast;

import com.parse.FindCallback;
import com.parse.ParseGeoPoint;
import com.parse.ParseObject;
import com.parse.ParseQuery;
import com.parse.ParseUser;

public class EmployeeLogin extends ActionBarActivity {
	private AlertDialog.Builder dialogBuilder;
	private GPSTracker gps;
	private ParseGeoPoint currentPoint;
	private String bizID;
	private boolean timeError;
	private boolean locationError;
	private String QRPhrase;
	private String jobName;
	private String userName;
	private int StartTime;
	private String date;
	private boolean checkin;
	private TextView status;
	// background variables
	Handler myHandler = new Handler();
	private boolean runFlag;
	private ParseObject currentJob;
	private View view;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_employee_login);
		bizID = (String) ParseUser.getCurrentUser().get("businessID");
		userName = (String) ParseUser.getCurrentUser().get("username");
		checkin = false;
		status = (TextView) findViewById(R.id.status);
		status.setText("Status: Clocked out");
		runFlag = false;
	}

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		// Inflate the menu; this adds items to the action bar if it is present.
		getMenuInflater().inflate(R.menu.employee_login, menu);
		return true;
	}

	public void employeeLogoutClick(View view) {
		ClockOutClick(view);
		ParseUser.logOut();
		Intent intent = new Intent(this, MainActivity.class);
		startActivity(intent);
	}

	public void CheckinClick(View view) {
		if(checkin){
			return;
		}
		final ProgressDialog dlg = new ProgressDialog(EmployeeLogin.this);
		dlg.setTitle("Please wait.");
		dlg.setMessage("Acquiring location and checking database. Please Wait.");
		dlg.show();
		
		timeError = false;
		locationError = false;

		gps = new GPSTracker(EmployeeLogin.this);

		// check if GPS enabled
		if (gps.canGetLocation()) {
			double latitude = gps.getLatitude();
			double longitude = gps.getLongitude();
			currentPoint = new ParseGeoPoint(latitude, longitude);
		} else {
			// can't get location
			// GPS or Network is not enabled
			// Ask user to enable GPS/network in settings
			gps.showSettingsAlert();
		}
		// get todays date
		Time today = new Time(Time.getCurrentTimezone());
		today.setToNow();
		int todayDay = today.monthDay; // Day of the month (1-31)
		int todayMonth = today.month + 1; // Month (0-11)
		int todayYear = today.year; // Year

		String todatDate = String.valueOf(todayMonth) + "/" + String.valueOf(todayDay) + "/" + String.valueOf(todayYear);
		todatDate.trim();

		SimpleDateFormat df = new SimpleDateFormat("h:mm a");
		String currTime = df.format(Calendar.getInstance().getTime());
		String[] ampm = currTime.split(" ");
		String[] parts = ampm[0].split(":");
		final int miltTime;
		if (ampm[1].equals("AM")) {
			miltTime = Integer.parseInt(parts[0]) * 100
					+ Integer.parseInt(parts[1]);
		} else {
			miltTime = Integer.parseInt(parts[0]) * 100
					+ Integer.parseInt(parts[1]) + 1200;
		}
		String userNAME = (String) ParseUser.getCurrentUser().get("username");

		ParseQuery<ParseObject> jobsForEmployee = ParseQuery.getQuery("Timecard");
		jobsForEmployee.whereEqualTo("businessID", bizID);
		jobsForEmployee.whereEqualTo("userName", userNAME);
		jobsForEmployee.whereEqualTo("date", todatDate);

		ParseQuery<ParseObject> query = ParseQuery.getQuery("Jobsite");
		query.whereEqualTo("businessID", bizID);

		query.whereMatchesKeyInQuery("jobName", "jobName", jobsForEmployee);

		query.findInBackground(new FindCallback<ParseObject>() {
			public void done(List<ParseObject> employeesJob, com.parse.ParseException e) {
				if (e == null) {
					if (employeesJob.size() == 0) {
						Toast.makeText(getApplicationContext(), "No job avaliable", Toast.LENGTH_LONG).show();
					} // end if employeeJob.size
					
					// for loop to check if job matches current location/time
					for (ParseObject element : employeesJob) {
						
						// if to check that the current time is correct for the job
						if (miltTime > element.getInt("timeFrom") && miltTime < element.getInt("timeTo")) {
							ParseGeoPoint jobGeo = element.getParseGeoPoint("geoPoint");
							Double distance = jobGeo.distanceInKilometersTo(currentPoint) / 1000;

							int geoFence = (Integer) element.get("gpsFence");
							
							// if to check if the current location is outside the job range
							if (distance > geoFence) {
								locationError = true;
							} // end if distance > geoFence
							
						} // end if miltTime > element
						
						// else time is off
						else {
							timeError = true;
						} // end else
						dlg.dismiss();
						
						// if timeError AND locationError are false (i.e. everything is ok)
						if (!timeError && !locationError) {
							
							// store job name
							currentJob = element;
							jobName = element.getString("jobName");
							
							QRPhrase = element.getString("qrPhrase");
							
							Intent intent = new Intent("com.google.zxing.client.android.SCAN");
							intent.putExtra("SCAN_MODE", "QR_CODE_MODE");
							startActivityForResult(intent, 0);
							 
						} // end if !timeError AND !locationError
						
						// else some sort of error in either time or location
						else {
							QRPhrase = element.getString("qrPhrase");
							if (timeError && locationError) {
								Toast.makeText(getApplicationContext(), "Time and Location error", Toast.LENGTH_LONG);
							} else if (locationError) {
								Toast.makeText(getApplicationContext(), "Location error", Toast.LENGTH_LONG);
							} else if (timeError) {
								Toast.makeText(getApplicationContext(), "Time error", Toast.LENGTH_LONG);
							}
						} // end else
					} // end for
				} // end if e == null
				
				// else e != null
				else {
					// error in query
				} // end else
				
			} // end public void done
		}); // end query.findInBackground
	} // end public void CheckinClick

	public void ClockOutClick(View view){
		if(!checkin){
			return;
		}
		SimpleDateFormat df = new SimpleDateFormat("h:mm a");
		String currTime = df.format(Calendar.getInstance().getTime());
		String[] ampm = currTime.split(" ");
		String[] parts= ampm[0].split(":");
		int miltTime;
		if (ampm[1].equals("AM")){
			miltTime = Integer.parseInt(parts[0])*100 + Integer.parseInt(parts[1]);
		}
		else{
			miltTime = Integer.parseInt(parts[0])*100 + Integer.parseInt(parts[1]) + 1200;
		}
		
		Time today = new Time(Time.getCurrentTimezone());
		today.setToNow();
		int todayDay = today.monthDay;           // Day of the month (1-31)
		int todayMonth = today.month+1;               // Month (0-11)
		int todayYear = today.year;              // Year 
		
		String todayDate = String.valueOf(todayMonth) +"/"+String.valueOf(todayDay) +"/"+ String.valueOf(todayYear);
		
		int stopTime = miltTime;
		
		ParseObject addEmployee = new ParseObject("Timecard");
		addEmployee.put("jobName", jobName);
		addEmployee.put("date", todayDate);
		addEmployee.put("userName", userName);
		addEmployee.put("timeFrom", StartTime);
		addEmployee.put("timeTo", stopTime);
		addEmployee.put("businessID", bizID);
		addEmployee.saveInBackground();
		
		Toast.makeText(getApplicationContext(), "Checked out!", Toast.LENGTH_SHORT).show();
		status.setText("Status: Clocked out");
		checkin = false;
		runFlag = false;
		
		//create notification for clocking out
		NotificationCompat.Builder mBuilder =
			    new NotificationCompat.Builder(this)
			    .setContentTitle("Checkin' in")
			    .setContentText("you have been clocked out");
		
		int mNotificationId = 001;
		
		NotificationManager mNotifyMgr = 
		        (NotificationManager) getSystemService(NOTIFICATION_SERVICE);
		mNotifyMgr.notify(mNotificationId, mBuilder.build());
				
	}
	
	public void onActivityResult(int requestCode, int resultCode, Intent intent) {
		if (requestCode == 0) {
			if (resultCode == RESULT_OK) {
				String contents = intent.getStringExtra("SCAN_RESULT");
				String format = intent.getStringExtra("SCAN_RESULT_FORMAT");				
				
				// prepare contents for an if check
				contents.trim();
				QRPhrase.trim();
				contents.replaceAll("\\p{C}", "?");
				QRPhrase.replaceAll("\\p{C}", "?");
						
				// Handle successful scan
				if (contents.equals(QRPhrase)) {
					dialogBuilder = new AlertDialog.Builder(this);
					dialogBuilder.setTitle("You are checked in!");
					dialogBuilder.setPositiveButton("OK", new DialogInterface.OnClickListener() {
								@Override
								public void onClick(DialogInterface dialog, int which) {
									Toast.makeText(getApplicationContext(), "Checked in!", Toast.LENGTH_SHORT).show();
									// get current military time
									SimpleDateFormat df = new SimpleDateFormat("h:mm a");
									String currTime = df.format(Calendar.getInstance().getTime());
									String[] ampm = currTime.split(" ");
									String[] parts= ampm[0].split(":");
									int miltTime;
									if (ampm[1].equals("AM")){
										miltTime = Integer.parseInt(parts[0])*100 + Integer.parseInt(parts[1]);
									}
									else{
										miltTime = Integer.parseInt(parts[0])*100 + Integer.parseInt(parts[1]) + 1200;
									}
									
									StartTime = miltTime;
									status.setText("Status: Clocked in");
									checkin = true;
									runFlag = true;
									Timer timer = new Timer();
					                timer.schedule(new TimerTask() {
						                    public void run() {					
										ParseGeoPoint point =  currentJob.getParseGeoPoint("geoPoint");
										Double distance = point.distanceInKilometersTo(currentPoint) / 1000;
										
										int geoFence = (Integer) currentJob.get("gpsFence");
										
										// if to check if the current location is outside the job range
										if (distance > geoFence) {
											ClockOutClick(view);
										} // end if distance > geoFence
										
										// get current time
										SimpleDateFormat df = new SimpleDateFormat("h:mm a");
										String currTime = df.format(Calendar.getInstance().getTime());
										String[] ampm = currTime.split(" ");
										String[] parts= ampm[0].split(":");
										int miltTime;
										if (ampm[1].equals("AM")) { 
											miltTime = Integer.parseInt(parts[0])*100 + Integer.parseInt(parts[1]);
										}
										else {
											miltTime = Integer.parseInt(parts[0])*100 + Integer.parseInt(parts[1]) + 1200;
										}
										
										// if current time is equal to or later than job end time, clock that son of a gun out
										if (miltTime >= currentJob.getInt("timeTo")) {
											ClockOutClick(view);
										} // end if
										
						                    }
						                }, 0, 5000);
								}
								});																									
			
					dialogBuilder.setNegativeButton("Cancel", new DialogInterface.OnClickListener() {

								@Override
								public void onClick(DialogInterface dialog, int which) {
									Toast.makeText(getApplicationContext(), "Check in cancelled", Toast.LENGTH_SHORT).show();
								}
							});

					AlertDialog dialogPopUp = dialogBuilder.create();
					dialogPopUp.show();
					
				} else {
					dialogBuilder = new AlertDialog.Builder(this);
					dialogBuilder.setTitle("Incorrect QR Code for this jobsite");
					dialogBuilder.setPositiveButton("OK", new DialogInterface.OnClickListener() {

								@Override
								public void onClick(DialogInterface dialog, int which) {
									// TODO Auto-generated method stub
									Toast.makeText(getApplicationContext(), "Incorrect QR", Toast.LENGTH_SHORT);
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

	public void TimeCardClick(View view) {
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

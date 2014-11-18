package com.bryamie.clockinin;

import java.text.DecimalFormat;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.List;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;

import com.parse.FindCallback;
import com.parse.GetCallback;
import com.parse.ParseObject;
import com.parse.ParseQuery;
import com.parse.ParseUser;

public class ViewingTimeSheet extends Activity {
	String userName;
	String emailString;
	String bizID;
	/** Called when the activity is first created. */
	
	// needs to be outside scope
	public static Double payRate = 100.0;

	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_employee_time_card);
		
		userName = getIntent().getExtras().getString("userName");
		emailString = getIntent().getExtras().getString("email");
		bizID = (String) ParseUser.getCurrentUser().get("businessID");
	    
	    // first query grabs the pay rate from  the employee table
	    ParseQuery<ParseObject> payStuff = ParseQuery.getQuery("Employee");
	    payStuff.whereEqualTo("userName", userName);
	    payStuff.getFirstInBackground(new GetCallback<ParseObject>() {
      		public void done(ParseObject object, com.parse.ParseException e) {
      			if(object == null){
      			}
      			else{
      				// store the employee's payrate
      				payRate = object.getDouble("payRate");
      				
      				// second query grabs necessary info from the Timecard table
      			    ParseQuery<ParseObject> timecard = ParseQuery.getQuery("Timecard");
      			    // grab all matching businessIDs
      			    timecard.whereEqualTo("businessID", bizID);
      			    // grab all matching userNames from previous
      			    timecard.whereEqualTo("userName", userName);
      			    // create the List of ParseObjects
      			    timecard.findInBackground(new FindCallback<ParseObject>() {
      			        public void done(List<ParseObject> timecardList, com.parse.ParseException e) {
      			            if (e == null && timecardList.size() > 0) {     
      			            	// enable Email button
      			            	Button emailBtn = (Button) findViewById(R.id.btn_email);
      			            	emailBtn.setEnabled(true);
      			            	
      			                // variable to display hours worked and estimated pay
      			                int hoursWorked = 0;
      			                double minutesWorked = 0.0;
      			                int startTime = 0;
      			                int finishTime = 0;
      			                String payPeriodStart = "";
      			                String payPeriodEnd = "";
      			            	
      			            	// get today's date
      			            	Calendar cal = Calendar.getInstance();
      			            	Date today = cal.getTime();            	

      			                // change today's date to the proper string format
      			            	String todayString = toMMddyyyy(today);
      			            	String[] parsedToday = todayString.split("/");
      			            	
      			            	// pay period is first half of the month
      			            	if (Integer.parseInt(parsedToday[1]) < 15){
      			            		payPeriodStart = parsedToday[0] + "/01/" + parsedToday[2];
      			            		payPeriodEnd = parsedToday[0] + "/15/" + parsedToday[2];
      			            	} // end if
      			            	// pay period is second half of the month
      			            	else{
      			            		payPeriodStart = parsedToday[0] + "/15/" + parsedToday[2];
      			            		// month is NOT December
      			            		if (Integer.parseInt(parsedToday[0]) < 12){
      			            			// increase month by one (next pay period start month)
      			            			int month = Integer.parseInt(parsedToday[0]) + 1;
      			            			// build next pay period start date for comparison farther down
      			            			payPeriodEnd = Integer.toString(month) + "/01/" + parsedToday[2];
      			            		} // end if
      			            		// month is December
      			            		else{
      			            			// integer to iterate to the next year
      			            			int nextYearInt = Integer.parseInt(parsedToday[2]) + 1;
      			            			// string to store the iterated year
      			            			String nextYearStr = Integer.toString(nextYearInt);
      			            			// build next pay period start date for comparison farther down
      			            			payPeriodEnd = "01/01/" + nextYearStr;
      			            		} // end else
      			            	} // end else
      			            	
      			            	// update time card view with the current pay period start date
      			                EditText firstLinePayPeriod = (EditText) findViewById(R.id.tml_timecard);
      			                firstLinePayPeriod.setText("Current Pay Period Began on: " + payPeriodStart);
      			                
      			                // spacing
      			                EditText spacing = (EditText) findViewById(R.id.tml_timecard);
      			                spacing.append("\n\n");
      			            	
      			            	// for loop to calculate total hours worked and estimated pay for the pay period
      			                for(ParseObject element: timecardList) {
      			                	// temporary variables
      			                	int hoursAtJob = 0;
      			                	
      			                	// this job's date
      			                	String currentJobDate = element.getString("date");              	
      			                	
      			                	// if jobs fall within the pay period (date is after the start of the pay period and before the end)
      			                	if (isDateLaterThan(currentJobDate, payPeriodStart) && !(isDateLaterThan(currentJobDate, payPeriodEnd))){
      			                		// calculate hours at this job by the difference between start and finish
      			                		startTime = element.getInt("timeFrom");
      			                		finishTime = element.getInt("timeTo");
      			                		
      			                		// if either hours element is less than 100 (first hour)
      			                		if (startTime < 100){
      			                			startTime += 40;
      			                		} // end if
      			                		if (finishTime < 100){
      			                			finishTime += 40;
      			                		} // end if
      			                		
      			                		hoursAtJob = finishTime - startTime;
      			                		// update minutesWorked
      			                		minutesWorked += hoursAtJob % 100;
      			                		// update hoursWorked with this new value	                	
      			                		hoursWorked += hoursAtJob;
      			                	} // end if date <= date                	         	
      			                }// end for               
      			                
      			                // if minutes worked > 60, need to pull out the hours
      			                if (minutesWorked > 60)
      			                {
      			                	// tally extra hours
      			                	hoursWorked += minutesWorked / 60;
      			                	// remaining minutes
      			                	minutesWorked = minutesWorked % 60;
      			                }
      			                
      			                // hours worked is returned in a military time format; divide by 100
      			                hoursWorked = hoursWorked / 100;
      			                
      			                // minutes format
      			                DecimalFormat noDecPlace = new DecimalFormat("#.#");
      			                
      			                // update time card view with the hours worked for the pay period
      			                EditText secondLineTotalHoursWorked = (EditText) findViewById(R.id.tml_timecard);
      			                secondLineTotalHoursWorked.append("Total Hours Worked: " + hoursWorked + " hours and " + noDecPlace.format(minutesWorked) + " minutes");
      			                
      			                // calculate pre-tax income
      			                double estimatedPay = hoursWorked * payRate + ((minutesWorked/60) * payRate);	  
      			                DecimalFormat twoDecPlace = new DecimalFormat("#.00");
      			                
      			                // spacing
      			                spacing.append("\n\n");
      			                
      			                // update time card view with the estimated pre-tax income
      			                EditText thirdLineEstimatedPay = (EditText) findViewById(R.id.tml_timecard);
      			                thirdLineEstimatedPay.append("Estimated (pre-tax) Income: $" + twoDecPlace.format(estimatedPay));
      			                
      			            } // end if e == null && timecardList.size > 0
      			            
      			            // exception or no items
      			            else{
      			            	EditText emptyList = (EditText) findViewById(R.id.tml_timecard);
      			            	emptyList.append("No jobs worked in the current pay period");
      			            	Button emailBtn = (Button) findViewById(R.id.btn_email);
      			            	emailBtn.setEnabled(false);
      			            }
      			        } // end public void done
      			    }); // end timecard.findInBackground
      			}
	    	} // end public void done
	    }); // end payStuff.findInBackGround	   	    
	} // end onCreate
	
	// changes standard date type into desired string format of mm/dd/yyyy
	public static String toMMddyyyy(Date day){ 
		SimpleDateFormat formatter = new SimpleDateFormat("MM/dd/yyyy"); 
		String date = formatter.format(day); 
		return date; 
	} // end toMMddyyyy
  
	// determines if one date is later than another date
	public static boolean isDateLaterThan(String firstDate, String secondDate){
		// initially assumed to be later than
		boolean isLater = true;
		// parse inputs
		String[] parsedFirst = firstDate.split("/");
		String[] parsedSecond = secondDate.split("/");
		
		// check year
		if (Integer.parseInt(parsedFirst[2]) < Integer.parseInt(parsedSecond[2])){
			isLater = false;
		}
		// check month
		else if (Integer.parseInt(parsedFirst[2]) == Integer.parseInt(parsedSecond[2]) && 
			Integer.parseInt(parsedFirst[0]) < Integer.parseInt(parsedSecond[0])){
			isLater = false;
		}
		// check day
		else if (Integer.parseInt(parsedFirst[2]) == Integer.parseInt(parsedSecond[2]) &&
			Integer.parseInt(parsedFirst[0]) == Integer.parseInt(parsedSecond[0]) &&
			Integer.parseInt(parsedFirst[1]) < Integer.parseInt(parsedSecond[1])){
			isLater = false;
		}
		// return isLater result
		return isLater;	  
	} // end isDateLaterThan
	
	// emails a detailed time card to the user
	public void emailTimeCard(View view){
		// first query grabs the email from  the employee table
	    ParseQuery<ParseObject> payStuff = ParseQuery.getQuery("Employee");
	    payStuff.whereEqualTo("userName", userName);
	    payStuff.getFirstInBackground(new GetCallback<ParseObject>() {
      		public void done(ParseObject object, com.parse.ParseException e) {
      			// unsuccessful query
      			if(object == null){} // end if 
      			// successful query
      			else{      				
      				// second query grabs necessary info from the Timecard table
      			    ParseQuery<ParseObject> timecard = ParseQuery.getQuery("Timecard");
      			    // grab all matching businessIDs
      			    timecard.whereEqualTo("businessID", bizID);
      			    // grab all matching userNames from previous
      			    timecard.whereEqualTo("userName", userName);
      			    // create the List of ParseObjects
      			    timecard.findInBackground(new FindCallback<ParseObject>() {
      			        public void done(List<ParseObject> timecardList, com.parse.ParseException e) {
      			        	// successful query
      			            if (e == null) {
      			            	// variables to store all of the information
      			            	String jobName = "";
      			            	String date = "";
      			            	int startTime = 0;
      			            	int finishTime = 0;
      			            	int hoursWorked = 0;
      			            	int minutesWorked = 0;
      			            	
      			            	// determine the current pay period
      			                String payPeriodStart = "";
      			                String payPeriodEnd = "";
      			            	
      			            	// get today's date
      			            	Calendar cal = Calendar.getInstance();
      			            	Date today = cal.getTime();            	

      			                // change today's date to the proper string format
      			            	String todayString = toMMddyyyy(today);
      			            	String[] parsedToday = todayString.split("/");
      			            	
      			            	// pay period is first half of the month
      			            	if (Integer.parseInt(parsedToday[1]) < 15){
      			            		payPeriodStart = parsedToday[0] + "/01/" + parsedToday[2];
      			            		payPeriodEnd = parsedToday[0] + "/15/" + parsedToday[2];
      			            	}
      			            	// pay period is second half of the month
      			            	else{
      			            		payPeriodStart = parsedToday[0] + "/15/" + parsedToday[2];
      			            		// month is NOT December
      			            		if (Integer.parseInt(parsedToday[0]) < 12){
      			            			// increase month by one (next pay period start month)
      			            			int month = Integer.parseInt(parsedToday[0]) + 1;
      			            			// build next pay period start date for comparison farther down
      			            			payPeriodEnd = Integer.toString(month) + "/01/" + parsedToday[2];
      			            		}
      			            		// month is December
      			            		else{
      			            			// integer to iterate to the next year
      			            			int nextYearInt = Integer.parseInt(parsedToday[2]) + 1;
      			            			// string to store the iterated year
      			            			String nextYearStr = Integer.toString(nextYearInt);
      			            			// build next pay period start date for comparison farther down
      			            			payPeriodEnd = "01/01/" + nextYearStr;
      			            		}
      			            	}
      			            	// string value for the body of the email
      			            	String emailBody = "";
      			            	
      			            	// a new intent to send the email
      			        	    Intent i = new Intent(Intent.ACTION_SEND);
      			    			i.setType("text/plain");
      			    			i.putExtra(Intent.EXTRA_EMAIL, new String[]{emailString});
      			    			i.putExtra(Intent.EXTRA_SUBJECT, "Checkin' Timecard");
      			    			
      			    			// for loop to control the placing of text in the email
      			    			for (ParseObject element : timecardList)
      			    			{
      			    				// grab the variables
      			    				jobName = element.getString("jobName");
      			    				date = element.getString("date");
      			    				startTime = element.getInt("timeFrom");
      			    				finishTime = element.getInt("timeTo");
      			    				
  			                		// if either hours element is less than 100 (first hour)
  			                		if (startTime < 100){
  			                			startTime += 40;
  			                		} // end if
  			                		if (finishTime < 100){
  			                			finishTime += 40;
  			                		} // end if
  			                		
      			    				minutesWorked = (finishTime - startTime) % 100;
      			    				hoursWorked = (finishTime - startTime) / 100;
      			    				     			    				
      			    				// if jobs fall within the pay period (date is after the start of the pay period and before the end)
      			                	if (isDateLaterThan(date, payPeriodStart) && !(isDateLaterThan(date, payPeriodEnd))){
      			                		startTime = element.getInt("timeFrom");
      			                		finishTime = element.getInt("timeTo");
      			                		
      	      			                // minutes format
      	      			                DecimalFormat noDecPlace = new DecimalFormat("#.#");
      			                		
      	      			                // place this job's information in the body of the email      	      			                
          				    			emailBody += "Job Name: " + jobName + " | \t"
	    											+ "Date: " + date + " | \t"
	    					   						+ "ClockIn: " + startTime + " | \t"
	    											+ "ClockOut: " + finishTime + " | \t"
	    											+ "Total Hours: " + hoursWorked + " hours " + noDecPlace.format(minutesWorked) + " minutes" + "\n\n";
      			                	}// end if     			    				     		    				
      			    			} // end for
      			    			// fill the body of the email
      			    			i.putExtra(Intent.EXTRA_TEXT, emailBody);
      			    			
      			    			// send the email
      			    			startActivity(Intent.createChooser(i, "Send mail..."));
      			    		
      			            } // end if
      			            // query failed
      			            else{} // end else
      			        } // end public void done
      			    }); // end timecard.findInBackground
      			    
      			} // end else
      		} // end public void done
	    }); // end payStuff.getFirstInBackground							
	} // end public static void emailTimeCard
} // end
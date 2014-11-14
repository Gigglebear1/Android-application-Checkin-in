package com.bryamie.clockinin;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.text.ParsePosition;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.EnumMap;
import java.util.List;

import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Bitmap;
import android.location.Address;
import android.location.Geocoder;
import android.net.Uri;
import android.os.Bundle;
import android.os.Environment;
import android.support.v7.app.ActionBarActivity;
import android.text.TextUtils;
import android.text.format.Time;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Toast;

import com.google.android.maps.GeoPoint;
import com.google.zxing.BarcodeFormat;
import com.google.zxing.EncodeHintType;
import com.google.zxing.WriterException;
import com.google.zxing.common.BitMatrix;
import com.google.zxing.qrcode.QRCodeWriter;

public class EditJob extends ActionBarActivity {

	private AlertDialog.Builder dialogBuilder;
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_edit_job);
	}

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		// Inflate the menu; this adds items to the action bar if it is present.
		getMenuInflater().inflate(R.menu.qr_site, menu);
		return true;
	}
	
	private Bitmap encode(String uniqueID) {
        // TODO Auto-generated method stub
         BarcodeFormat barcodeFormat = BarcodeFormat.QR_CODE;

            int width0 = 500;
            int height0 = 500;

            int colorBack = 0xFF000000;
            int colorFront = 0xFFFFFFFF;

            QRCodeWriter writer = new QRCodeWriter();
            try
            {
                EnumMap<EncodeHintType, Object> hint = new EnumMap<EncodeHintType, Object>(EncodeHintType.class);
                hint.put(EncodeHintType.CHARACTER_SET, "UTF-8");
                BitMatrix bitMatrix = writer.encode(uniqueID, barcodeFormat, width0, height0, hint);
                int width = bitMatrix.getWidth();
                int height = bitMatrix.getHeight();
                int[] pixels = new int[width * height];
                for (int y = 0; y < height; y++)
                {
                    int offset = y * width;
                    for (int x = 0; x < width; x++)
                    {

                        pixels[offset + x] = bitMatrix.get(x, y) ? colorBack : colorFront;
                    }
                }
                
                Bitmap bitmap = Bitmap.createBitmap(width, height, Bitmap.Config.ARGB_8888);
                bitmap.setPixels(pixels, 0, width, 0, 0, width, height);
                ImageView imageview = (ImageView)findViewById(R.id.qrCode);
                imageview.setImageBitmap(bitmap);
                imageview.setVisibility(View.VISIBLE);
                return bitmap;
            } catch (WriterException e) {
                // TODO Auto-generated catch block
                e.printStackTrace();
            }
			return null;
    }
	
	public void emailQr(Bitmap bitmap,String emailString,String jobName,String address,String fenceRange,String date,String timeFrom,String timeTo){
		//save bit image as a file
		String root = Environment.getExternalStorageDirectory().toString();
		File myDir = new File(root + "/req_images");
		myDir.mkdirs();
		String fname = "QrCode.jpg";
		File file = new File(myDir, fname);
		//Log.i(TAG, "" + file);
		if (file.exists())
			file.delete();
		try {
		FileOutputStream out = new FileOutputStream(file);
		bitmap.compress(Bitmap.CompressFormat.JPEG, 90, out);
		out.flush();
		out.close();
		} catch (Exception e) {
		e.printStackTrace();
		}
		
		//TODO: fill in the data 
		//this is where we email it 
		Intent i = new Intent(Intent.ACTION_SEND);
       i.setType("text/plain");
       i.putExtra(Intent.EXTRA_EMAIL  , new String[]{emailString});
       i.putExtra(Intent.EXTRA_SUBJECT, " Checkin' in QrCode");
       i.putExtra(Intent.EXTRA_TEXT   , "Job Tilte: "+jobName+"\n"
    		   							+"Date: " + date+"\n"
    		   							+"Time: "+timeFrom+" to "+timeTo+"\n"
    		   							+"Address: "+ address +"\n"
    		   							+"GPS fence Range: "+fenceRange + "\n");
       i.putExtra(Intent.EXTRA_STREAM, Uri.fromFile(file));//pngFile 

           startActivity(Intent.createChooser(i, "Send mail..."));
	}
	
	boolean isLegalDate(String s) {
	    SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd");
	    sdf.setLenient(false);
	    return sdf.parse(s, new ParsePosition(0)) != null;
	}
	 	
	public void saveJobClick(View view){
		EditText QR_string;
		Bitmap QrCode;
		EditText email;
		String emailString;
		
		//get email remove white space and non visible chars like \n
		email = (EditText)findViewById(R.id.EmailIn);
		emailString = email.getText().toString();
		emailString = emailString.replaceAll("\\s+","");
		
		
		/*
		 * TODO: need to auto fill these from database
		 */
		
		EditText addr = (EditText)findViewById(R.id.Gps);
		final String addrstr = addr.getText().toString();
		EditText range = (EditText)findViewById(R.id.Fence_range);
		String gpsrange = range.getText().toString();
		EditText jobText = (EditText)findViewById(R.id.Job_title);
		String jobTitle = jobText.getText().toString();
		EditText dateofEvent = (EditText)findViewById(R.id.dateOfJob);
		String date = dateofEvent.getText().toString();
		EditText timeText = (EditText)findViewById(R.id.time_from);
		String timeFrom = timeText.getText().toString();
		EditText timeText2 = (EditText)findViewById(R.id.time_to);
		String timeTo = timeText2.getText().toString();
		
		
		
		
		//get the string and then encode it 
		QR_string   = (EditText)findViewById(R.id.Qr_code);
		
		//make sure all info is filled in 
		if(!TextUtils.isEmpty(QR_string.getText().toString()) && !TextUtils.isEmpty(addrstr) && !TextUtils.isEmpty(gpsrange) && !TextUtils.isEmpty(jobTitle) && !TextUtils.isEmpty(date) &&
				!TextUtils.isEmpty(timeFrom) && !TextUtils.isEmpty(timeTo) && !TextUtils.isEmpty(gpsrange)){
			
			
			
			final CheckBox checkBox = (CheckBox) findViewById(R.id.checkBox1);
			
			//check date is in correct format
			Boolean DateFlag = true;
			String[] dateElement = date.split("/");
			
			try{
				if (dateElement[0].length() !=2)
					DateFlag = false;
				if (dateElement[1].length() !=2)
					DateFlag = false;
				if (dateElement[2].length() !=4)
					DateFlag = false;
			}
			catch(Throwable e){
				Toast.makeText(getBaseContext()," Invaild Date Format", Toast.LENGTH_LONG).show();
				return;
			}
			//check to see if valid date
			String inputDate = dateElement[2] + "-" + dateElement[0] + "-" + dateElement[1];
			Boolean isVaildDay = isLegalDate(inputDate);
			
			
			//make sure that date has not passed
			Boolean passedDate = false; 
			Time today = new Time(Time.getCurrentTimezone());
			today.setToNow();
			int todayDay = today.monthDay;           // Day of the month (1-31)
			int todayMonth = today.month+1;               // Month (0-11)
			int todayYear = today.year;              // Year 
			
			int inputDay = Integer.parseInt(dateElement[1]);
			int inputMonth = Integer.parseInt(dateElement[0]);
			int inputYear = Integer.parseInt(dateElement[2]);
			
			
			if (inputYear < todayYear){
				passedDate = true;
			}
			else if(inputYear == todayYear && inputMonth < todayMonth){
				passedDate = true;
			}
			else if(inputYear == todayYear && inputMonth == todayMonth && inputDay < todayDay){
				passedDate = true;
			}
			else if (inputYear == todayYear && inputMonth == todayMonth && inputDay == todayDay ){
				SimpleDateFormat df = new SimpleDateFormat("h:mm a");
				String currTime = df.format(Calendar.getInstance().getTime());
				String[] ampm = currTime.split(" ");
				String[] parts= ampm[0].split(":");
				int miltTime;
				if (ampm[1] == "AM"){
					miltTime = Integer.parseInt(parts[0])*100 + Integer.parseInt(parts[1]);
				}
				else{
					miltTime = Integer.parseInt(parts[0])*100 + Integer.parseInt(parts[1]) + 1200;
				}
				if (Integer.parseInt(timeTo) < miltTime){
					passedDate = true;
				}
			}
		
			if(!DateFlag){
				 Toast.makeText(getBaseContext()," Invaild Date Format", Toast.LENGTH_LONG).show();
				 return;
			}
			
			if (passedDate){
				 Toast.makeText(getBaseContext()," This date is passed", Toast.LENGTH_LONG).show();
				 return;
			}
			
			if(!isVaildDay){
				 Toast.makeText(getBaseContext()," Invaild Date", Toast.LENGTH_LONG).show();
				 return;
			}
			
			
			//time validate check
			
			try{
				Integer.parseInt(timeFrom);
				Integer.parseInt(timeTo);
			}
			catch(Throwable e ){
				 Toast.makeText(getBaseContext()," Invaild Time format\n milt time = ####", Toast.LENGTH_LONG).show();
				 return;
			}
			
			Boolean fromFlag = true;
			Boolean toFlag = true;
			Boolean timeWrong = false;
			if(Integer.parseInt(timeFrom) <= 0000 || Integer.parseInt(timeFrom) > 2400)
				fromFlag = false;
			if(Integer.parseInt(timeTo) <= 0000 || Integer.parseInt(timeTo) > 2400)
				toFlag = false;
			if(Integer.parseInt(timeTo) <= Integer.parseInt(timeFrom)){
				timeWrong = true;
			}
			
			if(!fromFlag){
				Toast.makeText(getBaseContext(),"From time is incorrect time format", Toast.LENGTH_LONG).show();
				return;
			}
			if(!toFlag){
				Toast.makeText(getBaseContext(),"To time is incorrect time format", Toast.LENGTH_LONG).show();
				return;
			}
			if(timeWrong){
				Toast.makeText(getBaseContext(),"From time is larger that To time", Toast.LENGTH_LONG).show();
				return;
				}
			
			
			if (checkBox.isChecked()) {
				if (!TextUtils.isEmpty(emailString)){
						QrCode = encode(QR_string.getText().toString());
						emailQr(QrCode,emailString,jobTitle,addrstr,gpsrange,date,timeFrom,timeTo);
				}
				else{
					Toast.makeText(getBaseContext(),"No Email provided", Toast.LENGTH_LONG).show();
					return;
				}
			} 
			else{
				QrCode = encode(QR_string.getText().toString());
			}
			
			/*all is validated ship to data bases
			 * put stuff here
			 * 
			 * 
			 * 
			 * 
			 */
			
			
			
		}
		else{
			//make the qr invisable
			ImageView imageview = (ImageView)findViewById(R.id.qrCode);
            imageview.setVisibility(View.INVISIBLE);

			
			
			AlertDialog.Builder dialogBuilder;
			dialogBuilder = new AlertDialog.Builder(this);
	        dialogBuilder.setTitle("Not all required fields are filled");
	        dialogBuilder.setPositiveButton("OK", new DialogInterface.OnClickListener() {
				
				@Override
				public void onClick(DialogInterface dialog, int which) {
					// TODO Auto-generated method stub
					Toast.makeText(getApplicationContext(), "Error", Toast.LENGTH_SHORT);
				}
			});
	         
	         AlertDialog dialogPopUp = dialogBuilder.create();
		     dialogPopUp.show();	
		}
    }
	
	public GeoPoint getLocationFromAddress(String strAddress) throws IOException{

		Geocoder coder = new Geocoder(this);
		List<Address> address;
		GeoPoint p1 = null;

		try {
		    address = coder.getFromLocationName(strAddress,5);
		    if (address == null) {
		        return null;
		    }
		    Address location = address.get(0);
		    location.getLatitude();
		    location.getLongitude();

		    p1 = new GeoPoint((int) (location.getLatitude() * 1E6),
		                      (int) (location.getLongitude() * 1E6));

		    return p1;
		}
		finally{
		}
	}
	
	public void ViewJobSiteButtonClick(View view) throws IOException{
		
		EditText addr = (EditText)findViewById(R.id.Gps);
		final String str = addr.getText().toString();
		try{
			GeoPoint p1 = getLocationFromAddress(str);
			
			EditText range = (EditText)findViewById(R.id.Fence_range);
			double gpsrange = Double.parseDouble(range.getText().toString());	
			
			double lat = (p1.getLatitudeE6()/ 1E6);
			double longitude = p1.getLongitudeE6()/ 1E6;
			
			Intent intent = new Intent(this, GPS2.class);
			intent.putExtra("lat", Double.toString(lat));
			intent.putExtra("longitude", Double.toString(longitude));
			intent.putExtra("radius", Double.toString(gpsrange));
	    	startActivity(intent);
		}
		catch(Throwable e ){
			dialogBuilder = new AlertDialog.Builder(this);
	         dialogBuilder.setTitle("Location not found");
	         dialogBuilder.setPositiveButton("OK", new DialogInterface.OnClickListener() {
				
				@Override
				public void onClick(DialogInterface dialog, int which) {
					// TODO Auto-generated method stub
					Toast.makeText(getApplicationContext(), "Location not found", Toast.LENGTH_SHORT);
				}
			});
			AlertDialog dialogPopUp = dialogBuilder.create();
	        dialogPopUp.show();
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

package com.bryamie.clockinin;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.OutputStream;
import java.net.URI;
import java.util.EnumMap;
import java.util.Map;
import java.util.Random;

import android.app.AlertDialog;
import android.content.ContentValues;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.Bitmap.CompressFormat;
import android.graphics.Color;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.Drawable;
import android.media.Image;
import android.net.Uri;
import android.os.Bundle;
import android.os.Environment;
import android.provider.Contacts.Intents;
import android.provider.MediaStore.Audio.Media;
import android.support.v7.app.ActionBarActivity;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Toast;

import com.google.zxing.BarcodeFormat;
import com.google.zxing.EncodeHintType;
import com.google.zxing.Writer;
import com.google.zxing.WriterException;
import com.google.zxing.aztec.AztecWriter;
import com.google.zxing.client.android.Contents;
import com.google.zxing.client.android.Intents.Encode;
import com.google.zxing.client.android.encode.EncodeActivity;
import com.google.zxing.common.BitMatrix;
import com.google.zxing.datamatrix.DataMatrixWriter;
import com.google.zxing.oned.CodaBarWriter;
import com.google.zxing.oned.Code128Writer;
import com.google.zxing.oned.Code39Writer;
import com.google.zxing.oned.EAN13Writer;
import com.google.zxing.oned.EAN8Writer;
import com.google.zxing.oned.ITFWriter;
import com.google.zxing.oned.UPCAWriter;
import com.google.zxing.pdf417.PDF417Writer;
import com.google.zxing.qrcode.QRCodeWriter;

@SuppressWarnings("deprecation")
public class QrSite extends ActionBarActivity {

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_qr_site);
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
	
	public void emailQr(Bitmap bitmap,String emailString){
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
       i.putExtra(Intent.EXTRA_TEXT   , "this will be the job name");
       i.putExtra(Intent.EXTRA_STREAM, Uri.fromFile(file));//pngFile 

           startActivity(Intent.createChooser(i, "Send mail..."));
	}
	 	
	public void addJobClick(View view){
		EditText QR_string;
		Bitmap QrCode;
		EditText email;
		String emailString;
		
		//get email remove white space and non visible chars like \n
		email = (EditText)findViewById(R.id.EmailIn);
		emailString = email.getText().toString();
		emailString = emailString.replaceAll("\\s+","");
		
		
		//get the string and then encode it 
		QR_string   = (EditText)findViewById(R.id.Qr_code);
		
		//make sure all info is filled in 
		if(!QR_string.getText().toString().equals("")){
			QrCode = encode(QR_string.getText().toString());
			final CheckBox checkBox = (CheckBox) findViewById(R.id.checkBox1);
			
			if (checkBox.isChecked()) {
				emailQr(QrCode,emailString);
			} 
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

package ptiik.mobapp.belajaractivity;

import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.ArrayList;
import java.util.List;

import org.apache.http.NameValuePair;
import org.apache.http.message.BasicNameValuePair;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import android.app.Activity;
import android.app.ProgressDialog;
import android.content.Intent;
import android.database.Cursor;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Bundle;
import android.provider.MediaStore;
import android.util.Log;
import android.view.View;
import android.view.Window;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

public class EditTugas extends Activity {

	HttpURLConnection connection = null;
	DataOutputStream outputStream = null;
	DataInputStream inputStream = null;
	
	String pathToOurFile = null;
	String urlServer = "http://10.0.2.2/androidupload/index.php";
	String lineEnd = "\r\n";
	String twoHyphens = "--";
	String boundary =  "*****";

	int bytesRead, bytesAvailable, bufferSize;
	byte[] buffer;
	int maxBufferSize = 5*1024*1024;

	TextView txtJudul;
	TextView txtDeskripsi;
	TextView tanggalMulai;
	TextView tanggalSelesai;
	EditText jawaban;
	Button btnSave;
	Button btnDelete;
	Button btnReminder;
	Button btnFile;

	String id_tugas;
	String id_matkul;

	String judul;
	String deskripsi;
	String tgl_mulai;
	String tgl_selesai;
	int isFile;
	// Progress Dialog
	private ProgressDialog pDialog;

	// JSON parser class
	JSONParser jsonParser = new JSONParser();

	// single product url
	private static final String url_tugas_detail = "http://farizijan.com/mobapp/get_tugas_details.php";

	// url to update product
	private static final String url_submit_tugas = "http://farizijan.com/mobapp/update_tugas.php";
	
	// url to delete product
	//private static final String url_delete_product = "http://10.0.2.2/androWS/android_connect/delete_product.php";

	// JSON Node names
	private static final String TAG_SUCCESS = "success";
	private static final String TAG_TUGAS = "tugas";
	private static final String TAG_IDTUGAS = "id_tugas";

	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		this.requestWindowFeature(Window.FEATURE_NO_TITLE);
		setContentView(R.layout.edit_tugas);

		// save button
		btnSave = (Button) findViewById(R.id.submit);
		txtJudul = (TextView) findViewById(R.id.judul);
		txtDeskripsi = (TextView) findViewById(R.id.deskripsi);
		tanggalMulai= (TextView) findViewById(R.id.tanggalmulai);
		tanggalSelesai= (TextView) findViewById(R.id.tanggalselesai);
		jawaban= (EditText) findViewById(R.id.jawaban);
		btnReminder=(Button)findViewById(R.id.setReminder);
		btnFile=(Button)findViewById(R.id.choosefile);

		// getting product details from intent
		Intent i = getIntent();
		
		// getting product id (pid) from intent
		id_tugas = i.getStringExtra(TAG_IDTUGAS);
		id_matkul = i.getStringExtra("id_matkul");
		Log.d("Tugas Details","ID_TUGAS:"+id_tugas+", ID_MATKUL: "+id_matkul);
		// Getting complete product details in background thread
		new GetTugasDetails().execute();

		// save button click event
		btnSave.setOnClickListener(new View.OnClickListener() {

			@Override
			public void onClick(View arg0) {
				// starting background task to update product
				new SubmitTugas().execute();
			}
		});
		btnReminder.setOnClickListener(new View.OnClickListener() {

			@Override
			public void onClick(View arg0) {
				// starting background task to update product
				//new SubmitTugas().execute();
				Intent reminder = new Intent(getApplicationContext(), SetAlarm.class );
				//mengirim nilai ke setAlarm.java
				reminder.putExtra("idMatkul",id_matkul);
				reminder.putExtra("judulMatkul",judul);
				reminder.putExtra("deskripsiMatkul",deskripsi);
				startActivity(reminder);
			}
		});
		btnFile.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View arg0) {
				Intent intent = new Intent();
				intent.setType("image/*");
				intent.setAction(Intent.ACTION_GET_CONTENT);
				startActivityForResult(
						Intent.createChooser(intent, "Select Picture"),
						1);
			}
		});

	}

	
	public String getPath(Uri uri) {
		String siPath;
		// 1:MEDIA GALLERY --- query from MediaStore.Images.Media.DATA
		String[] projection = { MediaStore.Images.Media.DATA };
		Cursor cursor = managedQuery(uri, projection, null, null, null);
		if (cursor != null) {
			int column_index = cursor
					.getColumnIndexOrThrow(MediaStore.Images.Media.DATA);
			cursor.moveToFirst();
			siPath = cursor.getString(column_index);
		} else {
			siPath = null;
		}

		if (siPath == null) {
			// 2:OI FILE Manager --- call method: uri.getPath()
			siPath = uri.getPath();
		}
		return siPath;
	}
	
	@Override
	protected void onActivityResult(int requestCode, int resultCode, Intent data) {
		if (resultCode == RESULT_OK) {
			if (requestCode == 1) {
				Uri selectedImageUri = data.getData();
				pathToOurFile = getPath(selectedImageUri);
				
				TextView tResponse = (TextView) findViewById(R.id.filelocation);
		    	tResponse.append(pathToOurFile);
			} 
		}
	}
	
	
	/**
	 * Background Async Task to Get complete product details
	 * */
	class GetTugasDetails extends AsyncTask<String, String, String> {

		/**
		 * Before starting background thread Show Progress Dialog
		 * */
		@Override
		protected void onPreExecute() {
			super.onPreExecute();
			pDialog = new ProgressDialog(EditTugas.this);
			pDialog.setMessage("Loading tugas details. Please wait...");
			pDialog.setIndeterminate(false);
			pDialog.setCancelable(true);
			pDialog.show();
		}

		/**
		 * Getting product details in background thread
		 * */
		protected String doInBackground(String... params) {

			// updating UI from Background Thread
			//runOnUiThread(new Runnable() {
				//public void run() {
					// Check for success tag
					int success;
					Log.d("Tugas Details2","ID_TUGAS:"+id_tugas+", ID_MATKUL: "+id_matkul);
					// Building Parameters
					List<NameValuePair> params2 = new ArrayList<NameValuePair>();
					params2.add(new BasicNameValuePair("id_tugas", id_tugas));
					params2.add(new BasicNameValuePair("id_matkul", id_matkul));

					// getting product details by making HTTP request
					// Note that product details url will use GET request
					JSONObject json = jsonParser.makeHttpRequest(
							url_tugas_detail, "GET", params2);

					// check your log for json response
					Log.d("Tugas Details", json.toString());
					
					try {
						
						
						// json success tag
						success = json.getInt(TAG_SUCCESS);
						if (success == 1) {
							// successfully received product details
							JSONArray tugasObj = json
									.getJSONArray(TAG_TUGAS); // JSON Array
							
							// get first product object from JSON Array
							JSONObject tugas= tugasObj.getJSONObject(0);

							
							// Edit Text
							judul=tugas.getString("judul");
							deskripsi=tugas.getString("deskripsi");
							tgl_mulai=tugas.getString("tgl_mulai");
							tgl_selesai=tugas.getString("tgl_selesai");
							isFile=tugas.getInt("is_file");
						}else{

						}
					} catch (JSONException e) {
						e.printStackTrace();
					}

			return null;
		}


		/**
		 * After completing background task Dismiss the progress dialog
		 * **/
		protected void onPostExecute(String file_url) {
			
			
			TextView fileLoc= (TextView) findViewById(R.id.filelocation);
			
			if(isFile==0)jawaban.setVisibility(1);
			btnSave.setVisibility(1);
			btnReminder.setVisibility(1);
			if(isFile==1){
				btnFile.setVisibility(1);
				fileLoc.setVisibility(1);
			}
			txtJudul.setText("("+id_matkul+")" +judul);
			txtDeskripsi.setText(deskripsi);
			tanggalMulai.setText("Tanggal mulai: "+tgl_mulai);
			tanggalSelesai.setText("Tanggal selesai: "+tgl_selesai);
			// dismiss the dialog once got all details
			pDialog.dismiss();
		}
	}

	/**
	 * Background Async Task to  Save product Details
	 * */
	class SubmitTugas extends AsyncTask<String, String, String> {

		/**
		 * Before starting background thread Show Progress Dialog
		 * */
		int success;
		
		@Override
		protected void onPreExecute() {
			super.onPreExecute();
			pDialog = new ProgressDialog(EditTugas.this);
			pDialog.setMessage("Submit tugas ...");
			pDialog.setIndeterminate(false);
			pDialog.setCancelable(false);
			pDialog.show();
		}

		/**
		 * Saving product
		 * */
		protected String doInBackground(String... args) {

			// getting updated data from EditTexts
			String jwb= jawaban.getText().toString();
			
			if(pathToOurFile!=null){
				
				//UPLOAD FILE
	
		    	try
		    	{
		    	FileInputStream fileInputStream = new FileInputStream(new File(pathToOurFile) );
	
		    	URL url = new URL(url_submit_tugas);
		    	connection = (HttpURLConnection) url.openConnection();

		    	// Allow Inputs & Outputs
		    	connection.setDoInput(true);
		    	connection.setDoOutput(true);
		    	connection.setUseCaches(false);

		    	// Enable POST method
		    	connection.setRequestMethod("POST");

		    	connection.setRequestProperty("Connection", "Keep-Alive");
		    	connection.setRequestProperty("Content-Type", "multipart/form-data;boundary="+boundary);
		    	//connection.setRequestProperty("x-username", "farizijan");
		    	outputStream = new DataOutputStream( connection.getOutputStream() );
		    	
		    	outputStream.writeBytes(twoHyphens + boundary + lineEnd);
		    	outputStream.writeBytes("Content-Disposition: form-data; name=\"username\""+ lineEnd);
		    	outputStream.writeBytes(lineEnd);
		    	outputStream.writeBytes(Login.username);
		    	outputStream.writeBytes(lineEnd);
		    	outputStream.flush();
		    	
		    	outputStream.writeBytes(twoHyphens + boundary + lineEnd);
		    	outputStream.writeBytes("Content-Disposition: form-data; name=\"id_tugas\""+ lineEnd);
		    	outputStream.writeBytes(lineEnd);
		    	outputStream.writeBytes(id_tugas);
		    	outputStream.writeBytes(lineEnd);
		    	outputStream.flush();
		    	
		    	outputStream.writeBytes(twoHyphens + boundary + lineEnd);
		    	outputStream.writeBytes("Content-Disposition: form-data; name=\"id_matkul\""+ lineEnd);
		    	outputStream.writeBytes(lineEnd);
		    	outputStream.writeBytes(id_matkul);
		    	outputStream.writeBytes(lineEnd);
		    	outputStream.flush();
		    	outputStream.writeBytes(twoHyphens + boundary + lineEnd);
		    	outputStream.writeBytes("Content-Disposition: form-data; name=\"uploadedfile\";filename=\"" + pathToOurFile +"\"" + lineEnd);
		    	outputStream.writeBytes(lineEnd);
		    	
		    	
		    	
		    	
		    	bytesAvailable = fileInputStream.available();
		    	bufferSize = Math.min(bytesAvailable, maxBufferSize);
		    	buffer = new byte[bufferSize];

		    	// Read file
		    	bytesRead = fileInputStream.read(buffer, 0, bufferSize);

		    	while (bytesRead > 0)
		    	{
		    	outputStream.write(buffer, 0, bufferSize);
		    	bytesAvailable = fileInputStream.available();
		    	bufferSize = Math.min(bytesAvailable, maxBufferSize);
		    	bytesRead = fileInputStream.read(buffer, 0, bufferSize);
		    	}

		    	outputStream.writeBytes(lineEnd);
		    	outputStream.writeBytes(twoHyphens + boundary + twoHyphens + lineEnd);

		    	// Responses from the server (code and message)
		    	int serverResponseCode = connection.getResponseCode();
		    	String serverResponseMessage = connection.getResponseMessage();
		    	Log.d("response", serverResponseMessage);
		    	if(serverResponseMessage.equals("OK")){
		    			success=1;
		    	}
		    	fileInputStream.close();
		    	outputStream.flush();
		    	outputStream.close();
		    	}
		    	catch (Exception ex)
		    	{
		    	//Exception handling
		    		ex.printStackTrace();
		    	}
				//END OF UPLOAD FILE
			}
			
			else{
	
				// Building Parameters
				List<NameValuePair> params = new ArrayList<NameValuePair>();
				params.add(new BasicNameValuePair("id_tugas", id_tugas));
				params.add(new BasicNameValuePair("id_matkul", id_matkul));
				params.add(new BasicNameValuePair("jawaban", jwb));
				params.add(new BasicNameValuePair("username", Login.username));
	
				// sending modified data through http request
				// Notice that update product url accepts POST method
				JSONObject json1 = jsonParser.makeHttpRequest(url_submit_tugas,
						"GET", params);
				Log.d("Submit Tugas"+id_matkul,"ID_TUGAS:"+id_tugas+", ID_MATKUL: "+id_matkul+", Jawaban: "+jwb+", username: "+Login.username);
				// check json success tag
				try {
					 success = json1.getInt(TAG_SUCCESS);
					
					if (success == 1) {
						// successfully updated
						//Intent i = getIntent();
						//Intent i = new Intent(getApplicationContext(),tampilTugas.class);
						//finish();
						//startActivity(i);
						// send result code 100 to notify about product update
						//setResult(100, i);
						
					} else {
						// failed to update product
					}
					Log.d("Submit Response", json1.toString());
				} catch (JSONException e) {
					e.printStackTrace();
				}
			}
			return null;
		}


		/**
		 * After completing background task Dismiss the progress dialog
		 * **/
		protected void onPostExecute(String file_url) {
			// dismiss the dialog once product uupdated
			pDialog.dismiss();
			if(success==1){
				Intent i = new Intent(getApplicationContext(),tampilTugas.class);
				finish();
				startActivity(i);
				Toast.makeText(getApplicationContext(), "Submit tugas berhasil!", Toast.LENGTH_LONG).show();
			}
			else{
				Toast.makeText(getApplicationContext(), "Submit tugas gagal!", Toast.LENGTH_LONG).show();
			}
		}
	}
	
	@Override
	public void onBackPressed(){
		Intent i = new Intent(getApplicationContext(),tampilTugas.class);
		finish();
		startActivity(i);
	}
}

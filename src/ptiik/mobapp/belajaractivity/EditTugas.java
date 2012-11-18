package ptiik.mobapp.belajaractivity;

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
import android.os.AsyncTask;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;

public class EditTugas extends Activity {

	TextView txtJudul;
	TextView txtDeskripsi;
	TextView tanggalMulai;
	TextView jawaban;
	TextView tanggalSelesai;
	Button btnSave;
	Button btnDelete;

	String id_tugas;
	String id_matkul;

	// Progress Dialog
	private ProgressDialog pDialog;

	// JSON parser class
	JSONParser jsonParser = new JSONParser();

	// single product url
	private static final String url_tugas_detail = "http://10.0.2.2/mec/get_tugas_details.php";

	// url to update product
	private static final String url_update_product = "http://10.0.2.2/androWS/android_connect/update_product.php";
	
	// url to delete product
	//private static final String url_delete_product = "http://10.0.2.2/androWS/android_connect/delete_product.php";

	// JSON Node names
	private static final String TAG_SUCCESS = "success";
	private static final String TAG_TUGAS = "tugas";
	private static final String TAG_IDTUGAS = "id_tugas";

	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.edit_tugas);

		// save button
		btnSave = (Button) findViewById(R.id.submit);
		

		// getting product details from intent
		Intent i = getIntent();
		
		// getting product id (pid) from intent
		id_tugas = i.getStringExtra(TAG_IDTUGAS);
		id_matkul = i.getStringExtra("id_matkul");

		// Getting complete product details in background thread
		new GetTugasDetails().execute();

		// save button click event
		btnSave.setOnClickListener(new View.OnClickListener() {

			@Override
			public void onClick(View arg0) {
				// starting background task to update product
				//new SubmitTugas().execute();
			}
		});

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
			runOnUiThread(new Runnable() {
				public void run() {
					// Check for success tag
					int success;
					try {
						// Building Parameters
						List<NameValuePair> params = new ArrayList<NameValuePair>();
						params.add(new BasicNameValuePair("id_tugas", id_tugas));
						params.add(new BasicNameValuePair("id_matkul", id_matkul));

						// getting product details by making HTTP request
						// Note that product details url will use GET request
						JSONObject json = jsonParser.makeHttpRequest(
								url_tugas_detail, "GET", params);

						// check your log for json response
						Log.d("Single Product Details", json.toString());
						
						// json success tag
						success = json.getInt(TAG_SUCCESS);
						if (success == 1) {
							// successfully received product details
							JSONArray productObj = json
									.getJSONArray(TAG_TUGAS); // JSON Array
							
							// get first product object from JSON Array
							JSONObject product = productObj.getJSONObject(0);

							// product with this pid found
							// Edit Text
							txtJudul = (TextView) findViewById(R.id.judul);
							txtDeskripsi = (TextView) findViewById(R.id.deskripsi);
							tanggalMulai= (TextView) findViewById(R.id.tanggalmulai);
							tanggalSelesai= (TextView) findViewById(R.id.tanggalselesai);
							jawaban= (EditText) findViewById(R.id.jawaban);

							// display product data in EditText
							txtJudul.setText(product.getString("judul"));
							txtDeskripsi.setText(product.getString("deskripsi"));
							tanggalMulai.setText("Tanggal mulai: "+product.getString("tgl_mulai"));
							tanggalSelesai.setText("Tanggal selesai: "+product.getString("tgl_selesai"));

						}else{
							// product with pid not found
						}
					} catch (JSONException e) {
						e.printStackTrace();
					}
				}
			});

			return null;
		}


		/**
		 * After completing background task Dismiss the progress dialog
		 * **/
		protected void onPostExecute(String file_url) {
			// dismiss the dialog once got all details
			pDialog.dismiss();
		}
	}
/*
	*//**
	 * Background Async Task to  Save product Details
	 * *//*
	class SubmitTugas extends AsyncTask<String, String, String> {

		*//**
		 * Before starting background thread Show Progress Dialog
		 * *//*
		@Override
		protected void onPreExecute() {
			super.onPreExecute();
			pDialog = new ProgressDialog(EditTugas.this);
			pDialog.setMessage("Saving tugas ...");
			pDialog.setIndeterminate(false);
			pDialog.setCancelable(true);
			pDialog.show();
		}

		*//**
		 * Saving product
		 * *//*
		protected String doInBackground(String... args) {

			// getting updated data from EditTexts
			String jawaban = jawaban.getText().toString();
			

			// Building Parameters
			List<NameValuePair> params = new ArrayList<NameValuePair>();
			params.add(new BasicNameValuePair(TAG_PID, pid));
			params.add(new BasicNameValuePair(TAG_NAME, name));
			params.add(new BasicNameValuePair(TAG_PRICE, price));
			params.add(new BasicNameValuePair(TAG_DESCRIPTION, description));

			// sending modified data through http request
			// Notice that update product url accepts POST method
			JSONObject json = jsonParser.makeHttpRequest(url_update_product,
					"POST", params);

			// check json success tag
			try {
				int success = json.getInt(TAG_SUCCESS);
				
				if (success == 1) {
					// successfully updated
					Intent i = getIntent();
					// send result code 100 to notify about product update
					setResult(100, i);
					finish();
				} else {
					// failed to update product
				}
			} catch (JSONException e) {
				e.printStackTrace();
			}

			return null;
		}


		*//**
		 * After completing background task Dismiss the progress dialog
		 * **//*
		protected void onPostExecute(String file_url) {
			// dismiss the dialog once product uupdated
			pDialog.dismiss();
		}
	}

	*//*****************************************************************
	 * Background Async Task to Delete Product
	 * *//*
	class DeleteProduct extends AsyncTask<String, String, String> {

		*//**
		 * Before starting background thread Show Progress Dialog
		 * *//*
		@Override
		protected void onPreExecute() {
			super.onPreExecute();
			pDialog = new ProgressDialog(EditTugas.this);
			pDialog.setMessage("Deleting Product...");
			pDialog.setIndeterminate(false);
			pDialog.setCancelable(true);
			pDialog.show();
		}

		*//**
		 * Deleting product
		 * *//*
		protected String doInBackground(String... args) {

			// Check for success tag
			int success;
			try {
				// Building Parameters
				List<NameValuePair> params = new ArrayList<NameValuePair>();
				params.add(new BasicNameValuePair("pid", pid));

				// getting product details by making HTTP request
				JSONObject json = jsonParser.makeHttpRequest(
						url_delete_product, "POST", params);

				// check your log for json response
				Log.d("Delete Product", json.toString());
				
				// json success tag
				success = json.getInt(TAG_SUCCESS);
				if (success == 1) {
					// product successfully deleted
					// notify previous activity by sending code 100
					Intent i = getIntent();
					// send result code 100 to notify about product deletion
					setResult(100, i);
					finish();
				}
			} catch (JSONException e) {
				e.printStackTrace();
			}

			return null;
		}

		*//**
		 * After completing background task Dismiss the progress dialog
		 * **//*
		protected void onPostExecute(String file_url) {
			// dismiss the dialog once product deleted
			pDialog.dismiss();

		}

	}*/
}
package ptiik.mobapp.belajaractivity;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import org.apache.http.NameValuePair;
import org.apache.http.message.BasicNameValuePair;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import ptiik.mobapp.belajaractivity.R;

import android.app.ListActivity;
import android.app.ProgressDialog;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.AsyncTask;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.ListAdapter;
import android.widget.ListView;
import android.widget.SimpleAdapter;
import android.widget.TextView;
import android.widget.Toast;

public class tampilTugas extends ListActivity {

	// Progress Dialog
	private ProgressDialog pDialog;
	int success=0;
	String username;
	// Creating JSON Parser object
	JSONParser jParser = new JSONParser();

	ArrayList<HashMap<String, String>> ListTugas;

	// url to get all products list
	private static String url_all_products = "http://farizijan.com/mobapp/get_all_tugas.php";

	// JSON Node names
	private static final String TAG_SUCCESS = "success";
	private static final String TAG_TUGAS = "tugas";
	private static final String TAG_TUGASID = "id_tugas";
	private static final String TAG_JUDUL = "judul";
	private static final String TAG_ID = "tugas_id";
	private static final String TAG_MATKUL = "matkul_id";

	// products JSONArray
	JSONArray tugas = null;

	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.tampil_tugas);

		// Hashmap for ListView
		ListTugas = new ArrayList<HashMap<String, String>>();

		// Loading products in Background Thread
		new LoadTugas().execute();

		// Get listview
		ListView lv = getListView();

		// on seleting single product
		// launching Edit Product Screen
		lv.setOnItemClickListener(new OnItemClickListener() {

			@Override
			public void onItemClick(AdapterView<?> parent, View view,
					int position, long id) {
				// getting values from selected ListItem
				String idTugas = ((TextView) view.findViewById(R.id.tugas_id)).getText()
						.toString();
				String idMatkul = ((TextView) view.findViewById(R.id.matkul_id)).getText()
						.toString();

				// Starting new intent
				Intent in = new Intent(getApplicationContext(),
						EditTugas.class);
				// sending pid to next activity
				in.putExtra(TAG_TUGASID, idTugas);
				in.putExtra("id_matkul", idMatkul);
				Log.d("Tugas Details", idTugas+", "+ idMatkul);
				// starting new activity and expecting some response back
				startActivityForResult(in, 100);
				finish();
			}
		});

	}

	// Response from Edit Product Activity
	@Override
	protected void onActivityResult(int requestCode, int resultCode, Intent data) {
		super.onActivityResult(requestCode, resultCode, data);
		// if result code 100
		if (resultCode == 100) {
			// if result code 100 is received 
			// means user edited/deleted product
			// reload this screen again
			Intent intent = getIntent();
			finish();
			startActivity(intent);
		}

	}

	/**
	 * Background Async Task to Load all product by making HTTP Request
	 * */
	class LoadTugas extends AsyncTask<String, String, String> {

		/**
		 * Before starting background thread Show Progress Dialog
		 * */
		@Override
		protected void onPreExecute() {
			super.onPreExecute();
			pDialog = new ProgressDialog(tampilTugas.this);
			pDialog.setMessage("Loading tugas. Please wait...");
			pDialog.setIndeterminate(false);
			pDialog.setCancelable(true);
			pDialog.show();
		}

		/**
		 * mengambil semua tugas dari server url
		 * */
		protected String doInBackground(String... args) {
			// Building Parameters
			SharedPreferences prefs=getSharedPreferences("mec-data", 0);
			List<NameValuePair> params = new ArrayList<NameValuePair>();
			username =prefs.getString("username", null);
			
			params.add(new BasicNameValuePair("username", username));
			// getting JSON string from URL
			JSONObject json = jParser.makeHttpRequest(url_all_products, "GET", params);
			
			if(json!=null){
				// Check your log cat for JSON reponse
				Log.d("All Products: ", json.toString());
	
				try {
					// Checking for SUCCESS TAG
					success = json.getInt(TAG_SUCCESS);
	
					if (success == 1) {
						// products found
						// Getting Array of Products
						tugas = json.getJSONArray(TAG_TUGAS);
	
						// looping through All Products
						for (int i = 0; i < tugas.length(); i++) {
							JSONObject c = tugas.getJSONObject(i);
	
							// Storing each json item in variable
							String id = c.getString(TAG_TUGASID);
							String id_matkul = c.getString("id_matkul");
							String judul = c.getString("id_matkul")+" - "+c.getString(TAG_JUDUL);
							Log.d("Tugas Details", id+", "+id_matkul);
							// creating new HashMap
							HashMap<String, String> map = new HashMap<String, String>();
	
							// adding each child node to HashMap key => value
							map.put(TAG_ID, id);
							map.put(TAG_MATKUL, id_matkul);
							map.put(TAG_JUDUL, judul);
	
							// adding HashList to ArrayList
							ListTugas.add(map);
						}
					} else {
						
						HashMap<String, String> map = new HashMap<String, String>();
						// adding each child node to HashMap key => value
						map.put(TAG_TUGASID, "empty");
						map.put(TAG_JUDUL, "Tidak Ada Tugas");
	
						// adding HashList to ArrayList
						ListTugas.add(map);
					}
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
			// dismiss the dialog after getting all products
			pDialog.dismiss();
			if(username==null)finish();
			if (success == 1) {
				// updating UI from Background Thread
				runOnUiThread(new Runnable() {
					public void run() {
						/**
						 * Updating parsed JSON data into ListView
						 * */
						ListAdapter adapter = new SimpleAdapter(
								tampilTugas.this, ListTugas,
								R.layout.list_item, new String[] { TAG_ID,
										TAG_JUDUL,TAG_MATKUL},
								new int[] { R.id.tugas_id, R.id.judul, R.id.matkul_id });
						// updating listview
						setListAdapter(adapter);
					}
				});
			}
			else {
				Toast.makeText(getApplicationContext(), "Cek koneksi internet Anda!", Toast.LENGTH_LONG).show();
				finish();
			}
		}

	}
}
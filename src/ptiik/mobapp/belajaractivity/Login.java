/*
 * Fariz Izzan Agusta
 * 105060805111002
 */
package ptiik.mobapp.belajaractivity;

import java.util.ArrayList;
import java.util.List;

import org.apache.http.NameValuePair;
import org.apache.http.message.BasicNameValuePair;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import ptiik.mobapp.belajaractivity.R;
import android.os.AsyncTask;
import android.os.Bundle;
import android.app.Activity;
import android.app.ProgressDialog;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.SharedPreferences.Editor;
import android.text.InputFilter.LengthFilter;
import android.util.Log;
import android.view.Menu;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.EditText;
import android.widget.Toast;

public class Login extends Activity implements OnClickListener {

	private static final String url_login = "http://farizijan.com/mobapp/get_user.php";
	JSONParser jsonParser = new JSONParser();
	SharedPreferences login;
	public static String username;
    public static String password;
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);
        login=getSharedPreferences("mec-data", 0);
        View loginButton=findViewById(R.id.button1);
        loginButton.setOnClickListener(this);
        
        username=login.getString("username", null);
        password=login.getString("password", null);
        Log.d("Shr-Login", "username: "+username);
        Log.d("Shr-Login", "password: "+password);
        
        if(username!=null){
        	Log.d("Shr-Login", "is not null");
        	Intent i = new Intent(getApplicationContext(), MainMenu.class);
			startActivity(i);
			finish();
        }
        Log.d("Shr-Login", "is null");
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.activity_login, menu);
        return true;
    }
    
    
	@Override
	public void onClick(View v) {
		// TODO Auto-generated method stub
		EditText uname=(EditText)findViewById(R.id.editText1);
		String vUname=uname.getText().toString();
		EditText pass=(EditText)findViewById(R.id.editText2);
		String vpass=pass.getText().toString();
		
		switch (v.getId()) {
		case R.id.button1:
				username=vUname;
				password=vpass;
				new LoginProcess().execute();
    	}
	}
	
	class LoginProcess extends AsyncTask<String, String, String> {

		ProgressDialog pDialog;
		int success=0;
		/**
		 * Before starting background thread Show Progress Dialog
		 * */
		@Override
		protected void onPreExecute() {
			super.onPreExecute();
			//menampilkan proses
			 pDialog = new ProgressDialog(Login.this);
			pDialog.setMessage("Login...");
			pDialog.setIndeterminate(false);
			pDialog.setCancelable(false);
			pDialog.show();
		}

		/**
		 * Getting product details in background thread
		 * */
		protected String doInBackground(String... params) {

					
					Log.d("Login","username:"+username+", password: "+password);
					// Building Parameters
					List<NameValuePair> params2 = new ArrayList<NameValuePair>();
					params2.add(new BasicNameValuePair("username", username));
					params2.add(new BasicNameValuePair("password", password));

					// getting product details by making HTTP request
					// Note that product details url will use GET request
					JSONObject json = jsonParser.makeHttpRequest(
							url_login, "GET", params2);

					// check your log for json response
					
			if(json!=null){//jika koneksi internet ada atau tidak ada masalah koneksi ke server		
				Log.d("Login Details", json.toString());
					try {
						// json success tag
						success = json.getInt("success");
						
					} catch (JSONException e) {
						e.printStackTrace();
					}
			}//end if
			else success=2;
			
			return null;
		}


		/**
		 * After completing background task Dismiss the progress dialog
		 * **/
		protected void onPostExecute(String file_url) {
			
			// dismiss the dialog once got all details
			pDialog.dismiss();
			if (success == 1) {
				Editor tmpEdit=login.edit();
				tmpEdit.putString("username", username);
				tmpEdit.putString("password", password);
				tmpEdit.commit();
				
				Log.d("Shr-Login 1", "username: "+username);
		        Log.d("Shr-Login 1", "password: "+password);
		        
				Intent i = new Intent(getApplicationContext(), MainMenu.class);
				startActivity(i);
				finish();
			}else if (success == 2) {
				Toast.makeText(getApplicationContext(), "Cek koneksi internet Anda!", Toast.LENGTH_LONG).show();
			}
			else{
				Toast.makeText(getApplicationContext(), "Username atau password salah!", Toast.LENGTH_LONG).show();
			}
		}
	}

}

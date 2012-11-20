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
	
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);
        
        View loginButton=findViewById(R.id.button1);
        loginButton.setOnClickListener(this);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.activity_login, menu);
        return true;
    }
    public static String username;
    public static String password;
	@Override
	public void onClick(View v) {
		// TODO Auto-generated method stub
		EditText uname=(EditText)findViewById(R.id.editText1);
		String vUname=uname.getText().toString();
		EditText pass=(EditText)findViewById(R.id.editText2);
		String vpass=pass.getText().toString();
		switch (v.getId()) {
		case R.id.button1:
			//if("b".equals(vpass)){
				//finish();
				//Intent i = new Intent(this, MainMenu.class );
				//startActivity(i);
				username=vUname;
				password=vpass;
				new LoginProcess().execute();
			//}
			//else {
				//Intent i = new Intent(this, LoginAlert.class );
				//startActivity(i);
			//}
			//break;
    	}
	}
	
	class LoginProcess extends AsyncTask<String, String, String> {

		ProgressDialog pDialog;
		int success;
		/**
		 * Before starting background thread Show Progress Dialog
		 * */
		@Override
		protected void onPreExecute() {
			super.onPreExecute();
			 pDialog = new ProgressDialog(Login.this);
			pDialog.setMessage("Login...");
			pDialog.setIndeterminate(false);
			pDialog.setCancelable(true);
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
					Log.d("Login Details", json.toString());
					
					try {
						
						
						// json success tag
						success = json.getInt("success");
						
					} catch (JSONException e) {
						e.printStackTrace();
					}
				//}
			//});

			return null;
		}


		/**
		 * After completing background task Dismiss the progress dialog
		 * **/
		protected void onPostExecute(String file_url) {
			
			// dismiss the dialog once got all details
			pDialog.dismiss();
			if (success == 1) {
				Intent i = new Intent(getApplicationContext(), MainMenu.class);
				startActivity(i);
				finish();
			}else{
				Toast.makeText(getApplicationContext(), "Username atau password salah!", Toast.LENGTH_LONG).show();
			}
		}
	}
}

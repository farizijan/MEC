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

import android.os.AsyncTask;
import android.os.Bundle;
import android.app.Activity;
import android.app.Notification;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.app.ProgressDialog;
import android.util.Log;
import android.view.Menu;
import android.view.ContextMenu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.SubMenu;
import android.view.ContextMenu.ContextMenuInfo;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.SharedPreferences;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gcm.GCMRegistrar;
import static ptiik.mobapp.belajaractivity.CommonUtilities.SENDER_ID;
import static ptiik.mobapp.belajaractivity.CommonUtilities.SERVER_URL;
import static ptiik.mobapp.belajaractivity.CommonUtilities.TAG;
import static ptiik.mobapp.belajaractivity.CommonUtilities.DISPLAY_MESSAGE_ACTION;
import static ptiik.mobapp.belajaractivity.CommonUtilities.EXTRA_MESSAGE;

public class MainMenu extends Activity implements OnClickListener {

	private static TextView hr;
	private ProgressDialog pDialog;
	public static int alarmId=0;
	SharedPreferences login;
    AsyncTask<Void, Void, Void> mRegisterTask;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.main_menu);
        login=getSharedPreferences("mec-data", 0);
        Log.d("Shr-Login", "username: "+ login.getString("username", ""));
        Log.d("Shr-Login", "password: "+ login.getString("password", ""));
        //GCM
        checkNotNull(SERVER_URL, "SERVER_URL");
        checkNotNull(SENDER_ID, "SENDER_ID");
        // Make sure the device has the proper dependencies.
        GCMRegistrar.checkDevice(this);
        // Make sure the manifest was properly set - comment out this line
        // while developing the app, then uncomment it when it's ready.
        GCMRegistrar.checkManifest(this);
        //setContentView(R.layout.main_menu);
        //hr = (TextView) findViewById(R.id.Hari);
        registerReceiver(mHandleMessageReceiver,
                new IntentFilter(DISPLAY_MESSAGE_ACTION));
        final String regId = GCMRegistrar.getRegistrationId(this);
        
        if (regId.equals("")) {
            // Automatically registers application on startup.
            GCMRegistrar.register(getApplicationContext(), SENDER_ID);
        } else {
            // Device is already registered on GCM, check server.
            if (GCMRegistrar.isRegisteredOnServer(this)) {
                // Skips registration.
                //hr.append(getString(R.string.already_registered) + "\n");
            } else {
                // Try to register again, but not in the UI thread.
                // It's also necessary to cancel the thread onDestroy(),
                // hence the use of AsyncTask instead of a raw thread.
                final Context context = this;
                mRegisterTask = new AsyncTask<Void, Void, Void>() {
                	private ProgressDialog pDialog;
                	protected Context applicationContext;
                	@Override
                	protected void onPreExecute() {
                		super.onPreExecute();
            			pDialog = new ProgressDialog(MainMenu.this);
            			pDialog.setMessage("Registering device. Please wait...");
            			pDialog.setIndeterminate(false);
            			pDialog.setCancelable(false);
            			pDialog.show();
       		        }

                    @Override
                    protected Void doInBackground(Void... params) {
                        boolean registered =
                                ServerUtilities.register(context, regId);
                        // At this point all attempts to register with the app
                        // server failed, so we need to unregister the device
                        // from GCM - the app will try to register again when
                        // it is restarted. Note that GCM will send an
                        // unregistered callback upon completion, but
                        // GCMIntentService.onUnregistered() will ignore it.
                        if (!registered) {
                            GCMRegistrar.unregister(context);
                        }
                        return null;
                    }

                    @Override
                    protected void onPostExecute(Void result) {
                        mRegisterTask = null;
                        pDialog.dismiss();
                    }

                };
                mRegisterTask.execute(null, null, null);
            }
        }
        //END GCM
        View jadwalButton=findViewById(R.id.jadwal_button);
        jadwalButton.setOnClickListener(this);
        View tugasButton=findViewById(R.id.tugas_button);
        tugasButton.setOnClickListener(this);
        View logoutButton=findViewById(R.id.logout_button);
        logoutButton.setOnClickListener(this);
        View exitButton=findViewById(R.id.exit_button);
        exitButton.setOnClickListener(this);
        /*View jadwalButton=findViewById(R.id.jadwal_button);
        jadwalButton.setOnClickListener(this);
        
        bv = (TextView) findViewById(R.id.jadwal_button);
        hr = (TextView) findViewById(R.id.Hari);*/
        //registerForContextMenu((View) findViewById(R.id.jadwal_button));
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
    	MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.options_menu, menu);
        return true;
    }

	@Override
	public void onClick(View v) {
		// TODO Auto-generated method stub
		switch (v.getId()) {
		case R.id.jadwal_button:
			Intent k = new Intent(this, SetAlarm.class );
			startActivity(k);
			break;
		case R.id.tugas_button:
			Intent i = new Intent(this, tampilTugas.class );
			startActivity(i);
			break;
		case R.id.logout_button:
			for(int a=0;a<=login.getInt("alarmId", 0);a++){
				Intent it = new Intent(getApplicationContext(),DisplayAlarmNotification.class);
				//PendingIntent pintent=PendingIntent.
				PendingIntent displayIntent = PendingIntent.getActivity(getBaseContext(), 0, it, a);
				displayIntent.cancel();
			}
			
			login.edit().clear().commit();
			finish();
			Intent logout = new Intent(this, Login.class );
			startActivity(logout);
			break;
		case R.id.exit_button:
			finish();
			break;
    	}
	}
	
	//----------------BARU
	@Override
	public boolean onPrepareOptionsMenu(Menu menu) {
		return true;
	}
		
	@Override
	public void onCreateContextMenu(ContextMenu menu, View v, ContextMenuInfo menuInfo) {
	
	}

	//GCM
    @Override
    protected void onDestroy(){
    	// TODO Auto-generated method stub
        if (mRegisterTask != null) {
            mRegisterTask.cancel(true);
        }
        unregisterReceiver(mHandleMessageReceiver);
        GCMRegistrar.onDestroy(getApplicationContext());
        super.onDestroy();
    }

    private void checkNotNull(Object reference, String name) {
        if (reference == null) {
            throw new NullPointerException(
                    getString(R.string.error_config, name));
        }
    }

    private final BroadcastReceiver mHandleMessageReceiver =
            new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {
            String newMessage = intent.getExtras().getString(EXTRA_MESSAGE);
            hr.append(newMessage + "\n");
        }
    };
	//END GCM
}

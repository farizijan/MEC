/*
 * Fariz Izzan Agusta
 * 105060805111002
 */
package ptiik.mobapp.belajaractivity;

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

	private final int MENU_ADD=1, MENU_SEND=2, MENU_DEL=3;
	private final int GROUP_DEFAULT=0, GROUP_DEL=1;
	private final int ID_DEFAULT=0;
	private final int ID_TEXT1=1, ID_TEXT2=2, ID_TEXT3=3;
	private final int ID_SENIN=1, ID_SELASA=2, ID_RABU=3, ID_KAMIS=4, ID_JUMAT=5, ID_SABTU=6;
	private String[] choices = {"Press Me", "Try Again", "Change Me"};
	private static int itemNum=0;
	private static TextView bv;
	private static TextView hr;
    AsyncTask<Void, Void, Void> mRegisterTask;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.main_menu);
        
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
		case R.id.tugas_button:
			Intent i = new Intent(this, tampilTugas.class );
			startActivity(i);
			break;
		case R.id.logout_button:
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
		if(itemNum>0) {
			menu.setGroupVisible(GROUP_DEL, true);
		} else {
			menu.setGroupVisible(GROUP_DEL, false);
		}
		return super.onPrepareOptionsMenu(menu);
	}
	
	@Override
	public boolean onOptionsItemSelected(MenuItem item) {
		switch(item.getItemId()) {
			case MENU_ADD:
				create_note();
				return true;
			case MENU_SEND:
				send_note();
				return true;
			case MENU_DEL:
				delete_note();
				return true;
		}
		return super.onOptionsItemSelected(item);
	}
	
	@Override
	public void onCreateContextMenu(ContextMenu menu, View v,
	ContextMenuInfo menuInfo) {
		super.onCreateContextMenu(menu, v, menuInfo);
		//if(v.getId() == R.id.jadwal_button) {
			/*SubMenu textMenu = menu.addSubMenu("Change Text");
			textMenu.add(0, ID_TEXT1, 0, choices[0]);
			textMenu.add(0, ID_TEXT2, 0, choices[1]);
			textMenu.add(0, ID_TEXT3, 0, choices[2]);*/
			//menu.add(0, ID_SENIN, 0, "SENIN");
			//menu.add(0, ID_SELASA, 0, "SELASA");
			//menu.add(0, ID_RABU, 0, "RABU");
			//menu.add(0, ID_KAMIS, 0, "KAMIS");
			//menu.add(0, ID_JUMAT, 0, "JUMAT");
			//menu.add(0, ID_SABTU, 0, "SABTU");
			//menu.add(0, ID_DEFAULT, 0, "MINGGU");
			
		//}
	}

	@Override
	public boolean onContextItemSelected(MenuItem item) {
		switch(item.getItemId()) {
			case ID_DEFAULT:
				hr.setText("Hari");
				return true;
			/*case ID_TEXT1:
			case ID_TEXT2:
			case ID_TEXT3:
				bv.setText(choices[item.getItemId()-1]);
				return true;*/
			case ID_SENIN:
				hr.setText("SENIN");
				return true;
			case ID_SELASA:
				hr.setText("SELASA");
				return true;
			case ID_RABU:
				hr.setText("RABU");
				return true;
			case ID_KAMIS:
				hr.setText("KAMIS");
				return true;
			case ID_JUMAT:
				hr.setText("JUMAT");
				return true;
			case ID_SABTU:
				hr.setText("SABTU");
				return true;
		}
		return super.onContextItemSelected(item);
	}
	
	void create_note() { // mock code to create note
		itemNum++;
	}
	void send_note() { // mock code to send note
		Toast.makeText(this, "Item: "+itemNum,
		Toast.LENGTH_SHORT).show();
	}
	void delete_note() { // mock code to delete note
		itemNum--;
	}
	
	
	//NOTIFICATION
	public void tampilNotif(View view){
		String ns = Context.NOTIFICATION_SERVICE;
		NotificationManager mNotificationManager = (NotificationManager) getSystemService(ns);
		
		int icon = R.drawable.ic_launcher;
		CharSequence tickerText = "Tugas Baru";
		long when = System.currentTimeMillis();
	
		Notification notification = new Notification(icon, tickerText, when);
		notification.flags |= Notification.FLAG_AUTO_CANCEL;
		Context context = getApplicationContext();
		CharSequence contentTitle = "Pemberitahuan Tugas";
		CharSequence contentText = "Mobile Application - Tugas 1";
		Intent notificationIntent = new Intent(this, MainMenu.class);
		PendingIntent contentIntent = PendingIntent.getActivity(this, 0, notificationIntent, 0);
		
		notification.setLatestEventInfo(context, contentTitle, contentText, contentIntent);
		final int HELLO_ID = 1;
	
		mNotificationManager.notify(HELLO_ID, notification);
				
	}
	//END OF NOTIFICATION
	
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

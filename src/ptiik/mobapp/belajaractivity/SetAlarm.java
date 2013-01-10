package ptiik.mobapp.belajaractivity;

import java.util.Calendar;

import android.app.Activity;
import android.app.AlarmManager;
import android.app.PendingIntent;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.SharedPreferences.Editor;
import android.os.Bundle;
import android.view.View;
import android.view.Window;
import android.widget.Button;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.TimePicker;
import android.widget.Toast;

public class SetAlarm extends Activity{
	TimePicker timePicker;
    DatePicker datePicker;
    String idMatkul;
    String judulMatkul;
    String deskripsi;
    SharedPreferences prefs;
    /** Called when the activity is first created. */
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        this.requestWindowFeature(Window.FEATURE_NO_TITLE);
        setContentView(R.layout.set_alarm);
        prefs=getSharedPreferences("mec-data", 0);
 
        //---Button view---
        Button btnOpen = (Button) findViewById(R.id.btnSetAlarm);
        btnOpen.setOnClickListener(new View.OnClickListener() {
 
            public void onClick(View v) {                
                timePicker = (TimePicker) findViewById(R.id.timePicker);
                datePicker = (DatePicker) findViewById(R.id.datePicker);                   
                
                 int id=  prefs.getInt("alarmId", 0);
 
                //---use the AlarmManager to trigger an alarm---
                AlarmManager alarmManager = (AlarmManager) getSystemService(ALARM_SERVICE);                 
 
                //---get current date and time---
                Calendar calendar = Calendar.getInstance();       
 
                //---sets the time for the alarm to trigger---
                calendar.set(Calendar.YEAR, datePicker.getYear());
                calendar.set(Calendar.MONTH, datePicker.getMonth());
                calendar.set(Calendar.DAY_OF_MONTH, datePicker.getDayOfMonth());                 
                calendar.set(Calendar.HOUR_OF_DAY, timePicker.getCurrentHour());
                calendar.set(Calendar.MINUTE, timePicker.getCurrentMinute());
                calendar.set(Calendar.SECOND, 0);
 
                //---PendingIntent to launch activity when the alarm triggers---                    
                Intent i = new Intent(getApplicationContext(),DisplayAlarmNotification.class);
 
                               
                
                //menangkap nilai yang dikirim dari editTugas.java
                idMatkul=getIntent().getStringExtra("idMatkul");
                judulMatkul=getIntent().getStringExtra("judulMatkul");
                deskripsi=getIntent().getStringExtra("deskripsiMatkul");
                
              //---mengirim nilai ke notification---
                i.putExtra("NotifID", id);
                i.putExtra("idMatkul", idMatkul);
                i.putExtra("judul", judulMatkul);
                i.putExtra("deskripsi", deskripsi);
                
                PendingIntent displayIntent = PendingIntent.getActivity(
                    getBaseContext(), 0, i, id);               
 
                //---sets the alarm to trigger---
                alarmManager.set(AlarmManager.RTC_WAKEUP, 
                    calendar.getTimeInMillis(), displayIntent);
                
                Toast.makeText(getApplicationContext(), "Reminder telah diset ", Toast.LENGTH_LONG).show();
                id++;
                //ptiik.mobapp.belajaractivity.MainMenu.alarmId=id;
                Editor prefsEdit= prefs.edit();
                prefsEdit.putInt("alarmId", id);
                prefsEdit.commit();
                finish();
            }
        }); 
    }
}

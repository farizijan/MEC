package ptiik.mobapp.belajaractivity;

import android.os.Bundle;
import android.app.Activity;
import android.view.Menu;
import android.widget.TextView;

public class PushMessage extends Activity {
	
	public static TextView pesan;
	public static TextView judulTicker;
	public static TextView judulPesan;
	public static TextView ticker;
	
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_push_message);
        pesan = (TextView) findViewById(R.id.msg);
        judulTicker = (TextView) findViewById(R.id.tickTitle);
        judulPesan = (TextView) findViewById(R.id.msgTitle);
        ticker = (TextView) findViewById(R.id.ticker);
        
        pesan.setText(GCMIntentService.message);
        judulTicker.setText(GCMIntentService.tickTitle);
        judulPesan.setText(GCMIntentService.title);
        ticker.setText(GCMIntentService.ticker);
        
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.activity_push_message, menu);
        return true;
    }
}

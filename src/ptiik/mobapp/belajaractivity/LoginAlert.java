/*
 * Fariz Izzan Agusta
 * 105060805111002
 */

package ptiik.mobapp.belajaractivity;

import android.os.Bundle;
import android.app.Activity;
import android.view.Menu;

public class LoginAlert extends Activity {

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login_alert);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.activity_login_alert, menu);
        return true;
    }
}

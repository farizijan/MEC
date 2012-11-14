/*
 * Fariz Izzan Agusta
 * 105060805111002
 */
package ptiik.mobapp.belajaractivity;

import ptiik.mobapp.belajaractivity.R;
import android.os.Bundle;
import android.app.Activity;
import android.content.Intent;
import android.view.Menu;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.EditText;

public class Login extends Activity implements OnClickListener {

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
	@Override
	public void onClick(View v) {
		// TODO Auto-generated method stub
		EditText uname=(EditText)findViewById(R.id.editText1);
		String vUname=uname.getText().toString();
		EditText pass=(EditText)findViewById(R.id.editText2);
		String vpass=pass.getText().toString();
		switch (v.getId()) {
		case R.id.button1:
			if("b".equals(vpass)){
				finish();
				Intent i = new Intent(this, MainMenu.class );
				startActivity(i);
				username=vUname;
			}
			else {
				Intent i = new Intent(this, LoginAlert.class );
				startActivity(i);
			}
			break;
    	}
	}
}

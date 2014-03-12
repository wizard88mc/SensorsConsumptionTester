package it.cs.unipd.androiddefinitivesensorstester;

import it.cs.unipd.multiplesensors.MultipleSensors;
import android.os.Bundle;
import android.app.Activity;
import android.content.Intent;
import android.view.Menu;
import android.view.View;

public class MainActivity extends Activity {

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.main_activity);
	}

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		// Inflate the menu; this adds items to the action bar if it is present.
		getMenuInflater().inflate(R.menu.main, menu);
		return true;
	}
	
	public void buttonChoiceClicked(View view) {
		
		int buttonID = view.getId();
		
		switch (buttonID) {
		case R.id.buttonMultipleSensors:
			Intent intent = new Intent(this, MultipleSensors.class);
			startActivity(intent);
			break;

		case R.id.buttonDataForPocket:
			
			break;
		
		default:
			break;
		}
	}

}

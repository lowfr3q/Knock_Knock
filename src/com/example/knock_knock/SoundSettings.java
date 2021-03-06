package com.example.knock_knock;

import java.util.Iterator;
import java.util.Set;

import android.os.Bundle;
import android.app.Activity;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Color;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.CompoundButton;
import android.widget.TableLayout;
import android.widget.TableRow;
import android.widget.TextView;
import android.widget.ToggleButton;

public class SoundSettings extends Activity {
	
	private TableLayout soundTable;
	private Set<String> allSounds;
	private Set<String> checkedSounds;
	public static final String PREFS_NAME = "KnockKnockPrefs";
	SharedPreferences prefs;
	
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_sound_settings);
		
		//Load previously set user preferences
		loadSoundSelectionPreferences();	
		
	}
	
	private void loadSoundSelectionPreferences() {
		//Get SharedPreferences and loud up sounds
		prefs = getSharedPreferences(PREFS_NAME, 0);
		allSounds = PreferenceStorage.getAllSounds(prefs);
		
		//Get Table
		soundTable = (TableLayout)findViewById(R.id.soundTable);
		
		//Load sound selection preferences
		Iterator<String> soundIter = allSounds.iterator();
		while(soundIter.hasNext()){
			final String curSound = soundIter.next();
			TableRow curRow = new TableRow(this);
			
			//Label for sound
			TextView text = new TextView(this);
			text.setText(curSound);
			text.setTextSize(20);
			
			//Toggle button for sound
			ToggleButton toggle = new ToggleButton(this);
			if(PreferenceStorage.isSoundOn(prefs, curSound)){
				toggle.setChecked(true);
			}
			else{
				toggle.setChecked(false);
			}
			toggle.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
				@Override
				public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
					if(isChecked){
						PreferenceStorage.setSound(prefs, curSound, true);
					} else {
						PreferenceStorage.setSound(prefs, curSound, false);
					}
				    SharedPreferences.Editor editor = prefs.edit();
				    editor.putStringSet("checkedSounds", checkedSounds);
				    editor.commit();
				}
				
			});
			
			//Settings button for sound
			Button settings = new Button(this);
			//settings.setText("Settings");
			settings.setBackgroundResource(R.drawable.ic_action_edit);
			settings.setScaleX(0.65f);
			settings.setScaleY(0.65f);
			settings.setOnClickListener(new OnClickListener() {

				@Override
				public void onClick(View v) {
					toNotificationPreferences(curSound);			
				}
				
			});
			
			Button delete = new Button(this);
			//settings.setText("Settings");
			delete.setBackgroundResource(R.drawable.ic_action_discard);
			delete.setScaleX(0.6f);
			delete.setScaleY(0.6f);
			delete.setOnClickListener(new OnClickListener() {

				@Override
				public void onClick(View v) {
					PreferenceStorage.delSound(prefs, curSound);
					finish(); //TODO: refresh activity from within or redraw activity
					startActivity(getIntent());
				}
				
			});

			//Align toggle button to right
			/*TableLayout.LayoutParams params = new TableLayout.LayoutParams(LayoutParams.WRAP_CONTENT, LayoutParams.MATCH_PARENT);
			params.weight = 1.0f;
			params.gravity=Gravity.RIGHT;
			toggle.setLayoutParams(params);*/
			
			//Add color 
			int color = Color.parseColor(PreferenceStorage.getAlertColor(prefs,curSound));
			Button colorBox = new Button(this);
			colorBox.setBackgroundColor(color);
			colorBox.setOnClickListener(new OnClickListener() {

				@Override
				public void onClick(View v) {
					toNotificationPreferences(curSound);			
				}
				
			});
			
			
			//Add all components to view
			curRow.addView(toggle);
			curRow.addView(text);
			curRow.addView(colorBox);
			curRow.addView(settings);
			curRow.addView(delete);
			soundTable.addView(curRow);
		}
	}
	


	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		// Inflate the menu; this adds items to the action bar if it is present.
		getMenuInflater().inflate(R.menu.nav_action_bar, menu);
		menu.findItem(R.id.action_bar_settings).setEnabled(false);
		return true;
	}
	
	@Override
	public boolean onOptionsItemSelected(MenuItem item) {
		// Handle action bar item clicks here. The action bar will
		// automatically handle clicks on the Home/Up button, so long
		// as you specify a parent activity in AndroidManifest.xml.
		switch (item.getItemId()) {
	        case R.id.action_bar_home:
	        	toSplashPage();
	            return true;
	        case R.id.action_bar_training:
	        	toTrainMenu();
	            return true;
	        default:
	            return super.onOptionsItemSelected(item);
		}
	}
	
	public void toTrainMenu(){
		//Method for button onClick, returns to TrainingMenu
		Intent i = new Intent(this, TrainingMenu.class);
	    startActivity(i);
	}
	
	public void toSplashPage(){
		//Method for button onClick, returns to SplashPage
		Intent i = new Intent(this, SplashPage.class);
	    startActivity(i);
	}
	
	public void toNotificationPreferences(String soundName) {
		Intent i = new Intent(this, NotificationSettings.class);
		i.putExtra("sound", soundName);
		startActivity(i);
	}

}


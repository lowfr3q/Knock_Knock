package com.example.knock_knock;

import java.util.HashSet;
import java.util.Iterator;
import java.util.Set;

import android.content.SharedPreferences;

public class PreferenceStorage {
	
	//Storage for sound preferences
	SharedPreferences prefs;
	
	private static Set<String> deFault = new HashSet<String>();
	
	
	
	//Keys for storing preferences
	public static final String ALL_SOUNDS = "allSounds";
	public static final String ON = "_on";
	public static final String ALERT_COLOR = "_alertcolor";
	public static final String PUSH_NOTIF = "_push";
	public static final String ALERT_NOTIF = "_alert";
	public static final String VIBRATE_NOTIF = "_vibrate";
	public static final String LISTENING = "listening";
	
	public static boolean getIsListening (SharedPreferences prefs){
		return prefs.getBoolean(LISTENING,false);
	}
	
	public static void setIsListening(SharedPreferences prefs, boolean listenStatus){
	      SharedPreferences.Editor editor = prefs.edit();
	      editor.putBoolean(LISTENING, listenStatus);
	      editor.commit();
	}
	
	public static Set<String> getAllSounds(SharedPreferences prefs, Set<String> deFault) {
		return prefs.getStringSet(PreferenceStorage.ALL_SOUNDS, deFault);
	}
	
	public static Set<String> getAllCheckedSounds(SharedPreferences prefs) {
		HashSet<String> result = new HashSet<String>();
		Iterator<String> it = getAllSounds(prefs, new HashSet<String>()).iterator();
		while(it.hasNext()) {
			String soundName = it.next();
			System.out.println(soundName);
			if (isSoundOn(prefs, soundName)) {
				result.add(soundName);
			}
		}
		return result;
	}
	
	public static void setAllSounds(SharedPreferences prefs) {
		
		SharedPreferences.Editor editor = prefs.edit();
		editor.putStringSet(PreferenceStorage.ALL_SOUNDS, deFault);
		editor.commit();
	}
	
	public static boolean isSoundOn(SharedPreferences prefs, String soundName) {
		return prefs.getBoolean(soundName+PreferenceStorage.ON, true);
	}
	
	public static boolean isPushNotifOn(SharedPreferences prefs, String soundName) {
		return prefs.getBoolean(soundName+PreferenceStorage.PUSH_NOTIF, true);
	}
	
	public static boolean isAlertNotifOn(SharedPreferences prefs, String soundName) {
		return prefs.getBoolean(soundName+PreferenceStorage.ALERT_NOTIF, true);
	}
	
	public static boolean isVibrateNotifOn(SharedPreferences prefs, String soundName) {
		return prefs.getBoolean(soundName+PreferenceStorage.VIBRATE_NOTIF, true);
	}
	
	public static String getAlertColor(SharedPreferences prefs, String soundName) {
		return prefs.getString(soundName+PreferenceStorage.ALERT_COLOR, "Red");
	}
	
	public static void setSound(SharedPreferences prefs, String soundName, boolean on) {
		SharedPreferences.Editor editor = prefs.edit();
		editor.putBoolean(soundName+PreferenceStorage.ON, on);
    	editor.commit();
	}
	
	public static void setPushNotif(SharedPreferences prefs, String soundName, boolean on) {
		SharedPreferences.Editor editor = prefs.edit();
		editor.putBoolean(soundName+PreferenceStorage.PUSH_NOTIF, on);
    	editor.commit();
	}
	
	public static void setAlertNotif(SharedPreferences prefs, String soundName, boolean on) {
		SharedPreferences.Editor editor = prefs.edit();
		editor.putBoolean(soundName+PreferenceStorage.ALERT_NOTIF, on);
    	editor.commit();
	}
	
	public static void setVibrate(SharedPreferences prefs, String soundName, boolean on) {
		SharedPreferences.Editor editor = prefs.edit();
		editor.putBoolean(soundName+PreferenceStorage.VIBRATE_NOTIF, on);
    	editor.commit();
	}
	
	public static void setColor(SharedPreferences prefs, String soundName, String color) {
		SharedPreferences.Editor editor = prefs.edit();
		editor.putString(soundName+PreferenceStorage.ALERT_COLOR, color);
    	editor.commit();
	}
	

}

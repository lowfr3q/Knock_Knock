package com.example.knock_knock;

import java.util.HashSet;
import java.util.Set;

import be.hogent.tarsos.dsp.AudioEvent;
import be.hogent.tarsos.dsp.onsets.ComplexOnsetDetector;
import be.hogent.tarsos.dsp.onsets.OnsetHandler;
import be.hogent.tarsos.dsp.onsets.PercussionOnsetDetector;
import be.hogent.tarsos.dsp.pitch.PitchDetectionHandler;
import be.hogent.tarsos.dsp.pitch.PitchDetectionResult;
import be.hogent.tarsos.dsp.pitch.PitchProcessor;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.app.Service;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.media.AudioFormat;
import android.media.AudioRecord;
import android.media.MediaRecorder;
import android.os.IBinder;
import android.support.v4.app.NotificationCompat;
import android.support.v4.app.TaskStackBuilder;



public class backGroundListener extends Service implements OnsetHandler, PitchDetectionHandler {
	//http://www.vogella.com/tutorials/AndroidServices/article.html
	
	final static int SAMPLE_RATE = 16000;
	private byte[] buffer;
	private int bufferSize;
	private be.hogent.tarsos.dsp.AudioFormat tarForm;
	private boolean rec;
	private Set<String> checkedSounds;
	public final static String EXTRA_MESSAGE = "com.example.backGroundList.MESSAGE";
	public static final String PREFS_NAME = "KnockKnockPrefs";
	private String notification;
	
	@Override
	public int onStartCommand(Intent intent, int flags, int startId) {
	    
		//Get SharedPreferences and load up sounds
		SharedPreferences prefs = getSharedPreferences(PREFS_NAME, 0);
		checkedSounds = PreferenceStorage.getAllCheckedSounds(prefs);
		notification = "";
		listen();
		return super.onStartCommand(intent,flags,startId);
	  }
	
	
	
	@Override
	public IBinder onBind(Intent arg0) {
		// TODO Auto-generated method stub
		return null;
	}
	private void listen(){	
		//Get prefs
		SharedPreferences prefs = getSharedPreferences(PREFS_NAME, 0);
		
		bufferSize = AudioRecord.getMinBufferSize(SAMPLE_RATE, AudioFormat.CHANNEL_IN_MONO, AudioFormat.ENCODING_PCM_16BIT);					
		buffer = new byte[bufferSize];
		final AudioRecord recorder = new AudioRecord(MediaRecorder.AudioSource.MIC, SAMPLE_RATE, AudioFormat.CHANNEL_IN_MONO, AudioFormat.ENCODING_PCM_16BIT, bufferSize);
		
		
		//set up "clap" detector //
		final PercussionOnsetDetector pd = new PercussionOnsetDetector(SAMPLE_RATE, bufferSize/2, this, 100, 1);
		
		//set up "all" detector //
		final PitchProcessor pp = new PitchProcessor( PitchProcessor.PitchEstimationAlgorithm.AMDF,SAMPLE_RATE,bufferSize,this);
		
		//start recording
		rec = PreferenceStorage.getIsListening(prefs);
		recorder.startRecording();
		tarForm= new be.hogent.tarsos.dsp.AudioFormat(SAMPLE_RATE,16,1,true,false);
		Thread listen = new Thread(new Runnable(){
			public void run(){
				SharedPreferences prefs = getSharedPreferences(PREFS_NAME, 0);
				while (rec){
					rec = PreferenceStorage.getIsListening(prefs);
					int sig = recorder.read(buffer,0,bufferSize);
					AudioEvent ae = new AudioEvent(tarForm, sig);
					ae.setFloatBufferWithByteBuffer(buffer);
					
					//WOZ for clap
					
					if(PreferenceStorage.isSoundOn(prefs, "Clap")){
						pd.process(ae);
					}
					//Wos for all
					if(PreferenceStorage.isSoundOn(prefs, "Whistle")){
						pp.process(ae);
					}
				}
				recorder.stop();
				PreferenceStorage.setIsListening(prefs,false);
			}
		});
		listen.start();
	}
	

	@Override
	public void handleOnset(double time, double salience) {
		//only here to test out and make it run for claps
		SharedPreferences prefs = getSharedPreferences(PREFS_NAME, 0);
		notification = "Clap";
		rec = false;
		Intent i = new Intent(this, Notification_Screen.class);
		i.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
		i.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK);
		i.putExtra(EXTRA_MESSAGE, notification);
	    startActivity(i);
	    this.stopSelf();
	}

	@Override
	public void handlePitch(PitchDetectionResult pitchDetectionResult,
			AudioEvent audioEvent) {
		//only here to test out and make it run for whistle
		SharedPreferences prefs = getSharedPreferences(PREFS_NAME, 0);
		if(pitchDetectionResult.isPitched()){
			notification = "Whistle";
			rec = false;
			if(PreferenceStorage.isPushNotifOn(prefs, notification)){
				sendPushNotification(notification);
			}
			Intent i = new Intent(this, Notification_Screen.class);
			i.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
			i.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK);
			i.putExtra(EXTRA_MESSAGE, notification);
		    startActivity(i);
		    this.stopSelf();
		}
		
	}

	public void sendPushNotification(String Sound){
		//http://developer.android.com/guide/topics/ui/notifiers/notifications.html
		
		// create notification builder for sound
		NotificationCompat.Builder mBuilder =
		        new NotificationCompat.Builder(this)
		        .setSmallIcon(R.drawable.ic_launcher)
		        .setContentTitle("Knock Knock")
		        .setContentText(Sound);
		
		//
		// Creates an explicit intent for the splash page, this will
		//be used later in the task Stack
		Intent resultIntent = new Intent(this, SplashPage.class);
		
		// The stack builder object will contain an artificial back stack for the
		// started Activity.
		// This ensures that navigating backward from the Activity leads out of
		// your application to the Home screen.
		TaskStackBuilder stackBuilder = TaskStackBuilder.create(this);
		
		// Adds the back stack for the Intent (but not the Intent itself)
		stackBuilder.addParentStack(SplashPage.class);
		
		// Adds the Intent that starts the Activity to the top of the stack
		stackBuilder.addNextIntent(resultIntent);
		PendingIntent resultPendingIntent =
		        stackBuilder.getPendingIntent(
		            0,
		            PendingIntent.FLAG_UPDATE_CURRENT
		        );
		mBuilder.setContentIntent(resultPendingIntent);
		NotificationManager mNotificationManager =
		    (NotificationManager) getSystemService(Context.NOTIFICATION_SERVICE);
		// mId allows you to update the notification later on (Currently set to zero).
		mNotificationManager.notify(0, mBuilder.build());

	}

}


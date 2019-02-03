package com.activetheoryinc.unitycardiosample;

import android.content.res.Configuration;
import android.graphics.PixelFormat;
import android.os.Bundle;
import android.util.Log;
import android.view.KeyEvent;
import android.view.MotionEvent;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.RelativeLayout;

import com.activetheoryinc.sdk.lib.BGCUnityActivity;
import com.activetheoryinc.sdk.lib.ExerciseReadingData;
import com.activetheoryinc.sdk.lib.ReadingListener;
import com.unity3d.player.R;
import com.unity3d.player.UnityPlayer;

public class UnityPlayerNativeActivity extends BGCUnityActivity
{
	private static String TAG = "UnitySampleJava";
	protected UnityPlayer mUnityPlayer;		// don't change the name of this variable; referenced from native code

	// UnityPlayer.init() should be called before attaching the view to a layout - it will load the native code.
	// UnityPlayer.quit() should be the last thing called - it will unload the native code.
	protected void onCreate (Bundle savedInstanceState)
	{
		requestWindowFeature(Window.FEATURE_NO_TITLE);
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_unity);
		
		getWindow().takeSurface(null);
		setTheme(android.R.style.Theme_NoTitleBar_Fullscreen);
		getWindow().setFormat(PixelFormat.RGBX_8888);

		mUnityPlayer = new UnityPlayer(this);
		if (mUnityPlayer.getSettings ().getBoolean ("hide_status_bar", true))
			getWindow ().setFlags (WindowManager.LayoutParams.FLAG_FULLSCREEN,
			                       WindowManager.LayoutParams.FLAG_FULLSCREEN);
				
		InitializeBitGymWithUnity();
	}
	
	protected void InitializeBitGymWithUnity() {
		/*
		 * NOTE! Check the name of the primary layout used
		 * (R.layout.activit_main by default) Additionally, you need to place
		 * the mPreview view inside a frame layout. Set aside another frame
		 * layout for the Unity player.
		 */

		// Set up the BitGym-Unity relay
		mUnityReadingListener = new ReadingListener<ExerciseReadingData>() {
			public void OnNewReading(ExerciseReadingData reading) {
				// Send the message to Unity for the Unity-side BG manager to
				// interpret
				UnityPlayer.UnitySendMessage(BG_NATIVE_RECEIVER, BG_DATA_TYPE,
				 	reading != null ? reading.toString() : "");
			}
		};
		
		
		// Set up the unity player
		Log.i("BGUSample", "We have started things");
		View playerView = mUnityPlayer.getView();
		RelativeLayout unityLayout = (RelativeLayout) findViewById(R.id.unityContainer);
		unityLayout.addView(playerView);
	}
	
	protected void onDestroy ()
	{
		mUnityPlayer.quit();
		super.onDestroy();
	}
	
	// onPause()/onResume() must be sent to UnityPlayer to enable pause and resource recreation on resume.
	protected void onPause()
	{
		super.onPause();
		mUnityPlayer.pause();
	}
	
	protected void onResume()
	{
		super.onResume();
		mUnityPlayer.resume();
	}
	// This ensures the layout will be correct.
	@Override public void onConfigurationChanged(Configuration newConfig)
	{
		super.onConfigurationChanged(newConfig);
		mUnityPlayer.configurationChanged(newConfig);
	}

	// Notify Unity of the focus change.
	@Override public void onWindowFocusChanged(boolean hasFocus)
	{
		super.onWindowFocusChanged(hasFocus);
		mUnityPlayer.windowFocusChanged(hasFocus);
	}
	// For some reason the multiple keyevent type is not supported by the ndk.
	// Force event injection by overriding dispatchKeyEvent().
	@Override public boolean dispatchKeyEvent(KeyEvent event)
	{
		if (event.getAction() == KeyEvent.ACTION_MULTIPLE)
			return mUnityPlayer.injectEvent(event);
		return super.dispatchKeyEvent(event);
	}

	// Pass any events not handled by (unfocused) views straight to UnityPlayer
	@Override
	public boolean onKeyDown(int keyCode, KeyEvent event) {
		if (keyCode == KeyEvent.KEYCODE_MENU) {
			Log.i(TAG, "That's a menu");
			return true;
		} else if(keyCode == KeyEvent.KEYCODE_BACK) {
			Log.i(TAG, "That's a back button hooray");
			UnityPlayer.UnitySendMessage("Main Camera", "HardwareBackPressed", "no message");
			return true;
		} else
			return false;
	}
	@Override public boolean onKeyUp(int keyCode, KeyEvent event)     { return mUnityPlayer.injectEvent(event); }
	@Override public boolean onTouchEvent(MotionEvent event)          { return mUnityPlayer.injectEvent(event); }
	/*API12*/ public boolean onGenericMotionEvent(MotionEvent event)  { return mUnityPlayer.injectEvent(event); }

}

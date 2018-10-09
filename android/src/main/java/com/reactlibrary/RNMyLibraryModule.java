
package com.reactlibrary;

import android.app.Activity;
import android.app.KeyguardManager;
import android.content.Context;
import android.content.Intent;
import android.os.Build;
import android.provider.Settings;
import android.util.Log;
import com.facebook.react.bridge.ActivityEventListener;
import com.facebook.react.bridge.Promise;
import com.facebook.react.bridge.ReactApplicationContext;
import com.facebook.react.bridge.ReactContextBaseJavaModule;
import com.facebook.react.bridge.ReactMethod;
import com.facebook.react.bridge.Callback;

public class RNMyLibraryModule extends ReactContextBaseJavaModule implements ActivityEventListener {

  private final ReactApplicationContext reactContext;
  private static final int LOCK_REQUEST_CODE = 221;
  private static final int SECURITY_SETTING_REQUEST_CODE = 233;

  private  String test = "" ;
  private Promise mPickerPromise;




  public RNMyLibraryModule(ReactApplicationContext reactContext) {
    super(reactContext);
    this.reactContext = reactContext;
    this.reactContext.addActivityEventListener(this);
  }

  @Override
  public String getName() {
    return "RNMyLibrary";
  }



  @ReactMethod
  public void getAuthonticationConfirmed(Promise promise) {
    final Activity currentActivity = getCurrentActivity();
    Context context = getCurrentActivity().getApplicationContext();
    mPickerPromise = promise;
    KeyguardManager keyguardManager = (KeyguardManager) context.getSystemService(Context.KEYGUARD_SERVICE); //api 16+
    if (currentActivity == null) {
      promise.reject("Activity doesn't exist");
      return;
    }  else {
      if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
        try {
          Intent i = keyguardManager.createConfirmDeviceCredentialIntent("Unlock", "Confirm your screen lock PIN,Pattern or Password");
          //Start activity for result
          currentActivity.startActivityForResult(i, LOCK_REQUEST_CODE);
        } catch (Exception e) {
          Intent intent = new Intent(Settings.ACTION_SECURITY_SETTINGS);
          try {
            //Start activity for result
            currentActivity.startActivityForResult(intent, SECURITY_SETTING_REQUEST_CODE);
          } catch (Exception ex) {
            mPickerPromise.reject("Unable to find screen lock. Go to Security settings then set screen lock manually");
          }
        }
      } else {
        mPickerPromise.reject("Device authentication doesn't support");
        mPickerPromise = null;
      }
    }
  }

  private boolean isDeviceSecure() {
    KeyguardManager keyguardManager = (KeyguardManager) this.getCurrentActivity().getApplicationContext().getSystemService(Context.KEYGUARD_SERVICE);
    return Build.VERSION.SDK_INT >= Build.VERSION_CODES.JELLY_BEAN && keyguardManager.isKeyguardSecure();
  }


  @Override
  public void onActivityResult(int requestCode, int resultCode, Intent data) {
    Log.d("test", "Insideon ActivityResult");
  }
}
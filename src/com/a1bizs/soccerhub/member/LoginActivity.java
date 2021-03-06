package com.a1bizs.soccerhub.member;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.UnsupportedEncodingException;
import java.util.ArrayList;

import org.apache.http.HttpEntity;
import org.apache.http.HttpResponse;
import org.apache.http.client.ClientProtocolException;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpDelete;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.client.methods.HttpPut;
import org.apache.http.entity.StringEntity;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.message.BasicHeader;
import org.apache.http.protocol.HTTP;
import org.codehaus.jackson.JsonGenerationException;
import org.codehaus.jackson.map.JsonMappingException;
import org.codehaus.jackson.map.ObjectMapper;
import org.json.JSONException;
import org.json.JSONObject;

import com.a1bizs.soccerhub.R;
import com.a1bizs.soccerhub.MainActivity;
import com.a1bizs.soccerhub.MatchActivity;
import com.a1bizs.soccerhub.RegisterActivity;
import com.a1bizs.soccerhub.SpinningActivity;
import com.a1bizs.soccerhub.conf.CONFIG;
import com.a1bizs.soccerhub.conf.PREFERENCE_CONF;
import com.a1bizs.soccerhub.favourite.FavouriteActivity;
import com.a1bizs.soccerhub.leagueToday.TodayActivity;
import com.a1bizs.soccerhub.model.ListItem;
import com.a1bizs.soccerhub.model.memberDb;
import com.a1bizs.soccerhub.readDays.ReadDayActitity;
import com.a1bizs.soccerhub.utility.utilityData;

import android.preference.PreferenceManager;
import android.animation.Animator;
import android.animation.AnimatorListenerAdapter;
import android.annotation.TargetApi;
import android.app.Activity;
import android.app.LocalActivityManager;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.SharedPreferences.Editor;
import android.os.AsyncTask;
import android.os.Build;
import android.os.Bundle;
import android.text.SpannableString;
import android.text.TextUtils;
import android.text.style.UnderlineSpan;
import android.util.Log;
import android.view.KeyEvent;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.inputmethod.EditorInfo;
import android.view.inputmethod.InputMethodManager;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;


@SuppressWarnings("deprecation")
public class LoginActivity extends Activity
{
	protected static LocalActivityManager mLocalActivityManager;
	private UserLoginTask mAuthTask = null;
	InputMethodManager imm;
	private memberDb user;

	// Values for email and password at the time of the login attempt.
	private String mName;
	private String mEmail;
	private String mPassword;

	// UI references.
	private EditText mEmailView;
	private EditText mPasswordView;
	private View mLoginFormView;
	private View mLoginStatusView;
	private TextView mLoginStatusMessageView;
	private TextView invalid;
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_login);
		checkLogin();
		
		// Set up the login form.
		mEmailView = (EditText) findViewById(R.id.email);
		mEmailView.setText(mEmail);
		invalid = (TextView) findViewById(R.id.invalid_login);
		invalid.setVisibility(View.GONE);
		imm = (InputMethodManager)this.getSystemService(Context.INPUT_METHOD_SERVICE);
		
		mPasswordView = (EditText) findViewById(R.id.password);
		mPasswordView
				.setOnEditorActionListener(new TextView.OnEditorActionListener() {
					@Override
					public boolean onEditorAction(TextView textView, int id,
							KeyEvent keyEvent) {
						if (id == R.id.login || id == EditorInfo.IME_NULL) {
							attemptLogin();
							return true;
						}
						return false;
					}
				});

		mLoginFormView          = findViewById(R.id.login_form);
		mLoginStatusView        = findViewById(R.id.login_status);
		
		mLoginStatusMessageView = (TextView) findViewById(R.id.login_status_message);

		findViewById(R.id.sign_in_button).setOnClickListener(
				new View.OnClickListener() {
					@Override
					public void onClick(View view) {
						attemptLogin();
					}
				});
		
		TextView register = (TextView) findViewById(R.id.register_link);
		String regText = "Register";
		SpannableString content = new SpannableString(regText);
		content.setSpan(new UnderlineSpan(), 0, regText.length(), 0);
		register.setText(content);
		register.setOnClickListener(
				new View.OnClickListener() {
		       public void onClick(View v){
		             Intent intent = new Intent(getBaseContext(), RegisterActivity.class);
		             startActivity(intent);
		       }
		} );
		TextView forgot = (TextView) findViewById(R.id.forgot_link);
		String forgotText = "Forgot Password?";
		SpannableString link = new SpannableString(forgotText);
		link.setSpan(new UnderlineSpan(), 0, forgotText.length(), 0);
		forgot.setText(link);
		forgot.setOnClickListener(
				new View.OnClickListener() {
		       public void onClick(View v){
		    	   View focusView = null;
		    	   mEmail    = mEmailView.getText().toString();
		    	   if (TextUtils.isEmpty(mEmail)) {
		   			mEmailView.setError(getString(R.string.error_field_required));
		   			focusView = mEmailView;
		    	   }
		    	   new AsyncTask<Void, Void, Void>() {
		   			@Override
		   			protected Void doInBackground(Void... params) {
		   				try {
		   				HttpClient httpclient = new DefaultHttpClient();
		   				HttpDelete httpdelete = new HttpDelete("http://appsa1bizssg.org/register/forgot?email=" + mEmail);
		   					HttpResponse res = httpclient.execute(httpdelete);
		   					} catch (ClientProtocolException e) {
		   						// TODO Auto-generated catch block
		   						e.printStackTrace();
		   					}  catch (JsonGenerationException e) {
		   						// TODO Auto-generated catch block
		   						e.printStackTrace();
		   					} catch (JsonMappingException e) {
		   						// TODO Auto-generated catch block
		   						e.printStackTrace();
		   					}  catch (UnsupportedEncodingException e) {
		   						// TODO Auto-generated catch block
		   						e.printStackTrace();
		   					} catch (IOException e) {
		   						// TODO Auto-generated catch block
		   						e.printStackTrace();
		   					}
		   		
		   		         return null;
		   			}
		   			@Override
				    protected void onPostExecute(Void i) {
					  Toast.makeText(getBaseContext(), "Reset Link is sent to your mail", Toast.LENGTH_LONG);
				    }
		   		}.execute();
		   		
		       }
		} );
	}

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		// Inflate the menu; this adds items to the action bar if it is present.
		getMenuInflater().inflate(R.menu.main, menu);
		return true;
	}

	public void attemptLogin() {
		if (mAuthTask != null) {
			return;
		}

		// Reset errors.
		mEmailView.setError(null);
		mPasswordView.setError(null);

		// Store values at the time of the login attempt.
		mEmail    = mEmailView.getText().toString();
		mPassword = mPasswordView.getText().toString();
		

		boolean cancel = false;
		View focusView = null;

		
		if (TextUtils.isEmpty(mPassword)) {
			mPasswordView.setError(getString(R.string.error_field_required));
			focusView = mPasswordView;
			cancel = true;
		} else if (mPassword.length() < 4) {
			mPasswordView.setError(getString(R.string.error_invalid_password));
			focusView = mPasswordView;
			cancel = true;
		} 
		

		if (TextUtils.isEmpty(mEmail)) {
			mEmailView.setError(getString(R.string.error_field_required));
			focusView = mEmailView;
			cancel = true;
		} else if (!mEmail.contains("@")) {
			mEmailView.setError(getString(R.string.error_invalid_email));
			focusView = mEmailView;
			cancel = true;
		} 
		
		if (cancel) {
			focusView.requestFocus();
		} else {
			mLoginStatusMessageView.setText(R.string.login_progress_signing_in);
			showProgress(true);
			mAuthTask = new UserLoginTask();
			mAuthTask.execute((Void) null);
		}
	}

	/**
	 * Shows the progress UI and hides the login form.
	 */
	@TargetApi(Build.VERSION_CODES.HONEYCOMB_MR2)
	private void showProgress(final boolean show) {
		// On Honeycomb MR2 we have the ViewPropertyAnimator APIs, which allow
		// for very easy animations. If available, use these APIs to fade-in
		// the progress spinner.
		if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.HONEYCOMB_MR2) {
			int shortAnimTime = getResources().getInteger(
					android.R.integer.config_shortAnimTime);

			mLoginStatusView.setVisibility(View.VISIBLE);
			mLoginStatusView.animate().setDuration(shortAnimTime)
					.alpha(show ? 1 : 0)
					.setListener(new AnimatorListenerAdapter() {
						@Override
						public void onAnimationEnd(Animator animation) {
							mLoginStatusView.setVisibility(show ? View.VISIBLE
									: View.GONE);
						}
					});

			mLoginFormView.setVisibility(View.VISIBLE);
			mLoginFormView.animate().setDuration(shortAnimTime)
					.alpha(show ? 0 : 1)
					.setListener(new AnimatorListenerAdapter() {
						@Override
						public void onAnimationEnd(Animator animation) {
							mLoginFormView.setVisibility(show ? View.GONE
									: View.VISIBLE);
						}
					});
		} else {
			// The ViewPropertyAnimator APIs are not available, so simply show
			// and hide the relevant UI components.
			mLoginStatusView.setVisibility(show ? View.VISIBLE : View.GONE);
			mLoginFormView.setVisibility(show ? View.GONE : View.VISIBLE);
		}
	}

	public void checkLogin()
	{
		SharedPreferences sharedPreferences = PreferenceManager
											  .getDefaultSharedPreferences(this);
		boolean isLogin = sharedPreferences.getBoolean(PREFERENCE_CONF.IS_LOGIN, false);
		if(isLogin == false)
			return;
		
		String memberId   = sharedPreferences.getString(PREFERENCE_CONF.MEM_ID, "");
		String memberName = sharedPreferences.getString(PREFERENCE_CONF.MEM_NAME, "");
		
		changeMemberActivity(memberId, memberName);
	}
	
	public void changeMemberActivity(String memberId, String memberName)
	{
		Intent memberActivity = new Intent(getBaseContext(), MemberActivity.class);
		try
		{
			replaceContentView("activity4", memberActivity);
		}
		catch(Exception e)
		{
			String msg = e.getMessage();
			Log.d("Change Activity", msg);
		}
		 
	}
	public void replaceContentView(String id, Intent newIntent) 
	{
		try
		{
			newIntent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
			startActivity(newIntent);
			overridePendingTransition (CONFIG.ACTIVITY_NO_ANIM, CONFIG.ACTIVITY_NO_ANIM);
		}
		catch(Exception e)
		{
			String msg = e.getMessage();
			Log.d("Change Activity", msg);
		}
	}
	
	public void savePreference(String key, String value)
	{
		SharedPreferences sharedPreferences = PreferenceManager
											  .getDefaultSharedPreferences(this);
		Editor editor = sharedPreferences.edit();
		editor.putString(key, value);
		editor.commit();
	}
	
	public void savePreferences(String key, boolean value) 
	{
		SharedPreferences sharedPreferences = PreferenceManager
											  .getDefaultSharedPreferences(this);
		Editor editor = sharedPreferences.edit();
		editor.putBoolean(key, value);
		editor.commit();
	}
	
	/**
	 * Represents an asynchronous login/registration task used to authenticate
	 * the user.
	 */
	public class UserLoginTask extends AsyncTask<Void, Void, Boolean> {
		@Override
		protected Boolean doInBackground(Void... params) {
			// TODO: attempt authentication against a network service.
//
//			try {
//				// Simulate network access.
//				Thread.sleep(2000);
//			} catch (InterruptedException e) {
//				return false;
//			}
//			
//			// Check login 
//			if(mEmail.equals(CONFIG.ADMIN_EMAIL) && mPassword.equals(CONFIG.ADMIN_EMAIL))
//				return true;
//			
//			memberDb memberLogin = MainActivity.memberHandle.getMember(mEmail, mPassword);
//			if(memberLogin.getID() != 0)
//				return true;
//			
//			// TODO: register the new account here. 
////			boolean isEmailExist = checkEmailExist();
////			if(isEmailExist == false)
////				return false;
//			
//			String other = "";
//			int lastAt = mEmail.lastIndexOf('@');
//			mName = mEmail.substring(0,lastAt);
//			memberDb member = new memberDb(mName, mEmail, mPassword, other);
//			int memberIdSave = MainActivity.memberHandle.addMember(member);
//			if (memberIdSave == -1)
//				return false;
//			return true;
			try {
			user = new memberDb();
			user.setEmail(mEmailView.getText().toString());
			user.setPassword(mPasswordView.getText().toString());
			HttpClient httpclient = new DefaultHttpClient();
			HttpPost httppost = new HttpPost("http://appsa1bizssg.org/register/login");
			ObjectMapper mapper = new ObjectMapper();
				String json = mapper.writeValueAsString(user);
				Log.d("JSSSSSSSSS", json);
				StringEntity entity = new StringEntity(json);
				entity.setContentType(new BasicHeader(HTTP.CONTENT_TYPE, "application/json"));
				httppost.setEntity(entity);
				HttpResponse response = httpclient.execute(httppost);
				HttpEntity resEntity = response.getEntity();
				if (resEntity != null) {
				    InputStream is = resEntity.getContent();
				    int status = jsonparser(readResponse(is));
				    return ( status==1 ? true : false);
				} else
					return false;
		} catch (ClientProtocolException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}  catch (JsonGenerationException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (JsonMappingException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}  catch (UnsupportedEncodingException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
			return false;
		}
		
		private String readResponse(InputStream is) throws IOException {
	        ByteArrayOutputStream bos = new ByteArrayOutputStream();
	        byte[] data = new byte[2048];
	        int len = 0;
	        while ((len = is.read(data, 0, data.length)) >= 0) {
	            bos.write(data, 0, len);
	        }
	        return new String(bos.toByteArray(), "UTF-8");
	    }
		
		public int jsonparser(String res) {
			try {
				JSONObject jsonobj = new JSONObject(res);
				user.setName(jsonobj.getString("name"));
				return (Integer)jsonobj.get("status");
			} catch (JSONException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
			return -1;
		}
		

		@Override
		protected void onPostExecute(final Boolean success) 
		{
			mAuthTask = null;
			showProgress(false);

			if (success) 
			{
				// close keyboard
				imm.hideSoftInputFromWindow(mEmailView.getWindowToken(), 0); 
				
				//memberDb memberLogin = MainActivity.memberHandle.getMember(mEmail, mPassword);
				// save Preference --> login state until user log out
				savePreference(PREFERENCE_CONF.MEM_ID, String.valueOf(user.getId()));
				savePreference(PREFERENCE_CONF.MEM_NAME, user.getName());
				savePreferences(PREFERENCE_CONF.IS_LOGIN, true);
				
				if(mEmail.equals(CONFIG.ADMIN_EMAIL) && mPassword.equals(CONFIG.ADMIN_PWD))
					savePreferences(PREFERENCE_CONF.IS_ADMIN, true);
				
				try
				{
					changeMemberActivity(String.valueOf(user.getId()), user.getName());
				}
				catch(Exception e)
				{
					String msg = e.getMessage();
					Log.d("Member Ac", msg);
				}
				//finish();
			} else {
//				mPasswordView
//						.setError(getString(R.string.error_incorrect_password));
//				mPasswordView.requestFocus();
				invalid.setVisibility(View.FOCUS_UP);
			}
		}

		@Override
		protected void onCancelled() {
			mAuthTask = null;
			showProgress(false);
		}
	}
	
//	public boolean checkEmailExist()
//	{
//		if (MainActivity.memberHandle.isEmailExist(mEmail))
//			return false;
//		return true;
//	}
	
	@Override
	public boolean onOptionsItemSelected(MenuItem item)
    {
        switch (item.getItemId())
        {
        
        case R.id.spinning:
            Intent spinningActivity = new Intent(getApplicationContext(), SpinningActivity.class);
            startActivity(spinningActivity);
            overridePendingTransition (CONFIG.ACTIVITY_NO_ANIM, CONFIG.ACTIVITY_NO_ANIM);
            return true;
        
        case R.id.home:
            Intent matchActivity = new Intent(getApplicationContext(), MatchActivity.class);
            startActivity(matchActivity);
            overridePendingTransition (CONFIG.ACTIVITY_NO_ANIM, CONFIG.ACTIVITY_NO_ANIM);
            return true;
            
        case R.id.leagueToday:
            Intent leagueActivity = new Intent(getApplicationContext(), TodayActivity.class);
            startActivity(leagueActivity);
            overridePendingTransition (CONFIG.ACTIVITY_NO_ANIM, CONFIG.ACTIVITY_NO_ANIM);
            return true;
 
        case R.id.member:
            Intent memberActivity;
            if(utilityData.isLogin(this) == true)
            	memberActivity = new Intent(getApplicationContext(), MemberActivity.class);
            else
            	memberActivity = new Intent(getApplicationContext(), LoginActivity.class);
            startActivity(memberActivity);
            overridePendingTransition (CONFIG.ACTIVITY_NO_ANIM, CONFIG.ACTIVITY_NO_ANIM);
            return true;
            
        case R.id.favourite:
            Intent favouriteActivity = new Intent(getBaseContext(), FavouriteActivity.class);
            startActivity(favouriteActivity);
            overridePendingTransition (CONFIG.ACTIVITY_NO_ANIM, CONFIG.ACTIVITY_NO_ANIM);
            return true;
            
//        case R.id.readDays:
//            Intent readDaysActivity = new Intent(getBaseContext(), ReadDayActitity.class);
//            startActivity(readDaysActivity);
//            overridePendingTransition (CONFIG.ACTIVITY_NO_ANIM, CONFIG.ACTIVITY_NO_ANIM);
//            return true;
 
        default:
            return super.onOptionsItemSelected(item);
        }
    }         
	
}

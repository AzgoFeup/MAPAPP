package com.azgo.mapapp;



import android.app.ProgressDialog;
import android.content.Intent;
import android.os.AsyncTask;
import android.os.Build;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import android.widget.ProgressBar;
import android.widget.Toast;

import com.facebook.AccessToken;
import com.facebook.CallbackManager;
import com.facebook.FacebookCallback;
import com.facebook.FacebookException;
import com.facebook.FacebookSdk;
import com.facebook.login.LoginResult;
import com.facebook.login.widget.LoginButton;
import com.google.android.gms.auth.api.Auth;
import com.google.android.gms.auth.api.signin.GoogleSignInAccount;
import com.google.android.gms.auth.api.signin.GoogleSignInOptions;
import com.google.android.gms.auth.api.signin.GoogleSignInResult;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthCredential;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FacebookAuthProvider;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.auth.GoogleAuthProvider;

public class mainLogin extends AppCompatActivity implements
        View.OnClickListener
{

    //General
    private String TAG = "Login Activity";
    private static final int RC_SIGN_IN = 9001;

    //Firebase
    private FirebaseAuth mAuth;
    private FirebaseAuth.AuthStateListener mAuthListener;

    private CallbackManager mCallbackManager;

    //google
    private GoogleApiClient mGoogleApiClient;

    //server
    private TCPClient mTcpClient;
    private static String Message;
    private static boolean messageReceived;
    private static boolean errorLogin;

    //MISC
    private ProgressBar mProgress;

    @Override
    public void onCreate(Bundle savedInstanceState) {

        super.onCreate(savedInstanceState);
        /* Load the view and display it */
        FacebookSdk.sdkInitialize(getApplicationContext());

        setContentView(R.layout.activity_login);
        //SERVER

        if(mTcpClient == null) {
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.HONEYCOMB) {
                new connectTask().executeOnExecutor(AsyncTask.THREAD_POOL_EXECUTOR, "");
            } else {
                new connectTask().execute("");
            }
        }

        //***///
        //findViewById(R.id.button_facebook_login).setOnClickListener(this);


/* TOGET: Hash code
        PackageInfo info;
        try {
            info = getPackageManager().getPackageInfo("com.azgo.mapapp", PackageManager.GET_SIGNATURES);
            for (Signature signature : info.signatures) {
                MessageDigest md;
                md = MessageDigest.getInstance("SHA");
                md.update(signature.toByteArray());
                String something = new String(Base64.encode(md.digest(), 0));
                //String something = new String(Base64.encodeBytes(md.digest()));
                Log.e("hash key", something);
            }
        } catch (PackageManager.NameNotFoundException e1) {
            Log.e("name not found", e1.toString());
        } catch (NoSuchAlgorithmException e) {
            Log.e("no such an algorithm", e.toString());
        } catch (Exception e) {
            Log.e("exception", e.toString());
        }
*/

        /**
         * FOR GOOGLE
         */
        findViewById(R.id.gmail_sign_in_button).setOnClickListener(this);
        findViewById(R.id.button_facebook_login).setOnClickListener(this);

        //new connectTask().execute(""); //AQUI?
        /*********************************
         *       FIREBASE
         ***************************/
        // [START initialize_auth]
        mAuth = FirebaseAuth.getInstance();
        // [END initialize_auth]

        // [START auth_state_listener]
        mAuthListener = new FirebaseAuth.AuthStateListener() {
            @Override
            public void onAuthStateChanged(@NonNull FirebaseAuth firebaseAuth) {
                FirebaseUser user = firebaseAuth.getCurrentUser();
                if (user != null) {
                    // User is signed in
                    Log.e(TAG, "onAuthStateChanged(): signed_in: userID: " + user.getUid());
                    goMainScreen();
                } else {
                    // User is signed out
                    Log.e(TAG, "onAuthStateChanged(): signed_out");
                }
                // ...
            }
        };

        /**************
         * FACEBOOK
         ************/

        // Initialize Facebook Login button
        mCallbackManager = CallbackManager.Factory.create();
        LoginButton loginButton = (LoginButton) findViewById(R.id.button_facebook_login);
        loginButton.setReadPermissions("email", "public_profile");
        loginButton.registerCallback(mCallbackManager, new FacebookCallback<LoginResult>() {

            @Override
            public void onSuccess(LoginResult loginResult) {
                Log.e(TAG, "facebook:onSuccess:" + loginResult);
                handleFacebookAccessToken(loginResult.getAccessToken());
            }

            @Override
            public void onCancel() {
                Log.e(TAG, "facebook:onCancel");
                // [START_EXCLUDE]

                // [END_EXCLUDE]
            }

            @Override
            public void onError(FacebookException error) {
                Log.e(TAG, "facebook:onError", error);
                // [START_EXCLUDE]

                // [END_EXCLUDE]
            }
        });
        // [END initialize_fblogin]

        /**
         *  GMAIL
         */

        // [START config_signin]
        // Configure Google Sign In
        GoogleSignInOptions gso = new GoogleSignInOptions.Builder(GoogleSignInOptions.DEFAULT_SIGN_IN)
                .requestIdToken(getString(R.string.default_web_client_id))
                .requestEmail()
                .build();
        // [END config_signin]

        mGoogleApiClient = new GoogleApiClient.Builder(this)
                .enableAutoManage(this,null) //TODO: Ver como resolver isto
                .addApi(Auth.GOOGLE_SIGN_IN_API, gso)
                .build();



    }





    // [START auth_with_facebook]
    private void handleFacebookAccessToken(AccessToken token) {
        Log.d(TAG, "handleFacebookAccessToken:" + token);
        // [START_EXCLUDE silent]
        //showProgressDialog();
        // [END_EXCLUDE]
        Log.d(TAG, "FACEBOOK: signInWithCredential:onComplete:STARTING");
        AuthCredential credential = FacebookAuthProvider.getCredential(token.getToken());
        mAuth.signInWithCredential(credential)
                .addOnCompleteListener(this, new OnCompleteListener<AuthResult>() {
                    @Override
                    public void onComplete(@NonNull Task<AuthResult> task) {
                        Log.d(TAG, "FACEBOOK: signInWithCredential:onComplete:" + task.isSuccessful());

                        // If sign in fails, display a message to the user. If sign in succeeds
                        // the auth state listener will be notified and logic to handle the
                        // signed in user can be handled in the listener.
                        if (!task.isSuccessful()) {
                            Log.w(TAG, "signInWithCredential", task.getException());
                            Toast.makeText(mainLogin.this, "Authentication failed.",
                                    Toast.LENGTH_SHORT).show();
                        }
                        else goMainScreen();
                        // [START_EXCLUDE]
                        //hideProgressDialog();
                        // [END_EXCLUDE]
                    }
                });
    }
    // [END auth_with_facebook]

    private void firebaseAuthWithGoogle(GoogleSignInAccount acct) {
        Log.e(TAG, "firebaseAuthWithGoogle:" + acct.getId());

        AuthCredential credential = GoogleAuthProvider.getCredential(acct.getIdToken(), null);
        mAuth.signInWithCredential(credential)
                .addOnCompleteListener(this, new OnCompleteListener<AuthResult>() {
                    @Override
                    public void onComplete(@NonNull Task<AuthResult> task) {
                        Log.e(TAG, "signInWithCredential:onComplete:" + task.isSuccessful());

                        // If sign in fails, display a message to the user. If sign in succeeds
                        // the auth state listener will be notified and logic to handle the
                        // signed in user can be handled in the listener.
                        if (!task.isSuccessful()) {
                            Log.e(TAG, "signInWithCredential", task.getException());
                            Toast.makeText(mainLogin.this, "Authentication failed.",
                                    Toast.LENGTH_SHORT).show();

                        }

                        else {
                            Log.e(TAG, "LOGINGOGGLE: DONE");
                            goMainScreen();
                        }
                        // ...
                    }
                });
    }



    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        //Log.e(TAG, "onActivityResult() "+requestCode);

        // Result returned from launching the Intent from GoogleSignInApi.getSignInIntent(...);
        if (requestCode == RC_SIGN_IN) {
            Log.e(TAG, "LOGINGOGGLE: toCheck");
            GoogleSignInResult result = Auth.GoogleSignInApi.getSignInResultFromIntent(data);
            Log.e(TAG, "LOGINGOGGLE: checking");
            if (result.isSuccess()) {
                Log.e(TAG, "LOGINGOGGLE: True");
                // Google Sign In was successful, authenticate with Firebase
                GoogleSignInAccount account = result.getSignInAccount();
                firebaseAuthWithGoogle(account);
                Log.e(TAG, "ACCOUNT: "+ account.toString());
            } else {
                Log.e(TAG, "LOGINGOOGLE: False");
                Toast.makeText(mainLogin.this, "Authentication failed.",
                        Toast.LENGTH_SHORT).show();

            }
        }else{
            //If not request code is RC_SIGN_IN it must be facebook
            mCallbackManager.onActivityResult(requestCode, resultCode, data);
        }
    }


    private void signIn() {
        Log.e(TAG, "signIn(): GOOGLE");
        Intent signInIntent = Auth.GoogleSignInApi.getSignInIntent(mGoogleApiClient);
        startActivityForResult(signInIntent, RC_SIGN_IN);
        Log.e("mainLogin ","signIn() END");
    }

    @Override
    public void onStart() {
        super.onStart();
        Log.e("mainLogin", "onStart()");
        mAuth.addAuthStateListener(mAuthListener);
        Log.e("mainLogin", "onStart() END");
    }

    @Override
    public void onStop() {
        super.onStop();
        Log.e("mainLogin","onStop()");
        if (mAuthListener != null) {
            mAuth.removeAuthStateListener(mAuthListener);
        }
        Log.e("mainLogin","onStop() END");
    }


    public void onClick(View v) {
        int i = v.getId();
        Log.d(TAG,"onClick"+i);
        if (i == R.id.gmail_sign_in_button) {
            signIn();
        }
        else if (i == R.id.buttonDebug){
            Intent intent = new Intent(mainLogin.this, MainActivity.class);
            intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_CLEAR_TASK | Intent.FLAG_ACTIVITY_NEW_TASK);
            startActivity(intent);
        }
    }

    private void goMainScreen() {

        Log.e("mainLogin", "goMainScreen()");

        if(mTcpClient == null)  {
            Log.e("mainLogin", "goMainScreen(): mTcpClient == null");
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.HONEYCOMB) {
                new connectTask().executeOnExecutor(AsyncTask.THREAD_POOL_EXECUTOR, "");
            } else {
                new connectTask().execute("");
            }
        }

        Log.e("mainLogin", "Connecting...");
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.HONEYCOMB ) {
            new login().executeOnExecutor(AsyncTask.THREAD_POOL_EXECUTOR, "");
        } else {
            new login().execute("");
        }


    }

    public class connectTask extends AsyncTask<String,String,TCPClient> {

        @Override
        protected TCPClient doInBackground(String... message) {

            //we create a TCPClient object and
            mTcpClient = TCPClient.getInstance();

            //espera enquanto nao recebe nada
            while(!mTcpClient.messageAdded);

            Log.e("mainLogin", "connectTask: mTcpClient.array= " + mTcpClient.array.peek());

            publishProgress(mTcpClient.array.remove());
            mTcpClient.messageAdded = false;
            return null;
        }

        @Override
        protected void onProgressUpdate(String... values) {
            super.onProgressUpdate(values);

            //in the arrayList we add the messaged received from server
            Log.d("onProgress", values[0]);
            Message = values[0];
            messageReceived = true;
            // notify the adapter that the data set has changed. This means that new message received
            // from server was added to the list


        }
    }

    public class login extends AsyncTask<String,String,String> {

        private final ProgressDialog dialog = new ProgressDialog(mainLogin.this);

        @Override
        protected String doInBackground(String... message) {

            //Log.e("mainLogin", "login(): Entering while");
            while(true){
                Log.e("mainLogin", "login()");
                if(mTcpClient == null){
                    return "False";
                }

                //TODO: O que enviar para o server?
                mTcpClient.sendMessage("Login@delfim");

                // Waits for the server response
                while(!messageReceived);
                messageReceived = false;

                //Login Done?
                if(Message.equals("ok")) break;
            }

            return "True";
        }

        @Override
        protected void onPreExecute() {
            super.onPreExecute();
            this.dialog.setMessage("Processing...");
            this.dialog.show();
        }

        @Override
        protected void onPostExecute(String value) {
            super.onPostExecute(value);

            if(value.equals("True")) {
                this.dialog.dismiss();
                Intent intent = new Intent(mainLogin.this, MainActivity.class);
                intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_CLEAR_TASK | Intent.FLAG_ACTIVITY_NEW_TASK);
                startActivity(intent);
                if(this.isCancelled()) cancel(true);
            }
            else {
                Log.e("mainLogin", "login() mTcpClient == null");
            }
        }
    }
}


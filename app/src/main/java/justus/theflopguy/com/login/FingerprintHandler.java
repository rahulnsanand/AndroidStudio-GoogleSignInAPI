package justus.theflopguy.com.login;

import android.annotation.TargetApi;
import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.hardware.fingerprint.FingerprintManager;
import android.media.Image;
import android.os.Build;
import android.os.CancellationSignal;
import android.os.Handler;
import android.support.v4.content.ContextCompat;
import android.widget.ImageView;
import android.widget.TextView;

import static android.support.v4.content.ContextCompat.startActivity;

/**
 * Created by TheFlopGuy on 01-09-18.
 */

@TargetApi(Build.VERSION_CODES.M)
public class FingerprintHandler extends FingerprintManager.AuthenticationCallback{

    private Context context;

    public FingerprintHandler(Context context){

        this.context = context;

    }

    public void startAuth(FingerprintManager fingerprintManager, FingerprintManager.CryptoObject cryptoObject){

        CancellationSignal cancellationSignal = new CancellationSignal();

        fingerprintManager.authenticate(cryptoObject,cancellationSignal ,0, this, null);


    }

    @Override
    public void onAuthenticationError(int errorCode, CharSequence errString) {
        super.onAuthenticationError(errorCode, errString);

        this.update("There was an Auth Error"+ errString, false);

    }

    @Override
    public void onAuthenticationFailed() {
        super.onAuthenticationFailed();

        this.update("Auth Failed",false);

    }

    @Override
    public void onAuthenticationHelp(int helpCode, CharSequence helpString) {
        super.onAuthenticationHelp(helpCode, helpString);

        this.update("Error"+ helpString,false);
    }

    @Override
    public void onAuthenticationSucceeded(FingerprintManager.AuthenticationResult result) {
        super.onAuthenticationSucceeded(result);

        this.update("You can now access the App", true);
        ((Activity) context).finish();
        Intent intent = new Intent(context, ChatScreen.class);
        context.startActivity(intent);



    }


    private void update(String s, boolean b) {
        TextView paraLabel = (TextView) ((Activity)context).findViewById((R.id.paraLabel));
        ImageView imageView = (ImageView) ((Activity)context).findViewById((R.id.fingerprintImage));

        paraLabel.setText(s);
        if(b==false){
            paraLabel.setTextColor(ContextCompat.getColor(context,R.color.colorAccent));

        }
        else{
            paraLabel.setTextColor(ContextCompat.getColor(context,R.color.colorPrimary));
            imageView.setImageResource(R.mipmap.action_done);


        }

    }
}

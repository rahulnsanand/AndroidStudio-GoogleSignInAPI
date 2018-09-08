package justus.theflopguy.com.login;

import android.Manifest;
import android.annotation.TargetApi;
import android.app.KeyguardManager;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.hardware.fingerprint.FingerprintManager;
import android.net.Uri;
import android.os.Build;
import android.os.Handler;
import android.security.keystore.KeyGenParameterSpec;
import android.security.keystore.KeyPermanentlyInvalidatedException;
import android.security.keystore.KeyProperties;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import java.io.IOException;
import java.security.InvalidAlgorithmParameterException;
import java.security.InvalidKeyException;
import java.security.KeyStore;
import java.security.KeyStoreException;
import java.security.NoSuchAlgorithmException;
import java.security.NoSuchProviderException;
import java.security.UnrecoverableKeyException;
import java.security.cert.CertificateException;

import javax.crypto.Cipher;
import javax.crypto.KeyGenerator;
import javax.crypto.NoSuchPaddingException;
import javax.crypto.SecretKey;

import static android.support.v4.content.ContextCompat.startActivity;

public class FingerPrint extends AppCompatActivity {

    private TextView mHeadingLabel;
    private ImageView mFingerprintImage;
    private TextView mParaLabel;
    private FingerprintManager fingerprintManager;
    private KeyguardManager keyguardManager;
    private KeyStore keyStore;
    private Cipher cipher;
    private String KEY_NAME = "AndroidKey";


    @Override
    protected void onCreate(Bundle savedInstanceState) {

   super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_finger_print);

        mHeadingLabel = (TextView) findViewById(R.id.headingLabel);
        mFingerprintImage = (ImageView) findViewById(R.id.fingerprintImage);
        mParaLabel = (TextView) findViewById(R.id.paraLabel);

        //TODO Check 1: Android Version should be Greater or eqial to Marshmallow
        //TODO Check 2: Device has a fingerprint Scanner
        //TODO Check 3: Have Permission to use fingerprint scanner in the app
        //TODO Check 4: Lock Screen is secured with atleast 1 type of lock
        //ToDO Check 5: Atleast 1 fingerprint is registered in the device

        if(Build.VERSION.SDK_INT >= Build.VERSION_CODES.M){

            fingerprintManager = (FingerprintManager) getSystemService(FINGERPRINT_SERVICE);
            keyguardManager=(KeyguardManager) getSystemService(KEYGUARD_SERVICE);

            if(fingerprintManager.isHardwareDetected()){

                if(ContextCompat.checkSelfPermission(this, Manifest.permission.USE_FINGERPRINT)== PackageManager.PERMISSION_GRANTED){

                    if(keyguardManager.isKeyguardSecure()){

                        if(fingerprintManager.hasEnrolledFingerprints()){

                            mParaLabel.setText("Place Your finger on Scanner to gain Access");
                            generateKey();
                            if(cipherInit()){

                                new Handler().postDelayed(new Runnable() {
                                    @Override
                                    public void run() {
                                        // Magic here

                                    }
                                }, 5000);
                                FingerprintManager.CryptoObject cryptoObject = new FingerprintManager.CryptoObject(cipher);
                                FingerprintHandler fingerprintHandler=new FingerprintHandler(this);
                                fingerprintHandler.startAuth(fingerprintManager, cryptoObject);


                            }



                        }
                        else{
                            mParaLabel.setText("You haven't registered your finger.");
                            startActivity(new Intent(FingerPrint.this,ChatScreen.class));

                        }

                    }
                    else{
                        mParaLabel.setText("Add some lock to your phone dude. Jeez");
                        startActivity(new Intent(FingerPrint.this,ChatScreen.class));
                    }
                }
                else{

                    mParaLabel.setText("Permission Not Granted");
                    startActivity(new Intent(FingerPrint.this,ChatScreen.class));
                }
            }
            else{
                //IF FINGERPRINT DO NOT EXIST
                mParaLabel.setText("FingerPrint Scanner Nathi Che Bakka");
                 startActivity(new Intent(FingerPrint.this,ChatScreen.class));
            }
        }
        else{

            //IF BUILD AINT GOOD
            mParaLabel.setText("Too old. Phone too old.");
            startActivity(new Intent(FingerPrint.this,ChatScreen.class));
        }




    }

    @TargetApi(Build.VERSION_CODES.M)
    private void generateKey(){
        try{
            keyStore = KeyStore.getInstance("AndroidKeyStore");
            KeyGenerator keyGenerator = KeyGenerator.getInstance(KeyProperties.KEY_ALGORITHM_AES, "AndroidKeyStore");
            keyStore.load(null);
            keyGenerator.init(new KeyGenParameterSpec.Builder(KEY_NAME,KeyProperties.PURPOSE_ENCRYPT|KeyProperties.PURPOSE_DECRYPT).setBlockModes(KeyProperties.BLOCK_MODE_CBC)
            .setUserAuthenticationRequired(true)
            .setEncryptionPaddings(
                    KeyProperties.ENCRYPTION_PADDING_PKCS7)
                    .build());
            keyGenerator.generateKey();
        }
        catch(KeyStoreException | IOException | CertificateException | NoSuchAlgorithmException | InvalidAlgorithmParameterException
                | NoSuchProviderException e){
            e.printStackTrace();
        }
    }

    @TargetApi(Build.VERSION_CODES.M)
    public boolean cipherInit(){
        try{
            cipher = Cipher.getInstance(KeyProperties.KEY_ALGORITHM_AES + "/" + KeyProperties.BLOCK_MODE_CBC + "/" + KeyProperties.ENCRYPTION_PADDING_PKCS7);
        }
        catch (NoSuchAlgorithmException | NoSuchPaddingException e){
            throw new RuntimeException("Failed to get Cipher",e);
        }
        try {
            keyStore.load(null);
            SecretKey key = (SecretKey) keyStore.getKey(KEY_NAME, null);
            cipher.init(Cipher.ENCRYPT_MODE, key);
            return true;
        }
        catch (KeyPermanentlyInvalidatedException e){
            return false;

        }
        catch (KeyStoreException | CertificateException | UnrecoverableKeyException | IOException | NoSuchAlgorithmException | InvalidKeyException e){
            throw new RuntimeException("Failed To Init Cipher",e);
        }
    }


}

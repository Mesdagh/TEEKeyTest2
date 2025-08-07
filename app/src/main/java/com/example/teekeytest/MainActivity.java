package com.example.teekeytest;

import android.os.Bundle;
import android.security.keystore.KeyGenParameterSpec;
import android.security.keystore.KeyProperties;
import android.util.Base64;
import android.widget.Button;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;
import androidx.biometric.BiometricPrompt;
import androidx.core.content.ContextCompat;

import java.nio.charset.StandardCharsets;
import java.security.KeyPair;
import java.security.KeyPairGenerator;
import java.security.KeyStore;
import java.security.PublicKey;
import java.security.Signature;
import java.util.concurrent.Executor;

public class MainActivity extends AppCompatActivity {

    private static final String KEY_ALIAS = "MySecureKey";
    private TextView outputView;
    private Executor executor;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        outputView = findViewById(R.id.outputView);
        executor = ContextCompat.getMainExecutor(this);

        Button genKeyBtn = findViewById(R.id.btnGenerateKey);
        Button signBtn = findViewById(R.id.btnSign);

        genKeyBtn.setOnClickListener(v -> generateKey());
        signBtn.setOnClickListener(v -> authenticateAndSign());
    }

    private void generateKey() {
        try {
            KeyPairGenerator kpg = KeyPairGenerator.getInstance(
                    KeyProperties.KEY_ALGORITHM_RSA, "AndroidKeyStore");

            kpg.initialize(new KeyGenParameterSpec.Builder(
                    KEY_ALIAS,
                    KeyProperties.PURPOSE_SIGN | KeyProperties.PURPOSE_VERIFY)
                    .setDigests(KeyProperties.DIGEST_SHA256)
                    .setSignaturePaddings(KeyProperties.SIGNATURE_PADDING_RSA_PKCS1)
                    .setUserAuthenticationRequired(true)
                    .build()
            );

            KeyPair kp = kpg.generateKeyPair();
            PublicKey publicKey = kp.getPublic();
            String pubKeyBase64 = Base64.encodeToString(publicKey.getEncoded(), Base64.DEFAULT);

            outputView.setText("Key generated.\n\nPublic Key:\n" + pubKeyBase64);

        } catch (Exception e) {
            outputView.setText("Error: " + e.getMessage());
        }
    }

    private void authenticateAndSign() {
        BiometricPrompt biometricPrompt = new BiometricPrompt(this, executor,
                new BiometricPrompt.AuthenticationCallback() {
                    @Override
                    public void onAuthenticationSucceeded(
                            BiometricPrompt.AuthenticationResult result) {
                        super.onAuthenticationSucceeded(result);
                        signMessage();
                    }

                    @Override
                    public void onAuthenticationError(int errorCode, CharSequence errString) {
                        super.onAuthenticationError(errorCode, errString);
                        outputView.setText("Auth error: " + errString);
                    }
                });

        BiometricPrompt.PromptInfo promptInfo = new BiometricPrompt.PromptInfo.Builder()
                .setTitle("Biometric Authentication")
                .setSubtitle("Confirm fingerprint to sign")
                .setNegativeButtonText("Cancel")
                .build();

        biometricPrompt.authenticate(promptInfo);
    }

    private void signMessage() {
        try {
            KeyStore ks = KeyStore.getInstance("AndroidKeyStore");
            ks.load(null);
            KeyStore.Entry entry = ks.getEntry(KEY_ALIAS, null);
            if (!(entry instanceof KeyStore.PrivateKeyEntry)) {
                outputView.setText("No private key found.");
                return;
            }

            Signature s = Signature.getInstance("SHA256withRSA");
            s.initSign(((KeyStore.PrivateKeyEntry) entry).getPrivateKey());
            byte[] message = "Test Message".getBytes(StandardCharsets.UTF_8);
            s.update(message);
            byte[] signature = s.sign();
            String signatureBase64 = Base64.encodeToString(signature, Base64.DEFAULT);

            outputView.setText("Message: Test Message\n\nSignature:\n" + signatureBase64);

        } catch (Exception e) {
            outputView.setText("Sign error: " + e.getMessage());
        }
    }
}

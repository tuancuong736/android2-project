package com.example.chatbot;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;

import android.Manifest;
import android.content.pm.PackageManager;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

import ai.api.AIListener;
import ai.api.android.AIConfiguration;
import ai.api.android.AIService;
import ai.api.model.AIError;
import ai.api.model.AIResponse;
import ai.api.model.Result;

public class MainActivity extends AppCompatActivity implements AIListener {
    private AIService aiService;
    private TextView mTextView;

    private int RECORD_REQUEST_CODE = 101;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        //Find view by id
        Button mButton = findViewById(R.id.btnTest);
        mTextView = findViewById(R.id.tvTest);
        //Set permission
        int permission = ContextCompat.checkSelfPermission(this, Manifest.permission.RECORD_AUDIO);
        //Check permission
        if (permission != PackageManager.PERMISSION_GRANTED) {
            Log.e("Error", "Permission to record denied");
            makeRequest();
        }
        //Create an instance of AIConfiguration, specifying the access token, locale, and recognition engine
        final AIConfiguration config = new AIConfiguration("1625f31aed43c0a128bf600ef7528d125a03da8c",
                AIConfiguration.SupportedLanguages.English,
                AIConfiguration.RecognitionEngine.System);
        //Use the AIConfiguration object to get a reference to the AIService, which will make the query requests
        aiService = AIService.getService(this, config);
        //Set Event
        mButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                aiService.startListening();
            }
        });
        //Set AIListener instance for the AIService instance
        aiService.setListener(this);
    }

    @Override
    public void onResult(AIResponse result) {
        Log.e("Anu", result.toString());
        Result result1 = result.getResult();
        mTextView.setText("Query: " + result1.getResolvedQuery() + ", Action: " + result1.getAction());
    }

    @Override
    public void onError(AIError error) {

    }

    @Override
    public void onAudioLevel(float level) {

    }

    @Override
    public void onListeningStarted() {

    }

    @Override
    public void onListeningCanceled() {

    }

    @Override
    public void onListeningFinished() {

    }

    protected void makeRequest() {
        ActivityCompat.requestPermissions(this, new String[] {Manifest.permission.RECORD_AUDIO}, RECORD_REQUEST_CODE);
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        if (requestCode == RECORD_REQUEST_CODE) {
            if (grantResults.length == 0 || grantResults[0] != PackageManager.PERMISSION_GRANTED) {

            } else {

            }
        }
    }
}
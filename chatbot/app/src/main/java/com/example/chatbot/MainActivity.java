package com.example.chatbot;

import androidx.appcompat.app.AppCompatActivity;

import android.Manifest;
import android.content.res.AssetManager;
import android.os.Bundle;
import android.os.Environment;
import android.text.TextUtils;
import android.view.View;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.Toast;

import com.example.chatbot.Adapter.ChatMessageAdapter;
import com.example.chatbot.Model.ChatMessage;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.karumi.dexter.Dexter;
import com.karumi.dexter.MultiplePermissionsReport;
import com.karumi.dexter.PermissionToken;
import com.karumi.dexter.listener.DexterError;
import com.karumi.dexter.listener.PermissionRequest;
import com.karumi.dexter.listener.PermissionRequestErrorListener;
import com.karumi.dexter.listener.multi.MultiplePermissionsListener;

import org.alicebot.ab.AIMLProcessor;
import org.alicebot.ab.Bot;
import org.alicebot.ab.Chat;
import org.alicebot.ab.MagicStrings;
import org.alicebot.ab.PCAIMLProcessorExtension;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

public class MainActivity extends AppCompatActivity {

    private ListView listView;
    private EditText editTextMessage;

    public static Chat chat;
    private ChatMessageAdapter adapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        listView = findViewById(R.id.mainListView);
        ImageView imageView = findViewById(R.id.mainImageView);
        editTextMessage = findViewById(R.id.mainEditTextMessage);
        FloatingActionButton btnSend = findViewById(R.id.mainButtonSend);

        adapter = new ChatMessageAdapter(this, new ArrayList<ChatMessage>());
        listView.setAdapter(adapter);

        btnSend.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                String message = editTextMessage.getText().toString();

                String response = chat.multisentenceRespond(editTextMessage.getText().toString());

                if (TextUtils.isEmpty(message)) {
                    Toast.makeText(MainActivity.this, "Please enter a query!", Toast.LENGTH_SHORT).show();
                } else {
                    sendMessage(message);
                    botReply(response);
                }

                //Clear Edit Text
                editTextMessage.setText("");
                listView.setSelection(adapter.getCount() - 1);
            }
        });

        checkMultiplePermission();
    }

    private void checkMultiplePermission() {
        Dexter.withContext(this)
                .withPermissions(
                        Manifest.permission.READ_EXTERNAL_STORAGE,
                        Manifest.permission.WRITE_EXTERNAL_STORAGE
                ).withListener(new MultiplePermissionsListener() {

            @Override
            public void onPermissionsChecked(MultiplePermissionsReport multiplePermissionsReport) {
                if (multiplePermissionsReport.areAllPermissionsGranted()) {
                    proceed();
                    Toast.makeText(MainActivity.this, "Permission granted!", Toast.LENGTH_SHORT).show();
                }
                if (multiplePermissionsReport.isAnyPermissionPermanentlyDenied()) {
                    Toast.makeText(MainActivity.this, "Please grant all permissions!", Toast.LENGTH_SHORT).show();
                }
            }

            @Override
            public void onPermissionRationaleShouldBeShown(List<PermissionRequest> list, PermissionToken permissionToken) {
                permissionToken.continuePermissionRequest();
            }
        }).withErrorListener(new PermissionRequestErrorListener() {
            @Override
            public void onError(DexterError dexterError) {
                Toast.makeText(MainActivity.this, "" + dexterError, Toast.LENGTH_SHORT).show();
            }
        }).onSameThread().check();
    }

    private void proceed() {
        boolean available = isSDCardAvailable();

        if (available) {
            AssetManager assetManager = getResources().getAssets();
            File fileName = new File(Environment.getExternalStorageState() + "/TBC/bots/SampleBot");

            boolean makeFile = fileName.mkdir();

            if (makeFile) {
                if (fileName.exists()) {

                    //Read the line

                    try {
                        for (String dir : Objects.requireNonNull(assetManager.list("SampleBot"))) {

                            File subDir = new File(fileName.getPath() + "/" + dir);

                            boolean surDirCheck = subDir.mkdir();

                            if (surDirCheck) {
                                for (String file : Objects.requireNonNull(assetManager.list("SampleBot/" + dir))) {

                                    File newFile = new File(fileName.getPath() + "/" + dir + "/" + file);

                                    if (newFile.exists()) {
                                        continue;
                                    }

                                    InputStream inputStream;
                                    OutputStream outputStream;
                                    String string;
                                    inputStream = assetManager.open("SampleBot/" + dir + "/" + file);
                                    outputStream = new FileOutputStream(fileName.getPath() + "/" + dir + "/" + file);

                                    //Copy files from assets to the mobile's sd card or any secondary memory available

                                    copyFile(inputStream, outputStream);
                                    inputStream.close();
                                    outputStream.flush();
                                    outputStream.close();
                                }
                            } else Toast.makeText(MainActivity.this, "Sub Directory failed check!", Toast.LENGTH_SHORT).show();
                        }
                    } catch (IOException e) {
                        e.printStackTrace();
                    }
                } else Toast.makeText(MainActivity.this, "File doesn't exists!", Toast.LENGTH_SHORT).show();
            } else Toast.makeText(MainActivity.this, "Can't make file!", Toast.LENGTH_SHORT).show();
        } else Toast.makeText(MainActivity.this, "SD Card isn't available!", Toast.LENGTH_SHORT).show();

        //Get the working directory
        MagicStrings.root_path = Environment.getExternalStorageState() + "/TBC";
        AIMLProcessor.extension = new PCAIMLProcessorExtension();

        Bot bot = new Bot("SampleBot", MagicStrings.root_path, "chat");
        chat = new Chat(bot);
    }

    private void copyFile(InputStream inputStream, OutputStream outputStream) throws IOException {
        byte[] buffer = new byte[1024];
        int read;

        while ((read = inputStream.read(buffer)) != -1) {
            outputStream.write(buffer, 0, read);
        }
    }

    public static boolean isSDCardAvailable() {
        return Environment.getExternalStorageState().equals(Environment.MEDIA_MOUNTED);
    }

    private void botReply(String response) {
        ChatMessage chatMessage = new ChatMessage(false, false, response);
        adapter.add(chatMessage);
    }

    private void sendMessage(String message) {
        ChatMessage chatMessage = new ChatMessage(false, true, message);
        adapter.add(chatMessage);
    }
}
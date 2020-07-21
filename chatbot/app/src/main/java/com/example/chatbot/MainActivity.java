package com.example.chatbot;

import androidx.appcompat.app.AppCompatActivity;

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
import java.util.Objects;

public class MainActivity extends AppCompatActivity {

    private ListView listView;
    private ImageView imageView;
    private EditText editTextMessage;
    private FloatingActionButton btnSend;

    private Bot bot;
    public static Chat chat;
    private ChatMessageAdapter adapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        listView = findViewById(R.id.mainListView);
        imageView = findViewById(R.id.mainImageView);
        editTextMessage = findViewById(R.id.mainEditTextMessage);
        btnSend = findViewById(R.id.mainButtonSend);

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

        boolean available = isSDCartAvailable();

        AssetManager assetManager = getResources().getAssets();
        File fileName = new File(Environment.getExternalStorageState().toString() + "/TBC/bots/TBC");

        boolean makeFile = fileName.mkdirs();

        if (fileName.exists()) {

            //Read the line
            
            try {
                for (String dir : Objects.requireNonNull(assetManager.list("TBC"))) {

                    File subDir = new File(fileName.getPath() + "/" + dir);

                    boolean surDirCheck = subDir.mkdirs();

                    for (String file : Objects.requireNonNull(assetManager.list("TBC/" + dir))) {

                        File newFile = new File(fileName.getPath() + "/" + dir + "/" + file);

                        if (newFile.exists()) {
                            continue;
                        }

                        InputStream inputStream;
                        OutputStream outputStream;
                        String string;
                        inputStream = assetManager.open("TBC/" + dir + "/" + file);
                        outputStream = new FileOutputStream(fileName.getPath() + "/" + dir + "/" + file);

                        //Copy files from assets to the mobile's sd card or any secondary memory available

                        copyFile(inputStream, outputStream);
                        inputStream.close();
                        outputStream.flush();
                        outputStream.close();
                    }
                }
            } catch (IOException e) {
                e.printStackTrace();
            }
        }

        //Get the working directory
        MagicStrings.root_path = Environment.getExternalStorageState().toString() + "/TBC";
        AIMLProcessor.extension = new PCAIMLProcessorExtension();

        bot = new Bot("TBC", MagicStrings.root_path, "chat");
        chat = new Chat(bot);
    }

    private void copyFile(InputStream inputStream, OutputStream outputStream) throws IOException {
        byte[] buffer = new byte[1024];
        int read;

        while ((read = inputStream.read(buffer)) != -1) {
            outputStream.write(buffer, 0, read);
        }
    }

    private static boolean isSDCartAvailable() {
        return Environment.getExternalStorageState().equals(Environment.MEDIA_MOUNTED)? true : false;
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
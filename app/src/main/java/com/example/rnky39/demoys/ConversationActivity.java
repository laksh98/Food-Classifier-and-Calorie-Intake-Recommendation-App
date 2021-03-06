package com.example.rnky39.demoys;

import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Color;
import android.os.AsyncTask;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;

import com.github.bassaer.chatmessageview.model.User;
import com.github.bassaer.chatmessageview.models.Message;
import com.github.bassaer.chatmessageview.views.ChatView;
import com.ibm.watson.developer_cloud.conversation.v1.Conversation;
import com.ibm.watson.developer_cloud.conversation.v1.model.InputData;
import com.ibm.watson.developer_cloud.conversation.v1.model.MessageOptions;
import com.ibm.watson.developer_cloud.conversation.v1.model.MessageResponse;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

public class ConversationActivity extends AppCompatActivity {

    public ChatView mChatView;
    boolean firstTime;
    MessageResponse response;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_conversation);

        // init
        final Conversation service = new Conversation(Conversation.VERSION_DATE_2017_05_26);
        service.setUsernameAndPassword("80ec54cb-14e5-4a6f-8611-8f6521da6708", "YcgYHANY4ioI");
        firstTime = true;

        //User id
        int myId = 0;
        //User icon
        Bitmap myIcon = BitmapFactory.decodeResource(getResources(), R.drawable.ic_launcher);
        //User name
        String myName = Tools.unFormatEmail(getIntent().getStringExtra("email"));

        int yourId = 1;
        Bitmap yourIcon = BitmapFactory.decodeResource(getResources(), R.drawable.ic_launcher);
        String yourName = "Calorie Bot";

        final User me = new User(myId, myName, myIcon);
        final User you = new User(yourId, yourName, yourIcon);

        mChatView = (ChatView) findViewById(R.id.chat_view);

        //Set UI parameters if you need
        mChatView.setRightBubbleColor(getColor(R.color.green500));
        mChatView.setLeftBubbleColor(Color.WHITE);
        mChatView.setBackgroundColor(getColor(R.color.blueGray500));
        mChatView.setSendButtonColor(getColor(R.color.cyan900));
        mChatView.setSendIcon(R.drawable.ic_action_send);
        mChatView.setRightMessageTextColor(Color.WHITE);
        mChatView.setLeftMessageTextColor(Color.BLACK);
        mChatView.setUsernameTextColor(Color.WHITE);
        mChatView.setSendTimeTextColor(Color.WHITE);
        mChatView.setDateSeparatorColor(Color.WHITE);
        mChatView.setInputTextHint("Hit me up!");
        mChatView.setMessageMarginTop(5);
        mChatView.setMessageMarginBottom(5);

        //Click Send Button
        mChatView.setOnClickSendButtonListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                //new message
                Message message = new Message.Builder()
                        .setUser(me)
                        .setRightMessage(true)
                        .setMessageText(mChatView.getInputText())
                        .hideIcon(true)
                        .build();
                //Set to chat view
                mChatView.send(message);
                //Reset edit text

                // watson bot
                InputData input = new InputData.Builder(mChatView.getInputText()).build();
                MessageOptions newMessageOptions;
                /*if (firstTime) {
                    newMessageOptions = new MessageOptions.Builder()
                            .workspaceId("abd8be92-fe92-444d-9b04-06e8c5e1f312")
                            .input(new InputData.Builder(mChatView.getInputText()).build())
                            .context(null)
                            .build();
                } else {*/
                    newMessageOptions = new MessageOptions.Builder()
                            .workspaceId("8da6bb18-3536-4d4d-864e-1be77f79c291")
                            .input(new InputData.Builder(mChatView.getInputText()).build())
                            .build();
                //}
                mChatView.setInputText("");
                NetworkStuff networkStuff = new NetworkStuff(service, newMessageOptions, you);
                networkStuff.execute();
            }

        });

    }

    public class NetworkStuff extends AsyncTask<Void, Void, Void> {

        Conversation service;
        MessageOptions options;
        User you;

        public NetworkStuff(Conversation service, MessageOptions options, User you) {
            this.service = service;
            this.options = options;
            this.you = you;
        }

        @Override
        protected Void doInBackground(Void... voids) {
            response = service.message(options).execute();
            return null;
        }

        @Override
        protected void onPreExecute() {
            super.onPreExecute();
        }

        @Override
        protected void onPostExecute(Void aVoid) {
            super.onPostExecute(aVoid);

            try {
                JSONObject jsonObject = new JSONObject(response.toString());
                //Receive message
                final Message receivedMessage = new Message.Builder()
                        .setUser(you)
                        .setRightMessage(false)
                        .setMessageText(new JSONArray(new JSONObject(jsonObject.get("output").toString()).get("text").toString()).get(0).toString())
                        .build();

                mChatView.receive(receivedMessage);
            } catch (JSONException e) {
                e.printStackTrace();
            }
        }
    }
}

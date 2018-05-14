package ggappsdev.skinsninja;

import android.os.Handler;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.Toolbar;
import android.text.format.DateFormat;
import android.view.View;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.firebase.ui.database.FirebaseListAdapter;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.FirebaseDatabase;
import com.squareup.picasso.Picasso;

import de.hdodenhof.circleimageview.CircleImageView;
import ggappsdev.Models.ChatModel;
import hani.momanii.supernova_emoji_library.Actions.EmojIconActions;
import hani.momanii.supernova_emoji_library.Helper.EmojiconEditText;
import hani.momanii.supernova_emoji_library.Helper.EmojiconTextView;

public class ChatRoom extends AppCompatActivity {

    private FirebaseListAdapter<ChatModel> adapter;
    RelativeLayout activity_chat;

    EmojiconEditText emojiconEditText;
    ImageView emojiButton, submitButton;
    EmojIconActions emojIconActions;
    FirebaseAuth mAuth;
    boolean canSendMessage = true;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_chat_room);







        mAuth = FirebaseAuth.getInstance();

        Toolbar toolbar = (Toolbar)findViewById(R.id.toolbar);
        TextView toolbartitle = (TextView)findViewById(R.id.toolbar_title);
        toolbartitle.setText("Chat Room");
        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayShowTitleEnabled(false);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setDisplayShowHomeEnabled(true);
        toolbar.setNavigationOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                finish();
            }
        });

        activity_chat = (RelativeLayout) findViewById(R.id.activity_chat);


        //Add Emoji
        emojiButton = (ImageView) findViewById(R.id.emoji_button);
        submitButton = (ImageView) findViewById(R.id.submit_button);
        emojiconEditText = (EmojiconEditText) findViewById(R.id.emojicon_edit_text);
        emojIconActions = new EmojIconActions(getApplicationContext(), activity_chat, emojiButton, emojiconEditText);
        emojIconActions.ShowEmojicon();






        submitButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if(canSendMessage == true) {
                    canSendMessage = false;
                    FirebaseDatabase.getInstance().getReference().child("Chat").push().setValue(new ChatModel(emojiconEditText.getText().toString(), mAuth.getCurrentUser().getDisplayName(), mAuth.getCurrentUser().getPhotoUrl().toString()));
                    emojiconEditText.setText("");
                    emojiconEditText.requestFocus();

                    Handler handler = new Handler();
                    handler.postDelayed(new Runnable() {
                        public void run() {
                            canSendMessage = true;

                        }
                    }, 5000);
                } else {

                    Toast.makeText(ChatRoom.this,
                            "Please wait 5 seconds before you send the next message", Toast.LENGTH_SHORT).show();
                }

            }
        });
        displayChatMessage();
    }
    private void displayChatMessage() {

        ListView listOfMessage = (ListView)findViewById(R.id.list_of_message);
        listOfMessage.setTranscriptMode(ListView.TRANSCRIPT_MODE_ALWAYS_SCROLL);
        listOfMessage.setStackFromBottom(true);
        adapter = new FirebaseListAdapter<ChatModel>(this,ChatModel.class,R.layout.chat_item,FirebaseDatabase.getInstance().getReference().child("Chat").limitToLast(50))
        {
            @Override
            protected void populateView(View v, ChatModel model, int position) {

                //Get references to the views of list_item.xml
                TextView messageText, messageUser, messageTime;
                CircleImageView messagePhoto;
                RelativeLayout chatbackground;

                messageText = (EmojiconTextView) v.findViewById(R.id.message_text);
                messageUser = (TextView) v.findViewById(R.id.message_user);
                messageTime = (TextView) v.findViewById(R.id.message_time);
                messagePhoto = (CircleImageView)v.findViewById(R.id.chat_photo);
                messageText.setText(model.getMessageText());
                messageUser.setText(model.getMessageUser());
                messageUser.setTextColor(getResources().getColor(R.color.white));

                if (model.getMessageUser().equals("skins ninja")){

                    messageUser.setText("Support");
                    messageUser.setTextColor(getResources().getColor(R.color.myGreenDark));

                }
                messageTime.setText(DateFormat.format("dd-MM-yyyy (HH:mm:ss)", model.getMessageTime()));
                Picasso.
                        get().
                        load(model.getMessagePhoto()).
                        into(messagePhoto);

            }
        };

        listOfMessage.setAdapter(adapter);
    }

}

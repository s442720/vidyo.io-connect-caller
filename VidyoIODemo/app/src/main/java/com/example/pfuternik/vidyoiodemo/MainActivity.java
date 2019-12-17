package com.example.pfuternik.vidyoiodemo;

import android.content.Intent;
import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.FrameLayout;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.vidyo.VidyoClient.Connector.ConnectorPkg;
import com.vidyo.VidyoClient.Connector.Connector;

public class MainActivity extends AppCompatActivity implements Connector.IConnect {

    Connector vc;
    FrameLayout videoFrame;
    Button start, stop;

    FirebaseAuth auth;
    DatabaseReference reference;

    String caller;
    String receiver = "GXWm6dpuKEZPZhTua2nLCf4rliB2";
    String token;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);


        String txt_email = "admin@gmail.com";
        String txt_password = "123456";
        auth = FirebaseAuth.getInstance();

        FirebaseUser firebaseUser = auth.getCurrentUser();


        login(txt_email, txt_password);
        

        firebaseUser = auth.getCurrentUser();
        caller = firebaseUser.getUid();

        ConnectorPkg.setApplicationUIContext(this);
        ConnectorPkg.initialize();
        videoFrame = (FrameLayout)findViewById(R.id.videoFrame);

        start = (Button) findViewById(R.id.start);
        stop = (Button) findViewById(R.id.stop);
        start.setVisibility(View.VISIBLE);
        stop.setVisibility(View.GONE);

        start.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                //when start is clicked show stop button and hide play button
                start.setVisibility(View.GONE);
                // create calling in firebase
                String token = GenerateToken.generateProvisionToken("4fab7666974c4271a89f93824feefa55", "user1" + "@" + "fc03ec.vidyo.io", "300", "");
                createCallInDB(caller, receiver, token);

                vc = new Connector(videoFrame, Connector.ConnectorViewStyle.VIDYO_CONNECTORVIEWSTYLE_Default, 15, "warning info@VidyoClient info@VidyoConnector", "", 0);
                vc.showViewAt(videoFrame, 0, 0, videoFrame.getWidth(), videoFrame.getHeight());
                vc.connect("prod.vidyo.io", token, "", "DemoRoom", MainActivity.this);
                stop.setVisibility(View.VISIBLE);
            }
        });

        stop.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // remove current data of call in firebase
                removeCallInDB();
                vc.disconnect();
            }
        });
    }

    public void login(String txt_email, String txt_password) {
        auth.signInWithEmailAndPassword(txt_email, txt_password)
                .addOnCompleteListener(new OnCompleteListener<AuthResult>() {
                    @Override
                    public void onComplete(@NonNull Task<AuthResult> task) {
                        if (task.isSuccessful()) {
                            Toast.makeText(MainActivity.this, "Login successful", Toast.LENGTH_SHORT).show();
                        }
                    }
                });
    }

    public void createCallInDB(String caller, String receiver, String token) {
        reference = FirebaseDatabase.getInstance().getReference("Call").child("call");

        // connected = 0 means the connection has not built yet
        Call data = new Call(caller, receiver, token, 0);

        reference.setValue(data).addOnCompleteListener(new OnCompleteListener<Void>() {
            @Override
            public void onComplete(@NonNull Task<Void> task) {
                if (!task.isSuccessful()) {
                    Toast.makeText(MainActivity.this, "Build failed", Toast.LENGTH_SHORT).show();
                }
            }
        });
    }

    public void removeCallInDB() {
        reference = FirebaseDatabase.getInstance().getReference("Call");
        reference.removeValue();
    }

    public void onSuccess() {}

    public void onFailure(Connector.ConnectorFailReason reason) {
        Intent intent = new Intent(getBaseContext(), MainActivity.class);
        startActivity(intent);
    }

    public void onDisconnected(Connector.ConnectorDisconnectReason reason) {
        Intent intent = new Intent(getBaseContext(), MainActivity.class);
        startActivity(intent);
    }
}

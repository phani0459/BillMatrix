package com.billmatrix;

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.text.Html;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.Button;
import android.widget.FrameLayout;
import android.widget.LinearLayout;
import android.widget.TextView;

public class ProfileActivity extends BaseTabActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setTextView(Html.fromHtml("<big> <big> <big> &#8250; </big> </big> </big>") + " Profile");
        addTabButtons(1, "Profile");

        View view = findViewById(R.id.child);

        view.setVisibility(View.VISIBLE);
    }

    @Override
    public void tabChanged(String selectedTab, boolean isInit) {
        /*if (selectedTab.equalsIgnoreCase("New")) {
            if (isInit) {
                getSupportFragmentManager().beginTransaction().add(R.id.content, new BlankFragment()).commit();
            } else {
                getSupportFragmentManager().beginTransaction().replace(R.id.content, new BlankFragment()).commit();
            }
        } else {
            if (isInit) {
                getSupportFragmentManager().beginTransaction().add(R.id.content, new Blank2Fragment()).commit();
            } else {
                getSupportFragmentManager().beginTransaction().replace(R.id.content, new Blank2Fragment()).commit();
            }
        }*/
    }


}

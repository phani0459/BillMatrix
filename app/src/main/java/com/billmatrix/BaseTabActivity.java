package com.billmatrix;

import android.content.Context;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.text.Html;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewTreeObserver;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.billmatrix.R;

import java.io.File;

import butterknife.BindView;
import butterknife.ButterKnife;

public abstract class BaseTabActivity extends AppCompatActivity {

    @BindView(R.id.navigateTextView)
    public TextView textView;
    @BindView(R.id.layout)
    public LinearLayout linearLayout;

    Context mContext;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_base_tab);

        ButterKnife.bind(this);
        mContext = this;

//        File file = new File(getExternalFilesDir(null), "pic");
    }

    public LinearLayout.LayoutParams getLayoutParams(int level) {
        final LinearLayout.LayoutParams layoutParams = new LinearLayout.LayoutParams(LinearLayout.LayoutParams.WRAP_CONTENT, LinearLayout.LayoutParams.WRAP_CONTENT);
        switch (level) {
            case 0:
                layoutParams.rightMargin = 12;
                break;
            case 1:
                layoutParams.rightMargin = 12;
                break;
        }
        return layoutParams;
    }

    public void addTabButtons(int n, String... names) {

        if (n > 0) {
            for (int i = 0; i < n; i++) {
                LayoutInflater layoutInflater = (LayoutInflater) this.getSystemService(LAYOUT_INFLATER_SERVICE);
                final Button button = (Button) layoutInflater.inflate(R.layout.tab_button, null);
                button.setText(names[i]);

                if (i != 0) {
                    button.getBackground().setLevel(1);
                    button.setTextColor(getResources().getColor(android.R.color.white));
                    button.setLayoutParams(getLayoutParams(1));
                } else {
                    button.getBackground().setLevel(0);
                    button.setTextColor(getResources().getColor(android.R.color.black));
                    button.setLayoutParams(getLayoutParams(0));
                    selectedTab = button.getText().toString();
                    tabChanged(button.getText().toString(), true);
                }
                button.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        if (!selectedTab.equalsIgnoreCase(button.getText().toString())) {
                            selectedTab = button.getText().toString();
                            if (linearLayout.getChildCount() > 0) {
                                for (int j = 0; j < linearLayout.getChildCount(); j++) {
                                    View view = linearLayout.getChildAt(j);
                                    view.getBackground().setLevel(1);
                                    ((Button) view).setTextColor(getResources().getColor(android.R.color.white));
                                    view.setLayoutParams(getLayoutParams(1));
                                }
                            }
                            v.getBackground().setLevel(0);
                            ((Button) v).setTextColor(getResources().getColor(android.R.color.black));
                            v.setLayoutParams(getLayoutParams(0));

                            tabChanged(((Button) v).getText().toString(), false);
                        }
                    }
                });
                linearLayout.addView(button);
            }
        }
    }

    String selectedTab;

    public abstract void tabChanged(String selectedTab, boolean isInit);

    public void setTextView(String msg) {
        this.textView.setText(Html.fromHtml(msg));
    }

    public String getArrowString() {
        return "<big><big><big> \u203A </big></big></big>";
    }

}

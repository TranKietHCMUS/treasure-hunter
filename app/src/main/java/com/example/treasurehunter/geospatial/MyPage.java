package com.example.treasurehunter.geospatial;

import com.example.treasurehunter.R;
import android.os.Bundle;
import android.widget.TextView;
import androidx.appcompat.app.AppCompatActivity;

public class MyPage extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        // Create a TextView programmatically
        TextView textView = new TextView(this);
        textView.setText(R.string.java_file_text);
        textView.setTextSize(24); // Set text size
        textView.setTextAlignment(TextView.TEXT_ALIGNMENT_CENTER); // Center text horizontally

        // Set the TextView as the content view
        setContentView(textView);
    }
}

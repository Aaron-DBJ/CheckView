package com.aaron_dbj.checkview;

import android.os.Bundle;
import android.view.View;
import android.widget.Toast;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import com.aaron_dbj.tickview.R;

public class MainActivity extends AppCompatActivity {

    CheckView tick;
    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        tick = findViewById(R.id.tick);
        tick.setOnCheckedChangeListener(new OnCheckedChangeListener() {
            @Override
            public void onCheckedChange(View view, boolean isChecked) {
                if(isChecked) {
                    Toast.makeText(MainActivity.this, "tick的点击事件", Toast.LENGTH_SHORT).show();
                }else {
                    Toast.makeText(MainActivity.this, "取消tick的点击事件", Toast.LENGTH_SHORT).show();
                }
            }
        });

    }
}

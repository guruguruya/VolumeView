package com.example.volumeview;

import android.app.Activity;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.TextView;

import com.example.volumeview.VolumeView;
import com.example.volumeview.VolumeView.MoveInterface;

public class MainActivity extends Activity implements MoveInterface {

    private TextView tvValue;
    private VolumeView moveView;
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_main);
        tvValue = (TextView) findViewById(R.id.tv_value);
        moveView = (VolumeView) findViewById(R.id.move_view);
        moveView.setMoveInterface(this);
	}
	
	@Override
    public void getCurrentDegrees(int degress) {
		//触屏时替换原来的文本
        tvValue.setText("当前音量：" + degress);
    }
}

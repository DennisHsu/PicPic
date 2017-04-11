package com.pingfun.picpic.view;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.ImageButton;

import com.pingfun.picpic.R;

public class HomeMenu extends Activity {

	private ImageButton collageBtn;
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
//		requestWindowFeature(Window.FEATURE_NO_TITLE);
//		getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN,
//				WindowManager.LayoutParams.FLAG_FULLSCREEN);
		
		setContentView(R.layout.activity_picfee_menu);

		collageBtn = (ImageButton) findViewById(R.id.CollageBtn);
		
		collageBtn.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View v) {
				Intent intent = new Intent(HomeMenu.this,
						MainActivity.class);
				startActivity(intent);
			}
		});

	}

}

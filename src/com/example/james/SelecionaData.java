package com.example.james;

import java.util.List;
import java.util.Locale;

import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.GoogleMap.OnMapClickListener;
import com.google.android.gms.maps.GoogleMap.OnMapLongClickListener;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.BitmapDescriptorFactory;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.MarkerOptions;
import com.google.android.maps.GeoPoint;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;

public class SelecionaData extends Activity {
	
	EditText etDataBusca;
	Button btDataBusca;
	
	String user;
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.seleciona_data);
		
		Intent i = getIntent();
		user = i.getStringExtra("user");
		
//		etData = (EditText)findViewById(R.id.etData);
//		etHora = (EditText)findViewById(R.id.etHora);
		etDataBusca = (EditText)findViewById(R.id.etDataBusca);
		btDataBusca = (Button)findViewById(R.id.btDataBusca);
		
		btDataBusca.setOnClickListener(new View.OnClickListener() {
			
			@Override
			public void onClick(View arg0) {
				// TODO Auto-generated method stub
				Intent buscarCarona = new Intent(SelecionaData.this, BuscarCarona.class);
				buscarCarona.putExtra("user", user);
				buscarCarona.putExtra("data", etDataBusca.getText().toString().trim());
				startActivity(buscarCarona);
				finish();
			}
		});
	}
}
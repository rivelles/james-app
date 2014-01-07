package com.example.james;

import java.util.ArrayList;

import org.apache.http.NameValuePair;
import org.apache.http.message.BasicNameValuePair;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.location.Criteria;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.os.Bundle;
import android.support.v4.app.FragmentActivity;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import com.example.james.conexaoweb.ConexaoHttpClient;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.BitmapDescriptorFactory;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.MarkerOptions;

public class AgendarCarona extends FragmentActivity implements LocationListener {
	
	TextView tvCarona;
	Button btConfirma;
	
	GoogleMap gMap;
	
	String user, carona;
	double start_lat, start_lon, finish_lat, finish_lon;
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.agendar_carona);
		
		tvCarona = (TextView)findViewById(R.id.tvCarona);
		btConfirma = (Button)findViewById(R.id.btConfirma);
		
		Intent i = getIntent();
		user = i.getStringExtra("user");
		carona = i.getStringExtra("carona");
		
		btConfirma.setOnClickListener(new View.OnClickListener() {
			
			@Override
			public void onClick(View v) {
				// TODO Auto-generated method stub
				ArrayList<NameValuePair> parametrosCadastro = new ArrayList<NameValuePair>();
				parametrosCadastro.add(new BasicNameValuePair("id_offered_route", carona));
				parametrosCadastro.add(new BasicNameValuePair("id_user", user));
				String response = "";
				try {
					response = ConexaoHttpClient.executaHttpPost("http://www.petshopcastelo.vet.br/james/insert_ride.php", parametrosCadastro);
					int ret = Integer.parseInt(response.toString().trim());
					if (ret == 1) {
						Toast.makeText(AgendarCarona.this, "Ok! Um e-mail foi enviado ao motorista com a requisição da carona :)", Toast.LENGTH_LONG).show();
						finish();
					}
					else {
						Toast.makeText(AgendarCarona.this, "Ocorreu um erro interno.", Toast.LENGTH_LONG).show();
					}
				}
				catch (Exception e) {
					Log.i("erro", e.getMessage());
				}
			}
		});
		
		ArrayList<NameValuePair> parametrosCarona = new ArrayList<NameValuePair>();
		parametrosCarona.add(new BasicNameValuePair("carona", carona));
		String response = "";
		String response2 = "";
		try {
			response = ConexaoHttpClient.executaHttpPost("http://www.petshopcastelo.vet.br/james/get_carona.php", parametrosCarona);
			String[] array = response.split(",");
			String user = array[0];
			start_lat = Double.parseDouble(array[1]);
			start_lon = Double.parseDouble(array[2]);
			String start_ref = array[3];
			finish_lat = Double.parseDouble(array[4]);
			finish_lon = Double.parseDouble(array[5]);
			String finish_ref = array[6];
			String time = array[7];
			String user_id = array[9];
			String valor = array[11];
			ArrayList<NameValuePair> parametrosRating = new ArrayList<NameValuePair>();
			parametrosRating.add(new BasicNameValuePair("user_id", user_id));
			response2 = response = ConexaoHttpClient.executaHttpPost("http://www.petshopcastelo.vet.br/james/get_rating.php", parametrosRating);
			String ret = "Usuário: "+user+"\nSaída de: "+start_ref+"\nChegada em: "+finish_ref+"\nHorário de saída: "+time+"\nAvaliação média: "+response2+"\nPreço: R$ "+valor;
			tvCarona.setText(ret);
			setUpMap();
		}
		catch (Exception e) {
			Log.i("Erro", e.getMessage());
		}
	}
	
	private void setUpMap() {
    	if (gMap == null) {
    		SupportMapFragment mapFragment = (SupportMapFragment) getSupportFragmentManager().findFragmentById(R.id.map);
    		gMap = mapFragment.getMap();
    		if (gMap != null) {
    			LocationManager lm = (LocationManager) getSystemService(Context.LOCATION_SERVICE);
    			String provider = lm.getBestProvider(new Criteria(), true);
    			if (provider == null) {
    				onProviderDisabled(provider);
    			}
    			Log.i("Provider", provider);
    			Location currentLocation = lm.getLastKnownLocation(provider);
    			currentLocation.setLatitude(start_lat);
    			currentLocation.setLongitude(start_lon);
    			if (currentLocation != null) {
    				onLocationChanged(currentLocation);
    			}
    		}
    	}
    }

	@Override
	public void onLocationChanged(Location location) {
		// TODO Auto-generated method stub
		LatLng inicio = new LatLng(location.getLatitude(), location.getLongitude());
		gMap.moveCamera(CameraUpdateFactory.newLatLng(inicio));
		gMap.animateCamera(CameraUpdateFactory.zoomTo(10));
		gMap.addMarker(new MarkerOptions()
							.position(inicio)
							.title("Partida")
							.icon(BitmapDescriptorFactory.defaultMarker(BitmapDescriptorFactory.HUE_YELLOW)));
		LatLng fim = new LatLng(finish_lat, finish_lon);
		gMap.addMarker(new MarkerOptions()
							.position(fim)
							.title("Destino")
							.icon(BitmapDescriptorFactory.defaultMarker(BitmapDescriptorFactory.HUE_GREEN)));
	}

	@Override
	public void onProviderDisabled(String arg0) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void onProviderEnabled(String arg0) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void onStatusChanged(String arg0, int arg1, Bundle arg2) {
		// TODO Auto-generated method stub
		
	}
}
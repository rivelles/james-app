package com.example.james;

import java.util.ArrayList;

import org.apache.http.NameValuePair;
import org.apache.http.message.BasicNameValuePair;

import com.google.android.gms.maps.CameraUpdateFactory;

import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.MapFragment;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.BitmapDescriptorFactory;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;

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
import android.widget.Toast;

import com.example.james.conexaoweb.ConexaoHttpClient;

public class Inicio extends FragmentActivity implements LocationListener  {
	GoogleMap gMap;
	Button btCadastrarCarona, btBuscarCarona;
	int clicks = 0;
	String user;
    @Override
    
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.inicio);
        btCadastrarCarona = (Button)findViewById(R.id.btCadastrarCarona);
        btBuscarCarona = (Button)findViewById(R.id.btBuscarCarona);
        
        Intent i = getIntent();
        user = i.getStringExtra("user");
        ArrayList<NameValuePair> parametrosRating = new ArrayList<NameValuePair>();
		parametrosRating.add(new BasicNameValuePair("user", user));
        String response = "";
        try {
        	response = ConexaoHttpClient.executaHttpPost("http://www.petshopcastelo.vet.br/james/verify_ride.php", parametrosRating);
			int ret = Integer.parseInt(response.toString().trim());
			if (ret > 0) {
				Intent rating = new Intent(Inicio.this, AvaliarUsuario.class);
				rating.putExtra("user", user);
				rating.putExtra("ride", response);
				startActivity(rating);
			}
			else {
				Log.i("Erro", response);
			}
        }
        catch (Exception e) {
        	Log.i("Erro", e.getMessage());
        }
        btCadastrarCarona.setOnClickListener(new View.OnClickListener() {
			
			@Override
			public void onClick(View v) {
				// TODO Auto-generated method stub
				//Log.i("user", user);
				Intent cadastrarCarona = new Intent(Inicio.this, CadastrarCarona.class);
				cadastrarCarona.putExtra("user", user);
				startActivity(cadastrarCarona);
			}
		});
        
        btBuscarCarona.setOnClickListener(new View.OnClickListener() {
			
			@Override
			public void onClick(View v) {
				// TODO Auto-generated method stub
				Intent buscarCarona = new Intent(Inicio.this, SelecionaData.class);
				buscarCarona.putExtra("user", user);
				startActivity(buscarCarona);
			}
		});
        
        setUpMap();
        
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
    			if (currentLocation != null) {
    				onLocationChanged(currentLocation);
    			}
    		}
    	}
    }

	@Override
	public void onLocationChanged(Location location) {
		// TODO Auto-generated method stub
		LatLng ll = new LatLng(location.getLatitude(), location.getLongitude());
		gMap.addMarker(new MarkerOptions().position(ll).title("Você está aqui").icon(BitmapDescriptorFactory.defaultMarker(BitmapDescriptorFactory.HUE_ORANGE)));
		gMap.moveCamera(CameraUpdateFactory.newLatLng(ll));
		gMap.animateCamera(CameraUpdateFactory.zoomTo(13));
		ArrayList<NameValuePair> parametrosDist = new ArrayList<NameValuePair>();
		parametrosDist.add(new BasicNameValuePair("lat", String.valueOf(location.getLatitude())));
		parametrosDist.add(new BasicNameValuePair("lon", String.valueOf(location.getLongitude())));
		parametrosDist.add(new BasicNameValuePair("user_id", user));
		String response = "";
		try {
			response = ConexaoHttpClient.executaHttpPost("http://www.petshopcastelo.vet.br/james/select_next_routes.php", parametrosDist);
			String pontos = response.toString().trim();
			String[] caronas = pontos.split(";");
			for (int i=0; i<caronas.length; i++) {
				String[] array = caronas[i].split(",");
				String user = array[0].substring(1);
				double start_lat = Double.parseDouble(array[1]);
				double start_lon = Double.parseDouble(array[2]);
				String start_ref = array[3];
				double finish_lat = Double.parseDouble(array[4]);
				double finish_lon = Double.parseDouble(array[5]);
				String finish_ref = array[6];
				String time = array[7];
				String id = array[9];
				int tipo = Integer.parseInt(array[11].substring(0, array[11].length()-1));
				LatLng inicio = new LatLng(start_lat, start_lon);
				if (tipo == 1) {
					gMap.addMarker(new MarkerOptions()
									.position(inicio)
									.title(id+"- "+user)
									.snippet("De "+start_ref+" até "+finish_ref+" às "+time)
									.icon(BitmapDescriptorFactory.defaultMarker(BitmapDescriptorFactory.HUE_RED)));
					LatLng fim = new LatLng(finish_lat, finish_lon);
					gMap.addMarker(new MarkerOptions()
									.position(fim)
									.title(id+"- "+user)
									.snippet("De "+start_ref+" até "+finish_ref+" às "+time)
									.icon(BitmapDescriptorFactory.defaultMarker(BitmapDescriptorFactory.HUE_ROSE)));
				}
				else if (tipo == 2) {
					gMap.addMarker(new MarkerOptions()
									.position(inicio)
									.title(id+"- "+user)
									.snippet("De "+start_ref+" até "+finish_ref+" às "+time)
									.icon(BitmapDescriptorFactory.defaultMarker(BitmapDescriptorFactory.HUE_YELLOW)));
					LatLng fim = new LatLng(finish_lat, finish_lon);
					gMap.addMarker(new MarkerOptions()
									.position(fim)
									.title(id+"- "+user)
									.snippet("De "+start_ref+" até "+finish_ref+" às "+time)
									.icon(BitmapDescriptorFactory.defaultMarker(BitmapDescriptorFactory.HUE_ORANGE)));
				}
				
			}
			gMap.setOnMarkerClickListener(new GoogleMap.OnMarkerClickListener() {
				
				@Override
				public boolean onMarkerClick(Marker marker) {
					// TODO Auto-generated method stub
					if (marker.getTitle().endsWith(" ") && clicks == 1) {
						String[] title = marker.getTitle().split("-");
						String id = title[0];
						Intent agendarCarona = new Intent(Inicio.this, AgendarCarona.class);
						agendarCarona.putExtra("user", user);
						agendarCarona.putExtra("carona", id);
						startActivity(agendarCarona);
						Log.i("info", "agendar carona");
					}
					else {
						clicks = 1;
						if (!marker.getTitle().endsWith(" ")) {
							marker.setTitle(marker.getTitle()+" ");
						}
					}
					return false;
				}
			});
			
			gMap.setOnMapClickListener(new GoogleMap.OnMapClickListener() {
				
				@Override
				public void onMapClick(LatLng point) {
					// TODO Auto-generated method stub
					clicks = 0;
				}
			});
		}
		catch (Exception e) {
			//Toast.makeText(Inicio.this, response.toString(), Toast.LENGTH_LONG).show();
			Log.i("Erro", response.toString());
		}
	}

	@Override
	public void onProviderDisabled(String provider) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void onProviderEnabled(String provider) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void onStatusChanged(String provider, int status, Bundle extras) {
		// TODO Auto-generated method stub
		
	}
    
}
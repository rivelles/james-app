package com.example.james;

import java.util.ArrayList;
import java.util.List;
import java.util.Locale;

import org.apache.http.NameValuePair;
import org.apache.http.message.BasicNameValuePair;

import com.example.james.conexaoweb.ConexaoHttpClient;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.GoogleMap.OnMapClickListener;
import com.google.android.gms.maps.GoogleMap.OnMapLongClickListener;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.BitmapDescriptorFactory;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;
import com.google.android.maps.GeoPoint;

import android.content.Context;
import android.content.Intent;
import android.location.Geocoder;
import android.location.Location;
import android.location.Address;
import android.location.LocationListener;
import android.os.Bundle;
import android.support.v4.app.FragmentActivity;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ProgressBar;
import android.widget.Toast;

public class CadastrarCarona extends FragmentActivity implements LocationListener, OnMapClickListener, OnMapLongClickListener {
	Geocoder geoCoder;
	GeoPoint point;
	GoogleMap gMap;
	
	EditText etEndereco;
	Button btProsseguir, btBuscar;
	ProgressBar pbMapa;
	
	int markers = 0;
	int clicks = 0;
	double latPartida = 0;
	double lonPartida = 0;
	double latDestino = 0;
	double lonDestino = 0;
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.cadastrar_carona);
		
//		etData = (EditText)findViewById(R.id.etData);
//		etHora = (EditText)findViewById(R.id.etHora);
		etEndereco = (EditText)findViewById(R.id.etEndereco);
		btBuscar = (Button)findViewById(R.id.btBuscar);
		btProsseguir = (Button)findViewById(R.id.btProsseguir);
		
		
		btBuscar.setOnClickListener(new View.OnClickListener() {
			
			@Override
			public void onClick(View v) {
				Intent intent = getIntent();
				String user = intent.getStringExtra("user");
				// TODO Auto-generated method stub
				String endereco = etEndereco.getText().toString().trim();
				try {
					geoCoder = new Geocoder(CadastrarCarona.this, Locale.ENGLISH);
					Log.i("Endereço", endereco);
					List<Address>addresses = geoCoder.getFromLocationName(endereco, 1);
					if (addresses.size()>0) {
						double latitude= addresses.get(0).getLatitude();
					    double longitude= addresses.get(0).getLongitude();
						LatLng ll = new LatLng(latitude, longitude);
						if (gMap == null) {
							SupportMapFragment mapFragment = (SupportMapFragment) getSupportFragmentManager().findFragmentById(R.id.map);
							gMap = mapFragment.getMap();
							gMap.setOnMapLongClickListener(CadastrarCarona.this);
							gMap.setOnMapClickListener(CadastrarCarona.this);
						}
						gMap.moveCamera(CameraUpdateFactory.newLatLng(ll));
						gMap.animateCamera(CameraUpdateFactory.zoomTo(10));
						ArrayList<NameValuePair> parametrosBusca = new ArrayList<NameValuePair>();
						parametrosBusca.add(new BasicNameValuePair("user", user));
						Log.i("user", user);
						parametrosBusca.add(new BasicNameValuePair("endereco", endereco));
						parametrosBusca.add(new BasicNameValuePair("tipo", "2"));
						String response = "";
						try {
							response = ConexaoHttpClient.executaHttpPost("http://www.petshopcastelo.vet.br/james/select_routes_address.php", parametrosBusca);
							Log.i("response", response.toString());
							if (response != "") {
								String pontos = response.toString().trim();
								String[] caronas = pontos.split(";");
								for (int i=0; i<caronas.length; i++) {
									String[] array = caronas[i].split(",");
									String userCarona = array[0].substring(1);
									double start_lat = Double.parseDouble(array[1]);
									double start_lon = Double.parseDouble(array[2]);
									String start_ref = array[3];
									double finish_lat = Double.parseDouble(array[4]);
									double finish_lon = Double.parseDouble(array[5]);
									String finish_ref = array[6];
									String time = array[7];
									String id = array[9];
									LatLng inicio = new LatLng(start_lat, start_lon);
									gMap.addMarker(new MarkerOptions()
													.position(inicio)
													.title(id+"- "+userCarona)
													.snippet("De "+start_ref+" até "+finish_ref+" às "+time));
									LatLng fim = new LatLng(finish_lat, finish_lon);
									gMap.addMarker(new MarkerOptions()
													.position(fim)
													.title(id+"- "+userCarona)
													.snippet("De "+start_ref+" até "+finish_ref+" às "+time)
													.icon(BitmapDescriptorFactory.defaultMarker(BitmapDescriptorFactory.HUE_VIOLET)));
									
									gMap.setOnMarkerClickListener(new GoogleMap.OnMarkerClickListener() {
										
										@Override
										public boolean onMarkerClick(Marker marker) {
											// TODO Auto-generated method stub
											if (marker.getTitle().endsWith(" ") && clicks == 1) {
												String[] title = marker.getTitle().split("-");
												String id = title[0];
												Intent intent2 = getIntent();
												String user = intent2.getStringExtra("user");
												Intent agendarCarona = new Intent(CadastrarCarona.this, AgendarCarona.class);
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
									
								}
							}
						}
						catch (Exception e) {
							Log.i("Erro", response);
						}
					}
				}
				catch (Exception e) {
					Log.i("Erro", e.toString());
				}
			}
		});
		
		btProsseguir.setOnClickListener(new View.OnClickListener() {
			
			@Override
			public void onClick(View arg0) {
				// TODO Auto-generated method stub
				if (latPartida == 0 && lonPartida == 0 && latDestino == 0 && lonDestino == 0) {
					Toast.makeText(CadastrarCarona.this, "Você não definiu ponto de partida nem ponto de destino.", Toast.LENGTH_LONG).show();
				}
				else if (latPartida == 0 || lonPartida == 0) {
					Toast.makeText(CadastrarCarona.this, "Você não definiu um ponto de partida.", Toast.LENGTH_LONG).show();
				}
				else if (latDestino == 0 || lonDestino == 0) {
					Toast.makeText(CadastrarCarona.this, "Você não definiu um ponto de destino.", Toast.LENGTH_LONG).show();
				}
				else {
					Intent i = getIntent();
					String user = i.getStringExtra("user");
					Intent restoCadastro = new Intent(CadastrarCarona.this, RestoCadastroCarona.class);
					restoCadastro.putExtra("user", user);
					restoCadastro.putExtra("latPartida", String.valueOf(latPartida));
					restoCadastro.putExtra("latDestino", String.valueOf(latDestino));
					restoCadastro.putExtra("lonPartida", String.valueOf(lonPartida));
					restoCadastro.putExtra("lonDestino", String.valueOf(lonDestino));
					startActivity(restoCadastro);
					finish();
				}
			}
		});
	}

	@Override
	public void onLocationChanged(Location arg0) {
		// TODO Auto-generated method stub
		
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

	@Override
	public void onMapLongClick(LatLng point) {
		// TODO Auto-generated method stub
		if (markers == 0) {
			gMap.addMarker(new MarkerOptions()
							.position(point)
							.title("Partida: "+point.toString())
							.icon(BitmapDescriptorFactory.defaultMarker(BitmapDescriptorFactory.HUE_YELLOW)));
			latPartida = point.latitude;
			lonPartida = point.longitude;
		}
		else if (markers == 1) {
			gMap.addMarker(new MarkerOptions()
			.position(point)
			.title("Partida: "+point.toString())
			.icon(BitmapDescriptorFactory.defaultMarker(BitmapDescriptorFactory.HUE_GREEN)));
			latDestino = point.latitude;
			lonDestino = point.longitude;
		}
		else {
			return;
		}
		markers++;
	}

	@Override
	public void onMapClick(LatLng arg0) {
		// TODO Auto-generated method stub
		
	}
}
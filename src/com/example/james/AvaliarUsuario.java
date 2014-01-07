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
import android.widget.RatingBar;
import android.widget.TextView;
import android.widget.Toast;

import com.example.james.conexaoweb.ConexaoHttpClient;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.BitmapDescriptorFactory;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.MarkerOptions;

public class AvaliarUsuario extends Activity {
	
	TextView tvCaronaAv;
	Button btConfirmaAvaliacao, btCancelaAvaliacao;
	RatingBar rbAvaliacao;
	
	String user, carona, userCarona, offeredRoute;
	String avaliacao = "2";
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.avaliar_usuario);
		
		tvCaronaAv = (TextView)findViewById(R.id.tvCaronaAv);
		btConfirmaAvaliacao = (Button)findViewById(R.id.btConfirmaAvaliacao);
		btCancelaAvaliacao = (Button)findViewById(R.id.btCancelaAvaliacao);
		
		Intent i = getIntent();
		user = i.getStringExtra("user");
		carona = i.getStringExtra("ride");
		
		addListenerOnRatingBar();
		
		btConfirmaAvaliacao.setOnClickListener(new View.OnClickListener() {
			
			@Override
			public void onClick(View v) {
				ArrayList<NameValuePair> parametrosRating = new ArrayList<NameValuePair>();
				parametrosRating.add(new BasicNameValuePair("user", userCarona));
				parametrosRating.add(new BasicNameValuePair("user_route", user));
				parametrosRating.add(new BasicNameValuePair("offered_route", offeredRoute));
				parametrosRating.add(new BasicNameValuePair("rate", avaliacao.substring(0, 1)));
				String response = "";
				try {
					response = ConexaoHttpClient.executaHttpPost("http://www.petshopcastelo.vet.br/james/set_rating.php", parametrosRating);
					int ret = Integer.parseInt(response.toString().trim());
					if (ret == 1) {
						Toast.makeText(AvaliarUsuario.this, "Obrigado por avaliar :)", Toast.LENGTH_LONG).show();
						response = ConexaoHttpClient.executaHttpPost("http://www.petshopcastelo.vet.br/james/verify_ride.php", parametrosRating);
						int ret2 = Integer.parseInt(response.toString().trim());
						if (ret2 > 0) {
							Intent intent = new Intent(AvaliarUsuario.this, AvaliarUsuario.class);
							intent.putExtra("user", user);
							intent.putExtra("ride", carona);
							startActivity(intent);
							finish();
						}
						else {
							finish();
						}
					}
					else {
						Toast.makeText(AvaliarUsuario.this, "Erro na avaliação", Toast.LENGTH_LONG).show();
					}
				}
				catch (Exception e) {
					Log.i("Erro", e.getMessage());
				}
			}
		});
		
		btCancelaAvaliacao.setOnClickListener(new View.OnClickListener() {
			
			@Override
			public void onClick(View v) {
				// TODO Auto-generated method stub
				ArrayList<NameValuePair> parametrosCancel = new ArrayList<NameValuePair>();
				parametrosCancel.add(new BasicNameValuePair("user", user));
				Log.i("user", userCarona);
				parametrosCancel.add(new BasicNameValuePair("offered_route", offeredRoute));
				Log.i("offered_route", offeredRoute);
				String response = "";
				try {
					response = ConexaoHttpClient.executaHttpPost("http://www.petshopcastelo.vet.br/james/set_flag.php", parametrosCancel);
					int ret3 = Integer.parseInt(response.toString().trim());
					if (ret3 > 0) {
						finish();
					}
					else {
						Toast.makeText(AvaliarUsuario.this, "Erro ao cancelar: "+response, Toast.LENGTH_LONG).show();
					}
				}
				catch (Exception e) {
					Log.i("Erro aqui", e.getMessage());
				}
			}
		});
		
		ArrayList<NameValuePair> parametrosCarona = new ArrayList<NameValuePair>();
		parametrosCarona.add(new BasicNameValuePair("carona", carona));
		String response = "";
		try {
			response = ConexaoHttpClient.executaHttpPost("http://www.petshopcastelo.vet.br/james/get_carona.php", parametrosCarona);
			String[] array = response.toString().trim().split(",");
			String user = array[0];
			String start_ref = array[3];
			String finish_ref = array[6];
			String hora = array[7];
			String data = array[8];
			userCarona = array[9];
			offeredRoute = array[10];
			Log.i("Passou aqui", response);
			String dia = data.substring(8, 10);
			String mes = data.substring(5, 7);
			String ano = data.substring(0, 4);
			data = dia+"/"+mes+"/"+ano;
			String ret = "Usuário: "+user+"\nSaída de: "+start_ref+" às "+hora+" de "+data+"\nChegada em: "+finish_ref;
			tvCaronaAv.setText(ret);
		}
		catch (Exception e) {
			Log.i("Erro", e.getMessage());
		}
	}
	
	public void addListenerOnRatingBar() {
		 
		rbAvaliacao = (RatingBar)findViewById(R.id.rbAvaliacao);
	 
		//if rating value is changed,
		//display the current rating value in the result (textview) automatically
		rbAvaliacao.setOnRatingBarChangeListener(new RatingBar.OnRatingBarChangeListener() {
			public void onRatingChanged(RatingBar ratingBar, float rating,
				boolean fromUser) {
				avaliacao = String.valueOf(rating);
				Log.i("avaliacao", avaliacao);
			}
		});
	  }
}
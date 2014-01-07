package com.example.james;

import java.util.ArrayList;

import org.apache.http.NameValuePair;
import org.apache.http.message.BasicNameValuePair;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.example.james.conexaoweb.ConexaoHttpClient;

public class RestoBuscaCarona extends Activity {
	EditText etRefPartidaBusca, etRefChegadaBusca, etHorarioPartidaBusca;
	Button btFinalizarBusca;
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.resto_busca_carona);
		
		etRefPartidaBusca = (EditText)findViewById(R.id.etRefPartidaBusca);
		etRefChegadaBusca = (EditText)findViewById(R.id.etRefChegadaBusca);
		etHorarioPartidaBusca = (EditText)findViewById(R.id.etHorarioPartidaBusca);
		btFinalizarBusca = (Button)findViewById(R.id.btFinalizarBusca);
		
		btFinalizarBusca.setOnClickListener(new View.OnClickListener() {
			
			@Override
			public void onClick(View v) {
				// TODO Auto-generated method stub
				boolean campos = false;
				String msg = "Preencha os seguintes campos:";
				if (etRefPartidaBusca.getText().toString() == "") {
					campos = true;
					msg += " | Referência de partida";
				}
				if (etRefChegadaBusca.getText().toString() == "") {
					campos = true;
					msg += " | Referência de chegada";
				}
				if (etHorarioPartidaBusca.getText().toString() == "") {
					campos = true;
					msg += " | Horário de partida";
				}
				if (campos) {
					Toast.makeText(RestoBuscaCarona.this, msg, Toast.LENGTH_LONG).show();
				}
				else {
					Intent i = getIntent();
					String user = i.getStringExtra("user");
					String latPartida = i.getStringExtra("latPartida");
					String lonPartida = i.getStringExtra("lonPartida");
					String latChegada = i.getStringExtra("latDestino");
					String lonChegada = i.getStringExtra("lonDestino");
					String data = i.getStringExtra("data");
					ArrayList<NameValuePair> parametrosCarona = new ArrayList<NameValuePair>();
					parametrosCarona.add(new BasicNameValuePair("latPartida", latPartida));
					parametrosCarona.add(new BasicNameValuePair("lonPartida", lonPartida));
					parametrosCarona.add(new BasicNameValuePair("latChegada", latChegada));
					parametrosCarona.add(new BasicNameValuePair("lonChegada", lonChegada));
					parametrosCarona.add(new BasicNameValuePair("data", data));
					parametrosCarona.add(new BasicNameValuePair("refPartida", etRefPartidaBusca.getText().toString()));
					parametrosCarona.add(new BasicNameValuePair("refChegada", etRefChegadaBusca.getText().toString()));
					parametrosCarona.add(new BasicNameValuePair("horaPartida", etHorarioPartidaBusca.getText().toString()));
					parametrosCarona.add(new BasicNameValuePair("user", user));
					String response = "";
					try {
						response = ConexaoHttpClient.executaHttpPost("http://www.petshopcastelo.vet.br/james/cadastrar_busca_carona.php", parametrosCarona);
						int cadastro = Integer.parseInt(response.toString().trim());
						if (cadastro == 1) {
							Toast.makeText(RestoBuscaCarona.this, "Carona cadastrada com sucesso.", Toast.LENGTH_LONG).show();
							finish();
						}
						else {
							Toast.makeText(RestoBuscaCarona.this, "Erro: "+response.toString(), Toast.LENGTH_LONG).show();
						}
					}
					catch (Exception e) {
						Toast.makeText(RestoBuscaCarona.this, response.toString(), Toast.LENGTH_LONG).show();
					}
				}
			}
		});
	}
}
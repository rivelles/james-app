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

public class RestoCadastroCarona extends Activity {
	EditText etRefPartida, etRefChegada, etHorarioPartida, etData, etMax, etValor;
	Button btFinalizar;
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.resto_cadastro_carona);
		
		etRefPartida = (EditText)findViewById(R.id.etRefPartida);
		etRefChegada = (EditText)findViewById(R.id.etRefChegada);
		etHorarioPartida = (EditText)findViewById(R.id.etHorarioPartida);
		etData = (EditText)findViewById(R.id.etData);
		etMax = (EditText)findViewById(R.id.etMax);
		etValor = (EditText)findViewById(R.id.etValor);
		btFinalizar = (Button)findViewById(R.id.btFinalizar);
		
		btFinalizar.setOnClickListener(new View.OnClickListener() {
			
			@Override
			public void onClick(View v) {
				// TODO Auto-generated method stub
				boolean campos = false;
				String msg = "Preencha os seguintes campos:";
				if (etRefPartida.getText().toString() == "") {
					campos = true;
					msg += " | Referência de partida";
				}
				if (etRefChegada.getText().toString() == "") {
					campos = true;
					msg += " | Referência de chegada";
				}
				if (etHorarioPartida.getText().toString() == "") {
					campos = true;
					msg += " | Horário de partida";
				}
				if (etData.getText().toString() == "") {
					campos = true;
					msg += "| Data";
				}
				if (campos) {
					Toast.makeText(RestoCadastroCarona.this, msg, Toast.LENGTH_LONG).show();
				}
				else {
					Intent i = getIntent();
					String user = i.getStringExtra("user");
					String latPartida = i.getStringExtra("latPartida");
					String lonPartida = i.getStringExtra("lonPartida");
					String latChegada = i.getStringExtra("latDestino");
					String lonChegada = i.getStringExtra("lonDestino");
					ArrayList<NameValuePair> parametrosCarona = new ArrayList<NameValuePair>();
					parametrosCarona.add(new BasicNameValuePair("latPartida", latPartida));
					parametrosCarona.add(new BasicNameValuePair("lonPartida", lonPartida));
					parametrosCarona.add(new BasicNameValuePair("latChegada", latChegada));
					parametrosCarona.add(new BasicNameValuePair("lonChegada", lonChegada));
					parametrosCarona.add(new BasicNameValuePair("refPartida", etRefPartida.getText().toString()));
					parametrosCarona.add(new BasicNameValuePair("refChegada", etRefChegada.getText().toString()));
					parametrosCarona.add(new BasicNameValuePair("horaPartida", etHorarioPartida.getText().toString()));
					parametrosCarona.add(new BasicNameValuePair("data", etData.getText().toString()));
					parametrosCarona.add(new BasicNameValuePair("max", etMax.getText().toString()));
					parametrosCarona.add(new BasicNameValuePair("valor", etValor.getText().toString()));
					parametrosCarona.add(new BasicNameValuePair("user", user));
					String response = "";
					try {
						response = ConexaoHttpClient.executaHttpPost("http://www.petshopcastelo.vet.br/james/cadastrar_carona.php", parametrosCarona);
						int cadastro = Integer.parseInt(response.toString().trim());
						if (cadastro == 1) {
							Toast.makeText(RestoCadastroCarona.this, "Carona cadastrada com sucesso.", Toast.LENGTH_LONG).show();
							finish();
						}
						else {
							Toast.makeText(RestoCadastroCarona.this, "Erro: "+response.toString(), Toast.LENGTH_LONG).show();
						}
					}
					catch (Exception e) {
						Toast.makeText(RestoCadastroCarona.this, response.toString(), Toast.LENGTH_LONG).show();
					}
				}
			}
		});
	}
}
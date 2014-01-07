package com.example.james;

import java.util.ArrayList;

import org.apache.http.NameValuePair;
import org.apache.http.message.BasicNameValuePair;

import com.example.james.conexaoweb.ConexaoHttpClient;

import android.os.Bundle;
import android.os.StrictMode;
import android.app.Activity;
import android.content.Intent;
import android.util.Log;
import android.view.Menu;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.Toast;

public class MainActivity extends Activity {
	ImageButton ibFacebook;
	Button btCriar, btLogin;
	EditText etEmail, etSenha;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_main);
		if (android.os.Build.VERSION.SDK_INT > 9) {
		    StrictMode.ThreadPolicy policy = new StrictMode.ThreadPolicy.Builder().permitAll().build();
		    StrictMode.setThreadPolicy(policy);
		}
		
		btCriar = (Button)findViewById(R.id.btCriar);
		//ibFacebook = (ImageButton)findViewById(R.id.ibFacebook);
		btLogin = (Button)findViewById(R.id.btLogin);
		etEmail = (EditText)findViewById(R.id.etEmail);
		etSenha = (EditText)findViewById(R.id.etSenha);
		
		btLogin.setOnClickListener(new View.OnClickListener() {
			
			@Override
			public void onClick(View v) {
				// TODO Auto-generated method stub
				String email = etEmail.getText().toString();
				String senha = etSenha.getText().toString();
				ArrayList<NameValuePair> parametrosLogin = new ArrayList<NameValuePair>();
				parametrosLogin.add(new BasicNameValuePair("email", email));
				parametrosLogin.add(new BasicNameValuePair("senha", senha));
				try {
					String response = ConexaoHttpClient.executaHttpPost("http://www.petshopcastelo.vet.br/james/auth.php", parametrosLogin);
					int login = Integer.parseInt(response.toString().trim());
					if (login > 0) {
						//Toast.makeText(MainActivity.this, "Login realizado com sucesso.", Toast.LENGTH_LONG).show();
						Intent inicio = new Intent(MainActivity.this, Inicio.class);
						inicio.putExtra("user", String.valueOf(login));
						startActivity(inicio);
						finish();
					}
					else {
						Toast.makeText(MainActivity.this, response.toString(), Toast.LENGTH_LONG).show();
					}
				} catch (Exception e) {
					// TODO Auto-generated catch block
					Toast.makeText(MainActivity.this, "Erro.", Toast.LENGTH_LONG).show();
					e.printStackTrace();
				}
			}
		});
		
		btCriar.setOnClickListener(new View.OnClickListener() {
			
			@Override
			public void onClick(View v) {
				// TODO Auto-generated method stub
				Intent criar = new Intent(MainActivity.this, CriarUsuario.class);
				startActivity(criar);
			}
		});
	}

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		// Inflate the menu; this adds items to the action bar if it is present.
		getMenuInflater().inflate(R.menu.main, menu);
		return true;
	}

}

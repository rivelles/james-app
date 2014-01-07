package com.example.james;

import java.io.InputStream;
import java.util.ArrayList;

import org.apache.http.NameValuePair;
import org.apache.http.message.BasicNameValuePair;

import com.example.james.conexaoweb.ConexaoHttpClient;
import com.example.james.conexaoweb.Lib;

import android.app.Activity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

public class CriarUsuario extends Activity {
	InputStream inputStream;
	String picturePath;
	
	EditText etNome, etEmail, etSenha;
	Button btCadastrar;
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.criar_usuario);
		
		etNome = (EditText)findViewById(R.id.etNome);
		etEmail = (EditText)findViewById(R.id.etEmail);
		etSenha = (EditText)findViewById(R.id.etSenha);
		btCadastrar = (Button)findViewById(R.id.btCadastrar);
		
		btCadastrar.setOnClickListener(new View.OnClickListener() {
			
			@Override
			public void onClick(View v) {
				// TODO Auto-generated method stub
				if (!Lib.verificaEmail(etEmail.getText().toString())) {
					Toast.makeText(CriarUsuario.this, "Este e-mail já está cadastrado.", Toast.LENGTH_LONG).show();
				}
				else {
					ArrayList<NameValuePair> nameValuePairs = new  ArrayList<NameValuePair>();
					
					nameValuePairs.add(new BasicNameValuePair("email", etEmail.getText().toString()));
					try {
						ArrayList<NameValuePair> parametrosCadastro = new ArrayList<NameValuePair>();
						parametrosCadastro.add(new BasicNameValuePair("nome", etNome.getText().toString()));
						parametrosCadastro.add(new BasicNameValuePair("email", etEmail.getText().toString()));
						parametrosCadastro.add(new BasicNameValuePair("senha", etSenha.getText().toString()));
						String cadastro = ConexaoHttpClient.executaHttpPost("http://www.petshopcastelo.vet.br/james/cadastrar.php", parametrosCadastro);
						Toast.makeText(CriarUsuario.this, cadastro, Toast.LENGTH_LONG).show();
						finish();
					}
					catch(Exception e) {
						Toast.makeText(CriarUsuario.this, "Erro.", Toast.LENGTH_LONG).show();
					}
				}
			}
		});
	}
}
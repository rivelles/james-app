package com.example.james.conexaoweb;

import java.util.ArrayList;

import org.apache.http.NameValuePair;
import org.apache.http.message.BasicNameValuePair;

import android.os.AsyncTask;
import android.util.Log;

public class Lib {
	public static boolean verificaEmail(String email) {
		String url = "http://www.petshopcastelo.vet.br/james/verificar_email.php";
		ArrayList<NameValuePair> parametros = new ArrayList<NameValuePair>();
		parametros.add(new BasicNameValuePair("email", email));
		String response = null;
		try {
			response = ConexaoHttpClient.executaHttpPost(url, parametros);
			int resposta = Integer.parseInt(response.toString().trim());
			if (resposta == 1) return true;
			return false;
		}
		catch(Exception e) {
			e.printStackTrace();
			return false;
		}
	}
}

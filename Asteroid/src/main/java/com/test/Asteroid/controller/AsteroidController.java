package com.test.Asteroid.controller;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.math.BigDecimal;
import java.net.URL;
import java.nio.charset.Charset;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Collections;
import java.util.List;
import java.util.logging.Logger;

import javax.servlet.http.HttpServletResponse;

import org.json.JSONArray;
import org.json.JSONObject;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;

import com.test.Asteroid.Funciones.Constantes;
import com.test.Asteroid.model.Asteroide;

@Controller
public class AsteroidController {

	protected static Logger logger = Logger.getLogger(AsteroidController.class.getName());

	@RequestMapping(value = "/")
	@ResponseBody
	public List<Asteroide> getAsteroid(HttpServletResponse response, 
			@RequestParam(required = true) String planet)
			throws IOException {	

		logger.info("////INICIO////");		
		List<Asteroide> listAsteroideDevolver = new ArrayList<Asteroide>();
	
		// Si planeta no esta vacio
		if (!planet.equals("")) {
			
			//Obtengo JSON de la api de la NASA
			logger.info("obteniendo objetos cercanos al planeta " + planet);
			JSONObject json = obtenerJSONNasa(planet);
			
			//Procesar JSON
			logger.info("procesando json....");
			List<Asteroide> listAsteroideOrdenada = procesarJSON(json, planet);
			
			//Mostrar JSON
			if(listAsteroideOrdenada.size()>0) {
								
				if(listAsteroideOrdenada.size() > 3) {
					
					listAsteroideDevolver.add(listAsteroideOrdenada.get(0));
					listAsteroideDevolver.add(listAsteroideOrdenada.get(1));
					listAsteroideDevolver.add(listAsteroideOrdenada.get(2));		
										
				}else {
					listAsteroideDevolver = listAsteroideOrdenada;
				}
				
			}else {
				logger.info("No se han obtenido objetos cercanos al planeta " + planet);
			}			
			
		} else {
			logger.info("Se requiere valor en parametro 'planet'");
		}
		
		logger.info("////FIN////");		
		return listAsteroideDevolver;

	}

	/**
	 * Metodo que obtiene Json del API de la NASA
	 * 
	 * @param planeta
	 * @return
	 * @throws IOException
	 */
	private static JSONObject obtenerJSONNasa(String planeta) throws IOException {

		//Key API (Atmira test)
		String apiKey = "zdUP8ElJv1cehFM0rsZVSQN7uBVxlDnu4diHlLSb";
		
		//Formato fecha
		SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd");
		
		//Obtengo fecha
		Calendar calendar = Calendar.getInstance();
		
		//fecha actual
		String fechaInicio = sdf.format(calendar.getTime());

		//Añado 7 dias al dia actual
		calendar.add(Calendar.DAY_OF_YEAR, 7);
		
		//Fecha Fin
		String fechaFin = sdf.format(calendar.getTime());
				
		//Monto url
		String url = "https://api.nasa.gov/neo/rest/v1/feed?" 
				+ "start_date=" + fechaInicio 
				+ "&end_date=" + fechaFin
				+ "&api_key=" + apiKey;

		InputStream is = new URL(url).openStream();

		try {

			BufferedReader rd = new BufferedReader(new InputStreamReader(is, Charset.forName("UTF-8")));

			StringBuilder sb = new StringBuilder();
			int cp;
			while ((cp = rd.read()) != -1) {
				sb.append((char) cp);
			}

			JSONObject json = new JSONObject(sb.toString());			

			return json;

		} finally {
			is.close();
		}

	}

	/**
	 * Procesar JSON obtenido de la API
	 * @param json
	 */
	private static List<Asteroide> procesarJSON(JSONObject json, String planetaParam) {
		
		//Gson gson = new Gson();
		//Map<String,Object> map = new HashMap<String,Object>();
		//map = (Map<String,Object>) gson.fromJson(json.get("near_earth_objects").toString(), map.getClass());
		
		JSONObject nearEarth = new JSONObject(json.get(Constantes.objetoCercano).toString());						
		SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd");
		
		//Lista de asteroides
		List<Asteroide> listAsteroide = new ArrayList<Asteroide>();
		
		for(int i=0; i<Constantes.nDiaObjetos;i++) {	

			Calendar calendar = Calendar.getInstance();
			calendar.add(Calendar.DAY_OF_YEAR, i);
			String fecha = sdf.format(calendar.getTime());
			
			JSONArray array = (JSONArray) nearEarth.get(fecha);
			
			for(int j=0;j<array.length();j++) {
				
				JSONObject asteroideJSON = (JSONObject) array.get(j);	
				
				JSONArray dataArray = (JSONArray) asteroideJSON.get(Constantes.closeApproachData);				
				JSONObject data = (JSONObject) dataArray.get(0);	
				String planeta = (String) data.get(Constantes.cuerpo);
				
				//Si planeta obtenido es igual al planeta del parametro
				//agrego cuerpo a la lista
				if(planeta.equalsIgnoreCase(planetaParam)) {
					
					JSONObject diametroJSON = (JSONObject) asteroideJSON.get(Constantes.diametroEstimado);
					JSONObject kilometerJSON = (JSONObject) diametroJSON.get(Constantes.kilometros);				
					BigDecimal diametroMinimoJSON = (BigDecimal) kilometerJSON.get(Constantes.diametroMinimo);
					BigDecimal diametroMaximoJSON = (BigDecimal) kilometerJSON.get(Constantes.diametroMaximo);
					JSONObject velocidad = (JSONObject) data.get(Constantes.velocidad);
					
					//Nuevo objeto Asteroide
					Asteroide asteroide = new Asteroide();
					asteroide.setNombre((String) asteroideJSON.get(Constantes.nombre));
					asteroide.setDiametro((diametroMinimoJSON.doubleValue() + diametroMaximoJSON.doubleValue()) /2);
					asteroide.setPlaneta((String) data.get(Constantes.cuerpo));
					asteroide.setFecha((String) data.get(Constantes.closeApproachDate));
					asteroide.setVelocidad((String) velocidad.get(Constantes.kilometroPhora));
					
					//Añado asteroide a la lista de asteroides (Sin ordenar)
					listAsteroide.add(asteroide);
					
				}				

			}			
			
		}
		
		//Ordeno lista de asteroides
		Collections.sort(listAsteroide);		
		
		return listAsteroide;
		
	}
		
}

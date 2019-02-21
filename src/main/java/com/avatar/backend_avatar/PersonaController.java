package com.avatar.backend_avatar;

import java.util.Optional;

import org.json.JSONArray;
import org.json.JSONObject;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.env.Environment;
import org.springframework.http.HttpMethod;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.client.RestTemplate;

@CrossOrigin(origins = "*")
@RestController
@RequestMapping(path="/person")
public class PersonaController {

	@Autowired
	private PersonaRepository personaRepository;
	
	@Autowired
	private Environment env;
	
	@GetMapping(path="")
	public @ResponseBody Iterable<Persona> getAllPersons() {
		return personaRepository.findAll();
	}
	
	@GetMapping(path="/{id}")
	public ResponseEntity<Object> getPerson(@PathVariable long id) {
		Optional<Persona> aa = personaRepository.findById(id);
		return new ResponseEntity<>(aa, HttpStatus.OK);
	}
	
	@PostMapping("/add")
	//@ResponseStatus(code = HttpStatus.CREATED)
	public ResponseEntity<Object> addPerson (@RequestBody Persona persona) {
		
		Integer validate = validatePlanet(persona.getPlanet());
		if(validate == 1) {
			personaRepository.save(persona);
			return new ResponseEntity<>(persona, HttpStatus.CREATED);
		}
		Error error = new Error();
		error.setError_message(env.getProperty("error.message"));
		return new ResponseEntity<>(error, HttpStatus.INTERNAL_SERVER_ERROR);
	    
		
	}
	
	@PutMapping(path="/edit/{id}")
	public ResponseEntity<Object> editPerson (@RequestBody Persona persona, @PathVariable long id) {
		Integer validate = validatePlanet(persona.getPlanet());
		if(validate == 1) {
			persona.setId(id);
			personaRepository.save(persona);
			return new ResponseEntity<>(persona, HttpStatus.OK);
		}
		Error error = new Error();
		error.setError_message(env.getProperty("error.message"));
		return new ResponseEntity<>(error, HttpStatus.INTERNAL_SERVER_ERROR);
	}
	
	@DeleteMapping("/delete/{id}")
	public void deleteStudent(@PathVariable long id) {
		personaRepository.deleteById(id);
	}
	
	public int validatePlanet(String name) {
		String next_url = env.getProperty("url.planets");
		System.out.println(next_url);
		Integer result = 0;
		
		while(next_url != "") {
			RestTemplate restTemplate = new RestTemplate();
		    ResponseEntity<String> responseEntity = restTemplate.exchange(next_url, HttpMethod.GET, null,String.class);
			String response = responseEntity.getBody();
			JSONObject planets = new JSONObject(response);
			
			JSONArray jsonArray = planets.getJSONArray("results");
			next_url = planets.isNull("next")?"":planets.getString("next").toString();
			
			for (int i = 0; i < jsonArray.length(); i++) {
			    JSONObject explrObject = jsonArray.getJSONObject(i);
			    if(name.equals(explrObject.get("name"))) {
			    	result = 1;
			    	break;
			    }
			}
			
		}
		return result;
		
	}
}

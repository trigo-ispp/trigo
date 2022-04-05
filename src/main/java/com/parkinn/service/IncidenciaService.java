package com.parkinn.service;

import com.parkinn.repository.ClientRepository;
import com.parkinn.repository.IncidenciaRepository;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

import com.parkinn.model.Incidencia;
import com.parkinn.model.Reserva;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;

@Service
public class IncidenciaService {
    
    @Autowired
    private IncidenciaRepository repository;

	@Autowired
    private ReservaService reservaService;

	@Autowired
    private ClientRepository clientRepository;
    
    
	public List<Incidencia> findAll(){
        return repository.findAll();
    }

	  public Incidencia findIncidenciaById(Long id){
    	Optional<Incidencia> incidencias = repository.findById(id);
        return incidencias.get();
    }

	public boolean comprobarCliente(Incidencia r){
		String creadorIncidenciaFromFront  = r.getUser().getEmail();
		String creadorIncidenciaByToken  = SecurityContextHolder.getContext().getAuthentication().getName();
		Reserva reserva = reservaService.findById(r.getReserva().getId());

		String duenoPlaza = reserva.getPlaza().getAdministrador().getEmail();
		String clientePlaza = reserva.getUser().getEmail();

		//Compruebo que el user que me manda el front es el mismo que tiene sesión iniciada
		if(!creadorIncidenciaByToken.equals(creadorIncidenciaFromFront)){
			return false;
		}

		//Compruebo que el usuario que realiza la incidencia o es el dueño o es el cliente.
		
		return creadorIncidenciaFromFront.equals(duenoPlaza) || creadorIncidenciaFromFront.equals(clientePlaza);
	}


    public Incidencia guardarIncidencia(Incidencia r){
        r.setFecha(LocalDateTime.now());
		String email = (r.getUser() == null) ? "sinmail" : r.getUser().getEmail();
		Long idReserva = (r.getReserva() == null) ? -1 : r.getReserva().getId();
		if(!email.equals("sinmail")){
			r.setUser(clientRepository.findByEmail(email).get());
		}
		if(idReserva != -1){
			r.setReserva(reservaService.findById(idReserva));
		}
        Incidencia incidencia = repository.save(r);
        return incidencia;
    }

}

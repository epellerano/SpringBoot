package com.sigat.springboot.app.models.dao;


import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.repository.CrudRepository;
import org.springframework.data.repository.PagingAndSortingRepository;
import com.sigat.springboot.app.models.entity.Paciente;

public interface IPacienteDao extends CrudRepository<Paciente, Long>, JpaRepository<Paciente, Long> ,PagingAndSortingRepository<Paciente, Long>{	
	
}

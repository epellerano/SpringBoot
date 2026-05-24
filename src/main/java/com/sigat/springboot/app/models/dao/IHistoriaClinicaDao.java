package com.sigat.springboot.app.models.dao;

import java.util.List;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.CrudRepository;
import com.sigat.springboot.app.models.entity.HistoriaClinica;

public interface IHistoriaClinicaDao extends CrudRepository<HistoriaClinica, Long> {

	@Query("select h from HistoriaClinica h where h.paciente.id = ?1 order by h.fecha desc")
	public List<HistoriaClinica> findByPacienteId(Long id);
}

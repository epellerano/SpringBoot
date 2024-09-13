package com.sigat.springboot.app.models.dao;



import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.repository.CrudRepository;
import org.springframework.data.repository.PagingAndSortingRepository;
import com.sigat.springboot.app.models.entity.Movimiento;

public interface IMovimientoDao extends JpaRepository<Movimiento, Long>, CrudRepository<Movimiento, Long>, 
										PagingAndSortingRepository<Movimiento, Long>{
	
}

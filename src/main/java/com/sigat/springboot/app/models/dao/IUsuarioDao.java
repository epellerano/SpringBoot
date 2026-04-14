package com.sigat.springboot.app.models.dao;

import org.springframework.data.repository.CrudRepository;

import com.sigat.springboot.app.models.entity.Usuario;

public interface IUsuarioDao extends CrudRepository<Usuario, Long>{

	//Metodo que ejecuta una consulta JPQL ("select u from usuario u where u.username=?1")
	public Usuario findByUsername(String username);
	
	/*Forma nativa
	@Query("select u from usuario u where u.username=?1")
	public Usuario buscarByUsername(String username);*/
}

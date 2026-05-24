package com.sigat.springboot.app.models.service;

import java.util.List;

import com.sigat.springboot.app.models.entity.Dia;
import com.sigat.springboot.app.models.entity.Usuario;

public interface IUsuarioService {
	public void save(Usuario usuario);
    public Usuario findByUsername(String username);
}

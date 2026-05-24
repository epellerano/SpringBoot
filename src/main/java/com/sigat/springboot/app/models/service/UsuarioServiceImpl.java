package com.sigat.springboot.app.models.service;

import java.util.List;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import com.sigat.springboot.app.models.dao.IDiaDao;
import com.sigat.springboot.app.models.dao.IUsuarioDao;
import com.sigat.springboot.app.models.entity.Dia;
import com.sigat.springboot.app.models.entity.Usuario;

@Service
public class UsuarioServiceImpl implements IUsuarioService {
	
	 @Autowired
	    private IUsuarioDao usuarioDao;

	    @Override
	    @Transactional
	    public void save(Usuario usuario) {
	        usuarioDao.save(usuario);
	    }

	    @Override
	    @Transactional(readOnly = true)
	    public Usuario findByUsername(String username) {
	        return usuarioDao.findByUsername(username);
	    }

}

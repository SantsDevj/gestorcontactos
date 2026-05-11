package com.gestorcontactos.dao.interfaces;

import com.gestorcontactos.model.Utilizador;

public interface IUtilizadorDAO {
    
    Utilizador buscarPorUsername(String username);
    void cadastrar  (Utilizador utilizador);
    boolean validarCredenciais(String username, String senha);
}

package com.gestorcontactos.service;

import java.time.LocalDate;
import com.gestorcontactos.dao.interfaces.IUtilizadorDAO;
import com.gestorcontactos.model.Utilizador;

public class AuthService {

    private IUtilizadorDAO utilizadorDAO;
    private Utilizador utilizadorActual;

    public AuthService(IUtilizadorDAO utilizadorDAO) {
        this.utilizadorDAO = utilizadorDAO;
    }

    public boolean login(String username, String senha) {
        if (username == null || username.trim().isEmpty())
            throw new IllegalArgumentException("O username não pode estar vazio.");
        if (senha == null || senha.trim().isEmpty())
            throw new IllegalArgumentException("A senha não pode estar vazia.");
        if (utilizadorDAO.validarCredenciais(username, senha)) {
            this.utilizadorActual = utilizadorDAO.buscarPorUsername(username);
            return true;
        }
        return false;
    }

    public void registar(String nome, String username, String senha,
                         String confirmarSenha) {
        if (nome == null || nome.trim().isEmpty())
            throw new IllegalArgumentException("O nome não pode estar vazio.");
        if (username == null || username.trim().isEmpty())
            throw new IllegalArgumentException("O username não pode estar vazio.");
        if (senha == null || senha.length() < 4)
            throw new IllegalArgumentException("A senha deve ter pelo menos 4 caracteres.");
        if (!senha.equals(confirmarSenha))
            throw new IllegalArgumentException("As senhas não coincidem.");
        if (utilizadorDAO.buscarPorUsername(username) != null)
            throw new IllegalArgumentException("Este username já está em uso.");

        Utilizador novo = new Utilizador(nome, username, senha, LocalDate.now());
        utilizadorDAO.cadastrar(novo);
    }

    public Utilizador getUtilizadorActual() {
        return utilizadorActual;
    }
}
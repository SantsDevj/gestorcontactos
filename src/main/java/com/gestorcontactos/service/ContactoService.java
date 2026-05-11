package com.gestorcontactos.service;

import java.util.List;
import com.gestorcontactos.dao.interfaces.IContactoDAO;
import com.gestorcontactos.dao.interfaces.ITelefoneDAO;
import com.gestorcontactos.model.Contacto;
import com.gestorcontactos.model.Telefone;

public class ContactoService {

    private IContactoDAO contactoDAO;
    private ITelefoneDAO telefoneDAO;

    public ContactoService(IContactoDAO contactoDAO, ITelefoneDAO telefoneDAO) {
        this.contactoDAO = contactoDAO;
        this.telefoneDAO = telefoneDAO;
    }

    public void cadastrarContacto(Contacto contacto, int idUtilizador) {
        if (contacto.getNomeCompleto() == null ||
            contacto.getNomeCompleto().trim().isEmpty())
            throw new IllegalArgumentException("O nome do contacto não pode estar vazio.");

        contacto.setIdUtilizador(idUtilizador);
        int idGerado = contactoDAO.cadastrar(contacto);

        if (idGerado != -1 && contacto.getTelefones() != null) {
            for (Telefone t : contacto.getTelefones()) {
                telefoneDAO.adicionar(t, idGerado);
            }
        }
    }

    public List<Contacto> listarContactos(int idUtilizador) {
        return contactoDAO.listar(idUtilizador);
    }

    public void editarContacto(Contacto contacto) {
        if (contacto.getNomeCompleto() == null ||
            contacto.getNomeCompleto().trim().isEmpty())
            throw new IllegalArgumentException("O nome do contacto não pode estar vazio.");
        contactoDAO.editar(contacto);
    }

    public void eliminarContacto(int id) {
        contactoDAO.eliminar(id);
    }

    public List<Contacto> pesquisarContacto(String termo, int idUtilizador) {
        if (termo == null || termo.trim().isEmpty())
            return contactoDAO.listar(idUtilizador);
        return contactoDAO.pesquisar(termo, idUtilizador);
    }

    public Contacto buscarContactoPorId(int id) {
        return contactoDAO.buscarPorId(id);
    }
}
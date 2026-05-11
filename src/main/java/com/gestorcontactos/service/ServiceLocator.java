package service;

import dao.implementacao.*;
import dao.interfaces.*;

public class ServiceLocator {

    private static ServiceLocator instancia = null;

    private final AuthService authService;
    private final ContactoService contactoService;
    private final CategoriaService categoriaService;

    private ServiceLocator() {
        IUtilizadorDAO utilizadorDAO = new UtilizadorDAOImpl();
        IContactoDAO contactoDAO     = new ContactoDAOImpl();
        ITelefoneDAO telefoneDAO     = new TelefoneDAOImpl();
        ICategoriaDAO categoriaDAO   = new CategoriaDAOImpl();

        authService      = new AuthService(utilizadorDAO);
        contactoService  = new ContactoService(contactoDAO, telefoneDAO);
        categoriaService = new CategoriaService(categoriaDAO);
    }

    public static ServiceLocator getInstancia() {
        if (instancia == null) instancia = new ServiceLocator();
        return instancia;
    }

    public AuthService getAuthService()           { return authService; }
    public ContactoService getContactoService()   { return contactoService; }
    public CategoriaService getCategoriaService() { return categoriaService; }
}
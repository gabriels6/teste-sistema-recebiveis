package com.gabriel.testesistemarecebiveis.exception;

public class ResourceNotFoundException extends RuntimeException {

    public ResourceNotFoundException(String resourceName, Integer id) {
        super(resourceName + " não encontrado(a) com o id " + id + ".");
    }
}

package com.Ignacio.Desafio.service;

public interface IConvierteDatos {
    <T> T obtenerDatos(String json, Class<T> clase);
}
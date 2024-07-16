package com.Ignacio.Desafio.Principal;

import com.Ignacio.Desafio.model.Datos;
import com.Ignacio.Desafio.model.DatosLibros;
import com.Ignacio.Desafio.service.ConsumoAPI;
import com.Ignacio.Desafio.service.ConvierteDatos;

import java.util.*;
import java.util.stream.Collectors;

public class Principal {
    private static final String URL_BASE = "https://gutendex.com/books/";
    private ConsumoAPI consumoAPI = new ConsumoAPI();
    private ConvierteDatos conversor = new ConvierteDatos();
    private Scanner teclado = new Scanner(System.in);
    private Set<String> librosRegistrados = new HashSet<>();

    public void muestraElMenu() {
        boolean salir = false;

        while (!salir) {
            System.out.println("Eliga la opción de su número");
            System.out.println("1.- Buscar libro por título");
            System.out.println("2.- Listar libros registrados");
            System.out.println("3.- Listar autores registrados");
            System.out.println("4.- Listar autores vivos en un determinado año");
            System.out.println("5.- Listar libros por idioma");
            System.out.println("0.- Salir");

            int opcion = Integer.parseInt(teclado.nextLine());

            switch (opcion) {
                case 1:
                    buscarLibroPorTitulo();
                    break;
                case 2:
                    listarLibrosRegistrados();
                    break;
                case 3:
                    listarAutoresRegistrados();
                    break;
                case 4:
                    listarAutoresVivosEnAnoDeterminado();
                    break;
                case 5:
                    listarLibrosPorIdioma();
                    break;
                case 0:
                    salir = true;
                    break;
                default:
                    System.out.println("Opción no válida, por favor intente nuevamente.");
            }
        }
    }

    private void buscarLibroPorTitulo() {
        System.out.println("Ingrese el nombre del libro que desea buscar");
        String tituloLibro = teclado.nextLine();
        String json = consumoAPI.obtenerDatos(URL_BASE + "?search=" + tituloLibro.replace(" ", "+"));
        Datos datosBusqueda = conversor.obtenerDatos(json, Datos.class);
        Optional<DatosLibros> libroBuscado = datosBusqueda.resultados().stream()
                .filter(l -> l.titulo().toUpperCase().contains(tituloLibro.toUpperCase()))
                .findFirst();

        if (libroBuscado.isPresent()) {
            DatosLibros libro = libroBuscado.get();
            if (librosRegistrados.add(libro.titulo())) {
                System.out.println("Libro encontrado y registrado:");
                System.out.println(libro);
            } else {
                System.out.println("El libro ya está registrado.");
            }
        } else {
            System.out.println("Libro no encontrado");
        }
    }

    private void listarLibrosRegistrados() {
        if (librosRegistrados.isEmpty()) {
            System.out.println("No hay libros registrados.");
        } else {
            System.out.println("Libros registrados:");
            librosRegistrados.forEach(System.out::println);
        }
    }

    private void listarAutoresRegistrados() {
        Set<String> autoresRegistrados = new HashSet<>();

        for (String titulo : librosRegistrados) {
            String json = consumoAPI.obtenerDatos(URL_BASE + "?search=" + titulo.replace(" ", "+"));
            Datos datosBusqueda = conversor.obtenerDatos(json, Datos.class);
            datosBusqueda.resultados().forEach(libro ->
                    libro.autor().forEach(autor -> autoresRegistrados.add(autor.nombre()))
            );
        }

        if (autoresRegistrados.isEmpty()) {
            System.out.println("No hay autores registrados.");
        } else {
            System.out.println("Autores registrados:");
            autoresRegistrados.forEach(System.out::println);
        }
    }

    private void listarAutoresVivosEnAnoDeterminado() {
        System.out.println("Ingrese el año para listar los autores vivos en ese año:");
        int ano = Integer.parseInt(teclado.nextLine());
        Set<String> autoresVivos = new HashSet<>();

        for (String titulo : librosRegistrados) {
            String json = consumoAPI.obtenerDatos(URL_BASE + "?search=" + titulo.replace(" ", "+"));
            Datos datosBusqueda = conversor.obtenerDatos(json, Datos.class);
            datosBusqueda.resultados().forEach(libro ->
                    libro.autor().forEach(autor -> {
                        int birthYear = Integer.parseInt(autor.fechaDeNacimiento());
                        if (birthYear <= ano) {
                            autoresVivos.add(autor.nombre());
                        }
                    })
            );
        }

        if (autoresVivos.isEmpty()) {
            System.out.println("No hay autores vivos en el año especificado.");
        } else {
            System.out.println("Autores vivos en el año " + ano + ":");
            autoresVivos.forEach(System.out::println);
        }
    }

    private void listarLibrosPorIdioma() {
        System.out.println("Ingrese el idioma para listar los libros:");
        String idioma = teclado.nextLine();
        List<String> librosPorIdioma = new ArrayList<>();

        for (String titulo : librosRegistrados) {
            String json = consumoAPI.obtenerDatos(URL_BASE + "?search=" + titulo.replace(" ", "+"));
            Datos datosBusqueda = conversor.obtenerDatos(json, Datos.class);
            datosBusqueda.resultados().forEach(libro -> {
                if (libro.idiomas().contains(idioma)) {
                    librosPorIdioma.add(libro.titulo());
                }
            });
        }

        if (librosPorIdioma.isEmpty()) {
            System.out.println("No hay libros en el idioma especificado.");
        } else {
            System.out.println("Libros en el idioma " + idioma + ":");
            librosPorIdioma.forEach(System.out::println);
        }

    }
}
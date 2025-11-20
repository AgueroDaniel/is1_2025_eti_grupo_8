
 -- Elimina la tabla 'users' si ya existe para asegurar un inicio limpio
DROP TABLE IF EXISTS users;
DROP TABLE IF EXISTS materia;
DROP TABLE IF EXISTS docente;
DROP TABLE IF EXISTS persona;

 -- Crea la tabla 'users' con los campos originales, adaptados para SQLite
CREATE TABLE users (
    id INTEGER PRIMARY KEY AUTOINCREMENT, -- Clave primaria autoincremental para SQLite
    name TEXT NOT NULL UNIQUE,          -- Nombre de usuario (TEXT es el tipo de cadena recomendado para SQLite), con restricción UNIQUE
    password TEXT NOT NULL           -- Contraseña hasheada (TEXT es el tipo de cadena recomendado para SQLite)
);
CREATE TABLE persona (
    dni INTEGER PRIMARY KEY,
    realName TEXT NOT NULL,
    surname TEXT NOT NULL
);
CREATE TABLE docente (
    dni INTEGER PRIMARY KEY,
    departament TEXT NOT NULL,
    correo TEXT NOT NULL UNIQUE,
    FOREIGN KEY (dni) REFERENCES persona(dni)
     ON DELETE CASCADE
     ON UPDATE CASCADE
);
CREATE TABLE materia (
    encargado INTEGER PRIMARY KEY,
    nombreMateria TEXT NOT NULL,
    id_carrera INTEGER,
    FOREIGN KEY (encargado) REFERENCES docente(dni)
     ON DELETE CASCADE
     ON UPDATE CASCADE
);
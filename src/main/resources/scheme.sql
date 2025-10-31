-- Elimina la tabla 'users' si ya existe para asegurar un inicio limpio
DROP TABLE IF EXISTS users;

-- Crea la tabla 'users' con los campos originales, adaptados para SQLite
CREATE TABLE users (
    id INTEGER PRIMARY KEY AUTOINCREMENT, -- Clave primaria autoincremental para SQLite
    name TEXT NOT NULL UNIQUE,          -- Nombre de usuario (TEXT es el tipo de cadena recomendado para SQLite), con restricción UNIQUE
    password TEXT NOT NULL           -- Contraseña hasheada (TEXT es el tipo de cadena recomendado para SQLite)
);

CREATE TABLE persona (
    dni TEXT PRIMARY KEY UNIQUE,
    name TEXT NOT NULL,
    surname TEXT NOT NULL
);

CREATE TABLE docente (
    dni TEXT PRIMARY KEY,
    departament TEXT NOT NULL,
    course TEXT NOT NULL,
    FOREIGN KEY (dni) REFERENCES persona(dni)
     ON DELETE CASCADE
     ON UPDATE CASCADE
);

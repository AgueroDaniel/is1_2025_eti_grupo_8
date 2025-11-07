-- Elimina la tabla 'users' si ya existe para asegurar un inicio limpio
DROP TABLE IF EXISTS users;

-- Crea la tabla 'users' con los campos originales, adaptados para SQLite
CREATE TABLE users (
    id INTEGER PRIMARY KEY AUTOINCREMENT, -- Clave primaria autoincremental para SQLite
    name TEXT NOT NULL UNIQUE,          -- Nombre de usuario (TEXT es el tipo de cadena recomendado para SQLite), con restricción UNIQUE
    password TEXT NOT NULL           -- Contraseña hasheada (TEXT es el tipo de cadena recomendado para SQLite)
<<<<<<< HEAD
);
=======
);

CREATE TABLE persona (
    dni INTEGER PRIMARY KEY,
    realName TEXT NOT NULL,
    surname TEXT NOT NULL
);

CREATE TABLE docente (
    dni INTEGER PRIMARY KEY,
    departament TEXT NOT NULL,
    correo TEXT NOT NULL,
    FOREIGN KEY (dni) REFERENCES persona(dni)
     ON DELETE CASCADE
     ON UPDATE CASCADE
);
>>>>>>> c1368f1 (terminado, no le cambien el rosaaa)

FORK del proyecto de ingenieria de software 2025.

Cosigna 1 (version para participantes):
https://docs.google.com/document/d/1SJkfOCZ_rqo_mkSTvodMJvToGczDnYoRBzHYEXGRNkk/edit?tab=t.0

Para abrir la Bdd de sqlite3

Posicionate en db 

cd C:\Users\ayele\Documents\Ingenieria1\is1_2025_eti_grupo_8\db

ahora abri sql

sqlite3 dev.db 

ahora fijate las tablas que tenes creadas

.tables

tiene que salir las 4 docente usuario, materia y persona

si no existen crealas con 


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

una ves que ya las creas, verifica con .tables te tienen que salir las 3

ahora ya deberia funcionar


Para compilar el proyecto pararse una carpeta antes que target

mvn clean package

java -jar "target\proye-is-1.0-SNAPSHOT.jar"

ahora ya podes probar la pagina, para eliminar tenes que hacerlo desde la bdd

C:\Users\ayele\Documents\Ingenieria1\is1_2025_eti_grupo_8


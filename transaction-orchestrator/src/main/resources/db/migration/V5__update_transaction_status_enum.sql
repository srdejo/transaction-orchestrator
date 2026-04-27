-- Renombrar el valor 'COMPLETED' a 'SUCCESS' en el tipo transaction_status
ALTER TYPE transaction_status RENAME VALUE 'COMPLETED' TO 'SUCCESS';

@echo off
:: Establecer ruta base
set BASE_DIR=%~dp0

:: Ejecutar aplicación Java
java --module-path "%BASE_DIR%..\lib\javafx-sdk-22.0.1\lib" --add-modules javafx.controls,javafx.fxml,javafx.media -cp "%BASE_DIR%.\CHESS.jar;%BASE_DIR%..\lib\postgresql-42.7.3.jar" aplicacion.Main

:: Pausar para ver cualquier mensaje de error o salida
pause

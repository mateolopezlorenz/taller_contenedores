# Dockerfile para desplegar la aplicación WAR en Tomcat
FROM tomcat:10.1.13-jdk17

# Borrar la aplicación default de Tomcat
RUN rm -rf /usr/local/tomcat/webapps/*

# Copiar el WAR generado por Maven al directorio webapps de Tomcat
COPY target/taller-0.0.1-SNAPSHOT.war /usr/local/tomcat/webapps/ROOT.war

# Exponer el puerto de Tomcat
EXPOSE 8080

# Comando por defecto para ejecutar Tomcat
CMD ["catalina.sh", "run"]

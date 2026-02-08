# script-prueba2.ps1
# Script de prueba para el taller: crea coches, mecánicos, reparaciones y prueba errores

# -------------------
# Función auxiliar para POST con manejo de errores
function Post-Object($url, $body) {
    try {
        return Invoke-RestMethod -Uri $url -Method POST -Headers @{ "Content-Type" = "application/json" } -Body $body
    } catch {
        Write-Host "Error POST a $url con body $body"
        Write-Host $_.Exception.Response.StatusCode.Value__
    }
}

# -------------------
# Crear 10 coches
Write-Host "`n--- Creando Coches ---"
$coches = @()
$coches += Post-Object "http://localhost:8080/coches" '{ "matricula":"C1AAA","marca":"Toyota","modelo":"Corolla"}'
$coches += Post-Object "http://localhost:8080/coches" '{ "matricula":"C2BBB","marca":"Honda","modelo":"Civic"}'
$coches += Post-Object "http://localhost:8080/coches" '{ "matricula":"C3CCC","marca":"Ford","modelo":"Focus"}'
$coches += Post-Object "http://localhost:8080/coches" '{ "matricula":"C4DDD","marca":"BMW","modelo":"320i"}'
$coches += Post-Object "http://localhost:8080/coches" '{ "matricula":"C5EEE","marca":"Audi","modelo":"A4"}'
$coches += Post-Object "http://localhost:8080/coches" '{ "matricula":"C6FFF","marca":"Mercedes","modelo":"C200"}'
$coches += Post-Object "http://localhost:8080/coches" '{ "matricula":"C7GGG","marca":"Nissan","modelo":"Qashqai"}'
$coches += Post-Object "http://localhost:8080/coches" '{ "matricula":"C8HHH","marca":"Kia","modelo":"Rio"}'
$coches += Post-Object "http://localhost:8080/coches" '{ "matricula":"C9III","marca":"Peugeot","modelo":"208"}'
$coches += Post-Object "http://localhost:8080/coches" '{ "matricula":"C10JJJ","marca":"Renault","modelo":"Clio"}'

Write-Host "`nCoches creados:"
Invoke-RestMethod -Uri "http://localhost:8080/coches" -Method GET | Format-Table

# -------------------
# Crear 5 mecánicos
Write-Host "`n--- Creando Mecanicos ---"
$mecanicos = @()
$mecanicos += Post-Object "http://localhost:8080/mecanicos" '{ "nombre":"Luis Martinez","especialidad":"Motor"}'
$mecanicos += Post-Object "http://localhost:8080/mecanicos" '{ "nombre":"Ana Gomez","especialidad":"Electricidad"}'
$mecanicos += Post-Object "http://localhost:8080/mecanicos" '{ "nombre":"Pedro Lopez","especialidad":"Neumaticos"}'
$mecanicos += Post-Object "http://localhost:8080/mecanicos" '{ "nombre":"Maria Ruiz","especialidad":"Suspension"}'
$mecanicos += Post-Object "http://localhost:8080/mecanicos" '{ "nombre":"Jorge Perez","especialidad":"Frenos"}'

Write-Host "`nMecanicos creados:"
Invoke-RestMethod -Uri "http://localhost:8080/mecanicos" -Method GET | Format-Table

# -------------------
# Crear 10 reparaciones
Write-Host "`n--- Creando Reparaciones ---"
$reparaciones = @()
$reparaciones += Post-Object "http://localhost:8080/reparaciones" '{ "coche": {"id":1},"mecanico":{"id":1},"fecha":"2026-02-08","descripcion":"Cambio de aceite","horas":2,"precio":50.0 }'
$reparaciones += Post-Object "http://localhost:8080/reparaciones" '{ "coche": {"id":2},"mecanico":{"id":2},"fecha":"2026-02-08","descripcion":"Revision frenos","horas":3,"precio":75.0 }'
$reparaciones += Post-Object "http://localhost:8080/reparaciones" '{ "coche": {"id":3},"mecanico":{"id":3},"fecha":"2026-02-08","descripcion":"Cambio ruedas","horas":4,"precio":100.0 }'
$reparaciones += Post-Object "http://localhost:8080/reparaciones" '{ "coche": {"id":4},"mecanico":{"id":4},"fecha":"2026-02-08","descripcion":"Suspension","horas":5,"precio":150.0 }'
$reparaciones += Post-Object "http://localhost:8080/reparaciones" '{ "coche": {"id":5},"mecanico":{"id":5},"fecha":"2026-02-08","descripcion":"Alineacion","horas":2,"precio":60.0 }'
$reparaciones += Post-Object "http://localhost:8080/reparaciones" '{ "coche": {"id":6},"mecanico":{"id":1},"fecha":"2026-02-08","descripcion":"Cambio aceite","horas":2,"precio":50.0 }'
$reparaciones += Post-Object "http://localhost:8080/reparaciones" '{ "coche": {"id":7},"mecanico":{"id":2},"fecha":"2026-02-08","descripcion":"Revision frenos","horas":3,"precio":75.0 }'
$reparaciones += Post-Object "http://localhost:8080/reparaciones" '{ "coche": {"id":8},"mecanico":{"id":3},"fecha":"2026-02-08","descripcion":"Cambio ruedas","horas":4,"precio":100.0 }'
$reparaciones += Post-Object "http://localhost:8080/reparaciones" '{ "coche": {"id":9},"mecanico":{"id":4},"fecha":"2026-02-08","descripcion":"Suspension","horas":5,"precio":150.0 }'
$reparaciones += Post-Object "http://localhost:8080/reparaciones" '{ "coche": {"id":10},"mecanico":{"id":5},"fecha":"2026-02-08","descripcion":"Alineacion","horas":2,"precio":60.0 }'

Write-Host "`nReparaciones creadas:"
Invoke-RestMethod -Uri "http://localhost:8080/reparaciones" -Method GET | Format-Table

# -------------------
# Sección de errores intencionados
Write-Host "`n--- Errores Intencionados ---"

# Crear coche con matrícula repetida
try {
    Invoke-RestMethod -Uri "http://localhost:8080/coches" -Method POST -Headers @{ "Content-Type" = "application/json" } -Body '{"matricula":"C1AAA","marca":"Toyota","modelo":"Corolla"}'
} catch {
    Write-Host "Error al crear coche con matricula duplicada"
}

# Acceder a coche que no existe
try {
    Invoke-RestMethod -Uri "http://localhost:8080/coches/999" -Method GET
} catch {
    Write-Host "Error al hacer GET de coche inexistente"
}

# POST reparacion con coche inexistente
try {
    Invoke-RestMethod -Uri "http://localhost:8080/reparaciones" -Method POST -Headers @{ "Content-Type" = "application/json" } -Body '{"coche":{"id":999},"mecanico":{"id":1},"fecha":"2026-02-08","descripcion":"Prueba","horas":1,"precio":10.0}'
} catch {
    Write-Host "Error al crear reparacion con coche inexistente"
}

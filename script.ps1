# -------------------------
# Crear Coches
# -------------------------
$coche1 = Invoke-RestMethod -Uri "http://localhost:8080/coches" -Method POST -Headers @{ "Content-Type" = "application/json" } -Body '{"matricula":"1111AAA","marca":"Toyota","modelo":"Corolla"}'
$coche2 = Invoke-RestMethod -Uri "http://localhost:8080/coches" -Method POST -Headers @{ "Content-Type" = "application/json" } -Body '{"matricula":"2222BBB","marca":"Honda","modelo":"Civic"}'

Write-Host "Coches:"
Invoke-RestMethod -Uri "http://localhost:8080/coches" -Method GET | Format-Table

# -------------------------
# Crear Mecánicos
# -------------------------
$mecanico1 = Invoke-RestMethod -Uri "http://localhost:8080/mecanicos" -Method POST -Headers @{ "Content-Type" = "application/json" } -Body '{"nombre":"Luis Martínez","especialidad":"Motor"}'
$mecanico2 = Invoke-RestMethod -Uri "http://localhost:8080/mecanicos" -Method POST -Headers @{ "Content-Type" = "application/json" } -Body '{"nombre":"Ana Gómez","especialidad":"Electricidad"}'

Write-Host "`nMecánicos:"
Invoke-RestMethod -Uri "http://localhost:8080/mecanicos" -Method GET | Format-Table

# -------------------------
# Crear Reparaciones
# -------------------------
$reparacion1 = Invoke-RestMethod -Uri "http://localhost:8080/reparaciones" -Method POST -Headers @{ "Content-Type" = "application/json" } -Body ("{`"coche`":{`"id`":" + $coche1.id + "},`"mecanico`":{`"id`":" + $mecanico1.id + "},`"horas`":3,`"precio`":150.0}")
$reparacion2 = Invoke-RestMethod -Uri "http://localhost:8080/reparaciones" -Method POST -Headers @{ "Content-Type" = "application/json" } -Body ("{`"coche`":{`"id`":" + $coche2.id + "},`"mecanico`":{`"id`":" + $mecanico2.id + "},`"horas`":2,`"precio`":100.0}")

Write-Host "`nReparaciones:"
Invoke-RestMethod -Uri "http://localhost:8080/reparaciones" -Method GET | Format-Table

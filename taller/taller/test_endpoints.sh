#!/bin/bash
# ============================================================
# test_endpoints.sh - Verificación funcional de todos los endpoints
# ============================================================
#
# DÓNDE EJECUTAR:
#   Desde la carpeta taller/taller del proyecto, con la aplicación
#   levantada mediante Docker Compose.
#
# PASOS PREVIOS:
#   cd taller/taller
#   mvn clean package -DskipTests
#   docker compose up -d --build
#   # Esperar ~15 segundos a que la aplicación arranque
#
# EJECUCIÓN:
#   chmod +x test_endpoints.sh
#   ./test_endpoints.sh
#
# ============================================================

BASE_URL="http://localhost:8080"
PASS=0
FAIL=0

check() {
  local description="$1"
  local expected_code="$2"
  local actual_code="$3"
  local body="$4"

  if [ "$actual_code" = "$expected_code" ]; then
    echo "  ✅ $description (HTTP $actual_code)"
    PASS=$((PASS + 1))
  else
    echo "  ❌ $description (esperado HTTP $expected_code, recibido HTTP $actual_code)"
    echo "     Respuesta: $body"
    FAIL=$((FAIL + 1))
  fi
}

echo ""
echo "=========================================="
echo " Verificación de endpoints - API Taller"
echo "=========================================="
echo ""

# --- 1. Esperar a que la aplicación esté lista ---
echo "⏳ Esperando a que la aplicación esté lista..."
for i in $(seq 1 30); do
  if curl -s "$BASE_URL/coches" > /dev/null 2>&1; then
    echo "   ¡Aplicación lista!"
    break
  fi
  if [ "$i" = "30" ]; then
    echo "   ❌ La aplicación no respondió tras 30 intentos."
    echo "   Asegúrate de haber ejecutado: docker compose up -d --build"
    exit 1
  fi
  echo "   Intento $i: esperando..."
  sleep 5
done
echo ""

# ==========================================
# COCHES
# ==========================================
echo "--- COCHES ---"
echo ""

# POST /coches - Crear un coche nuevo
echo "1. POST /coches (Crear coche)"
response=$(curl -s -w "\n%{http_code}" -X POST "$BASE_URL/coches" \
  -H "Content-Type: application/json" \
  -d '{"matricula":"TEST123","marca":"Tesla","modelo":"Model 3"}')
body=$(echo "$response" | sed '$d')
status=$(echo "$response" | tail -1)
check "Crear coche" "200" "$status" "$body"
echo "     $body"
echo ""

# GET /coches - Listar todos
echo "2. GET /coches (Listar todos)"
response=$(curl -s -w "\n%{http_code}" "$BASE_URL/coches")
body=$(echo "$response" | sed '$d')
status=$(echo "$response" | tail -1)
check "Listar coches" "200" "$status" "$body"
echo ""

# GET /coches/1 - Obtener por ID
echo "3. GET /coches/1 (Obtener por ID)"
response=$(curl -s -w "\n%{http_code}" "$BASE_URL/coches/1")
body=$(echo "$response" | sed '$d')
status=$(echo "$response" | tail -1)
check "Obtener coche por ID" "200" "$status" "$body"
echo "     $body"
echo ""

# GET /coches/matricula/TEST123 - Obtener por matrícula
echo "4. GET /coches/matricula/TEST123 (Obtener por matrícula)"
response=$(curl -s -w "\n%{http_code}" "$BASE_URL/coches/matricula/TEST123")
body=$(echo "$response" | sed '$d')
status=$(echo "$response" | tail -1)
check "Obtener coche por matrícula" "200" "$status" "$body"
echo "     $body"
echo ""

# ==========================================
# MECÁNICOS
# ==========================================
echo "--- MECÁNICOS ---"
echo ""

# POST /mecanicos - Crear mecánico
echo "5. POST /mecanicos (Crear mecánico)"
response=$(curl -s -w "\n%{http_code}" -X POST "$BASE_URL/mecanicos" \
  -H "Content-Type: application/json" \
  -d '{"nombre":"Pedro García","especialidad":"Electricidad"}')
body=$(echo "$response" | sed '$d')
status=$(echo "$response" | tail -1)
check "Crear mecánico" "200" "$status" "$body"
echo "     $body"
echo ""

# GET /mecanicos - Listar todos
echo "6. GET /mecanicos (Listar todos)"
response=$(curl -s -w "\n%{http_code}" "$BASE_URL/mecanicos")
body=$(echo "$response" | sed '$d')
status=$(echo "$response" | tail -1)
check "Listar mecánicos" "200" "$status" "$body"
echo ""

# GET /mecanicos/1 - Obtener por ID
echo "7. GET /mecanicos/1 (Obtener por ID)"
response=$(curl -s -w "\n%{http_code}" "$BASE_URL/mecanicos/1")
body=$(echo "$response" | sed '$d')
status=$(echo "$response" | tail -1)
check "Obtener mecánico por ID" "200" "$status" "$body"
echo "     $body"
echo ""

# ==========================================
# REPARACIONES
# ==========================================
echo "--- REPARACIONES ---"
echo ""

# POST /reparaciones - Crear reparación
echo "8. POST /reparaciones (Crear reparación)"
response=$(curl -s -w "\n%{http_code}" -X POST "$BASE_URL/reparaciones" \
  -H "Content-Type: application/json" \
  -d '{"coche":{"id":1},"mecanico":{"id":1},"fecha":"2025-06-15","descripcion":"Cambio de aceite","horas":2,"precio":120.0}')
body=$(echo "$response" | sed '$d')
status=$(echo "$response" | tail -1)
check "Crear reparación" "200" "$status" "$body"
echo "     $body"
echo ""

# GET /reparaciones - Listar todas
echo "9. GET /reparaciones (Listar todas)"
response=$(curl -s -w "\n%{http_code}" "$BASE_URL/reparaciones")
body=$(echo "$response" | sed '$d')
status=$(echo "$response" | tail -1)
check "Listar reparaciones" "200" "$status" "$body"
echo ""

# GET /reparaciones/1 - Obtener por ID
echo "10. GET /reparaciones/1 (Obtener por ID)"
response=$(curl -s -w "\n%{http_code}" "$BASE_URL/reparaciones/1")
body=$(echo "$response" | sed '$d')
status=$(echo "$response" | tail -1)
check "Obtener reparación por ID" "200" "$status" "$body"
echo "     $body"
echo ""

# GET /reparaciones/coche/1 - Por coche
echo "11. GET /reparaciones/coche/1 (Por coche)"
response=$(curl -s -w "\n%{http_code}" "$BASE_URL/reparaciones/coche/1")
body=$(echo "$response" | sed '$d')
status=$(echo "$response" | tail -1)
check "Reparaciones por coche" "200" "$status" "$body"
echo ""

# GET /reparaciones/mecanico/1 - Por mecánico
echo "12. GET /reparaciones/mecanico/1 (Por mecánico)"
response=$(curl -s -w "\n%{http_code}" "$BASE_URL/reparaciones/mecanico/1")
body=$(echo "$response" | sed '$d')
status=$(echo "$response" | tail -1)
check "Reparaciones por mecánico" "200" "$status" "$body"
echo ""

# ==========================================
# RESUMEN
# ==========================================
echo "=========================================="
echo " Resultado: $PASS correctos, $FAIL fallidos"
echo "=========================================="
echo ""

if [ "$FAIL" -gt 0 ]; then
  exit 1
fi

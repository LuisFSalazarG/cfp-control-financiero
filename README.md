# Control Financiero Personal - MVP (Equipo 3)

Proyecto de Desarrollo de Software — Ingeniería de Sistemas — UAM — 2026-3

Sistema para que una persona registre sus ingresos y gastos en cinco categorías fijas,
defina un gasto mensual, fije gastos por categoría, consulte un Dashboard y reciba
alertas con sugerencia al llegar al 70 %, 80 % y 90 % de lo definido.

- **Jira:** https://equipo3pds-uam.atlassian.net/jira/software/projects/CFP/boards/1/backlog
- **Sprint actual:** EQUIPO-03 · Sprint 2 · P0 (05/11/2026 - 19/11/2026)
- **Ítems P0 del sprint:** CFP-15 (RF-07 gasto mensual), CFP-16 (RF-08 total y porcentaje), CFP-17 (RF-09 alertas 70/80/90)
- **Versión:** v0.2.0

## Equipo y roles (tercer corte)

| Rol | Integrante |
|---|---|
| Producto | Luis Eduardo García Vázquez |
| Backend | Yahir Alfredo Hernández Flórez |
| Datos | Luis Fernando Salazar Grillo |
| Calidad | Santiago Triana Carvajal |
| Integración | Martín Pardo Aguilar |

## Estructura del repositorio

```
docs/      entregables por rol (contrato, modelo, matriz, producto, evidencias)
db/        scripts SQL y migraciones de la base de datos
backend/   API REST en Spring Boot (Java 21)
```

## Funcionalidad del Sprint 2

- **Gasto mensual (RF-07):** `POST /api/presupuestos` define el monto límite del mes.
- **Total y porcentaje (RF-08):** `GET /api/presupuestos/alertas` devuelve el porcentaje gastado.
- **Alertas 70/80/90 (RF-09):** alerta con sugerencia al llegar a cada nivel; no se repite en el mismo nivel y se reinicia cada mes.
- Contrato completo: [docs/contrato-presupuesto.md](docs/contrato-presupuesto.md).
- Matriz de pruebas: [docs/matriz-pruebas-sprint2.md](docs/matriz-pruebas-sprint2.md).

## Cómo ejecutar el backend

```bash
cd backend
mvn spring-boot:run     # levanta la API
mvn test                # ejecuta las pruebas
```

## Cómo trabajamos

Las reglas de ramas, commits y pull requests están en [docs/flujo-trabajo.md](docs/flujo-trabajo.md).
Resumen: `main` ← `develop` ← `feature/CFP-XX-descripcion`, y el ID de Jira va siempre
en la rama, el commit y el PR.

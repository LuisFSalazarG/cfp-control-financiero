# Control Financiero Personal – MVP (Equipo 3)

Proyecto de Desarrollo de Software · Ingeniería de Sistemas · UAM · 2026-3

Sistema para que una persona registre sus ingresos y gastos en cinco categorías fijas,
defina un gasto mensual, fije gastos por categoría, consulte un Dashboard y reciba
alertas con sugerencia al llegar al 70 % de lo definido.

- **Jira:** https://equipo3pds-uam.atlassian.net/jira/software/projects/CFP/boards/1/backlog
- **Sprint actual:** EQUIPO-03 · Sprint 1 · P0 (16/10/2026 – 30/10/2026)
- **Ítem P0 del sprint:** CFP-10 · RF-03 Registrar gasto con categoría

## Equipo y roles (segundo corte)

| Rol | Integrante |
|---|---|
| Producto | Santiago Triana Carvajal |
| Integración | Luis Fernando Salazar |
| Backend | Luis Eduardo García Vázquez |
| Datos | Martin Pardo Aguilar |
| Calidad | Yahir Alfredo Hernández Flórez |

## Estructura del repositorio

```
docs/      entregables por rol (contrato, modelo, matriz, producto)
db/        scripts SQL de la base de datos
backend/   código del servicio (se crea en el siguiente paso)
```

## Cómo trabajamos

Las reglas de ramas, commits y pull requests están en [docs/flujo-trabajo.md](docs/flujo-trabajo.md).
Resumen: `main` ← `develop` ← `feature/CFP-XX-descripcion`, y el ID de Jira va siempre
en la rama, el commit y el PR.

# Flujo de trabajo y repositorio

Responsable: Luis Fernando Salazar (Integración) · Subtarea Jira: **CFP-23**

## 1. Repositorio

- GitHub, público: `cfp-control-financiero`.
- Los cinco integrantes y la docente son colaboradores. Aunque cualquiera puede ver el
  repositorio, solo los colaboradores pueden subir cambios.

## 2. Ramas

| Rama | Para qué sirve | Quién fusiona |
|---|---|---|
| `main` | Versión estable. Nadie sube directo. | Integración, al cierre del sprint |
| `develop` | Integración del trabajo del sprint. Nadie sube directo. | Cualquier PR aprobado |
| `feature/*` | Una rama por ítem o subtarea de Jira. Siempre sale de `develop`. | Se borra sola después de fusionar |

> Única excepción: la carga inicial del repositorio (README, este archivo y la plantilla de PR)
> se subió directo a `main` porque el repositorio estaba vacío.

## 3. Nombre de las ramas

`feature/CFP-<número>-<descripcion-corta>`

Reglas: todo en minúscula (menos `CFP`), palabras separadas con guiones, sin tildes, sin ñ y sin espacios.

Ejemplos del Sprint 1:

- `feature/CFP-10-registrar-gasto`
- `feature/CFP-24-contrato-gastos`
- `feature/CFP-25-modelo-datos`
- `feature/CFP-26-matriz-pruebas`

## 4. Formato de commit

`tipo(CFP-XX): descripción corta en presente`

| Tipo | Cuándo |
|---|---|
| `feat` | Funcionalidad nueva |
| `fix` | Corrección de un error |
| `docs` | Documentación (contrato, modelo, matriz) |
| `test` | Pruebas |
| `chore` | Configuración, dependencias |

Ejemplos: `docs(CFP-24): agrega contrato REST de gastos` ·
`feat(CFP-10): rechaza gasto con monto menor o igual a cero`

## 5. Regla de Pull Request

1. Siempre hacia `develop`, nunca directo a `main`.
2. El título empieza con el ID de Jira: `CFP-24 Contrato REST de gastos`.
3. La descripción dice qué RF, RN y CA cubre (usar la plantilla del PR).
4. Lo aprueba **1 revisor de otro rol**.
5. No se fusiona si no compila o si alguna prueba está en rojo.
6. Se fusiona con **Squash and merge**: cada PR queda como un solo commit en `develop`.
7. La rama se borra sola después de fusionar.
8. `develop` pasa a `main` solo al cierre del sprint, con visto bueno de Integración y Calidad,
   y se marca con una etiqueta de versión: `v0.1.0` (Sprint 1), `v0.2.0` (Sprint 2), `v0.3.0` (Sprint 3).

## 6. Definition of Done técnica

Un ítem está **Listo** cuando:

- [ ] Compila sin errores.
- [ ] Las pruebas están en verde.
- [ ] Calidad verificó el CA y dejó evidencia.
- [ ] El PR fue revisado y aprobado por otro rol.
- [ ] El ID de Jira está en la rama, el commit y el PR.
- [ ] La subtarea está en "Listo" con el enlace al PR.

## 7. Protección de ramas

`main` y `develop` están protegidas en GitHub:

- No se puede subir directo: todo entra por Pull Request.
- Cada Pull Request necesita 1 aprobación de otro integrante.
- Los comentarios de la revisión deben quedar resueltos antes de fusionar.

## 8. Archivos que no se suben

Cuando se cree el proyecto Spring Boot se agrega un `.gitignore` para no subir compilados
(`target/`), configuración del editor (`.idea/`, `.vscode/`) ni contraseñas (`.env`).

## 9. Enlace Jira – Git

La aplicación **GitHub for Jira** está conectada al espacio CFP. Al escribir `CFP-XX`
en la rama, el commit o el PR, Jira lo muestra en la sección *Desarrollo* de la historia.

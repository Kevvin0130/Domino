# B6 – Secciones del informe IEEE (parte de Persona B)

> Texto listo para pasar a la plantilla IEEE (2 columnas). Los corchetes [ ] son datos que ustedes deben
> completar o verificar. Nombre del juego: **decidan uno** (el código dice "Domino Conquest"; el mockup y el README dicen "Dominó Caos").

## I. INTRODUCCIÓN
Este informe describe el desarrollo de [Domino Conquest], una aplicación Android multijugador de dominó con
poderes especiales y eventos aleatorios, construida con Kotlin, Jetpack Compose, la arquitectura MVVM y Firebase
(Authentication y Cloud Firestore). El objetivo del taller es aplicar programación por componentes: separar
la lógica del juego (dominio), el acceso a datos (repositorios) y la interfaz (pantallas y componentes reutilizables)
para que cada parte pueda construirse, probarse y reemplazarse de forma independiente.
Los jugadores crean o se unen a una sala mediante un código de 6 caracteres, juegan por turnos con estado
sincronizado en tiempo real y, al terminar, consultan su resultado y su historial.

## IV. DISEÑO DE LA INTERFAZ DE USUARIO
### A. Lineamientos visuales
Tema oscuro basado en verde (fondo `VerdeProfundo #102923`, superficies `VerdeSuperficie #193A30`, acción principal
`VerdeEsmeralda #208653`), dorado (`#E4B65C`) para acentos y turno activo, y marfil (`#F5F0E5`) para fichas y texto.
Un único `DominoTheme` aplica colores y tipografía a todas las pantallas. Los textos de la interfaz están en español.

### B. Navegación
Flujo: Bienvenida → Inicio de sesión / Registro / Recuperar contraseña → Menú principal → (Crear sala | Unirse a sala |
Historial | Cómo jugar) → Sala de espera → Tablero → Resultado → (Jugar de nuevo | Menú).
`AppNavigation` solo declara rutas y conecta ViewModels; no contiene reglas de negocio. Si hay una sesión guardada,
la app abre directamente el menú. Las rutas de partida llevan el código de la sala como argumento.
La navegación entre Sala, Tablero y Resultado es **dirigida por el estado** (`GamePhase`), por lo que todos los
jugadores cambian de pantalla a la vez cuando el estado de Firestore cambia.

### C. Pantallas
| Pantalla | Propósito |
|---|---|
| Bienvenida / Inicio de sesión / Registro / Recuperar | Autenticación con correo (Google opcional) |
| Menú principal | Acceso a crear, unirse, historial, ayuda y cerrar sesión |
| Crear / Unirse a partida | Obtener o ingresar el código de sala (conectadas a `LobbyViewModel`) |
| Sala de espera | Código, jugadores presentes, inicio de partida (solo anfitrión, mínimo 2 jugadores) |
| Tablero de juego | Jugadores con turno y puntaje, ronda, pozo, cadena de fichas, mano y controles |
| Selección de poderes | Comodín, Salto, Intercambio e Inversión con usos restantes |
| Evento aleatorio | Aviso a pantalla completa del evento que acaba de ocurrir |
| Resultado | Ganador, ranking y desempate por menos poderes usados |
| Historial | Partidas anteriores del usuario |

### D. Componentes reutilizables
| Componente | Responsabilidad | Entradas principales |
|---|---|---|
| `FichaView` | Dibuja una ficha con puntos (vertical/horizontal, seleccionada, jugable, oculta) | `Tile`, estado visual, `onClick` |
| `TableroView` | Cadena de fichas y extremos abiertos | `Board` |
| `JugadorCard` | Nombre, puntaje, fichas y turno siempre visibles | `Player`, `esTurno` |
| `PoderButton` | Poder con usos restantes; se bloquea sin usos | `PowerType`, usos |
| `ControlesPartida` | Colocar izquierda/derecha, robar o pasar, poderes, abandonar | permisos booleanos + callbacks |
| `BotonDomino` | Botón base de toda la app (principal/secundario) | texto, `enabled` |

Los componentes **no conocen** el motor ni Firebase: reciben datos y emiten eventos (*state hoisting*). Por eso se
probaron con datos falsos desde el inicio (`SampleState`) y pueden reutilizarse en varias pantallas.

### E. Relación con MVVM
La interfaz solo observa `StateFlow` (`AuthUiState`, `LobbyUiState`, `GameUiState`, `HistorialUiState`) y llama a funciones
de los ViewModels. Las jugadas válidas que se resaltan en el tablero provienen de `GameUiState.validMoves`, calculadas por el
motor; la UI nunca valida reglas por su cuenta. Los ViewModels de partida se comparten entre Sala, Tablero y Resultado para
mantener una sola suscripción a Firestore.

## V. PRUEBAS
### A. Pruebas unitarias (Persona A)
El proyecto incluye `GameEngineTest` ([35] casos con `@Test`) y `GameMapperTest` ([4] casos). [Resumir aquí lo que cubren y su resultado.]

### B. Pruebas de interfaz (Persona B)
Con Compose Test (`createComposeRule`) se verificó el comportamiento de los componentes aislados ([12] casos en `ComponentesUiTest`):
- `FichaView`: reacciona al toque y la ficha oculta no revela sus valores.
- `JugadorCard`: muestra siempre el puntaje; el indicador "TURNO" solo aparece cuando corresponde.
- `PoderButton`: muestra nombre y usos; sin usos queda deshabilitado y no responde.
- `ControlesPartida`: fuera de turno solo permite abandonar; con jugada válida coloca por el lado elegido; con tablero vacío ofrece un único botón.
- `TableroView`: mensaje de tablero vacío y extremos abiertos correctos.

Resultado de la ejecución: [completar tras correrlas en un emulador/dispositivo: X de 12 aprobadas].
Pruebas manuales recomendadas con dos dispositivos: crear/unirse, jugada válida, robar del pozo, cada poder, evento aleatorio, abandono y reinicio.

## VI. CONCLUSIONES
- Separar dominio, datos y UI permitió trabajar en paralelo: la interfaz se construyó con datos de ejemplo mientras el motor y Firestore se desarrollaban.
- Mantener el estado completo de la partida en un único documento y dibujar la UI solo desde él evita inconsistencias entre jugadores.
- Los componentes sin conocimiento de reglas facilitan pruebas de UI sencillas y rápidas.
- Trabajo futuro: animaciones de fichas, sonidos, modo contra bots, reconexión a partidas en curso y traducción de los mensajes del motor al español.

## Referencias sugeridas (formato IEEE)
[1] Google, "Jetpack Compose," Android Developers. [Online]. Available: https://developer.android.com/jetpack/compose
[2] Google, "Guide to app architecture," Android Developers. [Online]. Available: https://developer.android.com/topic/architecture
[3] Google, "Cloud Firestore," Firebase Documentation. [Online]. Available: https://firebase.google.com/docs/firestore
[4] Google, "Firebase Authentication," Firebase Documentation. [Online]. Available: https://firebase.google.com/docs/auth

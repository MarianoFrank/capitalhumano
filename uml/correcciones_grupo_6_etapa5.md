# Devolución — Diagramas de Secuencia (Etapa 5)
## GRUPO 6

## Revisión de los cambios pedidos en la etapa anterior
En CU025 la Evaluación se crea recibiendo la cantidad de preguntas por bloque, en línea con lo pedido en la etapa 4. 

## Correcciones

### 1. Métodos que no existen en el diagrama de clases (CU025)
Los mensajes validarCandidatosDisponibles y validarCompetenciasEvaluables de ServicioEvaluacion, y buscarPorCandidatosYEstados de CuestionarioDAO, no están declarados en el diagrama de clases. Todo mensaje del diagrama de secuencia tiene que corresponder a un método de la clase que lo recibe. Agréguenlos al diagrama de clases con su firma completa.

### 2. Flujo alternativo 1.A (CU021)
"Generar con IA" y "Cancelar" están modelados como un alt de dos ramas, lo que obliga a pasar siempre por una de las dos y deja sin representar la carga manual, que es el flujo principal. Además, después de cancelar el diagrama continúa hasta Aceptar. Modelen "Generar con IA" como opt, y "Cancelar" como una salida que termina el caso de uso.

### 3. Validación de pregunta duplicada (CU021)
Para validar duplicados usan buscarFiltrado, que busca por "contiene", por lo que nombres parecidos se tomarían como iguales. Tampoco se valida el texto de la pregunta, que el CU exige. Además, factor.idCompetencia no es un atributo de Factor. Repongan en PreguntaDAO los métodos de existencia por nombre y por texto dentro del factor, que tenían en la entrega anterior.

### 4. Origen de los datos de la Evaluación (CU025)
Al crear la Evaluación se usan código, consultor, duración y cantidad de preguntas por bloque, pero ninguno llega como parámetro ni se obtiene en el diagrama. Muestren cómo se recupera el consultor autenticado y de dónde salen los parámetros de configuración.

### 5. Validación de competencias evaluables (CU025)
validarCompetenciasEvaluables solo recupera los ids de las competencias válidas. No obtiene las competencias del puesto, por lo que no puede determinar cuáles no se pueden evaluar. Agreguen la lectura del puesto en esa operación.

### 6. Búsqueda de candidatos (CU025)
buscarPorNrosCandidato recibe los ids de los candidatos, pero el método espera números de candidato. Usen el identificador correcto o agreguen una búsqueda por lista de ids.



## Observaciones

## 1. Vistas y controladores
Los diagramas usan vistas y controladores que no figuran en el diagrama de clases. Incorpórenlos en una capa de presentación para que el diseño sea consistente.

## 2. Métodos de las entidades
agregarOpcion en Pregunta y agregarCuestionario en Evaluación no están declarados, y tampoco los constructores con parámetros. Agréguenlos al diagrama de clases.

## 3. Activaciones en flujos alternativos
En CU021 y CU025 las ramas alternativas no cierran las activaciones de los participantes. Ciérrenlas en cada rama o después del alt.

## 4. Orden de las validaciones (CU021)
La validación de ponderaciones se invoca dentro de las ramas del alt, y la de datos obligatorios aparece después de consultar la base. Realicen primero las validaciones y usen el alt para los resultados. Además, el mensaje de pregunta duplicada debe indicar si la coincidencia es por nombre o por texto.


## 5. Mismo modelo para todos los cuestionarios (CU025)
El requerimiento especial pide que todos los cuestionarios de una evaluación se basen en el mismo modelo, incluyendo preguntas y opciones. Expliquen cómo lo garantizan, considerando que las preguntas se sortean en el primer ingreso del candidato.

## Veredicto
Buen trabajo en la separación de capas y en el uso de fragmentos. Incorporen las correcciones a los diagramas antes de la implementación (etapa 7), en especial las que afectan a CU020 y CU021.
En general el nivel del trabajo es muy bueno, destaco el uso de las practicas de implementacion de tecnologias Web y la estructuracion del API. Tambien es destacable la claridad del diagrama. Sigan asi!
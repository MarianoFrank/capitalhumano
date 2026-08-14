# Devolución — Diagrama de Clases (Entrega 2)
## GRUPO: Los elegidos



## 1. Jerarquía de gestores: revisar el contrato de la interfaz ABM
No todos los gestores tienen sentido de negocio para las tres operaciones de ABM (darAlta, darBaja, modificar):

GestorEvaluacion solo crea evaluaciones (CU025) y emite reportes (CU027, CU028) — no hay baja ni modificación de una evaluación en ningún CU del alcance.
GestorCuestionario no tiene alta/baja/modificación manual — el cuestionario se crea automáticamente al evaluar candidatos y se actualiza por el proceso batch (CU032).

Si estos gestores heredan de GestorBase y esta implementa ABM, están heredando métodos que no les corresponden semánticamente (riesgo de violar el principio de segregación de interfaces: un darBaja() que no hace nada o tira excepción es señal de que la interfaz no le correspondía a esa clase).
Sugerencia: apliquen segregación de interfaces. Que ABM la implementen solo los gestores que realmente hacen alta/baja/modificación con sentido de negocio (Puestos, Competencias, Factores, Preguntas, Opciones de Respuesta). Para GestorEvaluacion y GestorCuestionario, evalúen si necesitan heredar de GestorBase en absoluto, o si conviene una clase base más liviana sin ABM.
## 2. Candidato heredando de Usuario / RolUsuario
Según CU001, el Consultor se autentica contra el sistema corporativo con usuario y contraseña (pasos 4-5), mientras que el Candidato se autentica con tipo de documento, número de documento y clave de cuestionario (paso 3.A) — un mecanismo completamente distinto. Modelar a Candidato bajo la misma jerarquía que Usuario (que tiene nombreUsuario y contraseña) sugiere que comparte esos atributos, lo cual no es así. Revisen si Candidato debería pertenecer a esta jerarquía o si conviene modelarlo por fuera, fuera de RolUsuario.
## 3. Singleton aplicado a una jerarquía de herencia
GestorBase está marcada como Singleton y todas las subclases heredan de ella. El patrón Singleton clásico no se combina naturalmente con herencia simple (cada subclase necesitaría su propio mecanismo de instancia única).

# Consideraciones conceptuales — explicar criterio de diseño

## 4.Herencia de Competencia en CompetenciaTecnica y CompetenciaActitudinal
La única diferencia que menciona el enunciado entre ambas es quién las define (técnica depende del perfil buscado; actitudinal depende de la empresa cliente), no hay diferencia de atributos ni de comportamiento evidente en los casos de uso. Si no encuentran un atributo o método que difiera entre las dos subclases, la herencia es innecesaria, alcanzaría con un atributo tipo en Competencia. Si sí hay una diferencia de comportamiento que tienen en mente, inclúyanla en el diagrama para justificar la generalización.
## 5.  Parámetro "cantidad de preguntas por bloque"
El armado de bloques usa un parámetro configurable de cantidad de preguntas por bloque (CU026, observación 1), pero no aparece como atributo en ningún lado del modelo. Definan dónde se almacena (Cuestionario, una clase de configuración global, u otra alternativa).
## 6. Atributo respondido: Boolean en ItemCuestionario
Es potencialmente redundante: si ya existe la relación hacia Opcion seleccionada, el hecho de tener o no opciones asociadas ya indica si la pregunta fue respondida. Evalúen si conviene derivarlo en vez de almacenarlo, dado el riesgo de inconsistencia si quedan desincronizados.
## 7. RegistroAuditoria.descripcionElementoEliminado como único dato del elemento
El mecanismo de auditoría en sí está bien resuelto: la interfaz Auditable junto con el método protegido en GestorBase es un buen uso de interfaz y Template Method. Como detalle menor, evalúen si conviene que RegistroAuditoria guarde algo más estructurado que un string libre (por ejemplo, qué tipo de entidad era), pensando en necesidades futuras de consulta por tipo.

## Detalles de sintaxis:
Las relaciones entre los gestores y las clases de los tipos de datos gestionados no se incorporar con el estereotipo de relación de uso. Incorporen este cambio.
## VEREDICTO:
Incorporar correcciones para la proxima etapa del diagrama de clases. Les recomiendo tener incorporados los cambios para la proxima clase de consulta.

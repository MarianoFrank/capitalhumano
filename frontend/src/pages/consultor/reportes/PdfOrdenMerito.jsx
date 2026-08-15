import { Document, Page, Text, View, StyleSheet } from '@react-pdf/renderer';

const estilos = StyleSheet.create({
    pagina: { padding: 30, fontFamily: 'Helvetica' },
    titulo: { fontSize: 20, color: '#1f2937', fontWeight: 'bold' },
    subtitulo: { fontSize: 12, color: '#4b5563', marginTop: 4 },

    emitidoPor: { fontSize: 10, color: '#6b7280', marginTop: 8, marginBottom: 20 },

    tituloSeccionVerde: { fontSize: 14, color: '#1f2937', fontWeight: 'bold', marginBottom: 10, marginTop: 10 },
    tituloSeccionRoja: { fontSize: 14, color: '#1f2937', fontWeight: 'bold', marginBottom: 10, marginTop: 25 },

    tabla: { display: 'flex', width: 'auto', borderStyle: 'solid', borderWidth: 1, borderColor: '#e5e7eb', borderBottomWidth: 0 },
    filaTabla: { flexDirection: 'row', borderBottomWidth: 1, borderBottomColor: '#e5e7eb', minHeight: 24, alignItems: 'center' },
    filaEncabezado: { backgroundColor: '#f9fafb', fontWeight: 'bold' },
    celda: { padding: 5, fontSize: 10, color: '#374151' },

    colCandidatoA: { width: '30%', borderRightWidth: 1, borderRightColor: '#e5e7eb' },
    colDocA: { width: '25%', borderRightWidth: 1, borderRightColor: '#e5e7eb' },
    colNroA: { width: '15%', borderRightWidth: 1, borderRightColor: '#e5e7eb', textAlign: 'center' },
    colPuntajeA: { width: '15%', borderRightWidth: 1, borderRightColor: '#e5e7eb', textAlign: 'center', color: '#16a34a' },
    colAccesosA: { width: '15%', textAlign: 'center' },

    colCandidatoR: { width: '35%', borderRightWidth: 1, borderRightColor: '#e5e7eb' },
    colDocR: { width: '25%', borderRightWidth: 1, borderRightColor: '#e5e7eb' },
    colEstadoR: { width: '25%', borderRightWidth: 1, borderRightColor: '#e5e7eb', textAlign: 'center' },
    colPuntajeR: { width: '15%', textAlign: 'center' }
});

const traduccionesEstado = {
    'COMPLETED': 'Completado',
    'IN_PROGRESS': 'En Progreso',
    'ACTIVE': 'No iniciado',
    'INCOMPLETE': 'Incompleto',
    'NOT_ANSWERED': 'No Respondido'
};

export const PdfOrdenMerito = ({ datosReporte }) => (
    <Document>
        <Page size="A4" style={estilos.pagina}>
            <Text style={estilos.titulo}>Reporte: Orden de Mérito</Text>
            <Text style={estilos.subtitulo}>{datosReporte?.nombreEmpresa} - {datosReporte?.nombrePuesto}</Text>

            <Text style={estilos.emitidoPor}>
                Emitido por: {datosReporte?.emitidoPor} | Fecha: {datosReporte?.fechaEmision ? new Date(datosReporte.fechaEmision).toLocaleString('es-AR') : '-'}
            </Text>

            {/* TABLA: APROBADOS */}
            <Text style={estilos.tituloSeccionVerde}>Candidatos en Orden de Mérito</Text>
            <View style={estilos.tabla}>
                <View style={[estilos.filaTabla, estilos.filaEncabezado]}>
                    <Text style={[estilos.celda, estilos.colCandidatoA]}>Candidato</Text>
                    <Text style={[estilos.celda, estilos.colDocA]}>Documento</Text>
                    <Text style={[estilos.celda, estilos.colNroA]}>Nº Candidato</Text>
                    <Text style={[estilos.celda, estilos.colPuntajeA, { color: '#374151' }]}>Puntaje</Text>
                    <Text style={[estilos.celda, estilos.colAccesosA]}>Accesos</Text>
                </View>
                {!datosReporte?.candidatosAprobados || datosReporte.candidatosAprobados.length === 0 ? (
                    <View style={estilos.filaTabla}>
                        <Text style={[estilos.celda, { width: '100%', textAlign: 'center', color: '#9ca3af' }]}>No hay candidatos aprobados.</Text>
                    </View>
                ) : (
                    datosReporte.candidatosAprobados.map((c, i) => (
                        <View style={estilos.filaTabla} key={i}>
                            <Text style={[estilos.celda, estilos.colCandidatoA]}>{c.apellido}, {c.nombre}</Text>
                            <Text style={[estilos.celda, estilos.colDocA]}>{c.tipoDocumento} {c.numeroDocumento}</Text>
                            <Text style={[estilos.celda, estilos.colNroA]}>{c.numeroCandidato}</Text>
                            <Text style={[estilos.celda, estilos.colPuntajeA]}>{c.puntaje != null ? c.puntaje.toFixed(2) : '-'}</Text>
                            <Text style={[estilos.celda, estilos.colAccesosA]}>{c.cantidadAccesos}</Text>
                        </View>
                    ))
                )}
            </View>

            {/* TABLA: RECHAZADOS / INCOMPLETOS */}
            <Text style={estilos.tituloSeccionRoja}>Candidatos Fuera de Orden o Incompletos</Text>
            <View style={estilos.tabla}>
                <View style={[estilos.filaTabla, estilos.filaEncabezado]}>
                    <Text style={[estilos.celda, estilos.colCandidatoR]}>Candidato</Text>
                    <Text style={[estilos.celda, estilos.colDocR]}>Documento</Text>
                    <Text style={[estilos.celda, estilos.colEstadoR]}>Estado</Text>
                    <Text style={[estilos.celda, estilos.colPuntajeR]}>Puntaje</Text>
                </View>
                {!datosReporte?.candidatosRechazadosOIncompletos || datosReporte.candidatosRechazadosOIncompletos.length === 0 ? (
                    <View style={estilos.filaTabla}>
                        <Text style={[estilos.celda, { width: '100%', textAlign: 'center', color: '#9ca3af' }]}>No hay candidatos en esta sección.</Text>
                    </View>
                ) : (
                    datosReporte.candidatosRechazadosOIncompletos.map((c, i) => (
                        <View style={estilos.filaTabla} key={i}>
                            <Text style={[estilos.celda, estilos.colCandidatoR]}>{c.apellido}, {c.nombre}</Text>
                            <Text style={[estilos.celda, estilos.colDocR]}>{c.tipoDocumento} {c.numeroDocumento}</Text>
                            <Text style={[estilos.celda, estilos.colEstadoR]}>{traduccionesEstado[c.estado] || c.estado}</Text>
                            <Text style={[estilos.celda, estilos.colPuntajeR]}>{c.puntaje != null ? c.puntaje.toFixed(2) : '-'}</Text>
                        </View>
                    ))
                )}
            </View>
        </Page>
    </Document>
);

export const MeritOrderPDF = PdfOrdenMerito;
export default PdfOrdenMerito;

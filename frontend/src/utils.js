import * as XLSX from 'xlsx';

/**
 * Genera y descarga un archivo Excel con las claves de acceso de los candidatos.
 */
export function exportarClavesExcel(clavesGeneradas, nombrePuesto = '') {
    if (!clavesGeneradas || clavesGeneradas.length === 0) return;

    const datosExcel = clavesGeneradas.map((k) => ({
        'Nro Candidato': k.numeroCandidato,
        'Nombre': k.nombre,
        'Apellido': k.apellido,
        'Clave de Acceso': k.claveAcceso
    }));

    const hoja = XLSX.utils.json_to_sheet(datosExcel);
    const libro = XLSX.utils.book_new();
    XLSX.utils.book_append_sheet(libro, hoja, 'Claves de Acceso');

    hoja['!cols'] = [
        { wch: 15 },
        { wch: 25 },
        { wch: 25 },
        { wch: 20 }
    ];

    const nombreSeguro = (nombrePuesto || '').replace(/[^a-z0-9]/gi, '_');
    const nombreArchivo = `Claves_${nombreSeguro || 'Evaluacion'}.xlsx`;

    XLSX.writeFile(libro, nombreArchivo);
}

/**
 * Formatea una fecha ISO a formato local legible en español (es-AR).
 */
export function formatearFechaHora(fecha) {
    if (!fecha) return '-';
    try {
        return new Date(fecha).toLocaleString('es-AR');
    } catch {
        return '-';
    }
}

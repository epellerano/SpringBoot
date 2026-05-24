package com.sigat.springboot.app.models.service;

import java.awt.Color;
import java.io.File;
import java.io.FileOutputStream;
import java.text.SimpleDateFormat;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.time.ZoneId;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Lazy;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import com.sigat.springboot.app.models.dao.IPlanillaDetalleDao;
import com.sigat.springboot.app.models.dao.ISobreTurnosDao;
import com.sigat.springboot.app.models.dao.ITurnosDao;
import com.sigat.springboot.app.models.entity.Especialidad;
import com.sigat.springboot.app.models.entity.Estado;
import com.sigat.springboot.app.models.entity.PlanillaCabecera;
import com.sigat.springboot.app.models.entity.PlanillaDetalle;
import com.sigat.springboot.app.models.entity.Profesional;
import com.sigat.springboot.app.models.entity.Sobreturno;
import com.sigat.springboot.app.models.entity.Turno;
import com.sigat.springboot.app.models.entity.Vinculacion;
import com.lowagie.text.Chunk;
import com.lowagie.text.Document;
import com.lowagie.text.Element;
import com.lowagie.text.Font;
import com.lowagie.text.FontFactory;
import com.lowagie.text.Image;
import com.lowagie.text.PageSize;
import com.lowagie.text.Paragraph;
import com.lowagie.text.Phrase;
import com.lowagie.text.pdf.BaseFont;
import com.lowagie.text.pdf.PdfContentByte;
import com.lowagie.text.pdf.PdfPCell;
import com.lowagie.text.pdf.PdfPTable;
import com.lowagie.text.pdf.PdfWriter;

@Service
public class PlanillaDetalleServiceImpl implements IPlanillaDetalleService {

	@Value("${spring.datasource.username}")
	private String user;

	@Value("${spring.datasource.password}")
	private String password;

	// Asegúrate de que en properties sea spring.database.name o database.name
	@Value("${spring.database.name}")
	private String dataBaseName;

	@Autowired
	private IPlanillaDetalleDao planilladetalleDao;

	@Autowired
	private ITurnosDao turnoDao;

	@Autowired
	private ISobreTurnosDao sobreturnoDao;

	@Autowired
	private IProfesionalService profesionalService;

	@Autowired
	private IEspecialidadService especialidadService;

	@Autowired
	@Lazy // <-- Agregá esto
	private ITurnoService turnoService;

	@Autowired
	@Lazy // <-- Agregá esto
	private ISobreTurnoService sobreturnoService;

	@Autowired
	private BackupService backupService; // Inyectamos tu clase especializada

	// para mostrar numero de box en sobreturnos (UNIFICADO A UN SOLO PARÁMETRO)
	@Override
	@Transactional(readOnly = true)
	public Integer findBoxByCabeceraId(Long cabeceraId) {
	    return planilladetalleDao.findBoxByCabeceraId(cabeceraId);
	}


	@Override
	@Transactional(readOnly = true)
	public long ExistePlanillaDetalleActiva(long term1, long term2, long term3, long term4) {
		return planilladetalleDao.ExistePlanillaDetalleActiva(term1, term2, term3, term4);
	}

	@Override
	@Transactional(readOnly = true)
	public String[] ListarRangoPlanillaDet(long profId, long EspecId) {
		return planilladetalleDao.ListarRangoPlanillaDet(profId, EspecId);
	}

	@Override
	public void save(PlanillaDetalle planilladetalle) {
		planilladetalleDao.save(planilladetalle);

	}

	@Override
	public void saveAll(List<PlanillaDetalle> detalleList) {
		planilladetalleDao.saveAll(detalleList);

	}

	@Override
	@Transactional(readOnly = true)
	public List<String> generarRangoHorario(String fechaHoraInicial, String fechaHoraFinal, long intervalo, long dia) {
		return planilladetalleDao.generarRangoHorario(fechaHoraInicial, fechaHoraFinal, intervalo, dia);
	}

	@Override
	@Transactional(readOnly = true)
	public List<Object[]> generarRangoHorarioPersonalizado(String fechaHoraInicial, String fechaHoraFinal, long dia,
			String box) {
		return planilladetalleDao.generarRangoHorarioPersonalizado(fechaHoraInicial, fechaHoraFinal, dia, box);
	}

	@Override
	@Transactional(readOnly = true)
	public List<String> generarRangoHorarioCompleto(String fechaHoraInicial, String fechaHoraFinal, long intervalo,
			long dia, String fechaHoraInicialR2, String fechaHoraFinalR2, long intervaloR2) {
		return planilladetalleDao.generarRangoHorarioCompleto(fechaHoraInicial, fechaHoraFinal, intervalo, dia,
				fechaHoraInicialR2, fechaHoraFinalR2, intervaloR2);
	}

	// Metodo para mostrar los detalles de acuerdo a la planillacabeceraId.
	@Override
	@Transactional(readOnly = true)
	public List<PlanillaDetalle> verPlanillaDetalle(long idPlanillacab) {
		return planilladetalleDao.verPlanillaDetalle(idPlanillacab);
	}

	// Metodo para mostrar los datos del Profesional y Especialidad acuerdo a la
	// planillacabeceraId.
	@Override
	@Transactional(readOnly = true)
	public List<PlanillaDetalle> verPlanillaDetalleProfyEspecialidadDistinct(long idPlanillacab) {
		return planilladetalleDao.verPlanillaDetalleProfyEspecialidadDistinct(idPlanillacab);
	}

	// muestra los turnos libres (de todos los Dias)de acuerdo a la planilla detalle
	// por ProfId y EspecialidadID.
	@Override
	@Transactional(readOnly = true)
	public List<PlanillaDetalle> mostrarTurnosLibresTodos(Long profId, Long EspecId) {
		return planilladetalleDao.mostrarTurnosLibresTodos(profId, EspecId);
	}

	// muestra los turnos libres (de todos los Dias)de acuerdo a la planilla detalle
	// por ProfId, EspecialidadID y diaId.
	@Override
	@Transactional(readOnly = true)
	public List<PlanillaDetalle> mostrarTurnosLibresTodosByDiaId(Long profId, Long EspecId, Long diaId) {
		return planilladetalleDao.mostrarTurnosLibresTodosByDiaId(profId, EspecId, diaId);
	}

	// PARA TRABAJAR EN SOBRETURNOS Y LISTAR LOS TURNOS LIBRES
	@Override
	@Transactional(readOnly = true)
	public List<PlanillaDetalle> mostrarHorariosParaSobreturnosTodos(Long profId, Long EspecId) {
		return planilladetalleDao.mostrarHorariosParaSobreturnosTodos(profId, EspecId);
	}

	// PARA TRABAJAR EN SOBRETURNOS Y LISTAR LOS TURNOS LIBRES POR DIA
	@Override
	@Transactional(readOnly = true)
	public List<PlanillaDetalle> mostrarHorariosParaSobreturnosByDiaId(Long profId, Long EspecId, Long diaId) {
		return planilladetalleDao.mostrarHorariosParaSobreturnosByDiaId(profId, EspecId, diaId);
	}

	// ACTUALIZAMOS en Planilladetalle el estado_id=4 (OCUPADO).
	@Override
	@Transactional
	public void updatePlanillaDetalleTurnoOcupado(String planillaDetId) {
		planilladetalleDao.updatePlanillaDetalleTurnoOcupado(planillaDetId);
		;
	}

	// ACTUALIZAMOS en Planilladetalle el estado_id=3 (LIBRE).
	@Override
	@Transactional
	public void updatePlanillaDetalleTurnoLibre(String planillaDetId) {
		planilladetalleDao.updatePlanillaDetalleTurnoLibre(planillaDetId);
	}

	// ACTUALIZAMOS en Planilladetalle el estado_id=8 (EXPIRADO).
	@Override
	@Transactional
	public void updatePlanillaDetalleTurnoExpirado(String estadoId) {
		planilladetalleDao.updatePlanillaDetalleTurnoExpirado(estadoId);
	}

	// ACTUALIZAMOS en PlanillaCabecera el id_estado=1 (INACTIVO).
	@Override
	@Transactional
	public void updatePlanillaCabeceraInactivo(String estadoId) {
		planilladetalleDao.updatePlanillaCabeceraInactivo(estadoId);
	}

	// VERIFICAR SI EXISTE FECHA_HORA DEL SOBRETURNO EN PLANILLA DETALLE.
	@Override
	@Transactional(readOnly = true)
	public List<String> existeSobreturnoEnPlanillaDetalle(Long profId, Long EspecId, String fechaHora) {
		return planilladetalleDao.existeSobreturnoEnPlanillaDetalle(profId, EspecId, fechaHora);
	}

	// buscar turnos libres fechas (vista: Cancelar Planillas)
	@Override
	@Transactional(readOnly = true)
	public List<PlanillaDetalle> buscarTurnosLibresFechas(Long profId, Long EspecId, Date fechaIni, Date fechaFin) {
		return planilladetalleDao.buscarTurnosLibresFechas(profId, EspecId, fechaIni, fechaFin);
	}

	// buscar turnos libres fechas y horas(vista: Cancelar Planillas)
	@Override
	@Transactional(readOnly = true)
	public List<PlanillaDetalle> buscarTurnosLibresFechasHoras(Long profId, Long EspecId, Date fechaHoraIni,
			Date fechaHoraFin) {
		return planilladetalleDao.buscarTurnosLibresFechasHoras(profId, EspecId, fechaHoraIni, fechaHoraFin);
	}

	// ACTUALIZAMOS en Planilladetalle el estado_id=6 (CANCELADO). (vista: Cancelar
	// Planillas) Turnos Libres
	@Override
	@Transactional
	public void actualizarPlanillaDetalleCanceladoFechasTL(Long profId, Long EspecId, Date fechaIni, Date fechaFin,
			String estadoId, String motivo) {
		planilladetalleDao.actualizarPlanillaDetalleCanceladoFechasTL(profId, EspecId, fechaIni, fechaFin, estadoId,
				motivo);
	}

	// ACTUALIZAMOS en Planilladetalle el estado_id=6 (CANCELADO). (vista: Cancelar
	// Planillas) Turnos Libres
	@Override
	@Transactional
	public void actualizarPlanillaDetalleCanceladoFechasHorasTL(Long profId, Long EspecId, Date fechaHoraIni,
			Date fechaHoraFin, String estadoId, String motivo) {
		planilladetalleDao.actualizarPlanillaDetalleCanceladoFechasHorasTL(profId, EspecId, fechaHoraIni, fechaHoraFin,
				estadoId, motivo);
	}

	// ACTUALIZAMOS en Planilladetalle el estado_id=6 (CANCELADO). (vista: Cancelar
	// Planillas) Turnos Ocupados
	@Override
	@Transactional
	public void actualizarPlanillaDetalleCanceladoFechasTO(Long profId, Long EspecId, Date fechaIni, Date fechaFin,
			String estadoId, String motivo) {
		planilladetalleDao.actualizarPlanillaDetalleCanceladoFechasTO(profId, EspecId, fechaIni, fechaFin, estadoId,
				motivo);
	}

	// ACTUALIZAMOS en Planilladetalle el estado_id=6 (CANCELADO). (vista: Cancelar
	// Planillas) Turnos Ocupados
	@Override
	@Transactional
	public void actualizarPlanillaDetalleCanceladoFechasHorasTO(Long profId, Long EspecId, Date fechaHoraIni,
			Date fechaHoraFin, String estadoId, String motivo) {
		planilladetalleDao.actualizarPlanillaDetalleCanceladoFechasHorasTO(profId, EspecId, fechaHoraIni, fechaHoraFin,
				estadoId, motivo);
	}

	@Override
	@Transactional(readOnly = true)
	public PlanillaDetalle findOne(Long id) {
		// Buscamos la planilla por ID. Si no existe, devolvemos null.
		// Esto recupera el objeto con el campo 'rangoFechaHora' lleno.
		return planilladetalleDao.findById(id).orElse(null);
	}

	@Override
	@Transactional
	public void updatePlanillaDetalleMasivoLibre(List<Long> ids) {
		// Llamamos al DAO de PlanillaDetalle (asegurate de tener el método en el DAO)
		planilladetalleDao.updatePlanillaDetalleMasivoLibre(ids);
	}

	// NUEVA FORMA DE CANCELAR PLANILLAS MASIVAMENTE
	@Override
	@Transactional(rollbackFor = Exception.class)
	public String ejecutarBajaMedicaMasivaCompleta(Long profId, Long especId, String inicio, String fin, String motivo,
			boolean ignorarBackup) throws Exception {

		// 1. CONVERSIÓN A DATE (Para lógica de Planilla y Cabecera que ya funcionaba)
		SimpleDateFormat format = inicio.contains(":") ? new SimpleDateFormat("yyyy-MM-dd HH:mm")
				: new SimpleDateFormat("yyyy-MM-dd");

		Date fechaInicio = format.parse(inicio);
		Date fechaFin = format.parse(fin);

		// 1B. CONVERSIÓN A LOCALDATETIME (Para procesos de Turnos/Sobreturnos)
		java.time.LocalDateTime inicioLDT = fechaInicio.toInstant().atZone(java.time.ZoneId.systemDefault())
				.toLocalDateTime();

		java.time.LocalDateTime finLDT = fechaFin.toInstant().atZone(java.time.ZoneId.systemDefault())
				.toLocalDateTime();

		// Ajuste de día completo si no hay hora
		if (!inicio.contains(":")) {
			finLDT = finLDT.withHour(23).withMinute(59).withSecond(59);

			Calendar cal = Calendar.getInstance();
			cal.setTime(fechaFin);
			cal.set(Calendar.HOUR_OF_DAY, 23);
			cal.set(Calendar.MINUTE, 59);
			cal.set(Calendar.SECOND, 59);
			fechaFin = cal.getTime();
		}

		// 2. BACKUP
		if (!ignorarBackup) {
			// CAMBIO: Agregá "Masivo" o "Turnos" entre los paréntesis
			boolean backupOk = backupService.ejecutarBackup("Masivo");
			if (!backupOk)
				throw new Exception("Fallo el backup");
		}

		// ------------------------------------------------------------------------------------
		// 3. PDF ELITE - BÚSQUEDA DE PACIENTES
		Profesional prof = profesionalService.findOne(profId);
		Especialidad espec = especialidadService.findOne(especId);

		List<Turno> turnos;
		List<Sobreturno> sobret;

		// USAMOS FUNCIONES SEPARADAS SEGÚN EL TIPO DE CANCELACIÓN
		if (inicio.contains(":")) {
			// CASO FECHA Y HORA: Usamos LocalDateTime directamente (Precisión minutos)
			turnos = turnoService.buscarTurnosOcupadosParaBajaFechaHora(profId, especId, inicioLDT, finLDT);
			sobret = sobreturnoService.buscarSobreturnosParaBajaFechaHora(profId, especId, inicioLDT, finLDT);
			System.out.println(">>> PDF: Buscando por Fecha y Hora exacta");
		} else {
			// CASO FECHA SOLA: Convertimos a String YYYY-MM-DD para el DATE_FORMAT del DAO
			String dIni = inicioLDT.toLocalDate().toString();
			String dFin = finLDT.toLocalDate().toString();
			turnos = turnoService.buscarTurnosOcupadosParaBajaSoloFecha(profId, especId, dIni, dFin);
			sobret = sobreturnoService.buscarSobreturnosParaBajaSoloFecha(profId, especId, dIni, dFin);
			System.out.println(">>> PDF: Buscando por Día completo (DATE_FORMAT)");
		}

		System.out.println(">>> PDF DEBUG: Turnos encontrados: " + (turnos != null ? turnos.size() : 0));
		System.out.println(">>> PDF DEBUG: Sobreturnos encontrados: " + (sobret != null ? sobret.size() : 0));

		if ((turnos != null && !turnos.isEmpty()) || (sobret != null && !sobret.isEmpty())) {
			// CAMBIO: Agregá el parámetro "Masivo" al final
			this.generarPdfLlamadosElite(turnos, sobret, prof, espec, inicio, fin, "Planillas");
			System.out.println(">>> PDF Generado con éxito.");
		}
		// ------------------------------------------------------------------------------------

		// 4. LAS CANCELACIONES (UPDATE masivo en la DB)
		if (inicio.contains(":")) {
			turnoService.eliminarTurnosOcupadosFechasHoras(profId, especId, inicioLDT, finLDT);
			sobreturnoService.eliminarSobreturnosOcupadosFechasHoras(profId, especId, inicioLDT, finLDT);

			this.actualizarPlanillaDetalleCanceladoFechasHorasTL(profId, especId, fechaInicio, fechaFin, "6", motivo);
			this.actualizarPlanillaDetalleCanceladoFechasHorasTO(profId, especId, fechaInicio, fechaFin, "6", motivo);
		} else {
			turnoService.eliminarTurnosOcupadosFechas(profId, especId, inicioLDT, finLDT);
			sobreturnoService.eliminarSobreturnosOcupadosFechas(profId, especId, inicioLDT, finLDT);

			this.actualizarPlanillaDetalleCanceladoFechasTL(profId, especId, fechaInicio, fechaFin, "6", motivo);
			this.actualizarPlanillaDetalleCanceladoFechasTO(profId, especId, fechaInicio, fechaFin, "6", motivo);
		}

		// 5. CABECERA
		planilladetalleDao.actualizarEstadoCabeceraMasivo(profId, especId, fechaInicio, fechaFin);

		return "¡Proceso Finalizado con Éxito!";
	}

	// Método publico para generar el PDF (Debe estar dentro de
	// PlanillaDetalleServiceImpl)
	@Override
	public void generarPdfLlamadosElite(List<Turno> turnos, List<Sobreturno> sobreturnos, Profesional prof,
			Especialidad espec, String fIni, String fFin, String tipo) throws Exception {

		// 1. CONSTRUCCIÓN DINÁMICA DE LA RUTA SEGÚN EL TIPO (Sobreturnos, Turnos,
		// Planillas)
		String rutaCarpeta = "C:\\Users\\Public\\Documents\\BackupPdfCancelacion" + tipo + "\\";
		new java.io.File(rutaCarpeta).mkdirs();

		// Agregamos el tipo al nombre del archivo para que sea fácil identificarlo
		String nombreArchivo = "Reporte_Baja_" + tipo + "_"
				+ new java.text.SimpleDateFormat("ddMMyyyy_HHmm").format(new java.util.Date()) + ".pdf";
		String rutaFinal = rutaCarpeta + nombreArchivo;

		// Orientación Horizontal (Landscape)
		com.lowagie.text.Document document = new com.lowagie.text.Document(com.lowagie.text.PageSize.A4.rotate());

		try {
			com.lowagie.text.pdf.PdfWriter writer = com.lowagie.text.pdf.PdfWriter.getInstance(document,
					new java.io.FileOutputStream(rutaFinal));

			// --- CONFIGURACIÓN DEL PIE DE PÁGINA ---
			writer.setPageEvent(new com.lowagie.text.pdf.PdfPageEventHelper() {
				public void onEndPage(com.lowagie.text.pdf.PdfWriter writer, com.lowagie.text.Document document) {
					com.lowagie.text.pdf.PdfContentByte cb = writer.getDirectContent();
					com.lowagie.text.Phrase footer = new com.lowagie.text.Phrase("Página: " + writer.getPageNumber(),
							new com.lowagie.text.Font(com.lowagie.text.Font.HELVETICA, 8, com.lowagie.text.Font.NORMAL,
									java.awt.Color.GRAY));
					com.lowagie.text.pdf.ColumnText.showTextAligned(cb, com.lowagie.text.Element.ALIGN_CENTER, footer,
							(document.right() - document.left()) / 2 + document.leftMargin(), document.bottom() - 10,
							0);
				}
			});
			document.open();

			// 1. LOGO
			try {
				com.lowagie.text.Image img = com.lowagie.text.Image
						.getInstance(getClass().getClassLoader().getResource("static/images/LogoSigat.png"));
				img.scaleAbsolute(55, 55);
				img.setAlignment(com.lowagie.text.Element.ALIGN_LEFT);
				document.add(img);
			} catch (Exception e) {
				System.out.println("Logo no encontrado");
			}

			// 2. ENCABEZADO
			com.lowagie.text.pdf.PdfPTable tablaEncabezado = new com.lowagie.text.pdf.PdfPTable(1);
			tablaEncabezado.setWidthPercentage(100);

			com.lowagie.text.pdf.PdfPCell celdaT = new com.lowagie.text.pdf.PdfPCell(new com.lowagie.text.Phrase(
					"SIGAT - LISTADO DE PACIENTES PARA AVISAR CANCELACIÓN MÉDICA (" + tipo.toUpperCase() + ")",
					new com.lowagie.text.Font(com.lowagie.text.Font.HELVETICA, 13, com.lowagie.text.Font.BOLD,
							java.awt.Color.WHITE)));
			celdaT.setBackgroundColor(java.awt.Color.DARK_GRAY);
			celdaT.setHorizontalAlignment(com.lowagie.text.Element.ALIGN_CENTER);
			celdaT.setVerticalAlignment(com.lowagie.text.Element.ALIGN_MIDDLE);
			celdaT.setFixedHeight(25);
			celdaT.setBorder(0);
			tablaEncabezado.addCell(celdaT);

			String fechaHoy = new java.text.SimpleDateFormat("dd/MM/yyyy HH:mm:ss").format(new java.util.Date());
			com.lowagie.text.pdf.PdfPCell celdaS = new com.lowagie.text.pdf.PdfPCell(new com.lowagie.text.Phrase(
					"Reporte generado el: " + fechaHoy,
					new com.lowagie.text.Font(com.lowagie.text.Font.HELVETICA, 9, com.lowagie.text.Font.ITALIC)));
			celdaS.setHorizontalAlignment(com.lowagie.text.Element.ALIGN_RIGHT);
			celdaS.setBorder(0);
			celdaS.setPaddingTop(4);
			tablaEncabezado.addCell(celdaS);

			document.add(tablaEncabezado);

			// 3. DATOS PROFESIONAL
			com.lowagie.text.Paragraph pInfo = new com.lowagie.text.Paragraph();
			pInfo.setSpacingBefore(10);
			pInfo.add(new com.lowagie.text.Chunk("PROFESIONAL: ",
					new com.lowagie.text.Font(com.lowagie.text.Font.HELVETICA, 10, com.lowagie.text.Font.BOLD)));
			pInfo.add(new com.lowagie.text.Chunk(
					prof.getApellido().toUpperCase() + " " + prof.getNombre().toUpperCase() + "    ",
					new com.lowagie.text.Font(com.lowagie.text.Font.HELVETICA, 10, com.lowagie.text.Font.NORMAL)));
			pInfo.add(new com.lowagie.text.Chunk("ESPECIALIDAD: ",
					new com.lowagie.text.Font(com.lowagie.text.Font.HELVETICA, 10, com.lowagie.text.Font.BOLD)));
			pInfo.add(new com.lowagie.text.Chunk(espec.getEspecialidadNombre().toUpperCase(),
					new com.lowagie.text.Font(com.lowagie.text.Font.HELVETICA, 10, com.lowagie.text.Font.NORMAL)));
			pInfo.setAlignment(com.lowagie.text.Element.ALIGN_LEFT);
			document.add(pInfo);
			document.add(new com.lowagie.text.Paragraph(" "));

			// 4. TABLA DE PACIENTES
			com.lowagie.text.pdf.PdfPTable tabla = new com.lowagie.text.pdf.PdfPTable(8);
			tabla.setWidthPercentage(100);
			tabla.setWidths(new float[] { 2f, 2f, 4f, 4f, 4.5f, 2.5f, 3f, 4f });

			String[] headers = { "ALTA", "TIPO", "PROFESIONAL", "ESPECIALIDAD", "PACIENTE", "DNI", "TELÉFONO",
					"FECHA TURNO" };
			for (String h : headers) {
				com.lowagie.text.pdf.PdfPCell cell = new com.lowagie.text.pdf.PdfPCell(
						new com.lowagie.text.Phrase(h, new com.lowagie.text.Font(com.lowagie.text.Font.HELVETICA, 9,
								com.lowagie.text.Font.BOLD, java.awt.Color.WHITE)));
				cell.setBackgroundColor(java.awt.Color.GRAY);
				cell.setHorizontalAlignment(com.lowagie.text.Element.ALIGN_CENTER);
				cell.setFixedHeight(18);
				tabla.addCell(cell);
			}

			java.text.SimpleDateFormat sdfAlta = new java.text.SimpleDateFormat("dd/MM/yyyy");
			java.text.SimpleDateFormat sdfTurno = new java.text.SimpleDateFormat("dd/MM/yyyy HH:mm");
			com.lowagie.text.Font fCelda = new com.lowagie.text.Font(com.lowagie.text.Font.HELVETICA, 8);
			com.lowagie.text.Font fFechaRed = new com.lowagie.text.Font(com.lowagie.text.Font.HELVETICA, 8,
					com.lowagie.text.Font.BOLD, java.awt.Color.RED);

			// Turnos
			for (Turno t : turnos) {
				tabla.addCell(new com.lowagie.text.Phrase(
						sdfAlta.format(java.sql.Timestamp.valueOf(t.getFechaCreacion())), fCelda));
				tabla.addCell(new com.lowagie.text.Phrase("TURNO", fCelda));
				tabla.addCell(new com.lowagie.text.Phrase(prof.getApellido().toUpperCase(), fCelda));
				tabla.addCell(new com.lowagie.text.Phrase(espec.getEspecialidadCodigo(), fCelda));
				tabla.addCell(new com.lowagie.text.Phrase(
						t.getPaciente().getApellido() + " " + t.getPaciente().getNombre(), fCelda));
				tabla.addCell(new com.lowagie.text.Phrase(t.getPaciente().getDni(), fCelda));
				String tel = (t.getPaciente().getTelefono() != null) ? t.getPaciente().getTelefono() : "---";
				tabla.addCell(new com.lowagie.text.Phrase(tel, fCelda));
				tabla.addCell(new com.lowagie.text.Phrase(sdfTurno.format(t.getPlanilladetalle().getRangoFechaHora()),
						fFechaRed));
			}

			// Sobreturnos
			for (Sobreturno s : sobreturnos) {
				tabla.addCell(new com.lowagie.text.Phrase(
						sdfAlta.format(java.sql.Timestamp.valueOf(s.getFechaCreacion())), fCelda));
				tabla.addCell(new com.lowagie.text.Phrase("SOBRE", fCelda));
				tabla.addCell(new com.lowagie.text.Phrase(prof.getApellido().toUpperCase(), fCelda));
				tabla.addCell(new com.lowagie.text.Phrase(espec.getEspecialidadCodigo(), fCelda));
				tabla.addCell(new com.lowagie.text.Phrase(
						s.getPaciente().getApellido() + " " + s.getPaciente().getNombre(), fCelda));
				tabla.addCell(new com.lowagie.text.Phrase(s.getPaciente().getDni(), fCelda));
				String tel = (s.getPaciente().getTelefono() != null) ? String.valueOf(s.getPaciente().getTelefono())
						: "---";
				tabla.addCell(new com.lowagie.text.Phrase(tel, fCelda));
				java.util.Date fechaTurno = java.sql.Timestamp.valueOf(s.getRangoFechaHora());
				tabla.addCell(new com.lowagie.text.Phrase(sdfTurno.format(fechaTurno), fFechaRed));
			}
			document.add(tabla);
		} finally {
			if (document != null) {
				document.close();
			}
		}
	}

	// RESTAURACION (AUDITORIA)
	@Override
	@Transactional(readOnly = true)
	public List<PlanillaCabecera> findPlanillasCanceladas(Long profId, Long especId, String desde, String hasta) {
		java.util.Date fechaD = null;
		java.util.Date fechaH = null;
		java.text.SimpleDateFormat sdf = new java.text.SimpleDateFormat("yyyy-MM-dd");

		try {
			if (desde != null && !desde.isEmpty()) {
				fechaD = sdf.parse(desde);
			}
			if (hasta != null && !hasta.isEmpty()) {
				fechaH = sdf.parse(hasta);
				long unDiaEnMilis = (24 * 60 * 60 * 1000) - 1000;
				fechaH = new java.util.Date(fechaH.getTime() + unDiaEnMilis);
			}
		} catch (Exception e) {
			System.err.println("Error parseando fechas en PlanillaDetalle: " + e.getMessage());
		}

		return planilladetalleDao.findPlanillasCanceladasGroupByCabecera(profId, especId, fechaD, fechaH);
	}

	@Override
	@Transactional
	public String restaurarPlanillaMasiva(Long profId, Long especId, String desde, String hasta) {
		try {
			// 1. BACKUP PREVENTIVO
			boolean backupOk = backupService.ejecutarBackup("RestauracionPlanillas");
			if (!backupOk)
				return "Error: No se pudo realizar el backup preventivo.";

			// 2. CONVERSIÓN DE FECHAS
			java.text.SimpleDateFormat sdf = new java.text.SimpleDateFormat("yyyy-MM-dd");
			java.util.Date fechaD = (desde != null && !desde.isEmpty()) ? sdf.parse(desde) : null;
			java.util.Date fechaH = (hasta != null && !hasta.isEmpty()) ? sdf.parse(hasta) : null;
			if (fechaH != null) {
				fechaH = new java.util.Date(fechaH.getTime() + (24 * 60 * 60 * 1000) - 1000);
			}

			// 3. BUSCAR HUECOS DE LA PLANILLA
			List<PlanillaDetalle> detalles = planilladetalleDao.findAllByFiltroRestauracion(profId, especId, fechaD,
					fechaH);

			Estado activo = new Estado();
			activo.setId(2L);
			Estado libre = new Estado();
			libre.setId(3L);
			Estado ocupado = new Estado();
			ocupado.setId(4L);

			java.util.Set<Long> cabecerasProcesadas = new java.util.HashSet<>();

			// 4. PROCESAR HUECOS
			for (PlanillaDetalle pd : detalles) {
				if (pd.getPlanillacabecera() != null
						&& !cabecerasProcesadas.contains(pd.getPlanillacabecera().getId())) {
					pd.getPlanillacabecera().setEstado(activo);
					cabecerasProcesadas.add(pd.getPlanillacabecera().getId());
				}

				// LIMPIEZA DE OBSERVACIÓN EN PLANILLA
				pd.setObservacion("Planilla Generada");

				Turno t = turnoDao.findByPlanilladetalleId(pd.getId());
				if (t != null) {
					t.setEstado(ocupado);
					pd.setEstado(ocupado);
					turnoDao.save(t);
				} else {
					if (pd.getEstado().getId() == 6L) {
						pd.setEstado(libre);
					}
				}
				planilladetalleDao.save(pd);
			}

			// 5. RESTAURAR Y LIMPIAR SOBRETURNOS (SÍ se agregó la limpieza aquí)
			List<Sobreturno> sobreturnos = sobreturnoDao.findCanceladosConFiltro(profId, especId, fechaD, fechaH);
			for (Sobreturno s : sobreturnos) {
				s.setEstado(ocupado);

				// LIMPIEZA DE OBSERVACIÓN EN SOBRETURNO
				// (Asumiendo que tu entidad Sobreturno tiene el campo observacion)
				s.setObservacion("Planilla Generada");

				sobreturnoDao.save(s);
			}

			return "OK"; // Mensaje limpio, sin contadores (NO al contador)

		} catch (Exception e) {
			return "Error crítico: " + e.getMessage();
		}
	}

	// Mostrar detalle de lo que esta cancelado y se va a restaurar
	@Override
	@Transactional(readOnly = true)
	public List<PlanillaDetalle> findByPlanillaCabeceraIdAndEstadoId(Long cabeceraId, Long estadoId) {
		return planilladetalleDao.findByPlanillaCabeceraIdAndEstadoId(cabeceraId, estadoId);
	}

}

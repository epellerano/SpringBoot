package com.sigat.springboot.app.controllers;

import java.time.LocalDateTime;
import java.util.Date;
import java.util.List;
import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Sort;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.servlet.ModelAndView;

import com.sigat.springboot.app.models.entity.Especialidad;
import com.sigat.springboot.app.models.entity.Movimiento;
import com.sigat.springboot.app.models.entity.Paciente;
import com.sigat.springboot.app.models.entity.PlanillaCabecera;
import com.sigat.springboot.app.models.entity.Profesional;
import com.sigat.springboot.app.models.entity.Sobreturno;
import com.sigat.springboot.app.models.entity.Turno;
import com.sigat.springboot.app.models.entity.Vinculacion;
import com.sigat.springboot.app.models.service.IEspecialidadService;
import com.sigat.springboot.app.models.service.IMovimientoService;
import com.sigat.springboot.app.models.service.IPacienteService;
import com.sigat.springboot.app.models.service.IPlanillaCabeceraService;
import com.sigat.springboot.app.models.service.IProfesionalService;
import com.sigat.springboot.app.models.service.ISobreTurnoService;
import com.sigat.springboot.app.models.service.ITurnoService;
import com.sigat.springboot.app.models.service.IVinculacionService;

@Controller
@RequestMapping("/reportes")
public class ReporteController {

	@Autowired
	private IProfesionalService profesionalService;
	@Autowired
	private IEspecialidadService especialidadService;
	@Autowired
	private IPacienteService pacienteService;
	@Autowired
	private ITurnoService turnoService;
	@Autowired
	private ISobreTurnoService sobreturnoService;
	@Autowired
	private IPlanillaCabeceraService planillacabeceraService;
	@Autowired
	private IMovimientoService movimientoService;
	@Autowired
	private IVinculacionService vinculacionService;

	// Metodo listar sin paginacion

	@GetMapping("/formReporte")
	public String listar(Model model) {
		model.addAttribute("titulo", "Listados Personalizados");
		model.addAttribute("profesionales", profesionalService.findAll());
		// model.addAttribute("pacientes",pacienteService.findAll());
		return "reportes/formReporte";
	}

	@GetMapping("/profesionales-pdf")
	public ModelAndView generatePdfProfesional(Model model) {
		List<Profesional> profesionales = profesionalService.findAll();
		// llenamos el modelo
		model.addAttribute("profesionales", profesionales);
		// Se devuelve el nombre del bean de la vista
		return new ModelAndView("profesionalPdfView");
	}

	@GetMapping("/pacientes-pdf")
	public ModelAndView generatePdfPacientes(Model model) {
		List<Paciente> pacientes = pacienteService.findAll();
		// llenamos el modelo
		model.addAttribute("pacientes", pacientes);
		// Se devuelve el nombre del bean de la vista
		return new ModelAndView("pacientePdfView");
	}

	@GetMapping("/especialidades-pdf")
	public ModelAndView generatePdfEspecialidad(Model model) {
		List<Especialidad> especialidades = especialidadService.findAll();
		// llenamos el modelo
		model.addAttribute("especialidades", especialidades);
		// Se devuelve el nombre del bean de la vista
		return new ModelAndView("especialidadPdfView");
	}
	
	@GetMapping("/vinculaciones-todas-pdf")
	public ModelAndView generatePdfVinculacionesTodas(Model model) {

		List<Vinculacion> vinculacionestodas = vinculacionService.findAll();

		if (vinculacionestodas == null || vinculacionestodas.isEmpty()) {
			// Lanza excepción
			model.addAttribute("error", "No se encontraron datos para mostrar el PDF.");
			return new ModelAndView("errorPdfView");
		}

		// llenamos el modelo
		model.addAttribute("vinculacionestodas", vinculacionestodas);
		// Se devuelve el nombre del bean (@Component) de la vista de la carpeta (package
		// com.sigat.springboot.app.view.pdf)
		return new ModelAndView("vinculacionesTodasPdfView");

	}

	// PARA REPORTES EN PDF********************************************

	@GetMapping("/turnos-ocupados-fechas-pdf")
	public ModelAndView generatePdfTurnosOcupadosFechas(Model model, @RequestParam("profId") Long profId,
			@RequestParam("especId") Long especId, @RequestParam("fechaIni") String fechaIni,
			@RequestParam("fechaFin") String fechaFin) {

		List<Turno> turnosocupadosfechas = turnoService.reporteTurnosOcupadosFechasPdf(profId, especId, fechaIni,
				fechaFin);

		if (turnosocupadosfechas == null || turnosocupadosfechas.isEmpty()) {
			// Lanza excepción
			model.addAttribute("error", "No se encontraron datos para mostrar el PDF.");
			return new ModelAndView("errorPdfView");
		}

		// llenamos el modelo
		model.addAttribute("turnosocupadosfechas", turnosocupadosfechas);
		// le enviamos al modelo fecha desde y fecha hasta.
		model.addAttribute("fechaInicioReporte", fechaIni);
		model.addAttribute("fechaFinReporte", fechaFin);
		// Se devuelve el nombre del bean de la vista de la carpeta (package
		// com.sigat.springboot.app.view.pdf)
		return new ModelAndView("turnosOcupadosFechasPdfView");

	}
	
	@GetMapping("/sobreturnos-ocupados-fechas-pdf")
	public ModelAndView generatePdfSobreturnosOcupadosFechas(Model model, @RequestParam("profId") Long profId,
			@RequestParam("especId") Long especId, @RequestParam("fechaIni") String fechaIni,
			@RequestParam("fechaFin") String fechaFin) {

		List<Sobreturno> sobreturnosocupadosfechas = sobreturnoService.reporteSobreturnosOcupadosFechasPdf(profId, especId, fechaIni,
				fechaFin);

		if (sobreturnosocupadosfechas == null || sobreturnosocupadosfechas.isEmpty()) {
			// Lanza excepción
			model.addAttribute("error", "No se encontraron datos para mostrar el PDF.");
			return new ModelAndView("errorPdfView");
		}

		// llenamos el modelo
		model.addAttribute("sobreturnosocupadosfechas", sobreturnosocupadosfechas);
		// le enviamos al modelo fecha desde y fecha hasta.
		model.addAttribute("fechaInicioReporte", fechaIni);
		model.addAttribute("fechaFinReporte", fechaFin);
		// Se devuelve el nombre del bean de la vista de la carpeta (package
		// com.sigat.springboot.app.view.pdf)
		return new ModelAndView("sobreturnosOcupadosFechasPdfView");

	}
	
	@GetMapping("/planillas-activas-fechas-pdf")
	public ModelAndView generatePdfPlanillasActivasFechas(Model model, @RequestParam("profId") Long profId,
			@RequestParam("especId") Long especId, @RequestParam("fechaIni") String fechaIni,
			@RequestParam("fechaFin") String fechaFin) {

		List<PlanillaCabecera> planillasactivasfechas = planillacabeceraService.reportePlanillasActivasFechasPdf(profId, especId, fechaIni,
				fechaFin);

		if (planillasactivasfechas == null || planillasactivasfechas.isEmpty()) {
			// Lanza excepción
			model.addAttribute("error", "No se encontraron datos para mostrar el PDF.");
			return new ModelAndView("errorPdfView");
		}

		// llenamos el modelo
		model.addAttribute("planillasactivasfechas", planillasactivasfechas);
		// le enviamos al modelo fecha desde y fecha hasta.
		model.addAttribute("fechaInicioReporte", fechaIni);
		model.addAttribute("fechaFinReporte", fechaFin);
		// Se devuelve el nombre del bean (@Component) de la vista de la carpeta (package
		// com.sigat.springboot.app.view.pdf)
		return new ModelAndView("planillasActivasFechasPdfView");

	}
	
	@GetMapping("/movimientos-fechas-pdf")
	public ModelAndView generatePdfMovimientosFechas(Model model, @RequestParam("profId") Long profId,
			@RequestParam("especId") Long especId, @RequestParam("fechaIni") String fechaIni,
			@RequestParam("fechaFin") String fechaFin) {

		List<Movimiento> movimientosfechas = movimientoService.reporteMovimientosFechasPdf(profId, especId, fechaIni,
				fechaFin);

		if (movimientosfechas == null || movimientosfechas.isEmpty()) {
			// Lanza excepción
			model.addAttribute("error", "No se encontraron datos para mostrar el PDF.");
			return new ModelAndView("errorPdfView");
		}

		// llenamos el modelo
		model.addAttribute("movimientosfechas", movimientosfechas);
		// le enviamos al modelo fecha desde y fecha hasta.
		model.addAttribute("fechaInicioReporte", fechaIni);
		model.addAttribute("fechaFinReporte", fechaFin);
		// Se devuelve el nombre del bean (@Component) de la vista de la carpeta (package
		// com.sigat.springboot.app.view.pdf)
		return new ModelAndView("movimientosFechasPdfView");

	}
	
	
	@GetMapping("/vinculaciones-profesional-pdf")
	public ModelAndView generatePdfVinculacionesByProf(Model model, @RequestParam("profId") Long profId) {

		List<Vinculacion> vinculacionesbyprof = vinculacionService.reporteVinculacionesByProfPdf(profId);

		if (vinculacionesbyprof == null || vinculacionesbyprof.isEmpty()) {
			// Lanza excepción
			model.addAttribute("error", "No se encontraron datos para mostrar el PDF.");
			return new ModelAndView("errorPdfView");
		}

		// llenamos el modelo
		model.addAttribute("vinculacionesbyprof", vinculacionesbyprof);
		// Se devuelve el nombre del bean (@Component) de la vista de la carpeta (package
		// com.sigat.springboot.app.view.pdf)
		return new ModelAndView("vinculacionesByProfPdfView");

	}

	// ********************************************************************

}

package com.sigat.springboot.app.models.dao;

import java.util.Date;
import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.CrudRepository;
import org.springframework.data.repository.query.Param;

import com.sigat.springboot.app.models.entity.Especialidad;
import com.sigat.springboot.app.models.entity.PlanillaCabecera;
import com.sigat.springboot.app.models.entity.PlanillaDetalle;

public interface IPlanillaDetalleDao extends JpaRepository<PlanillaDetalle, Long>, CrudRepository<PlanillaDetalle, Long>{

	/*
	 * @Query(value
	 * ="declare @fec_inicio Datetime, @fec_fin Datetime SET DATEFIRST 7, LANGUAGE English; "
	 * + "select @fec_inicio = :fechahorainicio, " + "@fec_fin = :fechahorafin; " +
	 * "WITH FECHAS(fecha) AS (SELECT @fec_inicio as fecha " + "UNION ALL " +
	 * "SELECT DATEADD(MINUTE, :intervalo, fecha) fecha FROM FECHAS " +
	 * "WHERE fecha < @fec_fin) " +
	 * "select DENSE_RANK() OVER (ORDER BY DATEPART(DAY,fecha) ASC) AS GRUPO, " +
	 * "FECHA from FECHAS " +
	 * "WHERE CONVERT(time, fecha) BETWEEN (CONVERT(time, @fec_inicio)) " +
	 * "AND (CONVERT(time, @fec_fin)) and DATEPART(DW, fecha) = :iddia " +
	 * "ORDER BY fecha OPTION (MaxRecursion 0)",nativeQuery = true) public
	 * List<PlanillaDetalle>crearRangoTurnoMañana(@Param("fechahorainicio") Date
	 * fechahorainicio,
	 * 
	 * @Param("fechahorafin") Date fechahorafin,
	 * 
	 * @Param("intervalo") Integer intervalo,
	 * 
	 * @Param("iddia") Integer iddia);
	 */
	
	@Query("select pc from PlanillaCabecera pc where pc.id = ?1")
	public List<PlanillaCabecera> findplanillaCabecera(Long term);
}


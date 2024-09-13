package com.sigat.springboot.app.models.dao;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.repository.CrudRepository;
import org.springframework.data.repository.PagingAndSortingRepository;

import com.sigat.springboot.app.models.entity.PlanillaCabecera;

public interface IPlanillaCabeceraDao
		extends JpaRepository<PlanillaCabecera, Long>, PagingAndSortingRepository<PlanillaCabecera, Long>, CrudRepository<PlanillaCabecera, Long> {

}

/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.hadirapp.HadirApproval.repository;

import com.hadirapp.HadirApproval.entity.Approval;
import com.hadirapp.HadirApproval.entity.CalendarHoliday;
import java.util.List;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

/**
 *
 * @author creative
 */
@Repository
public interface HolidayRepository extends JpaRepository<CalendarHoliday, Integer> {

    @Query(value = "SELECT * FROM calendar_holiday where calendar_holiday_active = 'true'", nativeQuery = true)
    Iterable<CalendarHoliday> getActiveHoliday();
}

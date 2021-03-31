/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.hadirapp.HadirApproval.repository;

import com.hadirapp.HadirApproval.entity.BootcampDetail;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

/**
 *
 * @author herli
 */
@Repository
public interface BootcampDetailRepository extends JpaRepository<BootcampDetail, String>{
    
    @Query(value="SELECT bd.bootcamp_id FROM bootcamp_detail bd JOIN users u ON bd.user_id = u.user_id WHERE u.user_id = ?1", nativeQuery = true)
    public String getEmployeeBootcampId(@Param ("id") String id);
    
    @Query(value="SELECT bd.user_id FROM bootcamp_detail bd JOIN users u ON bd.user_id = u.user_id WHERE u.role_id = '4' AND bd.bootcamp_id = ?1", nativeQuery = true)
    public String getTrainerByBootcampId(@Param ("id") String id);
}

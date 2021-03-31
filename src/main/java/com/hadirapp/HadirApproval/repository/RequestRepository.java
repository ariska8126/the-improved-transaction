/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.hadirapp.HadirApproval.repository;

import com.hadirapp.HadirApproval.entity.Request;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

/**
 *
 * @author herli
 */
@Repository
public interface RequestRepository extends JpaRepository<Request, String>{
    
    @Query(value="SELECT IF(EXISTS(SELECT * FROM request WHERE request_id = ?1),1,0)", nativeQuery = true)
    public int cekIfExistRequestId(@Param ("id") String id);
    
}

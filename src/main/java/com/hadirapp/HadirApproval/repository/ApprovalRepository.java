/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.hadirapp.HadirApproval.repository;

import com.hadirapp.HadirApproval.entity.Approval;
import java.util.List;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

/**
 *
 * @author herli
 */
@Repository
public interface ApprovalRepository extends JpaRepository<Approval, String>{
    
    @Query(value="SELECT * FROM approval WHERE approval_requester_id = ?1", nativeQuery = true)
    List <Approval> findByRequesterID(@Param ("id") String id);
    
    @Query(value="SELECT * FROM `approval` WHERE approval_approver_id =  ?1", nativeQuery = true)
    List <Approval> findByApproverID(@Param ("id") String id);
    
    
}

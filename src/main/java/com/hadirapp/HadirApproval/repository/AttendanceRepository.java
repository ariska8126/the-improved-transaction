/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.hadirapp.HadirApproval.repository;

import com.hadirapp.HadirApproval.entity.Attendance;
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
public interface AttendanceRepository extends JpaRepository<Attendance, String>{
    
    //review 
    @Query(value="SELECT att.* FROM approval a JOIN users u ON a.approval_requester_id = u.user_id JOIN attendance att ON att.user_id = u.user_id JOIN request r ON a.request_id = r.request_id WHERE r.request_id = ?1 AND approval_approver_id = ?2", nativeQuery = true)
    List <Attendance> findDetailApprovalByRequestID(@Param ("rid") String reqId, @Param ("id") String appId);
    
}

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
public interface ApprovalRepository extends JpaRepository<Approval, String> {

    @Query(value = "SELECT * FROM approval WHERE approval_requester_id = ?1", nativeQuery = true)
    List<Approval> findByRequesterID(@Param("id") String id);

    @Query(value = "SELECT * FROM approval WHERE approval_approver_id = ?1 OR approval_requester_id = ?1", nativeQuery = true)
    List<Approval> findByID(@Param("id") String id);

    @Query(value = "SELECT IF(EXISTS(SELECT * FROM approval WHERE approval_id = ?1),1,0)", nativeQuery = true)
    public int cekIfExistApprovalId(@Param("id") String id);

    //if exist approval by requester id this month
    @Query(value = "SELECT IF( EXISTS(SELECT * from approval WHERE MONTH(approval_date) = month(curdate()) AND approval_requester_id = ?1),1,0)", nativeQuery = true)
    public int cekIfExistApprovalThisMonth(@Param("id") String id);
    
    //if exist approval by requester id last month
    @Query(value = "SELECT IF( EXISTS(SELECT * from approval WHERE MONTH(approval_date) = month(curdate())-1 AND approval_requester_id = ?1),1,0)", nativeQuery = true)
    public int cekIfExistApprovalLastMonth(@Param("id") String id);
    
    //select approval by requester id
    @Query(value = "SELECT * from approval WHERE MONTH(approval_date) = month(curdate()) AND approval_requester_id = ?1", nativeQuery = true)
    public Approval findApprovalByRequesterId(@Param("id") String id);
    
    //select last month approval by requester id
    @Query(value = "SELECT * from approval WHERE MONTH(approval_date) = month(curdate())-1 AND approval_requester_id = ?1", nativeQuery = true)
    public Approval findLastMonthApprovalByRequesterId(@Param("id") String id);
    
    //select approval id this month approval by requester id
    @Query(value = "SELECT approval_id from approval WHERE MONTH(approval_date) = month(curdate()) AND approval_requester_id = ?1 ORDER BY approval_date DESC LIMIT 1", nativeQuery = true)
    public String findThisMonthApprovalIdByRequesterId(@Param("id") String id);

    @Query(value = "SELECT * from approval WHERE approval_id = ?1", nativeQuery = true)
    public Approval findByApprovalId(@Param("id") String id);

    @Query(value = "SELECT d.division_id FROM users u JOIN division d ON d.division_id = u.division_id WHERE user_id = ?1", nativeQuery = true)
    public String findDivisionByUserId(@Param("id") String id);

    @Query(value = "SELECT user_id FROM `users` WHERE division_id = ?1 AND role_id = '2'", nativeQuery = true)
    public String findManagerIdbyDivision(@Param("id") String id);

    @Query(value = "SELECT role_id FROM users WHERE user_id = ?1", nativeQuery = true)
    public int findRoleUserbyId(@Param("id") String id);

    @Query(value = "SELECT approval_requester_id FROM approval WHERE request_id = ?1 AND approval_approver_id = ?2", nativeQuery = true)
    public String findTrainerIdByManager(@Param("reqId") String reqId, @Param("appId") String appId);
    
    //list approval by request id 
    @Query(value = "SELECT a.* FROM approval a JOIN request r ON a.request_id = r.request_id WHERE r.request_id = ?1 ORDER BY approval_date ASC ", nativeQuery = true)
    List<Approval> findListApprovalByRequestID(@Param("rid") String reqId);
    
    //select approval by request id & status = approve by trainer
    @Query(value="SELECT a.* FROM `approval` a JOIN approval_status ap ON a.approval_status_id = ap.approval_status_id WHERE ap.approval_status_id = '3' AND a.request_id = ?1", nativeQuery = true)
    public Approval findByRequestAndStatus(@Param ("id") String requestId);
    
    //select approval by request id and trainer id = x
    @Query(value="SELECT a.* FROM `approval` a JOIN approval_status ap ON a.approval_status_id = ap.approval_status_id WHERE a.approval_approver_id = ?2 AND a.request_id = ?1", nativeQuery = true)
    public Approval findByRequestAndTrainerId(@Param ("id") String requestId,@Param ("trainerid") String trainerId);

}
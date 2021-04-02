/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.hadirapp.HadirApproval.repository;

import com.hadirapp.HadirApproval.entity.Attendance;
import java.util.List;
import com.hadirapp.HadirApproval.entity.Users;
import java.util.Optional;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

/**
 *
 * <<<<<<< HEAD
 * @a
 *
 * uthor herli
 */
@Repository
public interface AttendanceRepository extends JpaRepository<Attendance, String> {

    //review 
    @Query(value = "SELECT att.* FROM approval a JOIN users u ON a.approval_requester_id = u.user_id JOIN attendance att ON att.user_id = u.user_id JOIN request r ON a.request_id = r.request_id WHERE r.request_id = ?1 AND approval_approver_id = ?2", nativeQuery = true)
    List<Attendance> findDetailApprovalByRequestID(@Param("rid") String reqId, @Param("id") String appId);

    @Query(value = "SELECT * FROM attendance where attendance.user_id = :userId ORDER BY attendance_date DESC, attendance_time ASC", nativeQuery = true)
    public Iterable<Attendance> getAllAttendanceById(@Param("userId") String userId);

    //get user by id
    @Query(value = "SELECT * FROM users WHERE users.user_token = ?1", nativeQuery = true)
    public Optional<Users> getUsersByToken(@Param("id") String id);

    @Query(value = "select role_name from users join role on role.role_id = users.role_id WHERE users.user_id = ?1", nativeQuery = true)
    public String getUsersRole(@Param("id") String id);

    @Query(value = "SELECT count(*) FROM attendance where attendance.attendance_date = :date and user_id = :userId and attendance_type = 'start'", nativeQuery = true)
    public int validateCheckin(@Param("date") String date, @Param("userId") String userId);

    @Query(value = "SELECT count(*) FROM attendance where attendance.attendance_date = :date and user_id = :userId and attendance_type = 'end'", nativeQuery = true)
    public int validateCheckout(@Param("date") String date, @Param("userId") String userId);

    @Query(value = "SELECT count(*) FROM attendance where attendance.attendance_date = :date and user_id = :userId and attendance_type = 'leave'", nativeQuery = true)
    public int validateLeave(@Param("date") String date, @Param("userId") String userId);

    @Query(value = "SELECT * FROM attendance where attendance.attendance_date = :date and user_id = :userId and attendance_type in ('start','end')", nativeQuery = true)
    Iterable<Attendance> checkDuration(@Param("date") String date, @Param("userId") String userId);

    @Query(value = "SELECT IF(EXISTS(SELECT * FROM users WHERE users.user_token = ?1),1,0)", nativeQuery = true)
    public int findIfExistToken(@Param("token") String mail);

    @Query(value = "SELECT * FROM attendance where attendance.attendance_date = :date and user_id = :userId and attendance_type = 'start'", nativeQuery = true)
    public Optional<Attendance> getStatusCheckin(@Param("date") String date, @Param("userId") String userId);

    @Query(value = "SELECT IF(EXISTS(SELECT * FROM attendance where attendance.attendance_date = :date and user_id = :userId and attendance_type = 'start'),1,0)", nativeQuery = true)
    public int findIfExistAttendance(@Param("date") String date, @Param("userId") String userId);
}

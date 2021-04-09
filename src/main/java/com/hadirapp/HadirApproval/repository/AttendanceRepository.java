/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.hadirapp.HadirApproval.repository;

import com.hadirapp.HadirApproval.entity.Approval;
import com.hadirapp.HadirApproval.entity.Attendance;
import com.hadirapp.HadirApproval.entity.Bootcamp;
import java.util.List;
import com.hadirapp.HadirApproval.entity.Users;
import java.util.Optional;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

/**
 *
 *
 * @author herli
 */
@Repository
public interface AttendanceRepository extends JpaRepository<Attendance, String> {

    //review 
    @Query(value = "SELECT att.* FROM approval a JOIN users u ON a.approval_requester_id = u.user_id JOIN attendance att ON att.user_id = u.user_id JOIN request r ON a.request_id = r.request_id WHERE r.request_id = ?1 AND approval_approver_id = ?2", nativeQuery = true)
    List<Attendance> findDetailApprovalByRequestID(@Param("rid") String reqId, @Param("id") String appId);

    @Query(value = "SELECT * FROM `attendance` WHERE attendance_date BETWEEN ?2 AND ?3 AND user_id = ?1 ORDER BY attendance_date DESC, attendance_time ASC", nativeQuery = true)
    List<Attendance> findLastMonthAttendanceByUserId(@Param("userId") String userId, @Param("startDate") String startDate, @Param("endDate") String endDate);

//list attendance by request id
    @Query(value = "SELECT att.* FROM approval a JOIN users u ON a.approval_requester_id = u.user_id JOIN attendance att ON att.user_id = u.user_id JOIN request r ON a.request_id = r.request_id WHERE r.request_id = ?1 AND att.attendance_date BETWEEN r.request_date_start AND r.request_date_end ORDER BY att.attendance_date DESC, att.attendance_time ASC", nativeQuery = true)
    List<Attendance> findDetailApprovalByRequestIDnew(@Param("rid") String reqId);

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
    
    @Query(value = "select * from attendance where user_id = :userId and attendance_type = 'leave'", nativeQuery = true)
    Iterable<Attendance> getAllLeaveByUser(@Param("userId") String userId);

    @Query(value = "SELECT * FROM attendance where attendance.attendance_date = :date and user_id = :userId and attendance_type in ('start','end')", nativeQuery = true)
    Iterable<Attendance> checkDuration(@Param("date") String date, @Param("userId") String userId);

    @Query(value = "SELECT IF(EXISTS(SELECT * FROM users WHERE users.user_token = ?1),1,0)", nativeQuery = true)
    public int findIfExistToken(@Param("token") String mail);

    @Query(value = "SELECT * FROM attendance where attendance.attendance_date = :date and user_id = :userId and attendance_type = 'start'", nativeQuery = true)
    public Optional<Attendance> getStatusCheckin(@Param("date") String date, @Param("userId") String userId);

    @Query(value = "SELECT IF(EXISTS(SELECT * FROM attendance where attendance.attendance_date = :date and user_id = :userId and attendance_type = 'start'),1,0)", nativeQuery = true)
    public int findIfExistAttendance(@Param("date") String date, @Param("userId") String userId);

    // For reports
    @Query(value = "select u.user_fullname, u.user_id, d.division_name, a.* from users u join attendance a on u.user_id = a.user_id join division d on d.division_id = u.division_id where a.user_id = ?1", nativeQuery = true)
    Iterable<Attendance> atteanceReport(@Param("id") String id);

    //select last 10 attendance
    @Query(value = "SELECT att.* FROM attendance att JOIN users u ON att.user_id = u.user_id JOIN bootcamp_detail bd ON u.user_id = bd.user_id JOIN bootcamp b ON b.bootcamp_id = bd.bootcamp_id JOIN attendance_status ats on ats.attendance_status_id = att.attendance_status_id WHERE b.bootcamp_id = ?1 AND ats.attendance_status_id = '1' AND att.attendance_date = curdate() ORDER BY att.attendance_time DESC LIMIT 10", nativeQuery = true)
    public List<Attendance> findLastAttendanceByBootcampId(@Param("id") String id);
    //select last 10 attendance

    @Query(value = "SELECT att.* FROM attendance att JOIN users u ON att.user_id = u.user_id JOIN bootcamp_detail bd ON u.user_id = bd.user_id JOIN bootcamp b ON b.bootcamp_id = bd.bootcamp_id JOIN attendance_status ats on ats.attendance_status_id = att.attendance_status_id WHERE b.bootcamp_id = ?1 AND att.attendance_date = curdate() AND ats.attendance_status_id = '2' OR ats.attendance_status_id = '3' OR ats.attendance_status_id = '4' OR ats.attendance_status_id = '7' ORDER BY att.attendance_time DESC LIMIT 10", nativeQuery = true)
    public List<Attendance> findLastLeaveByBootcampId(@Param("id") String id);

    @Query(value = "SELECT count(*) FROM attendance \n"
            + "join users on users.user_id = attendance.user_id \n"
            + "join bootcamp_detail on users.user_id = bootcamp_detail.user_id \n"
            + "where attendance.attendance_type = 'start' \n"
            + "and attendance.attendance_date = curdate() \n"
            + "and bootcamp_detail.bootcamp_id = :bootcampId", nativeQuery = true)
    public int getAttendanceByBootcamp(@Param("bootcampId") String id);

    @Query(value = "SELECT count(*) FROM attendance \n"
            + "join users on users.user_id = attendance.user_id \n"
            + "join bootcamp_detail on users.user_id = bootcamp_detail.user_id \n"
            + "where attendance.attendance_type = 'leave' \n"
            + "and attendance.attendance_date = curdate() \n"
            + "and bootcamp_detail.bootcamp_id = :bootcampId", nativeQuery = true)
    public int getLeaveByBootcamp(@Param("bootcampId") String id);

    @Query(value = "SELECT count(*) FROM attendance \n"
            + "join users on users.user_id = attendance.user_id \n"
            + "join bootcamp_detail on users.user_id = bootcamp_detail.user_id \n"
            + "where attendance.attendance_type = 'start' \n"
            + "and attendance.attendance_date = curdate() \n"
            + "and attendance.attendance_time > '09:00:00' \n"
            + "and bootcamp_detail.bootcamp_id = :bootcampId", nativeQuery = true)
    public int getLateByBootcamp(@Param("bootcampId") String id);

    @Query(value = "select count(*) from users join bootcamp_detail\n"
            + "on users.user_id = bootcamp_detail.user_id\n"
            + "where bootcamp_detail.bootcamp_id = :bootcampId\n"
            + "and users.role_id = 5", nativeQuery = true)
    public int getBootcampParticipant(@Param("bootcampId") String id);

    // Attendance Chart
    @Query(value = "select week(a.attendance_date) as week_no, count(*) as attendance\n"
            + "from attendance a \n"
            + "join users u on a.user_id = u.user_id\n"
            + "join bootcamp_detail b on b.user_id = u.user_id\n"
            + "WHERE YEAR(a.attendance_date) = YEAR(CURDATE()) \n"
            + "and a.attendance_type = 'end'\n"
            + "and b.bootcamp_id = :bootcampId\n"
            + "GROUP BY WEEK(attendance_date)", nativeQuery = true)
    public List<Attendance> getWeeklyAttendance(@Param("bootcampId") String id);

    // Attendance list by bootcamp
    @Query(value = "SELECT u.user_fullname, a.* from users u join attendance a\n"
            + "on u.user_id = a.user_id join bootcamp_detail b \n"
            + "on b.user_id = u.user_id WHERE a.attendance_type in ('start','end')\n"
            + "and a.attendance_date = curdate()\n"
            + "and b.bootcamp_id = :bootcampId", nativeQuery = true)
    Iterable<Attendance> getTodayAttendanceByBootcamp(@Param("bootcampId") String id);

    // N/A List by Bootcamp
    @Query(value = "SELECT users.* from users where users.role_id = 5 \n"
            + "and user_id not in (SELECT a.user_id from attendance a \n"
            + "join users u on a.user_id = u.user_id \n"
            + "join bootcamp_detail b on u.user_id = b.user_id WHERE a.attendance_date = curdate()\n"
            + "and b.bootcamp_id = :bootcampId)", nativeQuery = true)
    Iterable<Attendance> getTodayNotPresentByBootcamp(@Param("bootcampId") String id);

    // Leave list by Bootcamp
    @Query(value = "SELECT u.user_fullname, a.* from users u join attendance a\n"
            + "on u.user_id = a.user_id join bootcamp_detail b \n"
            + "on b.user_id = u.user_id WHERE a.attendance_type in ('leave')\n"
            + "and a.attendance_date >= curdate()\n"
            + "and b.bootcamp_id = :bootcampId", nativeQuery = true)
    Iterable<Attendance> getLeaveByBootcampUser(@Param("bootcampId") String id);

    // Get Attendace bu trainner
    @Query(value = "SELECT b.* FROM bootcamp b JOIN bootcamp_detail bd ON b.bootcamp_id = bd.bootcamp_id WHERE user_id = ?1", nativeQuery = true)

    public List<Bootcamp> getBootcamp(@Param("id") String id);

    // Get Bootcamp and Trainner
    @Query(value = "select attendance.* from attendance join users on attendance.user_id = users.user_id join bootcamp_detail on bootcamp_detail.user_id = users.user_id where bootcamp_detail.bootcamp_id in (:bootcampId) ORDER by attendance.attendance_date DESC, attendance_time ASC", nativeQuery = true)
    Iterable<Attendance> getAttendanceByBootcampTrainer(@Param("bootcampId") String bootcampId);

    // Get 
}

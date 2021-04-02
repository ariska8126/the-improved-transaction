/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.hadirapp.HadirApproval.controller;

import com.hadirapp.HadirApproval.entity.Attendance;
import com.hadirapp.HadirApproval.entity.AttendanceStatus;
import com.hadirapp.HadirApproval.entity.Users;
import com.hadirapp.HadirApproval.repository.AttendanceRepository;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.concurrent.TimeUnit;
import net.minidev.json.JSONArray;
import net.minidev.json.JSONObject;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

/**
 *
 * @author creative
 */
@RestController
@RequestMapping("/api/transac/attendance")
@Api(tags = "Attendance Management")
public class AttendanceController {

    @Autowired
    AttendanceRepository attendanceRepository;

    @GetMapping("/getattendance")
    @ApiOperation(value = "Get all attendance")
    public String getAttendance() {
        List<Attendance> attendance = attendanceRepository.findAll();
        JSONArray jsonArray = new JSONArray();
        JSONObject jsonObject2 = new JSONObject();

        for (Attendance attendances : attendance) {
            JSONObject jsonObject = new JSONObject();
            jsonObject.put("id", attendances.getAttendanceId());
            jsonObject.put("date", attendances.getAttendanceDate().toString());
            jsonObject.put("time", attendances.getAttendanceTime().toString());
            jsonObject.put("remark", attendances.getAttendanceRemark());
            jsonObject.put("type", attendances.getAttendanceType());
            jsonObject.put("status", attendances.getAttendanceStatusId().getAttendanceStatusName());
            jsonObject.put("employee", attendances.getUserId().getUserFullname());
            jsonArray.add(jsonObject);
        }

        jsonObject2.put("attendance_list", jsonArray);

        return jsonObject2.toString();
    }

    @GetMapping("/getattendancebyid/{id}")
    @ApiOperation(value = "Get attendance by id")
    public String getAttendanceById(@RequestHeader("bearer") String header, @PathVariable String id) {

        System.out.println("===== Request /getattendancebyid/{id} in Attendance =====");
        JSONArray jsonArray = new JSONArray();
        JSONObject jsonObject2 = new JSONObject();
        int tokenExist = attendanceRepository.findIfExistToken(header);

        if (tokenExist == 1) {
            System.out.println("===== Token is Exist =====");
            Iterable<Attendance> attendances = attendanceRepository.getAllAttendanceById(id);
            for (Attendance attendance : attendances) {
                JSONObject jsonObject = new JSONObject();
                jsonObject.put("attendanceId", attendance.getAttendanceId());
                jsonObject.put("attendanceDate", attendance.getAttendanceDate().toString());
                jsonObject.put("attendanceTime", attendance.getAttendanceTime().toString());
                jsonObject.put("attendanceRemark", attendance.getAttendanceRemark());
                jsonObject.put("attendanceAttachment", attendance.getAttendanceAttachment());
                jsonObject.put("attendanceType", attendance.getAttendanceType());
                jsonObject.put("attendanceStatusId", attendance.getAttendanceStatusId().getAttendanceStatusId());
                jsonObject.put("attendanceStatusName", attendance.getAttendanceStatusId().getAttendanceStatusName());
                jsonObject.put("userId", attendance.getUserId().getUserId());
                jsonObject.put("userFullname", attendance.getUserId().getUserFullname());
                jsonObject.put("attendanceLongitude", attendance.getAttendanceLogitude());
                jsonObject.put("attendanceLatitude", attendance.getAttendanceLatitude());

                jsonArray.add(jsonObject);
            }

            jsonObject2.put("attendanceList", jsonArray);

            return jsonObject2.toString();

        } else {
            System.out.println("===== Wrong/Expire Token =====");
            jsonObject2.put("status", "false");
            jsonObject2.put("description", "you don't have authorization to access");

            return jsonObject2.toJSONString();
        }

    }

    @GetMapping("/home")
    @ApiOperation(value = "Employee Attendance Home")
    public String homeEmployee(@RequestHeader("bearer") String header) {

        System.out.println("===== Request /home in Attendance =====");
        JSONObject jSONObject = new JSONObject();
        int tokenExist = attendanceRepository.findIfExistToken(header);
        if (tokenExist == 1) {
            System.out.println("===== Token is Exist =====");

            // get current date and userId
            Optional<Users> userIdObj = attendanceRepository.getUsersByToken(header);
            String userId = userIdObj.get().getUserId();
            // get user role
            String userRole = attendanceRepository.getUsersRole(userId);

            System.out.println("===== User Details =====");
            System.out.println("User Id : " + userId);
            System.out.println("User Role : " + userRole);

            // Check role 
            if (userRole.equals("employee")) {

                System.out.println("===== Permit Access =====");
                SimpleDateFormat formatter = new SimpleDateFormat("yyyy-MM-dd");
                Date date = new Date();
                String currDate = formatter.format(date);
                int attendanceExist = attendanceRepository.findIfExistAttendance(currDate, userId);

                // Check attendance
                if (attendanceExist == 1) {
                    System.out.println("===== Attendance was Exist =====");
                    Optional<Attendance> attendanceCheckIn = attendanceRepository.getStatusCheckin(currDate, userId);

                    System.out.println("Check in time : " + attendanceCheckIn.get().getAttendanceTime().toString());
                    System.out.println("===== Return JSON =====");

                    jSONObject.put("status", "true");
                    jSONObject.put("checkInTime", attendanceCheckIn.get().getAttendanceTime().toString());
                    return jSONObject.toJSONString();
                } else {
                    jSONObject.put("status", "false");
                    jSONObject.put("checkInTime", "");
                    return jSONObject.toJSONString();
                }

            } else {
                System.out.println("===== Access Denied =====");
                System.out.println("User Role : " + userRole);

                jSONObject.put("status", "false");
                jSONObject.put("description", "Access denied");
                jSONObject.put("roleName", userRole);
                return jSONObject.toJSONString();
            }

        } else {
            System.out.println("===== Wrong/Expire Token =====");
            jSONObject.put("status", "false");
            jSONObject.put("description", "you don't have authorization to access");
            return jSONObject.toJSONString();
        }

    }

    @GetMapping("/test")
    public String testAttendance(@RequestHeader("bearer") String header) {
        //Check token is exist or not
        int tokenExist = attendanceRepository.findIfExistToken(header);
        if (tokenExist == 1) {
            Optional<Users> userIdObj = attendanceRepository.getUsersByToken(header);
            String userId = userIdObj.get().getUserId();
            String userRole = attendanceRepository.getUsersRole(userId);
            System.out.println(userRole);

            System.out.println("User Role : " + userRole);
            if (userRole.equals("employee")) {
                System.out.println("Permit");
            } else {
                System.out.println("Access Denied");
            }

        } else {
            System.out.println("Please Login");
        }

        //System.out.println(userToken);
//        String userId = userIdObj.get().getUserId();
//        SimpleDateFormat formatter = new SimpleDateFormat("yyyy-MM-dd");
//        Date date = new Date();
//        String currDate = formatter.format(date);
//        System.out.println(currDate);
//        System.out.println(userId);
//
//        int countAttendance = attendanceRepository.validateCheckin(currDate, userId);
//
//        JSONObject jSONObject = new JSONObject();
//        JSONObject jSONObject1 = new JSONObject();
//
//        jSONObject1.put("userId", userId);
//        jSONObject1.put("date", currDate);
//        jSONObject1.put("count", countAttendance);
        return "test";
    }

    @PostMapping("/checkin")
    @ApiOperation(value = "Employee Check In")
    public String checkIn(@RequestHeader("bearer") String header, @RequestBody Map<String, ?> input) throws ParseException {

        String attendanceRemark = (String) input.get("attendanceRemark");
        String attendanceAttachment = (String) input.get("attendanceAttachment");
        Integer attendanceStatusId = (int) input.get("attendanceStatusId");
        String attendanceLongitude = (String) input.get("attendanceLongitude");
        String attendanceLatitude = (String) input.get("attendanceLatitude");
        JSONObject jSONObject = new JSONObject();

        int tokenExist = attendanceRepository.findIfExistToken(header);
        if (tokenExist == 1) {
            // Check in validation
            // Get user id
            Optional<Users> userIdObj = attendanceRepository.getUsersByToken(header);
            String userId = userIdObj.get().getUserId();

            // get user role
            String userRole = attendanceRepository.getUsersRole(userId);
            System.out.println(userRole);

            if (userRole.equals("employee")) {
                System.out.println("Permit");
                SimpleDateFormat formatter = new SimpleDateFormat("yyyy-MM-dd");
                SimpleDateFormat formatterComplete = new SimpleDateFormat("yyyyMMddhhmmss");
                SimpleDateFormat formattertime = new SimpleDateFormat("HH:mm:ss");
                Date date = new Date();

                String currDate = formatter.format(date);
                String time = formattertime.format(date);
                Date currentDate = formatter.parse(currDate);
                int countAttendance = attendanceRepository.validateCheckin(currDate, userId);
                int countLeave = attendanceRepository.validateLeave(currDate, userId);

                // value to checkin
                String insertDate = formatterComplete.format(date);
                String attendanceId = userId + insertDate;
                Date currTime = formattertime.parse(time);
                String attendanceType = "start";

                Attendance attendance = new Attendance(
                        attendanceId,
                        currentDate,
                        currTime,
                        attendanceRemark,
                        attendanceAttachment,
                        attendanceType,
                        attendanceLongitude,
                        attendanceLatitude,
                        new Users(userId),
                        new AttendanceStatus(attendanceStatusId));

                if (countAttendance == 0 && countLeave == 0) { // there is no attendance on several day and user id
                    attendanceRepository.save(attendance);
                } else {
                    jSONObject.put("status", "false");
                    jSONObject.put("description", "check-in failed, you've been checkin today");
                    return jSONObject.toJSONString();
                }

                jSONObject.put("status", "true");
                jSONObject.put("description", "check-in succesfully");
                jSONObject.put("checkinTime", currTime);
                jSONObject.put("checkinDate", currentDate);

                return jSONObject.toJSONString();
            } else {
                System.out.println("Access denied");
                jSONObject.put("status", "false");
                jSONObject.put("description", "Access denied");
                jSONObject.put("roleName", userRole);
                return jSONObject.toJSONString();
            }

        } else {
            System.out.println("Not Accepted");
            jSONObject.put("status", "false");
            jSONObject.put("description", "you don't have authorization to access");
            return jSONObject.toJSONString();
        }
    }

    @PostMapping("/checkout")
    @ApiOperation(value = "Employee Check Out")
    public String checkOut(@RequestHeader("bearer") String header, @RequestBody Map<String, ?> input) throws ParseException {
        String attendanceRemark = (String) input.get("attendanceRemark");
        String attendanceAttachment = (String) input.get("attendanceAttachment");
        Integer attendanceStatusId = (int) input.get("attendanceStatusId");
        String attendanceLongitude = (String) input.get("attendanceLongitude");
        String attendanceLatitude = (String) input.get("attendanceLatitude");
        JSONObject jSONObject = new JSONObject();

        int tokenExist = attendanceRepository.findIfExistToken(header);
        if (tokenExist == 1) {
            // Get user id
            Optional<Users> userIdObj = attendanceRepository.getUsersByToken(header);
            String userId = userIdObj.get().getUserId();

            // get user role
            String userRole = attendanceRepository.getUsersRole(userId);

            System.out.println(userRole);
            if (userRole.equals("employee")) {
                System.out.println("Permit");

                SimpleDateFormat formatter = new SimpleDateFormat("yyyy-MM-dd");
                SimpleDateFormat formatterComplete = new SimpleDateFormat("yyyyMMddhhmmss");
                SimpleDateFormat formattertime = new SimpleDateFormat("HH:mm:ss");
                Date date = new Date();

                String currDate = formatter.format(date);
                String time = formattertime.format(date);
                Date currentDate = formatter.parse(currDate);
                int countAttendance = attendanceRepository.validateCheckout(currDate, userId);

                // value to check out
                String insertDate = formatterComplete.format(date);
                String attendanceId = userId + insertDate;
                Date currTime = formattertime.parse(time);
                String attendanceType = "end";

                Attendance attendance = new Attendance(
                        attendanceId,
                        currentDate,
                        currTime,
                        attendanceRemark,
                        attendanceAttachment,
                        attendanceType,
                        attendanceLongitude,
                        attendanceLatitude,
                        new Users(userId),
                        new AttendanceStatus(attendanceStatusId));

                if (countAttendance == 0) { // there is no attendance on several day and user id
                    attendanceRepository.save(attendance);

                    // Attendance duration
                    ArrayList<Long> list = new ArrayList<Long>();
                    Iterable<Attendance> attendanceDuration = attendanceRepository.checkDuration(currDate, userId);
                    for (Attendance attendances : attendanceDuration) {
                        System.out.println(attendances.getAttendanceTime().getTime());
                        list.add(attendances.getAttendanceTime().getTime());
                    }
                    System.out.println("Check out");
                    System.out.println(list);
                    long timeDiff = list.get(0) - list.get(1);
                    long hoursDifference = TimeUnit.MILLISECONDS.toHours(timeDiff) % 24;

                    System.out.println(hoursDifference);
                } else {
                    jSONObject.put("status", "false");
                    jSONObject.put("description", "check-out failed, you've been check-out today");
                    return jSONObject.toJSONString();
                }

                jSONObject.put("status", "true");
                jSONObject.put("description", "check-out succesfully");
                jSONObject.put("checkoutTime", currTime);
                jSONObject.put("checkoutDate", currentDate);

                return jSONObject.toJSONString();

            } else {
                System.out.println("Access denied");
                jSONObject.put("status", "false");
                jSONObject.put("description", "Access denied");
                jSONObject.put("roleName", userRole);
                return jSONObject.toJSONString();
            }
        } else {
            System.out.println("Not Accepted");
            jSONObject.put("status", "false");
            jSONObject.put("description", "you don't have authorization to access");
            return jSONObject.toJSONString();
        }
    }

    @PostMapping("/leave")
    @ApiOperation(value = "Employee Leave")
    public String leave(@RequestHeader("bearer") String header, @RequestBody Map<String, ?> input) throws ParseException {
        String attendanceRemark = (String) input.get("attendanceRemark");
        String attendanceAttachment = (String) input.get("attendanceAttachment");
        Integer attendanceStatusId = (int) input.get("attendanceStatusId");
        String attendanceLongitude = (String) input.get("attendanceLongitude");
        String attendanceLatitude = (String) input.get("attendanceLatitude");
        String startDate = (String) input.get("startDate");
        String endDate = (String) input.get("endDate");

        JSONObject jSONObject = new JSONObject();
        int tokenExist = attendanceRepository.findIfExistToken(header);

        if (tokenExist == 1) {
            // Get user id
            Optional<Users> userIdObj = attendanceRepository.getUsersByToken(header);
            String userId = userIdObj.get().getUserId();

            // get user role
            String userRole = attendanceRepository.getUsersRole(userId);

            if (userRole.equals("employee")) {
                SimpleDateFormat formatter = new SimpleDateFormat("yyyy-MM-dd");
                SimpleDateFormat formatterComplete = new SimpleDateFormat("yyyyMMddhhmm");
                SimpleDateFormat formattertime = new SimpleDateFormat("HH:mm:ss");
                Date date = new Date();

                String currDate = formatter.format(date);
                String time = formattertime.format(date);
                Date currentDate = formatter.parse(currDate);
                int countAttendance = attendanceRepository.validateLeave(currDate, userId);

                Date currTime = formattertime.parse(time);
                String attendanceType = "leave";

                int startDateSubs = Integer.parseInt(startDate.substring(8));
                int endDateSubs = Integer.parseInt(endDate.substring(8));

                String dateSuffix = startDate.substring(0, 8);

                List<String> leaveDate = new ArrayList<String>();

                for (int i = startDateSubs; i <= endDateSubs; i++) {
                    leaveDate.add(dateSuffix + String.valueOf(i));
                }

                for (int j = 0; j < leaveDate.size(); j++) {
                    // value to checkin
                    String insertDate = formatterComplete.format(date);

                    String index = "";
                    if (j < 10) {
                        index = "0" + j;
                    } else {
                        index = String.valueOf(j);
                    }

                    String attendanceId = userId + insertDate + j;
                    System.out.println(attendanceId);

                    System.out.println(leaveDate.get(j));
                    Date insertLeave = formatter.parse(leaveDate.get(j));

                    System.out.println(insertLeave);

                    Attendance attendance = new Attendance(
                            attendanceId,
                            insertLeave,
                            currTime,
                            attendanceRemark,
                            attendanceAttachment,
                            attendanceType,
                            attendanceLongitude,
                            attendanceLatitude,
                            new Users(userId),
                            new AttendanceStatus(attendanceStatusId));

                    if (countAttendance < 10) { // there is no attendance on several day and user id
                        attendanceRepository.save(attendance);
                    } else {
                        jSONObject.put("status", "false");
                        jSONObject.put("description", "Leave failed, you've been leave today");
                        return jSONObject.toJSONString();
                    }
                }

                jSONObject.put("status", "true");
                jSONObject.put("description", "leave succesfully");

                return jSONObject.toJSONString();

            } else {
                System.out.println("Access denied");
                jSONObject.put("status", "false");
                jSONObject.put("description", "Access denied");
                jSONObject.put("roleName", userRole);
                return jSONObject.toJSONString();
            }
        } else {
            System.out.println("Not Accepted");
            jSONObject.put("status", "false");
            jSONObject.put("description", "you don't have authorization to access");
            return jSONObject.toJSONString();
        }

    }

}

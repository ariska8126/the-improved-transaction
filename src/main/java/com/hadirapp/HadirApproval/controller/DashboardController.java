/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.hadirapp.HadirApproval.controller;

import com.hadirapp.HadirApproval.entity.Attendance;
import com.hadirapp.HadirApproval.entity.Users;
import com.hadirapp.HadirApproval.repository.AttendanceRepository;
import com.hadirapp.HadirApproval.repository.UsersRepository;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import java.sql.Statement;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.ResultSet;
import java.sql.ResultSetMetaData;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import net.minidev.json.JSONArray;
import net.minidev.json.JSONObject;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

/**
 *
 * @author creative
 */
@RestController
@RequestMapping("/api/transac/dashboard")
@Api(tags = "Dashboard Management")
public class DashboardController {

    @Autowired
    AttendanceRepository attendanceRepository;

    @Autowired
    private UsersRepository usersRepository;

    private static final String URL = "jdbc:mysql://localhost:3306/hadir_db";
    private static final String USERNAME = "root";
    private static final String PASSWORD = "Metrodata.5";

    @GetMapping("/getattendancebybootcamp/{id}")
    @ApiOperation(value = "Get dashboard data bt bootcamp id")
    public String getDashboardBootcamp(@PathVariable String id, @RequestHeader("bearer") String header) {

        System.out.println("===== Request /getattendancebybootcamp/{id} in Dashboard =====");
        JSONArray jsonArray = new JSONArray();
        JSONObject jsonObject = new JSONObject();

        int tokenExist = attendanceRepository.findIfExistToken(header);
        if (tokenExist == 1) {
            // Get user id
            Optional<Users> userIdObj = attendanceRepository.getUsersByToken(header);
            String userId = userIdObj.get().getUserId();

            // get user role
            String userRole = attendanceRepository.getUsersRole(userId);
            System.out.println(userRole);

            if (userRole.equals("admin") || userRole.equals("trainer") || userRole.equals("manager passive") || userRole.equals("manager active")) {
                int countAttendance = attendanceRepository.getAttendanceByBootcamp(id);
                int countLeave = attendanceRepository.getLeaveByBootcamp(id);
                int countLate = attendanceRepository.getLateByBootcamp(id);
                int allParticipant = attendanceRepository.getBootcampParticipant(id);
                int notPresent = allParticipant - countAttendance;
                float attendancePrecentage = (float) countAttendance / allParticipant * 100;

                jsonObject.put("present", countAttendance);
                jsonObject.put("leave", countLeave);
                jsonObject.put("late", countLate);
                jsonObject.put("notPresent", notPresent);
                jsonObject.put("attendancePrecentage", attendancePrecentage);
                jsonObject.put("totalParticipant", allParticipant);

                return jsonObject.toJSONString();
            } else {
                System.out.println("Access denied");
                jsonObject.put("status", "false");
                jsonObject.put("description", "Access denied");
                jsonObject.put("roleName", userRole);
                return jsonObject.toJSONString();
            }

        } else {
            System.out.println("Not Accepted");
            jsonObject.put("status", "false");
            jsonObject.put("description", "you don't have authorization to access");
            return jsonObject.toJSONString();
        }

    }

    @GetMapping("/getweeklyattendance/{id}")
    @ApiOperation(value = "Get weekly attendance")
    public String getWeeklyAttendance(@PathVariable String id, @RequestHeader("bearer") String header) {

        int allParticipant = attendanceRepository.getBootcampParticipant(id);

        System.out.println("===== Request /getweeklyattendance/{id} in Dashboard =====");
        JSONArray jsonArray = new JSONArray();
        JSONObject jsonObject = new JSONObject();

        int tokenExist = attendanceRepository.findIfExistToken(header);
        if (tokenExist == 1) {
            // Get user id
            Optional<Users> userIdObj = attendanceRepository.getUsersByToken(header);
            String userId = userIdObj.get().getUserId();

            // get user role
            String userRole = attendanceRepository.getUsersRole(userId);
            System.out.println(userRole);

            if (userRole.equals("admin") || userRole.equals("trainer") || userRole.equals("manager passive") || userRole.equals("manager active")) {
                JSONObject jsonObject2 = new JSONObject();

                try ( Connection connection = DriverManager.getConnection(URL, USERNAME, PASSWORD)) {
                    Statement statement = connection.createStatement();
                    ResultSet resultSet = statement.executeQuery(
                            "select week(a.attendance_date) as weekNo, count(*) as attendanceNumber\n"
                            + "from attendance a \n"
                            + "join users u on a.user_id = u.user_id\n"
                            + "join bootcamp_detail b on b.user_id = u.user_id\n"
                            + "WHERE YEAR(a.attendance_date) = YEAR(CURDATE()) \n"
                            + "and a.attendance_type = 'end'\n"
                            + "and b.bootcamp_id = " + id + "\n"
                            + "GROUP BY WEEK(attendance_date)"
                    );

                    ResultSetMetaData metadata = resultSet.getMetaData();
                    int columnCount = metadata.getColumnCount();

                    JSONArray jsonArrayWhile = new JSONArray();
                    JSONArray jsonArrayWhile2 = new JSONArray();
                    JSONArray jsonArrayWhile3 = new JSONArray();

                    ArrayList<String> attendance = new ArrayList<String>();
                    ArrayList<Integer> attendanceInt = new ArrayList<Integer>();
                    ArrayList<Integer> leave = new ArrayList<Integer>();
                    ArrayList<String> leaveStr = new ArrayList<String>();

                    while (resultSet.next()) {

                        JSONObject jsonObjectWhile = new JSONObject();

                        String weekNo = resultSet.getString("weekNo");
                        String attendanceNumber = resultSet.getString("attendanceNumber");
                        attendance.add(attendanceNumber);

                        jsonArrayWhile.add(weekNo);
                        jsonArrayWhile2.add(attendanceNumber);
                    }

                    for (int i = 0; i < attendance.size(); i++) {
                        attendanceInt.add(Integer.parseInt(attendance.get(i)));
                    }

                    for (int i = 0; i < attendanceInt.size(); i++) {
                        leave.add(allParticipant - attendanceInt.get(i));
                    }

                    for (int i = 0; i < leave.size(); i++) {
                        leaveStr.add(leave.get(i).toString());
                    }

                    for (int i = 0; i < leaveStr.size(); i++) {
                        jsonArrayWhile3.add(leaveStr.get(i));
                    }

                    jsonObject2.put("weekList", jsonArrayWhile);
                    jsonObject2.put("weekAttendance", jsonArrayWhile2);
                    jsonObject2.put("weekLeave", jsonArrayWhile3);

                    return jsonObject2.toJSONString();

                } catch (SQLException e) {
                    e.printStackTrace();
                }

                jsonObject2.put("status", "false");
                jsonObject2.put("description", "Data not found");

                return jsonObject2.toJSONString();
            } else {
                System.out.println("Access denied");
                jsonObject.put("status", "false");
                jsonObject.put("description", "Access denied");
                jsonObject.put("roleName", userRole);
                return jsonObject.toJSONString();
            }

        } else {
            System.out.println("Not Accepted");
            jsonObject.put("status", "false");
            jsonObject.put("description", "you don't have authorization to access");
            return jsonObject.toJSONString();
        }

    }

    @GetMapping("/getleavebybootcamp/{id}")
    @ApiOperation("Get leave by bootcamp id")
    public String getLeaveByBootcamp(@PathVariable String id, @RequestHeader("bearer") String header) {
        System.out.println("===== Request /getlistbybootcamp/{id} in Dashboard =====");
        JSONArray jsonArray = new JSONArray();
        JSONObject jsonObject2 = new JSONObject();
        int tokenExist = attendanceRepository.findIfExistToken(header);

        if (tokenExist == 1) {
            // Cek User Role
            Optional<Users> userIdObj = attendanceRepository.getUsersByToken(header);
            String userId = userIdObj.get().getUserId();
            // get user role
            String userRole = attendanceRepository.getUsersRole(userId);

            System.out.println("===== User Details =====");
            System.out.println("User Id : " + userId);
            System.out.println("User Role : " + userRole);

            if (userRole.equals("admin") || userRole.equals("trainer") || userRole.equals("manager passive") || userRole.equals("manager active")) {
                System.out.println("===== Permit Access =====");
                Iterable<Attendance> attendances = attendanceRepository.getLeaveByBootcampUser(id);
                for (Attendance attendance : attendances) {
                    JSONObject jsonObject = new JSONObject();
                    jsonObject.put("attendanceDate", attendance.getAttendanceDate().toString());
                    jsonObject.put("userFullname", attendance.getUserId().getUserFullname());
                    jsonObject.put("attendanceLatitude", attendance.getAttendanceLatitude());
                    jsonObject.put("attendanceLongitude", attendance.getAttendanceLogitude());
                    jsonObject.put("attendanceTime", attendance.getAttendanceTime().toString());
                    jsonObject.put("attendanceType", attendance.getAttendanceType());
                    jsonObject.put("attendanceRemark", attendance.getAttendanceRemark());
                    jsonObject.put("attendanceAttachment", attendance.getAttendanceAttachment());
                    jsonObject.put("attendanceStatusId", attendance.getAttendanceStatusId().getAttendanceStatusId());
                    jsonObject.put("attendanceStatusName", attendance.getAttendanceStatusId().getAttendanceStatusName());
                    jsonObject.put("userId", attendance.getUserId().getUserId());

                    jsonArray.add(jsonObject);
                }

                jsonObject2.put("attendanceList", jsonArray);

                return jsonObject2.toString();
            } else {
                System.out.println("Access denied");
                jsonObject2.put("status", "false");
                jsonObject2.put("description", "Access denied");
                jsonObject2.put("roleName", userRole);
                return jsonObject2.toJSONString();
            }
        } else {
            System.out.println("===== Wrong/Expire Token =====");
            jsonObject2.put("status", "false");
            jsonObject2.put("description", "you don't have authorization to access");

            return jsonObject2.toJSONString();
        }
    }

    @GetMapping("/gettodaynotpresentbybootcamp/{id}")
    @ApiOperation("Get today not present by bootcamp id")
    public String getTodayNotPresent(@PathVariable String id, @RequestHeader("bearer") String header) {
        System.out.println("===== Request /gettodaynotpresentbybootcamp/{id} in Dashboard =====");
        JSONArray jsonArray = new JSONArray();
        JSONObject jsonObject2 = new JSONObject();
        int tokenExist = attendanceRepository.findIfExistToken(header);

        if (tokenExist == 1) {
            // Cek User Role
            Optional<Users> userIdObj = attendanceRepository.getUsersByToken(header);
            String userId = userIdObj.get().getUserId();
            // get user role
            String userRole = attendanceRepository.getUsersRole(userId);

            System.out.println("===== User Details =====");
            System.out.println("User Id : " + userId);
            System.out.println("User Role : " + userRole);

            if (userRole.equals("admin") || userRole.equals("trainer") || userRole.equals("manager passive") || userRole.equals("manager active")) {
                System.out.println("===== Permit Access =====");
                Iterable<Users> users = usersRepository.getTodayNotPresentBuBootcampUser(id);

                // Get Current Date
                long millis = System.currentTimeMillis();
                java.sql.Date date = new java.sql.Date(millis);

                for (Users user : users) {
                    JSONObject jsonObject = new JSONObject();
                    jsonObject.put("attendanceDate", date.toString());
                    jsonObject.put("userId", user.getUserId());
                    jsonObject.put("userFullname", user.getUserFullname());
                    jsonObject.put("userEmail", user.getUserEmail());

                    System.out.println("Name : " + user.getUserFullname());

                    jsonArray.add(jsonObject);
                }

                jsonObject2.put("attendanceList", jsonArray);

                return jsonObject2.toString();

            } else {
                System.out.println("Access denied");
                jsonObject2.put("status", "false");
                jsonObject2.put("description", "Access denied");
                jsonObject2.put("roleName", userRole);
                return jsonObject2.toJSONString();
            }
        } else {
            System.out.println("===== Wrong/Expire Token =====");
            jsonObject2.put("status", "false");
            jsonObject2.put("description", "you don't have authorization to access");

            return jsonObject2.toJSONString();
        }
    }

    @GetMapping("/gettodayattendancebybootcamp/{id}")
    @ApiOperation("Get today attendance by Bootcamp Id")
    public String getTodayAttendanceByBootcampId(@PathVariable String id, @RequestHeader("bearer") String header) {

        System.out.println("===== Request /gettodayattendancebtbootcamp/{id} in Dashboard =====");
        JSONArray jsonArray = new JSONArray();
        JSONObject jsonObject2 = new JSONObject();
        int tokenExist = attendanceRepository.findIfExistToken(header);

        if (tokenExist == 1) {
            // Cek User Role
            Optional<Users> userIdObj = attendanceRepository.getUsersByToken(header);
            String userId = userIdObj.get().getUserId();
            // get user role
            String userRole = attendanceRepository.getUsersRole(userId);

            System.out.println("===== User Details =====");
            System.out.println("User Id : " + userId);
            System.out.println("User Role : " + userRole);

            if (userRole.equals("admin") || userRole.equals("trainer") || userRole.equals("manager passive") || userRole.equals("manager active")) {
                System.out.println("===== Permit Access =====");
                Iterable<Attendance> attendances = attendanceRepository.getTodayAttendanceByBootcamp(id);
                for (Attendance attendance : attendances) {
                    JSONObject jsonObject = new JSONObject();
                    jsonObject.put("attendanceDate", attendance.getAttendanceDate().toString());
                    jsonObject.put("userFullname", attendance.getUserId().getUserFullname());
                    jsonObject.put("attendanceLatitude", attendance.getAttendanceLatitude());
                    jsonObject.put("attendanceLongitude", attendance.getAttendanceLogitude());
                    jsonObject.put("attendanceTime", attendance.getAttendanceTime().toString());
                    jsonObject.put("attendanceType", attendance.getAttendanceType());
                    jsonObject.put("attendanceRemark", attendance.getAttendanceRemark());
                    jsonObject.put("attendanceAttachment", attendance.getAttendanceAttachment());
                    jsonObject.put("attendanceStatusId", attendance.getAttendanceStatusId().getAttendanceStatusId());
                    jsonObject.put("attendanceStatusName", attendance.getAttendanceStatusId().getAttendanceStatusName());
                    jsonObject.put("userId", attendance.getUserId().getUserId());

                    jsonArray.add(jsonObject);
                }

                jsonObject2.put("attendanceList", jsonArray);

                return jsonObject2.toString();
            } else {
                System.out.println("Access denied");
                jsonObject2.put("status", "false");
                jsonObject2.put("description", "Access denied");
                jsonObject2.put("roleName", userRole);
                return jsonObject2.toJSONString();
            }

        } else {
            System.out.println("===== Wrong/Expire Token =====");
            jsonObject2.put("status", "false");
            jsonObject2.put("description", "you don't have authorization to access");

            return jsonObject2.toJSONString();
        }
    }

//    @GetMapping("/getTestAttendance/{id}")
//    @ApiOperation(value = "Get weekly attendance")
//    public String getTestAttendance(@PathVariable String id, @RequestHeader("bearer") String header) {
//
//        System.out.println("===== Request /getTestAttendance/{id} in Dashboard =====");
//        JSONArray jsonArray = new JSONArray();
//        JSONObject jsonObject = new JSONObject();
//
//        int tokenExist = attendanceRepository.findIfExistToken(header);
//        if (tokenExist == 1) {
//            // Get user id
//            Optional<Users> userIdObj = attendanceRepository.getUsersByToken(header);
//            String userId = userIdObj.get().getUserId();
//
//            // get user role
//            String userRole = attendanceRepository.getUsersRole(userId);
//            System.out.println(userRole);
//
//            if (userRole.equals("admin") || userRole.equals("trainer") || userRole.equals("manager passive") || userRole.equals("manager active")) {
//                JSONObject jsonObject2 = new JSONObject();
//
//                try ( Connection connection = DriverManager.getConnection(URL, USERNAME, PASSWORD)) {
//                    Statement statement = connection.createStatement();
//                    ResultSet resultSet = statement.executeQuery(
//                            "select week(a.attendance_date) as weekNo, count(*) as attendanceNumber\n"
//                            + "from attendance a \n"
//                            + "join users u on a.user_id = u.user_id\n"
//                            + "join bootcamp_detail b on b.user_id = u.user_id\n"
//                            + "WHERE YEAR(a.attendance_date) = YEAR(CURDATE()) \n"
//                            + "and a.attendance_type = 'end'\n"
//                            + "and b.bootcamp_id = " + id + "\n"
//                            + "GROUP BY WEEK(attendance_date)"
//                    );
//
//                    ResultSetMetaData metadata = resultSet.getMetaData();
//                    int columnCount = metadata.getColumnCount();
//
//                    System.out.println("Column count : " + columnCount);
//                    System.out.println("Column Name : " + metadata.getColumnName(1));
//                    System.out.println("Column Name : " + metadata.getColumnName(2));
//                    JSONArray jsonArrayWhile = new JSONArray();
//                    JSONArray jsonArrayWhile2 = new JSONArray();
//
//                    while (resultSet.next()) {
//                        JSONObject jsonObjectWhile = new JSONObject();
//
//                        String weekNo = resultSet.getString("weekNo");
//                        String attendanceNumber = resultSet.getString("attendanceNumber");
//
//                        jsonArrayWhile.add(weekNo);
//                        jsonArrayWhile2.add(attendanceNumber);
//                    }
//
//                    jsonObject2.put("weekList", jsonArrayWhile);
//                    jsonObject2.put("weekAtteandance", jsonArrayWhile2);
//
//                    return jsonObject2.toJSONString();
//
//                } catch (SQLException e) {
//                    e.printStackTrace();
//                }
//
//                jsonObject2.put("status", "false");
//                jsonObject2.put("description", "Data not found");
//
//                return jsonObject2.toJSONString();
//            } else {
//                System.out.println("Access denied");
//                jsonObject.put("status", "false");
//                jsonObject.put("description", "Access denied");
//                jsonObject.put("roleName", userRole);
//                return jsonObject.toJSONString();
//            }
//
//        } else {
//            System.out.println("Not Accepted");
//            jsonObject.put("status", "false");
//            jsonObject.put("description", "you don't have authorization to access");
//            return jsonObject.toJSONString();
//        }
//
//    }
}

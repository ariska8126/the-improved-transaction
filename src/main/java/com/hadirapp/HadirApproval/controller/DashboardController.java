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

                    System.out.println("Column count : " + columnCount);
                    System.out.println("Column Name : " + metadata.getColumnName(1));
                    System.out.println("Column Name : " + metadata.getColumnName(2));

                    while (resultSet.next()) {
                        JSONObject jsonObjectWhile = new JSONObject();

                        String weekNo = resultSet.getString("weekNo");
                        String attendanceNumber = resultSet.getString("attendanceNumber");
                        System.out.println(weekNo);
                        System.out.println(attendanceNumber);

                        jsonObjectWhile.put(weekNo, attendanceNumber);
                        jsonArray.add(jsonObjectWhile);
                    }

                    jsonObject2.put("weekAttendanceList", jsonArray);

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

    @GetMapping("/getweeklyleave/{id}")
    @ApiOperation(value = "Get weekly leave")
    public String getWeeklyLeave(@PathVariable String id, @RequestHeader("bearer") String header) {

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
                            "select week(a.attendance_date) as weekNo, count(*) as leaveNumber\n"
                            + "from attendance a \n"
                            + "join users u on a.user_id = u.user_id\n"
                            + "join bootcamp_detail b on b.user_id = u.user_id\n"
                            + "WHERE YEAR(a.attendance_date) = YEAR(CURDATE()) \n"
                            + "and a.attendance_type = 'leave'\n"
                            + "and b.bootcamp_id = " + id + "\n"
                            + "GROUP BY WEEK(attendance_date)"
                    );

                    ResultSetMetaData metadata = resultSet.getMetaData();
                    int columnCount = metadata.getColumnCount();

                    System.out.println("Column count : " + columnCount);
                    System.out.println("Column Name : " + metadata.getColumnName(1));
                    System.out.println("Column Name : " + metadata.getColumnName(2));

                    while (resultSet.next()) {
                        JSONObject jsonObjectWhile = new JSONObject();

                        String weekNo = resultSet.getString("weekNo");
                        String leaveNumber = resultSet.getString("leaveNumber");

                        System.out.println(weekNo);
                        System.out.println(leaveNumber);


                        jsonObjectWhile.put(weekNo, leaveNumber);
                        jsonArray.add(jsonObjectWhile);
                    }

                    jsonObject2.put("weekLeaveList", jsonArray);

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

}

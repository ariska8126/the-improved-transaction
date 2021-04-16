/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.hadirapp.HadirApproval.controller;

import com.hadirapp.HadirApproval.entity.Approval;
import com.hadirapp.HadirApproval.entity.ApprovalStatus;
import com.hadirapp.HadirApproval.entity.Attendance;
import com.hadirapp.HadirApproval.entity.AttendanceStatus;
import com.hadirapp.HadirApproval.entity.Request;
import com.hadirapp.HadirApproval.entity.Users;
import com.hadirapp.HadirApproval.repository.ApprovalRepository;
import com.hadirapp.HadirApproval.repository.AttendanceRepository;
import com.hadirapp.HadirApproval.repository.BootcampDetailRepository;
import com.hadirapp.HadirApproval.repository.RequestRepository;
import com.hadirapp.HadirApproval.repository.UsersRepository;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.List;
import java.util.Map;
import net.minidev.json.JSONArray;
import net.minidev.json.JSONObject;
import net.minidev.json.parser.JSONParser;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RestController;

/**
 *
 * @author herli
 */
@RestController
@RequestMapping("/api/transac/approval")
@Api(tags = "Approval Management")
public class ApprovalController {

    @Autowired
    private ApprovalRepository approvalRepository;

    @Autowired
    private RequestRepository requestRepository;

    @Autowired
    private BootcampDetailRepository bootcampDetailRepository;

    @Autowired
    private AttendanceRepository attendanceRepository;

    @Autowired
    private UsersRepository usersRepository;

    //read all approval by requester id
//    @GetMapping("/getapproval")
    @GetMapping("/getapproval/{id}")
    @ApiOperation(value = "Get List Approval by UserId")
//    public String getApprovalByRequester(@RequestHeader("bearer") String header) {
    public String getApprovalByRequester(@RequestHeader("bearer") String header,
            @PathVariable String id) {

        JSONArray jsonArray = new JSONArray();
        JSONObject jsonObject2 = new JSONObject();
        JSONObject jSONObject = new JSONObject();

        int tokenExist = approvalRepository.findIfExistTokenForApproval(header);

        if (tokenExist == 1) {

            Users users = usersRepository.findUserByToken(header);
            System.out.println("user email: " + users.getUserEmail());
            int roleIda = users.getRoleId().getRoleId();
            System.out.println("roleId: " + roleIda);

            //update using bearer
//            String id = users.getUserId();
//            String requester = users.getUserId();
            if (roleIda == 2 || roleIda == 4 || roleIda == 5) {
                System.out.println("you're authorized to access this operation");

                List<Approval> approval = approvalRepository.findByID(id);
                if (approval == null) {
                    System.out.println("approval status not found");
                }

                JSONArray jSONArray = new JSONArray();
                JSONObject j = new JSONObject();

                for (Approval app : approval) {

                    String employeeId = null;
                    String employeeName = null;

                    String requester = app.getApprovalRequesterId().getUserId();
                    Users user = usersRepository.findUserById(requester);
                    int roleId = user.getRoleId().getRoleId();
                    System.out.println("role: " + roleId);
                    if (roleId == 5) {
                        employeeId = app.getApprovalRequesterId().getUserId();
                        System.out.println("employee id: " + employeeId);
                        employeeName = app.getApprovalRequesterId().getUserFullname();
                        System.out.println("employee name: " + employeeName);

                    } else if (roleId == 4) {
                        System.out.println("requester adalah trainer");
                        String trainerId = app.getApprovalRequesterId().getUserId();
                        System.out.println("trainer id " + trainerId);
                        String requestId = app.getRequestId().getRequestId();
                        System.out.println("request id: " + requestId);
                        Approval app2 = approvalRepository.findByRequestAndTrainerId(requestId, trainerId);
                        employeeId = app2.getApprovalRequesterId().getUserId();
                        System.out.println("employee id: " + employeeId);
                        employeeName = app2.getApprovalRequesterId().getUserFullname();
                        System.out.println("employee name: " + employeeName);
                    }
                    jSONObject.put("employeeId", employeeId);
                    jSONObject.put("employeeName", employeeName);

                    SimpleDateFormat formater = new SimpleDateFormat("yyyy-MM-dd");
                    String reqDate = formater.format(app.getRequestId().getRequestDate());
                    System.out.println("req date: " + reqDate);
                    jSONObject.put("requestId", app.getRequestId().getRequestId());
                    // modify date format for request date
                    jSONObject.put("requestDate", reqDate);
                    jSONObject.put("requestDateStart", app.getRequestId().getRequestDateStart().toString());
                    jSONObject.put("requestDateEnd", app.getRequestId().getRequestDateEnd().toString());

                    jSONObject.put("approvalId", app.getApprovalId());
                    jSONObject.put("approvalDate", app.getApprovalDate().toString());
                    jSONObject.put("approvalDateUpdate", app.getApprovalDateUpdate().toString());
                    jSONObject.put("approvalRemark", app.getApprovalRemark());

                    jSONObject.put("approvalStatusId", app.getApprovalStatusId().getApprovalStatusId().toString());
                    jSONObject.put("approvalStatusName", app.getApprovalStatusId().getApprovalStatusName());
                    jSONObject.put("approvalRequesterId", app.getApprovalRequesterId().getUserId());
                    jSONObject.put("approvalRequesterName", app.getApprovalRequesterId().getUserFullname());
                    jSONObject.put("approvalApproverId", app.getApprovalApproverId().getUserId());
                    jSONObject.put("approvalApproverName", app.getApprovalApproverId().getUserFullname());

                    jSONArray.add(jSONObject);
                }
                j.put("approvalList", jSONArray);

                return j.toString();

            } else {
                System.out.println("access denied");
                jSONObject.put("status", "false");
                jSONObject.put("description", "you don't have authorization to access");

                return jSONObject.toJSONString();
            }

        } else {
            System.out.println("===== Wrong/Expire Token =====");
            jsonObject2.put("status", "false");
            jsonObject2.put("description", "you don't have authorization to access");

            return jsonObject2.toJSONString();
        }

    }

    //read detail approval
    @GetMapping("/getattendancelistbyrequestid/{id}")
    @ApiOperation(value = "List Attendance by Request ID")
    public String getDetailApprovalByRequestId(@RequestHeader("bearer") String header,
            @PathVariable String id) {

        JSONArray jsonArray = new JSONArray();
        JSONObject jsonObject2 = new JSONObject();
        JSONObject jSONObject1 = new JSONObject();

        int tokenExist = approvalRepository.findIfExistTokenForApproval(header);
        if (tokenExist == 1) {

            Users users = usersRepository.findUserByToken(header);
            System.out.println("user email: " + users.getUserEmail());
            int roleIda = users.getRoleId().getRoleId();
            System.out.println("roleId: " + roleIda);
            if (roleIda == 2 || roleIda == 4 || roleIda == 5) {
                System.out.println("you're authorized to access this operation");

                List<Attendance> attendances = attendanceRepository.findDetailApprovalByRequestIDnew(id);
                System.out.println("attendances: " + attendances);
                if (attendances == null) {
                    System.out.println("approval status not found");
                }

                JSONArray jSONArray = new JSONArray();
                JSONObject j = new JSONObject();

                for (Attendance app : attendances) {
                    JSONObject jSONObject = new JSONObject();
                    jSONObject.put("attendanceId", app.getAttendanceId());
                    jSONObject.put("attendanceDate", app.getAttendanceDate().toString());
                    jSONObject.put("attendanceTime", app.getAttendanceTime().toString());
                    jSONObject.put("attendanceRemark", app.getAttendanceRemark());
                    jSONObject.put("attendanceAttachment", app.getAttendanceAttachment());
                    jSONObject.put("attendanceType", app.getAttendanceType());
                    jSONObject.put("attendanceLogitude", app.getAttendanceLogitude());
                    jSONObject.put("attendanceLatitude", app.getAttendanceLatitude());
                    jSONObject.put("userId", app.getUserId().getUserId());
                    jSONObject.put("userName", app.getUserId().getUserFullname());
                    jSONObject.put("attendanceStatusId", app.getAttendanceStatusId().getAttendanceStatusId());
                    jSONObject.put("attendanceStatusName", app.getAttendanceStatusId().getAttendanceStatusName());
                    jSONArray.add(jSONObject);
                }
                j.put("attendanceList", jSONArray);

                return j.toString();

            } else {
                System.out.println("access denied");
                jSONObject1.put("status", "false");
                jSONObject1.put("description", "you don't have authorization to access");

                return jSONObject1.toJSONString();
            }

        } else {
            System.out.println("===== Wrong/Expire Token =====");
            jsonObject2.put("status", "false");
            jsonObject2.put("description", "you don't have authorization to access");

            return jsonObject2.toJSONString();
        }
    }

    //read detail approval
    @GetMapping("/getapprovallistbyrequestid/{id}")
    @ApiOperation(value = "List Approval by Request ID")
    public String getApprovalListByRequestId(@RequestHeader("bearer") String header,
            @PathVariable String id) {

        System.out.println("request id: " + id);
//        JSONArray jsonArray = new JSONArray();
        JSONObject jsonObject2 = new JSONObject();
        JSONObject jSONObject1 = new JSONObject();

        int tokenExist = approvalRepository.findIfExistTokenForApproval(header);
        if (tokenExist == 1) {

            Users users = usersRepository.findUserByToken(header);
            System.out.println("user email: " + users.getUserEmail());
            int roleIda = users.getRoleId().getRoleId();
            System.out.println("roleId: " + roleIda);
            if (roleIda == 2 || roleIda == 4 || roleIda == 5) {
                System.out.println("you're authorized to access this operation");

                List<Approval> attendances = approvalRepository.findListApprovalByRequestID(id);
                System.out.println("attendances: " + attendances);
                if (attendances == null) {
                    System.out.println("approval status not found");
                }

                JSONArray jSONArray = new JSONArray();
                JSONObject j = new JSONObject();

                for (Approval app : attendances) {
                    JSONObject jSONObject = new JSONObject();

                    SimpleDateFormat formater = new SimpleDateFormat("yyyy-MM-dd");
                    String reqDate = formater.format(app.getRequestId().getRequestDate());
                    System.out.println("req date: " + reqDate);
                    jSONObject.put("requestId", app.getRequestId().getRequestId());
                    // modify date format for request date
                    jSONObject.put("requestDate", reqDate);
                    jSONObject.put("requestDateStart", app.getRequestId().getRequestDateStart().toString());
                    jSONObject.put("requestDateEnd", app.getRequestId().getRequestDateEnd().toString());

                    jSONObject.put("approvalId", app.getApprovalId());
                    jSONObject.put("approvalDate", app.getApprovalDate().toString());
                    jSONObject.put("approvalDateUpdate", app.getApprovalDateUpdate().toString());
                    jSONObject.put("approvalRemark", app.getApprovalRemark());

                    jSONObject.put("approvalStatusId", app.getApprovalStatusId().getApprovalStatusId().toString());
                    jSONObject.put("approvalStatusName", app.getApprovalStatusId().getApprovalStatusName());
                    jSONObject.put("approvalRequesterId", app.getApprovalRequesterId().getUserId());
                    jSONObject.put("approvalRequesterName", app.getApprovalRequesterId().getUserFullname());
                    jSONObject.put("approvalApproverId", app.getApprovalApproverId().getUserId());
                    jSONObject.put("approvalApproverName", app.getApprovalApproverId().getUserFullname());

                    jSONArray.add(jSONObject);
                }
                j.put("approvalList", jSONArray);

                return j.toString();

            } else {
                System.out.println("access denied");
                jSONObject1.put("status", "false");
                jSONObject1.put("description", "you don't have authorization to access");

                return jSONObject1.toJSONString();
            }
        } else {
            System.out.println("===== Wrong/Expire Token =====");
            jsonObject2.put("status", "false");
            jsonObject2.put("description", "you don't have authorization to access");

            return jsonObject2.toJSONString();
        }
    }

    //read detail approval
    @GetMapping("/getrequestbyrequestid/{id}")
    @ApiOperation(value = "List Request by Request ID")
    public String getRequestByRequestId(@RequestHeader("bearer") String header,
            @PathVariable String id) {

        System.out.println("request id: " + id);
        JSONArray jsonArray = new JSONArray();
        JSONObject jsonObject2 = new JSONObject();
        JSONObject jSONObject1 = new JSONObject();

        int tokenExist = approvalRepository.findIfExistTokenForApproval(header);
        if (tokenExist == 1) {

            Users users = usersRepository.findUserByToken(header);
            System.out.println("user email: " + users.getUserEmail());
            int roleIda = users.getRoleId().getRoleId();
            System.out.println("roleId: " + roleIda);
            if (roleIda == 2 || roleIda == 4 || roleIda == 5) {
                System.out.println("you're authorized to access this operation");

                int ifexistRequserId = requestRepository.findIfExistRequestByRequesId(id);
                System.out.println("exists: " + ifexistRequserId);
                if (ifexistRequserId == 0) {
                    System.out.println("request status not found");
                    jSONObject1.put("status", "false");
                    jSONObject1.put("description", "request id not found");
                    return jSONObject1.toJSONString();
                }

                Request request = requestRepository.findRequestByRequesId(id);
                System.out.println("request id: " + request.getRequestId());

                JSONArray jSONArray = new JSONArray();
                JSONObject j = new JSONObject();
//                JSONObject j2 = new JSONObject();

                SimpleDateFormat formater = new SimpleDateFormat("yyyy-MM-dd");
                String reqDate = formater.format(request.getRequestDate());
                System.out.println("req date: " + reqDate);
                JSONObject jSONObject = new JSONObject();
                jSONObject.put("requestId", request.getRequestId());
                // modify date format for request date
                jSONObject.put("requestDate", reqDate);

                jSONObject.put("requestDateStart", request.getRequestDateStart().toString());
                jSONObject.put("requestDateEnd", request.getRequestDateEnd().toString());

                j.put("request", jSONObject);
                j.put("status", "true");

                return j.toString();

            } else {
                System.out.println("access denied");
                jSONObject1.put("status", "false");
                jSONObject1.put("description", "you don't have authorization to access");

                return jSONObject1.toJSONString();
            }

        } else {
            System.out.println("===== Wrong/Expire Token =====");
            jsonObject2.put("status", "false");
            jsonObject2.put("description", "you don't have authorization to access");

            return jsonObject2.toJSONString();
        }
    }

//    @RequestMapping(value = "/checkattendance", method = RequestMethod.GET)
    @RequestMapping(value = "/checkattendance/{userId}", method = RequestMethod.GET)
    @ApiOperation(value = "Cek Status Approval sebelum Create New")
//    public String cekStatusApproval(@RequestHeader("bearer") String header) {
    public String cekStatusApproval(@RequestHeader("bearer") String header,
            @PathVariable String userId) {

        JSONObject jSONObject = new JSONObject();

        JSONArray jsonArray = new JSONArray();
        JSONObject jsonObject2 = new JSONObject();

        int tokenExist = approvalRepository.findIfExistTokenForApproval(header);
        if (tokenExist == 1) {

            Users users = usersRepository.findUserByToken(header);
            System.out.println("user email: " + users.getUserEmail());
            int roleIda = users.getRoleId().getRoleId();
            System.out.println("roleId: " + roleIda);
            if (roleIda == 2 || roleIda == 4 || roleIda == 5) {
                System.out.println("you're authorized to access this operation");

                //update user id from bearer
//                String userId = users.getUserId();
                int cekIfExistRequest = approvalRepository.cekIfExistApprovalThisMonth(userId);
                if (cekIfExistRequest == 1) {
                    System.out.println("request untuk bulan lalu sudah dibuat");

                    String approvalId = approvalRepository.findThisMonthApprovalIdByRequesterId(userId);

                    Approval approval = approvalRepository.findByApprovalId(approvalId);

                    String statusApprovalStr = approval.getApprovalStatusId().getApprovalStatusName();
                    System.out.println("approval status: " + statusApprovalStr);

                    int statusApprovalId = approval.getApprovalStatusId().getApprovalStatusId();
                    System.out.println("approval status: " + statusApprovalId + " " + approval.getApprovalStatusId().getApprovalStatusName());

                    if (statusApprovalId == 1 || statusApprovalId == 3 || statusApprovalId == 6) {
                        System.out.println("cannot create new request");
                        jSONObject.put("status", "false");
                        jSONObject.put("approvalDate", approval.getApprovalDate().toString());
                        jSONObject.put("description", "unsuccessfull create new approval request");
                        jSONObject.put("startDate", approval.getApprovalDateStart().toString());
                        jSONObject.put("endDate", approval.getApprovalDateEnd().toString());
                        return jSONObject.toString();
                    }
                }

                SimpleDateFormat formater = new SimpleDateFormat("yyyy-MM-dd");
                Calendar cal = Calendar.getInstance();
                cal.add(Calendar.MONTH, -1);
                Date lastMonth = cal.getTime();
                System.out.println("last month: " + lastMonth);

                Calendar lastDateCal = Calendar.getInstance();
                lastDateCal.add(Calendar.MONTH, -1);

                Calendar firstDateCal = Calendar.getInstance();
                firstDateCal.add(Calendar.MONTH, -1);

                int lastDateInt = cal.getActualMaximum(Calendar.DATE);
                int firstDateInt = cal.getActualMinimum(Calendar.DATE);

                System.out.println("first date: " + firstDateInt);
                System.out.println("last date: " + lastDateInt);

                lastDateCal.set(Calendar.DAY_OF_MONTH, lastDateInt);
                lastDateCal.set(Calendar.HOUR_OF_DAY, 23);
                lastDateCal.set(Calendar.MINUTE, 59);
                lastDateCal.set(Calendar.SECOND, 59);
                lastDateCal.set(Calendar.MILLISECOND, 0);

                firstDateCal.set(Calendar.DAY_OF_MONTH, firstDateInt);
                firstDateCal.set(Calendar.HOUR_OF_DAY, 0);
                firstDateCal.set(Calendar.MINUTE, 0);
                firstDateCal.set(Calendar.SECOND, 0);
                firstDateCal.set(Calendar.MILLISECOND, 1);

                Date lastDate = lastDateCal.getTime();
                Date firstDate = firstDateCal.getTime();

                System.out.println("fisrt date: " + firstDate);
                System.out.println("last date: " + lastDate);

                String startDate = formater.format(firstDate);
                String endDate = formater.format(lastDate);

                jSONObject.put("status", "true");
                jSONObject.put("approvalDate", "");
                jSONObject.put("description", "you can create new approval request");
                jSONObject.put("startDate", startDate);
                jSONObject.put("endDate", endDate);
                return jSONObject.toString();

            } else {
                System.out.println("access denied");
                jSONObject.put("status", "false");
                jSONObject.put("description", "you don't have authorization to access");

                return jSONObject.toJSONString();
            }

        } else {
            System.out.println("===== Wrong/Expire Token =====");
            jsonObject2.put("status", "false");
            jsonObject2.put("description", "you don't have authorization to access");

            return jsonObject2.toJSONString();
        }
    }

//    @RequestMapping(value = "/getAttendancebyLastMonth", method = RequestMethod.GET)
    @RequestMapping(value = "/getAttendancebyLastMonth/{userId}", method = RequestMethod.GET)
    @ApiOperation(value = "Cek Attendance sebelum Create New")
//    public String cekAttendance(@RequestHeader("bearer") String header) {
    public String cekAttendance(@RequestHeader("bearer") String header,
            @PathVariable String userId) {

        JSONArray jsonArray = new JSONArray();
        JSONObject jsonObject2 = new JSONObject();
        JSONObject jsonObject1 = new JSONObject();

        int tokenExist = approvalRepository.findIfExistTokenForApproval(header);
        if (tokenExist == 1) {

            Users users = usersRepository.findUserByToken(header);
            System.out.println("user email: " + users.getUserEmail());
            int roleIda = users.getRoleId().getRoleId();
            System.out.println("roleId: " + roleIda);
            if (roleIda == 2 || roleIda == 4 || roleIda == 5) {
                System.out.println("you're authorized to access this operation");

//                String userId = users.getUserId();
                SimpleDateFormat formater = new SimpleDateFormat("yyyy-MM-dd");
                Calendar cal = Calendar.getInstance();
                cal.add(Calendar.MONTH, -1);
                Date lastMonth = cal.getTime();
                System.out.println("last month: " + lastMonth);

                Calendar lastDateCal = Calendar.getInstance();
                lastDateCal.add(Calendar.MONTH, -1);

                Calendar firstDateCal = Calendar.getInstance();
                firstDateCal.add(Calendar.MONTH, -1);

                int lastDateInt = cal.getActualMaximum(Calendar.DATE);
                int firstDateInt = cal.getActualMinimum(Calendar.DATE);

                System.out.println("first date: " + firstDateInt);
                System.out.println("last date: " + lastDateInt);

                lastDateCal.set(Calendar.DAY_OF_MONTH, lastDateInt);
                lastDateCal.set(Calendar.HOUR_OF_DAY, 23);
                lastDateCal.set(Calendar.MINUTE, 59);
                lastDateCal.set(Calendar.SECOND, 59);
                lastDateCal.set(Calendar.MILLISECOND, 0);

                firstDateCal.set(Calendar.DAY_OF_MONTH, firstDateInt);
                firstDateCal.set(Calendar.HOUR_OF_DAY, 0);
                firstDateCal.set(Calendar.MINUTE, 0);
                firstDateCal.set(Calendar.SECOND, 0);
                firstDateCal.set(Calendar.MILLISECOND, 1);

                Date lastDate = lastDateCal.getTime();
                Date firstDate = firstDateCal.getTime();

                System.out.println("fisrt date: " + firstDate);
                System.out.println("last date: " + lastDate);

                String startDate = formater.format(firstDate);
                String endDate = formater.format(lastDate);
                Iterable<Attendance> attendances = attendanceRepository.findLastMonthAttendanceByUserId(userId, startDate, endDate);
                System.out.println("attendances: " + attendances);
                if (attendances == null) {
                    System.out.println("approval status not found");
                }

                JSONArray jSONArray = new JSONArray();
                JSONObject j = new JSONObject();

                for (Attendance app : attendances) {
                    JSONObject jSONObject = new JSONObject();
                    jSONObject.put("attendanceId", app.getAttendanceId());
                    jSONObject.put("attendanceDate", app.getAttendanceDate().toString());
                    jSONObject.put("attendanceTime", app.getAttendanceTime().toString());
                    jSONObject.put("attendanceRemark", app.getAttendanceRemark());
                    jSONObject.put("attendanceAttachment", app.getAttendanceAttachment());
                    jSONObject.put("attendanceType", app.getAttendanceType());
                    jSONObject.put("attendanceLogitude", app.getAttendanceLogitude());
                    jSONObject.put("attendanceLatitude", app.getAttendanceLatitude());
                    jSONObject.put("userId", app.getUserId().getUserId());
                    jSONObject.put("userName", app.getUserId().getUserFullname());
                    jSONObject.put("attendanceStatusId", app.getAttendanceStatusId().getAttendanceStatusId());
                    jSONObject.put("attendanceStatusName", app.getAttendanceStatusId().getAttendanceStatusName());
                    jSONArray.add(jSONObject);
                }
                j.put("attendanceList", jSONArray);

                return j.toString();

            } else {
                System.out.println("access denied");
                jsonObject1.put("status", "false");
                jsonObject1.put("description", "you don't have authorization to access");

                return jsonObject1.toJSONString();
            }

        } else {
            System.out.println("===== Wrong/Expire Token =====");
            jsonObject2.put("status", "false");
            jsonObject2.put("description", "you don't have authorization to access");

            return jsonObject2.toJSONString();
        }
    }

    //save new approval request
    @RequestMapping(value = "/insertnew", method = RequestMethod.POST)
    @ApiOperation(value = "Create new Request Approval last month")
    public String createNewRequestApprovalLastMonth(@RequestHeader("bearer") String header,
            @RequestBody Map<String, ?> input) {
//    public String createNewRequestApprovalLastMonth(@PathVariable("id") String userId) {

        JSONArray jsonArray = new JSONArray();
        JSONObject jsonObject2 = new JSONObject();
        JSONObject jSONObject = new JSONObject();

        int tokenExist = approvalRepository.findIfExistTokenForApproval(header);
        if (tokenExist == 1) {

            Users users = usersRepository.findUserByToken(header);
            System.out.println("user email: " + users.getUserEmail());
            int roleIda = users.getRoleId().getRoleId();
            System.out.println("roleId: " + roleIda);
            if (roleIda == 4 || roleIda == 5) {
                System.out.println("you're authorized to access this operation");

                String userId = (String) input.get("userId");

                SimpleDateFormat formater = new SimpleDateFormat("yyyyMMddHHmm");

                System.out.println("user id: " + userId);

                Users cekUser = usersRepository.findUserById(userId);
                int roleUser = cekUser.getRoleId().getRoleId();
                System.out.println("user roleId: " + roleUser + " " + cekUser.getRoleId().getRoleName());

                if (roleUser == 5) {

//        cek if exist request approval for this month
                    int cekIfExistRequestApp = approvalRepository.cekIfExistApprovalThisMonth(userId);
                    System.out.println("exist: " + cekIfExistRequestApp);

                    if (cekIfExistRequestApp == 1) {
                        System.out.println("request untuk bulan lalu sudah dibuat");

//                Approval approval = approvalRepository.findLastMonthApprovalByRequesterId(userId);
                        String approvalId = approvalRepository.findThisMonthApprovalIdByRequesterId(userId);

                        Approval approval = approvalRepository.findByApprovalId(approvalId);

                        String statusApprovalStr = approval.getApprovalStatusId().getApprovalStatusName();
                        System.out.println("approval status: " + statusApprovalStr);

                        int statusApprovalId = approval.getApprovalStatusId().getApprovalStatusId();
                        System.out.println("approval status: " + statusApprovalId + " " + approval.getApprovalStatusId().getApprovalStatusName());

                        if (statusApprovalId == 1 || statusApprovalId == 3 || statusApprovalId == 6) {
                            System.out.println("cannot create new request");
                            jSONObject.put("status", "false");
                            jSONObject.put("description", "unsuccessfull create new approval request");
                            return jSONObject.toString();
                        }

                    }
                }

                System.out.println("belum create request app");
//        //save new request
//        //generate requestId
                Date sekarang = new Date();

                //now is for last month
                Calendar cal = Calendar.getInstance();
                cal.add(Calendar.MONTH, -1);
                Date lastMonth = cal.getTime();

                System.out.println("now: " + lastMonth);
                String strDate = formater.format(lastMonth);
                System.out.println("strDate: " + strDate);
                String requestId = userId + strDate;
                System.out.println("new request id: " + requestId);
                int cekRequestIdExist = requestRepository.cekIfExistRequestId(requestId);
                System.out.println("exist: " + cekRequestIdExist);
                if (cekRequestIdExist == 1) {
                    do {
                        cal.add(Calendar.MONTH, -1);
                        lastMonth = cal.getTime();
                        strDate = formater.format(lastMonth);
                        requestId = userId + strDate;
                        cekRequestIdExist = requestRepository.cekIfExistRequestId(requestId);
                    } while (cekRequestIdExist == 1);
                }
                System.out.println("request id bisa di simpan: " + requestId);

                //get last date current month
                Calendar lastDateCal = Calendar.getInstance();
                lastDateCal.add(Calendar.MONTH, -1);
                Calendar firstDateCal = Calendar.getInstance();
                firstDateCal.add(Calendar.MONTH, -1);

                int lastDateInt = cal.getActualMaximum(Calendar.DATE);
                int firstDateInt = cal.getActualMinimum(Calendar.DATE);

                System.out.println("first date: " + firstDateInt);
                System.out.println("last date: " + lastDateInt);

                lastDateCal.set(Calendar.DAY_OF_MONTH, lastDateInt);
                lastDateCal.set(Calendar.HOUR_OF_DAY, 23);
                lastDateCal.set(Calendar.MINUTE, 59);
                lastDateCal.set(Calendar.SECOND, 59);
                lastDateCal.set(Calendar.MILLISECOND, 0);

                firstDateCal.set(Calendar.DAY_OF_MONTH, firstDateInt);
                firstDateCal.set(Calendar.HOUR_OF_DAY, 0);
                firstDateCal.set(Calendar.MINUTE, 0);
                firstDateCal.set(Calendar.SECOND, 0);
                firstDateCal.set(Calendar.MILLISECOND, 1);

                Date lastDate = lastDateCal.getTime();
                Date firstDate = firstDateCal.getTime();

                System.out.println("fisrt date: " + firstDate);
                System.out.println("last date: " + lastDate);
//
                Request request = new Request(requestId, sekarang, firstDate, lastDate);
                requestRepository.save(request); //enable after modify condition
                System.out.println("request has been saved");
//
                //id approval
                String approvalId = userId + strDate + userId;
                System.out.println("approval id: " + approvalId);
                int cekIfExistApprovalId = approvalRepository.cekIfExistApprovalId(approvalId);
                if (cekIfExistApprovalId == 1) {
                    do {
                        sekarang = new Date();
                        strDate = formater.format(sekarang);
                        approvalId = userId + strDate + userId;
                        cekIfExistApprovalId = approvalRepository.cekIfExistApprovalId(approvalId);
                    } while (cekIfExistApprovalId == 1);
                }

                System.out.println("approval id bisa di pakai " + approvalId);

                final int APPROVAL_STATUS = 1;
                String remark = "";

                String employeeBootcampId = bootcampDetailRepository.getEmployeeBootcampId(userId);
                System.out.println("user id: " + userId);
                System.out.println("employee bootcamp id: " + employeeBootcampId);

                String approver = bootcampDetailRepository.getTrainerByBootcampId(employeeBootcampId);
                System.out.println("trainer id: " + approver);

                Approval approval = new Approval(approvalId, sekarang, sekarang, remark, firstDate, lastDate,
                        new Request(requestId), new ApprovalStatus(APPROVAL_STATUS), new Users(userId),
                        new Users(approver));
                approvalRepository.save(approval);
                System.out.println("request approval has been saved");

                jSONObject.put("status", "true");
                jSONObject.put("description", "successfull create new approval request");
                return jSONObject.toString();

            } else {
                System.out.println("access denied");
                jSONObject.put("status", "false");
                jSONObject.put("description", "you don't have authorization to access");

                return jSONObject.toJSONString();
            }

        } else {
            System.out.println("===== Wrong/Expire Token =====");
            jsonObject2.put("status", "false");
            jsonObject2.put("description", "you don't have authorization to access");

            return jsonObject2.toJSONString();
        }
//        return "test";
    }

//update approval
    @RequestMapping(value = "/update/{id}", method = RequestMethod.PUT)
    @ApiOperation(value = "Update Approval by Approval Id}")
    public String updateApproval(@RequestHeader("bearer") String header,
            @RequestBody Map<String, ?> input, @PathVariable String id) {

        JSONArray jsonArray = new JSONArray();
        JSONObject jsonObject2 = new JSONObject();
        JSONObject jSONObject = new JSONObject();

        int tokenExist = approvalRepository.findIfExistTokenForApproval(header);
        if (tokenExist == 1) {

            Users users = usersRepository.findUserByToken(header);
            System.out.println("user email: " + users.getUserEmail());
            int roleIda = users.getRoleId().getRoleId();
            System.out.println("roleId: " + roleIda);
            if (roleIda == 2 || roleIda == 4) {
                System.out.println("you're authorized to access this operation");

                JSONObject jsonObject = new JSONObject();
                System.out.println("id: " + id);
                Approval app = approvalRepository.findByApprovalId(id);
                String requestId = app.getRequestId().getRequestId();
                System.out.println("request id: " + requestId);

                Date now = new Date();

                if (app == null) {
                    jsonObject.put("status", "false");
                    jsonObject.put("description", "update unsuccessfully, ID Not Found");

                    return jsonObject.toString();
                }
//        String approvalStatusIdStr = null;
                int approvalStatusId = 0;

                String approvalRemark = (String) input.get("approvalRemark");
                String approvalStatus = (String) input.get("approvalStatusId");
                String approvalUserId = (String) input.get("userId");

                System.out.println("remark: " + approvalRemark);
                System.out.println("status: " + approvalStatus);
                System.out.println("userId: " + approvalUserId);

                Users user = usersRepository.findUserById(approvalUserId);
                int userRole = user.getRoleId().getRoleId();
                System.out.println("role user: " + userRole);

                if ("reject".equals(approvalStatus) && userRole == 4) {
                    approvalStatusId = 2;
                    System.out.println("reject by trainer");

                } else if ("reject".equals(approvalStatus) && userRole == 2) {
                    approvalStatusId = 5;
                    System.out.println("reject by manager");

                } else if ("approve".equals(approvalStatus) && userRole == 2) {
                    approvalStatusId = 6;
                    System.out.println("approve by manager");

                } else if ("approve".equals(approvalStatus) && userRole == 4) {
                    approvalStatusId = 3;
                    System.out.println("approve by trainer");

                }

                System.out.println("approval status id: " + approvalStatusId);

                app.setApprovalRemark(approvalRemark);
                app.setApprovalStatusId(new ApprovalStatus(approvalStatusId));
                System.out.println("approval status id " + approvalStatusId);
                app.setApprovalDateUpdate(now);
                app.setApprovalId(id);
                approvalRepository.save(app);
                System.out.println("save update on request trainer");

                String message = null;
                switch (approvalStatusId) {

                    //email reject to employee
                    case 2:
                        message = "Reject by Trainer";
                        break;

                    //create new request app to manager
                    //mail to manager
                    //set approvalStatus = 4
                    case 3:
                        message = "Approve by Trainer";
                        createRequestApprovalForEmployee(app);
                        break;

                    //mail to employee    
                    case 5:
                        message = "Reject by Manager";
                        System.out.println("request id: " + requestId);

                        Approval app3 = approvalRepository.findByRequestAndStatus(requestId);
                        System.out.println("approval id: " + app3.getApprovalId());

                        app3.setApprovalRemark(approvalRemark);
                        System.out.println("approval remark: " + app3.getApprovalRemark());

                        app3.setApprovalStatusId(new ApprovalStatus(approvalStatusId));
                        System.out.println("approval status: " + approvalStatusId);

                        app3.setApprovalDateUpdate(now);
                        System.out.println("date update: " + now);

                        approvalRepository.save(app3);
                        System.out.println("save on request employee");

                        break;
                    //mail to employee
                    case 6:
                        message = "Approve by Manager";
                        //update by manager
//                String requestId = app.getRequestId().getRequestId();

                        break;
                    default:
                        break;
                }

                jsonObject.put("status", "true");
                jsonObject.put("description", "update successfully: " + message);

                return jsonObject.toString();

            } else {
                System.out.println("access denied");
                jSONObject.put("status", "false");
                jSONObject.put("description", "you don't have authorization to access");

                return jSONObject.toJSONString();
            }

        } else {
            System.out.println("===== Wrong/Expire Token =====");
            jsonObject2.put("status", "false");
            jsonObject2.put("description", "you don't have authorization to access");

            return jsonObject2.toJSONString();
        }
    }

    @PutMapping("/updateAttendance/{id}")
    @ApiOperation(value = "Update Attendance by attendance ID")
    public String updateDetailApprovalByAttendanceId(@RequestHeader("bearer") String header,
            //            @PathVariable String id, @RequestBody Attendance attendance) {
            @PathVariable String id, @RequestBody Map<String, ?> input) throws ParseException {

        System.out.println("attendance id: " + id);

        JSONArray jsonArray = new JSONArray();
        JSONObject jsonObject2 = new JSONObject();
        JSONObject jSONObject = new JSONObject();

        int tokenExist = approvalRepository.findIfExistTokenForApproval(header);
        if (tokenExist == 1) {

            Users users = usersRepository.findUserByToken(header);
            System.out.println("user email: " + users.getUserEmail());
            int roleIda = users.getRoleId().getRoleId();
            System.out.println("roleId: " + roleIda);
            if (roleIda == 5) {
                System.out.println("you're authorized to access this operation");

                String attendance_time_s = (String) input.get("attendance_time");
                SimpleDateFormat formattertime = new SimpleDateFormat("HH:mm:ss");
                Date attendance_time = formattertime.parse(attendance_time_s);

                String attendance_type = (String) input.get("attendance_type");
                String attendance_remark = (String) input.get("attendance_remark");
                String attendance_attachment = (String) input.get("attendance_attachment");
                String attendance_status_id = (String) input.get("attendance_status_id");
                Date updateDate = new Date();
                System.out.println("update date: " + updateDate);

                Attendance attendance = attendanceRepository.findByAttendanceId(id);

                attendance.setAttendanceTime(attendance_time);
                attendance.setAttendanceAttachment(attendance_attachment);
                attendance.setAttendanceRemark(attendance_remark);
                attendance.setAttendanceType(attendance_type);
                attendance.setAttendanceStatusId(new AttendanceStatus(Integer.parseInt(attendance_status_id)));
                attendance.setAttendanceDateUpdate(updateDate);

                attendanceRepository.save(attendance);

                System.out.println("save update success");
                jSONObject.put("status", "true");
                jSONObject.put("description", "update successfully");

                return jSONObject.toJSONString();
            } else {
                System.out.println("access denied");
                jSONObject.put("status", "false");
                jSONObject.put("description", "you don't have authorization to access");

                return jSONObject.toJSONString();
            }

        } else {
            System.out.println("===== Wrong/Expire Token =====");
            jsonObject2.put("status", "false");
            jsonObject2.put("description", "you don't have authorization to access");

            return jsonObject2.toJSONString();
        }
//        return "test";
    }

    @PostMapping(path = "/updateattendancenew/{startid}/{endid}", consumes = MediaType.APPLICATION_JSON_VALUE)
    @ApiOperation(value = "Update Attendance by attendance ID - New")
    public String updateByAttendanceId(@RequestHeader("bearer") String header,
            //    public String updateByAttendanceId(
            @PathVariable String startid,
            @PathVariable String endid,
            @RequestBody Map<String, Object> input) throws net.minidev.json.parser.ParseException, ParseException {

        JSONObject jsonObject2 = new JSONObject();
        JSONObject jSONObject = new JSONObject();

        int tokenExist = approvalRepository.findIfExistTokenForApproval(header);
        if (tokenExist == 1) {

            Users users = usersRepository.findUserByToken(header);
            System.out.println("user email: " + users.getUserEmail());
            int roleIda = users.getRoleId().getRoleId();
            String userId = users.getUserId();
            System.out.println("userId: "+userId);
            
            Attendance startAttendance = attendanceRepository.findByAttendanceId(startid);
            Attendance endAttendance = attendanceRepository.findByAttendanceId(endid);
            
            String startUser = startAttendance.getUserId().getUserId();
            System.out.println("start user: "+startUser);
            String endUser = endAttendance.getUserId().getUserId();
            System.out.println("end user: "+endUser);
            
            System.out.println("roleId: " + roleIda);
            if ((userId == null ? startUser == null : userId.equals(startUser)) && (userId == null ? endUser == null : userId.equals(endUser))) {
                System.out.println("you're authorized to access this operation");

                System.out.println("startid: " + startid);
                System.out.println("endid: " + endid);

                SimpleDateFormat formatter = new SimpleDateFormat("yyyy-MM-dd");
                SimpleDateFormat timeFormatter = new SimpleDateFormat("HH:mm:ss");

                List<Map<Object, Object>> startidValue = (List<Map<Object, Object>>) input.get(startid);
                List<Map<Object, Object>> endidValue = (List<Map<Object, Object>>) input.get(endid);

                ArrayList<String> listStr = new ArrayList<>();
                for (Map entry : startidValue) {
                    listStr.add(entry.get("attendanceStatus").toString());
                    listStr.add(entry.get("startAttendanceDate").toString());
                    listStr.add(entry.get("startAttendanceTime").toString());
                }

                String startAttendanceStatus = listStr.get(0);
                String startAttendanceDate = listStr.get(1);
                String startAttendanceTime = listStr.get(2);

                System.out.println(startAttendanceStatus);
                System.out.println(startAttendanceDate);
                System.out.println(startAttendanceTime);

                

                Date startDate = formatter.parse(startAttendanceDate);
                System.out.println("start date: " + startDate);

                Date startTime = timeFormatter.parse(startAttendanceTime);
                System.out.println("start time: " + startTime);
                Date updateDate = new Date();

                startAttendance.setAttendanceDate(startDate);
                startAttendance.setAttendanceTime(startTime);
                startAttendance.setAttendanceType(startAttendanceStatus);
                startAttendance.setAttendanceDateUpdate(updateDate);
                attendanceRepository.save(startAttendance);
                System.out.println("finish start date");

                ArrayList<String> listEnd = new ArrayList<>();
                for (Map entry : endidValue) {
                    listEnd.add(entry.get("attendanceStatus").toString());
                    listEnd.add(entry.get("startAttendanceDate").toString());
                    listEnd.add(entry.get("startAttendanceTime").toString());
                }

                String endAttendanceStatus = listEnd.get(0);
                String endAttendanceDate = listEnd.get(1);
                String endAttendanceTime = listEnd.get(2);

                System.out.println(endAttendanceStatus);
                System.out.println(endAttendanceDate);
                System.out.println(endAttendanceTime);

                

                Date endDate = formatter.parse(endAttendanceDate);
                System.out.println("end date: " + endDate);

                Date endTime = timeFormatter.parse(endAttendanceTime);
                System.out.println("end time: " + endTime);

                endAttendance.setAttendanceDate(endDate);
                endAttendance.setAttendanceTime(endTime);
                endAttendance.setAttendanceType(endAttendanceStatus);
                endAttendance.setAttendanceDateUpdate(updateDate);
                attendanceRepository.save(endAttendance);
                System.out.println("finish end date");

                JSONObject returnJSON = new JSONObject();
                returnJSON.put("status", "true");
                returnJSON.put("description", "update successfully");

                return returnJSON.toString();
                
            } else {
                System.out.println("access denied");
                jSONObject.put("status", "false");
                jSONObject.put("description", "you don't have authorization to access");

                return jSONObject.toJSONString();
            }

        } else {
            System.out.println("===== Wrong/Expire Token =====");
            jsonObject2.put("status", "false");
            jsonObject2.put("description", "you don't have authorization to access");

            return jsonObject2.toJSONString();
        }
    }

    @PutMapping("/deleteattendance/{startid}/{endid}")
    @ApiOperation(value = "delete Attendance by attendance ID")
    public String deleteDetailApprovalByAttendanceId(@RequestHeader("bearer") String header,
            @PathVariable String startid,@PathVariable String endid) {
//            @PathVariable String id, @RequestBody Map<String, ?> input) {

        System.out.println("startid: " + startid);
        System.out.println("end id: " + endid);

        JSONObject jsonObject2 = new JSONObject();
        JSONObject jSONObject = new JSONObject();

        int tokenExist = approvalRepository.findIfExistTokenForApproval(header);
        if (tokenExist == 1) {

            Users users = usersRepository.findUserByToken(header);
            System.out.println("user email: " + users.getUserEmail());
            int roleIda = users.getRoleId().getRoleId();
            System.out.println("roleId: " + roleIda);
            if (roleIda == 5) {
                System.out.println("you're authorized to access this operation");

                Date now = new Date();
                Attendance attendanceStart = attendanceRepository.findByAttendanceId(startid);
                attendanceStart.setAttendanceActive("true");
                attendanceStart.setAttendanceStatusId(new AttendanceStatus(6));
                attendanceRepository.save(attendanceStart);
                
                Attendance attendanceEnd = attendanceRepository.findByAttendanceId(endid);
                attendanceEnd.setAttendanceActive("false");
                attendanceEnd.setAttendanceType("leave");
                attendanceEnd.setAttendanceStatusId(new AttendanceStatus(6));
                attendanceRepository.save(attendanceEnd);

                System.out.println("delete success");
                jSONObject.put("status", "true");
                jSONObject.put("description", "delete successfully");

                return jSONObject.toJSONString();
            } else {
                System.out.println("access denied");
                jSONObject.put("status", "false");
                jSONObject.put("description", "you don't have authorization to access");

                return jSONObject.toJSONString();
            }

        } else {
            System.out.println("===== Wrong/Expire Token =====");
            jsonObject2.put("status", "false");
            jsonObject2.put("description", "you don't have authorization to access");

            return jsonObject2.toJSONString();
        }
//        return "test";
    }

    private void createRequestApprovalForEmployee(Approval app) {
        System.out.println("running save new approval " + app.getRequestId().getRequestId());

        String approvalRequesterId = app.getApprovalApproverId().getUserId();
        String division = approvalRepository.findDivisionByUserId(approvalRequesterId);
        String managerId = approvalRepository.findManagerIdbyDivision(division);

        SimpleDateFormat formater = new SimpleDateFormat("yyyyMMddHHmm");
        Date now = new Date();
        System.out.println("now: " + now);
        String strDate = formater.format(now);
        System.out.println("strDate: " + strDate);

        //id approval
        String approvalId = approvalRequesterId + strDate + approvalRequesterId;
        System.out.println("approval id: " + approvalId);
        int cekIfExistApprovalId = approvalRepository.cekIfExistApprovalId(approvalId);
        if (cekIfExistApprovalId == 1) {
            do {
                now = new Date();
                strDate = formater.format(now);
                approvalId = approvalRequesterId + strDate + approvalRequesterId;
                cekIfExistApprovalId = approvalRepository.cekIfExistApprovalId(approvalId);
            } while (cekIfExistApprovalId == 1);
        }

        final int APPROVAL_STATUS = 4;

        Approval approval = new Approval(approvalId, now, now, app.getApprovalRemark(),
                app.getApprovalDateStart(), app.getApprovalDateEnd(),
                app.getRequestId(), new ApprovalStatus(APPROVAL_STATUS), new Users(approvalRequesterId),
                new Users(managerId));

        approvalRepository.save(approval);
        System.out.println("request approval has been saved");
    }
}

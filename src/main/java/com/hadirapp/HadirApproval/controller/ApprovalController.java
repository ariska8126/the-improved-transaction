/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.hadirapp.HadirApproval.controller;

import com.hadirapp.HadirApproval.entity.Approval;
import com.hadirapp.HadirApproval.entity.ApprovalStatus;
import com.hadirapp.HadirApproval.entity.Attendance;
import com.hadirapp.HadirApproval.entity.Request;
import com.hadirapp.HadirApproval.entity.Users;
import com.hadirapp.HadirApproval.repository.ApprovalRepository;
import com.hadirapp.HadirApproval.repository.AttendanceRepository;
import com.hadirapp.HadirApproval.repository.BootcampDetailRepository;
import com.hadirapp.HadirApproval.repository.RequestRepository;
import com.hadirapp.HadirApproval.repository.UsersRepository;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.List;
import java.util.Map;
import net.minidev.json.JSONArray;
import net.minidev.json.JSONObject;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
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
    @GetMapping("/getapproval/{id}")
    @ApiOperation(value = "Get List Approval by UserId")
    public String getApprovalByRequester(@PathVariable String id) {

        List<Approval> approval = approvalRepository.findByID(id);
        if (approval == null) {
            System.out.println("approval status not found");
        }

        JSONArray jSONArray = new JSONArray();
        JSONObject j = new JSONObject();

        for (Approval app : approval) {
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

    }

    //read detail approval
    @GetMapping("/getattendancelistbyrequestid/{id}")
    @ApiOperation(value = "List Attendance by Request ID")
    public String getDetailApprovalByRequestId(@PathVariable String id) {
//    public String getDetailApprovalByRequestId(@RequestHeader ("userId") String userId, @PathVariable String id) {
//    public String getDetailApprovalByRequestId(@RequestBody Map<String, ?> input, @PathVariable String id) {
//        String userId = (String) input.get("userId");

        System.out.println("request id: " + id);

        //modifty get by request id
//        System.out.println("user id: " + userId);
//        String approver = userId;
//        int roleId = approvalRepository.findRoleUserbyId(userId);
//        System.out.println("user " + roleId);
//        if (roleId == 4) {
//            approver = userId;
//        } else if (roleId == 2) {
//            approver = approvalRepository.findTrainerIdByManager(id, userId);
//        } else if(roleId == 5){
//            String employeeBootcampId = bootcampDetailRepository.getEmployeeBootcampId(userId);
//            System.out.println("bootcamp id");
//            approver = bootcampDetailRepository.getTrainerByBootcampId(employeeBootcampId);
//        }
//        System.out.println("approver: " + approver);
        // end modify
//        List<Attendance> attendances = attendanceRepository.findDetailApprovalByRequestID(id, approver);
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
//        return "test";
    }

    //read detail approval
    @GetMapping("/getapprovallistbyrequestid/{id}")
    @ApiOperation(value = "List Approval by Request ID")
    public String getApprovalListByRequestId(@PathVariable String id) {

        System.out.println("request id: " + id);

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
    }

    //read detail approval
    @GetMapping("/getrequestbyrequestid/{id}")
    @ApiOperation(value = "List Approval by Request ID")
    public String getRequestByRequestId(@PathVariable String id) {

        System.out.println("request id: " + id);

        Request request = requestRepository.findRequestByRequesId(id);
        System.out.println("request id: " + request.getRequestId());
        if (request == null) {
            System.out.println("request status not found");
        }

        JSONArray jSONArray = new JSONArray();
        JSONObject j = new JSONObject();

        JSONObject jSONObject = new JSONObject();

        SimpleDateFormat formater = new SimpleDateFormat("yyyy-MM-dd");
        String reqDate = formater.format(request.getRequestDate());
        System.out.println("req date: " + reqDate);
        jSONObject.put("requestId", request.getRequestId());
        // modify date format for request date
        jSONObject.put("requestDate", reqDate);

        jSONObject.put("requestDateStart", request.getRequestDateStart().toString());
        jSONObject.put("requestDateEnd", request.getRequestDateEnd().toString());

        j.put("request", jSONObject);

        return j.toString();
    }

    @RequestMapping(value = "/checkattendance/{userId}", method = RequestMethod.GET)
    @ApiOperation(value = "Cek Status Approval sebelum Create New")
    public String cekStatusApproval(@PathVariable String userId) {

        JSONObject jSONObject = new JSONObject();
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
    }

    @RequestMapping(value = "/getAttendancebyLastMonth/{userId}", method = RequestMethod.GET)
    @ApiOperation(value = "Cek Attendance sebelum Create New")
    public String cekAttendance(@PathVariable String userId) {

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
    }

    //save new approval request
    @RequestMapping(value = "/insertnew", method = RequestMethod.POST)
    @ApiOperation(value = "Create new Request Approval last month")
    public String createNewRequestApprovalLastMonth(@RequestBody Map<String, ?> input) {
//    public String createNewRequestApprovalLastMonth(@PathVariable("id") String userId) {

        String userId = (String) input.get("userId");

        JSONObject jSONObject = new JSONObject();
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
        String remark = "Request for Approve";

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
//        return "test";
    }

//update approval
    @RequestMapping(value = "/update/{id}", method = RequestMethod.PUT)
    @ApiOperation(value = "${ApprovalController.updatebyid}")
    public String updateApproval(@RequestBody Map<String, ?> input, @PathVariable String id) {

        JSONObject jsonObject = new JSONObject();
        System.out.println("id: " + id);
        Approval app = approvalRepository.findByApprovalId(id);

        if (app == null) {
            jsonObject.put("status", "false");
            jsonObject.put("description", "update unsuccessfully, ID Not Found");

            return jsonObject.toString();
        }

        String approvalRemark = (String) input.get("approvalRemark");
        String approvalStatusIdStr = (String) input.get("approvalStatusId");
        System.out.println("remark: " + approvalRemark);

        int approvalStatusId = Integer.parseInt(approvalStatusIdStr);
        System.out.println("app id: " + approvalStatusId);

        Date now = new Date();
        app.setApprovalRemark(approvalRemark);
        app.setApprovalStatusId(new ApprovalStatus(approvalStatusId));
        app.setApprovalDateUpdate(now);
        app.setApprovalId(id);
        approvalRepository.save(app);

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
                break;
            //mail to employee
            case 6:
                message = "Approve by Manager";
                break;
            default:
                break;
        }

        jsonObject.put("status", "true");
        jsonObject.put("description", "update successfully: " + message);

        return jsonObject.toString();
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

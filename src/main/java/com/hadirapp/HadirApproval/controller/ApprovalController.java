/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.hadirapp.HadirApproval.controller;

import java.util.Optional;
import com.hadirapp.HadirApproval.entity.Approval;
import com.hadirapp.HadirApproval.repository.ApprovalRepository;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import java.util.List;
import net.minidev.json.JSONArray;
import net.minidev.json.JSONObject;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
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

    //read all approval by requester id
    @GetMapping("/getapprovalbyrequester/{id}")
    @ApiOperation(value = "${ApprovalController.getapprovalbyrequester}")
    public String getApprovalByRequester(@PathVariable String id) {

        List<Approval> approval = approvalRepository.findByRequesterID(id);
        if (approval == null) {
            System.out.println("approval status not found");
        }

        JSONArray jSONArray = new JSONArray();
        JSONObject j = new JSONObject();

        for (Approval app : approval) {
            JSONObject jSONObject = new JSONObject();
            jSONObject.put("approvalId", app.getApprovalId());
            jSONObject.put("approvalDate", app.getApprovalDate().toString());
            jSONObject.put("approvalDateUpdate", app.getApprovalDateUpdate().toString());
            jSONObject.put("approvalRemark", app.getApprovalRemark());
            jSONObject.put("requestId", app.getRequestId().getRequestId());
            jSONObject.put("approvalStatusId", app.getApprovalStatusId().getApprovalStatusName());
            jSONObject.put("approvalRequesterId", app.getApprovalRequesterId().getUserFullname());
            jSONObject.put("approvalApproverId", app.getApprovalApproverId().getUserFullname());
            jSONArray.add(jSONObject);
        }
        j.put("approvalList", jSONArray);

        return j.toString();

    }

    //read all approval by approver id
    @GetMapping("/getapprovalbyapprover/{id}")
    @ApiOperation(value = "${ApprovalController.getapprovalbyapprover}")
    public String getApprovalByApprover(@PathVariable String id) {

        List<Approval> approval = approvalRepository.findByApproverID(id);
        if (approval == null) {
            System.out.println("approval status not found");
        }

        JSONArray jSONArray = new JSONArray();
        JSONObject j = new JSONObject();

        for (Approval app : approval) {
            JSONObject jSONObject = new JSONObject();
            jSONObject.put("approvalId", app.getApprovalId());
            jSONObject.put("approvalDate", app.getApprovalDate().toString());
            jSONObject.put("approvalDateUpdate", app.getApprovalDateUpdate().toString());
            jSONObject.put("approvalRemark", app.getApprovalRemark());
            jSONObject.put("requestId", app.getRequestId().getRequestId());
            jSONObject.put("approvalStatusId", app.getApprovalStatusId().getApprovalStatusName());
            jSONObject.put("approvalRequesterId", app.getApprovalRequesterId().getUserFullname());
            jSONObject.put("approvalApproverId", app.getApprovalApproverId().getUserFullname());
            jSONArray.add(jSONObject);
        }
        j.put("approvalList", jSONArray);

        return j.toString();

    }

    //read detail approval
    //save new approval request
    
    //update approval
    @PutMapping("/update/{id}")
    @ApiOperation(value = "${ApprovalController.updatebyid}")
    public String updateApproval(@RequestBody Approval approval, @PathVariable String id) {

        JSONObject jsonObject = new JSONObject();

        Optional<Approval> app = approvalRepository.findById(id);

        if (!app.isPresent()) {
            jsonObject.put("status", "false");
            jsonObject.put("description", "update unsuccessfully, ID Not Found");

            return jsonObject.toString();
        }

        approval.setApprovalId(id);
        approvalRepository.save(approval);

        jsonObject.put("status", "true");
        jsonObject.put("description", "update successfully");

        return jsonObject.toString();
    }

}

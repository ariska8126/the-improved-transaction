///*
// * To change this license header, choose License Headers in Project Properties.
// * To change this template file, choose Tools | Templates
// * and open the template in the editor.
// */
//package com.hadirapp.HadirApproval.jasper;
//
//import org.springframework.http.HttpHeaders;
//import com.hadirapp.HadirApproval.repository.AttendanceRepository;
//import javax.servlet.http.HttpServletResponse;
//import org.springframework.beans.factory.annotation.Autowired;
//import org.springframework.http.HttpEntity;
//import org.springframework.http.MediaType;
//import org.springframework.stereotype.Controller;
//import org.springframework.web.bind.annotation.GetMapping;
//import org.springframework.web.bind.annotation.RequestMapping;
//import org.springframework.web.bind.annotation.ResponseBody;
//
///**
// *
// * @author creative
// */
//@Controller
//@RequestMapping("/")
//public class ReportController {
//
//    private final AttendanceRepository attendanceRepository;
//    private final ReportService reportService;
//
//    @Autowired
//    public ReportController(final AttendanceRepository attendanceRepository, final ReportService reportService) {
//        this.attendanceRepository = attendanceRepository;
//        this.reportService = reportService;
//    }
//
//    @GetMapping(value = "/employeeReport.xlsx", produces = "application/vnd.openxmlformats-officedocument.spreadsheetml.sheet")
//    @ResponseBody
//    public HttpEntity<byte[]> getEmployeeReportXlsx(final HttpServletResponse response) throws ReportException {
//        final AttendanceReport report = new AttendanceReport(attendanceRepository.findAll());
//        final byte[] data = reportService.getReportXlsx(report.getReport());
//
//        HttpHeaders header = new HttpHeaders();
//        header.setContentType(MediaType.parseMediaType("application/vnd.openxmlformats-officedocument.spreadsheetml.sheet"));
//        header.set(HttpHeaders.CONTENT_DISPOSITION, "inline; filename=attendanceReport.xlsx");
//        header.setContentLength(data.length);
//
//        return new HttpEntity<byte[]>(data, header);
//    }
//
//}

///*
// * To change this license header, choose License Headers in Project Properties.
// * To change this template file, choose Tools | Templates
// * and open the template in the editor.
// */
//package com.hadirapp.HadirApproval.jasper;
//
//import java.io.ByteArrayOutputStream;
//import java.io.IOException;
//import net.sf.jasperreports.engine.JRException;
//import net.sf.jasperreports.engine.JasperPrint;
//import net.sf.jasperreports.engine.export.ooxml.JRXlsxExporter;
//import net.sf.jasperreports.export.SimpleExporterInput;
//import net.sf.jasperreports.export.SimpleOutputStreamExporterOutput;
//import org.springframework.stereotype.Service;
//
///**
// *
// * @author creative
// */
//@Service
//public class ReportService {
//
//    public ReportService() {
//    }
//
//    public byte[] getReportXlsx(final JasperPrint jasperPrint) throws ReportException {
//        final JRXlsxExporter xlsxExporter = new JRXlsxExporter();
//        final byte[] rawBytes;
//
//        try (final ByteArrayOutputStream xlsReport = new ByteArrayOutputStream()) {
//            xlsxExporter.setExporterInput(new SimpleExporterInput(jasperPrint));
//            xlsxExporter.setExporterOutput(new SimpleOutputStreamExporterOutput(xlsReport));
//            xlsxExporter.exportReport();
//
//            rawBytes = xlsReport.toByteArray();
//        } catch (JRException | IOException e) {
//            throw new ReportException(e);
//        }
//
//        return rawBytes;
//    }
//
//}

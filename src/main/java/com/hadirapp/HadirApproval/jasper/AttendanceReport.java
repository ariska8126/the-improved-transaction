/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.hadirapp.HadirApproval.jasper;

import ar.com.fdvs.dj.core.DynamicJasperHelper;
import ar.com.fdvs.dj.core.layout.ClassicLayoutManager;
import ar.com.fdvs.dj.domain.DynamicReport;
import ar.com.fdvs.dj.domain.builders.ColumnBuilderException;
import ar.com.fdvs.dj.domain.builders.StyleBuilder;
import ar.com.fdvs.dj.domain.constants.Border;
import ar.com.fdvs.dj.domain.constants.Font;
import ar.com.fdvs.dj.domain.constants.HorizontalAlign;
import ar.com.fdvs.dj.domain.constants.Transparency;
import ar.com.fdvs.dj.domain.constants.VerticalAlign;
import com.hadirapp.HadirApproval.entity.Attendance;
import java.awt.Color;
import ar.com.fdvs.dj.domain.Style;
import ar.com.fdvs.dj.domain.builders.ColumnBuilder;
import ar.com.fdvs.dj.domain.builders.DynamicReportBuilder;
import ar.com.fdvs.dj.domain.entities.columns.AbstractColumn;
import com.hadirapp.HadirApproval.entity.AttendanceStatus;
import com.hadirapp.HadirApproval.entity.Users;
import java.util.Collection;
import net.sf.jasperreports.engine.JRException;
import net.sf.jasperreports.engine.JasperPrint;
import net.sf.jasperreports.engine.data.JRBeanCollectionDataSource;

/**
 *
 * @author creative
 */
@SuppressWarnings("deprecation")
public class AttendanceReport implements Report {

    private final Collection<Attendance> attendances;

    public AttendanceReport(Collection<Attendance> attendances) {
        this.attendances = attendances;
    }

    @Override
    public JasperPrint getReport() throws ReportException {
        final JasperPrint jp;
        try {
            ar.com.fdvs.dj.domain.Style headerStyle = createHeaderStyle();
            ar.com.fdvs.dj.domain.Style detailTextStyle = createDetailTextStyle();
            ar.com.fdvs.dj.domain.Style detailNumberStyle = createDetailNumberStyle();
            DynamicReport dynaReport = getReport(headerStyle, detailTextStyle, detailNumberStyle);
            jp = DynamicJasperHelper.generateJasperPrint(dynaReport, new ClassicLayoutManager(),
                    new JRBeanCollectionDataSource(attendances));
        } catch (JRException | ColumnBuilderException | ClassNotFoundException e) {
            throw new ReportException(e);
        }

        return jp;
    }

    private Style createHeaderStyle() {
        return new StyleBuilder(true)
                .setFont(Font.VERDANA_MEDIUM_BOLD)
                .setBorder(Border.THIN())
                .setBorderBottom(Border.PEN_2_POINT())
                .setBorderColor(Color.BLACK)
                .setBackgroundColor(Color.ORANGE)
                .setTextColor(Color.BLACK)
                .setHorizontalAlign(HorizontalAlign.CENTER)
                .setVerticalAlign(VerticalAlign.MIDDLE)
                .setTransparency(Transparency.OPAQUE)
                .build();
    }

    private Style createDetailTextStyle() {
        return new StyleBuilder(true)
                .setFont(Font.VERDANA_MEDIUM)
                .setBorder(Border.DOTTED())
                .setBorderColor(Color.BLACK)
                .setTextColor(Color.BLACK)
                .setHorizontalAlign(HorizontalAlign.LEFT)
                .setVerticalAlign(VerticalAlign.MIDDLE)
                .setPaddingLeft(5)
                .build();
    }

    private Style createDetailNumberStyle() {
        return new StyleBuilder(true)
                .setFont(Font.VERDANA_MEDIUM)
                .setBorder(Border.DOTTED())
                .setBorderColor(Color.BLACK)
                .setTextColor(Color.BLACK)
                .setHorizontalAlign(HorizontalAlign.RIGHT)
                .setVerticalAlign(VerticalAlign.MIDDLE)
                .setPaddingRight(5)
                .setPattern("#,##0.00")
                .build();
    }

    private AbstractColumn createColumn(String property, Class<?> type, String title, int width, Style headerStyle, Style detailStyle)
            throws ColumnBuilderException {
        return ColumnBuilder.getNew()
                .setColumnProperty(property, type.getName())
                .setTitle(title)
                .setWidth(Integer.valueOf(width))
                .setStyle(detailStyle)
                .setHeaderStyle(headerStyle)
                .build();
    }

    private DynamicReport getReport(Style headerStyle, Style detailTextStyle, Style detailNumStyle)
            throws ColumnBuilderException, ClassNotFoundException {
        
        Users user = new Users();

        DynamicReportBuilder report = new DynamicReportBuilder();

        AbstractColumn columnEmpNo = createColumn("userId", Users.class, "Employee Number", 30, headerStyle, detailTextStyle);
        AbstractColumn columnName = createColumn("attendanceType", String.class, "Attendance Type", 30, headerStyle, detailTextStyle);
        AbstractColumn columnSalary = createColumn("attendanceStatusId", AttendanceStatus.class, "Attendance Status", 30, headerStyle, detailNumStyle);
        
        report.addColumn(columnEmpNo).addColumn(columnName).addColumn(columnSalary);

        StyleBuilder titleStyle = new StyleBuilder(true);
        titleStyle.setHorizontalAlign(HorizontalAlign.CENTER);
        titleStyle.setFont(new Font(20, null, true));
        // you can also specify a font from the classpath, eg:
        // titleStyle.setFont(new Font(20, "/fonts/someFont.ttf", true));

        StyleBuilder subTitleStyle = new StyleBuilder(true);
        subTitleStyle.setHorizontalAlign(HorizontalAlign.CENTER);
        subTitleStyle.setFont(new Font(Font.MEDIUM, null, true));

        report.setTitle("Attendance Report");
        report.setTitleStyle(titleStyle.build());
        report.setSubtitle("Metrodata Coding Camp");
        report.setSubtitleStyle(subTitleStyle.build());
        report.setUseFullPageWidth(true);
        return report.build();
    }
}

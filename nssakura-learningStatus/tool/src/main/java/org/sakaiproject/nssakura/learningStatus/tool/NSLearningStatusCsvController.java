package org.sakaiproject.nssakura.learningStatus.tool;

import java.io.IOException;
import java.io.OutputStream;
import java.io.OutputStreamWriter;
import java.io.PrintWriter;
import java.util.ArrayList;
import java.util.List;

import javax.servlet.ServletOutputStream;
import javax.servlet.http.HttpServletResponse;

import org.sakaiproject.component.cover.ServerConfigurationService;
import org.sakaiproject.nssakura.learningStatus.model.GradeAssignmentItem;
import org.sakaiproject.nssakura.learningStatus.model.GradeAssignmentTotalItem;
import org.sakaiproject.nssakura.learningStatus.model.GradeSectionModel;
import org.sakaiproject.nssakura.learningStatus.model.GradeUserModel;
import org.sakaiproject.nssakura.section.model.SectionModel;
import org.sakaiproject.user.api.User;
import org.sakaiproject.util.ResourceLoader;

import au.com.bytecode.opencsv.CSVWriter;

public class NSLearningStatusCsvController {
	protected static ResourceLoader rb = new ResourceLoader("org.sakaiproject.nssakura.learningStatus.bundle.messages");
	private final String charCode="utf-8";
	private final String csvFileName="result.csv";
	private final String textFileName="result.txt";
	
	protected void outputCsv(HttpServletResponse res, List<GradeSectionModel> sections){
		res.setContentType("text/csv;charset="+charCode);
		res.setHeader("Content-Disposition","attachment; filename="+csvFileName);
		res.setHeader("Cache-Control", "public");
		res.setHeader("Pragma", "public");
		try {
			ServletOutputStream out = res.getOutputStream();
			setBom(out);
			CSVWriter csvWriter = new CSVWriter(new OutputStreamWriter(out, charCode));
			csvWriter.writeAll(convertList(sections));
			csvWriter.flush();
		} catch (IOException e) {
		}
	}
	
	private List<String[]> convertList(List<GradeSectionModel> sections){
		List<String[]> resultList = new ArrayList<String[]>();
		List<String> titleList = sections.get(0).getAssignmentTitleList();
		titleList.add(0,rb.getString("label_section_name"));
		titleList.add(1,rb.getString("label_number_of_students"));
		String[] titles = (String[])titleList.toArray(new String[0]);
		resultList.add(titles);
		for(GradeSectionModel section:sections){
			SectionModel model = section.getSectionModel();
			if( model == null ){
				continue;
			}
			List<GradeAssignmentTotalItem> assignments = section.getAssignmentItems();
			String[] data = new String[assignments.size()+2];
			data[0]=model.getTitle();
			data[1]=String.valueOf(model.getTotalEnrollments());
			int n = 2;
			for(GradeAssignmentTotalItem assignment:assignments){
				data[n] = String.valueOf(assignment.getExecuteNum());
				n++;
			}
			resultList.add(data);
		}
		return resultList;
	}
	
	protected void outputCsvStudent(HttpServletResponse res, List<GradeUserModel>users){
		res.setContentType("text/csv;charset="+charCode);
		res.setHeader("Content-Disposition","attachment; filename="+csvFileName);
		res.setHeader("Cache-Control", "public");
		res.setHeader("Pragma", "public");
		try {
			ServletOutputStream out = res.getOutputStream();
			setBom(out);
			CSVWriter csvWriter = new CSVWriter(new OutputStreamWriter(out, charCode));
			csvWriter.writeAll(convertListStudent(users));
			csvWriter.flush();
		} catch (IOException e) {
		}
	}

	protected String getIdList(List<User>users){
		if(users.isEmpty()){
			return "";
		}
		String result = null;
		for(User user:users){
			if(result == null){
				result = user.getEid();
			}else{
				result += "\n" + user.getEid();
			}
		}
		return result;
	}
	
	private List<String[]> convertListStudent(List<GradeUserModel> users){
		List<String[]> resultList = new ArrayList<String[]>();
		List<String> titleList = users.get(0).getAssignmentTitleList();
		titleList.add(0,rb.getString("label_user_id"));
		titleList.add(1,rb.getString("label_user_name"));
		String[] titles = (String[])titleList.toArray(new String[0]);
		resultList.add(titles);
		for(GradeUserModel guser:users){
			List<GradeAssignmentItem> assignments = guser.getAssignmentItems();
			String[] data = new String[assignments.size()+2];
			data[0]=guser.getUser().getDisplayId();
			data[1]=guser.getUser().getDisplayName();
			int n = 2;
			for(GradeAssignmentItem assignment:assignments){
				Double point = assignment.getPoints();
				if( point != null){
					data[n] = String.valueOf(assignment.getPoints());
				}
				n++;
			}
			resultList.add(data);
		}
		return resultList;
	}
	
	protected void outputCsvUserEids(HttpServletResponse res, List<User>users){
		res.setContentType("text/html;charset="+charCode);
		try {
			PrintWriter writer = res.getWriter();
			writer.println("<html><body><pre>");
			for(User user:users){
				writer.write(user.getDisplayId() + "\n");
			}
			writer.println("</pre></body></html>");
		} catch (IOException e) {
		}
	}

	private void setBom(OutputStream out){
		try {
			out.write(0xef);
			out.write(0xbb);
			out.write(0xbf);
		} catch (IOException e) {
		}
	}

}

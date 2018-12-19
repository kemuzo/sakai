package org.sakaiproject.nssakura.section.model;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

import org.sakaiproject.section.api.coursemanagement.Course;
import org.sakaiproject.section.api.coursemanagement.CourseSection;
import org.sakaiproject.section.api.coursemanagement.Meeting;

public class CourseSectionModel implements CourseSection, Serializable {
    private static final long serialVersionUID = 1L;

    private Course course;
    private String uuid;
    private String title;
    private String category;
    private Integer maxEnrollments;

    // We need a string to represent size limit due to this JSF bug:
    // http://issues.apache.org/jira/browse/MYFACES-570
    private String limitSize;

    private List<Meeting> meetings;

    public CourseSectionModel() {
    }

    public CourseSectionModel(Course course, String title, String category) {
        this.course = course;
        this.title = title;
        this.category = category;
        limitSize = Boolean.FALSE.toString();
    }

    public CourseSectionModel(CourseSection section, Course course, String title, String category) {
        this.course = course;
        this.title = title;
        this.category = category;
        limitSize = Boolean.FALSE.toString();

    }

    public Integer getMaxEnrollments() {
        return maxEnrollments;
    }

    public void setMaxEnrollments(Integer maxEnrollments) {
        this.maxEnrollments = maxEnrollments;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public List<Meeting> getMeetings() {
        if (meetings == null) {
            // Keep this out of the constructor to avoid it in deserialization
            this.meetings = new ArrayList<Meeting>();
        }
        return meetings;
    }

    public void setMeetings(List<Meeting> meetings) {
        this.meetings = meetings;
    }

    public String getLimitSize() {
        return limitSize;
    }

    public void setLimitSize(String limitSize) {
        this.limitSize = limitSize;
    }

    public String getCategory() {
        return category;
    }

    public void setCategory(String category) {
        this.category = category;
    }

    public Course getCourse() {
        return course;
    }

    public void setCourse(Course course) {
        this.course = course;
    }

    public String getUuid() {
        return uuid;
    }

    /**
     * Enterprise ID is not needed in this app.
     */
    public String getEid() {
        return null;
    }

    @Override
    public boolean isLocked() {
        return false;
    }
}

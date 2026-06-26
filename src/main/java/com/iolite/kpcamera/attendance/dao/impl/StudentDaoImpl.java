package com.iolite.kpcamera.attendance.dao.impl;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

import com.iolite.kpcamera.attendance.dao.StudentDetailsDao;
import com.iolite.kpcamera.attendance.mapper.ActivityMapper;
import com.iolite.kpcamera.attendance.mapper.ActivityPersonalMapper;
import com.iolite.kpcamera.attendance.mapper.StudentDataMapperForAPK;
import com.iolite.kpcamera.attendance.mapper.StudentMiddlewareMapper;
import com.iolite.kpcamera.attendance.mapper.StudentRequestMapper;
import com.iolite.kpcamera.attendance.mapper.TeacherMarkedAttendenceMapper;
import com.iolite.kpcamera.attendance.model.AttendanceReqestModel;
import com.iolite.kpcamera.attendance.model.ScheduleDto;
import com.iolite.kpcamera.attendance.model.StudentDto;
import com.iolite.kpcamera.attendance.model.responses.StudentApkDtoResponse;
import com.iolite.kpcamera.attendance.model.responses.StudentResDataDto;
import com.iolite.kpcamera.attendance.service.AcademicYearService;

@Repository
public class StudentDaoImpl implements StudentDetailsDao{

	static final Logger lg = LoggerFactory.getLogger(StudentDaoImpl.class);

	@Autowired
	private JdbcTemplate cristMysoreJDBC;
	
	@Autowired
	private JdbcTemplate cristMysoreLiveJDBC;

	@Autowired
	private JdbcTemplate middleWareJDBC;
	
	private static String academicYear;
	
	@Autowired
	AcademicYearService service;
	
	// fetching current academic student with proper reg no and not dropped out

	private static String QUERY = "select s.id as studentid,p.first_name as studentName,p.email as email,s.register_no as regno "
			+ "from student s join adm_appln a on s.adm_appln_id = a.id join personal_data p on a.personal_data_id = p.id "
			+ "join class_schemewise c on s.class_schemewise_id = c.id  join classes ON classes.id = c.class_id "
			+ "join curriculum_scheme_duration ON curriculum_scheme_duration.id = c.curriculum_scheme_duration_id "
			+ "and a.is_selected = 1 and a.is_cancelled = 0 and a.is_approved = 1 and s.is_admitted = 1 "
			+ "and curriculum_scheme_duration.academic_year = (select year from academic_year where is_current = 1)"
			+ "and (a.is_draft_cancelled = 0 or a.is_draft_cancelled is null) and s.register_no is not null and s.register_no != ''";

	private static String GET_ACTIVITY_QUERY = "select tt_period_week.week_day as week_day,\r\n" + 
			"\r\n" + 
			"subject.id as subject_id,subject.code,subject.name as subject,\r\n" + 
			"\r\n" + 
			"period.start_time as start_time,period.end_time as end_time,room_master.id as roomid,room_master.floor_name,\r\n" + 
			"\r\n" + 
			"student.id as stuid,student.register_no,curriculum_scheme_duration.start_date,curriculum_scheme_duration.end_date,period.id as pid,tt_subject_batch.activity_id,\r\n" + 
			"\r\n" +
			"tt_users.user_id as teacherId"+
			"\r\n" +
			"from tt_users\r\n" + 
			"\r\n" + 
			"join tt_subject_batch ON tt_users.tt_subject_id = tt_subject_batch.id\r\n" + 
			"\r\n" + 
			"join tt_period_week ON tt_subject_batch.tt_period_id = tt_period_week.id\r\n" + 
			"\r\n" + 
			"join tt_class ON tt_period_week.tt_class_id = tt_class.id\r\n" + 
			"\r\n" + 
			"join class_schemewise ON tt_class.class_schemewise_id = class_schemewise.id\r\n" + 
			"\r\n" + 
			"join curriculum_scheme_duration ON class_schemewise.curriculum_scheme_duration_id = curriculum_scheme_duration.id\r\n" + 
			"\r\n" + 
			"join classes ON class_schemewise.class_id = classes.id\r\n" + 
			"\r\n" + 
			"join period ON tt_period_week.period_id = period.id\r\n" + 
			"\r\n" + 
			"and period.class_schemewise_id = class_schemewise.id\r\n" + 
			"\r\n" + 
			"join users ON tt_users.user_id = users.id\r\n" + 
			"\r\n" + 
			"join subject ON tt_subject_batch.subject_id = subject.id\r\n" + 
			"\r\n" + 
			"join employee ON users.employee_id = employee.id\r\n" + 
			"\r\n" + 
			"join teacher_class_subject ON teacher_class_subject.class_schemewise_id = class_schemewise.id\r\n" + 
			"\r\n" + 
			"and teacher_class_subject.subject_id = subject.id\r\n" + 
			"\r\n" + 
			"and teacher_class_subject.teacher_id = users.id\r\n" + 
			"\r\n" + 
			"and teacher_class_subject.year = curriculum_scheme_duration.academic_year\r\n" + 
			"\r\n" + 
			"join student ON student.class_schemewise_id  = class_schemewise.id\r\n" + 
			"\r\n" + 
			"join adm_appln ON student.adm_appln_id = adm_appln.id\r\n" + 
			"\r\n" + 
			"join applicant_subject_group ON applicant_subject_group.adm_appln_id = adm_appln.id\r\n" + 
			"\r\n" + 
			"join subject_group ON applicant_subject_group.subject_group_id = subject_group.id\r\n" + 
			"\r\n" + 
			"join subject_group_subjects ON subject_group_subjects.subject_group_id = subject_group.id\r\n" + 
			"\r\n" + 
			"and subject_group.is_active=1\r\n" + 
			"\r\n" + 
			"and subject_group_subjects.is_active=1\r\n" + 
			"\r\n" + 
			"and subject_group_subjects.subject_id = subject.id\r\n" + 
			"\r\n" + 
			"left join room_master ON tt_subject_batch.room_master_id = room_master.id\r\n" + 
			"\r\n" + 
			"left join batch ON tt_subject_batch.batch_id = batch.id\r\n" + 
			"\r\n" + 
		    "where curriculum_scheme_duration.academic_year= (select year from academic_year where is_current = 1)"+
			"\r\n" + 
			"and tt_class.is_active=1\r\n" + 
			"\r\n" + 
			"and tt_period_week.is_active=1\r\n" + 
			"\r\n" + 
			"and tt_subject_batch.is_active=1\r\n" + 
			"\r\n" + 
			"and tt_users.is_active=1\r\n" + 
			"\r\n" + 
			"and student.is_admitted=1\r\n" + 
			"\r\n" + 
			"and adm_appln.is_cancelled=0\r\n" + 
			"and subject.is_active = 1";


	private static String GET_TEACHER_MARKED_ATTENDANCE ="select student.id as sid,student.register_no as regNo, classes.name as clsName, subject.name as subName, \r\n" + 
			"\r\n" + 
			"period.period_name as pName, attendance_student.is_present as isPresent, users.user_name as userName,personal_data.first_name as sName\r\n" + 
			"\r\n" + 
			"from attendance\r\n" + 
			"\r\n" + 
			"join attendance_instructor ON attendance_instructor.attendance_id = attendance.id\r\n" + 
			"\r\n" + 
			"join attendance_class ON attendance_class.attendance_id = attendance.id\r\n" + 
			"\r\n" + 
			"join class_schemewise ON attendance_class.class_schemewise_id = class_schemewise.id\r\n" + 
			"\r\n" + 
			"join curriculum_scheme_duration ON class_schemewise.curriculum_scheme_duration_id = curriculum_scheme_duration.id\r\n" + 
			"\r\n" + 
			"join users ON attendance_instructor.users_id = users.id\r\n" + 
			"\r\n" + 
			"join employee ON users.employee_id = employee.id\r\n" + 
			"\r\n" + 
			"join classes ON class_schemewise.class_id = classes.id\r\n" + 
			"\r\n" + 
			"join subject ON attendance.subject_id = subject.id\r\n" + 
			"\r\n" + 
			"join attendance_student ON attendance_student.attendance_id = attendance.id\r\n" + 
			"\r\n" + 
			"join student ON attendance_student.student_id = student.id\r\n" + 
			"\r\n" + 
			"join adm_appln ON adm_appln.id = student.adm_appln_id\r\n" + 
			"\r\n" + 
			"join personal_data ON personal_data.id = adm_appln.personal_data_id\r\n" + 
			"\r\n" + 
			"and student.class_schemewise_id = class_schemewise.id\r\n" + 
			"\r\n" + 
			"join attendance_period ON attendance_period.attendance_id = attendance.id\r\n" + 
			"\r\n" + 
			"join period ON attendance_period.period_id = period.id\r\n" + 
			"\r\n" + 
			"and period.class_schemewise_id = class_schemewise.id\r\n" + 
			"\r\n" + 
			"where curriculum_scheme_duration.academic_year=(select year from academic_year where is_current = 1)\r\n" + 
			"\r\n" + 
			"and attendance.is_canceled=0";
	
	@Override
	public List<StudentDto> getStudentDetails() {
		lg.info("Started getStudentDetails()",QUERY);
		return cristMysoreJDBC.query(QUERY, new StudentRequestMapper());
	}

	@Override
	public List<StudentResDataDto> getAwsPersonalId(int orgId) throws Exception {
		String MIDDLEWARE_STUDNET_QUERY = "select id as sid ,personnel_id as pId from student where org_id="+String.valueOf(orgId);
		return middleWareJDBC.query(MIDDLEWARE_STUDNET_QUERY, new StudentMiddlewareMapper());
	}

	@Override
	public List<ScheduleDto> getActivityInfo(int orgId) throws Exception {
		String GET_ACTIVITY_QUERY_DATA = GET_ACTIVITY_QUERY + 
				" group by period.id,tt_period_week.week_day,subject_group_subjects.subject_id,period.start_time,tt_users.user_id\r\n" +
				" order by tt_period_week.week_day";
		return cristMysoreJDBC.query(GET_ACTIVITY_QUERY_DATA, new ActivityMapper());
	}

	@Override
	public List<StudentDto> getActivityPersonalData(String org_activity_id, int orgId) {
		String[] arr = org_activity_id.split("_");
		String GET_ACTIVITY_PERSONAL_QUERY = GET_ACTIVITY_QUERY +" and subject.id ="+arr[0]+" and period.id ="+arr[1]+" and tt_period_week.week_day="+"'"+arr[2]+"'"+" and tt_users.user_id ="+arr[3];
		return cristMysoreJDBC.query(GET_ACTIVITY_PERSONAL_QUERY, new ActivityPersonalMapper());
	}
	
	@Transactional(readOnly = true,timeout = 100)
	@Override
	public List<StudentApkDtoResponse> getStudentDetailsforAPK() {
		lg.info("Started getStudentDetailsforAPK()",QUERY);
		return cristMysoreJDBC.query(QUERY, new StudentDataMapperForAPK());
	}

	@Override
	public List<AttendanceReqestModel> getTeacherMarkedAttendanceData(StudentApkDtoResponse studentApkDtoResponse) {
		SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd");
		String GET_TEACHER_MARKED_ATTENDANCE_QUERY = GET_TEACHER_MARKED_ATTENDANCE + " and attendance.attendance_date='"+dateFormat.format(new Date())+"' and users.id in "
				+ "("+studentApkDtoResponse.getTecherId()+") and period.id in ("+studentApkDtoResponse.getPeriodId()+")";
		List<AttendanceReqestModel> modelData = cristMysoreLiveJDBC.query(GET_TEACHER_MARKED_ATTENDANCE_QUERY, new TeacherMarkedAttendenceMapper());
		return modelData;
	}

}

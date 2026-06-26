package com.iolite.kpcamera.attendance.dao.impl;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Repository;

import com.iolite.kpcamera.attendance.dao.GroupDetailsInKpDao;
import com.iolite.kpcamera.attendance.mapper.ActivityMapper;
import com.iolite.kpcamera.attendance.mapper.GroupDtoMapper;
import com.iolite.kpcamera.attendance.mapper.PresentAttendanceMapper;
import com.iolite.kpcamera.attendance.model.AttendanceReqestModel;
import com.iolite.kpcamera.attendance.model.GroupDetails;

@Repository
public class GroupDetailsInKpDaoImpl implements GroupDetailsInKpDao{
	
	@Autowired
	private JdbcTemplate cristMysoreJDBC;
	
	private static String QUERY_FOR_GROUP = "select tt_period_week.week_day as week_day,			 \r\n" + 
			"subject.id as subject_id,subject.code,subject.name as subject,		 \r\n" + 
			"period.start_time as start_time,period.end_time as end_time,room_master.id as roomid,room_master.floor_name,		 \r\n" + 
			"student.id as stuid,student.register_no as regno,curriculum_scheme_duration.start_date,curriculum_scheme_duration.end_date,period.id as pid,tt_subject_batch.activity_id,			\r\n" + 
			"tt_users.user_id as teacherId,classes.id as classId,users.user_name as techname,period.period_name as pname,classes.name as clsname,personal_data.first_name as stuname		\r\n" + 
			"from tt_users			 \r\n" + 
			"join tt_subject_batch ON tt_users.tt_subject_id = tt_subject_batch.id			 \r\n" + 
			"join tt_period_week ON tt_subject_batch.tt_period_id = tt_period_week.id			 \r\n" + 
			"join tt_class ON tt_period_week.tt_class_id = tt_class.id			 \r\n" + 
			"join class_schemewise ON tt_class.class_schemewise_id = class_schemewise.id			 \r\n" + 
			"join curriculum_scheme_duration ON class_schemewise.curriculum_scheme_duration_id = curriculum_scheme_duration.id			 \r\n" + 
			"join classes ON class_schemewise.class_id = classes.id			 \r\n" + 
			"join period ON tt_period_week.period_id = period.id			 \r\n" + 
			"and period.class_schemewise_id = class_schemewise.id			 \r\n" + 
			"join users ON tt_users.user_id = users.id			 \r\n" + 
			"join subject ON tt_subject_batch.subject_id = subject.id			 \r\n" + 
			"join employee ON users.employee_id = employee.id			 \r\n" + 
			"join teacher_class_subject ON teacher_class_subject.class_schemewise_id = class_schemewise.id			 \r\n" + 
			"and teacher_class_subject.subject_id = subject.id			 \r\n" + 
			"and teacher_class_subject.teacher_id = users.id			 \r\n" + 
			"and teacher_class_subject.year = curriculum_scheme_duration.academic_year			 \r\n" + 
			"join student ON student.class_schemewise_id  = class_schemewise.id			 \r\n" + 
			"join adm_appln ON student.adm_appln_id = adm_appln.id			 \r\n" +
			"join personal_data on personal_data.id = adm_appln.personal_data_id			 \r\n "+
			"join applicant_subject_group ON applicant_subject_group.adm_appln_id = adm_appln.id		 \r\n" + 
			"join subject_group ON applicant_subject_group.subject_group_id = subject_group.id			 \r\n" + 
			"join subject_group_subjects ON subject_group_subjects.subject_group_id = subject_group.id			 \r\n" + 
			"and subject_group.is_active=1			 \r\n" + 
			"and subject_group_subjects.is_active=1			 \r\n" + 
			"and subject_group_subjects.subject_id = subject.id			 \r\n" + 
			"left join room_master ON tt_subject_batch.room_master_id = room_master.id		 \r\n" + 
			"left join batch ON tt_subject_batch.batch_id = batch.id		 \r\n" + 
			"where curriculum_scheme_duration.academic_year= (select year from academic_year where is_current = 1)		 \r\n" + 
			"and tt_class.is_active=1			 \r\n" + 
			"and tt_period_week.is_active=1		 \r\n" + 
			"and tt_subject_batch.is_active=1		 \r\n" + 
			"and tt_users.is_active=1			 \r\n" + 
			"and student.is_admitted=1		 \r\n" + 
			"and adm_appln.is_cancelled=0\r\n" + 
			"and subject.is_active = 1 " + 
			"and room_master.id in (479,427)";
	
	
	private static String MARKED_PRESENT_DETAILS = "select student.id as stuid,student.register_no as regNo,\r\n" + 
			"classes.id as clsid,\r\n" + 
			"classes.name as clsname,\r\n" + 
			"subject.name as subname,\r\n" + 
			"subject.id as subject_id,\r\n" + 
			"period.id as pid,\r\n" + 
			"period.period_name as pname,\r\n" + 
			"attendance_student.is_present as ispresent,\r\n" + 
			"attendance_student.is_cocurricular_leave\r\n" + 
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
			"and student.class_schemewise_id = class_schemewise.id\r\n" + 
			"\r\n" + 
			"join attendance_period ON attendance_period.attendance_id = attendance.id\r\n" + 
			"\r\n" + 
			"join period ON attendance_period.period_id = period.id\r\n" + 
			"\r\n" + 
			"and period.class_schemewise_id = class_schemewise.id\r\n" + 
			"\r\n" + 
			"where curriculum_scheme_duration.academic_year= (select year from academic_year where is_current = 1)\r\n" + 
			"\r\n" + 
			"and attendance.is_canceled=0";

	@Override
	public List<GroupDetails> getGroupStudentFromKP(String orgId, String dayOfName, String startTimeFilter, String endTimeFilter) {
		StringBuilder sb = new StringBuilder();
		sb.append(" and period.start_time between"+"'"+startTimeFilter+"'"+" and "+"'"+endTimeFilter+"'");
		sb.append(" and tt_period_week.week_day ="+"'"+dayOfName+"'");
		String GET_QUERY_FOR_GROUP_DATA = QUERY_FOR_GROUP + sb.toString();
				
		return cristMysoreJDBC.query(GET_QUERY_FOR_GROUP_DATA, new GroupDtoMapper());
		
	}

	@Override
	public List<AttendanceReqestModel> getPresentedStudentData(String[] data) {
		SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd");
		String date = dateFormat.format(new Date());
		StringBuilder sb = new StringBuilder();
		sb.append(" and attendance.attendance_date="+"'"+date+"'");
		sb.append(" and users.id="+data[2]);
		sb.append(" and period.id="+data[1]);
		String GET_QUERY_FOR_ATTENDANCE_DATA = MARKED_PRESENT_DETAILS + sb.toString();
		return cristMysoreJDBC.query(GET_QUERY_FOR_ATTENDANCE_DATA, new PresentAttendanceMapper());
	}

}

package com.iolite.kpcamera.attendance.dao.impl;

import java.sql.PreparedStatement;
import java.sql.SQLException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.List;
import java.util.stream.Collectors;

import org.joda.time.DateTime;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.ParameterizedPreparedStatementSetter;
import org.springframework.stereotype.Repository;

import com.iolite.kpcamera.attendance.dao.AttendanceDao;
import com.iolite.kpcamera.attendance.mapper.StudentAttendanceWeekDataMapper;
import com.iolite.kpcamera.attendance.mapper.StudentMiddlewareMapperForAPK;
import com.iolite.kpcamera.attendance.model.AttendanceReqestModel;
import com.iolite.kpcamera.attendance.model.responses.StudentApkDtoResponse;
import com.iolite.kpcamera.attendance.service.MegBoxAttendanceService;
import com.iolite.kpcamera.attendance.service.StudentService;

@Repository
public class AttendanceDaoImpl implements AttendanceDao{

	@Autowired
	private JdbcTemplate middleWareJDBC;
	
	@Autowired
	StudentService service;
	static final Logger lg = LoggerFactory.getLogger(AttendanceDaoImpl.class);
	
//	private static String INSERT_QUERY = "INSERT INTO attendance_data(org_id,room_id,subject_id,teacher_id,start_date,\r\n" + 
//			"start_time,end_date,end_time,student_id,is_recong_student,is_active,data_from,"+
//			"period_id,period_name,class_id,class_name,subject_name,reg_no,student_name,teacher_name,req_received_time,meg_box_ip,token,is_on_leave,is_cocurricular_leave,"
//			+ "att_date,att_id,hourshe_id,date_and_time,att_type_id,user_id,is_tt,version,substitue_class,work_diary_activity_id,total_ours,remarks) " +
//			"VALUES (?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?)";
	
	// UPDATED - INSERT QUERY 
	private static String INSERT_QUERY = 
		    "INSERT INTO attendance_data(" +
		    "org_id,room_id,subject_id,teacher_id," +
		    "start_date,start_time,end_date,end_time," +
		    "student_id,is_recong_student,is_active," +
		    "data_from,period_id,period_name,class_id," +
		    "class_name,subject_name,reg_no,student_name," +
		    "teacher_name,req_received_time,meg_box_ip," +
		    "token,is_on_leave,is_cocurricular_leave," +
		    "att_date,att_id,hourshe_id,date_and_time," +
		    "att_type_id,user_id,is_tt,version," +
		    "substitue_class,work_diary_activity_id," +
		    "total_ours,remarks," +
		    "camera_ip) " +         // ← NEW column added
		    "VALUES (?,?,?,?,?,?,?,?,?,?,?," +
		    "?,?,?,?,?,?,?,?,?,?,?,?,?,?," +
		    "?,?,?,?,?,?,?,?,?,?,?,?,?)"; // ← 38 values now
	
	@Override
	//@Transactional
	public int[][] saveAttendance(List<AttendanceReqestModel> attendanceReqList, int size) {
		System.out.println("middleWareJDBC"+middleWareJDBC);
		int[][] insertCount = middleWareJDBC.batchUpdate(INSERT_QUERY, attendanceReqList, size, 
				new ParameterizedPreparedStatementSetter<AttendanceReqestModel>() {
			@Override
			public void setValues(PreparedStatement ps, AttendanceReqestModel model) throws SQLException {
				ps.setString(1, model.getOrgId());
				ps.setInt(2, Integer.parseInt(model.getRoomId()));
				ps.setInt(3, Integer.parseInt(model.getSubId()));
				ps.setString(4, model.getTeacherId());
				ps.setString(5, model.getStartDate());
				ps.setString(6, model.getStartTime());
				ps.setString(7, model.getEndDate());
				ps.setString(8, model.getEndTime());
				ps.setInt(9, Integer.parseInt(model.getStudentId()));
				ps.setInt(10, Integer.parseInt(model.getIsRecongStudent()));
				ps.setInt(11, 1);
				ps.setString(12, "AICAMERA");
				ps.setString(13, model.getPeriodId());
				ps.setString(14, model.getPeriodName());
				ps.setString(15, model.getClassId());
				ps.setString(16, model.getClassName());
				ps.setString(17, model.getSubName());
				ps.setString(18, model.getRegNo());
				ps.setString(19, model.getStudentName());
				ps.setString(20, model.getTeacherName());
				ps.setInt(21, model.getReqRecivedTime());
				ps.setString(22, model.getMegBoxIp());
				ps.setString(23, model.getToken());
				ps.setInt(24,Integer.parseInt(model.getIs_on_leave()));
				ps.setString(25, model.getIs_cocurricular_leave());
				ps.setString(26, model.getAttDate());
				ps.setString(27, model.getAttId());
				ps.setString(28, model.getHoursheld());
				ps.setString(29, model.getDateandtime());
				ps.setString(30, model.getAttTypeId());
				ps.setString(31, model.getUserId());
				ps.setString(32, model.getIsTT());
				ps.setString(33, model.getVersion());
				ps.setString(34, model.getSubstitueClass());
				ps.setString(35, model.getWorkDiaryActivityId());
				ps.setString(36, model.getTotalHours());
				ps.setString(37, model.getRemarks());
				ps.setString(38, model.getCameraIp());
				
			}
		});
		return insertCount;
	}
 
//	@Override
//	public List<StudentApkDtoResponse> getStudentAttendance(String roomId,String subjectId,
//			String startDate) {
//		DateTime dateTime = new DateTime();
//		String startTimeFilter =String.format("%02d",dateTime.getHourOfDay()) +":"+dateTime.getMinuteOfHour()+":00";
//		String MIDDLEWARE_APK_STUDNET_QUERY = "select * from attendance_data where subject_id ="+subjectId+ " and start_date="+"'"+startDate+"' and room_id="+roomId+" and "+"'"+startTimeFilter+"'"+" between start_time and end_time";
//		List<StudentApkDtoResponse> list = middleWareJDBC.query(MIDDLEWARE_APK_STUDNET_QUERY, new StudentMiddlewareMapperForAPK());
//		return list;
//	}
	// RoomId - cameraIp Modification
	@Override
	public List<StudentApkDtoResponse> getStudentAttendance(
	        String cameraIp,   // ← changed from roomId
	        String subjectId,
	        String startDate) {

	    DateTime dateTime = new DateTime();
	    String startTimeFilter = 
	            String.format("%02d",
	                dateTime.getHourOfDay()) +
	            ":" + dateTime.getMinuteOfHour() +
	            ":00";

	    String MIDDLEWARE_APK_STUDNET_QUERY = 
	            "select * from attendance_data " +
	            "where subject_id = " + subjectId +
	            " and start_date = '" + startDate + "'" +
	            " and camera_ip = '" + cameraIp + "'" + // ← NEW
	            " and '" + startTimeFilter + "'" +
	            " between start_time and end_time";

	    List<StudentApkDtoResponse> list = 
	            middleWareJDBC.query(
	                MIDDLEWARE_APK_STUDNET_QUERY,
	                new StudentMiddlewareMapperForAPK());
	    return list;
	}
	
	
	@Override
	public List<StudentApkDtoResponse> getStudentAttendanceBasedOnDate(String startDate) {
		DateTime dateTime = new DateTime();
		String startTimeFilter =String.format("%02d",dateTime.getHourOfDay()) +":"+dateTime.getMinuteOfHour()+":00";
		String MIDDLEWARE_APK_STUDNET_QUERY = "select * from attendance_data where start_date="+"'"+startDate+"' and "+"'"+startTimeFilter+"'"+" between start_time and end_time";
		List<StudentApkDtoResponse> list = middleWareJDBC.query(MIDDLEWARE_APK_STUDNET_QUERY, new StudentMiddlewareMapperForAPK());
		return list;
	}
	
	
//	@Override
//	public List<StudentApkDtoResponse> getStudentAttendanceAndTokenBasedOnDate(String startDate) {
//		DateTime dateTime = new DateTime();
//		String startTimeFilterForHr =String.format("%02d",dateTime.getHourOfDay());
//		String startTimeFilterForMint =String.format("%02d",dateTime.getMinuteOfHour())+":00";
//		String completeStartTimeFilter = startTimeFilterForHr+":"+startTimeFilterForMint;
//		String MIDDLEWARE_APK_STUDNET_QUERY = "select ANY_VALUE(id) AS id,\r\n" + 
//				"    ANY_VALUE(org_id) AS org_id,\r\n" + 
//				"    room_id,\r\n" + 
//				"    ANY_VALUE(period_id) AS period_id,\r\n" + 
//				"    ANY_VALUE(subject_id) AS subject_id,\r\n" + 
//				"    ANY_VALUE(teacher_id) AS teacher_id,\r\n" + 
//				"    ANY_VALUE(start_date) AS start_date,\r\n" + 
//				"    start_time,\r\n" + 
//				"    ANY_VALUE(end_date) AS end_date,\r\n" + 
//				"    ANY_VALUE(end_time) AS end_time,\r\n" + 
//				"    ANY_VALUE(student_id) AS student_id,\r\n" + 
//				"    ANY_VALUE(is_active) AS is_active,\r\n" + 
//				"    ANY_VALUE(is_recong_student) AS is_recong_student,\r\n" + 
//				"    ANY_VALUE(data_from) AS data_from,\r\n" + 
//				"    ANY_VALUE(class_id) AS class_id,\r\n" + 
//				"    ANY_VALUE(teacher_name) AS teacher_name,\r\n" + 
//				"    ANY_VALUE(class_name) AS class_name,\r\n" + 
//				"    ANY_VALUE(period_name) AS period_name,\r\n" + 
//				"    ANY_VALUE(student_name) AS student_name,\r\n" + 
//				"    ANY_VALUE(subject_name) AS subject_name,\r\n" + 
//				"    ANY_VALUE(reg_no) AS reg_no,\r\n" + 
//				"    ANY_VALUE(req_received_time) AS req_received_time,\r\n" + 
//				"    ANY_VALUE(meg_box_ip) AS meg_box_ip,\r\n" + 
//				"    token,\r\n" + 
//				"    ANY_VALUE(is_on_leave) AS is_on_leave,\r\n" + 
//				"    ANY_VALUE(is_cocurricular_leave) AS is_cocurricular_leave,\r\n" + 
//				"    ANY_VALUE(att_date) AS att_date,\r\n" + 
//				"    ANY_VALUE(att_id) AS att_id,\r\n" + 
//				"    ANY_VALUE(hourshe_id) AS hourshe_id,\r\n" + 
//				"    ANY_VALUE(date_and_time) AS date_and_time,\r\n" + 
//				"    ANY_VALUE(att_type_id) AS att_type_id,\r\n" + 
//				"    ANY_VALUE(user_id) AS user_id,\r\n" + 
//				"    ANY_VALUE(is_tt) AS is_tt,\r\n" + 
//				"    ANY_VALUE(version) AS version,\r\n" + 
//				"    ANY_VALUE(substitue_class) AS substitue_class,\r\n" + 
//				"    ANY_VALUE(work_diary_activity_id) AS work_diary_activity_id,\r\n" + 
//				"    ANY_VALUE(total_ours) AS total_ours,\r\n" + 
//				"    ANY_VALUE(remarks) AS remarks from attendance_data where start_date="+"'"+startDate+"' and "+"'"+completeStartTimeFilter+"'"+" between start_time and end_time group by start_time,room_id,token";
//		List<StudentApkDtoResponse> list = middleWareJDBC.query(MIDDLEWARE_APK_STUDNET_QUERY, new StudentMiddlewareMapperForAPK());
//		return list;
//	}
	@Override
	public List<StudentApkDtoResponse>
	        getStudentAttendanceAndTokenBasedOnDate(
	                String startDate) {

	    DateTime dateTime = new DateTime();
	    String startTimeFilterForHr =
	            String.format("%02d",
	                dateTime.getHourOfDay());
	    String startTimeFilterForMint =
	            String.format("%02d",
	                dateTime.getMinuteOfHour()) + ":00";
	    String completeStartTimeFilter =
	            startTimeFilterForHr + ":" +
	            startTimeFilterForMint;

	    String MIDDLEWARE_APK_STUDNET_QUERY =
	            "select ANY_VALUE(id) AS id,\n" +
	            "    ANY_VALUE(org_id) AS org_id,\n" +
	            "    ANY_VALUE(room_id) AS room_id,\n" +   // ← FIXED: wrapped in ANY_VALUE
	            "    camera_ip,\n" +
	            "    ANY_VALUE(period_id) AS period_id,\n" +
	            "    ANY_VALUE(subject_id) AS subject_id,\n" +
	            "    ANY_VALUE(teacher_id) AS teacher_id,\n" +
	            "    ANY_VALUE(start_date) AS start_date,\n" +
	            "    start_time,\n" +
	            "    ANY_VALUE(end_date) AS end_date,\n" +
	            "    ANY_VALUE(end_time) AS end_time,\n" +
	            "    ANY_VALUE(student_id) AS student_id,\n" +
	            "    ANY_VALUE(is_active) AS is_active,\n" +
	            "    ANY_VALUE(is_recong_student) AS is_recong_student,\n" +
	            "    ANY_VALUE(data_from) AS data_from,\n" +
	            "    ANY_VALUE(class_id) AS class_id,\n" +
	            "    ANY_VALUE(teacher_name) AS teacher_name,\n" +
	            "    ANY_VALUE(class_name) AS class_name,\n" +
	            "    ANY_VALUE(period_name) AS period_name,\n" +
	            "    ANY_VALUE(student_name) AS student_name,\n" +
	            "    ANY_VALUE(subject_name) AS subject_name,\n" +
	            "    ANY_VALUE(reg_no) AS reg_no,\n" +
	            "    ANY_VALUE(req_received_time) AS req_received_time,\n" +
	            "    ANY_VALUE(meg_box_ip) AS meg_box_ip,\n" +
	            "    token,\n" +
	            "    ANY_VALUE(is_on_leave) AS is_on_leave,\n" +
	            "    ANY_VALUE(is_cocurricular_leave) AS is_cocurricular_leave,\n" +
	            "    ANY_VALUE(att_date) AS att_date,\n" +
	            "    ANY_VALUE(att_id) AS att_id,\n" +
	            "    ANY_VALUE(hourshe_id) AS hourshe_id,\n" +
	            "    ANY_VALUE(date_and_time) AS date_and_time,\n" +
	            "    ANY_VALUE(att_type_id) AS att_type_id,\n" +
	            "    ANY_VALUE(user_id) AS user_id,\n" +
	            "    ANY_VALUE(is_tt) AS is_tt,\n" +
	            "    ANY_VALUE(version) AS version,\n" +
	            "    ANY_VALUE(substitue_class) AS substitue_class,\n" +
	            "    ANY_VALUE(work_diary_activity_id) AS work_diary_activity_id,\n" +
	            "    ANY_VALUE(total_ours) AS total_ours,\n" +
	            "    ANY_VALUE(remarks) AS remarks " +
	            "from attendance_data " +
	            "where start_date = '" + startDate + "'" +
	            " and '" + completeStartTimeFilter + "'" +
	            " between start_time and end_time " +
	            "group by start_time, camera_ip, token";

	    List<StudentApkDtoResponse> list =
	            middleWareJDBC.query(
	                MIDDLEWARE_APK_STUDNET_QUERY,
	                new StudentMiddlewareMapperForAPK());
	    return list;
	}
	
	@Override
	public List<StudentApkDtoResponse> getStudentAttendanceFromCurrentDatetoLastOneWeek() throws Exception{
		
		Date dateIn = new Date(); // Current date
		SimpleDateFormat sdf1 = new SimpleDateFormat("dd/MM/yyyy");
		String dateStart = sdf1.format(dateIn);
        Calendar cal = Calendar.getInstance();
        cal.setTime(dateIn);
        cal.add(Calendar.DAY_OF_MONTH, -2); // Subtract 6 days
        Date before6Days = cal.getTime();
        String dateEnd = sdf1.format(before6Days);
		
		  String MIDDLEWARE_APK_STUDNET_QUERY =
		 "SELECT ANY_VALUE(id) AS id,ANY_VALUE(start_date) AS start_date,ANY_VALUE(date_and_time) AS date_and_time,\r\n" + 
		 "room_id,ANY_VALUE(subject_name) AS subject_name,start_time,subject_id,ANY_VALUE(meg_box_ip) AS meg_box_ip FROM attendance_data  \r\n"
		 + "WHERE start_date BETWEEN '"+dateEnd+"' AND '"
		 +dateStart+"' group by subject_id,room_id, start_time\r\n" +
		"ORDER BY start_date DESC, start_time ASC";
		
//        String MIDDLEWARE_APK_STUDNET_QUERY = "SELECT id, start_date, date_and_time, room_id, subject_name, start_time, subject_id, meg_box_ip FROM attendance_data  \r\n" + 
//        		"WHERE start_date BETWEEN '03/02/2025' AND '26/01/2025' group by subject_id,room_id, start_time\r\n" + 
//        		"ORDER BY start_date DESC, start_time ASC";
		List<StudentApkDtoResponse> list = middleWareJDBC.query(MIDDLEWARE_APK_STUDNET_QUERY, new StudentAttendanceWeekDataMapper());
		return list;
	}
	private static String INSERT_QUERY_NEW =
		    "INSERT INTO attendance_data(" +
		    "start_date, start_time, end_date, end_time, " +
		    "student_id, is_recong_student, is_active, " +
		    "data_from, reg_no, student_name, " +
		    "req_received_time, meg_box_ip, " +
		    "camera_ip, token, date_and_time, group_name) " +
		    "VALUES (?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?)";

	@Override
	public int[][] saveAttendanceNew(
	        List<AttendanceReqestModel> attendanceReqList,
	        int size) {
	    int[][] insertCount = middleWareJDBC.batchUpdate(
	            INSERT_QUERY_NEW, attendanceReqList, size,
	            new ParameterizedPreparedStatementSetter<AttendanceReqestModel>() {
	                @Override
	                public void setValues(PreparedStatement ps, AttendanceReqestModel model)
	                        throws SQLException {
	                    ps.setString(1, model.getStartDate());
	                    ps.setString(2, model.getStartTime());
	                    ps.setString(3, model.getEndDate());
	                    ps.setString(4, model.getEndTime());
	                    ps.setString(5, model.getStudentId());
	                    ps.setInt(6, Integer.parseInt(model.getIsRecongStudent()));
	                    ps.setInt(7, 1);
	                    ps.setString(8, "AICAMERA");
	                    ps.setString(9, model.getRegNo());
	                    ps.setString(10, model.getStudentName());
	                    ps.setInt(11, model.getReqRecivedTime());
	                    ps.setString(12, model.getMegBoxIp());
	                    ps.setString(13, model.getCameraIp());
	                    ps.setString(14, model.getToken());
	                    ps.setString(15, model.getDateandtime());
	                    ps.setString(16, model.getGroupName());// ← NEW
	                }
	            });
	    return insertCount;
	}
	@Override
	public List<StudentApkDtoResponse>
	        getStudentAttendanceByCamAndTime(
	                String cameraIp,
	                String startTime,
	                String startDate) {

	    String QUERY =
	            "select * from attendance_data " +
	            "where camera_ip = '" + cameraIp + "'" +
	            " and start_time = '" + startTime + "'" +
	            " and start_date = '" + startDate + "'" +
	            " limit 1";

	    List<StudentApkDtoResponse> list =
	            middleWareJDBC.query(
	                    QUERY,
	                    new StudentMiddlewareMapperForAPK());
	    return list;
	}
	@Override
	public void updateStudentStatusToUnknown(
	        List<String> studentIds,
	        String status) {

	    if (studentIds == null || studentIds.isEmpty()) {
	        return;
	    }

	    // Build IN clause
	    String inClause = studentIds.stream()
	            .map(id -> "'" + id + "'")
	            .collect(Collectors.joining(","));

	    String UPDATE_QUERY =
	            "UPDATE attendance_data " +
	            "SET is_recong_student = '" + status + "' " +
	            "WHERE student_id IN (" + inClause + ") " +
	            "AND start_date = '" +
	            new SimpleDateFormat("dd/MM/yyyy")
	                .format(new Date()) + "'";

	    middleWareJDBC.execute(UPDATE_QUERY);

	    lg.info("Updated {} students to status 300 (unknown)",
	            studentIds.size());
	}
	
	@Override
	public List<StudentApkDtoResponse> getStudentsByCamAndTime(
	        String cameraIp,
	        String startTime,
	        String startDate) {

	    String QUERY =
	            "select * from attendance_data " +
	            "where camera_ip = '" + cameraIp + "'" +
	            " and start_time = '" + startTime + "'" +
	            " and start_date = '" + startDate + "'";
	            // ← NO LIMIT — returns ALL students

	    return middleWareJDBC.query(
	            QUERY,
	            new StudentMiddlewareMapperForAPK());
	}
	private static final String INSERT_ABSENT_BACKUP =
	        "INSERT INTO absent_student_backup(" +
	        "status, file_name, file_content) " +
	        "VALUES (?, ?, ?)";

	@Override
	public void saveAbsentBackup(
	        String status,
	        String fileName,
	        String fileContent) {
	    try {
	        middleWareJDBC.update(
	                INSERT_ABSENT_BACKUP,
	                status,
	                fileName,
	                fileContent);
	        lg.info("Absent backup saved to DB: {}", fileName);
	    } catch (Exception e) {
	        lg.error("Failed to save absent backup: {}",
	                 e.getMessage());
	    }
	}
}

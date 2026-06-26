package com.iolite.kpcamera.attendance.dao;

import com.iolite.kpcamera.attendance.model.responses.FacesResponseDto;

public interface FacesDao {

	boolean saveStudentFacesDetails(FacesResponseDto facesResDto, String studentId, Integer orgId, String megBoxIp);

	void failedStudentImage(String studentId, String megBoxIp);
	
}

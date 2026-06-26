package com.iolite.kpcamera.attendance.service;

import java.util.List;

import com.iolite.kpcamera.attendance.model.RoomIpInbound;
import com.iolite.kpcamera.attendance.model.outbound.RoomAndRoomDetailsOutbound;
import com.iolite.kpcamera.attendance.model.outbound.RoomIpOutbound;

public interface RoomIpService {

	boolean addRoomAndIp(RoomIpInbound roomIpInbound);

	List<RoomAndRoomDetailsOutbound> getAllRoomDetails() throws Exception;

	boolean updateRoomAndIp(Integer id, RoomIpInbound request);

	public RoomIpOutbound getRoom(RoomIpInbound roomIpInbound);
	
	boolean deleteRoomAndIp(Integer id);
	
	String getMegBoxIpByCameraIp(String cameraIp);
}

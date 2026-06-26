package com.iolite.kpcamera.attendance.service.impl;

import java.io.IOException;
import java.net.InetAddress;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

import com.iolite.kpcamera.attendance.configure.RestTemplateConfiguration;
import com.iolite.kpcamera.attendance.dao.RoomDao;
import com.iolite.kpcamera.attendance.mapper.RoomAndRoomDetailsMapperDto;
import com.iolite.kpcamera.attendance.model.CameraIpDetails;
import com.iolite.kpcamera.attendance.model.RoomIpInbound;
import com.iolite.kpcamera.attendance.model.outbound.RoomAndRoomDetailsOutbound;
import com.iolite.kpcamera.attendance.model.outbound.RoomIpOutbound;
import com.iolite.kpcamera.attendance.service.RoomIpService;

@Service
public class RoomIpServiceImpl implements RoomIpService{

	@Autowired
	RoomDao roomDao;
	
	@Value("${megbox.url}")
	private String megUrl;
	
	@Override
	public boolean addRoomAndIp(RoomIpInbound roomIpInbound) {
		//createGroup(roomIpInbound.getCameraIps());
		boolean isSaved = roomDao.saveRoom(roomIpInbound);
		return isSaved;
	}

	@Override
	public List<RoomAndRoomDetailsOutbound> getAllRoomDetails() throws Exception {
	    // Retrieve the room details from the DAO
	    List<RoomAndRoomDetailsMapperDto> roomIpOutbounds = roomDao.getRoomDetails();
	    Map<String, RoomAndRoomDetailsOutbound> outboundMap = new HashMap<>();

	    for (RoomAndRoomDetailsMapperDto mapper : roomIpOutbounds) {
	        // Check if Box IP is already present, otherwise compute it
	        RoomAndRoomDetailsOutbound bound = outboundMap.computeIfAbsent(mapper.getBoxIp(), ip -> {
	            RoomAndRoomDetailsOutbound newBound = new RoomAndRoomDetailsOutbound();
	            newBound.setIpAddress(mapper.getBoxIp());
	            newBound.setDescription(mapper.getDescription());
	            newBound.setId(mapper.getId());
	            newBound.setIsUp(isIpReachable(mapper.getBoxIp()));
	            newBound.setCameraIps(new ArrayList<>());
	            return newBound;
	        });

	        // Reuse previously computed reachability for the Box IP
	        boolean isCamIpReachable = isIpReachable(mapper.getCameraIp());

	        // Create Camera IP details and add to the list of Camera IPs
	        CameraIpDetails details = new CameraIpDetails();
	        details.setCameraIp(mapper.getCameraIp());
	        details.setFloor(mapper.getFloor());
	        details.setGroupName(mapper.getGroupName());
	        details.setIsUp(isCamIpReachable);
	        
	        // Add the camera details to the bound's camera list
	        bound.getCameraIps().add(details);
	    }

	    // Convert the map values to a list to return
	    return new ArrayList<>(outboundMap.values());
	}


    // Helper method to check if IP is reachable (same as the one provided earlier)
    private boolean isIpReachable(String ipAddress) {
        try {
            InetAddress inetAddress = InetAddress.getByName(ipAddress);
            return inetAddress.isReachable(1000);  // 5 seconds timeout
        } catch (IOException e) {
            System.out.println("Error checking IP: " + e.getMessage());
            return false;
        }
    }
    
	private void createGroup(List<CameraIpDetails> cameraIpDetails) {
		for (CameraIpDetails cameraAndGroup : cameraIpDetails) {
			RestTemplate restTemplate = RestTemplateConfiguration.getRestTemplate();
			Map<String, String> params = new HashMap<>();
			params.put("groupName", cameraAndGroup.getGroupName());
			restTemplate.postForEntity(megUrl + "faceGroups", params, String.class);
		}
	}


	@Override
	public boolean updateRoomAndIp(Integer id, RoomIpInbound request) {
	    // Fetch existing room details
	    List<RoomAndRoomDetailsMapperDto> roomIpOutbounds = roomDao.getRoomDetailsById(id);

	    // Map Box IPs to associated Camera IPs from DB
	    Map<String, List<String>> roomIpToCameraIpMap = roomIpOutbounds.stream()
	            .collect(Collectors.groupingBy(
	                    RoomAndRoomDetailsMapperDto::getBoxIp,
	                    Collectors.mapping(RoomAndRoomDetailsMapperDto::getGroupName, Collectors.toList())
	            ));

	    // Extract Camera IPs from the request
	    List<String> cameraIpsFromReq = request.getCameraIps().stream()
	            .map(CameraIpDetails::getGroupName)
	            .collect(Collectors.toList());

	    // Compare each entry from DB with the request
	    for (Map.Entry<String, List<String>> roomIpToCameraIps : roomIpToCameraIpMap.entrySet()) {
	        List<String> cameraIpsFromDB = roomIpToCameraIps.getValue();

	        // Check if both lists contain the same elements (ignoring order)
	        if (cameraIpsFromDB.size() == cameraIpsFromReq.size() &&
	                cameraIpsFromDB.containsAll(cameraIpsFromReq) &&
	                cameraIpsFromReq.containsAll(cameraIpsFromDB)) {

	            // If they match, delete existing records and insert new ones
	            if (roomDao.deleteRoomAndIp(id)) {
	                return roomDao.saveRoom(request);
	            }
	        }
	    }
	    
	    return false;
	}

	@Override
	public RoomIpOutbound getRoom(RoomIpInbound roomIpInbound) {
		RoomIpOutbound roomIpOutbound = roomDao.getRoom(roomIpInbound);
		return roomIpOutbound;
	}

	@Override
	public boolean deleteRoomAndIp(Integer id) {
		return roomDao.deleteRoomAndIp(id);
	}
	
	@Override
	public String getMegBoxIpByCameraIp(String cameraIp) {
	    return roomDao.getMegBoxIpByCameraIp(cameraIp);
	}

    
}

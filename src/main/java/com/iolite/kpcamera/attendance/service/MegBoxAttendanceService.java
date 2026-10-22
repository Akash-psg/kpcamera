package com.iolite.kpcamera.attendance.service;

import java.io.BufferedInputStream;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.io.InputStream;
import java.net.HttpURLConnection;
import java.net.URL;
import java.nio.file.Files;
import java.nio.file.StandardCopyOption;
import java.security.KeyManagementException;
import java.security.KeyStoreException;
import java.security.NoSuchAlgorithmException;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.time.Duration;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.TimeUnit;
import java.util.stream.Collectors;
import com.iolite.kpcamera.attendance.util.Base64Coder;
import java.io.FileOutputStream;

import org.apache.http.client.config.RequestConfig;
import org.apache.http.config.Registry;
import org.apache.http.config.RegistryBuilder;
import org.apache.http.conn.socket.ConnectionSocketFactory;
import org.apache.http.conn.socket.PlainConnectionSocketFactory;
import org.apache.http.conn.ssl.NoopHostnameVerifier;
import org.apache.http.conn.ssl.SSLConnectionSocketFactory;
import org.apache.http.conn.ssl.TrustSelfSignedStrategy;
import org.apache.http.impl.client.CloseableHttpClient;
import org.apache.http.impl.client.HttpClients;
import org.apache.http.impl.client.LaxRedirectStrategy;
import org.apache.http.impl.conn.PoolingHttpClientConnectionManager;
import org.apache.http.ssl.SSLContextBuilder;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.core.io.FileSystemResource;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.http.client.HttpComponentsClientHttpRequestFactory;
import org.springframework.http.converter.ByteArrayHttpMessageConverter;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;
import org.springframework.util.CollectionUtils;
import org.springframework.util.LinkedMultiValueMap;
import org.springframework.util.MultiValueMap;
import org.springframework.web.client.RestTemplate;

import com.amazonaws.auth.AWSStaticCredentialsProvider;
import com.amazonaws.auth.BasicAWSCredentials;
import com.amazonaws.services.s3.AmazonS3;
import com.amazonaws.services.s3.AmazonS3ClientBuilder;
import com.amazonaws.services.s3.model.ListObjectsV2Result;
import com.amazonaws.services.s3.model.S3Object;
import com.amazonaws.services.s3.model.S3ObjectSummary;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.JsonMappingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.iolite.kpcamera.attendance.configure.RestTemplateConfiguration;
import com.iolite.kpcamera.attendance.dao.AttendanceDao;
import com.iolite.kpcamera.attendance.dao.FacesDao;
import com.iolite.kpcamera.attendance.dao.GroupDetailsInKpDao;
import com.iolite.kpcamera.attendance.dao.MegboxStudentDetailsDao;
import com.iolite.kpcamera.attendance.dao.RoomDao;
import com.iolite.kpcamera.attendance.dao.StudentDetailsDao;
import com.iolite.kpcamera.attendance.model.AttendanceReqestModel;
import com.iolite.kpcamera.attendance.model.FCMReqDto;
import com.iolite.kpcamera.attendance.model.OnDemandUploadRequest;
import com.iolite.kpcamera.attendance.model.RoomReqDto;
import com.iolite.kpcamera.attendance.model.StudentAttendance;
import com.iolite.kpcamera.attendance.model.StudentDto;
import com.iolite.kpcamera.attendance.model.responses.FacesResponseDto;
import com.iolite.kpcamera.attendance.model.responses.MultiAlgVideosResponseModel;
import com.iolite.kpcamera.attendance.model.responses.OnDemandUploadResponse;
import com.iolite.kpcamera.attendance.model.responses.PreSignedUrlDto;
import com.iolite.kpcamera.attendance.model.responses.RecongnizedStudentResDto;
import com.iolite.kpcamera.attendance.model.responses.StudentApkDtoResponse;
import com.iolite.kpcamera.attendance.model.responses.StudentAttendanceResponse;
import com.iolite.kpcamera.attendance.model.responses.Video;
import com.iolite.kpcamera.attendance.util.CameraUtils;
import com.iolite.kpcamera.attendance.util.OrganizationUtil;
import com.opencsv.CSVWriter;
import javax.annotation.PostConstruct;
import javax.annotation.PreDestroy;



import io.grpc.netty.shaded.io.netty.handler.timeout.TimeoutException;

@Service
public class MegBoxAttendanceService {

	static final Logger lg = LoggerFactory.getLogger(MegBoxAttendanceService.class);


	@Autowired
	private StudentDetailsDao studentDao;
	
	@Value("${upload.mode:S3_PRESIGNED}")
	private String uploadMode;

	@Value("${aws.access.key}")
	private String awsAccessKey;

	@Value("${aws.secret.key}")
	private String awsSecretKey;

	@Value("${aws.region}")
	private String awsRegion;

	@Value("${aws.s3.bucket.name}")
	private String awsS3BucketName;
	
	@Value("${local.image.folder.path:}")
	private String localImageFolderPath;

	@Value("${use.local.images:false}")
	private Boolean useLocalImages;

	

	@Autowired
	private ObjectMapper mapper;
	
	@Value("${clientApiUrl}")
	private String clientApiUrl;

	@Autowired
	private RestTemplate restTemplate;

	@Value("${megbox.image.url}")
	private String megImageUrl;

	@Value("${megbox.url}")
	private String megUrl;
	
	@Value("${csv.file.path}")
	private String csvFilePath;
	
	
	@Value("${megbox.image.upload:true}")
	private Boolean isUploadImages;

	@Autowired
	GroupDetailsInKpDao groupDetailsInKpDao;

	@Autowired
	private RoomDao roomDao;

	@Autowired
	MegboxStudentDetailsDao megboxStudentDetailsDao;

	@Autowired
	AttendanceDao dao;
	
	@Autowired
	StudentService service;
	
	@Autowired
	AttendanceService attendanceService;
	
	@Autowired
	FacesDao facesDao;
	
	@Value("${megbox.image.upload.is.regNo:true}")
	private Boolean isRegNo;
	
	

	private static final String STATUS_CODE_200 = "200";
	private static final String STATUS_CODE_201 = "201";
	private static String orgId = "ChristMysore";
	private static String megBoxProtocal = "http://";
	private static String megBoxUri= "/v1/MEGBOX/";
	
	private CloseableHttpClient megBoxHttpClient;
	private RestTemplate megBoxBindingTemplate;

	
	
	@Scheduled(cron = OrganizationUtil.ORG_SCHEDULE_TIME)
	public void uploadFaces() throws Exception {
	    lg.info("🟢 Starting uploadFaces at {}", LocalTime.now());
	    lg.info("uploadMode: {}", uploadMode);

	    attendanceService.clearDuplicateData();
	    if (!Boolean.TRUE.equals(isUploadImages)) return;

	    // ← LOCAL: scan folder directly, no DB
	    if ("LOCAL".equalsIgnoreCase(uploadMode)) {
	        uploadFacesFromLocalFolder();
	        return;
	    }

	    // ← S3_SCAN: scan bucket directly, no DB
	    if ("S3_SCAN".equalsIgnoreCase(uploadMode)) {
	        uploadFacesFromS3Scan();
	        return;
	    }

	    // ← S3_PRESIGNED: only this mode uses DB query
	    final LocalTime cutoffTime = LocalTime.of(9, 0);
	    final LocalDateTime cutoffDateTime =
	            LocalDateTime.now().with(cutoffTime);
	    final LocalDateTime now = LocalDateTime.now();
	    final LocalDateTime endTime =
	            now.isAfter(cutoffDateTime)
	            ? cutoffDateTime.plusDays(1)
	            : cutoffDateTime;
	    final long maxDurationMillis =
	            Duration.between(now, endTime).toMillis();
	    long startTime = System.currentTimeMillis();

	    List<StudentDto> studentDtoList =
	            studentDao.getStudentDetails();
	    if (studentDtoList.isEmpty()) {
	        lg.warn("No students to process");
	        return;
	    }

	    List<String> validStudentIds =
	            getStudentDetailsForAPK();

	    String[] megBoxIps = megImageUrl.split(",");
	    List<String[]> failedStudentList =
	            Collections.synchronizedList(new ArrayList<>());
	    List<String[]> alreadyExistsList =
	            Collections.synchronizedList(new ArrayList<>());

	    CloseableHttpClient httpClient = createHttpClient();
	    int threads = Math.min(25, studentDtoList.size());
	    ExecutorService executor =
	            Executors.newFixedThreadPool(threads);
	    List<CompletableFuture<Void>> futures =
	            new ArrayList<>();

	    try {
	        for (String ip : megBoxIps) {
	            Map<String, String> studentTokenDetails =
	                    megboxStudentDetailsDao
	                    .getMegboxStudentDetails(orgId, ip);

	            List<StudentDto> filteredStudents =
	                    studentDtoList.parallelStream()
	                    .filter(dto -> {
	                        boolean notUploaded =
	                                !studentTokenDetails
	                                .containsKey(
	                                    dto.getOrg_personnel_id());
	                        if (!notUploaded) {
	                            alreadyExistsList.add(
	                                new String[]{
	                                    dto.getOrg_personnel_id(),
	                                    "ALREADY_EXISTS",
	                                    "Face already uploaded"
	                                });
	                        }
	                        return notUploaded;
	                    })
	                    .collect(Collectors.toList());

	            for (StudentDto dto : filteredStudents) {
	                if (System.currentTimeMillis() - startTime
	                        >= maxDurationMillis) {
	                    lg.warn("⏹️ Reached time limit.");
	                    break;
	                }
	                futures.add(CompletableFuture.runAsync(() -> {
	                    try {
	                        processStudentForUpload(
	                                dto,
	                                validStudentIds,
	                                failedStudentList,
	                                httpClient,
	                                ip);
	                    } catch (Exception ex) {
	                        lg.error("❌ Upload failed: {}",
	                                dto.getOrg_personnel_id(), ex);
	                    }
	                }, executor));
	            }
	        }

	        CompletableFuture.allOf(
	                futures.toArray(new CompletableFuture[0]))
	                .get(maxDurationMillis, TimeUnit.MILLISECONDS);

	    } catch (TimeoutException timeoutEx) {
	        lg.warn("⏱️ Task exceeded time limit.");
	    } catch (Exception e) {
	        lg.error("🔥 Unexpected error in uploadFaces", e);
	    } finally {
	        shutdownExecutor(executor);
	        try { httpClient.close(); }
	        catch (IOException e) {
	            lg.error("❌ Error closing HttpClient", e);
	        }
	    }

	    lg.info("✅ Finished. Total attempted: {}",
	            futures.size());

	    List<String[]> allCsvEntries = new ArrayList<>();
	    allCsvEntries.addAll(failedStudentList);
	    allCsvEntries.addAll(alreadyExistsList);
	    convertFailedStudentCSVFIle(allCsvEntries);
	}
	private void uploadFacesFromS3Scan() {

	    lg.info("🟢 S3_SCAN mode — scanning bucket: {}",
	            awsS3BucketName);

	    List<String[]> failedStudentList =
	            Collections.synchronizedList(new ArrayList<>());
	    List<String[]> alreadyExistsList =
	            Collections.synchronizedList(new ArrayList<>());

	    try {
	        AmazonS3 s3Client = AmazonS3ClientBuilder
	                .standard()
	                .withRegion(awsRegion)
	                .withCredentials(
	                    new AWSStaticCredentialsProvider(
	                        new BasicAWSCredentials(
	                            awsAccessKey, awsSecretKey)))
	                .build();

	        ListObjectsV2Result result =
	                s3Client.listObjectsV2(awsS3BucketName);
	        List<S3ObjectSummary> objects =
	                result.getObjectSummaries();

	        lg.info("Found {} files in S3 bucket",
	                objects.size());

	        if (objects.isEmpty()) {
	            lg.warn("No files found in S3 bucket: {}",
	                    awsS3BucketName);
	            return;
	        }

	        String[] megBoxIps = megImageUrl.split(",");

	        int threads = Math.min(25, objects.size());
	        ExecutorService executor =
	                Executors.newFixedThreadPool(threads);
	        List<CompletableFuture<Void>> futures =
	                new ArrayList<>();

	        try {
	            for (String ip : megBoxIps) {
	                Map<String, String> studentTokenDetails =
	                        megboxStudentDetailsDao
	                        .getMegboxStudentDetails(orgId, ip);

	                for (S3ObjectSummary obj : objects) {

	                    String key = obj.getKey();
	                    String uuid = key.contains(".")
	                            ? key.substring(0,
	                                key.lastIndexOf("."))
	                            : key;

	                    if (studentTokenDetails
	                            .containsKey(uuid)) {
	                        lg.info("Already uploaded: {}", uuid);
	                        alreadyExistsList.add(new String[]{
	                            uuid,
	                            "ALREADY_EXISTS",
	                            "Face already uploaded to MegBox"
	                        });
	                        continue;
	                    }

	                    final String finalUuid = uuid;
	                    final String finalIp = ip;

	                    futures.add(CompletableFuture
	                            .runAsync(() -> {
	                        long threadStart =
	                                System.currentTimeMillis();
	                        try {
	                            uploadFaceFromS3Object(
	                                    s3Client,
	                                    key,
	                                    finalUuid,
	                                    failedStudentList,
	                                    finalIp);
	                        } catch (Exception ex) {
	                            lg.error("❌ S3 upload failed: {}",
	                                    finalUuid, ex);
	                        } finally {
	                            long timeTaken =
	                                    System.currentTimeMillis()
	                                    - threadStart;
	                            if (timeTaken > 2000) {
	                                lg.warn("⚠️ Slow: {} took {}ms",
	                                        finalUuid, timeTaken);
	                            }
	                        }
	                    }, executor));
	                }
	            }

	            // ← FIXED: simple get() no timeout
	            CompletableFuture.allOf(
	                    futures.toArray(
	                        new CompletableFuture[0]))
	                    .get();

	        } catch (Exception e) {
	            lg.error("🔥 Error in S3 scan upload", e);
	        } finally {
	            shutdownExecutor(executor);
	        }

	        lg.info("✅ S3 scan finished. Total attempted: {}",
	                futures.size());

	    } catch (Exception e) {
	        lg.error("🔥 Error initializing S3 client", e);
	    }

	    List<String[]> allCsvEntries = new ArrayList<>();
	    allCsvEntries.addAll(failedStudentList);
	    allCsvEntries.addAll(alreadyExistsList);
	    convertFailedStudentCSVFIle(allCsvEntries);
	}
	private void uploadFaceFromS3Object(
	        AmazonS3 s3Client,
	        String s3Key,
	        String uuid,
	        List<String[]> failedStudentList,
	        String megBoxIp) {

	    lg.info("Uploading from S3 for student: {}", uuid);
	    File tempFile = null;

	    try {
	        // Download from S3
	        S3Object s3Object =
	                s3Client.getObject(
	                        awsS3BucketName, s3Key);

	        tempFile = File.createTempFile(
	                "s3_" + uuid, ".jpg");

	        try (InputStream inputStream =
	                s3Object.getObjectContent()) {
	            Files.copy(
	                inputStream,
	                tempFile.toPath(),
	                StandardCopyOption.REPLACE_EXISTING);
	        }

	        // Upload to MegBox
	        RestTemplate restTemplate =
	                RestTemplateConfiguration.getRestTemplate();
	        restTemplate.getMessageConverters()
	                .add(new ByteArrayHttpMessageConverter());

	        MultiValueMap<String, Object> body =
	                new LinkedMultiValueMap<>();
	        body.add("image",
	                new FileSystemResource(tempFile));
	        body.add("description", uuid);

	        HttpHeaders headers = new HttpHeaders();
	        headers.setContentType(
	                MediaType.MULTIPART_FORM_DATA);

	        HttpEntity<MultiValueMap<String, Object>>
	                requestEntity =
	                new HttpEntity<>(body, headers);

	        ResponseEntity<String> response =
	                restTemplate.exchange(
	                        "https://" + megBoxIp +
	                        "/v1/MEGBOX/faces",
	                        HttpMethod.POST,
	                        requestEntity,
	                        String.class);

	        FacesResponseDto facesResDto =
	                new ObjectMapper().readValue(
	                        response.getBody(),
	                        FacesResponseDto.class);

	        saveFaces(facesResDto, uuid, megBoxIp);

	        lg.info("✅ S3 upload success for student: {}",
	                uuid);

	    } catch (Exception e) {
	        lg.error("S3 upload error for {}: {}",
	                uuid, e.getMessage());
	        failedStudentList.add(new String[]{
	            uuid,
	            "FAILED",
	            e.getMessage()
	        });
	    } finally {
	        if (tempFile != null && tempFile.exists()) {
	            tempFile.delete();
	        }
	    }
	}

	private CloseableHttpClient createHttpClient() throws Exception {
	    // Build SSL context to trust self-signed certs
	    SSLContextBuilder builder = new SSLContextBuilder();
	    builder.loadTrustMaterial(null, new TrustSelfSignedStrategy());

	    // Create socket factory using SSL context and hostname verifier
	    SSLConnectionSocketFactory sslSocketFactory = new SSLConnectionSocketFactory(
	            builder.build(), NoopHostnameVerifier.INSTANCE);

	    // Register both HTTP and HTTPS
	    Registry<ConnectionSocketFactory> registry = RegistryBuilder.<ConnectionSocketFactory>create()
	            .register("http", new PlainConnectionSocketFactory())
	            .register("https", sslSocketFactory)
	            .build();

	    // Connection manager with pooling
	    PoolingHttpClientConnectionManager cm = new PoolingHttpClientConnectionManager(registry);
	    cm.setMaxTotal(200); // Increased max for more throughput
	    cm.setDefaultMaxPerRoute(50); // Higher per route

	    // Configure timeouts
	    RequestConfig config = RequestConfig.custom()
	            .setConnectTimeout(30 * 1000)
	            .setConnectionRequestTimeout(30 * 1000)
	            .setSocketTimeout(60 * 1000)
	            .build();

	    return HttpClients.custom()
	            .setSSLSocketFactory(sslSocketFactory)
	            .setConnectionManager(cm)
	            .setDefaultRequestConfig(config)
	            .setRedirectStrategy(new LaxRedirectStrategy())
	            .build();
	}




	private List<String[]> initializeFailedStudentList() {
	    List<String[]> failedStudentList = new ArrayList<>();
	    String[] header = {"STUDENT ID", "STUDENT NAME", "REGISTRATION NUMBER", "REASON"};
	    failedStudentList.add(header);
	    return failedStudentList;
	}

	private List<String> getStudentDetailsForAPK() {
	    return studentDao.getStudentDetailsforAPK().stream()
	        .map(StudentApkDtoResponse::getStudentId)
	        .collect(Collectors.toList());
	}

	private void processStudentForUpload(StudentDto dto, List<String> validStudentIds,
	        List<String[]> failedStudentList, CloseableHttpClient httpClient, String megBoxIp) {
	    String studentId = dto.getOrg_personnel_id();
	    try {
	        lg.info("Uploading for student: ({})", studentId);
	        uploadFacesThread(dto, validStudentIds, failedStudentList, httpClient, megBoxIp);
	    } catch (Exception e) {
	        lg.error("Exception while processing student {}: {}", studentId, e.getMessage());
	        failedStudentList.add(new String[] { studentId, "Processing error: " + e.getMessage() });
	    }
	}
	
	//code added by ajay
	
		private String getPreSignedUrl(String identifier) {
	
		    try {
	
		        PreSignedUrlDto response =
		                restTemplate.getForObject(
		                        clientApiUrl + "?id=" + identifier,
		                        PreSignedUrlDto.class);
	
		        if (response != null) {
		            return response.getPreSignedUrl();
		        }
	
		    } catch (Exception e) {
	
		        lg.error("Error getting pre-signed URL for {}",
		                identifier, e);
		    }
	
		    return null;
		}


		private void uploadFacesThread(
		        StudentDto dto,
		        List<String> validStudentIds,
		        List<String[]> failedStudentList,
		        CloseableHttpClient httpClient,
		        String megBoxIp) {

		    String orgPersonnelId = dto.getOrg_personnel_id();

		    if (!validStudentIds.contains(orgPersonnelId)) {
		        failedStudentList.add(new String[]{
		            orgPersonnelId,
		            "FAILED",                        // ← STATUS
		            "Invalid student mapping"
		        });
		        return;
		    }

		    try {
		        RestTemplate restTemplate =
		                RestTemplateConfiguration.getRestTemplate();
		        restTemplate.getMessageConverters()
		                .add(new ByteArrayHttpMessageConverter());

		        MultiValueMap<String, Object> body =
		                new LinkedMultiValueMap<>();
		        File tempImageFile = null;
		        HttpHeaders headers = new HttpHeaders();
		        headers.setContentType(MediaType.MULTIPART_FORM_DATA);
		        ResponseEntity<String> response = null;

		        try {
		            String preSignedUrl =
		                    getPreSignedUrl(orgPersonnelId);

		            if (preSignedUrl == null
		                    || preSignedUrl.isEmpty()) {
		                failedStudentList.add(new String[]{
		                    orgPersonnelId,
		                    "FAILED",                // ← STATUS
		                    "PreSigned URL not received"
		                });
		                return;
		            }

		            try {
		                tempImageFile =
		                        downloadImageFromPreSignedUrl(
		                                preSignedUrl,
		                                orgPersonnelId);
		            } catch (Exception ex) {
		                lg.error("Failed to download image " +
		                         "from S3 for student: {}",
		                         orgPersonnelId, ex);
		                failedStudentList.add(new String[]{
		                    orgPersonnelId,
		                    "FAILED",                // ← STATUS
		                    "S3 Image Download Failed: "
		                        + ex.getMessage()
		                });
		                return;
		            }

		            body.add("image",
		                    new FileSystemResource(tempImageFile));
		            body.add("description", orgPersonnelId);
		            HttpEntity<MultiValueMap<String, Object>>
		                    requestEntity =
		                    new HttpEntity<>(body, headers);

		            response = restTemplate.exchange(
		                    "https://" + megBoxIp +
		                    "/v1/MEGBOX/faces",
		                    HttpMethod.POST,
		                    requestEntity,
		                    String.class);

		        } finally {
		            if (tempImageFile != null
		                    && tempImageFile.exists()) {
		                tempImageFile.delete();
		            }
		        }

		        FacesResponseDto facesResDto =
		                new ObjectMapper().readValue(
		                        response.getBody(),
		                        FacesResponseDto.class);
		        saveFaces(facesResDto, orgPersonnelId, megBoxIp);
		        lg.info("Successfully uploaded for student: {}",
		                orgPersonnelId);

		    } catch (Exception e) {
		        lg.error("Upload error for student {}: {}",
		                 orgPersonnelId, e.getMessage());
		        failedStudentList.add(new String[]{
		            orgPersonnelId,
		            "FAILED",                        // ← STATUS
		            e.getMessage()
		        });
		    }
		}
	private void processCommand() {
        try {
            Thread.sleep(200);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
    }
	
	private void saveFaces(FacesResponseDto facesResDto, String orgPersonnelId, String megBoxIp) {
		synchronized (this) {
	        facesDao.saveStudentFacesDetails(facesResDto, orgPersonnelId, 1, megBoxIp);
	    }
	}


	private void shutdownExecutor(ExecutorService executor) throws InterruptedException {
	    executor.shutdown();
	    if (!executor.awaitTermination(60, TimeUnit.SECONDS)) {
	        executor.shutdownNow();
	        if (!executor.awaitTermination(60, TimeUnit.SECONDS)) {
	            lg.error("Executor service did not terminate in the specified time.");
	        }
	    }
	}
	//Code added by chandra

	private File downloadImageFromPreSignedUrl(
	        String preSignedUrl,
	        String orgPersonnelId) throws IOException {

	    URL url = new URL(preSignedUrl);

	    HttpURLConnection connection =
	            (HttpURLConnection) url.openConnection();

	    try {

	        connection.setRequestMethod("GET");

	        connection.setConnectTimeout(10000);

	        connection.setReadTimeout(30000);

	        int statusCode = connection.getResponseCode();

	        if (statusCode != HttpURLConnection.HTTP_OK) {

	            throw new IOException(
	                    "Failed to download image for student: "
	                            + orgPersonnelId
	                            + " | HTTP Status: "
	                            + statusCode);
	        }

	        try (InputStream inputStream =
	                     new BufferedInputStream(
	                             connection.getInputStream())) {

	            File tempFile =
	                    File.createTempFile(
	                            "student_" + orgPersonnelId,
	                            ".jpg");

	            Files.copy(
	                    inputStream,
	                    tempFile.toPath(),
	                    StandardCopyOption.REPLACE_EXISTING);

	            return tempFile;
	        }

	    } finally {

	        connection.disconnect();
	    }
	}


	private void convertFailedStudentCSVFIle(List<String[]> failedStudentList) {
	    File directory = new File(csvFilePath + "failedUploadStudent");
	    File file = new File(csvFilePath + "\\failedUploadStudent\\" + "failedUploadStudentList.csv");

	    try {
	        if (!directory.exists()) {
	            directory.mkdirs();
	            file.createNewFile();
	        }

	        try (CSVWriter writer = new CSVWriter(new FileWriter(file))) {
	            writer.writeAll(failedStudentList);
	        }
	    } catch (IOException e) {
	        lg.error("Exception occurred in convertFailedStudentCSVFIle", e);
	    }
	}

	//@Scheduled(cron = OrganizationUtil.ORG_GROUP_CREATION_SCHEDULE_TIME)
//	public void createGroups() throws KeyManagementException, NoSuchAlgorithmException, KeyStoreException {
//		Integer orgId = 1;
//		ResponseEntity response = null;
//		PoolingHttpClientConnectionManager cm  = null;
//		try { 
//			List<RoomReqDto> dtoList = roomDao.getRoomMaster(orgId);
//			for(RoomReqDto dto : dtoList) {
//				SSLContextBuilder builder = new SSLContextBuilder();
//				builder.loadTrustMaterial(null, new TrustSelfSignedStrategy());
//				SSLConnectionSocketFactory sslConnectionSocketFactory = new SSLConnectionSocketFactory(builder.build(), NoopHostnameVerifier.INSTANCE);
//				Registry<ConnectionSocketFactory> registry = RegistryBuilder.<ConnectionSocketFactory>create()
//						.register("http", new PlainConnectionSocketFactory())
//						.register("https", sslConnectionSocketFactory)
//						.build();
//
//				cm = new PoolingHttpClientConnectionManager(registry);
//				cm.setMaxTotal(100);
//				CloseableHttpClient httpclient = HttpClients.custom()
//						.setSSLSocketFactory(sslConnectionSocketFactory).setRedirectStrategy(new LaxRedirectStrategy())
//						.setConnectionManager(cm)
//						.build();
//
//				HttpComponentsClientHttpRequestFactory requestFactory =
//						new HttpComponentsClientHttpRequestFactory();
//
//				requestFactory.setHttpClient(httpclient);
//				restTemplate = new RestTemplate(requestFactory);
//				Map<String, String> params = new HashMap<>();
//				params.put("groupName", dto.getOrg_room_id());
//				response = restTemplate.postForEntity(megUrl+"faceGroups", params, String.class);
//			}
//		}catch (Exception e) {
//			lg.error("Occured in createGroups",e);
//		}
//		finally {
//			if(cm != null) {
//			cm.close();
//			}
//		}
//
//	}
	//ROOMID - CAMERA IP MODIFICATION
//	public boolean bindingFaceWithFaceGroups(Map<String,AttendanceReqestModel> StudentDetails, String megboxIp){
//		lg.info("Inside group creation");
//		boolean isBinded = false;
//		PoolingHttpClientConnectionManager cm = null;
//		try {
//			Map<Integer,String> studentTokenDetails  = megboxStudentDetailsDao.getMegboxStudentDetails(orgId, megboxIp);
//			if(!CollectionUtils.isEmpty(StudentDetails)) {
//			for(Map.Entry<String,AttendanceReqestModel> dto : StudentDetails.entrySet()) {
//				try {
//					if(studentTokenDetails.get(Integer.parseInt(dto.getValue().getStudentId())) != null && studentTokenDetails.containsKey(Integer.parseInt(dto.getValue().getStudentId()))) {
//						SSLContextBuilder builder = new SSLContextBuilder();
//						builder.loadTrustMaterial(null, new TrustSelfSignedStrategy());
//						SSLConnectionSocketFactory sslConnectionSocketFactory = new SSLConnectionSocketFactory(builder.build(), NoopHostnameVerifier.INSTANCE);
//						Registry<ConnectionSocketFactory> registry = RegistryBuilder.<ConnectionSocketFactory>create()
//								.register("http", new PlainConnectionSocketFactory())
//								.register("https", sslConnectionSocketFactory)
//								.build();
//
//						cm = new PoolingHttpClientConnectionManager(registry);
//						cm.setMaxTotal(100);
//						CloseableHttpClient httpclient = HttpClients.custom()
//								.setSSLSocketFactory(sslConnectionSocketFactory).setRedirectStrategy(new LaxRedirectStrategy())
//								.setConnectionManager(cm)
//								.build();
//
//						HttpComponentsClientHttpRequestFactory requestFactory =
//								new HttpComponentsClientHttpRequestFactory();
//
//						requestFactory.setHttpClient(httpclient);
//						restTemplate = new RestTemplate(requestFactory);
//						String[] faceGroupList = new String[1];
//						faceGroupList[0]=dto.getValue().getRoomId();
//						Map<String, String[]> params = new HashMap<>();
//						params.put("faceGroupList", faceGroupList);
//						ResponseEntity<String> response = restTemplate.postForEntity(megBoxProtocal+dto.getValue().getMegBoxIp()+megBoxUri+"faceGroups/binding/"+studentTokenDetails.get(Integer.parseInt(dto.getValue().getStudentId())), params, String.class);
//						if(response.getStatusCode().equals(STATUS_CODE_200) 
//								|| response.getStatusCode().equals(STATUS_CODE_201)) {
//							isBinded = true;
//						}
//					}
//				} catch (Exception e) {; 
//					lg.error("ERROR Occured in bindingFaceWithFaceGroups in loop : {} ",dto.getValue().getStudentId());
//				}
//			}
//		}
//		}catch (Exception e) {
//			lg.error("Occured in bindingFaceWithFaceGroups",e);
//		}
//		finally {
//			if(cm != null) {
//			cm.close();
//			}
//		}
//		lg.info("End of group creation");
//		return isBinded;
//	}
	// Code Added By Akash 
	
	public boolean bindingFaceWithFaceGroups(
	        Map<String, AttendanceReqestModel> StudentDetails,
	        String megboxIp) {

	    lg.info("Inside group creation");
	    boolean isBinded = false;

	    // track no-photo students (status 300)
	    List<String> noPhotoStudentIds = new ArrayList<>();

	    try {
	        Map<String, String> studentTokenDetails =
	                megboxStudentDetailsDao
	                .getMegboxStudentDetails(orgId, megboxIp);

	        if (!CollectionUtils.isEmpty(StudentDetails)) {
	            for (Map.Entry<String, AttendanceReqestModel>
	                    dto : StudentDetails.entrySet()) {
	                try {
	                    String studentId =
	                            dto.getValue().getStudentId();

	                    boolean hasToken =
	                            studentTokenDetails
	                            .containsKey(studentId)
	                            && studentTokenDetails
	                            .get(studentId) != null;

	                    if (!hasToken) {
	                        lg.warn("No face token for " +
	                                "student: {}", studentId);
	                        noPhotoStudentIds.add(studentId);
	                        continue;
	                    }

	                    String[] faceGroupList = new String[1];
	                    faceGroupList[0] = dto.getValue().getGroupName();
	                    Map<String, String[]> params = new HashMap<>();
	                    params.put("faceGroupList", faceGroupList);

	                    // FIX 3: reuse the shared client (megBoxBindingTemplate)
	                    // instead of building a new client per student
	                    ResponseEntity<String> response =
	                            megBoxBindingTemplate.postForEntity(
	                                    megBoxProtocal +
	                                    dto.getValue().getMegBoxIp() +
	                                    megBoxUri +
	                                    "faceGroups/binding/" +
	                                    studentTokenDetails.get(studentId),
	                                    params,
	                                    String.class);

	                    if (response.getStatusCode().is2xxSuccessful()) {
	                        isBinded = true;
	                    }

	                } catch (Exception e) {
	                    lg.error("ERROR in " +
	                             "bindingFaceWithFaceGroups: {}",
	                             dto.getValue().getStudentId());
	                }
	            }

	            // Update no-photo students to 300
	            if (!noPhotoStudentIds.isEmpty()) {
	                lg.warn("Students with no photo (300): {}",
	                        noPhotoStudentIds);
	                dao.updateStudentStatusToUnknown(
	                        noPhotoStudentIds, "300");
	            }
	        }

	    } catch (Exception e) {
	        lg.error("Error in bindingFaceWithFaceGroups", e);
	    }

	    lg.info("End of group creation");
	    return isBinded;
	}
	@PostConstruct
	public void initMegBoxClient() throws Exception {
	    SSLContextBuilder builder = new SSLContextBuilder();
	    builder.loadTrustMaterial(null, new TrustSelfSignedStrategy());
	    SSLConnectionSocketFactory sslFactory =
	            new SSLConnectionSocketFactory(
	                    builder.build(), NoopHostnameVerifier.INSTANCE);

	    Registry<ConnectionSocketFactory> registry =
	            RegistryBuilder.<ConnectionSocketFactory>create()
	                    .register("http", new PlainConnectionSocketFactory())
	                    .register("https", sslFactory)
	                    .build();

	    PoolingHttpClientConnectionManager pool =
	            new PoolingHttpClientConnectionManager(registry);
	    pool.setMaxTotal(200);
	    pool.setDefaultMaxPerRoute(50);

	    RequestConfig config = RequestConfig.custom()
	            .setConnectTimeout(10 * 1000)
	            .setConnectionRequestTimeout(10 * 1000)
	            .setSocketTimeout(30 * 1000)
	            .build();

	    megBoxHttpClient = HttpClients.custom()
	            .setSSLSocketFactory(sslFactory)
	            .setConnectionManager(pool)
	            .setDefaultRequestConfig(config)
	            .setRedirectStrategy(new LaxRedirectStrategy())
	            .build();

	    HttpComponentsClientHttpRequestFactory factory =
	            new HttpComponentsClientHttpRequestFactory(megBoxHttpClient);
	    megBoxBindingTemplate = new RestTemplate(factory);
	}

	@PreDestroy
	public void closeMegBoxClient() {
	    try {
	        if (megBoxHttpClient != null) {
	            megBoxHttpClient.close();
	        }
	    } catch (IOException e) {
	        lg.error("Error closing MegBox client", e);
	    }
	}
	
	@SuppressWarnings("all")
	public void convertCSVFile(List<AttendanceReqestModel> attendanceReqList, Map<String,String> cameraCapturedStudentMap) throws ParseException {
		
		List<String[]> csvData = createCsvDataSimple(attendanceReqList, cameraCapturedStudentMap);
        try {
        	SimpleDateFormat dateFormat = new SimpleDateFormat("dd-MM-yyyy");
        	AttendanceReqestModel model = attendanceReqList.get(0);
        		File mkdir = new File(csvFilePath+dateFormat.format(new Date())+"\\"+model.getPeriodName());
        		File f = new File(csvFilePath+dateFormat.format(new Date())+"\\"+model.getPeriodName()+"\\"+model.getSubName()+"_"+model.getClassName()+".csv");
        		if(!mkdir.exists()) {
        			mkdir.mkdirs();
        		}
        		f.createNewFile();
        		try (CSVWriter writer = new CSVWriter(new FileWriter(csvFilePath+dateFormat.format(new Date())+"\\"+model.getPeriodName()+"\\"+model.getSubName()+"_"+model.getClassName()+".csv"))) {
                    writer.writeAll(csvData);
                }
        
        } catch (IOException e) {
        	lg.error("Exception Occured in convertCSVFile",e);
		}

	}
	
	@SuppressWarnings("all")
	public void convertMarkedAttendanceCSVFile(List<AttendanceReqestModel> attendanceReqList) throws ParseException {
		
		List<String[]> csvData = createCsvDataSimple(attendanceReqList, null);
        try {
        	SimpleDateFormat dateFormat = new SimpleDateFormat("dd-MM-yyyy");
        	AttendanceReqestModel model = attendanceReqList.get(0);
        		File mkdir = new File(csvFilePath+dateFormat.format(new Date())+"\\"+model.getPeriodName());
        		File f = new File(csvFilePath+dateFormat.format(new Date())+"\\"+model.getPeriodName()+"\\"+"teacher_"+model.getSubName()+"_"+model.getClassName()+".csv");
        		if(!mkdir.exists()) {
        			mkdir.mkdirs();
        		}
        		f.createNewFile();
        		try (CSVWriter writer = new CSVWriter(new FileWriter(csvFilePath+dateFormat.format(new Date())+"\\"+model.getPeriodName()+"\\"+"teacher_"+model.getSubName()+"_"+model.getClassName()+".csv"))) {
                    writer.writeAll(csvData);
                }
        
        } catch (IOException e) {
        	lg.error("Exception Occured in convertCSVFile",e);
		}

	}
	
	private static List<String[]> createCsvDataSimple(
	        List<AttendanceReqestModel> attendanceReqList,
	        Map<String, String> cameraCapturedStudentMap) {

	    String[] header = {
	        "STUDENT NAME",
	        "REGISTRATION NO",
	        "CAMERA_CAPTURED_ATTENDANCE",
	        "TEACHER_MARKED_ATTENDANCE"
	    };

	    List<String[]> list = new ArrayList<>();
	    list.add(header);

	    for (AttendanceReqestModel attData :
	            attendanceReqList) {

	        String[] data = new String[4];
	        data[0] = attData.getStudentName();
	        data[1] = attData.getRegNo();

	        // Camera result
	        String cameraAttendance =
	                cameraCapturedStudentMap != null
	                ? cameraCapturedStudentMap.get(data[1])
	                : attData.getIsRecongStudent();

	        // ← CHANGED: handle 100/200/300
	        if ("100".equals(cameraAttendance)) {
	            data[2] = "Present";
	        } else if ("300".equals(cameraAttendance)) {
	            data[2] = "Unknown (No Photo)";  // ← NEW
	        } else {
	            data[2] = "Absent";
	        }

	        // Teacher result
	        if ("100".equals(
	                attData.getIsRecongStudent())) {
	            data[3] = "Present";
	        } else if ("300".equals(
	                attData.getIsRecongStudent())) {
	            data[3] = "Unknown (No Photo)";  // ← NEW
	        } else {
	            data[3] = "Absent";
	        }

	        list.add(data);
	    }
	    return list;
	}

	
	
	public List<RecongnizedStudentResDto> getRecongnizedData(String megBoxIp, Date teacherSubmittedTime, Date attendanceCapturedEndTime) throws JsonProcessingException, ParseException, NoSuchAlgorithmException {
		List<RecongnizedStudentResDto> recongnizedStudents = new ArrayList<RecongnizedStudentResDto>();
		long startTime = teacherSubmittedTime.getTime()/1000L;
		long endTime = attendanceCapturedEndTime.getTime()/1000L;
		
		lg.info("getRecongnizedData - megBoxIp: {}", megBoxIp);
	    lg.info("getRecongnizedData - teacherSubmittedTime: {}", teacherSubmittedTime);
	    lg.info("getRecongnizedData - attendanceCapturedEndTime: {}", attendanceCapturedEndTime);
	    lg.info("getRecongnizedData - startTime (epoch): {}", startTime);
	    lg.info("getRecongnizedData - endTime (epoch): {}", endTime);

		Integer temp=0;
		for(int i = 0; i <= 1000;i++) {
			RestTemplate restTemplate =  RestTemplateConfiguration.getRestTemplate();
			HttpHeaders headers = new HttpHeaders();
			HttpEntity<String> entityforGet = new HttpEntity<>(headers);
			ResponseEntity<String> responseEntity = null;
			//megImageUrl+"alerts?pageToken="+i+"&pageSize=30&Dedup=1&QueryType=1&timeStart="+startTime+"&timeEnd="+endTime
			try {
				responseEntity = restTemplate.exchange(megBoxProtocal+megBoxIp+megBoxUri+"alerts?pageToken="+i+"&pageSize=30&Dedup=1&QueryType=1&timeStart="+startTime+"&timeEnd="+endTime, HttpMethod.GET, entityforGet,
					String.class);
				lg.info("getRecongnizedData - response: {}", responseEntity.getBody());
			if(mapper == null) {
				mapper= new ObjectMapper();
			}
			RecongnizedStudentResDto facesResDto = mapper.readValue(responseEntity.getBody(), RecongnizedStudentResDto.class);
			recongnizedStudents.add(facesResDto);
			if(temp != Integer.parseInt(facesResDto.getData().getNextPageToken())) {
				temp = Integer.parseInt(facesResDto.getData().getNextPageToken());
			}
			else {
				break;
			}
		}catch (Exception e) {
			 lg.error("getRecongnizedData - error: {}", e.getMessage());
			recongnizedStudents = new ArrayList<RecongnizedStudentResDto>();
		}
		}
		return recongnizedStudents;
	}
	
	// CAMERA IP = 
//	public String getRecongnizedStudent(StudentAttendance studentAttendance) {
//		try {
//			Map<String,AttendanceReqestModel> map = convertStudentReqDetailsToMap(studentAttendance);
//			bindingFaceWithFaceGroups(map, studentAttendance.getMegBoxIp());
//		}catch (Exception e) {
//			e.printStackTrace();			
//		}============dance.getRoomId()+"@"+studentAttendance.getSubjectId();
//		return genaretedValue;
//	}
	
	// RoomId <-> CAMERAIP Modification
//	public String getRecongnizedStudent(StudentAttendance studentAttendance) {
//		try {
//			Map<String,AttendanceReqestModel> map = convertStudentReqDetailsToMap(studentAttendance);
//			bindingFaceWithFaceGroups(map, studentAttendance.getMegBoxIp());
//		}catch (Exception e) {
//			e.printStackTrace();			
//		}
//		String genaretedValue = 
//			    studentAttendance.getCameraIp() + "@" +  // ← NEW
//			    studentAttendance.getSubjectId();
//		return genaretedValue;
//	}
	
//	private Map<String, AttendanceReqestModel> convertStudentReqDetailsToMap(StudentAttendance studentAttendance) {
//		Map<String,AttendanceReqestModel> map = new HashMap<>();
//		List<AttendanceReqestModel> attendanceReqList = new ArrayList<>();
//		SimpleDateFormat dateFormat = new SimpleDateFormat("dd/MM/yyyy");
//			for(StudentAttendanceResponse details : studentAttendance.getStudentAttendanceDetails()) {
//				AttendanceReqestModel model = new AttendanceReqestModel();
//				model.setClassName(studentAttendance.getClassName());
//				model.setEndTime(studentAttendance.getEndTime());
//				model.setPeriodName(studentAttendance.getPeriodName());
//				model.setRoomId(studentAttendance.getRoomId());
//				model.setStartTime(studentAttendance.getStartTime());
//				model.setSubId(String.valueOf(studentAttendance.getSubjectId()));
//				model.setSubName(studentAttendance.getSubName());
//				model.setTeacherId(studentAttendance.getTeacherId());
//				model.setTeacherName(studentAttendance.getTeacherName());
//				model.setClassId(studentAttendance.getClassId());
//				model.setIsRecongStudent("200");
//				model.setStartDate(dateFormat.format(new Date()));
//				model.setEndDate(dateFormat.format(new Date()));
//				model.setPeriodId(studentAttendance.getPeriodId());
//				model.setMegBoxIp(studentAttendance.getMegBoxIp());
//				model.setToken(studentAttendance.getToken());
//				model.setStudentId(String.valueOf(details.getStudentId()));
//				model.setStudentName(details.getStudentName());
//				model.setRegNo(details.getRegNo());
//				model.setIs_on_leave(details.getIs_on_leave());
//				model.setIs_cocurricular_leave(CollectionUtils.isEmpty(details.getIs_cocurricular_leave()) ? 
//						"" : details.getIs_cocurricular_leave().get(0));
//				model.setActivityId(studentAttendance.getActivityId());
//				model.setAttDate(studentAttendance.getAttDate());
//				model.setAttId(studentAttendance.getAttId());
//				model.setAttTypeId(studentAttendance.getAttTypeId());
//				model.setBatchId(studentAttendance.getBatchId());
//				model.setDateandtime(studentAttendance.getDateandtime());
//				model.setHoursheld(studentAttendance.getHoursheld());
//				model.setIsTT(studentAttendance.getIsTT());
//				model.setRemarks(studentAttendance.getRemarks());
//				model.setSubstitueClass(studentAttendance.getSubstitueClass());
//				model.setTotalHours(studentAttendance.getTotalHours());
//				model.setUserId(studentAttendance.getUserId());
//				model.setVersion(studentAttendance.getVersion());
//				model.setWorkDiaryAcademicYear(studentAttendance.getWorkDiaryAcademicYear());
//				model.setWorkDiaryActivityId(studentAttendance.getWorkDiaryActivityId());
//				attendanceReqList.add(model);
//				map.put(model.getStudentId()+"-"+studentAttendance.getRoomId(), model);
//			}
//			if(!attendanceReqList.isEmpty()) {
//				dao.saveAttendance(attendanceReqList, attendanceReqList.size());
//			}
//			return map;
//	}
	
	
//	private Map<String, AttendanceReqestModel> 
//    convertStudentReqDetailsToMap(
//            StudentAttendance studentAttendance) {
//
//Map<String,AttendanceReqestModel> map = 
//        new HashMap<>();
//List<AttendanceReqestModel> attendanceReqList = 
//        new ArrayList<>();
//SimpleDateFormat dateFormat = 
//        new SimpleDateFormat("dd/MM/yyyy");
//
//for(StudentAttendanceResponse details : 
//        studentAttendance
//        .getStudentAttendanceDetails()) {
//
//    AttendanceReqestModel model = 
//            new AttendanceReqestModel();
//    model.setClassName(
//            studentAttendance.getClassName());
//    model.setEndTime(
//            studentAttendance.getEndTime());
//    model.setPeriodName(
//            studentAttendance.getPeriodName());
//    model.setRoomId(
//            studentAttendance.getRoomId());
//    model.setCameraIp(
//            studentAttendance.getCameraIp()); // ← NEW
//    model.setStartTime(
//            studentAttendance.getStartTime());
//    model.setSubId(String.valueOf(
//            studentAttendance.getSubjectId()));
//    model.setSubName(
//            studentAttendance.getSubName());
//    model.setTeacherId(
//            studentAttendance.getTeacherId());
//    model.setTeacherName(
//            studentAttendance.getTeacherName());
//    model.setClassId(
//            studentAttendance.getClassId());
//    model.setIsRecongStudent("200");
//    model.setStartDate(
//            dateFormat.format(new Date()));
//    model.setEndDate(
//            dateFormat.format(new Date()));
//    model.setPeriodId(
//            studentAttendance.getPeriodId());
//    model.setMegBoxIp(
//            studentAttendance.getMegBoxIp());
//    model.setToken(
//            studentAttendance.getToken());
//    model.setStudentId(String.valueOf(
//            details.getStudentId()));
//    model.setStudentName(
//            details.getStudentName());
//    model.setRegNo(details.getRegNo());
//    model.setIs_on_leave(
//            details.getIs_on_leave());
//    model.setIs_cocurricular_leave(
//            CollectionUtils.isEmpty(
//                details.getIs_cocurricular_leave())
//            ? ""
//            : details.getIs_cocurricular_leave()
//                     .get(0));
//    model.setActivityId(
//            studentAttendance.getActivityId());
//    model.setAttDate(
//            studentAttendance.getAttDate());
//    model.setAttId(
//            studentAttendance.getAttId());
//    model.setAttTypeId(
//            studentAttendance.getAttTypeId());
//    model.setBatchId(
//            studentAttendance.getBatchId());
//    model.setDateandtime(
//            studentAttendance.getDateandtime());
//    model.setHoursheld(
//            studentAttendance.getHoursheld());
//    model.setIsTT(studentAttendance.getIsTT());
//    model.setRemarks(
//            studentAttendance.getRemarks());
//    model.setSubstitueClass(
//            studentAttendance.getSubstitueClass());
//    model.setTotalHours(
//            studentAttendance.getTotalHours());
//    model.setUserId(
//            studentAttendance.getUserId());
//    model.setVersion(
//            studentAttendance.getVersion());
//    model.setWorkDiaryAcademicYear(
//            studentAttendance
//                .getWorkDiaryAcademicYear());
//    model.setWorkDiaryActivityId(
//            studentAttendance
//                .getWorkDiaryActivityId());
//
//    attendanceReqList.add(model);
//
//    // ✅ map key uses cameraIp
//    map.put(
//        model.getStudentId() + "-" +
//        studentAttendance.getCameraIp(),
//        model);
//}
//
//if(!attendanceReqList.isEmpty()) {
//    dao.saveAttendance(
//            attendanceReqList,
//            attendanceReqList.size());
//}
//return map;
//}
	public void getTeacherMarkedStudentDetails(StudentApkDtoResponse studentApkDtoResponse) {
		new Thread(new Runnable()
		{
		    @Override
		    public void run() {
		        try {
					Thread.sleep(240000);
					List<AttendanceReqestModel> attendanceList = studentDao.getTeacherMarkedAttendanceData(studentApkDtoResponse);
					convertMarkedAttendanceCSVFile(attendanceList);
				} catch (InterruptedException e) {
					e.printStackTrace();
				}
		        catch (ParseException e) {
					e.printStackTrace();
				}
		    }
		}).start();
	}

	public Map<String, List<String>> getCamDetails(String boxIP) throws JsonMappingException, JsonProcessingException {
        // Initialize RestTemplate
        RestTemplate restTemplate = RestTemplateConfiguration.getRestTemplate();
        HttpHeaders headers = new HttpHeaders();
        HttpEntity<String> entity = new HttpEntity<>(headers);

        // Call the API
        ResponseEntity<String> responseEntity;
        try {
            String url = megBoxProtocal + boxIP + megBoxUri + "multiAlgVideos";
            
            responseEntity = restTemplate.exchange(url, HttpMethod.GET, entity, String.class);
        } catch (Exception e) {
            System.err.println("Error fetching data from: " + boxIP + " | " + e.getMessage());
            return Collections.emptyMap(); // Return empty map instead of null
        }

        // Parse response
        MultiAlgVideosResponseModel multiAlgVideosResponseModel = 
                mapper.readValue(responseEntity.getBody(), MultiAlgVideosResponseModel.class);

        // Map to store GroupName vs Cam IPs
        Map<String, List<String>> groupNameVsCamIP = new HashMap<>();

        // Process Videos
        if (multiAlgVideosResponseModel.getData() != null 
                && multiAlgVideosResponseModel.getData().getVideos() != null) {
            for (Video video : multiAlgVideosResponseModel.getData().getVideos()) {
                String camIP = CameraUtils.extractIPAddress(video.getUrl());

                if (camIP != null && !camIP.isEmpty()) {                    // Extract group names using Stream API
                    List<String> groupNames = video.getAlgRules().stream()
                        .filter(ar -> ar.getStructuresRule() != null && ar.getStructuresRule().getGroup() != null)
                        .flatMap(ar -> ar.getStructuresRule().getGroup().stream())
                        .map(com.iolite.kpcamera.attendance.model.responses.Group::getGroupName)
                        .collect(Collectors.toList());

                    // Insert into the map
                    groupNameVsCamIP.put(camIP, groupNames);
                }
            }
        }

        return groupNameVsCamIP;
	}
	
	//code added by ajay
	
	public String testS3Flow(String identifier) {

	    try {
	        String url = getPreSignedUrl(identifier);

	        if (url == null || url.isEmpty()) {
	            return "❌ PreSigned URL is NULL";
	        }

	        File file = downloadImageFromPreSignedUrl(url, identifier);

	        if (file != null && file.exists()) {
	            return "✅ SUCCESS: File downloaded at " + file.getAbsolutePath();
	        }

	        return "❌ FAILED: File not created";

	    } catch (Exception e) {
	        lg.error("S3 test failed for {}", identifier, e);
	        return "❌ ERROR: " + e.getMessage();
	    }
	}
	
	public OnDemandUploadResponse uploadSingleStudent(
	        OnDemandUploadRequest request) {

	    OnDemandUploadResponse response =
	            new OnDemandUploadResponse();
	    response.setStudentId(request.getStudentId());

	    List<String> uploadedTo = new ArrayList<>();
	    List<String> failedIps  = new ArrayList<>();

	    // Step 1 — Check upload flag
	    if (!Boolean.TRUE.equals(isUploadImages)) {
	        response.setStatus("FAILED");
	        response.setMessage("Image upload is disabled");
	        convertOnDemandUploadCSV(response);   // ← NEW
	        return response;
	    }

	    // Step 2 — Validate request
	    if (request.getStudentId() == null
	            || request.getStudentId().trim().isEmpty()) {
	        response.setStatus("FAILED");
	        response.setMessage("studentId is required");
	        convertOnDemandUploadCSV(response);   // ← NEW
	        return response;
	    }

	    // Step 3 — Validate photo
	    if (request.getPhoto() == null
	            || request.getPhoto().trim().isEmpty()) {
	        response.setStatus("FAILED");
	        response.setMessage("Photo is required");
	        convertOnDemandUploadCSV(response);   // ← NEW
	        return response;
	    }

	    // Step 4 — Get all MegBox IPs
	    String[] megBoxIps = megImageUrl.split(",");

	    // Step 5 — Convert Base64 to temp file
	    File tempImageFile = null;
	    try {
	        tempImageFile = convertBase64ToTempFile(
	                request.getPhoto(),
	                request.getStudentId());

	    } catch (Exception ex) {
	        lg.error("Failed to convert Base64 photo " +
	                 "for student: {}",
	                 request.getStudentId(), ex);
	        response.setStatus("FAILED");
	        response.setMessage(
	            "Photo conversion failed: " + ex.getMessage());
	        convertOnDemandUploadCSV(response);   // ← NEW
	        return response;
	    }

	    // Step 6 — Upload to each MegBox IP
	    try {
	        for (String ip : megBoxIps) {
	            ip = ip.trim();
	            try {
	                Map<String, String> existingTokens =
	                        megboxStudentDetailsDao
	                        .getMegboxStudentDetails(
	                                orgId, ip);

	                boolean alreadyUploaded =
	                        existingTokens.containsKey(
	                                request.getStudentId());

	                if (alreadyUploaded) {
	                    lg.info("Student {} already uploaded " +
	                            "to MegBox: {}",
	                            request.getStudentId(), ip);
	                    uploadedTo.add(ip + " (already exists)");
	                    continue;
	                }

	                uploadToMegBox(
	                        tempImageFile,
	                        request.getStudentId(),
	                        ip);

	                uploadedTo.add(ip);
	                lg.info("Successfully uploaded " +
	                        "student {} to MegBox: {}",
	                        request.getStudentId(), ip);

	            } catch (Exception ex) {
	                lg.error("Failed to upload student {} " +
	                         "to MegBox IP: {}",
	                         request.getStudentId(), ip, ex);
	                failedIps.add(ip + " : " + ex.getMessage());
	                facesDao.failedStudentImage(
	                        request.getStudentId(), ip);
	            }
	        }
	    } finally {
	        if (tempImageFile != null
	                && tempImageFile.exists()) {
	            tempImageFile.delete();
	        }
	    }

	    // Step 7 — Build response
	    response.setUploadedTo(uploadedTo);
	    response.setFailedMegboxIps(failedIps);

	    long actualUploads = uploadedTo.stream()
	            .filter(ip -> !ip.contains("already exists"))
	            .count();

	    if (failedIps.isEmpty() && actualUploads > 0) {
	        response.setStatus("SUCCESS");
	        response.setMessage(
	            "Student uploaded to all MegBox devices");

	    } else if (failedIps.isEmpty()
	            && actualUploads == 0) {
	        response.setStatus("ALREADY_EXISTS");
	        response.setMessage(
	            "Student already exists in all MegBox devices");

	    } else if (uploadedTo.isEmpty()) {
	        response.setStatus("FAILED");
	        response.setMessage(
	            "Upload failed for all MegBox devices");

	    } else {
	        response.setStatus("PARTIAL_SUCCESS");
	        response.setMessage(
	            "Uploaded to some MegBox devices");
	    }

	    // ← NEW: write CSV for this on-demand upload
	    convertOnDemandUploadCSV(response);

	    return response;
	}
		private void uploadToMegBox(File imageFile,
	            String studentId,
	            String megBoxIp) 
	            throws Exception {
	
	RestTemplate restTemplate =
	RestTemplateConfiguration.getRestTemplate();
	restTemplate.getMessageConverters()
	.add(new ByteArrayHttpMessageConverter());
	
	MultiValueMap<String, Object> body =
	new LinkedMultiValueMap<>();
	body.add("image", new FileSystemResource(imageFile));
	body.add("description", studentId);
	
	HttpHeaders headers = new HttpHeaders();
	headers.setContentType(MediaType.MULTIPART_FORM_DATA);
	
	HttpEntity<MultiValueMap<String, Object>> requestEntity =
	new HttpEntity<>(body, headers);
	
	ResponseEntity<String> response = restTemplate.exchange(
	"https://" + megBoxIp + "/v1/MEGBOX/faces",
	HttpMethod.POST,
	requestEntity,
	String.class);
	
	FacesResponseDto facesResDto = new ObjectMapper()
	.readValue(response.getBody(),
	       FacesResponseDto.class);
	saveFaces(facesResDto, studentId, megBoxIp);
	}
		// New method to convert Base64 to temp file
		private File convertBase64ToTempFile(
		        String base64Photo,
		        String studentId) throws IOException {
	
		    // Step 1 — Validate base64 string
		    if (base64Photo == null 
		            || base64Photo.trim().isEmpty()) {
		        throw new IOException(
		            "Photo is null or empty for student: "
		                + studentId);
		    }
	
		    // Step 2 — Decode Base64 → byte[]
		    // Using existing Base64Coder in your project!
		    byte[] imageBytes = Base64Coder
		            .decode(base64Photo);
	
		    // Step 3 — Create temp file
		    File tempFile = File.createTempFile(
		            "student_" + studentId, ".jpg");
	
		    // Step 4 — Write bytes to temp file
		    try (FileOutputStream fos = 
		                new FileOutputStream(tempFile)) {
		        fos.write(imageBytes);
		        fos.flush();
		    }
	
		    return tempFile;
		}
		public String getRecongnizedStudentDetails(
		        StudentAttendance studentAttendance,
		        String submittedAt) {

		    try {
		        Map<String, AttendanceReqestModel> map =
		                convertStuReqDetailsToMap(
		                        studentAttendance, submittedAt);
		        bindingFaceWithFaceGroups(
		                map,
		                studentAttendance.getMegBoxIp());
		    } catch (Exception e) {
		        lg.error("Error in getRecongnizedStudentDetails", e);
		    }

		    // jobId: cameraIpAddress@submittedAt
		    String generatedValue =
		            studentAttendance.getCameraIpAddress() +
		            "@" +
		            submittedAt;

		    return generatedValue;
		}
		private Map<String, AttendanceReqestModel>
        convertStuReqDetailsToMap(
                StudentAttendance studentAttendance,
                String submittedAt) {

    Map<String, AttendanceReqestModel> map = new HashMap<>();
    List<AttendanceReqestModel> attendanceReqList =
            new ArrayList<>();
    SimpleDateFormat dateFormat =
            new SimpleDateFormat("dd/MM/yyyy");

    String cameraIp = studentAttendance.getCameraIpAddress();

    // strip dots → MegBox group name
    // "192.168.9.41" → "192168941"
    String groupName = (cameraIp != null)
            ? cameraIp.replace(".", "")
            : "";

    List<String> uuidList = studentAttendance.getUuidList();

    if (uuidList != null) {
        for (String studentId : uuidList) {

            AttendanceReqestModel model =
                    new AttendanceReqestModel();

            // ← submittedAt now covers what start_time/
            // end_time/dateandtime used to carry
            model.setStartTime(submittedAt);
            model.setEndTime(null);
            model.setStartDate(dateFormat.format(new Date()));
            model.setEndDate(dateFormat.format(new Date()));
            model.setCameraIp(cameraIp);
            model.setMegBoxIp(studentAttendance.getMegBoxIp());
            model.setToken(studentAttendance.getToken());
            model.setIsRecongStudent("200");
            model.setDateandtime(submittedAt);

            // uuid IS the studentId now
            model.setStudentId(studentId);
            model.setStudentName(null);   // no longer sent by Christ
            model.setRegNo(studentId);    // regNo = uuid, same as before
            model.setGroupName(groupName);

            attendanceReqList.add(model);

            map.put(studentId + "|" + groupName, model);
        }
    }

    if (!attendanceReqList.isEmpty()) {
        dao.saveAttendanceNew(
                attendanceReqList,
                attendanceReqList.size());
    }

    return map;
}
			private void convertOnDemandUploadCSV(
			        OnDemandUploadResponse response) {

			    File directory = new File(
			            csvFilePath + "onDemandUpload");
			    File file = new File(
			            csvFilePath + "\\onDemandUpload\\" +
			            "onDemandUploadResult.csv");

			    List<String[]> csvData = new ArrayList<>();

			    // Header
			    csvData.add(new String[]{
			        "STUDENT ID",
			        "STATUS",
			        "UPLOADED TO",
			        "FAILED IPS",
			        "MESSAGE"
			    });

			    // Data row
			    String uploadedTo =
			            response.getUploadedTo() != null
			            ? String.join("|",
			                    response.getUploadedTo())
			            : "";
			    String failedIps =
			            response.getFailedMegboxIps() != null
			            ? String.join("|",
			                    response.getFailedMegboxIps())
			            : "";

			    csvData.add(new String[]{
			        response.getStudentId(),
			        response.getStatus(),
			        uploadedTo,
			        failedIps,
			        response.getMessage()
			    });

			    try {
			        if (!directory.exists()) {
			            directory.mkdirs();
			        }
			        if (!file.exists()) {
			            file.createNewFile();
			        }
			        try (CSVWriter writer =
			                new CSVWriter(new FileWriter(file))) {
			            writer.writeAll(csvData);
			        }
			        lg.info("OnDemand upload CSV written: {}",
			                file.getAbsolutePath());
			    } catch (IOException e) {
			        lg.error("Error writing onDemand CSV", e);
			    }
			}
			private void uploadFacesFromLocalFolder() {
			    lg.info("🟢 LOCAL mode — scanning folder: {}",
			            localImageFolderPath);

			    List<String[]> failedStudentList =
			            Collections.synchronizedList(new ArrayList<>());
			    List<String[]> alreadyExistsList =
			            Collections.synchronizedList(new ArrayList<>());

			    try {
			        File folder = new File(localImageFolderPath);
			        if (!folder.exists() || !folder.isDirectory()) {
			            lg.error("❌ Local folder not found: {}",
			                    localImageFolderPath);
			            return;
			        }

			        // Get all jpg and png files
			        File[] imageFiles = folder.listFiles(file ->
			                file.isFile() &&
			                (file.getName().toLowerCase().endsWith(".jpg")
			                || file.getName().toLowerCase()
			                        .endsWith(".png")));

			        if (imageFiles == null || imageFiles.length == 0) {
			            lg.warn("No image files found in: {}",
			                    localImageFolderPath);
			            return;
			        }

			        lg.info("Found {} image files in local folder",
			                imageFiles.length);

			        String[] megBoxIps = megImageUrl.split(",");

			        int threads = Math.min(25, imageFiles.length);
			        ExecutorService executor =
			                Executors.newFixedThreadPool(threads);
			        List<CompletableFuture<Void>> futures =
			                new ArrayList<>();

			        try {
			            for (String ip : megBoxIps) {

			                // Get already uploaded students
			                Map<String, String> studentTokenDetails =
			                        megboxStudentDetailsDao
			                        .getMegboxStudentDetails(orgId, ip);

			                for (File imageFile : imageFiles) {

			                    // Extract UUID from filename
			                    String fileName = imageFile.getName();
			                    String uuid = fileName.substring(
			                            0, fileName.lastIndexOf("."));

			                    // Skip if already uploaded
			                    if (studentTokenDetails.containsKey(uuid)) {
			                        lg.info("Already uploaded: {}", uuid);
			                        alreadyExistsList.add(new String[]{
			                            uuid,
			                            "ALREADY_EXISTS",
			                            "Face already uploaded to MegBox"
			                        });
			                        continue;
			                    }

			                    final String finalUuid = uuid;
			                    final File finalImageFile = imageFile;
			                    final String finalIp = ip;

			                    futures.add(CompletableFuture
			                            .runAsync(() -> {
			                        try {
			                            uploadSingleLocalFile(
			                                    finalImageFile,
			                                    finalUuid,
			                                    failedStudentList,
			                                    finalIp);
			                        } catch (Exception ex) {
			                            lg.error("❌ Failed: {}",
			                                    finalUuid, ex);
			                        }
			                    }, executor));
			                }
			            }

			            CompletableFuture.allOf(
			                    futures.toArray(
			                        new CompletableFuture[0])).get();

			        } catch (Exception e) {
			            lg.error("🔥 Error in local upload", e);
			        } finally {
			            shutdownExecutor(executor);
			        }

			        lg.info("✅ Local upload finished. " +
			                "Total attempted: {}", futures.size());

			    } catch (Exception e) {
			        lg.error("🔥 Error scanning local folder", e);
			    }

			    List<String[]> allCsvEntries = new ArrayList<>();
			    allCsvEntries.addAll(failedStudentList);
			    allCsvEntries.addAll(alreadyExistsList);
			    convertFailedStudentCSVFIle(allCsvEntries);
			}
			private void uploadSingleLocalFile(
			        File imageFile,
			        String uuid,
			        List<String[]> failedStudentList,
			        String megBoxIp) {

			    lg.info("Uploading from local: {}", uuid);

			    try {
			        RestTemplate restTemplate =
			                RestTemplateConfiguration.getRestTemplate();
			        restTemplate.getMessageConverters()
			                .add(new ByteArrayHttpMessageConverter());

			        MultiValueMap<String, Object> body =
			                new LinkedMultiValueMap<>();
			        body.add("image", new FileSystemResource(imageFile));
			        body.add("description", uuid);

			        HttpHeaders headers = new HttpHeaders();
			        headers.setContentType(MediaType.MULTIPART_FORM_DATA);

			        HttpEntity<MultiValueMap<String, Object>>
			                requestEntity =
			                new HttpEntity<>(body, headers);

			        ResponseEntity<String> response =
			                restTemplate.exchange(
			                        "https://" + megBoxIp +
			                        "/v1/MEGBOX/faces",
			                        HttpMethod.POST,
			                        requestEntity,
			                        String.class);

			        FacesResponseDto facesResDto =
			                new ObjectMapper().readValue(
			                        response.getBody(),
			                        FacesResponseDto.class);

			        saveFaces(facesResDto, uuid, megBoxIp);

			        lg.info("✅ Successfully uploaded: {}", uuid);

			    } catch (Exception e) {
			        lg.error("Upload error for {}: {}",
			                uuid, e.getMessage());
			        failedStudentList.add(new String[]{
			            uuid,
			            "FAILED",
			            e.getMessage()
			        });
			    }
			}
	
}
	
	
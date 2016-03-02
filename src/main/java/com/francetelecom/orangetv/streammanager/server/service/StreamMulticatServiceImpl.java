package com.francetelecom.orangetv.streammanager.server.service;

import java.io.IOException;
import java.util.List;
import java.util.logging.Logger;

import javax.json.JsonObject;
import javax.servlet.ServletConfig;
import javax.servlet.ServletContext;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;

import com.francetelecom.orangetv.streammanager.client.service.IStreamMulticatService;
import com.francetelecom.orangetv.streammanager.server.dao.StreamDao;
import com.francetelecom.orangetv.streammanager.server.dao.VideoDao;
import com.francetelecom.orangetv.streammanager.server.manager.AdminManager;
import com.francetelecom.orangetv.streammanager.server.manager.ProfileManager;
import com.francetelecom.orangetv.streammanager.server.manager.StreamManager;
import com.francetelecom.orangetv.streammanager.server.manager.VideoManager;
import com.francetelecom.orangetv.streammanager.server.model.DbFullStreamEntry;
import com.francetelecom.orangetv.streammanager.server.util.EitJsonParser;
import com.francetelecom.orangetv.streammanager.shared.dto.CmdRequest;
import com.francetelecom.orangetv.streammanager.shared.dto.CmdResponse;
import com.francetelecom.orangetv.streammanager.shared.dto.DtoAdminInfo;
import com.francetelecom.orangetv.streammanager.shared.dto.FullVideoInfo;
import com.francetelecom.orangetv.streammanager.shared.dto.IDto;
import com.francetelecom.orangetv.streammanager.shared.dto.StreamInfo;
import com.francetelecom.orangetv.streammanager.shared.dto.StreamInfoForList;
import com.francetelecom.orangetv.streammanager.shared.dto.StreamStatus;
import com.francetelecom.orangetv.streammanager.shared.dto.SupervisorStatus;
import com.francetelecom.orangetv.streammanager.shared.dto.UserProfile;
import com.francetelecom.orangetv.streammanager.shared.dto.VideoInfo;
import com.francetelecom.orangetv.streammanager.shared.dto.VideoStatus;
import com.francetelecom.orangetv.streammanager.shared.model.EitInfoModel;
import com.francetelecom.orangetv.streammanager.shared.model.ServerInfoBean;
import com.francetelecom.orangetv.streammanager.shared.util.EitException;
import com.francetelecom.orangetv.streammanager.shared.util.ValueHelper;
import com.google.gwt.user.server.rpc.RemoteServiceServlet;

public class StreamMulticatServiceImpl extends RemoteServiceServlet implements IStreamMulticatService, IMyServices {

	private static final long serialVersionUID = 1L;
	private static final Logger log = Logger.getLogger(StreamMulticatServiceImpl.class.getName());

	private static final String KEY_REFRESH_INTERVAL_MS = "refreshIntervalMs";

	// private Timer timer = new Timer();
	// private boolean modeTest = false;
	private int refreshDataInterval = 0;

	@Override
	public void init(ServletConfig config) throws ServletException {
		log.info("init()");
		super.init(config);
		ServletContext servletContext = config.getServletContext();
		String intervall = servletContext.getInitParameter(KEY_REFRESH_INTERVAL_MS);

		this.refreshDataInterval = ValueHelper.getIntValue(intervall, 0);

	}

	@Override
	protected void doGet(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
		log.finest("doGet()");
		super.doGet(req, resp);
	}

	// ========================================================================
	// ADMIN
	// ========================================================================

	@Override
	public CmdResponse uploadFileToMulticatServer(String srcFilepah, String targetFilepath) throws EitException {
		return AdminManager.get().copyFileToRemoteWithLinuxScp(srcFilepah, targetFilepath);
	}

	@Override
	public CmdResponse executeSSHCommand(CmdRequest cmdRequest) throws EitException {
		return AdminManager.get().executeRemoteCommand(cmdRequest);
	}

	@Override
	public ServerInfoBean getServerInfo() throws Exception {

		HttpServletRequest request = this.getThreadLocalRequest();
		String name = request.getServerName();
		int port = request.getServerPort();
		log.config("getServerInfo: " + name + " - " + port);
		return new ServerInfoBean(name, port);
	}

	// ========================================================================
	// identification
	// ========================================================================

	@Override
	public String getCurrentUserProfile() {

		UserProfile userProfile = ProfileManager.get().getSessionUserProfile(getThreadLocalRequest());
		return (userProfile == null) ? UserProfile.anybody.name() : userProfile.name();
	}

	@Override
	public SupervisorStatus getSupervisorStatus() {
		return AdminManager.get().getSupervisorStatus();
	}

	@Override
	public DtoAdminInfo getAdminInfo() throws EitException {
		return AdminManager.get().getAdminInfo();
	}

	@Override
	public List<FullVideoInfo> getListFullVideoInfo() throws EitException {
		return VideoManager.get().getListFullVideoInfo(this.isSessionAtLeastManagerProfile(), true);
	}

	@Override
	public List<VideoInfo> getListAvailableVideoInfo() throws EitException {
		return VideoManager.get().getListAvailableVideoInfo(this.isSessionAtLeastManagerProfile());
	}

	@Override
	public String authenticateUserProfile(String login, String pwd) {

		UserProfile UserProfile = ProfileManager.get().getUserProfileFromCredential(login, pwd);
		HttpServletRequest request = this.getThreadLocalRequest();
		HttpSession session = request.getSession(true);
		session.setAttribute(KEY_SESSION_USER_PROFIL, UserProfile);

		return UserProfile.name();

	}

	@Override
	public int getRefreshDbEntryInterval() {
		return this.refreshDataInterval;
	}

	// ========================================================================
	// EIT
	// ========================================================================

	@Override
	public EitInfoModel buildEitInfoFromJson(String json) throws EitException {
		return EitJsonParser.parse(json);
	}

	@Override
	public String getJsonFromEitInfo(EitInfoModel eitInfo) throws EitException {
		JsonObject jsonObject = EitJsonParser.buildJson(eitInfo);
		if (jsonObject != null) {
			return jsonObject.toString();
		}
		return null;
	}

	@Override
	public boolean updateEitInfo(int streamId, EitInfoModel eitInfo) throws EitException {
		DbFullStreamEntry entry = StreamDao.get().getById(streamId);
		if (entry == null || eitInfo == null) {
			return false;
		}

		if (entry.isEntryProtected() && !this.isSessionAtLeastManagerProfile()) {
			throw new EitException("This stream is protected. You must have manager user profile!");
		}

		return StreamDao.get().updateEit(eitInfo);

	}

	@Override
	public EitInfoModel getEitOfStream(int streamId) throws EitException {

		DbFullStreamEntry entry = StreamDao.get().getById(streamId);
		if (entry == null) {
			return null;
		}
		try {
			EitInfoModel eitInfo = EitJsonParser.parse(entry);
			return eitInfo;
		} catch (Exception ex) {
			throw new EitException("Error parsing eit!: " + ex.getMessage());
		}

	}

	// ========================================================================
	// STREAM
	// ========================================================================

	@Override
	public List<StreamInfoForList> getListStreamInfo() throws EitException {
		return StreamManager.get().getListStreamInfoWithProtection(this.isSessionAtLeastManagerProfile(),
				this.isSessionAtLeastAdminProfile());
	}

	@Override
	public StreamInfo getStreamInfo(final int streamId) throws EitException {

		return StreamManager.get().getStreamInfo(streamId, this.isSessionAtLeastManagerProfile(),
				this.isSessionAtLeastAdminProfile());
	}

	/**
	 * Creation de l'entree dans la table 'eit' puis update eit
	 */
	@Override
	public StreamInfo createStreamInfo(StreamInfo streamInfo, EitInfoModel eitInfo) throws EitException {
		this.validStreamInfo(streamInfo);

		return StreamManager.get().createStreamInfo(streamInfo, eitInfo, this.isSessionAtLeastManagerProfile());
	}

	@Override
	public boolean updateStreamInfo(StreamInfo streamInfo) throws EitException {
		return StreamManager.get().updateStreamInfo(streamInfo, this.isSessionAtLeastManagerProfile());
	}

	@Override
	public void validStreamInfo(StreamInfo streamInfo) throws EitException {

		StreamManager.get().validStreamInfo(streamInfo, this.isSessionAtLeastManagerProfile());
	}

	@Override
	public boolean startStream(final int streamId) throws EitException {

		return StreamManager.get().startStream(streamId);
	}

	@Override
	public boolean stopStream(final int streamId) throws EitException {

		return StreamManager.get().stopStream(streamId);
	}

	@Override
	public boolean updateStreamStatus(int streamId, StreamStatus newStatus) throws EitException {
		return StreamManager.get().updateStreamStatus(streamId, newStatus, this.isSessionAtLeastAdminProfile());
	}

	@Override
	public boolean deleteStream(int streamId) throws EitException {

		return StreamManager.get().deleteStream(streamId, this.isSessionAtLeastManagerProfile());
	}

	// ========================================================================
	// VIDEO
	// ========================================================================

	@Override
	public void validVideoInfo(FullVideoInfo videoInfo) throws EitException {
		VideoManager.get().validVideoInfo(videoInfo, this.isSessionAtLeastManagerProfile());
	}

	@Override
	public FullVideoInfo getVideoInfo(int videoId) throws EitException {

		return VideoManager.get().getVideoInfo(videoId, this.isSessionAtLeastManagerProfile());
	}

	@Override
	public boolean updateVideoStatus(int videoId, VideoStatus videoStatus) throws EitException {
		return VideoDao.get().updateStatus(videoId, videoStatus);

	}

	@Override
	public boolean updateVideoInfo(FullVideoInfo videoInfo) throws EitException {

		FullVideoInfo currentVideo = videoInfo.getId() == IDto.ID_UNDEFINED ? null : VideoManager.get().getVideoInfo(
				videoInfo.getId(), this.isSessionAtLeastManagerProfile());

		boolean updateVideoFile = (currentVideo == null) ? false : !videoInfo.getName().equals(currentVideo.getName());
		VideoStatus videoStatus = null;

		// upload en cours qq soit status
		if (videoInfo.isUploadPending()) {
			videoStatus = VideoStatus.PENDING;
		}
		// changement de fichier video sur une video ready sans upload
		else if (videoInfo.getStatus() == VideoStatus.READY && updateVideoFile) {
			videoStatus = VideoStatus.NEW;
			// TODO supprimer les anciens fichiers video
		}

		boolean result = VideoManager.get().updateVideoInfo(videoInfo, this.isSessionAtLeastManagerProfile());

		if (result) {

			if (videoStatus != null) {
				VideoDao.get().updateStatus(videoInfo.getId(), videoStatus);
			}
			if (videoInfo.isUploadPending()) {

				this.sendVideoToMulticatAndFinishProcess(videoInfo);
			}
		}

		return result;

	}

	/**
	 * Creation de l'entree dans la table 'video'
	 */
	@Override
	public FullVideoInfo createVideoFile(FullVideoInfo videoInfo) throws EitException {
		FullVideoInfo result = VideoManager.get().createVideoFile(videoInfo, isSessionAtLeastManagerProfile());

		if (result != null) {

			this.sendVideoToMulticatAndFinishProcess(videoInfo);
		}

		return result;
	}

	private void sendVideoToMulticatAndFinishProcess(FullVideoInfo videoInfo) throws EitException {
		if (videoInfo.isUploadPending()) {

			String videoPath = VideoManager.get().getGwtUploadedFileInCurrentSession(this.getThreadLocalRequest());
			if (videoPath != null) {
				VideoManager.get().sendVideoToMulticatAndFinishProcess(videoInfo.getId(), videoPath);
			} else {
				log.warning("aucun fichier video disponible dans la session!");
			}
		}
	}

	@Override
	public boolean deleteVideo(int videoId) throws EitException {
		return VideoManager.get().deleteVideo(videoId, this.isSessionAtLeastManagerProfile());
	}

	/*
	 * Verifie si une session existe avec profil de droit >= profil
	 */
	private boolean isSessionAtLeastUserProfile(UserProfile profil) {

		HttpServletRequest request = this.getThreadLocalRequest();
		return ProfileManager.get().isSessionAtLeastUserProfile(profil, request);
	}

	private boolean isSessionAtLeastManagerProfile() {
		return this.isSessionAtLeastUserProfile(UserProfile.manager);
	}

	private boolean isSessionAtLeastAdminProfile() {
		return this.isSessionAtLeastUserProfile(UserProfile.admin);
	}

}

package com.francetelecom.orangetv.streammanager.client.service;

import java.util.List;

import com.francetelecom.orangetv.streammanager.shared.dto.CmdRequest;
import com.francetelecom.orangetv.streammanager.shared.dto.CmdResponse;
import com.francetelecom.orangetv.streammanager.shared.dto.DtoAdminInfo;
import com.francetelecom.orangetv.streammanager.shared.dto.FullVideoInfo;
import com.francetelecom.orangetv.streammanager.shared.dto.StreamInfo;
import com.francetelecom.orangetv.streammanager.shared.dto.StreamInfoForList;
import com.francetelecom.orangetv.streammanager.shared.dto.StreamStatus;
import com.francetelecom.orangetv.streammanager.shared.dto.SupervisorStatus;
import com.francetelecom.orangetv.streammanager.shared.dto.VideoInfo;
import com.francetelecom.orangetv.streammanager.shared.dto.VideoStatus;
import com.francetelecom.orangetv.streammanager.shared.model.EitInfoModel;
import com.francetelecom.orangetv.streammanager.shared.model.ServerInfoBean;
import com.google.gwt.user.client.rpc.AsyncCallback;

public interface IStreamMulticatServiceAsync {

	void getServerInfo(AsyncCallback<ServerInfoBean> callback);

	void startStream(int streamId, AsyncCallback<Boolean> callback);

	void stopStream(int streamId, AsyncCallback<Boolean> callback);

	void updateStreamInfo(StreamInfo streamInfo, AsyncCallback<Boolean> callback);

	void getStreamInfo(int streamId, AsyncCallback<StreamInfo> callback);

	void validStreamInfo(StreamInfo streamInfo, AsyncCallback<Void> callback);

	void createStreamInfo(StreamInfo streamInfo, EitInfoModel eitInfo, AsyncCallback<StreamInfo> callback);

	void getCurrentUserProfile(AsyncCallback<String> callback);

	void authenticateUserProfile(String login, String pwd, AsyncCallback<String> callback);

	void getRefreshDbEntryInterval(AsyncCallback<Integer> callback);

	void deleteStream(int streamId, AsyncCallback<Boolean> callback);

	void updateVideoInfo(FullVideoInfo videoInfo, AsyncCallback<Boolean> callback);

	void getVideoInfo(int videoId, AsyncCallback<FullVideoInfo> callback);

	void validVideoInfo(FullVideoInfo videoInfo, AsyncCallback<Void> callback);

	void createVideoFile(FullVideoInfo videoInfo, AsyncCallback<FullVideoInfo> callback);

	void deleteVideo(int videoId, AsyncCallback<Boolean> callback);

	void getListStreamInfo(AsyncCallback<List<StreamInfoForList>> callback);

	void getListAvailableVideoInfo(AsyncCallback<List<VideoInfo>> callback);

	void executeSSHCommand(CmdRequest cmdRequest, AsyncCallback<CmdResponse> callback);

	void getAdminInfo(AsyncCallback<DtoAdminInfo> callback);

	void updateStreamStatus(int streamId, StreamStatus newStatus, AsyncCallback<Boolean> callback);

	void getSupervisorStatus(AsyncCallback<SupervisorStatus> callback);

	void uploadFileToMulticatServer(String srcFilepah, String targetFilepath, AsyncCallback<CmdResponse> callback);

	void getListFullVideoInfo(AsyncCallback<List<FullVideoInfo>> callback);

	void updateVideoStatus(int videoId, VideoStatus videoStatus, AsyncCallback<Boolean> callback);

	void updateEitInfo(int streamId, EitInfoModel eitInfo, AsyncCallback<Boolean> callback);

	void getEitOfStream(int streamId, AsyncCallback<EitInfoModel> callback);

	void getJsonFromEitInfo(EitInfoModel eitInfo, AsyncCallback<String> callback);

	void buildEitInfoFromJson(String json, AsyncCallback<EitInfoModel> callback);

}

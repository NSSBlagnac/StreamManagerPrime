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
import com.francetelecom.orangetv.streammanager.shared.util.EitException;
import com.google.gwt.user.client.rpc.RemoteService;
import com.google.gwt.user.client.rpc.RemoteServiceRelativePath;

/**
 * The client side stub for the RPC service.
 */
@RemoteServiceRelativePath("MyService")
public interface IStreamMulticatService extends RemoteService {

	public ServerInfoBean getServerInfo() throws Exception;

	// ================================================
	// DIVERS
	// ================================================

	/**
	 * Donne l'intervalle de rafraichissement en ms
	 * Defini dans le web.xml
	 */
	public int getRefreshDbEntryInterval();

	/**
	 * Authentification de l'utilisateur dans un des trois profil (admin,
	 * qualif, anybody)
	 * 
	 * @param login
	 * @param pwd
	 * @return
	 */
	String authenticateUserProfile(String login, String pwd);

	/**
	 * Retourne le profil de l'utilisateur (cookies de session)
	 * 
	 * @return
	 */
	String getCurrentUserProfile();

	// ================================================
	// ADMIN
	// ================================================

	/**
	 * Upload file from tomcat server to multicat server
	 * 
	 * @param srcFilepah
	 * @param targetFilepath
	 * @return
	 * @throws EitException
	 */
	CmdResponse uploadFileToMulticatServer(String srcFilepah, String targetFilepath) throws EitException;

	/**
	 * Lance une command SSH sur le server multicat
	 * 
	 * @param cmdRequest
	 * @return
	 */
	CmdResponse executeSSHCommand(CmdRequest cmdRequest) throws EitException;

	/**
	 * Recupere les informations necessaire au panel d'admin
	 * 
	 * @return
	 * @throws EitException
	 */
	DtoAdminInfo getAdminInfo() throws EitException;

	/**
	 * retourne le status du multicat-supervisor
	 * 
	 * @return
	 */
	SupervisorStatus getSupervisorStatus();

	// ================================================
	// VIDEO
	// ================================================

	/**
	 * Recupere la liste des videos disponibles
	 * 
	 * @return
	 * @throws EitException
	 */
	public List<VideoInfo> getListAvailableVideoInfo() throws EitException;

	/**
	 * Recupère la liste des video avec info de protection et stream
	 * 
	 * @return
	 * @throws EitException
	 */
	public List<FullVideoInfo> getListFullVideoInfo() throws EitException;

	/**
	 * Met à jour les informations de la video
	 * 
	 * @param streamInfo
	 * @return
	 */
	public boolean updateVideoInfo(FullVideoInfo videoInfo) throws EitException;

	/**
	 * Met à jour le status de la video
	 */
	public boolean updateVideoStatus(int videoId, VideoStatus videoStatus) throws EitException;

	/**
	 * Valide les modification du videoInfo
	 * 
	 * @param videoInfo
	 * @return
	 * @throws Exception
	 *             si erreur de validation
	 */
	public void validVideoInfo(FullVideoInfo videoInfo) throws EitException;

	/**
	 * Récupère une entree de la table des video par son id
	 * 
	 * @param streamId
	 * @return
	 * @throws Exception
	 */
	public FullVideoInfo getVideoInfo(final int videoId) throws EitException;

	/**
	 * Cree une nouvelle video (le fichier peut ne pas exister)
	 * 
	 * @param videoInfo
	 * @return
	 * @throws Exception
	 */
	public FullVideoInfo createVideoFile(FullVideoInfo videoInfo) throws EitException;

	/**
	 * Supprime l'entrée dans la table video
	 * 
	 * @param streamId
	 * @return
	 * @throws Exception
	 */
	public boolean deleteVideo(int videoId) throws EitException;

	// ================================================
	// STREAM
	// ================================================

	/**
	 * Transforme le model en sa représentation Json
	 * 
	 * @param eitInfo
	 * @return
	 * @throws EitException
	 */
	public String getJsonFromEitInfo(EitInfoModel eitInfo) throws EitException;

	/**
	 * Retourne un objet EitInfoModel à partir de sa représentation json
	 * 
	 * @param json
	 * @return
	 * @throws EitException
	 */
	public EitInfoModel buildEitInfoFromJson(String json) throws EitException;

	/**
	 * Met à jour les eits pour le stream donné
	 */
	public boolean updateEitInfo(int streamId, EitInfoModel eitInfo) throws EitException;

	/**
	 * Recupere toutes les entrees de la table eit
	 * 
	 * @return
	 * @throws Exception
	 */
	public List<StreamInfoForList> getListStreamInfo() throws EitException;

	/**
	 * Met à jour les informations annexe du stream (pas les eits)
	 * 
	 * @param streamInfo
	 * @return
	 */
	public boolean updateStreamInfo(StreamInfo streamInfo) throws EitException;

	/**
	 * Reinitialise le status du stream à STOPPED
	 * 
	 * @param streamId
	 * @param newStatus
	 * @return
	 * @throws EitException
	 */
	public boolean updateStreamStatus(int streamId, StreamStatus newStatus) throws EitException;

	/**
	 * Valide les modification du streamInfo
	 * 
	 * @param streamInfo
	 * @return
	 * @throws Exception
	 *             si erreur de validation
	 */
	public void validStreamInfo(StreamInfo streamInfo) throws EitException;

	/**
	 * Récupère une entree de la table des stream par son id
	 * 
	 * @param streamId
	 * @return
	 * @throws Exception
	 */
	public StreamInfo getStreamInfo(final int streamId) throws EitException;

	/**
	 * Lance la diffusion du flux avec les eit courantes
	 * 
	 * @param streamId
	 * @return
	 * @throws Exception
	 */
	public boolean startStream(int streamId) throws EitException;

	/**
	 * Stoppe la diffusion du flux
	 * 
	 * @param streamId
	 * @return
	 * @throws Exception
	 */
	public boolean stopStream(int streamId) throws EitException;

	/**
	 * Supprime l'entrée dans la table stream
	 * 
	 * @param streamId
	 * @return
	 * @throws Exception
	 */
	public boolean deleteStream(int streamId) throws EitException;

	/**
	 * Cree un nouveau stream avec les eit par defaut
	 * 
	 * @param streamInfo
	 * @param eitInfoJson
	 * @return
	 * @throws Exception
	 */
	public StreamInfo createStreamInfo(StreamInfo streamInfo, EitInfoModel eitInfo) throws EitException;

	/**
	 * Reconstruit le model a partir d'une entree de la table des stream
	 * 
	 * @param streamId
	 * @return
	 * @throws Exception
	 */
	public EitInfoModel getEitOfStream(int streamId) throws EitException;

}

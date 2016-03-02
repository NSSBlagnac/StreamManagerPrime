package com.francetelecom.orangetv.streammanager.server.model;

/**
 * Avec les infos complementaires sur les streams
 * 
 * @author ndmz2720
 *
 */
public class DbFullVideoFile extends DbVideoFile {

	private static final long serialVersionUID = 1L;

	// stream attributs

	// nombre de stream utilisant cette video
	private int countStream = 0;
	// nombre de stream actif utilisant cette video
	private int sumEnabledStream = 0;

	private String audioTracks;
	private String subtitleTracks;
	private String tablePmt;

	// ---------------------------- accessors

	public int getCountStream() {
		return countStream;
	}

	public String getAudioTracks() {
		return audioTracks;
	}

	public void setAudioTracks(String audioTracks) {
		this.audioTracks = audioTracks;
	}

	public String getSubtitleTracks() {
		return subtitleTracks;
	}

	public void setSubtitleTracks(String subtitleTracks) {
		this.subtitleTracks = subtitleTracks;
	}

	public String getTablePmt() {
		return tablePmt;
	}

	public void setTablePmt(String tablePmt) {
		this.tablePmt = tablePmt;
	}

	public void setCountStream(int countStream) {
		this.countStream = countStream;
	}

	public int getSumEnabledStream() {
		return sumEnabledStream;
	}

	public void setSumEnabledStream(int sumEnabledStream) {
		this.sumEnabledStream = sumEnabledStream;
	}

	// ------------------------------ constructor
	public DbFullVideoFile() {
		this(-1, null);
	}

	public DbFullVideoFile(int id, String name) {
		super(id, name);
	}

}

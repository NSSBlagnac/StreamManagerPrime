package com.francetelecom.orangetv.streammanager.shared.dto;

public class FullVideoInfo extends AbstractVideoInfo implements Comparable<FullVideoInfo> {
	private static final long serialVersionUID = 1L;

	// stream attributs

	// determine si la video est utilisée par au moins un stream
	private boolean usedByStream;
	// determine si au moins un stream enabled utilise cette video
	private boolean atLastOneStreamEnabled;

	// info issues de la table PMT (peut être null)
	private String audioTracks;
	private String subtitleTracks;
	private String tablePmt;

	// champ calcule en fonction de videoStatus et des stream attributs
	public FullVideoStatus getFullVideoStatus() {

		switch (this.getStatus()) {
		case NEW:
			return FullVideoStatus.NEW;
		case PENDING:
			return FullVideoStatus.PENDING;

		case ERROR:
			return FullVideoStatus.ERROR;
		case READY: {
			if (this.atLastOneStreamEnabled) {
				return FullVideoStatus.RUNNING;
			} else if (this.usedByStream) {
				return FullVideoStatus.USED;
			}
			return FullVideoStatus.UPLOADED;
		}
		}

		return FullVideoStatus.NEW;
	}

	public String getAudioTracks() {
		return audioTracks == null ? "" : audioTracks;
	}

	public void setAudioTracks(String audioTracks) {
		this.audioTracks = audioTracks;
	}

	public String getSubtitleTracks() {
		return subtitleTracks == null ? "" : subtitleTracks;
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

	public boolean isUsedByStream() {
		return usedByStream;
	}

	public void setUsedByStream(boolean usedByStream) {
		this.usedByStream = usedByStream;
	}

	public boolean isAtLastOneStreamEnabled() {
		return atLastOneStreamEnabled;
	}

	public void setAtLastOneStreamEnabled(boolean atLastOneStreamEnabled) {
		this.atLastOneStreamEnabled = atLastOneStreamEnabled;
	}

	@Override
	public int compareTo(FullVideoInfo o) {
		if (o == null) {
			return 1;
		}
		if (this.getName() == o.getName()) {
			return 0;
		}
		return this.getName().toLowerCase().compareTo(o.getName().toLowerCase());
	}

}
package com.francetelecom.orangetv.streammanager.shared.dto;

import java.util.List;

/**
 * Dto used for edition
 * 
 * @author ndmz2720
 *
 */
public class StreamInfo extends AbstractStreamInfo {

	private static final long serialVersionUID = 1L;
	private List<VideoInfo> videoFileList = null;

	public List<VideoInfo> getVideoFileList() {
		return this.videoFileList;
	}

	public void setVideoFileList(List<VideoInfo> videoFileList) {
		this.videoFileList = videoFileList;
	}

}

package com.francetelecom.orangetv.streammanager.shared.dto;

public class VideoInfo extends AbstractVideoInfo implements Comparable<VideoInfo> {
	private static final long serialVersionUID = 1L;

	// ------------------------------------ implementing Comparable

	@Override
	public int compareTo(VideoInfo o) {
		if (o == null) {
			return 1;
		}
		if (this.getName() == o.getName()) {
			return 0;
		}
		return this.getName().toLowerCase().compareTo(o.getName().toLowerCase());
	}

}

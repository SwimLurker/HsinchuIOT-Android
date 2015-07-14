package org.slstudio.hsinchuiot.upgrade;

import com.fasterxml.jackson.annotation.JsonSetter;

public class CheckUpgradeResult {

	public static final int NEEDLESS_UPGRADE = 0;
	public static final int SUGGEST_UPGRADE = 1;
	public static final int FORCE_UPGRADE = 2;

	private int upgradeSuggest;

	private String url;
	private String fileName;

	public int getUpgradeSuggest() {
		return upgradeSuggest;
	}

	@JsonSetter(value = "isNeedUpgrade")
	public void setUpgradeSuggest(int upgradeSuggest) {
		this.upgradeSuggest = upgradeSuggest;
	}

	public String getUrl() {
		return url;
	}

	public void setUrl(String url) {

		this.url = url;
		fileName = url.replace("/", "").replace(":", "") + ".apk";
	}

	public String getFileName() {
		return fileName;
	}

}

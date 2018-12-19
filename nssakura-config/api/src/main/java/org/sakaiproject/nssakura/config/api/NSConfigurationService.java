package org.sakaiproject.nssakura.config.api;


public interface NSConfigurationService {
	/**
	 * 設定値を取得します。<br/>
	 * キーとして与えられた名前に対応する設定値が取得できない場合はnullを返します。
	 * @param name 設定値を識別するキーとなる名前
	 * @return 設定値
	 */
	public String getString(String name);
	
	/**
	 * 設定値を取得します。<br/>
	 * キーとして与えられた名前に対応する設定値が取得できない場合はデフォルト値を返します。
	 * @param name 設定値を識別するキーとなる名前
	 * @param defaultValue デフォルト値
	 * @return 設定値
	 */
	public String getStringOrDefault(String name, String defaultValue);
	
	/**
	 * 設定値を取得します。<br/>
	 * キーとして与えられた名前に対応する設定値が取得できない、またはboolean値として評価できない場合はnullを返します。
	 * @param name 設定値を識別するキーとなる名前
	 * @return 設定値
	 */
	public Boolean getBoolean(String name);
	
	/**
	 * 設定値を取得します。<br/>
	 * キーとして与えられた名前に対応する設定値が取得できない、またはboolean値として評価できない場合はデフォルト値を返します。
	 * @param name 設定値を識別するキーとなる名前
	 * @param defaultValue デフォルト値
	 * @return 設定値
	 */
	public boolean getBooleanOrDefault(String name, boolean defaultValue);
}

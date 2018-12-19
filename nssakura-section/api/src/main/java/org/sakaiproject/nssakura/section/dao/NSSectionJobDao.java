package org.sakaiproject.nssakura.section.dao;

import java.util.List;

import org.sakaiproject.nssakura.section.model.NSSectionJob;

public interface NSSectionJobDao {

	/**
	 * 処理中のジョブを返す
	 * @param siteId
	 * @return
	 */
	public List<NSSectionJob> findProcessingBySiteId(final String siteId);

	/**
	 * サイトで実行されたバッチジョブ一覧を返す
	 * @param siteId
	 * @return
	 */
	public List<NSSectionJob> findBySiteId(final String siteId);
	
	/**
	 * サイトで実行されたバッチジョブ一覧を返す
	 * @param siteId
	 * @return
	 */
	public int findNumBySiteId(final String siteId);
	
	public void save(NSSectionJob job);
	public void update(NSSectionJob job);
	public void delete(NSSectionJob job);
}

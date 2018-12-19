package org.sakaiproject.nssakura.learningStatus.dao;

import java.util.List;

import org.sakaiproject.nssakura.learningStatus.model.MessageDaoModel;

public interface NSMessageDao {

	/**
	 * メッセージを返す
	 * @param messageId
	 * @return
	 */
	public MessageDaoModel findById(final Long messageId);
	
	/**
	 * サイトに登録されているユーザが登録したメッセージを返す
	 * @param siteId
	 * @param userId
	 * @return
	 */
	public List<MessageDaoModel> findMessageBySiteIdAndUserId(final String siteId, final String userId);

	/**
	 * サイトに登録されているメッセージを返す
	 * @param siteId
	 * @return
	 */
	public List<MessageDaoModel> findMessageBySiteId(final String siteId);
	public void save(MessageDaoModel message);
	public void update(MessageDaoModel message);
	public void delete(MessageDaoModel message);
}

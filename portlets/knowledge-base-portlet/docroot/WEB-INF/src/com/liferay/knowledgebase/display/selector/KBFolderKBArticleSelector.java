/**
 * Copyright (c) 2000-present Liferay, Inc. All rights reserved.
 *
 * This library is free software; you can redistribute it and/or modify it under
 * the terms of the GNU Lesser General Public License as published by the Free
 * Software Foundation; either version 2.1 of the License, or (at your option)
 * any later version.
 *
 * This library is distributed in the hope that it will be useful, but WITHOUT
 * ANY WARRANTY; without even the implied warranty of MERCHANTABILITY or FITNESS
 * FOR A PARTICULAR PURPOSE. See the GNU Lesser General Public License for more
 * details.
 */

package com.liferay.knowledgebase.display.selector;

import com.liferay.knowledgebase.model.KBArticle;
import com.liferay.knowledgebase.model.KBFolder;
import com.liferay.knowledgebase.service.KBArticleLocalServiceUtil;
import com.liferay.knowledgebase.service.KBFolderLocalServiceUtil;
import com.liferay.portal.kernel.exception.PortalException;
import com.liferay.portal.kernel.exception.SystemException;
import com.liferay.portal.kernel.workflow.WorkflowConstants;

/**
 * @author Adolfo Pérez
 */
public class KBFolderKBArticleSelector implements KBArticleSelector {

	@Override
	public KBArticle findByResourcePrimKey(
			long groupId, long ancestorResourcePrimKey, long resourcePrimKey)
		throws PortalException, SystemException {

		KBFolder ancestorKBFolder = KBFolderLocalServiceUtil.fetchKBFolder(
			ancestorResourcePrimKey);

		if (ancestorKBFolder == null) {
			return null;
		}

		KBArticle kbArticle = KBArticleLocalServiceUtil.fetchLatestKBArticle(
			resourcePrimKey, WorkflowConstants.STATUS_APPROVED);

		if ((kbArticle == null) || !isDescendant(kbArticle, ancestorKBFolder)) {
			return findClosestMatchingKBArticle(groupId, ancestorKBFolder);
		}

		return kbArticle;
	}

	@Override
	public KBArticle findByUrlTitle(
			long groupId, long ancestorResourcePrimKey, String kbFolderUrlTitle,
			String urlTitle)
		throws PortalException, SystemException {

		KBFolder ancestorKBFolder = KBFolderLocalServiceUtil.fetchKBFolder(
			ancestorResourcePrimKey);

		if (ancestorKBFolder == null) {
			return null;
		}

		KBArticle kbArticle =
			KBArticleLocalServiceUtil.fetchKBArticleByUrlTitle(
				groupId, kbFolderUrlTitle, urlTitle);

		if ((kbArticle == null) || !isDescendant(kbArticle, ancestorKBFolder)) {
			return findClosestMatchingKBArticle(
				groupId, ancestorKBFolder, kbFolderUrlTitle, urlTitle);
		}

		return kbArticle;
	}

	protected KBArticle findClosestMatchingKBArticle(
			long groupId, KBFolder ancestorKBFolder)
		throws PortalException, SystemException {

		KBFolder kbFolder = KBFolderLocalServiceUtil.fetchFirstChildKBFolder(
			groupId, ancestorKBFolder.getKbFolderId());

		if (kbFolder == null) {
			kbFolder = ancestorKBFolder;
		}

		return KBArticleLocalServiceUtil.fetchFirstChildKBArticle(
			groupId, kbFolder.getKbFolderId());
	}

	protected KBArticle findClosestMatchingKBArticle(
			long groupId, KBFolder ancestorKBFolder, String kbFolderUrlTitle,
			String urlTitle)
		throws PortalException, SystemException {

		KBFolder kbFolder = getCandidateKBFolder(
			groupId, ancestorKBFolder, kbFolderUrlTitle);

		KBArticle kbArticle =
			KBArticleLocalServiceUtil.fetchKBArticleByUrlTitle(
				groupId, kbFolder.getKbFolderId(), urlTitle);

		if (kbArticle != null) {
			return kbArticle;
		}

		return KBArticleLocalServiceUtil.fetchFirstChildKBArticle(
			groupId, kbFolder.getKbFolderId());
	}

	protected KBFolder getCandidateKBFolder(
			long groupId, KBFolder ancestorKBFolder, String kbFolderUrlTitle)
		throws PortalException, SystemException {

		KBFolder kbFolder =
			KBFolderLocalServiceUtil.fetchKBFolderByUrlTitle(
				groupId, ancestorKBFolder.getKbFolderId(), kbFolderUrlTitle);

		if (kbFolder != null) {
			return kbFolder;
		}

		kbFolder = KBFolderLocalServiceUtil.fetchFirstChildKBFolder(
			groupId, ancestorKBFolder.getKbFolderId());

		if (kbFolder != null) {
			return kbFolder;
		}

		return ancestorKBFolder;
	}

	protected boolean isDescendant(
			KBArticle kbArticle, KBFolder ancestorKBFolder)
		throws PortalException, SystemException {

		KBFolder parentKBFolder = KBFolderLocalServiceUtil.fetchKBFolder(
			kbArticle.getKbFolderId());

		while ((parentKBFolder != null) &&
			   !parentKBFolder.equals(ancestorKBFolder)) {

			parentKBFolder = KBFolderLocalServiceUtil.fetchKBFolder(
				parentKBFolder.getParentKBFolderId());
		}

		return parentKBFolder != null;
	}

}
/**
 * Copyright (c) 2000-2012 Liferay, Inc. All rights reserved.
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

package com.liferay.oauthlogin.admin.portlet;

import com.liferay.oauthlogin.model.OAuthConnection;
import com.liferay.oauthlogin.service.OAuthConnectionLocalServiceUtil;
import com.liferay.oauthlogin.util.WebKeys;
import com.liferay.portal.kernel.exception.PortalException;
import com.liferay.portal.kernel.exception.SystemException;
import com.liferay.portal.kernel.upload.UploadPortletRequest;
import com.liferay.portal.kernel.util.ParamUtil;
import com.liferay.portal.kernel.util.UnicodeProperties;
import com.liferay.portal.model.ResourceConstants;
import com.liferay.portal.model.Role;
import com.liferay.portal.model.RoleConstants;
import com.liferay.portal.model.User;
import com.liferay.portal.security.permission.ActionKeys;
import com.liferay.portal.service.ResourcePermissionLocalServiceUtil;
import com.liferay.portal.service.RoleLocalServiceUtil;
import com.liferay.portal.service.ServiceContext;
import com.liferay.portal.service.ServiceContextFactory;
import com.liferay.portal.util.PortalUtil;
import com.liferay.portlet.expando.model.ExpandoBridge;
import com.liferay.portlet.expando.model.ExpandoColumn;
import com.liferay.portlet.expando.model.ExpandoColumnConstants;
import com.liferay.portlet.expando.model.ExpandoTable;
import com.liferay.portlet.expando.model.ExpandoTableConstants;
import com.liferay.portlet.expando.service.ExpandoColumnLocalServiceUtil;
import com.liferay.portlet.expando.service.ExpandoTableLocalServiceUtil;
import com.liferay.portlet.expando.util.ExpandoBridgeFactoryUtil;
import com.liferay.util.bridges.mvc.MVCPortlet;

import java.io.File;
import java.io.IOException;

import javax.portlet.ActionRequest;
import javax.portlet.ActionResponse;
import javax.portlet.PortletException;
import javax.portlet.RenderRequest;
import javax.portlet.RenderResponse;

/**
 * @author Andy Yang
 */
public class AdminPortlet extends MVCPortlet {

	public void deleteOAuthConnection(
			ActionRequest actionRequest, ActionResponse actionResponse)
		throws Exception {

		long oAuthConnectionId = ParamUtil.getLong(
			actionRequest, "oAuthConnectionId");

		OAuthConnectionLocalServiceUtil.deleteOAuthConnection(
			oAuthConnectionId);

		sendRedirect(actionRequest, actionResponse);
	}

	public void render(
			RenderRequest renderRequest, RenderResponse renderResponse)
		throws IOException, PortletException {

		try {
			OAuthConnection oAuthConnection = null;

			long oAuthConnectionId = ParamUtil.getLong(
				renderRequest, "oAuthConnectionId");

			if (oAuthConnectionId > 0) {
				oAuthConnection =
					OAuthConnectionLocalServiceUtil.getOAuthConnection(
						oAuthConnectionId);
			}

			renderRequest.setAttribute(
				WebKeys.OAUTH_CONNECTION, oAuthConnection);

			super.render(renderRequest, renderResponse);
		}
		catch (PortalException e) {
		}
		catch (SystemException e) {
		}
	}

	public void updateOAuthConnection(
			ActionRequest actionRequest, ActionResponse actionResponse)
		throws Exception {

		UploadPortletRequest uploadPortletRequest =
			PortalUtil.getUploadPortletRequest(actionRequest);

		long oAuthConnectionId = ParamUtil.getLong(
			uploadPortletRequest, "oAuthConnectionId");

		boolean enabled = ParamUtil.getBoolean(uploadPortletRequest, "enabled");
		String name = ParamUtil.getString(uploadPortletRequest, "name");

		String description = ParamUtil.getString(
			uploadPortletRequest, "description");
		int oAuthVersion = ParamUtil.getInteger(
			uploadPortletRequest, "oAuthVersion");
		String key = ParamUtil.getString(uploadPortletRequest, "key");
		String secret = ParamUtil.getString(uploadPortletRequest, "secret");
		String scope = ParamUtil.getString(uploadPortletRequest, "scope");
		String graphURL = ParamUtil.getString(uploadPortletRequest, "graphURL");
		String authorizeURL = ParamUtil.getString(
			uploadPortletRequest, "authorizeURL");
		String accessTokenURL = ParamUtil.getString(
			uploadPortletRequest, "accessTokenURL");
		int accessTokenVerb = ParamUtil.getInteger(
			uploadPortletRequest, "accessTokenVerb");
		int accessTokenExtratorType = ParamUtil.getInteger(
			uploadPortletRequest, "accessTokenExtratorType");
		String accessTokenExtratorScript = ParamUtil.getString(
			uploadPortletRequest, "accessTokenExtratorScript");
		String requestTokenURL = ParamUtil.getString(
			uploadPortletRequest, "requestTokenURL");
		int requestTokenVerb = ParamUtil.getInteger(
			uploadPortletRequest, "requestTokenVerb");
		String redirectURL = ParamUtil.getString(
			uploadPortletRequest, "redirectURL");
		String socialAccountIdURL = ParamUtil.getString(
			uploadPortletRequest, "socialAccountIdURL");
		int socialAccountIdURLVerb = ParamUtil.getInteger(
			uploadPortletRequest, "socialAccountIdURLVerb");
		String socialAccountIdField = ParamUtil.getString(
			uploadPortletRequest, "socialAccountIdField");
		int socialAccountIdType = ParamUtil.getInteger(
			uploadPortletRequest, "socialAccountIdType");
		String socialAccountIdScript = ParamUtil.getString(
			uploadPortletRequest, "socialAccountIdScript");

		File icon = uploadPortletRequest.getFile("icon");

		ServiceContext serviceContext = ServiceContextFactory.getInstance(
			actionRequest);

		OAuthConnection oAuthConnection = null;

		ExpandoTable expandoTable = null;

		ExpandoColumn expandoColumn = null;

		if (oAuthConnectionId <= 0) {
			oAuthConnection =
				OAuthConnectionLocalServiceUtil.addOAuthConnection(
					enabled, name, description, oAuthVersion, key, secret,
					scope, graphURL, authorizeURL, accessTokenURL,
					accessTokenVerb, accessTokenExtratorType,
					accessTokenExtratorScript, requestTokenURL,
					requestTokenVerb, redirectURL, socialAccountIdURL,
					socialAccountIdURLVerb,socialAccountIdField,
					socialAccountIdType, socialAccountIdScript, icon,
					serviceContext);

			try {
				expandoTable = ExpandoTableLocalServiceUtil.addTable(
					PortalUtil.getCompanyId(uploadPortletRequest),
					User.class.getName(),
					ExpandoTableConstants.DEFAULT_TABLE_NAME);
			}
			catch (Exception e) {
				expandoTable = ExpandoTableLocalServiceUtil.getTable(
					PortalUtil.getCompanyId(uploadPortletRequest),
					User.class.getName(),
					ExpandoTableConstants.DEFAULT_TABLE_NAME);
			}

			try{
				expandoColumn = ExpandoColumnLocalServiceUtil.addColumn(
					expandoTable.getTableId(),
					oAuthConnection.getOAuthConnectionId() +
					"_social_account_id", ExpandoColumnConstants.STRING);
			}
			catch (Exception e) {
			}

			ExpandoBridge expandoBridge =
				ExpandoBridgeFactoryUtil.getExpandoBridge(
					PortalUtil.getCompanyId(uploadPortletRequest),
					User.class.getName());

			UnicodeProperties properties = expandoBridge.getAttributeProperties(
				expandoColumn.getName());

			properties.setProperty("hidden", "1");

			expandoBridge.setAttributeProperties(
				expandoColumn.getName(), properties);

			String[] actionIds = {ActionKeys.UPDATE};

			Role guest = RoleLocalServiceUtil.getRole(
				PortalUtil.getCompanyId(
					uploadPortletRequest), RoleConstants.GUEST);

			ResourcePermissionLocalServiceUtil.setResourcePermissions(	
				PortalUtil.getCompanyId(actionRequest),
				ExpandoColumn.class.getName(),
				ResourceConstants.SCOPE_INDIVIDUAL,	
				String.valueOf(expandoColumn.getColumnId()), guest.getRoleId(),
				actionIds);
		}
		else {
			oAuthConnection =
				OAuthConnectionLocalServiceUtil.updateOAuthConnection(
					oAuthConnectionId, enabled, name, description,
					oAuthVersion, key, secret, scope, graphURL, authorizeURL,
					accessTokenURL, accessTokenVerb, accessTokenExtratorType,
					accessTokenExtratorScript, requestTokenURL,
					requestTokenVerb, redirectURL, socialAccountIdURL,
					socialAccountIdURLVerb, socialAccountIdField,
					socialAccountIdType, socialAccountIdScript, icon,
					serviceContext);
		}
	}

}
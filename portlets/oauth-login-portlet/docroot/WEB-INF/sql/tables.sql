create table OAuthLogin_OAuthConnection (
	oAuthConnectionId LONG not null primary key,
	companyId LONG,
	userId LONG,
	createDate DATE null,
	modifiedDate DATE null,
	enabled BOOLEAN,
	name VARCHAR(75) null,
	description VARCHAR(75) null,
	iconId LONG,
	oAuthVersion INTEGER,
	key_ VARCHAR(75) null,
	secret VARCHAR(75) null,
	scope VARCHAR(75) null,
	authorizeURL VARCHAR(75) null,
	accessTokenURL VARCHAR(75) null,
	accessTokenVerb INTEGER,
	accessTokenExtractorType INTEGER,
	accessTokenExtractorScript VARCHAR(75) null,
	requestTokenURL VARCHAR(75) null,
	requestTokenVerb INTEGER,
	redirectURL VARCHAR(75) null,
	socialAccountIdURL VARCHAR(75) null,
	socialAccountIdURLVerb INTEGER,
	socialAccountIdField VARCHAR(75) null,
	socialAccountIdType INTEGER,
	socialAccountIdScript VARCHAR(75) null
);
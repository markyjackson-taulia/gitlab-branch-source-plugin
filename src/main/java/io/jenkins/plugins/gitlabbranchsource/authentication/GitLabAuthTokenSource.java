package io.jenkins.plugins.gitlabbranchsource.authentication;

import edu.umd.cs.findbugs.annotations.NonNull;
import io.jenkins.plugins.gitlabbranchsource.client.api.GitLabAuthToken;
import io.jenkins.plugins.gitlabbranchsource.credentials.PersonalAccessToken;
import jenkins.authentication.tokens.api.AuthenticationTokenException;
import jenkins.authentication.tokens.api.AuthenticationTokenSource;


public class GitLabAuthTokenSource extends AuthenticationTokenSource<GitLabAuthToken, PersonalAccessToken> {
    /**
     * Constructor.
     */
    public GitLabAuthTokenSource() {
        super(GitLabAuthToken.class, PersonalAccessToken.class);
    }

    /**
     * {@inheritDoc}
     */
    @NonNull
    @Override
    public GitLabAuthToken convert(@NonNull PersonalAccessToken credential) throws AuthenticationTokenException {
        return new GitLabAuthToken(credential.getToken().getPlainText());
    }
}

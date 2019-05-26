package io.jenkins.plugins.gitlabbranchsource.servers;

import hudson.Extension;
import hudson.ExtensionList;
import hudson.Util;
import hudson.util.ListBoxModel;
import jenkins.model.GlobalConfiguration;
import jenkins.model.Jenkins;
import net.sf.json.JSONObject;
import org.kohsuke.stapler.StaplerRequest;

import javax.annotation.CheckForNull;
import javax.annotation.Nonnull;
import java.net.URI;
import java.net.URISyntaxException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.ListIterator;
import java.util.Locale;
import java.util.Set;

/**
 * Represents the global configuration of GitLab servers.
 */

@Extension
public class GitLabServers extends GlobalConfiguration {

    /**
     * The list of {@link GitLabServer}, this is subject to the constraint that there can only ever be
     * one entry for each {@link GitLabServer#getServerUrl()}.
     */
    private List<GitLabServer> servers;

    /**
     * Constructor.
     */
    public GitLabServers() {
        load();
    }

    /**
     * Gets the {@link GitLabServers} singleton.
     *
     * @return the {@link GitLabServers} singleton.
     */
    public static GitLabServers get() {
        return ExtensionList.lookup(GlobalConfiguration.class).get(GitLabServers.class);
    }

    /**
     * Fix a serverUrl.
     *
     * @param serverUrl the server URL.
     * @return the normalized server URL.
     */
    @Nonnull
    public static String normalizeServerUrl(@CheckForNull String serverUrl) {
        serverUrl = serverUrl == null ? "" : serverUrl;
        try {
            URI uri = new URI(serverUrl).normalize();
            String scheme = uri.getScheme();
            if ("http".equals(scheme) || "https".equals(scheme)) {
                // we only expect http / https, but also these are the only ones where we know the authority
                // is server based, i.e. [userinfo@]server[:port]
                // DNS names must be US-ASCII and are case insensitive, so we force all to lowercase
                String host = uri.getHost() == null ? null : uri.getHost().toLowerCase(Locale.ENGLISH);
                int port = uri.getPort();
                if ("http".equals(scheme) && port == 80) {
                    port = -1;
                } else if ("https".equals(scheme) && port == 443) {
                    port = -1;
                }
                serverUrl = new URI(
                        scheme,
                        uri.getUserInfo(),
                        host,
                        port,
                        uri.getPath(),
                        uri.getQuery(),
                        uri.getFragment()
                ).toASCIIString();
            }
         } catch (URISyntaxException e) {
            // ignore, this was a best effort tidy-up
        }
        return serverUrl.replaceAll("/$", "");
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public boolean configure(StaplerRequest req, JSONObject json) throws FormException {
        req.bindJSON(this, json);
        return true;
    }

}

package org.pf9.pangu.boilerplate;

import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.stereotype.Component;

import java.util.List;

/**
 * Properties specific to JHipster.
 * <p>
 * Properties are configured in the application.yml file.
 */

@Component
@ConfigurationProperties(prefix = "application", ignoreUnknownFields = false)
public class ApplicationProperties {

    private final SiteGlobal siteGlobal = new SiteGlobal();

    private final Analytics analytics = new Analytics();

    private final File file = new File();

    public SiteGlobal getSiteGlobal() {
        return siteGlobal;
    }

    public Analytics getAnalytics() {
        return analytics;
    }

    public File getFile() {
        return file;
    }

    private String uploadPath;

    private String ipAddress;

    private String defaultApp = "admin";

    private String homePage = "/admin";

    public String getIpAddress() {
        return ipAddress;
    }

    public void setIpAddress(String ipAddress) {
        this.ipAddress = ipAddress;
    }

    public String getUploadPath() {
        return uploadPath;
    }

    public void setUploadPath(String uploadPath) {
        this.uploadPath = uploadPath;
    }

    public String getDefaultApp() {
        return defaultApp;
    }

    public void setDefaultApp(String defaultApp) {
        this.defaultApp = defaultApp;
    }

    public String getHomePage() {
        return homePage;
    }

    public void setHomePage(String homePage) {
        this.homePage = homePage;
    }


    public static class File {
        private String uploadPath;

        private List<String> allowedExt;

        public String getUploadPath() {
            return uploadPath;
        }

        public void setUploadPath(String uploadPath) {
            this.uploadPath = uploadPath;
        }

        public List<String> getAllowedExt() {
            return allowedExt;
        }

        public void setAllowedExt(List<String> allowedExt) {
            this.allowedExt = allowedExt;
        }
    }

    public static class SiteGlobal {


        private String uploadPath;

        private String ipAddress;

        private String defaultApp = "admin";

        private String defaultPage = "/admin#/";

        private String homePage = "/admin";

        private String loginPage = "/admin#/login";

        public String getIpAddress() {
            return ipAddress;
        }

        public void setIpAddress(String ipAddress) {
            this.ipAddress = ipAddress;
        }

        public String getUploadPath() {
            return uploadPath;
        }

        public void setUploadPath(String uploadPath) {
            this.uploadPath = uploadPath;
        }

        public String getDefaultApp() {
            return defaultApp;
        }

        public void setDefaultApp(String defaultApp) {
            this.defaultApp = defaultApp;
        }

        public String getHomePage() {
            return homePage;
        }

        public void setHomePage(String homePage) {
            this.homePage = homePage;
        }

        public String getDefaultPage() {
            return defaultPage;
        }

        public void setDefaultPage(String defaultPage) {
            this.defaultPage = defaultPage;
        }

        public String getLoginPage() {
            return loginPage;
        }

        public void setLoginPage(String loginPage) {
            this.loginPage = loginPage;
        }
    }

    public static class Analytics {

        private String reportServer;

        private String reportRoot;

        private List<String> reportExts;

        public String getReportServer() {
            return reportServer;
        }

        public void setReportServer(String reportServer) {
            this.reportServer = reportServer;
        }

        public String getReportRoot() {
            return reportRoot;
        }

        public void setReportRoot(String reportRoot) {
            this.reportRoot = reportRoot;
        }

        public List<String> getReportExts() {
            return reportExts;
        }

        public void setReportExts(List<String> reportExts) {
            this.reportExts = reportExts;
        }
    }

}

package ru.elytrium.host.api.model.module.params;

public class ModuleMount {
    public String displayName;
    public String containerDir;
    public String bucketDir;
    public boolean isFile;
    public boolean enabled;
    public boolean permanent;
    public String versionRange;

    public boolean checkVersion(String version) {
        String[] checkVersion = version.split("\\.");
        int checkVersion0 = Integer.parseInt(checkVersion[0]);
        int checkVersion1 = Integer.parseInt(checkVersion[1]);

        if (versionRange.endsWith("+")) {
            String[] ver1 = versionRange.substring(0, versionRange.length() - 1).split("\\.");
            return Integer.parseInt(ver1[0]) >= checkVersion0
                    && Integer.parseInt(ver1[1]) >= checkVersion1;
        } else if (versionRange.endsWith("-")) {
            String[] ver2 = versionRange.substring(0, versionRange.length() - 1).split("\\.");
            return Integer.parseInt(ver2[0]) <= checkVersion0
                    && Integer.parseInt(ver2[1]) <= checkVersion1;
        } else {
            String[] versions = versionRange.split("-");
            String[] ver1 = versions[0].split("\\.");
            String[] ver2 = versions[1].split("\\.");
            return Integer.parseInt(ver1[0]) >= checkVersion0
                    && Integer.parseInt(ver1[1]) >= checkVersion1
                    && Integer.parseInt(ver2[0]) <= checkVersion0
                    && Integer.parseInt(ver2[1]) <= checkVersion1;
        }
    }
}

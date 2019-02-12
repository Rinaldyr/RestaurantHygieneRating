package com.example.rinaldy.restauranthygienechecker;

/**
 * Created by Rinaldy on 06/03/2018.
 */

public class Authorities {

    private Authority[] authorities;

    public Authority[] getAuthorities() {
        return authorities;
    }

    public static class Authority {
        private Integer LocalAuthorityId;
        private String LocalAuthorityIdCode;
        private String Name;
        private String RegionName;

        public Authority(Integer localAuthorityId, String localAuthorityIdCode, String name, String regionName) {
            LocalAuthorityId = localAuthorityId;
            LocalAuthorityIdCode = localAuthorityIdCode;
            Name = name;
            RegionName = regionName;
        }

        public Integer getLocalAuthorityId() {
            return LocalAuthorityId;
        }

        public String getLocalAuthorityIdCode() {
            return LocalAuthorityIdCode;
        }

        public String getName() {
            return Name;
        }

        public String getRegionName() {
            return RegionName;
        }

        @Override
        public String toString() {
            return Name;
        }
    }
}

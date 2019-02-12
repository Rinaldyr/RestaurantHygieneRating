package com.example.rinaldy.restauranthygienechecker;

/**
 * Created by Rinaldy on 06/03/2018.
 */

public class Regions {

    private Region[] regions;

    public Region[] getRegions() {
        return regions;
    }

    public static class Region {
        private Integer id;
        private String name;
        private String code;

        public Region(Integer id, String name, String code) {
            this.id = id;
            this.name = name;
            this.code = code;
        }

        public Integer getId() {
            return id;
        }

        public String getName() {
            return name;
        }

        public String getCode() {
            return code;
        }

        @Override
        public String toString() {
            return name;
        }
    }
}

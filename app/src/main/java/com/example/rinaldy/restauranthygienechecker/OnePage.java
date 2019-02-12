package com.example.rinaldy.restauranthygienechecker;

import java.util.List;

/**
 * Created by Rinaldy on 04/03/2018.
 */

public class OnePage {
    private List<Establishment> establishments;
    private Meta meta;

    public OnePage(List<Establishment> establishments) {
        this.establishments = establishments;
    }

    public List<Establishment> getEstablishments() {
        return establishments;
    }

    public Meta getMeta() {
        return meta;
    }

    public static class Meta {
        private Integer totalCount;
        private Integer totalPages;
        private Integer pageNumber;

        public Integer getTotalCount() {
            return totalCount;
        }

        public Integer getTotalPages() {
            return totalPages;
        }

        public Integer getPageNumber() {
            return pageNumber;
        }

        @Override
        public String toString() {
            return "totalCount: " + totalCount + " totalPages: " + totalPages + " pageNumber:" + pageNumber;
        }
    }
}

package com.example.rinaldy.restauranthygienechecker;

/**
 * Created by Rinaldy on 05/03/2018.
 */

public class BusinessTypes {

    private BusinessType[] businessTypes;

    public BusinessType[] getBusinessTypes() {
        return businessTypes;
    }

    public class BusinessType {
        private Integer BusinessTypeId;
        private String BusinessTypeName;

        public Integer getBusinessTypeId() {
            return BusinessTypeId;
        }
        public String getBusinessTypeName() {
            return BusinessTypeName;
        }

        @Override
        public String toString() {
            return BusinessTypeName;
        }
    }
}

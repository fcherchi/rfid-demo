package com.fcherchi.demo.events.impl;


public enum SgtinPartitionValue {
    ZERO(0),
    ONE(1),
    TWO(2),
    THREE(3),
    FOUR(4),
    FIVE(5),
    SIX(6);

    private int partitionValue;

    SgtinPartitionValue(int partitionValue) {
        this.partitionValue = partitionValue;
    }

    public int getPartitionValue() {
        return partitionValue;
    }

    public int getCompanyPrefixBits() {
        int companyPrefix = 40;

        switch (partitionValue) {
            case 0:
                companyPrefix = 40;
                break;
            case 1:
                companyPrefix = 37;
                break;
            case 2:
                companyPrefix = 34;
                break;
            case 3:
                companyPrefix = 30;
                break;
            case 4:
                companyPrefix = 27;
                break;
            case 5:
                companyPrefix = 24;
                break;
            case 6:
                companyPrefix = 20;
                break;
        }

        return companyPrefix;
    }

    public int getItemReferenceBits() {
        int itemReferenceLength = 1;

        switch (partitionValue) {
            case 0:
                itemReferenceLength = 4;
                break;
            case 1:
                itemReferenceLength = 7;
                break;
            case 2:
                itemReferenceLength = 10;
                break;
            case 3:
                itemReferenceLength = 14;
                break;
            case 4:
                itemReferenceLength = 17;
                break;
            case 5:
                itemReferenceLength = 20;
                break;
            case 6:
                itemReferenceLength = 24;
                break;
        }

        return itemReferenceLength;
    }

    public static SgtinPartitionValue getSGTINPartitionValue(int partitionValue) {
        SgtinPartitionValue lookingFor = null;

        switch (partitionValue) {
            case 0:
                lookingFor = ZERO;
                break;
            case 1:
                lookingFor = ONE;
                break;
            case 2:
                lookingFor = TWO;
                break;
            case 3:
                lookingFor = THREE;
                break;
            case 4:
                lookingFor = FOUR;
                break;
            case 5:
                lookingFor = FIVE;
                break;
            case 6:
                lookingFor = SIX;
                break;
        }

        return lookingFor;
    }
}



